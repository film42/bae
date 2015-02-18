require 'spec_helper'

describe ::Bae::Classifier do

  subject { described_class.new }

  it "can classify from ruby to java with a hash document" do
    subject.train("positive", {"aaa" => 0, "bbb" => 1})
    subject.train("negative", {"ccc" => 2, "ddd" => 3})
    results = subject.classify({"aaa" => 1, "bbb" => 1})

    expect(results["positive"]).to be_within(0.001).of(0.94117)
    expect(results["negative"]).to be_within(0.001).of(0.05882)
  end

  it "can classify from ruby to java with a string based document" do
    subject.train("positive", "aaa aaa bbb");
    subject.train("negative", "ccc ccc ddd ddd");
    subject.train("neutral", "eee eee eee fff fff fff");
    results = subject.classify("aaa bbb")

    expect(results["positive"]).to be_within(0.001).of(0.89626)
    expect(results["negative"]).to be_within(0.001).of(0.06639)
    expect(results["neutral"]).to be_within(0.001).of(0.03734)
  end

end
