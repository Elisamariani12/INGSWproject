package it.polimi.ingsw.server.model.gameplay;

import it.polimi.ingsw.common.util.GameConstants;
import it.polimi.ingsw.server.exceptions.FaithTrackOutOfBoundsException;

/**
 * Represents the 'Faith Track' on which faith points are counted and on which the pope's tiles are activated and deactivated
 */
public class FaithTrack {
    private int position;
    private boolean[] hasPopeFavour;

    /**
     * Instantiates a new Faith track and set it to the initial position: 0.
     */
    public FaithTrack(){
        this.position=0;
        //pope's tiles start at 'false' by default
        this.hasPopeFavour= new boolean[GameConstants.FAITH_TRACK_NUMBER_OF_POPE_TILES];
    }

    /**
     * Return the current position of the player in the faith track
     *
     * @return number corresponding to the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Increase the number of faith points on the player's faith track
     *
     * @param amount faith points increase
     * @throws FaithTrackOutOfBoundsException the faith track out of bounds exception
     */
    public void advance(int amount) throws FaithTrackOutOfBoundsException {

        //the position on the faith track should never exceed the last space
        if(position+amount > GameConstants.FAITH_TRACK_LAST_SPACE_INDEX)
            throw new FaithTrackOutOfBoundsException();
        else position+=amount;
    }

    /**
     * Returns if the player has the Pope's favor tile number 'index' active or not
     *
     * @param index number of the pope's favor tile whose status is questioned
     * @return status is active(true) or not(false)
     */
    public boolean getPopeFavour(int index){
        return hasPopeFavour[index-1];
    }

    /**
     * Activate the Pope's Favor tiles number 'index' of the player
     *
     * @param index index of the Pope's favor tile to activate
     */
    public void activatePopeFavour(int index){
        hasPopeFavour[index-1]=true;
    }

    /**
     * Returns the total points won with faith points and Pope's favour tiles
     *
     * @return total of papal points
     */
    public int getTotalPapalPoints(){
        //sum the points given by the pope's tiles
        int sum=0;
        for(int i=0;i<GameConstants.FAITH_TRACK_NUMBER_OF_POPE_TILES; i++){
            if(hasPopeFavour[i])sum+=GameConstants.FAIT_TRACK_POPE_TILES_PV[i];
        }
        //the total amount of points, without the points given by the pope's tiles can be computed as int(position/3)
        if(position>2)return sum+GameConstants.FAITH_TRACK_POSITION_PV[position/3-1];
        else return sum;
    }
}
