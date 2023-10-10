package it.polimi.ingsw.server.model.gameplay;

import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.util.Deck;
import it.polimi.ingsw.common.util.GameConstants;


/**
 * Represents the space where the development cards are placed, divided into 3 decks
 */
public class DevelopmentCardSpace {

    private Deck<DevelopmentCard>[] developmentCardSpaces;

    /**
     * Instantiates a new Development card space.
     */
    public DevelopmentCardSpace(){
        developmentCardSpaces=new Deck[GameConstants.DEV_CARD_NUMBER_OF_SPACES];
        for(int i=0;i<GameConstants.DEV_CARD_NUMBER_OF_SPACES;i++){
            developmentCardSpaces[i]=new Deck<>();
        }
    }

    /**
     * Returns the total number of victory points given by development cards
     *
     * @return total victory points given by development cards
     */
    public int getTotalStackVictoryPoints(){
        //for each space the points of the cards are added, taking one card at a time from the decks
        int sumPV=0;
        for(int i=0; i<GameConstants.DEV_CARD_NUMBER_OF_SPACES;i++){
            for(int j=0;j<developmentCardSpaces[i].getSize();j++){
                sumPV+=developmentCardSpaces[i].getElement(j).getVictoryPoints();
            }
        }
        return sumPV;
    }

    /**
     * Adds the card 'card' to deck number 'index' in the DevelopmentCardSpace
     *
     * @param index index of the deck in which to insert the card
     * @param card  card to add to the required deck
     */
    public void pushCard(int index, DevelopmentCard card){
        //index number 1 correspond to the FIRST SPACE on the personal board, and the cell 0 in the array
        developmentCardSpaces[index-1].addElement(card);
    }

    /**
     * Returns the total number of cards in the DevelopmentCardSpace
     *
     * @return total number of bought development cards
     */
    public int getTotalCardAmount(){
        int totcard=0;
        for(int i=0;i<GameConstants.DEV_CARD_NUMBER_OF_SPACES;i++){
            totcard+=developmentCardSpaces[i].getSize();
        }
        return totcard;
    }

    /**
     * Returns the highest card in the deck number 'index' in the DevelopmentCardSpace
     *
     * @param index index of the deck starting from 1
     * @return top card of the required deck
     */
    public DevelopmentCard getHighestCard(int index){
        return developmentCardSpaces[index-1].peekElement();
    }

    /**
     * Returns the card number 'height'(starting from the lowest) in the deck number 'index' in the DevelopmentCardSpace
     *
     * @param index  index of the deck
     * @param height index of the card's position in the deck
     * @return card in the required position of the required deck
     * @deprecated use getDevelopmentCardDeck(int)
     */
    public DevelopmentCard getNthCard(int index, int height){
        //height starts from value '1', so the first card will be in the height-1='0' position of the deck
        return developmentCardSpaces[index-1].getElement(height-1);
    }

    /**
     * Returns the n-th deck
     *
     * @param index 1,2,3 (decks from left to right)
     * @return Deck of Development Cards
     */
    public Deck<DevelopmentCard> getDevelopmentCardDeck(int index) {
        return developmentCardSpaces[index-1];
    }
}
