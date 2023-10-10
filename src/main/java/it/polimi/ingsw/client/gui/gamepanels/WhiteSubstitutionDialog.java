package it.polimi.ingsw.client.gui.gamepanels;

import it.polimi.ingsw.client.gui.components.GameButton;
import it.polimi.ingsw.client.util.ImageRepository;
import it.polimi.ingsw.client.util.ImageUtils;
import it.polimi.ingsw.common.util.Resource;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.HashMap;

/** Used to ask player for WMS or discard resources*/
public class WhiteSubstitutionDialog extends JPanel implements ChangeListener
{
    private JPanel leftPanel, rightPanel;

    private GameButton[] resourceButtons;
    private JLabel[] resourceLabels;
    private JSpinner[] resourceSpinners;

    private final Resource[] resourceOrder = {Resource.COIN, Resource.SERVANT, Resource.SHIELD, Resource.STONE};

    private int maxAmount;

    /** Creates a new ResourceDialog
     * @param maxAmount Max number of resources to select
     */
    public WhiteSubstitutionDialog(int maxAmount)
    {
        super();
        this.maxAmount = maxAmount;

        leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(2, 2));
        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
        rightPanel.setSize(180, 240);

        resourceButtons = new GameButton[4];
        resourceLabels = new JLabel[4];
        resourceSpinners = new JSpinner[4];

        for(int i = 0; i < 4; i++)
        {
            Image resourceImage = ImageRepository.getInstance().getResourceImage(resourceOrder[i]);
            resourceButtons[i] = new GameButton(resourceImage, 80, 80);
            resourceButtons[i].setPreferredSize(new Dimension(80, 80));
            final int index = i;
            resourceButtons[i].addActionListener(event -> increaseResourceAmount(resourceOrder[index]));
            leftPanel.add(resourceButtons[i]);

            JPanel elementPanel = new JPanel();
            elementPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));

            ImageIcon icon = new ImageIcon(ImageUtils.resizeImage(resourceImage, 30, 30));
            resourceLabels[i] = new JLabel(icon);
            resourceLabels[i].setPreferredSize(new Dimension(30, 30));
            elementPanel.add(resourceLabels[i]);

            resourceSpinners[i] = new JSpinner(new SpinnerNumberModel(0, 0, maxAmount, 1));
            resourceSpinners[i].setPreferredSize(new Dimension(100, 30));
            resourceSpinners[i].addChangeListener(this);
            elementPanel.add(resourceSpinners[i]);
            rightPanel.add(elementPanel);
        }

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    /** Returns the index associated with resource order
     * @param resource Input resource
     * @return Index of the resource type in resourceOrder
     */
    private int getResourceIndex(Resource resource)
    {
        int buffer = -1;

        for(int i = 0; i < 4; i++)
            if(resourceOrder[i] == resource) buffer = i;

        return buffer;
    }

    /** Tries to increase the amount of the requested resource
     * @param resource Input resource
     */
    private void increaseResourceAmount(Resource resource)
    {
        int totalAmount = 0;
        for(JSpinner spinner : resourceSpinners) totalAmount += (int)spinner.getValue();

        int index = getResourceIndex(resource);
        if(index != -1 && totalAmount < maxAmount)
        {
            resourceSpinners[index].setValue((int)resourceSpinners[index].getValue() + 1);
        }
    }

    /** Return values that have been inserted
     * @return Resource -> Amount map
     */
    public HashMap<Resource, Integer> getChoices()
    {
        HashMap<Resource, Integer> buffer = new HashMap<>();
        for(int i = 0; i < 4; i++)
        {
            buffer.put(resourceOrder[i], (int)resourceSpinners[i].getValue());
        }
        return buffer;
    }

    /** Returns whether or not the formulated request contains the correct number of resources
     * @return Has the player formulated a correct request
     */
    public boolean wasChoiceCorrect()
    {
        int totalAmount = 0;
        for(JSpinner spinner : resourceSpinners) totalAmount += (int)spinner.getValue();
        return totalAmount == maxAmount;
    }

    @Override
    public void stateChanged(ChangeEvent e)
    {
        int totalAmount = 0;
        for(JSpinner spinner : resourceSpinners) totalAmount += (int)spinner.getValue();

        //Never allow more resources chosen than available
        if(totalAmount > maxAmount)
        {
            JSpinner caller = (JSpinner)e.getSource();
            int prevAmount = (int)caller.getValue();
            caller.setValue(prevAmount - 1);
        }
    }
}
