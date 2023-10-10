package it.polimi.ingsw.common.util;

import com.google.gson.Gson;
import it.polimi.ingsw.common.model.ActionToken;
import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.serializable.ActionTokenStock;
import it.polimi.ingsw.common.serializable.RawLeaderCard;
import it.polimi.ingsw.common.exception.RawDataNotLoadedException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Singleton class used to load card data from files
 */

public class CardRepository
{
    //Used to read JSON files
    private Gson gson;
    //Unique instance
    private static CardRepository _instance;

    private Deck<DevelopmentCard> developmentCardRepo;
    private Deck<LeaderCard> leaderCardRepo;
    private Deck<ActionToken> actionTokenRepo;

    /**
     * Private class constructor, to respect singleton pattern
     */
    private CardRepository()
    {
        gson = new Gson();
        developmentCardRepo = null;
        leaderCardRepo = null;
        actionTokenRepo = null;
    }

    /**
     * Returns the unique instance of this class
     * @return Unique instance
     */
    public static CardRepository getInstance()
    {
        if(_instance == null) _instance = new CardRepository();
        return _instance;
    }

    /** Loads all required JSON data from resource folder */
    public void loadAllData()
    {
        //Get all required resources streams
        InputStream devCardStream = getClass().getClassLoader().getResourceAsStream("dev_cards.json");
        InputStream leaderCardStream = getClass().getClassLoader().getResourceAsStream("lead_cards.json");
        InputStream actionTokenStream = getClass().getClassLoader().getResourceAsStream("action_token_stock.json");

        //Turn all binary streams to corresponding text
        String devCardsJSON = new BufferedReader(new InputStreamReader(devCardStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        String leaderCardsJSON = new BufferedReader(new InputStreamReader(leaderCardStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        String actionTokensJSON = new BufferedReader(new InputStreamReader(actionTokenStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        //Deserialize JSONs
        DevelopmentCard[] rawDevCardData = gson.fromJson(devCardsJSON, DevelopmentCard[].class);
        developmentCardRepo = new Deck<DevelopmentCard>();

        //Add all the cards in the main deck
        for(DevelopmentCard rawCard : rawDevCardData)
            developmentCardRepo.addElement(rawCard);

        //Deserialize JSON
        RawLeaderCard[] rawLeaderCardData = gson.fromJson(leaderCardsJSON, RawLeaderCard[].class);
        leaderCardRepo = new Deck<LeaderCard>();

        //Convert all raw cards to actual cards
        for(RawLeaderCard rawCard : rawLeaderCardData)
            leaderCardRepo.addElement(rawCard.generateLeaderCard());

        ActionTokenStock stock = gson.fromJson(actionTokensJSON, ActionTokenStock.class);
        actionTokenRepo = stock.getActionTokenDeck();
    }

    /**
     * Returns a COPY of the main deck containing all dev cards available for the game
     *
     * @return Copy of the deck with all dev cards
     * @throws RawDataNotLoadedException the raw data not loaded exception
     */
    public Deck<DevelopmentCard> getAllDevelopmentCards() throws RawDataNotLoadedException
    {
        return developmentCardRepo.copy();
    }

    /**
     * Returns a COPY of the main deck containing all leader cards available for the game
     *
     * @return Copy of the deck with all leader cards
     * @throws RawDataNotLoadedException the raw data not loaded exception
     */
    public Deck<LeaderCard> getAllLeaderCards() throws RawDataNotLoadedException
    {
        return leaderCardRepo.copy();
    }

    /**
     * Returns a COPY of the main deck containing all action tokens available for the game
     *
     * @return Copy of the deck with all action tokens
     * @throws RawDataNotLoadedException the raw data not loaded exception
     */
    public Deck<ActionToken> getAllActionTokens() throws RawDataNotLoadedException
    {
        return actionTokenRepo.copy();
    }

    /**
     * Returns the reference to the Development Card object corresponding to the given ID
     *
     * @param ID Card ID
     * @return Reference to the corresponding development card (or null if invalid)
     */
    public DevelopmentCard getDevCardByID(int ID){
        for(DevelopmentCard card: developmentCardRepo){
            if(ID == card.getCardID()){
                return card;
            }
        }
        return null;
    }

    /**
     * Returns the reference to the Leader Card object corresponding to the given ID
     *
     * @param ID Card ID
     * @return Reference to the corresponding leader card (or null if invalid)
     */
    public LeaderCard getLeaderCardByID(int ID){
        for(LeaderCard card: leaderCardRepo){
            if(ID == card.getCardID()){
                return card;
            }
        }
        return null;
    }

}
