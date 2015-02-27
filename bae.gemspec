# coding: utf-8
lib = File.expand_path('../lib', __FILE__)
$LOAD_PATH.unshift(lib) unless $LOAD_PATH.include?(lib)
require 'bae/version'

Gem::Specification.new do |spec|
  spec.name          = "bae"
  spec.version       = Bae::VERSION
  spec.authors       = ["Garrett Thornburg"]
  spec.email         = ["film42@gmail.com"]
  spec.summary       = "Multinomial naive bayes classifier with a kick of java"
  spec.description   = "Multinomial naive bayes classifier with a kick of java"
  spec.homepage      = "https://github.com/film42/bae"
  spec.license       = "GPL version 3, or LGPL version 3 (Dual License)"

  spec.files         = `git ls-files -z`.split("\x0")
  spec.executables   = spec.files.grep(%r{^bin/}) { |f| File.basename(f) }
  spec.test_files    = spec.files.grep(%r{^(test|spec|features)/})
  spec.require_paths = ["lib"]

  spec.add_development_dependency "bundler", "~> 1.6"
  spec.add_development_dependency "rspec"
  spec.add_development_dependency "rake"
end
