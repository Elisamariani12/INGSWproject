package it.polimi.ingsw.server.model.gameplay;

import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.server.exceptions.InsufficientResourcesException;
import it.polimi.ingsw.common.util.*;
import it.polimi.ingsw.common.util.Triplet;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LeaderCardSpaceTest {
    AbilityPower abilityPower=new AbilityPower(Resource.COIN,SpecialAbility.DISCOUNT);
    AbilityPower abilityPower2=new AbilityPower(Resource.COIN,SpecialAbility.STORAGE);
    ResourceStack r1 = new ResourceStack(Resource.COIN, 5, 5);
    ResourceStack r2 = new ResourceStack(Resource.SHIELD, 2, 3);
    Set<ResourceStack> req1=new HashSet<>();
    Set<ResourceStack> req2=new HashSet<>();
    List<ResourceStack> listforproductionpower=new ArrayList<>();
    List<ResourceStack> listforproductionpower2=new ArrayList<>();
    List<Triplet<BannerColor,Integer,Integer>> bannerrequirements=new ArrayList<>();
    List<Triplet<BannerColor,Integer,Integer>> bannerrequirements2=new ArrayList<>();

    LeaderCardSpaceTest() throws FullResourceStackException {
    }

    @Test
    void addCard() {
        Triplet<BannerColor,Integer,Integer> triplet=new Triplet<>(BannerColor.BLUE,2,2);
        Triplet<BannerColor,Integer,Integer> triplet2=new Triplet<>(BannerColor.BLUE,1,1);
        listforproductionpower.add(r1);
        listforproductionpower2.add(r2);
        ProductionPower productionPower= new ProductionPower(listforproductionpower,listforproductionpower2);
        bannerrequirements.add(triplet);
        bannerrequirements2.add(triplet2);
        req1.add(r1);
        req2.add(r2);
        LeaderCard leaderCard=new LeaderCard(1,req1,bannerrequirements,abilityPower,12);
        LeaderCard leaderCard1=new LeaderCard(2,req2,bannerrequirements2,abilityPower2,12);
        LeaderCard leaderCard2=new LeaderCard(2,req2,bannerrequirements2,productionPower,12);
        LeaderCardSpace leaderCardSpace=new LeaderCardSpace();

        leaderCardSpace.addCard(leaderCard);
        leaderCardSpace.addCard(leaderCard2);
        assertTrue(leaderCardSpace.getAllCards().contains(leaderCard));
        assertTrue(leaderCardSpace.getAllCards().contains(leaderCard2));
        assertTrue(leaderCardSpace.ResourcesFromCard(leaderCard2).getMaxSize()==0);
    }

    @Test
    void setCardStatus() {
        Triplet<BannerColor,Integer,Integer> triplet=new Triplet<>(BannerColor.BLUE,2,2);
        Triplet<BannerColor,Integer,Integer> triplet2=new Triplet<>(BannerColor.BLUE,1,1);
        bannerrequirements.add(triplet);
        bannerrequirements2.add(triplet2);
        req1.add(r1);
        req2.add(r2);
        LeaderCard leaderCard=new LeaderCard(1,req1,bannerrequirements,abilityPower,12);
        LeaderCard leaderCard1=new LeaderCard(2,req2,bannerrequirements2,abilityPower2,12);
        LeaderCardSpace leaderCardSpace=new LeaderCardSpace();

        leaderCardSpace.addCard(leaderCard);
        leaderCardSpace.setCardStatus(leaderCard,LeaderCardStatus.IN_USE);
        assertTrue(leaderCardSpace.getActiveCards().contains(leaderCard));
    }

    @Test
    void getActiveCards() {
        Triplet<BannerColor,Integer,Integer> triplet=new Triplet<>(BannerColor.BLUE,2,2);
        Triplet<BannerColor,Integer,Integer> triplet2=new Triplet<>(BannerColor.BLUE,1,1);
        bannerrequirements.add(triplet);
        bannerrequirements2.add(triplet2);
        req1.add(r1);
        req2.add(r2);
        LeaderCard leaderCard=new LeaderCard(1,req1,bannerrequirements,abilityPower,12);
        LeaderCard leaderCard1=new LeaderCard(2,req2,bannerrequirements2,abilityPower2,12);
        LeaderCardSpace leaderCardSpace=new LeaderCardSpace();

        leaderCardSpace.addCard(leaderCard);
        leaderCardSpace.setCardStatus(leaderCard,LeaderCardStatus.IN_USE);
        assertTrue(leaderCardSpace.getActiveCards().contains(leaderCard));
    }

    @Test
    void getChosenCards() {
        Triplet<BannerColor,Integer,Integer> triplet=new Triplet<>(BannerColor.BLUE,2,2);
        Triplet<BannerColor,Integer,Integer> triplet2=new Triplet<>(BannerColor.BLUE,1,1);
        bannerrequirements.add(triplet);
        bannerrequirements2.add(triplet2);
        req1.add(r1);
        req2.add(r2);
        LeaderCard leaderCard=new LeaderCard(1,req1,bannerrequirements,abilityPower,12);
        LeaderCard leaderCard1=new LeaderCard(2,req2,bannerrequirements2,abilityPower2,12);
        LeaderCardSpace leaderCardSpace=new LeaderCardSpace();

        leaderCardSpace.addCard(leaderCard);
        leaderCardSpace.setCardStatus(leaderCard,LeaderCardStatus.IN_USE);
        assertTrue(leaderCardSpace.getChosenCards().contains(leaderCard));
    }

    @Test
    void getActiveStorageCards() {
        Triplet<BannerColor,Integer,Integer> triplet=new Triplet<>(BannerColor.BLUE,2,2);
        Triplet<BannerColor,Integer,Integer> triplet2=new Triplet<>(BannerColor.BLUE,1,1);
        bannerrequirements.add(triplet);
        bannerrequirements2.add(triplet2);
        req1.add(r1);
        req2.add(r2);
        LeaderCard leaderCard=new LeaderCard(1,req1,bannerrequirements,abilityPower,12);
        LeaderCard leaderCard1=new LeaderCard(2,req2,bannerrequirements2,abilityPower2,12);
        LeaderCardSpace leaderCardSpace=new LeaderCardSpace();

        leaderCardSpace.addCard(leaderCard);
        leaderCardSpace.addCard(leaderCard1);
        leaderCardSpace.setCardStatus(leaderCard,LeaderCardStatus.IN_USE);
        leaderCardSpace.setCardStatus(leaderCard1,LeaderCardStatus.IN_USE);
        assertTrue(leaderCardSpace.getActiveStorageCards().size()==1);

    }

    @Test
    void getAllCards() {
        Triplet<BannerColor,Integer,Integer> triplet=new Triplet<>(BannerColor.BLUE,2,2);
        Triplet<BannerColor,Integer,Integer> triplet2=new Triplet<>(BannerColor.BLUE,1,1);
        bannerrequirements.add(triplet);
        bannerrequirements2.add(triplet2);
        req1.add(r1);
        req2.add(r2);
        LeaderCard leaderCard=new LeaderCard(1,req1,bannerrequirements,abilityPower,12);
        LeaderCard leaderCard1=new LeaderCard(2,req2,bannerrequirements2,abilityPower2,12);
        LeaderCardSpace leaderCardSpace=new LeaderCardSpace();

        leaderCardSpace.addCard(leaderCard);

        assertTrue(leaderCardSpace.getAllCards().contains(leaderCard));
        assertFalse(leaderCardSpace.getAllCards().contains(leaderCard1));
    }


    @Test
    void resourcesFromCard() throws FullResourceStackException {
        Triplet<BannerColor,Integer,Integer> triplet=new Triplet<>(BannerColor.BLUE,2,2);
        Triplet<BannerColor,Integer,Integer> triplet2=new Triplet<>(BannerColor.BLUE,1,1);
        bannerrequirements.add(triplet);
        bannerrequirements2.add(triplet2);
        req1.add(r1);
        req2.add(r2);
        LeaderCard leaderCard=new LeaderCard(1,req1,bannerrequirements,abilityPower,12);
        LeaderCard leaderCard1=new LeaderCard(2,req2,bannerrequirements2,abilityPower2,12);
        LeaderCardSpace leaderCardSpace=new LeaderCardSpace();

        leaderCardSpace.addCard(leaderCard);
        assertEquals(leaderCardSpace.ResourcesFromCard(leaderCard).getResourceType(),Resource.COIN);
        assertEquals(leaderCardSpace.ResourcesFromCard(leaderCard).getAmount(),0);
        assertEquals(leaderCardSpace.ResourcesFromCard(leaderCard).getMaxSize(),2);
        assertNull(leaderCardSpace.ResourcesFromCard(leaderCard1));
    }

    @Test
    void setResourcesIntoCard() throws FullResourceStackException {
        Triplet<BannerColor,Integer,Integer> triplet=new Triplet<>(BannerColor.BLUE,2,2);
        Triplet<BannerColor,Integer,Integer> triplet2=new Triplet<>(BannerColor.BLUE,1,1);
        bannerrequirements.add(triplet);
        bannerrequirements2.add(triplet2);
        req1.add(r1);
        req2.add(r2);
        LeaderCard leaderCard=new LeaderCard(1,req1,bannerrequirements,abilityPower,12);
        LeaderCard leaderCard1=new LeaderCard(2,req2,bannerrequirements2,abilityPower2,12);
        LeaderCardSpace leaderCardSpace=new LeaderCardSpace();


        leaderCardSpace.addCard(leaderCard1);
        leaderCardSpace.setResourcesIntoCard(leaderCard1,2);
        assertEquals(leaderCardSpace.ResourcesFromCard(leaderCard1).getResourceType(),Resource.COIN);
        assertEquals(leaderCardSpace.ResourcesFromCard(leaderCard1).getAmount(),2);
        assertEquals(leaderCardSpace.ResourcesFromCard(leaderCard1).getMaxSize(),2);

        assertThrows(FullResourceStackException.class,()->{
            leaderCardSpace.setResourcesIntoCard(leaderCard1,1);
        });
    }

    @Test
    void removeResourcesFromCard() throws FullResourceStackException, InsufficientResourcesException {
        Triplet<BannerColor,Integer,Integer> triplet=new Triplet<>(BannerColor.BLUE,2,2);
        Triplet<BannerColor,Integer,Integer> triplet2=new Triplet<>(BannerColor.BLUE,1,1);
        bannerrequirements.add(triplet);
        bannerrequirements2.add(triplet2);
        req1.add(r1);
        req2.add(r2);
        LeaderCard leaderCard=new LeaderCard(1,req1,bannerrequirements,abilityPower,12);
        LeaderCard leaderCard1=new LeaderCard(2,req2,bannerrequirements2,abilityPower2,12);
        LeaderCardSpace leaderCardSpace=new LeaderCardSpace();

        leaderCardSpace.addCard(leaderCard1);
        leaderCardSpace.setResourcesIntoCard(leaderCard1,2);
        leaderCardSpace.removeResourcesFromCard(leaderCard1,2);
        assertEquals(leaderCardSpace.ResourcesFromCard(leaderCard1).getResourceType(),Resource.COIN);
        assertEquals(leaderCardSpace.ResourcesFromCard(leaderCard1).getAmount(),0);
        assertEquals(leaderCardSpace.ResourcesFromCard(leaderCard1).getMaxSize(),2);

        assertThrows(InsufficientResourcesException.class,()->{
            leaderCardSpace.removeResourcesFromCard(leaderCard1,1);
        });

    }
}