package it.polimi.ingsw.client.cli.scenes;

import it.polimi.ingsw.client.PlayerWorkSpace;
import it.polimi.ingsw.client.cli.TextScene;
import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.serializable.CompressedModel;
import it.polimi.ingsw.common.util.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * CLI scene used to let the user do a devCard purchase
 */
public class DevCardPurchaseScene extends TextScene {
    private static final String background =
            "\n" +
                    "        ╔╗ ┬ ┬┬ ┬  ┌─┐  ┌┬┐┌─┐┬  ┬┌─┐┬  ┌─┐┌─┐┌┬┐┌─┐┌┐┌┬┐  ┌─┐┌─┐┬─┐┌┬┐\n" +
                    "        ╠╩╗│ │└┬┘  ├─┤   ││├┤ └┐┌┘├┤ │  │ │├─┘│││├┤ ││││   │  ├─┤├┬┘ ││\n" +
                    "        ╚═╝└─┘ ┴   ┴ ┴  ─┴┘└─┘ └┘ └─┘┴─┘└─┘┴  ┴ ┴└─┘┘└┘┴   └─┘┴ ┴┴└──┴┘\n" +
                    "\n" +
                    "     ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄\n" +
                    "     █ Here are the development cards that you can buy:                   █\n" +
                    "     █      ╔════A════╗╔════B════╗╔════C════╗╔════D════╗╔════E════╗       █\n" +
                    "     █      ║pts:     ║║pts:     ║║pts:     ║║pts:     ║║pts:     ║       █\n" +
                    "     █      ║lv:      ║║lv:      ║║lv:      ║║lv:      ║║lv:      ║       █\n" +
                    "     █      ║colour:  ║║colour:  ║║colour:  ║║colour:  ║║colour:  ║       █\n" +
                    "     █  L   ║in:      ║║in:      ║║in:      ║║in:      ║║in:      ║  R    █\n" +
                    "     █  <-  ║         ║║         ║║         ║║         ║║         ║  ->   █\n" +
                    "     █      ║         ║║         ║║         ║║         ║║         ║       █\n" +
                    "     █      ║         ║║         ║║         ║║         ║║         ║       █\n" +
                    "     █      ║Out:     ║║Out:     ║║Out:     ║║Out:     ║║Out:     ║       █\n" +
                    "     █      ║         ║║         ║║         ║║         ║║         ║       █\n" +
                    "     █      ║         ║║         ║║         ║║         ║║         ║       █\n" +
                    "     █      ║         ║║         ║║         ║║         ║║         ║       █\n" +
                    "     █      ╚═════════╝╚═════════╝╚═════════╝╚═════════╝╚═════════╝       █\n" +
                    "     █  Your development card space:               Your resources:        █\n" +
                    "     █   ╔═════1═════╗╔═════2═════╗╔═════3═════╗                          █\n" +
                    "     █   ║In:        ║║In:        ║║In:        ║   -coins:                █\n" +
                    "     █   ║           ║║           ║║           ║   -servants:             █\n" +
                    "     █   ║           ║║           ║║           ║   -shields:              █\n" +
                    "     █   ║           ║║           ║║           ║   -stones:               █\n" +
                    "     █   ║Out:       ║║Out:       ║║Out:       ║                          █\n" +
                    "     █   ║           ║║           ║║           ║                          █\n" +
                    "     █   ║           ║║           ║║           ║                          █\n" +
                    "     █   ║           ║║           ║║           ║                          █\n" +
                    "     █   ╚═══════════╝╚═══════════╝╚═══════════╝                          █\n" +
                    "     █  Type: -> TO SEE ALL THE CARDS YOU CAN BUY: 'R' to swipe right     █\n" +
                    "     █                                             'L' to swipe left      █\n" +
                    "     █                                                                    █\n" +
                    "     █        -> TO SELECT YOUR CHOICE:Type the letter of the card        █\n" +
                    "     █           you want to purchase followed by '-' and the number      █\n" +
                    "     █           of the slot where to insert the card  es. A-2            █\n" +
                    "     █                                                                    █\n" +
                    "     █        -> TO SEE ALL THE DEVELOPMENT CARD GRID: Type 'VIEW ALL'    █\n" +
                    "     █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█\n";

    private static final String background2 ="\n" +
            "     ╔═╗┬  ┬    ┌┬┐┬ ┬┌─┐  ╔╦╗┌─┐┬  ┬┌─┐┬  ┌─┐┌─┐┌┬┐┌─┐┌┐┌┌┬┐  ┌─┐┌─┐┬─┐┌┬┐┌─┐\n" +
            "     ╠═╣│  │     │ ├─┤├┤    ║║├┤ └┐┌┘├┤ │  │ │├─┘│││├┤ │││ │   │  ├─┤├┬┘ ││└─┐\n" +
            "     ╩ ╩┴─┘┴─┘   ┴ ┴ ┴└─┘  ═╩╝└─┘ └┘ └─┘┴─┘└─┘┴  ┴ ┴└─┘┘└┘ ┴   └─┘┴ ┴┴└──┴┘└─┘\n" +
            "       ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄\n" +
            "       █ ╔═════════╗╔═════════╗╔═════════╗╔═════════╗╔═════════╗╔═════════╗ █\n" +
            "       █ ║pts:     ║║pts:     ║║pts:     ║║pts:     ║║pts:     ║║pts:     ║ █\n" +
            "       █ ║lv:      ║║lv:      ║║lv:      ║║lv:      ║║lv:      ║║lv:      ║ █\n" +
            "       █ ║colour:  ║║colour:  ║║colour:  ║║colour:  ║║colour:  ║║colour:  ║ █\n" +
            "       █ ║in:      ║║in:      ║║in:      ║║in:      ║║in:      ║║in:      ║ █\n" +
            "       █ ║         ║║         ║║         ║║         ║║         ║║         ║ █\n" +
            "       █ ║         ║║         ║║         ║║         ║║         ║║         ║ █\n" +
            "       █ ║         ║║         ║║         ║║         ║║         ║║         ║ █\n" +
            "       █ ║Out:     ║║Out:     ║║Out:     ║║Out:     ║║Out:     ║║Out:     ║ █\n" +
            "       █ ║         ║║         ║║         ║║         ║║         ║║         ║ █\n" +
            "       █ ║         ║║         ║║         ║║         ║║         ║║         ║ █\n" +
            "       █ ║         ║║         ║║         ║║         ║║         ║║         ║ █\n" +
            "       █ ║Required:║║Required:║║Required:║║Required:║║Required:║║Required:║ █\n" +
            "       █ ║         ║║         ║║         ║║         ║║         ║║         ║ █\n" +
            "       █ ║         ║║         ║║         ║║         ║║         ║║         ║ █\n" +
            "       █ ║         ║║         ║║         ║║         ║║         ║║         ║ █\n" +
            "       █ ╚═════════╝╚═════════╝╚═════════╝╚═════════╝╚═════════╝╚═════════╝ █\n" +
            "       █ ╔═════════╗╔═════════╗╔═════════╗╔═════════╗╔═════════╗╔═════════╗ █\n" +
            "       █ ║pts:     ║║pts:     ║║pts:     ║║pts:     ║║pts:     ║║pts:     ║ █\n" +
            "       █ ║lv:      ║║lv:      ║║lv:      ║║lv:      ║║lv:      ║║lv:      ║ █\n" +
            "       █ ║colour:  ║║colour:  ║║colour:  ║║colour:  ║║colour:  ║║colour:  ║ █\n" +
            "       █ ║in:      ║║in:      ║║in:      ║║in:      ║║in:      ║║in:      ║ █\n" +
            "       █ ║         ║║         ║║         ║║         ║║         ║║         ║ █\n" +
            "       █ ║         ║║         ║║         ║║         ║║         ║║         ║ █\n" +
            "       █ ║         ║║         ║║         ║║         ║║         ║║         ║ █\n" +
            "       █ ║Out:     ║║Out:     ║║Out:     ║║Out:     ║║Out:     ║║Out:     ║ █\n" +
            "       █ ║         ║║         ║║         ║║         ║║         ║║         ║ █\n" +
            "       █ ║         ║║         ║║         ║║         ║║         ║║         ║ █\n" +
            "       █ ║         ║║         ║║         ║║         ║║         ║║         ║ █\n" +
            "       █ ║Required:║║Required:║║Required:║║Required:║║Required:║║Required:║ █\n" +
            "       █ ║         ║║         ║║         ║║         ║║         ║║         ║ █\n" +
            "       █ ║         ║║         ║║         ║║         ║║         ║║         ║ █\n" +
            "       █ ║         ║║         ║║         ║║         ║║         ║║         ║ █\n" +
            "       █ ╚═════════╝╚═════════╝╚═════════╝╚═════════╝╚═════════╝╚═════════╝ █\n" +
            "       █       Type 'BACK'to return to the development card purchase.       █\n" +
            "       █▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄█\n";


    //LEFT and RIGHT BOXES
    private static final int BOX_LEFTRIGHT_ROW = 11;
    private static final int BOX_LEFT_COL = 9;
    private static final int BOX_RIGHT_COL = 70;

    //dev cards
    private static final int[] BOXDEVCARD_COLUMNS = {11, 24, 37};
    private static final int BOXDEVCARD_INPUTS_ROW = 23;
    private static final int BOXDEVCARD_OUTPUT_ROW = 27;
    private static final int BOXDEVCARD_HEIGHT = 8;
    private static final int BOXDEVCARD_LEVEL_ROW = 21;
    private static final int[] BOXDEVCARD_LEVEL_COL= {19,32,45};


    //your resources section
    private static final int BOX_COINS_ROW = 22;
    private static final int BOX_SERVANTS_ROW = 23;
    private static final int BOX_SHIELDS_ROW = 24;
    private static final int BOX_STONES_ROW = 25;
    private static final int BOX_RESOURCES_COLUMN = 63;

    //PURCHASABLE CARDS
    private static final char[] INDEXES_OF_THE_CARDS = {'A','B','C','D','E','F','G','H','I','J','K','L'};
    private static final int[] BOXpurchCARDS_INDEXES_COLUMNS= {18,29,40,51,62};
    private static final int BOXpurchCARDS_INDEXES_ROW= 7;
    private static final int numberOfVisibleCardsForScreen = 5;
    private static final int BOXpurchCARDS_PTS_ROW = 8;
    private static final int BOXpurchCARDS_IN_ROW = 12;
    private static final int BOXpurchCARDS_OUT_ROW = 16;
    private static final int[] BOXpurchCARDS_IN_OUT_COLUMNS = {14,25,36,47,58};
    private static final int[] BOXpurchCARDS_PTS_COLUMNS= {21,32,43,54,65};
    private static final int BOXpurchCARD_HEIGHT=11;

    //ALL THE DEV CARDS
    private static final int FIRSTlineOFcards_pts_row=6;
    private static final int[] PTS_LV_col={18,29,40,51,62,73};
    private static final int FIRSTlineOFcards_INPUTS_row=10;
    private static final int[] RES_col={11,22,33,44,55,66};
    private static final int FIRSTlineOFcards_OUTPUTS_row=14;
    private static final int FIRSTlineOFcards_REQ_row=18;
    private static final int SECOND_lineOFcards_pts_row=23;
    private static final int SECONDlineOFcards_INPUTS_row=27;
    private static final int SECONDlineOFcards_OUTPUTS_row=31;
    private static final int SECONDlineOFcards_REQ_row=35;



    private static String BACKGROUND_DEVCARD_RES;
    private static int TOTAL_NUMBER_OF_SCREENS;
    private static int ACTUAL_SCREEN_INDEX;
    private static List<Integer> ALL_PURCHASABLE_CARDS;


    @Override
    public void draw(CompressedModel compressedModel) {
        //draw devcard and resources and write in STATIC_DRAW_DEVCARDANDRES
        BACKGROUND_DEVCARD_RES= "";
        TOTAL_NUMBER_OF_SCREENS=0;
        ACTUAL_SCREEN_INDEX=0;
        BACKGROUND_DEVCARD_RES=drawResources(compressedModel);

        String[] arrayToPrint = super.toArrayFromString(BACKGROUND_DEVCARD_RES);

        ALL_PURCHASABLE_CARDS = new ArrayList<>();
        ResourceStack[] resourceStacks=compressedModel.getCumulatedPlayerStorage().get(compressedModel.getaP());

        boolean[] levelPurchasable={false,false,false};
        for(Stack<Integer> decks:compressedModel.getDevCardSpace().get(compressedModel.getaP())){
           try {
               DevelopmentCard developmentCard = CardRepository.getInstance().getDevCardByID(decks.peek());
               if(developmentCard.getCardLevel()!=GameConstants.MAX_LEVEL_OF_DEVCARDS) {
                   levelPurchasable[developmentCard.getCardLevel()] = true;
               }
           }
           catch(EmptyStackException e){levelPurchasable[0]=true;}
        }



        for(int row=0;row<GameConstants.DEV_CARD_GRID_ROWS_COUNT;row++){
            for(int col=0;col<GameConstants.DEV_CARD_GRID_COLS_COUNT;col++){
                try{
                    DevelopmentCard developmentCard = CardRepository.getInstance().getDevCardByID(compressedModel.getDevCardGridState()[row][col]);
                    //check the level of the card


                    if(developmentCard != null && levelPurchasable[developmentCard.getCardLevel()-1]){

                        //DISCOUNT ACTIVE LEADER CARDS
                        List<LeaderCard> myDiscountActiveLeaderCards=compressedModel.getAllPlayersActiveLeaderCards().get(compressedModel.getaP()).stream().map((x)->CardRepository.getInstance().getLeaderCardByID(x)).filter((x)->x.getPower().getSpecialAbilityType()==SpecialAbility.DISCOUNT).collect(Collectors.toList());
                        Resource firstDiscount=null;Resource secondDiscount=null;
                        if(myDiscountActiveLeaderCards.size()==1){firstDiscount= ((AbilityPower) myDiscountActiveLeaderCards.get(0).getPower()).getResourceType();}
                        if(myDiscountActiveLeaderCards.size()==2){secondDiscount= ((AbilityPower) myDiscountActiveLeaderCards.get(1).getPower()).getResourceType();}

                        //check if your resources are enough
                        boolean enough=true;
                        for(ResourceStack resourceStackThatThePlayerHas:resourceStacks){
                            for(ResourceStack resourceStackRequired:developmentCard.getRequiredResources()){
                                if(resourceStackRequired.getResourceType()==resourceStackThatThePlayerHas.getResourceType()){

                                    //IF YOU HAVE A DISCOUNT
                                    if(((firstDiscount!=null)&&(firstDiscount==resourceStackThatThePlayerHas.getResourceType()))||((secondDiscount!=null)&&(secondDiscount==resourceStackThatThePlayerHas.getResourceType()))) {
                                        if (resourceStackRequired.getAmount()-1> resourceStackThatThePlayerHas.getAmount()) {
                                            enough = false;
                                        }
                                    }

                                    //IF YOU DO NOT HAVE A DISCOUNT
                                    else{
                                        if (resourceStackRequired.getAmount() > resourceStackThatThePlayerHas.getAmount()) {
                                            enough = false;
                                        }
                                    }
                                }
                            }
                        }
                        if(enough)ALL_PURCHASABLE_CARDS.add(developmentCard.getCardID());
                    }
                }
                catch (EmptyStackException ignored){}
                ;
            }
        }
        //System.out.println("all the cards you can buy are:"+allpurchasableCards); //for testing

        if(ALL_PURCHASABLE_CARDS.size()<numberOfVisibleCardsForScreen){
            TOTAL_NUMBER_OF_SCREENS=1;
        }
        else {
            TOTAL_NUMBER_OF_SCREENS=0;
            while(ALL_PURCHASABLE_CARDS.size()>TOTAL_NUMBER_OF_SCREENS*numberOfVisibleCardsForScreen){TOTAL_NUMBER_OF_SCREENS++; }
        }
        ACTUAL_SCREEN_INDEX=1;
        arrayToPrint=modifyArrayTPrintWithPurchCards(arrayToPrint,compressedModel);

        System.out.println(super.toStringFromArray(arrayToPrint));

    }

    @Override
    public void askInput(CompressedModel compressedModel) {
        Scanner scanner=new Scanner(System.in);
        int cardID = -1;
        int rowToInsert = 0;
        int colToInsert = 0;
        int numberofslot = 0;
        List<Pair<Resource, StorageType>> toInsertInPlayerEvent= new ArrayList<>();
        boolean selectionEnded=false;
        String digit = "";

        int[] levelAcceptable={-1,-1,-1};
        int counter=0;
        for(Stack<Integer> decks:compressedModel.getDevCardSpace().get(compressedModel.getaP())){
            try {
                DevelopmentCard developmentCard = CardRepository.getInstance().getDevCardByID(decks.peek());
                if(developmentCard.getCardLevel()!=GameConstants.MAX_LEVEL_OF_DEVCARDS) {
                    levelAcceptable[counter] = developmentCard.getCardLevel()+1;
                }
            }
            catch(EmptyStackException e){levelAcceptable[counter]=1;}
            counter++;
        }

        System.out.println(GameConstants.GO_BACK_MESSAGE);

        do{
            String strBuffer = scanner.nextLine();
            if(!((strBuffer.isBlank())||(strBuffer.equals("\n")))){
                strBuffer = strBuffer.toUpperCase();
                digit = strBuffer.charAt(0) + "";
            }
            //Player requested to go back
            if(strBuffer.equals("BACK"))
            {
                registerGoBackRequest();
                return;
            }
            //SWIPE RIGHT
            else if(digit.equals("R")){
                if(ACTUAL_SCREEN_INDEX<TOTAL_NUMBER_OF_SCREENS) {
                    String[] arrayToPrint = super.toArrayFromString(BACKGROUND_DEVCARD_RES);
                    ACTUAL_SCREEN_INDEX++;
                    arrayToPrint = modifyArrayTPrintWithPurchCards(arrayToPrint, compressedModel);
                    System.out.println(super.toStringFromArray(arrayToPrint));
                }
                else{
                    //you can't swipe right
                    System.out.println(GameConstants.SCENE_DEVCARD_PURCHASE_NO_MORE_SWIPE_RIGHT);
                }
            }
            //SWIPE LEFT
            else if(digit.equals("L")){
                if(ACTUAL_SCREEN_INDEX>1) {
                    String[] arrayToPrint = super.toArrayFromString(BACKGROUND_DEVCARD_RES);
                    ACTUAL_SCREEN_INDEX--;
                    arrayToPrint = modifyArrayTPrintWithPurchCards(arrayToPrint, compressedModel);
                    System.out.println(super.toStringFromArray(arrayToPrint));
                }
                else{
                    //you can't swipe left
                    System.out.println(GameConstants.SCENE_DEVCARD_PURCHASE_NO_MORE_SWIPE_LEFT);
                }
            }
            else if((strBuffer.length()==3)&&(strBuffer.charAt(1)=='-')&&(Arrays.toString(INDEXES_OF_THE_CARDS).indexOf(strBuffer.charAt(0))!=-1)&&((strBuffer.charAt(2)=='1')||(strBuffer.charAt(2)=='2')||strBuffer.charAt(2)=='3')){

                //CONVERT THE CHAR THAT REPRESENTS THE CARD TO ITS HASHCODE and -65 TO MAKE IT START FROM 0
                char cardChar=strBuffer.charAt(0);
                int cardHashCode= Character.hashCode(cardChar) -65;

                //EXTRACT THE ID OF THE CARD
                try {
                    cardID = ALL_PURCHASABLE_CARDS.get(cardHashCode);


                    //NUMBER OF THE SLOT FROM 1 TO 3!!

                    char convertSlotInNumber=strBuffer.charAt(2);
                    numberofslot=Character.hashCode(convertSlotInNumber) -48;
                    if (levelAcceptable[numberofslot-1] == CardRepository.getInstance().getDevCardByID(cardID).getCardLevel()) {
                        int[][] devCardGrid= compressedModel.getDevCardGridState();
                        rowToInsert=0;
                        colToInsert=0;
                        for(int rows=0;rows<GameConstants.DEV_CARD_GRID_ROWS_COUNT;rows++){
                            for(int cols=0;cols<GameConstants.DEV_CARD_GRID_COLS_COUNT;cols++){
                                if(devCardGrid[rows][cols]==cardID){
                                    rowToInsert=rows;
                                    colToInsert=cols;
                                }
                            }
                        }
                        selectionEnded = true;
                    } else {
                        //WRONG SLOT
                        //System.out.println("WRONG SLOT");
                        System.out.println(GameConstants.SCENE_DEVCARD_PURCHASE_WRONG_SLOT);
                    }
                }
                catch(IndexOutOfBoundsException e){
                    //WRONG INPUT
                    // System.out.println("NON TROVA CARTA");
                    System.out.println(GameConstants.SCENE_DEVCARD_PURCHASE_WRONG_TYPING);
                }
            }
            else if(strBuffer.equals("VIEW ALL")){
                //DRAW ALL THE DEV CARD GRID
                printAllDevCards(compressedModel);
                strBuffer=scanner.nextLine().toUpperCase(Locale.ROOT);
                while(!strBuffer.equals("BACK")){
                    System.out.println(GameConstants.SCENE_DEVCARD_PURCHASE_WRONG_TYPING);
                    strBuffer=scanner.nextLine().toUpperCase(Locale.ROOT);
                }
                draw(compressedModel);
            }
            else{
                //WRONG INPUT SYNTAX
                System.out.println(GameConstants.SCENE_DEVCARD_PURCHASE_WRONG_TYPING);
            }
        }while(!selectionEnded);

        //CHOICE OF THE CARD

        PlayerWorkSpace.getInstance().devCardPurchaseAction(cardID, rowToInsert, colToInsert, numberofslot);

    }


    //####################################### INTERNAL METHODS #################################################

    /**
     * print all purchasable devCards
     * @param compressedModel the actual compressedModel
     */
    private void printAllDevCards(CompressedModel compressedModel) {
        String[] arrayToPrint = super.toArrayFromString(background2);

        List<DevelopmentCard> allDevCards=new ArrayList<>();
        for(int i=0;i<GameConstants.DEV_CARD_GRID_ROWS_COUNT;i++){
            for(int j=0;j<GameConstants.DEV_CARD_GRID_COLS_COUNT;j++){
                int cardID=compressedModel.getDevCardGridState()[i][j];
                //-1 means Empty Stack
                if(cardID != -1){
                    DevelopmentCard toAdd= CardRepository.getInstance().getDevCardByID(cardID);
                    allDevCards.add(toAdd);
                }
            }
        }

        int count=0;
        for(int i=0;i< allDevCards.size();i++){
            if(i<6){

                arrayToPrint=substituteLineBackground(arrayToPrint, ((Integer) allDevCards.get(i).getVictoryPoints()).toString(),FIRSTlineOFcards_pts_row,PTS_LV_col[i]) ;
                arrayToPrint=substituteLineBackground(arrayToPrint, ((Integer) allDevCards.get(i).getCardLevel()).toString(),FIRSTlineOFcards_pts_row+1,PTS_LV_col[i]) ;
                arrayToPrint=substituteLineBackground(arrayToPrint,((Character) allDevCards.get(i).getBannerColor().toString().charAt(0)).toString() ,FIRSTlineOFcards_pts_row+2,PTS_LV_col[i]) ;
                List<ResourceStack> resourceStackList= new ArrayList<>(allDevCards.get(i).getProductionPower().getRequirements());
                arrayToPrint=devCardRequRew(resourceStackList,arrayToPrint,i,true,FIRSTlineOFcards_INPUTS_row,FIRSTlineOFcards_OUTPUTS_row,RES_col);
                resourceStackList= new ArrayList<>(allDevCards.get(i).getProductionPower().getReward());
                arrayToPrint=devCardRequRew(resourceStackList,arrayToPrint,i,false,FIRSTlineOFcards_INPUTS_row,FIRSTlineOFcards_OUTPUTS_row,RES_col);
                resourceStackList= new ArrayList<>(allDevCards.get(i).getRequiredResources());
                int countlines=0;
                for(ResourceStack resourceStack:resourceStackList){
                    arrayToPrint=substituteLineBackground(arrayToPrint, resourceStack.getResourceType().toString()+" "+((Integer) resourceStack.getAmount()).toString(),FIRSTlineOFcards_REQ_row+countlines,RES_col[i]) ;
                    countlines++;
                }

            }
            else{
                arrayToPrint=substituteLineBackground(arrayToPrint, ((Integer) allDevCards.get(i).getVictoryPoints()).toString(),SECOND_lineOFcards_pts_row,PTS_LV_col[i-6]) ;
                arrayToPrint=substituteLineBackground(arrayToPrint, ((Integer) allDevCards.get(i).getCardLevel()).toString(),SECOND_lineOFcards_pts_row+1,PTS_LV_col[i-6]) ;
                arrayToPrint=substituteLineBackground(arrayToPrint,((Character) allDevCards.get(i).getBannerColor().toString().charAt(0)).toString() ,SECOND_lineOFcards_pts_row+2,PTS_LV_col[i-6]) ;

                List<ResourceStack> resourceStackList= new ArrayList<>(allDevCards.get(i).getProductionPower().getRequirements());
                arrayToPrint=devCardRequRew(resourceStackList,arrayToPrint,i-6,true,SECONDlineOFcards_INPUTS_row,SECONDlineOFcards_OUTPUTS_row,RES_col);
                resourceStackList= new ArrayList<>(allDevCards.get(i).getProductionPower().getReward());
                arrayToPrint=devCardRequRew(resourceStackList,arrayToPrint,i-6,false,SECONDlineOFcards_INPUTS_row,SECONDlineOFcards_OUTPUTS_row,RES_col);

                resourceStackList= new ArrayList<>(allDevCards.get(i).getRequiredResources());
                int countlines=0;
                for(ResourceStack resourceStack:resourceStackList){
                    arrayToPrint=substituteLineBackground(arrayToPrint, resourceStack.getResourceType().toString()+" "+((Integer) resourceStack.getAmount()).toString(),SECONDlineOFcards_REQ_row+countlines,RES_col[i-6]) ;
                    countlines++;
                }
            }

            count++;
        }

        System.out.println(super.toStringFromArray(arrayToPrint));
    }

    /**
     * Prints the devCardRequirements on the arrayToPrint
     * @param list the list of resourceStack
     * @param arrayToPrint the arrayToPrint
     * @param deckCount the number of decks
     * @param isInput Are those input or output resources?
     * @param rowInput the input row
     * @param rowOutput the output row
     * @param col the column
     * @return the modified arrayToPrint
     */
    private String[] devCardRequRew(List<ResourceStack> list, String[] arrayToPrint, int deckCount, boolean isInput, int rowInput, int rowOutput,int[] col) {
        int resourceLineCount = 0;
        for (ResourceStack resource : list) {
            String stringToWrite = resource.getResourceType().toString() + " " + String.valueOf(resource.getAmount());
            arrayToPrint = substituteLineBackground(arrayToPrint, String.valueOf(stringToWrite), isInput ? rowInput + resourceLineCount : rowOutput+ resourceLineCount, col[deckCount]);
            resourceLineCount++;
        }
        return arrayToPrint;
    }


    /**
     * Modify arrayToPrint with purchased cards string [ ].
     *
     * @param arrayToPrint    the arrayToPrint
     * @param compressedModel the compressed model
     * @return the string arrayTOoPrint [ ]
     */
    public String[] modifyArrayTPrintWithPurchCards(String[] arrayToPrint,CompressedModel compressedModel) {
        //DELETE R/L SYMBOLS BASED ON THE NUMBER OF THE SCREEN
        if((ACTUAL_SCREEN_INDEX==1)&&(TOTAL_NUMBER_OF_SCREENS==1)) {
            arrayToPrint = substituteLineBackground(arrayToPrint, "  ", BOX_LEFTRIGHT_ROW, BOX_LEFT_COL);
            arrayToPrint = substituteLineBackground(arrayToPrint, "  ", BOX_LEFTRIGHT_ROW + 1, BOX_LEFT_COL);
            arrayToPrint = substituteLineBackground(arrayToPrint, "  ", BOX_LEFTRIGHT_ROW, BOX_RIGHT_COL);
            arrayToPrint = substituteLineBackground(arrayToPrint, "  ", BOX_LEFTRIGHT_ROW + 1, BOX_RIGHT_COL);
        }
        else if(ACTUAL_SCREEN_INDEX==TOTAL_NUMBER_OF_SCREENS){
            arrayToPrint = substituteLineBackground(arrayToPrint, "  ", BOX_LEFTRIGHT_ROW, BOX_RIGHT_COL);
            arrayToPrint = substituteLineBackground(arrayToPrint, "  ", BOX_LEFTRIGHT_ROW + 1, BOX_RIGHT_COL);
        }
        else if(ACTUAL_SCREEN_INDEX==1){
            arrayToPrint = substituteLineBackground(arrayToPrint, "  ", BOX_LEFTRIGHT_ROW, BOX_LEFT_COL);
            arrayToPrint = substituteLineBackground(arrayToPrint, "  ", BOX_LEFTRIGHT_ROW + 1, BOX_LEFT_COL);
        }

        int indexOfTheVisibleCard=(ACTUAL_SCREEN_INDEX-1)*numberOfVisibleCardsForScreen;
        for(int counterOfVisibleCards=0;counterOfVisibleCards<numberOfVisibleCardsForScreen;counterOfVisibleCards++){
            try{
                DevelopmentCard developmentCard= CardRepository.getInstance().getDevCardByID(ALL_PURCHASABLE_CARDS.get(indexOfTheVisibleCard));
                arrayToPrint = devCardRequRew(developmentCard.getProductionPower().getRequirements(), arrayToPrint, counterOfVisibleCards, true,BOXpurchCARDS_IN_ROW,BOXpurchCARDS_OUT_ROW,BOXpurchCARDS_IN_OUT_COLUMNS);
                arrayToPrint = devCardRequRew(developmentCard.getProductionPower().getReward(), arrayToPrint, counterOfVisibleCards, false,BOXpurchCARDS_IN_ROW,BOXpurchCARDS_OUT_ROW,BOXpurchCARDS_IN_OUT_COLUMNS);
                arrayToPrint=substituteLineBackground(arrayToPrint,((Integer)developmentCard.getVictoryPoints()).toString(), BOXpurchCARDS_PTS_ROW,BOXpurchCARDS_PTS_COLUMNS[counterOfVisibleCards]);
                arrayToPrint=substituteLineBackground(arrayToPrint,((Integer)developmentCard.getCardLevel()).toString(),BOXpurchCARDS_PTS_ROW+1,BOXpurchCARDS_PTS_COLUMNS[counterOfVisibleCards]);
                arrayToPrint=substituteLineBackground(arrayToPrint, ((Character) developmentCard.getBannerColor().toString().charAt(0)).toString(), BOXpurchCARDS_PTS_ROW+2,BOXpurchCARDS_PTS_COLUMNS[counterOfVisibleCards]);
                arrayToPrint=substituteLineBackground(arrayToPrint, ((Character)INDEXES_OF_THE_CARDS[indexOfTheVisibleCard]).toString(),BOXpurchCARDS_INDEXES_ROW,BOXpurchCARDS_INDEXES_COLUMNS[counterOfVisibleCards]);
            }
            catch(Exception e){
                for(int i=0;i<BOXpurchCARD_HEIGHT;i++){arrayToPrint = substituteLineBackground(arrayToPrint,"░░░░░░░░░",BOXpurchCARDS_PTS_ROW+i , BOXpurchCARDS_IN_OUT_COLUMNS[counterOfVisibleCards]);}
                arrayToPrint=substituteLineBackground(arrayToPrint, "░",BOXpurchCARDS_INDEXES_ROW,BOXpurchCARDS_INDEXES_COLUMNS[counterOfVisibleCards]);
            }

            indexOfTheVisibleCard++;
        }


        return arrayToPrint;
    }


    /**
     * Draw resources into the arrayToPrint.
     *
     * @param compressedModel the compressed model
     * @return the arrayToPrint.
     */
    public String drawResources(CompressedModel compressedModel){
        String[] arrayToPrint = super.toArrayFromString(background);

        //DRAW DEV CARDS
        int deckCount = 0;
        for (Stack<Integer> deck : compressedModel.getDevCardSpace().get(compressedModel.getaP())) {
            try {
                DevelopmentCard developmentCard = CardRepository.getInstance().getDevCardByID(deck.peek());

                arrayToPrint = devCardRequRew(developmentCard.getProductionPower().getRequirements(), arrayToPrint, deckCount, true,BOXDEVCARD_INPUTS_ROW,BOXDEVCARD_OUTPUT_ROW,BOXDEVCARD_COLUMNS);
                arrayToPrint = devCardRequRew(developmentCard.getProductionPower().getReward(), arrayToPrint, deckCount, false,BOXDEVCARD_INPUTS_ROW,BOXDEVCARD_OUTPUT_ROW,BOXDEVCARD_COLUMNS);
                arrayToPrint = substituteLineBackground(arrayToPrint,"lv"+((Integer)developmentCard.getCardLevel()).toString(),BOXDEVCARD_LEVEL_ROW,BOXDEVCARD_LEVEL_COL[deckCount]);
                deckCount++;
            } catch (EmptyStackException e) {
                for (int i = 0; i < BOXDEVCARD_HEIGHT; i++) {
                    arrayToPrint = substituteLineBackground(arrayToPrint, "░░░░░░░░░░░", BOXDEVCARD_INPUTS_ROW + i - 1, BOXDEVCARD_COLUMNS[deckCount]);
                }
                deckCount++;
            }
        }
        while (deckCount < GameConstants.DEV_CARD_NUMBER_OF_SPACES) {
            for (int i = 0; i < BOXDEVCARD_HEIGHT; i++) {
                arrayToPrint = substituteLineBackground(arrayToPrint, "░░░░░░░░░░░", BOXDEVCARD_INPUTS_ROW + i - 1, BOXDEVCARD_COLUMNS[deckCount]);
            }
            deckCount++;
        }

        //draw your resources
        int numberOfCoins, numberOfServants, numberOfShields, numberOfStones;
        try {
            numberOfCoins = Arrays.stream(compressedModel.getCumulatedPlayerStorage().get(compressedModel.getaP())).filter((x) -> (x.getResourceType() == Resource.COIN)).findFirst().get().getAmount();
        } catch (NoSuchElementException e) {
            numberOfCoins = 0;
        }
        try {
            numberOfServants = Arrays.stream(compressedModel.getCumulatedPlayerStorage().get(compressedModel.getaP())).filter((x) -> (x.getResourceType() == Resource.SERVANT)).findFirst().get().getAmount();
        } catch (NoSuchElementException e) {
            numberOfServants = 0;
        }
        try {
            numberOfShields = Arrays.stream(compressedModel.getCumulatedPlayerStorage().get(compressedModel.getaP())).filter((x) -> (x.getResourceType() == Resource.SHIELD)).findFirst().get().getAmount();
        } catch (NoSuchElementException e) {
            numberOfShields = 0;
        }
        try {
            numberOfStones = Arrays.stream(compressedModel.getCumulatedPlayerStorage().get(compressedModel.getaP())).filter((x) -> (x.getResourceType() == Resource.STONE)).findFirst().get().getAmount();
        } catch (NoSuchElementException e) {
            numberOfStones = 0;
        }
        arrayToPrint = substituteLineBackground(arrayToPrint, Integer.toString(numberOfCoins), BOX_COINS_ROW, BOX_RESOURCES_COLUMN);
        arrayToPrint = substituteLineBackground(arrayToPrint, String.valueOf(numberOfServants), BOX_SERVANTS_ROW, BOX_RESOURCES_COLUMN);
        arrayToPrint = substituteLineBackground(arrayToPrint, String.valueOf(numberOfShields), BOX_SHIELDS_ROW, BOX_RESOURCES_COLUMN);
        arrayToPrint = substituteLineBackground(arrayToPrint, String.valueOf(numberOfStones), BOX_STONES_ROW, BOX_RESOURCES_COLUMN);
        return super.toStringFromArray(arrayToPrint);
    }

    /**
     * Gets static draw devcardandres.
     *
     * @return the static draw devcardandres
     */
    public static String getStaticDrawDevcardandres() {
        return BACKGROUND_DEVCARD_RES;
    }
}