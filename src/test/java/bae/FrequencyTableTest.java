package bae;

import bae.FrequencyTable;
import org.junit.Test;

import static org.junit.Assert.*;

public class FrequencyTableTest {

    @Test
    public void canInsertIntoFrequencyTable() {
        FrequencyTable frequencyTable = new FrequencyTable();

        frequencyTable.increaseFrequencyBy("a", "b", 100);

        assertEquals(100, frequencyTable.get("a", "b"));

        // Make sure we fail correctly
        assertEquals(0, frequencyTable.get("a", "z"));
        assertEquals(0, frequencyTable.get("z", "z"));
    }

}