package it.polimi.ingsw.server.model.gameplay;

import it.polimi.ingsw.common.model.Card;
import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.common.util.*;
import it.polimi.ingsw.common.util.Triplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    private Card devCard;
    private Card leadCard;
    private HashSet<ResourceStack> resources;
    private ArrayList<ResourceStack> prodPowerResources;
    private ArrayList<Triplet<BannerColor, Integer, Integer>> requirements;

    @BeforeEach
    void setUp() {
        try {
            resources = new HashSet<>();
            prodPowerResources = new ArrayList<>();
            resources.add( new ResourceStack(Resource.COIN, 3, 3));
            resources.add( new ResourceStack(Resource.SERVANT, 1, 2));
            prodPowerResources.add( new ResourceStack(Resource.SERVANT, 10, 10));
            prodPowerResources.add( new ResourceStack(Resource.STONE, 4, 10));
        } catch (FullResourceStackException e) {
            e.printStackTrace();
        }

        requirements = new ArrayList<>();
        requirements.add( new Triplet<>(BannerColor.BLUE, 2, 1));
        ProductionPower power = new ProductionPower(prodPowerResources, prodPowerResources);
        devCard = new DevelopmentCard(5, resources, BannerColor.BLUE, 3, power, 10);
        leadCard = new LeaderCard(5, resources, requirements, power, 89);

        //just to show 100% coverage
        Card leadCard2 = new LeaderCard(5, resources, requirements, power,12);
    }

    @Test
    void getVictoryPoints() {
        assertEquals(devCard.getVictoryPoints(), 5);
        assertEquals(leadCard.getVictoryPoints(), 5);
    }

    @Test
    void getRequiredResources() {
        assertEquals(devCard.getRequiredResources(), resources);
        assertEquals(leadCard.getRequiredResources(), resources);
    }

    @Test
    void getCardID() {
        assertEquals(devCard.getCardID(), 10);
        assertEquals(leadCard.getCardID(), 89);
    }

    @Test
    void testToString() {
        devCard.toString();
    }
}