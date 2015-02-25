require 'spec_helper'

require 'bae/native_classifier'

describe ::Bae::Classifier do

  subject { described_class.new }

  let(:state_json) {
    '{"frequency_table":{"aaa":[0,0],"bbb":[1,0],"ccc":[0,2],"ddd":[0,3]},"label_instance_count":{"positive":1,"negative":1},"label_index":{"positive":0,"negative":1},"label_index_sequence":1,"total_terms":2.0}'
  }
  let(:state) { ::JSON.parse(state_json) }

  it "can classify a hash document" do
    subject.train("positive", {"aaa" => 0, "bbb" => 1})
    subject.train("negative", {"ccc" => 2, "ddd" => 3})

    subject.finish_training!

    results = subject.classify({"aaa" => 1, "bbb" => 1})

    expect(results["positive"]).to be_within(0.001).of(0.94117)
    expect(results["negative"]).to be_within(0.001).of(0.05882)
  end

  it "can classify from a string based document" do
    subject.train("positive", "aaa aaa bbb");
    subject.train("negative", "ccc ccc ddd ddd");
    subject.train("neutral", "eee eee eee fff fff fff");

    subject.finish_training!

    results = subject.classify("aaa bbb")

    expect(results["positive"]).to be_within(0.001).of(0.89626)
    expect(results["negative"]).to be_within(0.001).of(0.06639)
    expect(results["neutral"]).to be_within(0.001).of(0.03734)
  end

  it "fails when you attempt to train or test anything other than a hash or string" do
    subject.train("positive", "aaa aaa bbb");
    expect{ subject.train("a", 1337) }.to raise_error 'Training data must either be a string or hash'

    subject.finish_training!

    subject.classify("aaa bbb")
    expect{ subject.classify(1337) }.to raise_error 'Training data must either be a string or hash'
  end

  it "can save the classifier state" do
    subject.train("positive", {"aaa" => 0, "bbb" => 1})
    subject.train("negative", {"ccc" => 2, "ddd" => 3})

    subject.finish_training!

    temp_file = ::Tempfile.new('some_state')
    subject.save_state(temp_file.path)

    temp_file.rewind
    expect(temp_file.read).to eq(state_json)

    temp_file.close
    temp_file.unlink
  end

  it "can correctly load a classifier state and correctly classify" do
    temp_file = ::Tempfile.new('some_state')
    temp_file.write(state_json)
    temp_file.rewind

    subject.load_state(temp_file.path)

    results = subject.classify({"aaa" => 1, "bbb" => 1})

    expect(results["positive"]).to be_within(0.001).of(0.94117)
    expect(results["negative"]).to be_within(0.001).of(0.05882)

    temp_file.close
    temp_file.unlink
  end

end
