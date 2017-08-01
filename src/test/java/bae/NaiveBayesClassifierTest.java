package bae;

import bae.Document;
import bae.NaiveBayesClassifier;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class NaiveBayesClassifierTest {

    @Test
    public void canCorrectlyTrainFrequencyTable() {
        NaiveBayesClassifier n = new NaiveBayesClassifier();

        n.train("positive", new Document("bbb"));
        n.train("negative", new Document("ccc ccc ddd ddd ddd"));
        n.train("positive", new Document("aaa bbb bbb"));
        n.train("negative", new Document("ccc ccc ccc ddd ddd ddd ddd"));
        n.calculateInitialLikelihoods();

        assertEquals(1, n.getFrequencyTable().get("positive", "aaa"));
        assertEquals(3, n.getFrequencyTable().get("positive", "bbb"));
        assertEquals(5, n.getFrequencyTable().get("negative", "ccc"));
        assertEquals(7, n.getFrequencyTable().get("negative", "ddd"));
    }

    @Test
    public void canCorrectlyTrainWordTable() {
        NaiveBayesClassifier n = new NaiveBayesClassifier();

        n.train("positive", new Document("bbb"));
        n.train("negative", new Document("ccc ccc ddd ddd ddd"));
        n.train("positive", new Document("aaa bbb bbb"));
        n.train("negative", new Document("ccc ccc ccc ddd ddd ddd ddd"));
        n.calculateInitialLikelihoods();

        assertEquals(1, (long)n.getWordTable().get("aaa"));
        assertEquals(1, (long)n.getWordTable().get("bbb"));
        assertEquals(1, (long)n.getWordTable().get("ccc"));
        assertEquals(1, (long)n.getWordTable().get("ddd"));
    }

    @Test
    public void canCorrectlyTrainInstanceCount() {
        NaiveBayesClassifier n = new NaiveBayesClassifier();

        n.train("positive", new Document("bbb"));
        n.train("negative", new Document("ccc ccc ddd ddd ddd"));
        n.train("positive", new Document("aaa bbb bbb"));
        n.train("negative", new Document("ccc ccc ccc ddd ddd ddd ddd"));
        n.train("negative", new Document("ccc ccc ccc ddd ddd"));
        n.calculateInitialLikelihoods();

        assertEquals(2, (long)n.getInstanceCount().get("positive"));
        assertEquals(3, (long)n.getInstanceCount().get("negative"));
        assertEquals(5, (long)n.getTotalCount());
    }

    @Test
    public void canCorrectlyClassifyPositiveWithTwoLabels() {
        NaiveBayesClassifier n = new NaiveBayesClassifier();

        Document d = new Document("bbb");
        d.addZeroCount("aaa");

        n.train("positive", d);
        n.train("negative", new Document("ccc ccc ddd ddd ddd"));
        n.calculateInitialLikelihoods();

        Map<String, Double> results = n.classify(new Document("aaa bbb"));

        assertEquals(0.9411764705882353, results.get("positive"), 0.00001);
        assertEquals(0.05882352941176469, results.get("negative"), 0.00001);
    }

    @Test
    public void canCorrectlyClassifyNegativeWithTwoLabels() {
        NaiveBayesClassifier n = new NaiveBayesClassifier();

        Document d = new Document("bbb");
        d.addZeroCount("aaa");

        n.train("positive", d);
        n.train("negative", new Document("ccc ccc ddd ddd ddd"));
        n.calculateInitialLikelihoods();

        Map<String, Double> results = n.classify(new Document("ccc ccc ccc ddd ddd ddd"));

        assertEquals(0.05882352941176469, results.get("positive"), 0.00001);
        assertEquals(0.9411764705882353, results.get("negative"), 0.00001);
    }

    @Test
    public void canCorrectlyClassifyPositiveWithThreeLabels() {
        NaiveBayesClassifier n = new NaiveBayesClassifier();
        n.train("positive", new Document("aaa aaa bbb"));
        n.train("negative", new Document("ccc ccc ddd ddd"));
        n.train("neutral", new Document("eee eee eee fff fff fff"));
        n.calculateInitialLikelihoods();

        Map<String, Double> results = n.classify(new Document("aaa bbb"));

        assertEquals(0.896265560165975, results.get("positive"), 0.00001);
        assertEquals(0.06639004149377592, results.get("negative"), 0.00001);
        assertEquals(0.03734439834024896, results.get("neutral"), 0.00001);
    }

    @Test
    public void canCorrectlyClassifyNegativeWithThreeLabels() {
        NaiveBayesClassifier n = new NaiveBayesClassifier();

        n.train("positive", new Document("aaa aaa bbb"));
        n.train("negative", new Document("ccc ccc ddd ddd"));
        n.train("neutral", new Document("eee eee eee fff fff fff"));
        n.calculateInitialLikelihoods();

        Map<String, Double> results = n.classify(new Document("ccc ccc ccc ddd ddd"));

        assertEquals(0.05665722379603399, results.get("positive"), 0.00001);
        assertEquals(0.9178470254957508, results.get("negative"), 0.00001);
        assertEquals(0.0254957507082153, results.get("neutral"), 0.00001);
    }

    @Test
    public void canCorrectlyClassifyNeutralWithThreeLabels() {
        NaiveBayesClassifier n = new NaiveBayesClassifier();

        n.train("positive", new Document("aaa aaa bbb"));
        n.train("negative", new Document("ccc ccc ddd ddd"));
        n.train("neutral", new Document("eee eee eee fff fff fff"));
        n.calculateInitialLikelihoods();

        Map<String, Double> results = n.classify(new Document("aaa ddd ddd eee eee eee fff"));

        assertEquals(0.12195121951219513, results.get("positive"), 0.00001);
        assertEquals(0.09756097560975606, results.get("negative"), 0.00001);
        assertEquals(0.7804878048780488, results.get("neutral"), 0.00001);
    }
}
