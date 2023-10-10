package it.polimi.ingsw.common.serializable;

import it.polimi.ingsw.common.util.Move;
import it.polimi.ingsw.common.util.Pair;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.StorageType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


/*
The format of the PlayerEvent changes based on the first attribute: playerMove. The attribute can contain the following values:

- INITIAL_CHOICES
inputResources : The field will enumerate the choice of initial resources. StorageType is ignored
leaderCards : The field contains the choice of leader cards (two in total) to be stored as CHOSEN in the player’s LeaderCardSpace

- LEADER_ACTION
leaderCards : The field contains which leader cards is to be activated
hasDiscarded : This field indicates whether or not the leader action involves discarding
discardedList : This field contains card IDs of discarded leader cards

- MARKET_MOVE
NOTE: only one between row and col should be used. Put either one or the other as -1 to mark it as unused.
row : This field is used to select the row (0 to 2 from top to bottom) to be selected at the market
col : This field is used to select the col (0 to 3 from left to right) to be selected at the market
whiteMarbleSubstitution : This field contains chooses for each white marble on the selected line (Resource type and amount) as a Map
hasDiscarded : This field indicates whether or not there are specified resources to discard
outputResources : This field contains resources to discard (if any, can be left null if not necessary)

- DEV_CARD_REQUEST
row, col : indices of the card slot on the DevCardGrid (0 to 2 top to bottom, 0 to 3 left to right)
index : This field represents the index of the Dev Card Space slot (1, 2 or 3) where to place the selected card.

- PRODUCTION_POWER_REQUEST
devCard : This field contains the ID of the development card which contains the Production Power to activate (assign -1 if Generic Production)
leaderCards : This field should contain a single leader card ID, the one of the card which contains the Production Power to activate (assign null or empty if none)
inputResources : This field contains pairs of resources and inventory types as choices of GENERICs inside production power
outputResources : This field should only contain one resource (the GENERIC output of the production power, if one is required)

- PRODUCTION_POWER_END
Ends the production phase, no parameters needed

- SESSION_CREATION
playerUserName : This field contains the username of the player creating the game session
maxNumberOfPlayers : This field contains the player count for the new session
*/

/**
 * Message to be sent from client to server
 */
public class PlayerEvent {
    /**
     * The Player move (enum).
     */
    private Move playerMove;
    /**
     * The Input resources.
     */
    //to use in case of generic production
    private List<Pair<Resource, StorageType>> inputResources;
    /**
     * The Leader cards.
     */
    private List<Integer> leaderCards;
    /**
     * The Development card.
     */
    //set to -1 in case of generic production
    private int devCard;
    /**
     * Used whenever an int is required, for example the slot where we have to insert the development card
     */
    private int index;
    /**
     * The Output resources.
     */
    private List<Resource> outputResources;
    /**
     * The Player user name.
     */
    private String playerUserName;
    /**
     * The resources to substitute White marble, ONLY if there are 2 active leadercards with this power
     */
    private List<Pair<Resource, Integer>> whiteMarbleSubstitution;
    /**
     * Field to be filled in whenever you need to specify a line, for example when purchasing a development card
     */
    private int row;
    /**
     * Field to be filled in whenever you need to specify a line, for example when purchasing a development card
     */
    private int col;
    /**
     * Does the leader action / market action require discarding?
     */
    private boolean hasDiscarded;
    /**
     * List of discarded cards in discard leader action
     */
    private List<Integer> discardedList;

    /**
     * Gets player move.
     *
     * @return the player move
     */
    public Move getPlayerMove() {
        return playerMove;
    }

    /**
     * Sets player move.
     *
     * @param playerMove the player move
     */
    public void setPlayerMove(Move playerMove) {
        this.playerMove = playerMove;
    }

    /**
     * Gets input resources and the storage from which to take them
     *
     * @return the input resources+storage
     */
    public List<Pair<Resource, StorageType>> getInputResources() {
        return inputResources;
    }

    /**
     * Sets input resources and the storage from which to take them
     *
     * @param inputResources the input resources+storage
     */
    public void setInputResources(List<Pair<Resource, StorageType>> inputResources) {
        this.inputResources = inputResources;
    }

    /**
     * Gets leader cards ID
     *
     * @return the leader cards ID
     */
    public List<Integer> getLeaderCards() {
        return leaderCards;
    }

    /**
     * Sets leader cards ID
     *
     * @param leaderCards the leader cards ID
     */
    public void setLeaderCards(List<Integer> leaderCards) {
        this.leaderCards = leaderCards;
    }

    /**
     * Gets dev card ID
     *
     * @return the dev card ID
     */
    public int getDevCard() {
        return devCard;
    }

    /**
     * Sets dev card ID
     *
     * @param devCard the dev card ID
     */
    public void setDevCard(int devCard) {
        this.devCard = devCard;
    }

    /**
     * Gets the index, used whenever an 'int' is required
     *
     * @return the int
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets index, used whenever an 'int' is required
     *
     * @param index the index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Gets output resources, for (generic+leadercard) productions
     *
     * @return the output resources
     */
    public List<Resource> getOutputResources() {
        return outputResources;
    }

    /**
     * Sets output resources, for (generic+leadercard) productions
     *
     * @param outputResources the output resources
     */
    public void setOutputResources(List<Resource> outputResources) {
        this.outputResources = outputResources;
    }

    /**
     * Gets player user name.
     *
     * @return the player user name
     */
    public String getPlayerUserName() {
        return playerUserName;
    }

    /**
     * Sets player user name.
     *
     * @param playerUserName the player user name
     */
    public void setPlayerUserName(String playerUserName) {
        this.playerUserName = playerUserName;
    }


    /**
     * Gets the resource to substitute the white marble, only if there are 2 leadercards active with that power
     *
     * @return the resource and the quantity
     */
    public HashMap<Resource, Integer> getWhiteMarbleSubstitution() {
        if(whiteMarbleSubstitution!=null){
            HashMap<Resource,Integer> convertToHashMap=new HashMap<>();
            for(Pair<Resource,Integer> pair:whiteMarbleSubstitution){convertToHashMap.put(pair.getFirst(),pair.getSecond());}
            return convertToHashMap;
        }
        else{
            return null;
        }
    }

    /**
     * Sets resource to substitute the white marble,only if there are 2 leadercards active with that power
     *
     * @param whiteMarbleSubstitution the resource and the quantity
     */
    public void setWhiteMarbleSubstitution(HashMap<Resource, Integer> whiteMarbleSubstitution) {
        if(whiteMarbleSubstitution!=null){
            List<Pair<Resource,Integer>> list=new ArrayList<>();
            for(Resource r:whiteMarbleSubstitution.keySet()){list.add(new Pair<>(r,whiteMarbleSubstitution.get(r)));}
            this.whiteMarbleSubstitution = list;
        }
        else this.whiteMarbleSubstitution=null;
    }

    /**
     * Gets row, used for market and devcardgrid
     *
     * @return the row
     */
    public int getRow() {
        return row;
    }

    /**
     * Sets row, used for market and devcardgrid
     *
     * @param row the row
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Gets col, used for market and devcardgrid
     *
     * @return the column
     */
    public int getCol() {
        return col;
    }

    /**
     * Sets col,used for market and devcardgrid
     *
     * @param col the column
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Sets whether or not the player's action involves discarding (leader action / market board)
     * @param value Has the player discarded anything?
     */
    public void setHasDiscarded(boolean value)
    {
        this.hasDiscarded = value;
    }

    /**
     * Returns whether or not the player's action involves discarding (leader action / market board)
     * @return Has the player discarded anything?
     */
    public boolean getHasDiscarded()
    {
        return this.hasDiscarded;
    }

    /**
     * Returns list of discarded cards (if any)
     * @return List of discarded cards (if any)
     */
    public List<Integer> getDiscardedList()
    {
        return discardedList;
    }

    /**
     * Sets discarded cards in leader action
     * @param discardedList List of discarded cards
     */
    public void setDiscardedList(List<Integer> discardedList) {this.discardedList = discardedList;}

    /**
     * Returns a copy of the PlayerEvent object
     * @return Copy of this
     */
    public PlayerEvent copy()
    {
        PlayerEvent buffer = new PlayerEvent();
        buffer.setPlayerMove(this.playerMove);
        buffer.inputResources =this.inputResources != null ? this.inputResources.stream().map(Pair::copy).collect(Collectors.toList()) : new ArrayList<>();
        buffer.leaderCards = new ArrayList<>(this.leaderCards != null ? this.leaderCards : new ArrayList<>());
        buffer.devCard = this.devCard;
        buffer.index = this.index;
        buffer.outputResources = new ArrayList<>( this.outputResources != null ? this.outputResources : new ArrayList<>());
        buffer.playerUserName = this.playerUserName;
        buffer.whiteMarbleSubstitution = this.whiteMarbleSubstitution;
        buffer.row = this.row;
        buffer.col = this.col;
        buffer.hasDiscarded = this.hasDiscarded;
        buffer.discardedList = new ArrayList<>( this.discardedList != null ? this.discardedList : new ArrayList<>());
        return buffer;
    }
}
