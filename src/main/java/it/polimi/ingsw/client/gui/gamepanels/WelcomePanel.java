package it.polimi.ingsw.client.gui.gamepanels;

import it.polimi.ingsw.client.util.ImageRepository;
import it.polimi.ingsw.common.util.GameConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GUI welcome animation
 */
public class WelcomePanel extends JPanel {
    private Image background;
    private Image backgroundTown;


    /**
     * Instantiates a new Welcome panel animation.
     */
    public WelcomePanel (){
        background=ImageRepository.getInstance().getWelcomePanelImg();
        backgroundTown=ImageRepository.getInstance().getWelcomePanelBackgroundTown();
    }

    @Override
    public void paintComponent(Graphics g)
    {
            // the header
            g.drawImage(backgroundTown,0,0, GameConstants.WINDOW_WIDTH,GameConstants.WINDOW_HEIGHT,null);
            g.drawImage(background, 190,-50 , 900, 720, null);
    }


}
