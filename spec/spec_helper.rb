require 'bundler/setup'
require 'bae'
require 'rspec'

RSpec.configure do |c|
  c.order = :rand
  c.color = true
end
