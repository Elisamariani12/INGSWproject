package it.polimi.ingsw.client.util;

import it.polimi.ingsw.client.PlayerWorkSpace;
import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.util.GameConstants;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/** Used to check whether the player won or lost and manage victory points */
public class WinningChecker
{
    private CompressedModel compressedModel;
    private boolean isSinglePlayer;
    private boolean hasWon;
    private LinkedHashMap<String, Integer> leaderBoard;
    private OutcomeReason outcomeReason;

    /** Creates a new WinningChecker
     *
     * @param compressedModel the updated compressed model
     */
    public WinningChecker(CompressedModel compressedModel)
    {
        this.compressedModel = compressedModel;
        this.isSinglePlayer = compressedModel.getPlayerNames().size() == 1;
        verifyOutcome();
    }

    /** Returns whether or not the player has won the match
     * @return Has the player won the match?
     */
    public boolean hasWon()
    {
        return hasWon;
    }

    /** Returns the reason why the player won / lost the game
     * @return Explanation for the outcome
     */
    public OutcomeReason getPlayerOutcomeReason()
    {
        return outcomeReason;
    }

    /** Returns the complete leaderboard
     * @return Complete leader board
     */
    public LinkedHashMap<String, Integer> getLeaderBoard()
    {
        return leaderBoard;
    }

    /** Returns a complete string with the outcome of the game
     * @return the end of match response
     * */
    public String getOutcomeString()
    {
        StringBuilder strBuffer = new StringBuilder();

        strBuffer.append("<html><center><font face=\"Times New Roman\" size=\"20\">");

        final String SINGLEPLAYER_WON = "Congratulations! You have won against <i>Lorenzo il Magnifico</i>!";
        final String SINGLEPLAYER_LOST = "You failed to show the Pope your loyalty, <i>Lorenzo il Magnifico</i> remains undefeated!";
        final String MULTIPLAYER_WON = "Congratulations! You have won against all your opponents.";
        final String MULTIPLAYER_LOST = "You failed to show the Pope your loyalty, your opponents have triumphed.";

        if(isSinglePlayer) strBuffer.append(hasWon ? SINGLEPLAYER_WON : SINGLEPLAYER_LOST);
        else strBuffer.append(hasWon ? MULTIPLAYER_WON : MULTIPLAYER_LOST);

        strBuffer.append("\n</font></center><p style=\"font-size:15px;\"><br /><br />");

        if(isSinglePlayer)
        {
            String yourUsername = PlayerWorkSpace.getInstance().getPlayerUsername();
            strBuffer.append("Your final score was " + leaderBoard.get(yourUsername) + " VP");
        }
        else
        {

            strBuffer.append("Score Board:<br /><br />");

            int position = 1;

            for(String username : leaderBoard.keySet())
            {
                strBuffer.append(position + ". " + username + " with " + leaderBoard.get(username) + " VP<br />");
                position++;
            }
        }

        strBuffer.append("</p></html>");

        return strBuffer.toString();
    }

    /** Verifies the outcome of the game session */
    private void verifyOutcome()
    {
        //reorder scoreBoard
        reorderScoreBoard();

        if(isSinglePlayer)
        {
            boolean devCardShortage = isDevCardColumnEmpty();
            boolean lorenzoFaith = hasLorenzoReachedTheEnd();
            boolean playerFaith = haveYouReachedTheEnd();
            boolean seventhDevCard = hasAnyonePurchasedSeventhCard();

            if(devCardShortage)
            {
                outcomeReason = OutcomeReason.SINGLEPLAYER_DEV_CARDS_SHORTAGE;
                hasWon = false;
            }
            else if(lorenzoFaith)
            {
                outcomeReason = OutcomeReason.SINGLEPLAYER_FAITH_LORENZO;
                hasWon = false;
            }
            else if(playerFaith)
            {
                outcomeReason = OutcomeReason.SINGLEPLAYER_FAITH_PLAYER;
                hasWon = true;
            }
            else if(seventhDevCard)
            {
                outcomeReason = OutcomeReason.SINGLEPLAYER_DEV_CARD_SEVEN;
                hasWon = true;
            }
        }
        else
        {
            this.outcomeReason = OutcomeReason.MULTIPLAYER_END;

            //Find the winner
            int maxVP = 0;
            String winningUsername = null;
            for(String username : leaderBoard.keySet())
            {
                if (leaderBoard.get(username) > maxVP)
                {
                    maxVP = leaderBoard.get(username);
                    winningUsername = username;
                }
            }
            if(winningUsername!=null) {
                this.hasWon = (winningUsername.equals(PlayerWorkSpace.getInstance().getPlayerUsername()));
            }
        }
    }

    /**
     * check if a devCardGrid column is empty
     *
     * @return is a devCardGrid column empty?
     */
    private boolean isDevCardColumnEmpty()
    {
        int[][] devCardGrid = compressedModel.getDevCardGridState();

        boolean isColEmpty = false;

        for(int i = 0; i < GameConstants.DEV_CARD_GRID_COLS_COUNT; i++)
        {
            if(devCardGrid[2][i] <= 0 && devCardGrid[1][i] <= 0 && devCardGrid[0][i] <= 0)
            {
                isColEmpty = true;
                break;
            }
        }

        return isColEmpty;
    }

    /**
     * Check if Lorenzo has reached the end of the faith track
     * @return has Lorenzo reached the end?
     */
    private boolean hasLorenzoReachedTheEnd()
    {
        int lorenzoPosition = compressedModel.getPositionsOfFaithTrack().get(1);
        int yourPosition = compressedModel.getPositionsOfFaithTrack().get(0);

        return isSinglePlayer &&
                lorenzoPosition == GameConstants.FAITH_TRACK_LAST_SPACE_INDEX &&
                yourPosition < lorenzoPosition;
    }

    /**
     * Check if the player has reached the end of the faith track
     * @return has the player reached the end?
     */
    private boolean haveYouReachedTheEnd()
    {
        return compressedModel.getPositionsOfFaithTrack().get(0) == GameConstants.FAITH_TRACK_LAST_SPACE_INDEX;
    }

    /**
     * Count the number of devCards owned by the player
     * @param devCardSpace the devCardSpace
     * @return the number of devCards owned
     */
    private int countCardsInDevCardSpace(List<Stack<Integer>> devCardSpace)
    {
        int count = 0;

        for(Stack<Integer> deck : devCardSpace)
        {
            for(Integer cardID : deck)
                if(cardID > 0) count++;
        }

        return count;
    }

    /**
     * Check if one of the player has purchased his seventh devCard
     * @return has someone purchased his seventh card?
     */
    private boolean hasAnyonePurchasedSeventhCard()
    {
        return compressedModel.getDevCardSpace().stream().map(this::countCardsInDevCardSpace).anyMatch(count -> count >= 7);
    }

    /**
     * Reorders score board coming from Server
     */
    private void reorderScoreBoard(){
        LinkedHashMap<String, Integer> leaderBoardBuffer = this.compressedModel.getEndOfSessionLeaderBoard();
        List<Integer> orderedScores = leaderBoardBuffer.values().stream().sorted().collect(Collectors.toList());
        this.leaderBoard = new LinkedHashMap<>();

        for(int i = orderedScores.size() - 1; i >= 0; i--)
        {
            int points = orderedScores.get(i);
            for(String name : leaderBoardBuffer.keySet()){
                if(leaderBoardBuffer.get(name) == points)
                {
                    leaderBoard.put(name, points);
                    break;
                }
            }
        }
    }
}
