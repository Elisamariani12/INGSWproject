package it.polimi.ingsw.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeckTest
{
    @Test
    public void testAllElementsAreKeptDuringShuffle()
    {
        Deck<Integer> deck = new Deck<>();
        Deck<Integer> copiedDeck;

        for(int i = 0; i < 32; i++) deck.addElement(i);

        copiedDeck = deck.copy();

        deck.shuffle();

        System.out.println("Shuffled deck:");

        for(Integer num : copiedDeck)
        {
            assertTrue(deck.contains(num));
        }
        for(Integer num : deck)
        {
            assertTrue(copiedDeck.contains(num));
            System.out.println(num);
        }
    }
}
