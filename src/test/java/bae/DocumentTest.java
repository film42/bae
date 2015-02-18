package bae;

import bae.Document;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class DocumentTest {

    @Test
    public void testCanCreateAccurateFrequencyTable() {
        Document document = new Document("aaa bbb aaa bbb ccc");
        Map<String, Long> frequencyMap = document.getFrequencyMap();

        assertEquals(2, (long)frequencyMap.get("aaa"));
        assertEquals(2, (long)frequencyMap.get("bbb"));
        assertEquals(1, (long)frequencyMap.get("ccc"));
    }

    @Test
    public void testCanParseADirtyString() {
        Document document = new Document(" a    aaa\ta     \t\t\t  aa  a      bbb a  aaa   bbb   ccc    ");
        Map<String, Long> frequencyMap = document.getFrequencyMap();

        assertEquals(2, (long)frequencyMap.get("aaa"));
        assertEquals(2, (long)frequencyMap.get("bbb"));
        assertEquals(1, (long)frequencyMap.get("ccc"));
        assertEquals(1, (long)frequencyMap.get("aa"));
        assertEquals(4, (long)frequencyMap.get("a"));
    }

}