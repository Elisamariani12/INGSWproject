package it.polimi.ingsw.common.util;

import it.polimi.ingsw.common.exception.FullResourceStackException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceStackTest {

    ResourceStack res1;

    {
        try {
            res1 = new ResourceStack(Resource.COIN, 5,10);
        } catch (FullResourceStackException e) {
            e.printStackTrace();
        }
    }

    ResourceStack res2 = new ResourceStack(Resource.COIN, 5);
    ResourceStack res3 = new ResourceStack(Resource.COIN);

    @Test
    void exceptionTester() {
        Assertions.assertThrows(FullResourceStackException.class, () -> {
            new ResourceStack(Resource.COIN, 15,10);
        });
    }

    @Test
    void getResourceType() {
        assertEquals(res1.getResourceType(), Resource.COIN);
        assertEquals(res2.getResourceType(), Resource.COIN);
        assertEquals(res3.getResourceType(), Resource.COIN);
    }

    @Test
    void getAmount() {
        assertEquals(res1.getAmount(), 5);
        assertEquals(res2.getAmount(), 0);
        assertEquals(res3.getAmount(), 0);
    }

    @Test
    void getMaxSize() {
        assertEquals(res1.getMaxSize(), 10);
        assertEquals(res2.getMaxSize(), 5);
        assertEquals(res3.getMaxSize(), Integer.MAX_VALUE);
    }

    @Test
    void setType() {
        res1.setType(Resource.STONE);
        res2.setType(Resource.STONE);
        res3.setType(Resource.STONE);

        assertEquals(res1.getResourceType(), Resource.STONE);
        assertEquals(res2.getResourceType(), Resource.STONE);
        assertEquals(res3.getResourceType(), Resource.STONE);
    }

    @Test
    void setAmount() {
        try {
            res1.setAmount(8);
        } catch (FullResourceStackException e) {
            assert false;
        }

        assertEquals(res1.getAmount(), 8);

        Assertions.assertThrows(FullResourceStackException.class, () -> {
            res1.setAmount(200);
        });

        try {
            res2.setAmount(4);
        } catch (FullResourceStackException e) {
            assert false;
        }

        assertEquals(res2.getAmount(), 4);

        try {
            res3.setAmount(300);
        } catch (FullResourceStackException e) {
            assert false;
        }

        assertEquals(res3.getAmount(), 300);


    }

    @Test
    void setMaxSize() {
        ResourceStack res5 = new ResourceStack(Resource.COIN, 5);
        try {
            res5.setAmount(4);
        } catch (FullResourceStackException e) {
            e.printStackTrace();
        }
        try {
            res5.setMaxSize(20);
        } catch (FullResourceStackException e) {
            e.printStackTrace();
        }
        assertEquals(res5.getMaxSize(), 20);


        Assertions.assertThrows(FullResourceStackException.class, () -> {
            res5.setMaxSize(3);
        });


    }

    @Test
    void copy() {
        ResourceStack res6 = null;
        try {
            res6 = new ResourceStack(Resource.COIN, 4,5);
        } catch (FullResourceStackException e) {
            e.printStackTrace();
        }
        assertEquals(res6.copy().getAmount(),res6.getAmount());
        assertEquals(res6.copy().getMaxSize(),res6.getMaxSize());
        assertEquals(res6.copy().getResourceType(),res6.getResourceType());
    }

    @Test
    void ToString() {
        ResourceStack res7 = new ResourceStack(Resource.COIN, 5);
        res7.toString();
    }

}