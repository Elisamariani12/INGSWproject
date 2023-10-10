package it.polimi.ingsw.client.gui.gamepanels;

import it.polimi.ingsw.client.gui.components.GameButton;
import it.polimi.ingsw.client.util.ImageRepository;
import it.polimi.ingsw.client.util.ImageUtils;
import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.util.CardRepository;

import javax.swing.*;
import java.awt.*;

/**
 * Used to select a card slot to place the Development Card in
 */
@SuppressWarnings("JavaDoc")
public class CardSlotSelectionDialog extends JPanel
{
    private Image background;
    private Image arrow;
    private int[] cardIDs;
    private Image[] cardImages;
    private GameButton[] cardButtons;
    private Image flippedCard;
    private int chosenSlot = -1;

    //Positioning constants
    private final int DIALOG_WIDTH = 616, DIALOG_HEIGHT = 400;
    private final int CARD_WIDTH = 185, CARD_HEIGHT = 279; //ASP RAT 0.6618
    private final int INITIAL_CARD_X = 5;
    private final int CARD_Y = 75;
    private final int CARD_INTERVAL_X = 210;

    //JOptionPanel constants
    private final Object[] options = {"Cancel"};

    /**
     * Creates a new Card slot selection dialog
     *
     * @param cardIDs   IDs of upmost cards on the player's DevCardSpace
     * @param cardLevel Level of the card that we wish to place
     */
    public CardSlotSelectionDialog(int[] cardIDs, int cardLevel)
    {
        super();

        this.background = ImageRepository.getInstance().getCardSlotSelectionImage();
        this.cardIDs = cardIDs;
        this.arrow = ImageUtils.flipVertical(ImageRepository.getInstance().getMarketArrowImage(false));
        this.flippedCard = ImageRepository.getInstance().getLeaderCardBackImage();

        this.cardImages = new Image[3];
        this.cardButtons = new GameButton[3];
        for(int i = 0; i < 3; i++)
        {
            DevelopmentCard cardRef = cardIDs[i] > 0 ? CardRepository.getInstance().getDevCardByID(cardIDs[i]) : null;
            cardImages[i] = cardIDs[i] > 0 ? ImageRepository.getInstance().getCardImage(cardIDs[i]) : flippedCard;
            cardButtons[i] = new GameButton(cardImages[i], CARD_WIDTH, CARD_HEIGHT);
            cardButtons[i].setSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
            cardButtons[i].setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
            cardButtons[i].setBounds(INITIAL_CARD_X + i * CARD_INTERVAL_X, CARD_Y, CARD_WIDTH, CARD_HEIGHT);
            final int index = i;
            cardButtons[i].addActionListener(event -> onPress(index));

            //Only activate buttons where the card can be placed
            boolean canBePlaced = (cardRef == null && cardLevel == 1) || (cardRef != null && cardRef.getCardLevel() == cardLevel - 1);
            cardButtons[i].setEnabled(canBePlaced);

            add(cardButtons[i]);
        }

        this.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        this.setLayout(null);
        this.setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), null);

        //Draw the arrow selecting the card
        if(chosenSlot > 0)
        {
            int posX = INITIAL_CARD_X + (CARD_WIDTH / 2 - 20) + CARD_INTERVAL_X * (chosenSlot - 1);
            g.drawImage(arrow, posX, -40, 50, 109, null);
        }
    }

    /**
     * Action listener that selects the chosen slot
     */
    private void onPress(final int index)
    {
        this.chosenSlot = index + 1;
        repaint();
    }

    /**
     * Returns the slot that was chosen using the dialog @return the chosen slot
     */
    public int getChosenSlot()
    {
        return chosenSlot;
    }
}
