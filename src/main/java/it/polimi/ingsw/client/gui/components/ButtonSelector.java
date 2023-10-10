package it.polimi.ingsw.client.gui.components;

import it.polimi.ingsw.client.util.ImageRepository;
import it.polimi.ingsw.common.util.Resource;

import java.awt.*;
import java.awt.event.MouseEvent;

/** Selectable and deselectable GameButton */
public class ButtonSelector extends GameButton
{
    private boolean isSelected;
    private Resource resource;

    /** Creates a new ButtonSelector
     * @param resource Resource associated to the button
     * @param width Button width
     * @param height Button height
     * @param isSelected Initial state
     */
    public ButtonSelector(Resource resource, int width, int height, boolean isSelected)
    {
        super(ImageRepository.getInstance().getResourceImage(resource), width, height);
        this.setSize(new Dimension(width, height));
        this.setPreferredSize(new Dimension(width, height));
        this.isSelected = isSelected;
        this.resource = resource;
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Image crossed = ImageRepository.getInstance().getCrossedResourceOverlay();
        Image ticked = ImageRepository.getInstance().getTickedResourceOverlay();

        g.drawImage(isSelected ? ticked : crossed, 0, 0, width, height, null);
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        super.mouseReleased(e);
        isSelected = !isSelected;
    }

    /** Returns whether or not the button has been selected
     * @return Has the button been selected?
     */
    public boolean isSelected()
    {
        return isSelected;
    }

    /** Returns the resource associated to the button
     * @return Resource associated to the button
     */
    public Resource getResource()
    {
        return resource;
    }
}
