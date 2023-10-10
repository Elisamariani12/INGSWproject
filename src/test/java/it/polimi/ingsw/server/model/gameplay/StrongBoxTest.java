package it.polimi.ingsw.server.model.gameplay;

import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.server.exceptions.InsufficientResourcesException;
import it.polimi.ingsw.common.util.Resource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StrongBoxTest {

    @Test
    void getResourceAmount() {
        StrongBox strongBox=new StrongBox();
        assertEquals(strongBox.getResourceAmount(Resource.COIN),0);
        assertEquals(strongBox.getResourceAmount(Resource.STONE),0);
        assertEquals(strongBox.getResourceAmount(Resource.SHIELD),0);
        assertEquals(strongBox.getResourceAmount(Resource.SERVANT),0);
        assertEquals(strongBox.getResourceAmount(Resource.FAITH), 0);


    }

    @Test
    void setResourceAmount() throws FullResourceStackException {
        StrongBox strongBox=new StrongBox();
        strongBox.setResourceAmount(Resource.COIN,2);
        strongBox.setResourceAmount(Resource.STONE,2);
        strongBox.setResourceAmount(Resource.SERVANT,2);
        strongBox.setResourceAmount(Resource.SHIELD,2);
        assertEquals(strongBox.getResourceAmount(Resource.COIN),2);
        assertEquals(strongBox.getResourceAmount(Resource.STONE),2);
        assertEquals(strongBox.getResourceAmount(Resource.SHIELD),2);
        assertEquals(strongBox.getResourceAmount(Resource.SERVANT),2);
    }

    @Test
    void addResourceAmount() throws FullResourceStackException {
        StrongBox strongBox=new StrongBox();
        strongBox.addResourceAmount(Resource.COIN,2);
        strongBox.addResourceAmount(Resource.STONE,2);
        strongBox.addResourceAmount(Resource.SERVANT,2);
        strongBox.addResourceAmount(Resource.SHIELD,2);
        assertEquals(strongBox.getResourceAmount(Resource.COIN),2);
        assertEquals(strongBox.getResourceAmount(Resource.STONE),2);
        assertEquals(strongBox.getResourceAmount(Resource.SHIELD),2);
        assertEquals(strongBox.getResourceAmount(Resource.SERVANT),2);

    }

    @Test
    void removeResourceAmount() throws FullResourceStackException, InsufficientResourcesException {
        StrongBox strongBox=new StrongBox();
        strongBox.addResourceAmount(Resource.COIN,2);
        strongBox.addResourceAmount(Resource.STONE,2);
        strongBox.addResourceAmount(Resource.SERVANT,2);
        strongBox.addResourceAmount(Resource.SHIELD,2);
        strongBox.removeResourceAmount(Resource.COIN,2);
        strongBox.removeResourceAmount(Resource.STONE,2);
        strongBox.removeResourceAmount(Resource.SERVANT,2);
        strongBox.removeResourceAmount(Resource.SHIELD,2);
        assertEquals(strongBox.getResourceAmount(Resource.COIN),0);
        assertEquals(strongBox.getResourceAmount(Resource.STONE),0);
        assertEquals(strongBox.getResourceAmount(Resource.SHIELD),0);
        assertEquals(strongBox.getResourceAmount(Resource.SERVANT),0);


        assertThrows(InsufficientResourcesException.class,()->{
            StrongBox strongbox1=new StrongBox();
            strongbox1.removeResourceAmount(Resource.COIN,2);
        });
        assertThrows(InsufficientResourcesException.class,()->{
            StrongBox strongbox2=new StrongBox();
            strongbox2.removeResourceAmount(Resource.STONE,2);
        });
        assertThrows(InsufficientResourcesException.class,()->{
            StrongBox strongbox3=new StrongBox();
            strongbox3.removeResourceAmount(Resource.SHIELD,2);
        });
        assertThrows(InsufficientResourcesException.class,()->{
            StrongBox strongbox4=new StrongBox();
            strongbox4.removeResourceAmount(Resource.SERVANT,2);
        });


    }
}