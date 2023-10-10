package it.polimi.ingsw.server.model.gameplay;

import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.server.exceptions.ResourceNotInsertableException;
import it.polimi.ingsw.common.util.Resource;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseDepotTest {

    @Test
    void setLayerType() throws FullResourceStackException {
        WarehouseDepot warehouseDepot=new WarehouseDepot();
        warehouseDepot.SetLayerMaxAmount();
        warehouseDepot.setLayerType(1,Resource.COIN);
        assertEquals(warehouseDepot.getLayerType(1),Resource.COIN);
    }

    @Test
    void getLayerType() throws FullResourceStackException {
        WarehouseDepot warehouseDepot=new WarehouseDepot();
        warehouseDepot.SetLayerMaxAmount();
        assertNull(warehouseDepot.getLayerType(1));
    }

    @Test
    void getLayerAmount() throws FullResourceStackException {
        WarehouseDepot warehouseDepot=new WarehouseDepot();
        warehouseDepot.SetLayerMaxAmount();
        assertEquals(warehouseDepot.getLayerAmount(1),0);
    }

    @Test
    void setLayerAmount() throws FullResourceStackException {
        WarehouseDepot warehouseDepot=new WarehouseDepot();
        warehouseDepot.SetLayerMaxAmount();

        warehouseDepot.setLayerAmount(2,2);
        assertEquals(warehouseDepot.getLayerAmount(2),2);

        assertThrows(FullResourceStackException.class,()->{
            WarehouseDepot warehouseDepot1=new WarehouseDepot();
            warehouseDepot1.SetLayerMaxAmount();
            warehouseDepot1.setLayerAmount(2,3);
        });

    }

    @Test
    void getAmountByResource() throws FullResourceStackException {
        WarehouseDepot warehouseDepot=new WarehouseDepot();
        warehouseDepot.SetLayerMaxAmount();
        assertEquals(warehouseDepot.getAmountByResource(Resource.COIN),0);
    }

    @Test
    void InsertResource() throws ResourceNotInsertableException {
        WarehouseDepot warehouseDepot=new WarehouseDepot();
        warehouseDepot.SetLayerMaxAmount();

        warehouseDepot.InsertResource(Resource.SERVANT);
        assertEquals(warehouseDepot.getAmountByResource(Resource.SERVANT), 1);
        warehouseDepot.InsertResource(Resource.STONE);
        assertEquals(warehouseDepot.getAmountByResource(Resource.STONE), 1);
        warehouseDepot.InsertResource(Resource.COIN);
        assertEquals(warehouseDepot.getAmountByResource(Resource.COIN), 1);
        warehouseDepot.InsertResource(Resource.COIN);
        assertEquals(warehouseDepot.getAmountByResource(Resource.COIN), 2);
        warehouseDepot.InsertResource(Resource.SERVANT);
        assertEquals(warehouseDepot.getAmountByResource(Resource.SERVANT), 2);
        warehouseDepot.InsertResource(Resource.SERVANT);
        assertEquals(warehouseDepot.getAmountByResource(Resource.SERVANT), 3);
        assertThrows(ResourceNotInsertableException.class,()->warehouseDepot.InsertResource(Resource.SERVANT));

    }

    @RepeatedTest(100)
    void StressedInsertResource() throws ResourceNotInsertableException {
        WarehouseDepot warehouseDepot=new WarehouseDepot();
        warehouseDepot.SetLayerMaxAmount();
        ArrayList<Resource> elements = new ArrayList<>();

        elements.add(Resource.STONE);
        elements.add(Resource.COIN);
        elements.add(Resource.COIN);
        elements.add(Resource.SERVANT);
        elements.add(Resource.SERVANT);
        elements.add(Resource.SERVANT);

        Collections.shuffle(elements);
        //System.out.println(elements.toString());

        for (Resource res : elements){
            warehouseDepot.InsertResource(res);
        }

        assertEquals(warehouseDepot.getAmountByResource(Resource.COIN), 2);
        assertEquals(warehouseDepot.getAmountByResource(Resource.SERVANT), 3);
        assertEquals(warehouseDepot.getAmountByResource(Resource.STONE), 1);



    }
}