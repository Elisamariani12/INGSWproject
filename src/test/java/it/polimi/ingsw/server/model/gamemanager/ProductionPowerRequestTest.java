package it.polimi.ingsw.server.model.gamemanager;

import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.common.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductionPowerRequestTest {

    private ProductionPowerRequest ppr;
    private ProductionPower pow;



    @Test
    void addPaymentSource() {
    }

    @Test
    void removePaymentSource() {
    }

    @Test
    void isTransactionValid() throws FullResourceStackException {
        assertTrue(ppr.isTransactionValid());
    }

    @BeforeEach
    void setUp() throws FullResourceStackException {
        ResourceStack r1 = new ResourceStack(Resource.STONE, 2, 2);
        ResourceStack r2 = new ResourceStack(Resource.COIN, 1, 1);
        ResourceStack r3 = new ResourceStack(Resource.GENERIC, 3, 3);
        List<ResourceStack> list = new ArrayList<ResourceStack>();
        list.add(r1);
        list.add(r2);
        list.add(r3);
        pow = new ProductionPower(list, list);
        ppr = new ProductionPowerRequest(pow);


        ppr.addPaymentSource(Resource.STONE, StorageType.LEADER_CARD);
        ppr.addPaymentSource(Resource.STONE, StorageType.LEADER_CARD);
        ppr.addPaymentSource(Resource.COIN, StorageType.LEADER_CARD);
        ppr.addPaymentSource(Resource.STONE, StorageType.LEADER_CARD);
        ppr.addPaymentSource(Resource.STONE, StorageType.LEADER_CARD);
        ppr.addPaymentSource(Resource.STONE, StorageType.LEADER_CARD);
    }

    @Test
    void getProductionPower() {
        assertEquals(ppr.getProductionPower(), pow);
    }
}