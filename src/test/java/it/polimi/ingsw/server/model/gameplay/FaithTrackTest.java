package it.polimi.ingsw.server.model.gameplay;

import it.polimi.ingsw.server.exceptions.FaithTrackOutOfBoundsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FaithTrackTest {


    @Test
    void getPosition() {
        FaithTrack faithTrack=new FaithTrack();
        assertEquals(faithTrack.getPosition(),0);
    }

    @Test
    void advance() throws FaithTrackOutOfBoundsException {
        FaithTrack faithTrack=new FaithTrack();
        faithTrack.advance(24);
        assertEquals(faithTrack.getPosition(),24);

        Assertions.assertThrows(FaithTrackOutOfBoundsException.class,() ->{
            FaithTrack faithTrack1=new FaithTrack();
            faithTrack1.advance(25);
        });
    }

    @Test
    void getPopeFavour() {
        FaithTrack faithTrack=new FaithTrack();
        assertFalse(faithTrack.getPopeFavour(1));
    }

    @Test
    void activatePopeFavour() {
        FaithTrack faithTrack=new FaithTrack();
        faithTrack.activatePopeFavour(1);
        faithTrack.activatePopeFavour(2);
        assertTrue(faithTrack.getPopeFavour(1));
        assertTrue(faithTrack.getPopeFavour(2));
        assertFalse(faithTrack.getPopeFavour(3));
    }

    @Test
    void getTotalPapalPoints() throws FaithTrackOutOfBoundsException{
        FaithTrack faithTrack=new FaithTrack();
        faithTrack.advance(23);
        faithTrack.activatePopeFavour(3);
        assertEquals(faithTrack.getTotalPapalPoints(),20);

        FaithTrack faithTrack1=new FaithTrack();
        faithTrack1.advance(1);
        faithTrack1.activatePopeFavour(1);
        assertEquals(faithTrack1.getTotalPapalPoints(),2);
    }
}