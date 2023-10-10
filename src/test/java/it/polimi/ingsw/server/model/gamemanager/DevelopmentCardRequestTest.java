package it.polimi.ingsw.server.model.gamemanager;

import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.server.exceptions.ResourceNotFoundInPaymentBufferException;
import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.util.*;
import it.polimi.ingsw.common.util.Triplet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class DevelopmentCardRequestTest {
    private DevelopmentCard devCard;
    private HashSet<ResourceStack> resources;
    private ArrayList<Triplet<BannerColor, Integer, Integer>> requirements;
    private ArrayList<ResourceStack> prodPowerResources;
    private ProductionPower power;
    private DevelopmentCardRequest developmentCardRequest;


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
        power = new ProductionPower(prodPowerResources, prodPowerResources);
        devCard = new DevelopmentCard(5, resources, BannerColor.BLUE, 3, power, 10);
        developmentCardRequest = new DevelopmentCardRequest(devCard);
    }

    @Test
    void registerDiscount() {
        developmentCardRequest.registerDiscount(Resource.COIN);
        developmentCardRequest.registerDiscount(Resource.COIN, Resource.FAITH);
    }

    @Test
    void addPaymentSource() {
        developmentCardRequest.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
    }

    @Test
    void removePaymentSource() {
        developmentCardRequest.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        try {
            developmentCardRequest.removePaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        } catch (ResourceNotFoundInPaymentBufferException resourceNotFoundInPaymentBufferException) {
            assert false;
        }

        Assertions.assertThrows(ResourceNotFoundInPaymentBufferException.class, () -> {
            developmentCardRequest.removePaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        });
    }

    @Test
    void isTransactionValid1() {
        //without discount
        developmentCardRequest.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        developmentCardRequest.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        assertFalse(developmentCardRequest.isTransactionValid());
        developmentCardRequest.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        assertFalse(developmentCardRequest.isTransactionValid());
        developmentCardRequest.addPaymentSource(Resource.SERVANT, StorageType.LEADER_CARD);
        try {
            developmentCardRequest.removePaymentSource(Resource.SERVANT, StorageType.LEADER_CARD);
        } catch (ResourceNotFoundInPaymentBufferException resourceNotFoundInPaymentBufferException) {
            assert false;
        }
        Assertions.assertThrows(ResourceNotFoundInPaymentBufferException.class, () -> {
            developmentCardRequest.removePaymentSource(Resource.COIN, StorageType.WAREHOUSE_DEPOT);
        });
        developmentCardRequest.addPaymentSource(Resource.SERVANT, StorageType.WAREHOUSE_DEPOT);
        assertTrue(developmentCardRequest.isTransactionValid());
    }

    @Test
    void isTransactionValid2() {
        //with one wrong discount
        developmentCardRequest.registerDiscount(Resource.SHIELD);

        developmentCardRequest.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        developmentCardRequest.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        assertFalse(developmentCardRequest.isTransactionValid());
        developmentCardRequest.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        assertFalse(developmentCardRequest.isTransactionValid());
        developmentCardRequest.addPaymentSource(Resource.SERVANT, StorageType.LEADER_CARD);
        try {
            developmentCardRequest.removePaymentSource(Resource.SERVANT, StorageType.LEADER_CARD);
        } catch (ResourceNotFoundInPaymentBufferException resourceNotFoundInPaymentBufferException) {
            assert false;
        }
        Assertions.assertThrows(ResourceNotFoundInPaymentBufferException.class, () -> {
            developmentCardRequest.removePaymentSource(Resource.COIN, StorageType.WAREHOUSE_DEPOT);
        });
        developmentCardRequest.addPaymentSource(Resource.SERVANT, StorageType.WAREHOUSE_DEPOT);
        assertTrue(developmentCardRequest.isTransactionValid());
    }

    @Test
    void isTransactionValid3() {
        //with one right discount
        developmentCardRequest.registerDiscount(Resource.COIN);

        developmentCardRequest.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        assertFalse(developmentCardRequest.isTransactionValid());
        developmentCardRequest.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        assertFalse(developmentCardRequest.isTransactionValid());
        developmentCardRequest.addPaymentSource(Resource.SERVANT, StorageType.LEADER_CARD);
        try {
            developmentCardRequest.removePaymentSource(Resource.SERVANT, StorageType.LEADER_CARD);
        } catch (ResourceNotFoundInPaymentBufferException resourceNotFoundInPaymentBufferException) {
            assert false;
        }
        Assertions.assertThrows(ResourceNotFoundInPaymentBufferException.class, () -> {
            developmentCardRequest.removePaymentSource(Resource.COIN, StorageType.WAREHOUSE_DEPOT);
        });
        developmentCardRequest.addPaymentSource(Resource.SERVANT, StorageType.WAREHOUSE_DEPOT);
        assertTrue(developmentCardRequest.isTransactionValid());
    }

    @Test
    void isTransactionValid4() {
        //with one right and one wrong discount
        developmentCardRequest.registerDiscount(Resource.COIN, Resource.SHIELD);

        developmentCardRequest.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        assertFalse(developmentCardRequest.isTransactionValid());
        developmentCardRequest.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        assertFalse(developmentCardRequest.isTransactionValid());
        developmentCardRequest.addPaymentSource(Resource.SERVANT, StorageType.LEADER_CARD);
        try {
            developmentCardRequest.removePaymentSource(Resource.SERVANT, StorageType.LEADER_CARD);
        } catch (ResourceNotFoundInPaymentBufferException resourceNotFoundInPaymentBufferException) {
            assert false;
        }
        Assertions.assertThrows(ResourceNotFoundInPaymentBufferException.class, () -> {
            developmentCardRequest.removePaymentSource(Resource.COIN, StorageType.WAREHOUSE_DEPOT);
        });
        developmentCardRequest.addPaymentSource(Resource.SERVANT, StorageType.WAREHOUSE_DEPOT);
        assertTrue(developmentCardRequest.isTransactionValid());
    }

    @Test
    void isTransactionValid5() {
        //with two right discounts of the same resources
        developmentCardRequest.registerDiscount(Resource.COIN, Resource.COIN);

        assertFalse(developmentCardRequest.isTransactionValid());
        developmentCardRequest.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        assertFalse(developmentCardRequest.isTransactionValid());
        developmentCardRequest.addPaymentSource(Resource.SERVANT, StorageType.LEADER_CARD);
        try {
            developmentCardRequest.removePaymentSource(Resource.SERVANT, StorageType.LEADER_CARD);
        } catch (ResourceNotFoundInPaymentBufferException resourceNotFoundInPaymentBufferException) {
            assert false;
        }
        Assertions.assertThrows(ResourceNotFoundInPaymentBufferException.class, () -> {
            developmentCardRequest.removePaymentSource(Resource.COIN, StorageType.WAREHOUSE_DEPOT);
        });
        developmentCardRequest.addPaymentSource(Resource.SERVANT, StorageType.WAREHOUSE_DEPOT);
        assertTrue(developmentCardRequest.isTransactionValid());
    }

    @Test
    void isTransactionValid6() {
        //with two right discounts of different resources
        developmentCardRequest.registerDiscount(Resource.COIN, Resource.SERVANT);
        assertFalse(developmentCardRequest.isTransactionValid());
        developmentCardRequest.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        developmentCardRequest.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        developmentCardRequest.addPaymentSource(Resource.SERVANT, StorageType.LEADER_CARD);
        try {
            developmentCardRequest.removePaymentSource(Resource.SERVANT, StorageType.LEADER_CARD);
        } catch (ResourceNotFoundInPaymentBufferException resourceNotFoundInPaymentBufferException) {
            assert false;
        }
        Assertions.assertThrows(ResourceNotFoundInPaymentBufferException.class, () -> {
            developmentCardRequest.removePaymentSource(Resource.COIN, StorageType.WAREHOUSE_DEPOT);
        });
        assertTrue(developmentCardRequest.isTransactionValid());
    }

    @Test
    void isTransactionValid7() {
        //with two right discounts of different resources, it is expected to fail due to wrong quantities
        developmentCardRequest.registerDiscount(Resource.COIN, Resource.SERVANT);

        assertFalse(developmentCardRequest.isTransactionValid());
        developmentCardRequest.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        developmentCardRequest.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        developmentCardRequest.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        assertFalse(developmentCardRequest.isTransactionValid());
        developmentCardRequest.addPaymentSource(Resource.SERVANT, StorageType.LEADER_CARD);
        try {
            developmentCardRequest.removePaymentSource(Resource.SERVANT, StorageType.LEADER_CARD);
        } catch (ResourceNotFoundInPaymentBufferException resourceNotFoundInPaymentBufferException) {
            assert false;
        }
        Assertions.assertThrows(ResourceNotFoundInPaymentBufferException.class, () -> {
            developmentCardRequest.removePaymentSource(Resource.COIN, StorageType.WAREHOUSE_DEPOT);
        });
        assertFalse(developmentCardRequest.isTransactionValid());
    }


    @Test
    void getDevelopmentCard() {
        assertEquals(developmentCardRequest.getDevelopmentCard(), devCard);
    }
}