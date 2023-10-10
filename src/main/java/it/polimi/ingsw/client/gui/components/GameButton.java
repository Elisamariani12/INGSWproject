package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Special JButton containing an image, flashing upon hovering
 */
public class GameButton extends JButton implements MouseListener
{
    private Image sprite;
    private Image litSprite;
    private Image clickedSprite;

    //Is mouse hovering on the button?
    private boolean isHovering;
    private boolean isClicking;
    /**
     * The Width.
     */
    protected int width, /**
 * The Height.
 */
height;

    /**
     * Creates a new GameButton  @param img the img
     *
     * @param img the background button's image
     * @param width  the width of the GameButton
     * @param height the height of the GameButton
     */
    public GameButton(Image img, int width, int height)
    {
        super();
        this.setGameButtonImage(img);
        this.width = width;
        this.height = height;
        this.setBorderPainted(false);
        this.setOpaque(false);
        this.addMouseListener(this);
        isHovering = false;
        isClicking = false;
        repaint();
    }

    /**
     * Sets the button's image
     *
     * @param img New image for the Game Button to show
     */
    public void setGameButtonImage(Image img)
    {
        this.litSprite = img;
        if(img != null)
        {
            this.sprite = ImageUtils.brightenImage(litSprite, 0.8f);
            this.clickedSprite = ImageUtils.brightenImage(litSprite, 0.6f);
        }
    }

    @Override
    public void paintComponent(Graphics g)
    {
        if(this.litSprite != null)
        {
            Image imgToDraw = isClicking ? clickedSprite : (isHovering ? litSprite : sprite);
            g.drawImage(imgToDraw, 0, 0, width, height, null);
        }
        else
        {
            g.clearRect(0, 0, this.getWidth(), this.getHeight());
        }
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        if(!enabled)
        {
            isClicking = false;
            isHovering = false;
        }

        super.setEnabled(enabled);
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        isHovering = true;
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        isHovering = false;
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        isClicking = true;
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        isClicking = false;
    }

    @Override
    public void mouseClicked(MouseEvent e){}

}
