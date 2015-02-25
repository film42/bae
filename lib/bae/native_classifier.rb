require 'java'
require ::File.join(::File.dirname(__FILE__), "..", "..", "target" , "bae.jar")

module Bae
  class NativeClassifier

    attr_reader :internal_classifier

    def initialize
      @internal_classifier = ::Java::Bae::NaiveBayesClassifier.new
    end

    def train(label, feature)
      internal_classifier.train(label, ::Java::Bae::Document.new(feature))
    end

    def classify(feature)
      internal_classifier.classify(::Java::Bae::Document.new(feature))
    end

    def finish_training!
      internal_classifier.calculateInitialLikelihoods()
    end

  end
end
