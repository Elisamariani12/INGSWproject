package it.polimi.ingsw.server.model.gameplay;

import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.GameConstants;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;


public class MarketTest
{
    MarketBoard board;

    @Test
    public void checkCorrectPreparation()
    {
        Resource[] marbleSet = GameConstants.MARKET_MARBLE_RESERVE;

        board = new MarketBoard();

        HashMap<Resource, Integer> stockCounter, instanceCounter;
        stockCounter = new HashMap<>();
        instanceCounter = new HashMap<>();

        //Count stock
        for(Resource r : marbleSet)
        {
            if(stockCounter.containsKey(r))
                stockCounter.replace(r, stockCounter.get(r) + 1);
            else
                stockCounter.put(r, 1);
        }

        //Count marbles on the board
        instanceCounter.put(board.getExternalResource(), 1);
        for(int i = 0; i < GameConstants.MARKET_ROWS_COUNT; i++)
            for(Resource r : board.getRow(i))
            {
                if(instanceCounter.containsKey(r))
                    instanceCounter.replace(r, instanceCounter.get(r) + 1);
                else
                    instanceCounter.put(r, 1);
            }

        //Verify that all marbles are the same amount in each counter
        for(Resource r : stockCounter.keySet())
        {
            assertEquals(instanceCounter.get(r), stockCounter.get(r));
        }
    }

    @Test
    public void checkVerticalInsertion()
    {
        List<Resource> column, newColumn;
        Resource externalBuffer;

        board = new MarketBoard();

        for(int i = 0; i < GameConstants.MARKET_COLS_COUNT; i++)
        {
            column = board.getColumn(i);
            externalBuffer = board.getExternalResource();

            board.insertVertical(i);
            newColumn = board.getColumn(i);

            assertEquals(column.get(0), board.getExternalResource());
            assertEquals(column.get(1), newColumn.get(0));
            assertEquals(column.get(2), newColumn.get(1));
            assertEquals(externalBuffer, newColumn.get(2));
        }
    }

    @Test
    public void checkHorizontalInsertion()
    {
        List<Resource> row, newRow;
        Resource externalBuffer;

        board = new MarketBoard();

        for(int i = 0; i < GameConstants.MARKET_ROWS_COUNT; i++)
        {
            row = board.getRow(i);
            externalBuffer = board.getExternalResource();

            board.insertHorizontal(i);
            newRow = board.getRow(i);

            assertEquals(row.get(0), board.getExternalResource());
            assertEquals(row.get(1), newRow.get(0));
            assertEquals(row.get(2), newRow.get(1));
            assertEquals(row.get(3), newRow.get(2));
            assertEquals(externalBuffer, newRow.get(3));
        }
    }
}
