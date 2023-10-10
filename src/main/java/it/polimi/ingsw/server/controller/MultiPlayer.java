package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.common.util.GameConstants;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.server.exceptions.FaithTrackOutOfBoundsException;
import it.polimi.ingsw.server.exceptions.ResourceNotInsertableException;
import it.polimi.ingsw.server.model.gamemanager.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The type Multi player.
 */
public class MultiPlayer extends GameMode{

    /**
     * Instantiates a new Multi player calling also its superclass constructor.
     */
    public MultiPlayer() {
        super();
    }

    /**
     * First phase of the game: call all the functions to initialize the personal boards of the players and
     */
    @Override
    public void setupGame() {
        //distribute the 4 leader cards to the players, prepare the devcardgrid, and the personal boards(personal boards without the initial resources)
        getModel().getGameSession().shufflePlayersOrder();
        prepareDevCardGrid();
        dealLeaderCard();
        preparePersonalBoards();

    }

    /**
     * Distribute initial resources (faith points+resources) to the players
     * @param player Index of the player
     * @param initialResources triplet of player, resource1 and resource2
     */
    @Override
    protected void distributeInitialResources(int player, List<Resource> initialResources) {
        Player p=getModel().getGameSession().getPlayerByIndex(player);

        //distribute initial faith points
        tryAdvance(p,GameConstants.FAITH_TRACK_INITIAL_POINTS[player]);

        //distribute initial resources
        try{insertResourcesInDeposits(player,initialResources);}
        //this exception should not be thrown because the player can only choose max 2 resources
        catch(ResourceNotInsertableException e){e.printStackTrace();}
    }



    /**
     * Try to advance of 'amount' position in the faith track
     * @param player the player whose faith track increases
     * @param amount increase in the player's faith track
     */
    @Override
    public void tryAdvance(Player player, int amount) {
        int position=player.getBoard().getFaithTrack().getPosition();
        int indexplayer= getModel().getGameSession().getPlayerList().indexOf(player);

        //check if a vatican report is needed
        for(int i=0; i<GameConstants.FAITH_TRACK_POSITION_POPE_SPACE.length;i++){
            if((position<GameConstants.FAITH_TRACK_POSITION_POPE_SPACE[i])&&(position+amount>=GameConstants.FAITH_TRACK_POSITION_POPE_SPACE[i])){
                vaticanReport(indexplayer,i+1);
            }
        }

        //check if the player reaches the slot '24', in that case set 'isLastRound'
        try{
            player.getBoard().getFaithTrack().advance(amount);
            if(player.getBoard().getFaithTrack().getPosition()==24)getModel().getGameSession().setIsLastRound();
        }catch(FaithTrackOutOfBoundsException e){
            getModel().getGameSession().setIsLastRound();
            int diff= player.getBoard().getFaithTrack().getPosition()+amount-GameConstants.FAITH_TRACK_LAST_SPACE_INDEX;
            try{player.getBoard().getFaithTrack().advance(amount-diff);}catch(FaithTrackOutOfBoundsException ignored){}
        }

    }

    @Override
    protected void selectMarketRow(int row, HashMap<Resource, Integer> whiteMarbleChoice, List<Resource> resourcesToDiscard) throws ResourceNotInsertableException {
        super.selectMarketRow(row, whiteMarbleChoice, resourcesToDiscard);

        for(Resource r:resourcesToDiscard){
            //for every resource discarded, the other players advance
            for(Player player:getModel().getGameSession().getPlayerList()){
                if(!player.equals(getModel().getGameSession().getActivePlayer())){
                    tryAdvance(player,1);
                }
            }
        }
    }

    @Override
    protected void selectMarketColumn(int column, HashMap<Resource, Integer> whiteMarbleChoice, List<Resource> resourcesToDiscard) throws ResourceNotInsertableException {
        super.selectMarketColumn(column, whiteMarbleChoice, resourcesToDiscard);

        for(Resource r:resourcesToDiscard){
            //for every resource discarded, the other players advance
            for(Player player:getModel().getGameSession().getPlayerList()){
                if(!player.equals(getModel().getGameSession().getActivePlayer())){
                    tryAdvance(player,1);
                }
            }
        }
    }

    /**
     * A player has reached the pope space number 'index' so he activated the vatican report, if the vatican report number 'index' has already beeen activated, nothing changes
     * @param index index of the vatican report that has been activated
     */
    public void vaticanReport(int playerActivatingVR,int index){
        //Each vatican report can be activated by one player and only one time: check if it has already been activated
        boolean check=false;
        for(Player player:getModel().getGameSession().getPlayerList()){
            if(player.getBoard().getFaithTrack().getPopeFavour(index))check=true;
        }

        //if no one has already activated the vatican report, activate it: set true the pope favor of the active player
        // and check the position of the other players
        if(!check) {
            //the indexes are 1,2,3 based on which vatican report has been activated
            for (Player p : getModel().getGameSession().getPlayerList()) {
                if (p.equals(getModel().getGameSession().getPlayerByIndex(playerActivatingVR))) {
                    p.getBoard().getFaithTrack().activatePopeFavour(index);
                } else {
                    if (p.getBoard().getFaithTrack().getPosition() >= (GameConstants.FAITH_TRACK_POSITION_POPE_SPACE[index-1] - GameConstants.FAITH_TRACK_LENGTH_VATICAN_REPORT_SECTION[index-1]+1)) {
                        p.getBoard().getFaithTrack().activatePopeFavour(index);
                    }
                }
            }
        }
    }


    @Override
    public void calculateScores() {

        List<Integer> winnerlist=new ArrayList<>();

        //find players / players with the highest total points
        int maxPoints=0;
        for(int i=0;i<getModel().getGameSession().getPlayerList().size();i++){
            if(maxPoints<countAllVictoryPointsForPlayer(i)){
                winnerlist.clear();winnerlist.add(i);
                maxPoints=countAllVictoryPointsForPlayer(i);
            }
            else if(maxPoints==countAllVictoryPointsForPlayer(i)){ winnerlist.add(i);}

        }


        //if there are 2 or more players with the same number of points, look at the total number of resources they have
        if(winnerlist.size()>1){
            int maxResources=0;
            int tempNumResources; //support variable
            List<Integer> winnerlist2=new ArrayList<>(); //create a temporary winner list to save the players with the highest number of resources

            for (Integer integer : winnerlist) {
                tempNumResources = 0;
                for (Resource r : Resource.values()) {
                    tempNumResources += getModel().getGameSession().getPlayerByIndex(integer).getBoard().getTotalStoredAmount(r);
                }

                if (tempNumResources > maxResources) {
                    maxResources = tempNumResources;
                    winnerlist2.clear();
                    winnerlist2.add(integer);
                } else if (tempNumResources == maxResources) {
                    winnerlist2.add(integer);
                }
            }
            winnerlist=winnerlist2;
        }

        //in case of draw (even with the resources), return the first player in the order
        getModel().getGameSession().getOrderedScoreBoard().put(getModel().getGameSession().getPlayerByIndex(winnerlist.get(0)),maxPoints);


        for(int i=0 ; i<getModel().getGameSession().getPlayerCount(); i++){
            //skip winner score, already inserted in scoreboard list
            if(i != winnerlist.get(0)){
                getModel().getGameSession().getOrderedScoreBoard().put(getModel().getGameSession().getPlayerByIndex(i),countAllVictoryPointsForPlayer(i));
            }
        }
    }
}
