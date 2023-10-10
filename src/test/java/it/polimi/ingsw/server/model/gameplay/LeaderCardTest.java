package it.polimi.ingsw.server.model.gameplay;

import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.common.util.BannerColor;
import it.polimi.ingsw.common.util.ProductionPower;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.ResourceStack;
import it.polimi.ingsw.common.util.Triplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class LeaderCardTest {

    private LeaderCard leadCard;
    private HashSet<ResourceStack> resources;
    private ArrayList<ResourceStack> prodPowerResources;
    private ArrayList<Triplet<BannerColor, Integer, Integer>> requirements;
    private ProductionPower power;

    @BeforeEach
    void setUp() {
        try {
            resources = new HashSet<>();
            prodPowerResources = new ArrayList<>();
            resources.add( new ResourceStack(Resource.COIN, 3, 3));
            resources.add( new ResourceStack(Resource.SERVANT, 1, 2));
        } catch (FullResourceStackException e) {
            e.printStackTrace();
        }

        requirements = new ArrayList<>();
        requirements.add( new Triplet<>(BannerColor.BLUE, 2, 1));
        power = new ProductionPower(prodPowerResources, prodPowerResources);
        leadCard = new LeaderCard(5, resources, requirements, power, 89);
    }

    @Test
    void getBannerRequirements() {
        assertEquals(leadCard.getBannerRequirements(), requirements);
    }

    @Test
    void getPower() {
        assertEquals(leadCard.getPower(), power);
    }

    @Test
    void testToString() {
        leadCard.toString();
    }
}