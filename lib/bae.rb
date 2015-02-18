require "bae/version"

require ::File.join(::File.dirname(__FILE__), "..", "target" , "bae.jar")

java_import "bae.Document"
java_import "bae.FrequencyTable"
java_import "bae.NaiveBayesClassifier"

require "bae/classifier"

module Bae
end
