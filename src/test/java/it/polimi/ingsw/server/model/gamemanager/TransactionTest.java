package it.polimi.ingsw.server.model.gamemanager;

import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.server.exceptions.ResourceNotFoundInPaymentBufferException;
import it.polimi.ingsw.common.util.ProductionPower;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.ResourceStack;
import it.polimi.ingsw.common.util.StorageType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {
    private Transaction prodTransaction;
    private Transaction devTransaction;


    @BeforeEach
    void setUp() throws FullResourceStackException {
        ResourceStack r1 = new ResourceStack(Resource.COIN, 5, 5);
        ResourceStack r2 = new ResourceStack(Resource.COIN, 5, 5);
        ArrayList<ResourceStack> req = new ArrayList<>();
        req.add(r1);
        req.add(r2);
        ArrayList<ResourceStack> rew = new ArrayList<>();
        rew.add(r1);
        rew.add(r2);
        ProductionPower p1 = new ProductionPower(req, rew);
        ProductionPowerRequest t1 = new ProductionPowerRequest(p1);


        prodTransaction = new ProductionPowerRequest(p1);
    }

    @RepeatedTest(500)
    void addPaymentSource() {
        prodTransaction.addPaymentSource(Resource.STONE, StorageType.STRONG_BOX);

    }

    @Test
    void removePaymentSource() {
        prodTransaction.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        try {
            prodTransaction.removePaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        } catch (ResourceNotFoundInPaymentBufferException resourceNotFoundInPaymentBufferException) {
            assert false;
        }

        prodTransaction.addPaymentSource(Resource.COIN, StorageType.STRONG_BOX);
        prodTransaction.addPaymentSource(Resource.STONE, StorageType.WAREHOUSE_DEPOT);

        Assertions.assertThrows(ResourceNotFoundInPaymentBufferException.class, () -> {
            prodTransaction.removePaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        });

        try {
            prodTransaction.removePaymentSource(Resource.COIN, StorageType.STRONG_BOX);
            prodTransaction.removePaymentSource(Resource.STONE, StorageType.WAREHOUSE_DEPOT);
        } catch (ResourceNotFoundInPaymentBufferException resourceNotFoundInPaymentBufferException) {
            assert false;
        }

    }

    @Test
    void isTransactionValid() {
        for(int i=0 ; i<10; i++){
            prodTransaction.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        }
        assertTrue(prodTransaction.isTransactionValid());

        try {
            prodTransaction.removePaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        } catch (ResourceNotFoundInPaymentBufferException resourceNotFoundInPaymentBufferException) {
            assert false;
        }

        assertFalse(prodTransaction.isTransactionValid());

    }

    @Test
    void addToBuffer() {
    }

    @Test
    void removeFromBuffer() {
    }

    @Test
    void resourcesCounter() {
    }
}