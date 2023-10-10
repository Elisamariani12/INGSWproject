package it.polimi.ingsw.client.gui.gamepanels;

import it.polimi.ingsw.client.util.ImageRepository;
import it.polimi.ingsw.client.util.WinningChecker;
import it.polimi.ingsw.common.serializable.CompressedModel;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for a dialog to be shown at the end of the session
 */
public class OutcomeDialog extends JPanel
{
    private Image backgroundImage;
    private WinningChecker winningChecker;
    private final int WIDTH = 525, HEIGHT = 700;
    private JLabel textLabel;

    /**
     * Creates a new OutcomeDialog
     * @param compressedModel the compressed model
     */
    public OutcomeDialog(CompressedModel compressedModel)
    {
        boolean isSingleplayer = compressedModel.getPlayerNames().size() == 1;
        winningChecker = new WinningChecker(compressedModel);

        backgroundImage = isSingleplayer ? ImageRepository.getInstance().getSingleplayerParchment() : ImageRepository.getInstance().getMultiplayerParchment();

        textLabel = new JLabel(winningChecker.getOutcomeString());
        textLabel.setBounds(30, isSingleplayer ? 40 : 100, 480, 500);
        add(textLabel);

        this.setLayout(null);
        this.setSize(WIDTH, HEIGHT);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT, null);
    }
}
