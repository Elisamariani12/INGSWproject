package it.polimi.ingsw.server.model.gameplay;

import it.polimi.ingsw.common.util.GameConstants;
import it.polimi.ingsw.common.util.Resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** Represents the game's market device */
public class MarketBoard
{
    //Marble device internal representation
    private Resource[][] marketState;
    private Resource externalMarble;

    /**
     * Creates an instance of the public Market where players get resources
     */
    public MarketBoard()
    {
        int currentMarbleIndex;

        Random rng = new Random();

        //List of marbles (represented by Resources) that have yet to be allocated on the board
        List<Resource> availableMarbles = new ArrayList<>(Arrays.asList(GameConstants.MARKET_MARBLE_RESERVE));
        marketState = new Resource[GameConstants.MARKET_ROWS_COUNT][GameConstants.MARKET_COLS_COUNT];

        //Place 12 marbles on the board
        for(int j = 0; j < GameConstants.MARKET_ROWS_COUNT; j++)
            for(int i = 0; i < GameConstants.MARKET_COLS_COUNT; i++)
            {
                currentMarbleIndex = rng.nextInt(availableMarbles.size());
                marketState[j][i] = availableMarbles.get(currentMarbleIndex);
                availableMarbles.remove(currentMarbleIndex);
            }

        //Place the last marble on the external slot
        externalMarble = availableMarbles.get(0);
    }

    /** Inserts the external marble into the device
     * @param column index of the column (from left to right) where the marble is pushed
     */
    public void insertVertical(int column)
    {
        Resource resourceBuffer = externalMarble;
        externalMarble = marketState[0][column];

        for(int j = 1; j < GameConstants.MARKET_ROWS_COUNT; j++)
            marketState[j - 1][column] = marketState[j][column];

        marketState[GameConstants.MARKET_ROWS_COUNT - 1][column] = resourceBuffer;
    }

    /** Inserts the external marble into the device
     * @param row index of the row (from top to bottom) where the marble is pushed
     */
    public void insertHorizontal(int row)
    {
        Resource resourceBuffer = externalMarble;
        externalMarble = marketState[row][0];

        for(int i = 1; i < GameConstants.MARKET_COLS_COUNT; i++)
            marketState[row][i - 1] = marketState[row][i];

        marketState[row][GameConstants.MARKET_COLS_COUNT - 1] = resourceBuffer;
    }

    /** Returns a list of the content a row
     * @param row index of the row (from top to bottom)
     * @return list of resources contained in a row
     */
    public List<Resource> getRow(int row)
    {
        List<Resource> buffer = new ArrayList<Resource>();
        for(int i = 0; i < GameConstants.MARKET_COLS_COUNT; i++)
            buffer.add(marketState[row][i]);

        return buffer;
    }

    /** Returns a list of the content a column
     * @param column index of the column (from left to right)
     * @return list of resources contained in a column
     */
    public List<Resource> getColumn(int column)
    {
        List<Resource> buffer = new ArrayList<Resource>();
        for(int j = 0; j < GameConstants.MARKET_ROWS_COUNT; j++)
            buffer.add(marketState[j][column]);

        return buffer;
    }

    /** Returns a list of the content a column
     * @return Resource represented by the external marble
     */
    public Resource getExternalResource()
    {
        return externalMarble;
    }

    /** Returns a representation of the market state to be used in client-server communications
     * @return A 2-dimensional array representing the market state
     */
    public Resource[][] getSerializableState()
    {
        return marketState.clone();
    }
}
