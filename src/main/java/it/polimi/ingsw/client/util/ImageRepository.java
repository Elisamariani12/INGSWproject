package it.polimi.ingsw.client.util;

import it.polimi.ingsw.client.exceptions.ImagesNotLoadedException;
import it.polimi.ingsw.client.exceptions.UnregisteredCardIDException;
import it.polimi.ingsw.common.exception.ResourceTypeNotSupportedException;
import it.polimi.ingsw.common.util.ActionTokenType;
import it.polimi.ingsw.common.util.Resource;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Used to load images once for performance and memory usage reasons [Singleton]
 */
public class ImageRepository
{
    //Unique instance
    private static ImageRepository _instance;
    private boolean loadingComplete;

    //Image variables
    private Image personalBoardImg;
    private Image personalBoardBWImg;
    private Image marketBoardImg;
    private Image marbleBoardImg;
    private Map<Integer, Image> cardImages;
    private Image[] vaticanReportDisabledImages;
    private Image[] vaticanReportEnabledImages;
    private Image[] resourceImages;
    private Image redCrossImg;
    private Image blackCrossImg;
    private Image inkWellImg;
    private Image[] marbleImages;
    private Image[] actionTokenImages;
    private Image horizontalMarketArrowImg;
    private Image verticalMarketArrowImg;
    private Image leaderCardBackImg;
    private Image welcomePanelImg;
    private Image welcomePanelBackgroundTown;
    private Image marketDiscardImg;
    private Image crossedResImg, tickedResImg;
    private Image cardSlotSelectionImage;
    private Image genericProductionDialogBackground;
    private Image productionResourcesChest;
    private Image parchmentMP, parchmentSP;

    /** Creates a new ImageRepository instance
     */
    private ImageRepository()
    {
        loadingComplete = false;
    }

    /**
     * Returns the unique instance of this Singleton
     *
     * @return Unique instance
     */
    public static ImageRepository getInstance()
    {
        if(_instance == null) _instance = new ImageRepository();
        return _instance;
    }

    /**
     * Returns whether or not loadAllGraphics() has been successfully called
     *
     * @return Has loadAllGraphics() been successfully called?
     */
    public boolean hasLoadedAllGraphics()
    {
        return loadingComplete;
    }

    /**
     * Loads all images to be retrieved afterwards
     */
    public void loadAllGraphics()
    {
        try
        {
            welcomePanelImg = ImageIO.read(getImageStream("welcome_panel", "board"));
            welcomePanelBackgroundTown = ImageIO.read(getImageStream("initial_background", "board"));
            personalBoardImg = ImageIO.read(getImageStream("personalBoard", "board"));
            personalBoardBWImg = ImageIO.read(getImageStream("personalBoard_BW", "board"));
            marketBoardImg = ImageIO.read(getImageStream("marketBoard", "punchboard"));
            marbleBoardImg = ImageIO.read(getImageStream("marbleBoard", "punchboard"));
            marketDiscardImg = ImageIO.read(getImageStream("marketDiscard", "board"));
            cardSlotSelectionImage = ImageIO.read(getImageStream("cardSlotSelection", "board"));
            genericProductionDialogBackground = ImageIO.read(getImageStream("genericPowerBack", "other"));
            productionResourcesChest = ImageIO.read(getImageStream("productionResourcesChest", "punchboard"));
            parchmentSP = ImageIO.read(getImageStream("parchmentSingleplayer", "outcome"));
            parchmentMP = ImageIO.read(getImageStream("parchmentMultiplayer", "outcome"));

            vaticanReportEnabledImages = new Image[3];
            vaticanReportEnabledImages[0] = ImageIO.read(getImageStream("vatRepEn1", "punchboard"));
            vaticanReportEnabledImages[1] = ImageIO.read(getImageStream("vatRepEn2", "punchboard"));
            vaticanReportEnabledImages[2] = ImageIO.read(getImageStream("vatRepEn3", "punchboard"));
            vaticanReportDisabledImages = new Image[3];
            vaticanReportDisabledImages[0] = ImageIO.read(getImageStream("vatRepDis1", "punchboard"));
            vaticanReportDisabledImages[1] = ImageIO.read(getImageStream("vatRepDis2", "punchboard"));
            vaticanReportDisabledImages[2] = ImageIO.read(getImageStream("vatRepDis3", "punchboard"));

            resourceImages = new Image[4];
            resourceImages[0] = ImageIO.read(getImageStream("coin", "punchboard.resources"));
            resourceImages[1] = ImageIO.read(getImageStream("servant", "punchboard.resources"));
            resourceImages[2] = ImageIO.read(getImageStream("shield", "punchboard.resources"));
            resourceImages[3] = ImageIO.read(getImageStream("stone", "punchboard.resources"));

            redCrossImg = ImageIO.read(getImageStream("redCross", "punchboard"));
            blackCrossImg = ImageIO.read(getImageStream("blackCross", "punchboard"));
            inkWellImg = ImageIO.read(getImageStream("inkWell", "punchboard"));

            marbleImages = new Image[7];
            marbleImages[0] = ImageIO.read(getImageStream("yellow", "punchboard.marbles"));
            marbleImages[1] = ImageIO.read(getImageStream("purple", "punchboard.marbles"));
            marbleImages[2] = ImageIO.read(getImageStream("blue", "punchboard.marbles"));
            marbleImages[3] = ImageIO.read(getImageStream("gray", "punchboard.marbles"));
            marbleImages[4] = ImageIO.read(getImageStream("red", "punchboard.marbles"));
            marbleImages[5] = ImageIO.read(getImageStream("white", "punchboard.marbles"));
            marbleImages[6] = ImageIO.read(getImageStream("whiteSub", "punchboard.marbles"));

            actionTokenImages = new Image[6];
            actionTokenImages[0] = ImageIO.read(getImageStream("blueDiscard", "punchboard.actiontokens"));
            actionTokenImages[1] = ImageIO.read(getImageStream("greenDiscard", "punchboard.actiontokens"));
            actionTokenImages[2] = ImageIO.read(getImageStream("purpleDiscard", "punchboard.actiontokens"));
            actionTokenImages[3] = ImageIO.read(getImageStream("yellowDiscard", "punchboard.actiontokens"));
            actionTokenImages[4] = ImageIO.read(getImageStream("crossAdvance", "punchboard.actiontokens"));
            actionTokenImages[5] = ImageIO.read(getImageStream("crossAdvanceShuffle", "punchboard.actiontokens"));

            horizontalMarketArrowImg = ImageIO.read(getImageStream("marketArrowHorizontal", "punchboard"));
            verticalMarketArrowImg = ImageIO.read(getImageStream("marketArrowVertical", "punchboard"));
            leaderCardBackImg = ImageIO.read(getImageStream("leader_card_back", "cards.back"));

            crossedResImg = ImageIO.read(getImageStream("crossedRes", "other"));
            tickedResImg  = ImageIO.read(getImageStream("tickedRes", "other"));

            InputStream cardGraphDataStream = getClass().getClassLoader().getResourceAsStream("graphics/cards/card_graphics_list.txt");
            Scanner cardGraphicsDataScanner = new Scanner(cardGraphDataStream);

            String rangeBufferString;
            int leaderCardFirst = 0, leaderCardLast = 0, devCardFirst = 0, devCardLast = 0;

            //Read card num ranges from file
            while(cardGraphicsDataScanner.hasNext())
            {
                rangeBufferString = cardGraphicsDataScanner.nextLine();

                if(rangeBufferString.contains("leader_cards"))
                {
                    String[] pieces = rangeBufferString.substring(13).split("-");
                    leaderCardFirst = Integer.valueOf(pieces[0]);
                    leaderCardLast = Integer.valueOf(pieces[1]);
                }
                else if(rangeBufferString.contains("dev_cards"))
                {
                    String[] pieces = rangeBufferString.substring(10).split("-");
                    devCardFirst = Integer.valueOf(pieces[0]);
                    devCardLast = Integer.valueOf(pieces[1]);
                }
            }

            cardImages = new HashMap<>();

            //Load all leader cards graphics
            for(int i = leaderCardFirst; i <= leaderCardLast; i++)
            {
                InputStream imageBinaryStream = getClass().getClassLoader().getResourceAsStream("graphics/cards/leaderCards/" + i + ".png");
                cardImages.put(i, ImageIO.read(imageBinaryStream));
            }

            //Load all dev cards graphics
            for(int i = devCardFirst; i <= devCardLast; i++)
            {
                InputStream imageBinaryStream = getClass().getClassLoader().getResourceAsStream("graphics/cards/devCards/" + i + ".png");
                cardImages.put(i, ImageIO.read(imageBinaryStream));
            }

            loadingComplete = true;
        }
        catch (IOException exception)
        {
            exception.printStackTrace();
            loadingComplete = false;
        }

    }

    /** Helper function to retrieve resource graphics path
     * @param filename Name of the image file
     * @param folderName Name of the folder containing the image file (USE DOTS INSTEAD OF SLASHES)
     * @return InputStream of the image
     */
    private InputStream getImageStream(String filename, String folderName)
    {
        ClassLoader classLoader = getClass().getClassLoader();
        StringBuilder buffer = new StringBuilder();
        buffer.append("graphics/");
        buffer.append(folderName.replace('.', '/'));
        buffer.append("/");
        buffer.append(filename);

        if(!filename.contains(".png") && !filename.contains(".gif")) buffer.append(".png");

        return classLoader.getResourceAsStream(buffer.toString());
    }

    /**
     * Returns the PersonalBoard's image file
     *
     * @param coloured true -> returns coloured version, false -> returns black and white version
     * @return The requested image
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getPersonalBoardImage(boolean coloured) throws ImagesNotLoadedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();
        return coloured ? personalBoardImg : personalBoardBWImg;
    }

    /**
     * Returns the Market Board's image file
     *
     * @return The requested image
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getMarketBoardImage() throws ImagesNotLoadedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();
        return marketBoardImg;
    }


    /**
     * Returns the Card's image file
     *
     * @param id Card id
     * @return The requested image
     * @throws ImagesNotLoadedException    When loadAllGraphics has not yet been successfully called
     * @throws UnregisteredCardIDException When the requested Card ID is not associated with a loaded card
     */
    public Image getCardImage(int id) throws ImagesNotLoadedException, UnregisteredCardIDException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();
        Image retrievedImage = cardImages.get(id);

        if(retrievedImage == null) throw new UnregisteredCardIDException();
        else return retrievedImage;
    }

    /**
     * Returns the Vatican Reports's image file
     *
     * @param index   1, 2 or 3 (Index of the vatican report space)
     * @param enabled Enabled or disabled vatican space?
     * @return The requested image
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getVaticanReportSignal(int index, boolean enabled) throws ImagesNotLoadedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();
        return enabled ? vaticanReportEnabledImages[index - 1] : vaticanReportDisabledImages[index - 1];
    }

    /**
     * Returns the Resource's image file
     *
     * @param resource Requested resource
     * @return The requested image
     * @throws ImagesNotLoadedException          When loadAllGraphics has not yet been successfully called
     * @throws ResourceTypeNotSupportedException When resource type is White / Generic / Faith
     */
    public Image getResourceImage(Resource resource) throws ImagesNotLoadedException, ResourceTypeNotSupportedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();
        int index = -1;

        switch (resource)
        {
            case COIN: index = 0;
                break;
            case SERVANT: index = 1;
                break;
            case SHIELD: index = 2;
                break;
            case STONE: index = 3;
                break;
            default: throw new ResourceTypeNotSupportedException();
        }

        return resourceImages[index];
    }

    /**
     * Returns the Red Cross image file
     *
     * @return The requested image
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getRedCrossImage() throws ImagesNotLoadedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();
        return redCrossImg;
    }

    /**
     * Returns the Black Cross image file
     *
     * @return The requested image
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getBlackCrossImage() throws ImagesNotLoadedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();
        return blackCrossImg;
    }

    /**
     * Returns the Ink Well image file
     *
     * @return The requested image
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getInkWellImage() throws ImagesNotLoadedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();
        return inkWellImg;
    }

    /**
     * Returns the Marble's image file
     *
     * @param resource                   Resource corresponding to the marble colour
     * @param hasWhiteMarbleSubstitution Whether or not the Player has an active WMS Leader Card
     * @return The requested image
     * @throws ImagesNotLoadedException          When loadAllGraphics has not yet been successfully called
     * @throws ResourceTypeNotSupportedException When resource type is Generic
     */
    public Image getMarbleImage(Resource resource, boolean hasWhiteMarbleSubstitution) throws ImagesNotLoadedException, ResourceTypeNotSupportedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();

        int index = -1;

        switch (resource)
        {
            case COIN: index = 0;
                break;
            case SERVANT: index = 1;
                break;
            case SHIELD: index = 2;
                break;
            case STONE: index = 3;
                break;
            case FAITH: index = 4;
                break;
            case WHITE: index = hasWhiteMarbleSubstitution ? 6 : 5;
                break;
            default: throw new ResourceTypeNotSupportedException();
        }

        return marbleImages[index];
    }

    /**
     * Returns the Action Token's image file
     *
     * @param type Action Token Type
     * @return The requested image
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getActionTokenImage(ActionTokenType type) throws ImagesNotLoadedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();

        int index = -1;

        switch (type)
        {
            case DEV_CARD_DISCARD_BLUE: index = 0;
                break;
            case DEV_CARD_DISCARD_GREEN: index = 1;
                break;
            case DEV_CARD_DISCARD_PURPLE: index = 2;
                break;
            case DEV_CARD_DISCARD_YELLOW: index = 3;
                break;
            case ADVANCE_BLACK_CROSS: index = 4;
                break;
            case SHUFFLE_ADVANCE_BLACK_CROSS: index = 5;
                break;
        }

        return actionTokenImages[index];
    }

    /**
     * Returns the Market Arrow image file
     *
     * @param isHorizontal Is the requested Market Arrow the horizontal one?
     * @return The requested image
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getMarketArrowImage(boolean isHorizontal) throws ImagesNotLoadedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();
        return isHorizontal ? horizontalMarketArrowImg : verticalMarketArrowImg;
    }

    /**
     * Returns the Leader Cards' back image file
     *
     * @return The requested image
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getLeaderCardBackImage() throws ImagesNotLoadedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();
        return leaderCardBackImg;
    }

    /**
     * Returns the Marble Board's image file
     *
     * @return The requested image
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getMarbleBoardImg() throws ImagesNotLoadedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();
        return marbleBoardImg;
    }

    /**
     * Returns the welcome panel background image
     *
     * @return The requested image
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getWelcomePanelImg() throws ImagesNotLoadedException{
        if(!loadingComplete) throw new ImagesNotLoadedException();
        return welcomePanelImg;
    }

    /**
     * Returns the welcome panel background gif
     *
     * @return The requested gif
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getWelcomePanelBackgroundTown() throws ImagesNotLoadedException{
        if(!loadingComplete) throw new ImagesNotLoadedException();
        return welcomePanelBackgroundTown;
    }

    /**
     * Returns the Marked Discard Background image
     *
     * @return The requested image
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getMarketDiscardImage() throws ImagesNotLoadedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();
        return marketDiscardImg;
    }

    /**
     * Returns the Crossed Resource image overlay
     *
     * @return The requested image
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getCrossedResourceOverlay() throws ImagesNotLoadedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();
        return crossedResImg;
    }

    /**
     * Returns the Crossed Resource image overlay
     *
     * @return The requested image
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getTickedResourceOverlay() throws ImagesNotLoadedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();
        return tickedResImg;
    }

    /**
     * Returns the Card Slot Selection image
     *
     * @return The requested image
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getCardSlotSelectionImage() throws ImagesNotLoadedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();
        return cardSlotSelectionImage;
    }

    /**
     * Returns the Generic Production Power Dialog Background image
     *
     * @return The requested image
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getGenericProductionDialogBackground()  throws ImagesNotLoadedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();
        return genericProductionDialogBackground;
    }

    /**
     * Returns the Production Resources chest image
     *
     * @return The requested image
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getProductionResourcesChest()  throws ImagesNotLoadedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();
        return productionResourcesChest;
    }

    /**
     * Returns the Singleplayer outcome background image
     *
     * @return The requested image
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getSingleplayerParchment()  throws ImagesNotLoadedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();
        return parchmentSP;
    }

    /**
     * Returns the Multiplayer outcome background image
     *
     * @return The requested image
     * @throws ImagesNotLoadedException When loadAllGraphics has not yet been successfully called
     */
    public Image getMultiplayerParchment()  throws ImagesNotLoadedException
    {
        if(!loadingComplete) throw new ImagesNotLoadedException();
        return parchmentMP;
    }
}
