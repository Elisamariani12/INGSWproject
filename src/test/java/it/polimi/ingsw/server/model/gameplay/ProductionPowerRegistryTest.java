package it.polimi.ingsw.server.model.gameplay;

import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.common.util.ProductionPower;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.ResourceStack;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductionPowerRegistryTest {
    List<ResourceStack> listforproductionpower=new ArrayList<>();
    List<ResourceStack> listforproductionpower2=new ArrayList<>();
    ResourceStack r1 = new ResourceStack(Resource.COIN, 5, 5);
    ResourceStack r2 = new ResourceStack(Resource.SHIELD, 2, 3);



    @Test
    void getActivatedProductionPower_register_remove_ActivatedProductionPower() {
        listforproductionpower.add(r1);
        listforproductionpower2.add(r2);
        ProductionPower productionPower= new ProductionPower(listforproductionpower,listforproductionpower2);
        ProductionPowerRegistry productionPowerRegistry=new ProductionPowerRegistry();

        productionPowerRegistry.registerActivatedProductionPower(productionPower);
        assertTrue(productionPowerRegistry.getActivatedProductionPowers().contains(productionPower));

        productionPowerRegistry.removeActivatedProductionPower(productionPower);
        assertTrue(productionPowerRegistry.getActivatedProductionPowers().size() == 1);

    }

    ProductionPowerRegistryTest() throws FullResourceStackException {
    }
}