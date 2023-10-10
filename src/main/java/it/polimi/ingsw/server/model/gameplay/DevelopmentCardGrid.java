package it.polimi.ingsw.server.model.gameplay;

import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.util.Deck;
import it.polimi.ingsw.common.util.GameConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the space where available Development Cards are placed
 */
public class DevelopmentCardGrid
{
    private Deck<DevelopmentCard>[][] availableCardGrid;

    /**
     * Instantiates a new Development card grid.
     */
    public DevelopmentCardGrid()
    {
        availableCardGrid = new Deck[GameConstants.DEV_CARD_GRID_ROWS_COUNT][GameConstants.DEV_CARD_GRID_COLS_COUNT];
    }

    /**
     * Places a ready-to-be-used deck on the chosen slot (used by the game master)
     *
     * @param deck The ready-to-be-used deck
     * @param row  index of the row
     * @param col  index of the column
     */
    public void placeCardDeck(Deck<DevelopmentCard> deck, int row, int col)
    {
        availableCardGrid[row][col] = deck;
    }

    /**
     * Picks the chosen card from the grid
     *
     * @param row index of the row
     * @param col index of the column
     * @return The card you picked
     */
    public DevelopmentCard pickCard(int row, int col)
    {
        return availableCardGrid[row][col].removeElement();
    }

    /**
     * Returns a list of all available cards on the grid
     *
     * @return List of available cards
     */
    public List<DevelopmentCard> getAvailableCards()
    {
        List<DevelopmentCard> buffer = new ArrayList<>();
        for(int j = 0; j < GameConstants.DEV_CARD_GRID_ROWS_COUNT; j++)
            for(int i = 0; i < GameConstants.DEV_CARD_GRID_COLS_COUNT; i++)
            {
                if(availableCardGrid[j][i].getSize() > 0) buffer.add(availableCardGrid[j][i].peekElement());
            }

        return buffer;
    }

    /**
     * Returns a list of all purchasable cards based on specified levels
     *
     * @param lv1 Can the player buy level 1 cards?
     * @param lv2 Can the player buy level 2 cards?
     * @param lv3 Can the player buy level 3 cards?
     * @return List of purchasable cards
     */
    public List<DevelopmentCard> getPurchasableCardsByLevel(boolean lv1, boolean lv2, boolean lv3)
    {
          return getAvailableCards().stream()
                .filter((x)->(x.getCardLevel() == 1 && lv1)||
                             (x.getCardLevel() == 2 && lv2)||
                             (x.getCardLevel() == 3 && lv3))
                .collect(Collectors.toList());
    }

    /**
     * Returns whether or not the specified deck is empty
     *
     * @param row index of the row
     * @param col index of the column
     * @return Answer to the question
     */
    public boolean isDeckEmpty(int row, int col)
    {
        return availableCardGrid[row][col].getSize() == 0;
    }

    /**
     * Gets the chosen card deck from the grid
     *
     * @param row index of the row
     * @param col index of the column
     * @return The selected card deck
     */
    public Deck<DevelopmentCard> getDeck(int row, int col)
    {
        return availableCardGrid[row][col];
    }
}
