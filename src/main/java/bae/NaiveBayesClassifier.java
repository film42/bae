package bae;

import java.util.HashMap;
import java.util.Map;

public class NaiveBayesClassifier {

    private FrequencyTable frequencyTable;
    private Map<String, Long> wordTable;
    private Map<String, Long> instanceCountOf;
    private Map<String, Double> initialLikelihoodOf;
    Map<String, Double> classPriorOf;
    private double totalCount = 0;

    public NaiveBayesClassifier() {
        this.frequencyTable = new FrequencyTable();
        this.wordTable = new HashMap<>();
        this.instanceCountOf = new HashMap<>();
        this.initialLikelihoodOf = new HashMap<>();
        this.classPriorOf = new HashMap<>();
    }

    public void train(String label, Document document) {
        // Add to the frequency table if it doesn't exist
        this.frequencyTable.insertOrIgnore(label);

        // Update frequency table with the documents frequency
        for(Map.Entry<String, Long> entry : document.getFrequencyMap().entrySet()) {
            String word = entry.getKey();
            long frequency = entry.getValue();

            // Update counts
            this.frequencyTable.increaseFrequencyBy(label, word, frequency);
            // Add the word's presence to the word table
            this.wordTable.put(word, 1L);
        }

        // Update global counts
        totalCount += 1;
        // Update instance count
        updateIntegerCountBy(this.instanceCountOf, label, 1);
    }

    public void calculateInitialLikelihoods() {
        // Update likelihood counts
        for(String label : this.frequencyTable.getLabels()) {
            // Set initial likelihood
            initialLikelihoodOf.put(label, 1d);

            // Calculate likelihoods
            for (String word : this.wordTable.keySet()) {
                double laplaceWordLikelihood =
                        (this.frequencyTable.get(label, word) + 1d) /
                        (this.instanceCountOf.get(label) + this.wordTable.size());

                // Update likelihood
                double likelihood = initialLikelihoodOf.get(label);
                initialLikelihoodOf.put(label, likelihood * (1d - laplaceWordLikelihood));
            }
        }

        // Update the prior
        for(Map.Entry<String, Long> entry : this.instanceCountOf.entrySet()) {
            String label = entry.getKey();
            double frequency = entry.getValue();

            // Update instance count
            this.classPriorOf.put(label, (frequency / this.totalCount));
        }
    }

    public Map<String, Double> classify(Document document) {
        Map<String, Double> likelihoodOf = new HashMap<>();
        Map<String, Double> classPosteriorOf = new HashMap<>();
        Map<String, Long> featureFrequencyMap = document.getFrequencyMap();
        double evidence = 0;

        // Update likelihood counts
        for(String label : this.frequencyTable.getLabels()) {
            // Set initial likelihood
            likelihoodOf.put(label, this.initialLikelihoodOf.get(label));

            // Calculate actual likelihoods likelihoods
            for(String word : featureFrequencyMap.keySet()) {
                double laplaceWordLikelihood =
                        (this.frequencyTable.get(label, word) + 1d) /
                        (this.instanceCountOf.get(label) + this.wordTable.size());

                // Update likelihood for words not in features
                double likelihood = likelihoodOf.get(label);
                if(featureFrequencyMap.containsKey(word)) {
                    likelihoodOf.put(label, (likelihood * laplaceWordLikelihood) / (1d - laplaceWordLikelihood));
                }
            }

            // Default class posterior of label to 1.0
            classPosteriorOf.put(label, 1d);

            // Update class posterior
            double classPosterior = this.classPriorOf.get(label) * likelihoodOf.get(label);
            classPosteriorOf.put(label, classPosterior);
            evidence += classPosterior;
        }

        // Normalize results
        for(Map.Entry<String, Double> entry : classPosteriorOf.entrySet()) {
            String label = entry.getKey();
            double posterior = entry.getValue();
            classPosteriorOf.put(label, posterior / evidence);
        }

        return classPosteriorOf;
    }

    public void updateIntegerCountBy(Map<String, Long> someMap, String someKey, long count) {
        if(!someMap.containsKey(someKey)) {
            someMap.put(someKey, 0L);
        }
        someMap.put(someKey, someMap.get(someKey) + count);
    }

    public void updateDoubleCountBy(Map<String, Double> someMap, String someKey, double count) {
        if(!someMap.containsKey(someKey)) {
            someMap.put(someKey, 0.0);
        }
        someMap.put(someKey, someMap.get(someKey) + count);
    }

    public FrequencyTable getFrequencyTable() {
        return this.frequencyTable;
    }

    public Map<String, Long> getWordTable() {
        return this.wordTable;
    }

    public Map<String, Long> getInstanceCount() {
        return this.instanceCountOf;
    }

    public double getTotalCount() {
        return totalCount;
    }
}
