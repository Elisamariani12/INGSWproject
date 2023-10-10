package it.polimi.ingsw.common.util;

/** Enumerates the states in which a leader card can be found */
public enum LeaderCardStatus {
    /**
     * status of discarded cards, including those exchanged for faith points
     */
    DISCARDED,
    /**
     * status of cards in use
     */
    IN_USE,
    /**
     * state of the cards chosen by the player at the beginning of the game
     */
    CHOSEN
}
