module Bae
  class Classifier

    attr_accessor :frequency_table, :label_index, :label_index_sequence,
      :label_instance_count, :total_terms

    def initialize
      @frequency_table = ::Hash.new
      @label_instance_count = ::Hash.new { |hash, label| hash[label] = 0 }
      @label_index = ::Hash.new { |hash, label| hash[label] = 0 }
      @label_index_sequence = -1 # start at -1 so 0 is first value
      @total_terms = 0.0
    end

    def finish_training!
      @frequency_table_size = @frequency_table.keys.size

      calculate_likelihoods!
      calculate_priors!
    end

    def train(label, training_data)
      if training_data.is_a?(::String)
        train_from_string(label, training_data)
      elsif training_data.is_a?(::Hash)
        train_from_hash(label, training_data)
      else
        fail 'Training data must either be a string or hash'
      end
    end

    def train_from_string(label, document)
      words = document.split

      words.each do |word|
        update_label_index(label)
        update_frequency_table(label, word, 1)
      end
      @label_instance_count[label] += 1
      @total_terms += 1
    end

    def train_from_hash(label, frequency_hash)
      frequency_hash.each do |word, frequency|
        update_label_index(label)
        update_frequency_table(label, word, frequency)
      end
      @label_instance_count[label] += 1
      @total_terms += 1
    end

    def classify(data)
      if data.is_a?(::String)
        classify_from_string(data)
      elsif data.is_a?(::Hash)
        classify_from_hash(data)
      else
        fail 'Training data must either be a string or hash'
      end
    end

    def classify_from_hash(frequency_hash)
      document = frequency_hash.map{ |word, frequency| (word + ' ') * frequency }.join

      classify_from_string(document)
    end

    def classify_from_string(document)
      words = document.split.uniq
      likelihoods = @likelihoods.dup
      posterior = {}

      vocab_size = @frequency_table_size

      label_index.each do |label, index|
        words.map do |word|
          row = frequency_table[word]

          unless row.nil?
            laplace_word_likelihood = (row[index] + 1.0).to_f / (label_instance_count[label] + vocab_size).to_f
            likelihoods[label] *= laplace_word_likelihood / (1.0 - laplace_word_likelihood)
          end
        end

        posterior[label] = @priors[label] * likelihoods[label]
      end

      normalize(posterior)
    end

    def save_state(path)
      ::File.open(::File.expand_path(path), 'w') do |handle|
        handle.write(to_json)
      end
    end

    def load_from_json(json)
      state = ::JSON.parse(json)

      fail 'Missing frequency_table' unless state['frequency_table']
      fail 'Missing label_instance_count' unless state['label_instance_count']
      fail 'Missing label_index' unless state['label_index']
      fail 'Missing label_index_sequence' unless state['label_index_sequence']
      fail 'Missing total_terms' unless state['total_terms']

      @frequency_table = state['frequency_table']
      @label_instance_count = state['label_instance_count']
      @label_index = state['label_index']
      @label_index_sequence = state['label_index_sequence']
      @total_terms = state['total_terms']

      finish_training!
    end

    def load_state(path)
      state_json = ::File.read(::File.expand_path(path))
      load_from_json(state_json)
    end

    def to_json
      state = {}
      state['frequency_table'] = frequency_table
      state['label_instance_count'] = label_instance_count
      state['label_index'] = label_index
      state['label_index_sequence'] = label_index_sequence
      state['total_terms'] = total_terms
      state.to_json
    end

  private

    def calculate_likelihoods!
      @likelihoods = label_index.inject({}) do |accumulator, (label, index)|
        initial_likelihood = 1.0
        vocab_size = @frequency_table_size

        frequency_table.each do |feature, row|
          laplace_word_likelihood = (row[index] + 1.0).to_f / (label_instance_count[label] + vocab_size).to_f
          initial_likelihood *= (1.0 - laplace_word_likelihood)
        end

        accumulator[label] = initial_likelihood
        accumulator
      end
    end

    def calculate_priors!
      @priors = label_instance_count.inject({}) do |hash, (label, count)|
        hash[label] = count / total_terms
        hash
      end
    end

    def get_next_sequence_value
      @label_index_sequence += 1
    end

    def normalize(posterior)
      sum = posterior.inject(0.0) { |accumulator, (key, value)| accumulator + value }

      posterior.inject({}) do |accumulator, (key, value)|
        accumulator[key] = value / sum
        accumulator
      end
    end

    def update_label_index(label)
      unless label_index.keys.include?(label)
        index = get_next_sequence_value
        label_index[label] = index

        frequency_table.each do |feature, value|
          value[index] = 0
        end
      end
    end

    def update_frequency_table(label, word, frequency)
      row = frequency_table[word]
      index = label_index[label]

      if row
        row[index] += frequency
      else
        frequency_table[word] = label_index.keys.map { |label| 0 }
        frequency_table[word][index] += frequency
      end
    end
  end
end
