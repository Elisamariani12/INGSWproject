package it.polimi.ingsw.common.util;

import it.polimi.ingsw.common.exception.FullResourceStackException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ProductionPowerTest {
    ResourceStack r1 = new ResourceStack(Resource.COIN, 5, 5);
    ResourceStack r2 = new ResourceStack(Resource.COIN, 5, 5);
    ArrayList<ResourceStack> req = new ArrayList<>();
    ArrayList<ResourceStack> rew = new ArrayList<>();

    ProductionPowerTest() throws FullResourceStackException {
    }

    @Test
    void getRequirements() {
        req.add(r1);
        req.add(r2);
        rew.add(r1);
        rew.add(r2);
        ProductionPower productionPower = new ProductionPower(req,rew);
        assertEquals(productionPower.getRequirements().get(1).getResourceType(), req.get(1).getResourceType());
    }

    @Test
    void getReward() {
        req.add(r1);
        req.add(r2);
        rew.add(r1);
        rew.add(r2);
        ProductionPower productionPower = new ProductionPower(req,rew);
        assertEquals(productionPower.getReward().get(1).getResourceType(), req.get(1).getResourceType());
    }

    @Test
    void testToString() {
        ProductionPower productionPower = new ProductionPower(req,rew);
        productionPower.toString();
    }
}