package it.polimi.ingsw.common.util;

/**
 * Class that contains all the gameConstants used
 */
public class GameConstants
{
    /* --------- GENERAL -------- */
    public static final Resource[] STORAGE_COMPATIBLE_RESOURCES = {Resource.COIN, Resource.SERVANT, Resource.STONE, Resource.SHIELD};
    public static final char[] PLAYER_MARKERS={'@','$','§','#'};
    public static final int MAX_USERNAME_LENGTH = 12;
    public static final int MAX_LEVEL_OF_DEVCARDS=3;

    /* ------ GAME SESSION ------ */
    public static final int MAX_PLAYER_COUNT = 4;

    /* ------ FAITH TRACK ------ */
    //The faith track's spaces go from 0 to 24
    public static final int FAITH_TRACK_LAST_SPACE_INDEX = 24;
    //The number of Pope's favor tiles is 3
    public static final int FAITH_TRACK_NUMBER_OF_POPE_TILES=3;
    //every pope tiles is worth different PV
    public static final int[] FAIT_TRACK_POPE_TILES_PV ={2,3,4};
    //The player gets different points based on the last yellow space reached on the track
    public static final int[] FAITH_TRACK_POSITION_PV ={1,2,4,6,9,12,16,20};
    //The player can activate a Vatican Report if he reaches a pope space
    public static final int[] FAITH_TRACK_POSITION_POPE_SPACE ={8,16,24};
    //The players who have not activate the Vatican Report have to check if they are in the vatican report section or not
    public static final int[] FAITH_TRACK_LENGTH_VATICAN_REPORT_SECTION ={4,5,6};



    /*------- WAREHOUSE DEPOT ------*/
    //The layers of the warehouse depot are 3
    public static final int WAREHOUSE_DEPOT_NUMBER_OF_LAYERS=3;
    //The layers of the warehouse depot have capacity 3,2,1
    public static final int[] WAREHOUSE_DEPOT_CAPACITY_OF_LAYERS={3,2,1};


    /* ------ MARKET BOARD ------ */
    public static final Resource[] MARKET_MARBLE_RESERVE =
            {Resource.WHITE, Resource.WHITE, Resource.WHITE, Resource.WHITE,
             Resource.SHIELD, Resource.SHIELD,
             Resource.STONE, Resource.STONE,
             Resource.COIN, Resource.COIN,
             Resource.SERVANT, Resource.SERVANT,
             Resource.FAITH};
    public static final int MARKET_ROWS_COUNT = 3, MARKET_COLS_COUNT = 4;

    /* ------ DEV CARD GRID ------ */
    public static final int DEV_CARD_GRID_ROWS_COUNT = 3, DEV_CARD_GRID_COLS_COUNT = 4;
    public static final int DEV_CARD_MAX_NUMBER_TO_WIN = 7;

    /* ------ DEV CARD SPACE ----- */
    //The 'basic' spaces in the personal board to activate development cards are 3
    public static final int DEV_CARD_NUMBER_OF_SPACES = 3;
    public static final int NUM_OF_INPUTS_GENERIC_PRODUCTION=2;
    public static final int NUM_OF_OUTPUTS_GENERIC_PRODUCTION=1;


    /* ------ INITIAL CHOICES ----- */
    public static final int LEADER_CARDS_CHOICE_AMOUNT = 2;
    public static final int[] INITIAL_RESOURCES_AMOUNT_FOR_PLAYER = {0, 1, 1, 2};
    //The players have different quantity of initial faith points, based on their turn
    public static final int[] FAITH_TRACK_INITIAL_POINTS={0,0,1,1};

    /* ---------- CLI ----------- */
    public static final String ASK_PLAYER_NAME_MESSAGE = "Choose a unique username to join the session (max 12 characters)...";
    public static final String ASK_ALREADY_CONNECTED_PLAYER_NAME_MESSAGE = "Insert your previous username to join the session (max 12 characters)...";
    public static final String ASK_RECONNECTING_OR_NEW = "Type R to re-join a match, N to start a new one\n";
    public static final String CHOOSE_STYLE_CLIorGUI = "Type '1' if you want to play with CLI, type '2' if you want to play with GUI";
    public static final String INVALID_RECONNECTING_OR_NEW = "Sorry :( you entered an invalid input, try again";
    public static final String PLAYER_USERNAME_RETRY_MESSAGE = "Sorry :( that username is already taken, try again...";
    public static final String PLAYER_USERNAME_NOT_FOUND_MESSAGE = "Sorry :( you must use your previous username to reconnect, try again.";
    public static final String PLAYER_USERNAME_OUT_OF_BOUNDS = "Sorry, the username is invalid (insert a max of 12 characters)";
    public static final String NUMBER_OF_PLAYERS_MESSAGE = "It appears that there are no available game sessions, let's create a new one.\nHow many players do you want? (including you): ";
    public static final String INVALID_NUMBER_OF_PLAYERS_MESSAGE = "Sorry, you just entered an invalid number.\nThe game is supposed to be played by 1 to 4 players, try again: ";
    public static final String ACTION_REJECTED = "Your last move was rejected, try again...\n";
    public static final String INITIAL_CLIENT_WELCOME_MESSAGE = "Welcome to Masters of the Renaissance, join a server to start playing.";
    public static final String IP_ADDRESS_REQUEST = "Type the server's IP address here: ";
    public static final String INVALID_IP_ADDRESS = "Sorry, the IP address you just entered is invalid, try again...";
    public static final String CONNECTION_FAILED_MESSAGE = "Could not establish connection with the provided server address, try again...";

    /* ---------- LEADER CARD SCENE ----------- */
    public static final String SCENE_LEADER_CARD_DISCARD = "Leader Card n.X will be discarded";
    public static final String SCENE_LEADER_CARD_ACTIVATE = "Leader Card n.X will be activated";
    public static final String SCENE_LEADER_CARD_ALREADY_ACTIVATED = "Leader Card n.X is already active";
    public static final String SCENE_LEADER_CARD_ACTIVE_IMPOSSIBLE_TO_DISCARD = "Leader Card n.X is active, it cannot be discarded";
    public static final String SCENE_LEADER_CARD_ALREADY_DISCARDED = "Leader Card n.X has already been discarded";
    public static final String SCENE_LEADER_CARD_SYNTAX_ERROR = "The requested action's syntax is wrong, try again...";
    public static final String SCENE_LEADER_CARD_NOT_ENOUGH_RESOURCES = "The requirements for the selected card could not be fulfilled";
    public static final String SCENE_LEADER_CARD_REQUIREMENTS_MET_PART1 = "The resources you gained  allow you to activate card n.";
    public static final String SCENE_LEADER_CARD_REQUIREMENTS_MET_PART2 = " and card n.";
    public static final String SCENE_LEADER_CARD_REQUIREMENTS_MET_PART3 = "What will you do? (press ENTER to skip)";
    public static final String SCENE_LEADER_CARD_NO_ACTION_AVAILABLE = "No action can be taken at this point (press ENTER to continue)";
    public static final String SCENE_LEADER_CARD_NO_ACTION_ACTIVATION = "You can't activate any leader card at the moment, either discard or press ENTER to continue";
    public static final String SCENE_LEADER_CARD_NO_EXISTS = "The selected card does not exist, try again...";

    /* ---------- CHOOSE RESOURCES SCENE ---------*/
    public static final String SCENE_CHOOSE_RES_RESOURCE_NOT_RECOGNIZED = "->Resource name not recognized, type again:";
    public static final String SCENE_CHOOSE_RES_LEADERCARD_NOT_RECOGNIZED = "Leader card not recognized: wrong syntax. Type again:";



    /* ---------- DEV CARD PURCHASE SCENE ---------*/
    public static final String SCENE_DEVCARD_PURCHASE_WRONG_TYPING = "Incorrect command syntax, type again:";
    public static final String SCENE_DEVCARD_PURCHASE_NO_MORE_SWIPE_RIGHT= "You can't swipe right more than this, type again:";
    public static final String SCENE_DEVCARD_PURCHASE_NO_MORE_SWIPE_LEFT= "You can't swipe left more than this, type again:";
    public static final String SCENE_DEVCARD_PURCHASE_WRONG_SLOT= "The slot does not match , type agaiwith the level of the card, type again:";


    /* ----------- END OF MATCH SCENE ------------ */
    public static final String SCENE_END_OF_MATCH_MESSAGE = "The session is over, press ENTER to exit ...";

    /* ---- INITIAL COMMUNICATION CLIENT-SERVER ---- */
    public static final String WELCOME_ACTION = "RECONNECTINGORNEW";
    public static final String NEW_CONNECTION = "NEW";
    public static final String RECONNECTING = "RECONNECTING";
    public static final String HOST_PLAYER_MESSAGE = "NAMEANDPLAYERS";
    public static final String GUEST_PLAYER_MESSAGE = "NAME";
    public static final String ACK = "OK";
    public static final String NACK = "KO";

    /* -------------- TURN STATUS SCENE --------------- */
    public static final String GO_BACK_MESSAGE = "Otherwise, if you wish to go back and pick a different move, type BACK...";

    /* --------------- PRODUCTION SCENE --------------- */
    public static final String PRODUCTION_SCENE_CANT_GO_BACK = "Sorry, you can't go back once you started the production phase...";

    /* -------------- ACTION TOKEN EXTRACTION ---------- */
    public static final String ACTION_TOKEN_NOTIFICATION_START = "Lorenzo Il Magnifico has used its turn to ";
    public static final String ACTION_TOKEN_NOTIFICATION_END = "It is your turn once again...";
    public static final String ACTION_TOKEN_REMOVE_YELLOW = "remove two yellow Development Cards from the common grid.";
    public static final String ACTION_TOKEN_REMOVE_GREEN = "remove two green Development Cards from the common grid.";
    public static final String ACTION_TOKEN_REMOVE_BLUE = "remove two blue Development Cards from the common grid.";
    public static final String ACTION_TOKEN_REMOVE_PURPLE = "remove two purple Development Cards from the common grid.";
    public static final String ACTION_TOKEN_ADVANCE = "advance two positions forward on the faith track.";
    public static final String ACTION_TOKEN_ADVANCE_SHUFFLE = "advance one positions forward on the faith track and make the future unpredictable.";
    public static final String ACTION_TOKEN_ASCII_ART = "            ▒▒▒▒▒▒▒▒▒▒▒\n" +
                                                        "          ▒▒  Action   ▒▒\n" +
                                                        "          ▒▒   Token   ▒▒\n" +
                                                        "          ▒▒           ▒▒\n" +
                                                        "          ▒▒     ?     ▒▒\n" +
                                                        "            ▒▒▒▒▒▒▒▒▒▒▒\n";

    /* ------------- GUI ------------- */
    public static final int WINDOW_WIDTH = 1280, WINDOW_HEIGHT = 720;
    public static final String WINDOW_TITLE = "Masters of the Renaissance - GC7";
    public static final String GENERIC_ERROR = "Sorry, this action cannot be taken";
    public static final String RESOURCE_DISCARD_REQUEST = "Select # resources to discard...";
    public static final String RESOURCE_SUBSTITUTION_REQUEST = "Select # resources for white substitution...";
    public static final String PRODUCTION_FAILED_NOT_ENOUGH_RESOURCES ="Sorry, you don't have enough resources to activate this production" ;
}
