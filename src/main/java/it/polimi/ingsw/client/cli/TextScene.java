package it.polimi.ingsw.client.cli;

import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.util.GameConstants;
import it.polimi.ingsw.common.util.PlayerMoveResponse;

/** A CLI scene representing a specific stage of the player's turn */
public abstract class TextScene {

    //Has the player requested to go back to the previous scene?
    //Only applicable in TurnStatus scenes
    private boolean hasRequestedToGoBack = false;

    /** Draws CLI scene - Scene specific
     * @param compressedModel Latest model state as compressedModel
     */
    public void draw(CompressedModel compressedModel){
    }

    /** Registers player actions - Scene specific
     * @param compressedModel Latest model state as compressedModel
     */
    public void askInput(CompressedModel compressedModel){
    }

    /** Replaces a portion of the given string array with another string at the specified coordinates
     * @param stringArray Input string array
     * @param substitute String to be placed on the stringArray representation
     * @param row Row coordinate of the placement
     * @param linepos Placement position on line
     * @return The resulting string array
     */
    protected String[] substituteLineBackground(String[] stringArray,String substitute, int row, int linepos){
        int charsToSubstitute = substitute.length();
        String toModifyLine = stringArray[row];
        String newLine = toModifyLine.substring(0,linepos-1) +substitute + toModifyLine.substring(linepos + charsToSubstitute-1);
        stringArray[row] = newLine;
        return stringArray;
    }

    /** Converts a regular String to a String array (split at any \n occurrence)
     * @param string Input string
     * @return Resulting string array
     */
    protected String[] toArrayFromString(String string){
        return string.split("\n");
    }

    /** Converts a String array to a regular String (adds \n after each line)
     * @param array Input string array
     * @return Resulting string
     */
    protected String toStringFromArray(String[] array){
        StringBuilder builder = new StringBuilder();
        for( String elem : array){
            builder.append(elem);
            builder.append("\n");
        }
        return builder.toString();
    }

    /** Check if the previous move was rejected and, in that case, notify the player
     * @param compressedModel Latest model state as compressedModel
     */
    protected void checkRejectedMove(CompressedModel compressedModel){
        String activePlayerUsername = compressedModel.getPlayerNames().get(compressedModel.getaP());
        boolean wasCurrentPlayerRejected = compressedModel.getLatestActionSource().equals(activePlayerUsername);

        if(compressedModel.getPlayerMoveResponse() == PlayerMoveResponse.REJECTED && wasCurrentPlayerRejected){
            System.out.println(GameConstants.ACTION_REJECTED);
        }
    }

    /** Checks whether or not the Player has requested to go back to the previous scene
     * (only applicable to TurnStatusScene, MarketScene, DevCardPurchaseScene and ProductionScene)
     * WARNING: The flag is set to false after calling this method
     * @return Has the player
     */
    public boolean getGoBackRequest()
    {
        boolean buffer = hasRequestedToGoBack;
        hasRequestedToGoBack = false;
        return buffer;
    }

    /** Registers that the player has requested to go to the previous scene
     * (only applicable to TurnStatusScene, MarketScene, DevCardPurchaseScene and ProductionScene)
     */
    protected void registerGoBackRequest()
    {
        this.hasRequestedToGoBack = true;
    }

}
