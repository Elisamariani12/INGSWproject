package it.polimi.ingsw.common.util;

/**
 * Enum that contains all the possible moves that the player can do
 */
public enum Move {
    //the player has chosen the starting resources and the starting leader cards: pass a List<Resources> destined for the WD+ ID of two leadercards
    INITIAL_CHOICES,
    //the player has decided to make a leader action: pass the card ID
    LEADER_ACTION,
    //the player has decided to go to the market: indicate the row / column +
    // + any resources to be replaced with the white marble (only if there are 2 leadercards)
    // + any excess resources to be eliminated
    MARKET_MOVE,
    //the player has decided to buy a development card, indicates the card to buy
    DEV_CARD_REQUEST,
    //the player has decided to activate a production: if it is the generic one then it indicates the Input / output Resources,
    //otherwise it indicates the corresponding development card
    PRODUCTION_POWER_REQUEST,
    //the player has decided to end production, so all the produced resources must be moved to the StrongBox
    PRODUCTION_POWER_END,
    //Used in fake controller packets to report a player's disconnection
    DISCONNECTION,
    //Used in fake controller packets to report a player's reconnection
    RECONNECTION
}
