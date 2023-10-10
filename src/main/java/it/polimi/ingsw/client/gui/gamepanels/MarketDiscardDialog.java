package it.polimi.ingsw.client.gui.gamepanels;

import it.polimi.ingsw.client.gui.components.ButtonSelector;
import it.polimi.ingsw.client.util.ImageRepository;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.ResourceStack;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Panel to be used as dialog to select what resources to discard and what resources to be kept */
public class MarketDiscardDialog extends JPanel
{
    //Positioning constants
    private static final int RES_SIZE = 48;
    private static final int[] WD_CELL_X = {430, 398, 459, 370, 425, 480};
    private static final int[] WD_CELL_Y = {107, 196, 196, 290, 290, 290};

    //State variables
    private List<ResourceStack> warehouseDepotState;
    private List<Resource> marketLoot;

    //Components
    private JPanel lootPanel;
    private List<ButtonSelector> lootButtons;

    //Buffers
    private Image lv1_img;
    private Image lv2_img;
    private Image lv3_img;
    private int lv1_amt;
    private int lv2_amt;
    private int lv3_amt;
    private Resource lv1_type;
    private Resource lv2_type;
    private Resource lv3_type;

    /** Creates a new MarketDiscardDialog
     * @param warehouseDepotState Current WD state
     * @param marketLoot All selectable resources retrieved from market
     */
    public MarketDiscardDialog(List<ResourceStack> warehouseDepotState, List<Resource> marketLoot)
    {
        this.warehouseDepotState = warehouseDepotState;
        this.marketLoot = marketLoot;

        //WD Data and graphics
        ImageRepository imgRepo = ImageRepository.getInstance();

        lv1_type = this.warehouseDepotState.get(2).getResourceType();
        lv2_type = this.warehouseDepotState.get(1).getResourceType();
        lv3_type = this.warehouseDepotState.get(0).getResourceType();
        lv1_img = (isValidWDResource(lv1_type)) ? imgRepo.getResourceImage(lv1_type) : null;
        lv2_img = (isValidWDResource(lv2_type)) ? imgRepo.getResourceImage(lv2_type) : null;
        lv3_img = (isValidWDResource(lv3_type)) ? imgRepo.getResourceImage(lv3_type) : null;
        lv1_amt = this.warehouseDepotState.get(2).getAmount();
        lv2_amt = this.warehouseDepotState.get(1).getAmount();
        lv3_amt = this.warehouseDepotState.get(0).getAmount();

        //Panel setup
        lootButtons = new ArrayList<>();

        //Instantiate all Buttons
        for(Resource res : marketLoot)
        {
            ButtonSelector resButton = new ButtonSelector(res, RES_SIZE, RES_SIZE, true);
            lootButtons.add(resButton);
        }

        lootPanel = new JPanel();
        lootPanel.setSize(250, 230);
        lootPanel.setLocation(20, 35);
        lootPanel.setOpaque(false);
        lootButtons.forEach(lootPanel::add);
        add(lootPanel);

        this.setSize(600,400);
        this.setPreferredSize(new Dimension(600, 400));
        this.setLayout(null);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.drawImage(ImageRepository.getInstance().getMarketDiscardImage(), 0, 0, null);

        if(lv1_amt > 0 && lv1_type != Resource.GENERIC)
            g.drawImage(lv1_img, WD_CELL_X[0], WD_CELL_Y[0], RES_SIZE, RES_SIZE, null);

        for(int i = 0; i < lv2_amt && lv2_type != Resource.GENERIC; i++)
            g.drawImage(lv2_img, WD_CELL_X[1 + i], WD_CELL_Y[1 + i], RES_SIZE, RES_SIZE, null);

        for(int j = 0; j < lv3_amt && lv3_type != Resource.GENERIC; j++)
            g.drawImage(lv3_img, WD_CELL_X[3 + j], WD_CELL_Y[3 + j], RES_SIZE, RES_SIZE, null);
    }

    /** Returns a list of resources that have been selected by the player
     * @return List of selected resources
     */
    public List<Resource> getSelectedResources()
    {
        return lootButtons.stream()
                .filter(ButtonSelector::isSelected)
                .map(ButtonSelector::getResource)
                .collect(Collectors.toList());
    }

    /** Returns whether or not the input resources could be stored inside WD
     * @param resource Input resource
     * @return Could the resource be stored inside WD?
     */
    private boolean isValidWDResource(Resource resource)
    {
        return resource != null && resource != Resource.GENERIC && resource != Resource.FAITH && resource != Resource.WHITE;
    }

}
