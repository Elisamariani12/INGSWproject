package it.polimi.ingsw.server.model.gameplay;

import it.polimi.ingsw.common.util.GameConstants;
import it.polimi.ingsw.server.exceptions.FaithTrackOutOfBoundsException;

import java.util.ArrayList;

/** Represents the opponent in the SinglePlayer game mode */
public class LorenzoDeMedici
{
    //Position on the faith track
    private int faithTrackPosition;
    private ArrayList<Boolean> hasPopeFavour;

    /**
     * Creates an instance of Lorenzo De Medici (opponent in singleplayer)
     */
    public LorenzoDeMedici()
    {
        hasPopeFavour = new ArrayList<>();
        faithTrackPosition = 0;
        hasPopeFavour.add(0, false);
        hasPopeFavour.add(1, false);
        hasPopeFavour.add(2, false);
    }

    /**
     * Returns the opponent's position on the faith track
     * @return Position on faith track
     */
    public int getFaithTrackPosition()
    {
        return faithTrackPosition;
    }

    /**
     * Makes the opponent advance on the faith track by the specified amount
     * @param amount Number of forward steps on the faith track
     * @exception FaithTrackOutOfBoundsException If the opponent's position exceeds the limit
     */
    public void advance(int amount) throws FaithTrackOutOfBoundsException
    {
        //The position on the faith track should never exceed the last space
        if(faithTrackPosition + amount > GameConstants.FAITH_TRACK_LAST_SPACE_INDEX) {
            throw new FaithTrackOutOfBoundsException();
        }
        faithTrackPosition += amount;
    }

    /**
     * Returns whether or not the opponent has the pope favour at the specified pos (1, 2 or 3)
     * @param pos index of the Vatican Report space (1, 2 or 3);
     * @return Boolean corresponding to the required pope favour
     */
    public boolean getPopeFavour(int pos){
        return hasPopeFavour.get(pos-1);
    }

    /**
     * Activates the opponent's Pope Favour at the specified space
     * @param pos index of the Vatican Report space (1, 2 or 3);
     */
    public void activatePopeFavour(int pos){
        hasPopeFavour.set(pos-1, true);
    }

}
