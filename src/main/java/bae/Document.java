package bae;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Document {

    private Map<String, Long> frequencyMap;

    public Document(String text) {
        createFrequencyMap(text);
    }

    public Document(Map<String, Long> frequencyMap) {
        this.frequencyMap = frequencyMap;
    }

    public Map<String, Long> getFrequencyMap() {
        return frequencyMap;
    }

    public void addZeroCount(String key) {
        this.frequencyMap.put(key, 0L);
    }

    private void createFrequencyMap(String text) {
        this.frequencyMap = new HashMap<>();

        Scanner parser = new Scanner(text);
        while(parser.hasNext()) {
            String wordToken = parser.next();

            // Set initial count if it doesn't have one yet
            // Use zero because we'll add counts in the next line.
            if(!this.frequencyMap.containsKey(wordToken)) {
                this.frequencyMap.put(wordToken, 0L);
            }

            // Update count
            this.frequencyMap.put(wordToken, this.frequencyMap.get(wordToken) + 1);
        }
    }
}
