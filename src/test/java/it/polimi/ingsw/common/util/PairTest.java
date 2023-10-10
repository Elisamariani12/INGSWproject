package it.polimi.ingsw.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PairTest {
    Pair<Integer, Integer> pair = new Pair<Integer, Integer>(5, 6);

    @Test
    void testEquals() {
        Pair<Integer, Integer> pair2 = new Pair<Integer, Integer>(5, 6);
        Pair<Integer, Integer> pair3 = new Pair<Integer, Integer>(7, 6);
        Pair<Integer, String> pair4 = new Pair<Integer, String>(7, "ciao");
        Pair<Integer, String> pair5 = null;
        assertTrue(pair.equals(pair2));
        assertFalse(pair.equals(pair3));
        assertFalse(pair.equals(pair4));
        assertFalse(pair.equals(pair5));
    }

    @Test
    void testToString() {
        assertEquals(pair.toString(), "(" + 5 + ", " + 6 + ")");
    }

    @Test
    void getFirst() {
        assertEquals(5, pair.getFirst());
    }

    @Test
    void setFirst() {
        pair.setFirst(88);
        assertEquals(88, pair.getFirst());
    }

    @Test
    void getSecond() {
        assertEquals(6, pair.getSecond());
    }

    @Test
    void setSecond() {
        pair.setSecond(88);
        assertEquals(88, pair.getSecond());
    }

    @Test
    void copy() {
        Pair<Integer, Integer> pair2 = pair.copy();
        assertEquals(pair2.getFirst(), pair.getFirst());
        assertEquals(pair2.getSecond(), pair.getSecond());
    }
}