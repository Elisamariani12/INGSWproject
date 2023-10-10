package it.polimi.ingsw.server.model.gameplay;

import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.common.util.BannerColor;
import it.polimi.ingsw.common.util.ProductionPower;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.ResourceStack;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DevelopmentCardSpaceTest {
    ResourceStack r1 = new ResourceStack(Resource.COIN, 5, 5);
    ResourceStack r2 = new ResourceStack(Resource.SHIELD, 2, 3);

    Set<ResourceStack> req1=new HashSet<>();
    List<ResourceStack> listforproductionpower=new ArrayList<>();
    List<ResourceStack> listforproductionpower2=new ArrayList<>();

    DevelopmentCardSpaceTest() throws FullResourceStackException {
    }

    @Test
    void getTotalStackVictoryPoints() {
        req1.add(r1);
        ProductionPower productionPower= new ProductionPower(listforproductionpower,listforproductionpower2);
        listforproductionpower.add(r1);
        listforproductionpower2.add(r2);
        DevelopmentCard developmentCard=new DevelopmentCard(1,req1, BannerColor.BLUE,2,productionPower,0);
        DevelopmentCard developmentCard2=new DevelopmentCard(3,req1, BannerColor.BLUE,3,productionPower,0);
        DevelopmentCardSpace developmentCardSpace=new DevelopmentCardSpace();

        developmentCardSpace.pushCard(1,developmentCard);
        developmentCardSpace.pushCard(1,developmentCard2);
        assertEquals(developmentCardSpace.getTotalStackVictoryPoints(), 4);
    }

    @Test
    void pushCard() {
        req1.add(r1);
        ProductionPower productionPower= new ProductionPower(listforproductionpower,listforproductionpower2);
        listforproductionpower.add(r1);
        listforproductionpower2.add(r2);
        DevelopmentCard developmentCard=new DevelopmentCard(1,req1, BannerColor.BLUE,2,productionPower,0);
        DevelopmentCardSpace developmentCardSpace=new DevelopmentCardSpace();

        developmentCardSpace.pushCard(1,developmentCard);
        assertTrue(developmentCardSpace.getHighestCard(1).equals(developmentCard));
    }

    @Test
    void getTotalCardAmount() {
        req1.add(r1);
        ProductionPower productionPower= new ProductionPower(listforproductionpower,listforproductionpower2);
        listforproductionpower.add(r1);
        listforproductionpower2.add(r2);
        DevelopmentCard developmentCard=new DevelopmentCard(1,req1, BannerColor.BLUE,2,productionPower,0);
        DevelopmentCard developmentCard2=new DevelopmentCard(3,req1, BannerColor.BLUE,3,productionPower,0);
        DevelopmentCardSpace developmentCardSpace=new DevelopmentCardSpace();

        developmentCardSpace.pushCard(1,developmentCard);
        developmentCardSpace.pushCard(1,developmentCard2);
        assertEquals(developmentCardSpace.getTotalCardAmount(), 2);
    }

    @Test
    void getHighestCardandgetDevelopmentCardDeck() {
        req1.add(r1);
        ProductionPower productionPower= new ProductionPower(listforproductionpower,listforproductionpower2);
        listforproductionpower.add(r1);
        listforproductionpower2.add(r2);
        DevelopmentCard developmentCard=new DevelopmentCard(1,req1, BannerColor.BLUE,2,productionPower,0);
        DevelopmentCard developmentCard2=new DevelopmentCard(3,req1, BannerColor.BLUE,3,productionPower,0);
        DevelopmentCardSpace developmentCardSpace=new DevelopmentCardSpace();

        developmentCardSpace.pushCard(1,developmentCard);
        developmentCardSpace.pushCard(1,developmentCard2);
        assertTrue(developmentCardSpace.getHighestCard(1).equals(developmentCard2));
        assertTrue(developmentCardSpace.getDevelopmentCardDeck(1).contains(developmentCard));
    }

    @Test
    void getNthCard() {
        req1.add(r1);
        ProductionPower productionPower= new ProductionPower(listforproductionpower,listforproductionpower2);
        listforproductionpower.add(r1);
        listforproductionpower2.add(r2);
        DevelopmentCard developmentCard=new DevelopmentCard(1,req1, BannerColor.BLUE,2,productionPower,0);
        DevelopmentCard developmentCard2=new DevelopmentCard(3,req1, BannerColor.BLUE,3,productionPower,0);
        DevelopmentCardSpace developmentCardSpace=new DevelopmentCardSpace();

        developmentCardSpace.pushCard(1,developmentCard);
        developmentCardSpace.pushCard(1,developmentCard2);
        assertTrue(developmentCardSpace.getNthCard(1,1).equals(developmentCard));
        assertTrue(developmentCardSpace.getNthCard(1,2).equals(developmentCard2));
    }
}