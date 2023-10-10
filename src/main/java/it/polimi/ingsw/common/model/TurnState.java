package it.polimi.ingsw.common.model;

/**
 * Enumerates all possible turn states (this class is symmetrical to controller\TurnState and has no additional functionality)
 * @see it.polimi.ingsw.server.controller.TurnState
 */
public enum TurnState
{
    //The initial setup is done, the player has to choose his leader cards
    SETUP_PLAYER_CHOICES,
    //The player can do a leader action at the beginning of his turn
    INITIAL_LEADER_ACTION,
    //The player has to decide his action: take resources from the market, buy a development card or activate the production
    CHOOSE_ACTION,
    //The player has taken resources from the market
    MARKET,
    //The player has decided to purchase leader cards, he has to decide which cart to buy
    DEV_CARD_PURCHASE,
    //The player has decided to activate productions, he has to decide which production to activate
    PRODUCTION_PHASE,
    //The player has finished productions of the turn and the transaction of resources is finished
    CLOSE_TRANSACTIONS,
    //The player can do a leader action at the end of his turn
    FINAL_LEADER_ACTIONS
}
