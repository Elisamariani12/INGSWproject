package it.polimi.ingsw.common.serializable;

import it.polimi.ingsw.common.model.ActionToken;
import it.polimi.ingsw.common.util.ActionTokenType;
import it.polimi.ingsw.common.util.Deck;

import java.util.LinkedHashMap;

/**
 * Represents a stock of Action Tokens for the JSON representation
 */
public class ActionTokenStock extends LinkedHashMap<ActionTokenType, Integer>
{
    /**
     * Returns an action token deck from the map
     * @return Deck of Action Tokens
     */
    public Deck<ActionToken> getActionTokenDeck()
    {
        Deck<ActionToken> deck = new Deck<>();

        for(ActionTokenType type : this.keySet())
            for(int amount = this.get(type); amount > 0; amount--)
                deck.addElement(new ActionToken(type));

        return deck;
    }
}
