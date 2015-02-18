package bae;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FrequencyTable {

    private Map<String, Map<String, Long>> frequencyTable;

    public FrequencyTable() {
        this.frequencyTable = new HashMap<>();
    }

    public void insertOrIgnore(String label) {
        // Add new hash to frequency table if it's not already there
        this.frequencyTable.putIfAbsent(label, new HashMap<String, Long>());
    }

    public void increaseFrequencyBy(String label, String word, long frequency) {
        // Add label if it doesn't exist
        insertOrIgnore(label);

        Map<String, Long> frequencyRow = this.frequencyTable.get(label);

        // Make sure we have a frequency for that position in the table
        frequencyRow.putIfAbsent(word, 0L);

        // Update frequency
        frequencyRow.put(word, frequencyRow.get(word) + frequency);
    }

    public Set<String> getLabels() {
        return this.frequencyTable.keySet();
    }

    public long get(String label, String word) {
        try {
            return this.frequencyTable.get(label).get(word);
        } catch (NullPointerException e) {
            return 0L;
        }
    }
}
