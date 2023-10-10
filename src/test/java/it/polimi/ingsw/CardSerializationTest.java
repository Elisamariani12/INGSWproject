package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.polimi.ingsw.common.model.ActionToken;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.util.*;
import it.polimi.ingsw.common.util.CardRepository;
import org.junit.jupiter.api.Test;

/**
 * Unit test for JSON deserialization
 */
public class CardSerializationTest
{

    @Test
    public void deserialize()
    {

        CardRepository.getInstance().loadAllData();
        Deck<LeaderCard> cards= CardRepository.getInstance().getAllLeaderCards();

        for( LeaderCard x : cards){
            System.out.println(x.getCardID());
        }


    }
}
