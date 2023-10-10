package it.polimi.ingsw.server.model.gameplay;

import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.server.exceptions.FaithTrackOutOfBoundsException;
import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.common.util.*;
import it.polimi.ingsw.common.util.Triplet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PersonalBoardTest {
    AbilityPower abilityPower=new AbilityPower(Resource.COIN, SpecialAbility.DISCOUNT);
    AbilityPower abilityPower2=new AbilityPower(Resource.COIN,SpecialAbility.STORAGE);
    ResourceStack r1 = new ResourceStack(Resource.COIN, 5, 5);
    ResourceStack r2 = new ResourceStack(Resource.SHIELD, 2, 3);
    Set<ResourceStack> req1=new HashSet<>();
    Set<ResourceStack> req2=new HashSet<>();
    List<Triplet<BannerColor,Integer,Integer>> bannerrequirements=new ArrayList<>();
    List<Triplet<BannerColor,Integer,Integer>> bannerrequirements2=new ArrayList<>();

    //to test the getter of developmentcardspace
    List<ResourceStack> listforproductionpower=new ArrayList<>();
    List<ResourceStack> listforproductionpower2=new ArrayList<>();

    PersonalBoardTest() throws FullResourceStackException {
    }


    @Test
    void getTotalStoredAmount() throws FullResourceStackException {

        PersonalBoard personalBoard=new PersonalBoard();

        Triplet<BannerColor,Integer,Integer> triplet=new Triplet<>(BannerColor.BLUE,2,2);
        Triplet<BannerColor,Integer,Integer> triplet2=new Triplet<>(BannerColor.BLUE,1,1);
        bannerrequirements.add(triplet);
        bannerrequirements2.add(triplet2);
        req1.add(r1);
        req2.add(r2);
        LeaderCard leaderCard=new LeaderCard(1,req1,bannerrequirements,abilityPower,12);
        LeaderCard leaderCard1=new LeaderCard(2,req2,bannerrequirements2,abilityPower2,12);

        personalBoard.getLeaderCardSpace().addCard(leaderCard);
        personalBoard.getLeaderCardSpace().addCard(leaderCard1);
        personalBoard.getLeaderCardSpace().setCardStatus(leaderCard,LeaderCardStatus.IN_USE);
        personalBoard.getLeaderCardSpace().setCardStatus(leaderCard1,LeaderCardStatus.IN_USE);
        personalBoard.getLeaderCardSpace().setResourcesIntoCard(leaderCard1,1);

        personalBoard.getWarehouseDepot().SetLayerMaxAmount();
        personalBoard.getWarehouseDepot().setLayerType(1,Resource.COIN);
        personalBoard.getWarehouseDepot().setLayerAmount(1,3);


        personalBoard.getStrongBox().setResourceAmount(Resource.COIN,2);
        personalBoard.getStrongBox().setResourceAmount(Resource.STONE,2);
        personalBoard.getStrongBox().setResourceAmount(Resource.SERVANT,2);
        personalBoard.getStrongBox().setResourceAmount(Resource.SHIELD,2);

        assertEquals(personalBoard.getTotalStoredAmount(Resource.COIN),6);
        assertEquals(personalBoard.getTotalStoredAmount(Resource.SHIELD),2);
        assertEquals(personalBoard.getTotalStoredAmount(Resource.STONE),2);
        assertEquals(personalBoard.getTotalStoredAmount(Resource.SERVANT),2);

    }




    @Test
    void getFaithTrack() throws FaithTrackOutOfBoundsException{
        PersonalBoard P=new PersonalBoard();
        P.getFaithTrack().advance(3);
        assertEquals(P.getFaithTrack().getPosition(),3);
    }

    @Test
    void getProductionPowerRegistry() {
        listforproductionpower.add(r1);
        listforproductionpower2.add(r2);
        ProductionPower productionPower= new ProductionPower(listforproductionpower,listforproductionpower2);
        PersonalBoard personalBoard=new PersonalBoard();
        personalBoard.getProductionPowerRegistry().registerActivatedProductionPower(productionPower);

        assertTrue(personalBoard.getProductionPowerRegistry().getActivatedProductionPowers().contains(productionPower));

    }


    @Test
    void getDevelopmentCardSpace() {
        req1.add(r1);
        listforproductionpower.add(r1);
        listforproductionpower2.add(r2);
        ProductionPower productionPower= new ProductionPower(listforproductionpower,listforproductionpower2);
        DevelopmentCard developmentCard=new DevelopmentCard(1,req1, BannerColor.BLUE,2,productionPower,0);

        PersonalBoard personalBoard=new PersonalBoard();
        personalBoard.getDevelopmentCardSpace().pushCard(1,developmentCard);
        assertEquals(personalBoard.getDevelopmentCardSpace().getHighestCard(1),developmentCard);
    }
}