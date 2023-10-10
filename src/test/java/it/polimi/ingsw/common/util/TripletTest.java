package it.polimi.ingsw.common.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TripletTest {

    private Triplet<Integer, Integer, Integer> triplet;

    @BeforeEach
    void setUp() {
        triplet = new Triplet<>(12, 10, 8);
    }

    @Test
    void testEquals() {
        Triplet<Integer, Integer, Integer> triplet2 = new Triplet<>(12, 10, 8);
        Triplet<Integer, Integer, Integer> triplet3 = new Triplet<>(10, 10, 8);
        Triplet<Integer, Integer, String> triplet4 = new Triplet<>(12, 10, "test");
        Triplet<Integer, Integer, String> triplet5 = null;
        assertTrue(triplet.equals(triplet2));
        assertFalse(triplet.equals(triplet3));
        assertFalse(triplet.equals(triplet4));
        assertFalse(triplet.equals(triplet5));
    }

    @Test
    void testToString() {
        assertEquals(triplet.toString(), "(" + 12 + ", " + 10 + ", " + 8 + ")");
    }

    @Test
    void getFirst() {
        assertEquals(triplet.getFirst(), 12);
    }

    @Test
    void setFirst() {
        triplet.setFirst(5);
        assertEquals(triplet.getFirst(), 5);
    }

    @Test
    void getSecond() {
        assertEquals(triplet.getSecond(), 10);
    }

    @Test
    void setSecond() {
        triplet.setSecond(5);
        assertEquals(triplet.getSecond(), 5);
    }

    @Test
    void getThird() {
        assertEquals(triplet.getThird(), 8);
    }

    @Test
    void setThird() {
        triplet.setThird(5);
        assertEquals(triplet.getThird(), 5);
    }

    @Test
    void copy() {
        Triplet<Integer, Integer, Integer> triplet5 = triplet.copy();
        assertEquals(triplet5, triplet);
    }

    @Test
    void eliminate2element() {
        Pair<Integer, Integer> pair = new Pair<>(12, 8);
        assertEquals(pair, triplet.eliminate2element());
    }
}