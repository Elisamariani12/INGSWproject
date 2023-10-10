package it.polimi.ingsw.client.gui.gamepanels;

import it.polimi.ingsw.client.util.ImageRepository;
import it.polimi.ingsw.common.util.GameConstants;
import it.polimi.ingsw.common.util.Pair;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.StorageType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The Choose resource production dialog during the Production Phase.
 */
public class ChooseResourceProductionDialog extends JDialog implements WindowListener {
    private Resource inputRes1 = Resource.COIN;
    private Resource inputRes2 = Resource.COIN;
    private StorageType storageTypeInputRes1 = StorageType.WAREHOUSE_DEPOT;
    private StorageType storageTypeInputRes2 = StorageType.WAREHOUSE_DEPOT;
    private Resource outputRes = Resource.COIN;
    private BackgroundPanel backgroundPanel;

    private boolean hasClosed = false;

    /**
     * Instantiates a new Choose resource production dialog.
     *
     * @param isGeneric tell is the player want to activate a generic Power, if false tells that he wants to activate a leaderCard power
     */
    @SuppressWarnings("JavaDoc")
    public ChooseResourceProductionDialog(boolean isGeneric) {
        this.setModalityType(DEFAULT_MODALITY_TYPE);
        backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(null);


        //Handles generic production
        if(isGeneric){
            JComboBox<Resource> inputResource1 = new JComboBox<>();
            JComboBox<Resource> inputResource2 = new JComboBox<>();
            JComboBox<StorageType> storageInputResource1 = new JComboBox<>();
            JComboBox<StorageType> storageInputResource2 = new JComboBox<>();
            JComboBox<Resource> outputResource = new JComboBox<>();


            for(Resource r:Resource.values()){
                if((r!=Resource.FAITH)&&(r!=Resource.WHITE)&&(r!=Resource.GENERIC)) {
                    inputResource1.addItem(r);
                    inputResource2.addItem(r);
                    outputResource.addItem(r);
                }
            }

            for(StorageType stype:StorageType.values()){
                storageInputResource1.addItem(stype);
                storageInputResource2.addItem(stype);
            }

            inputResource1.setBounds(10, 100, 100,20);
            inputResource2.setBounds(120, 100, 100,20);
            storageInputResource1.setBounds(10, 130, 100, 20);
            storageInputResource2.setBounds(120, 130, 100, 20);
            outputResource.setBounds(300, 100, 100,20);

            inputResource1.addActionListener(e -> setResource(1, e));
            inputResource2.addActionListener(e -> setResource(2, e));
            outputResource.addActionListener(e -> setResource(3, e));
            storageInputResource1.addActionListener(e -> setResource(-1, e));
            storageInputResource2.addActionListener(e -> setResource(-2, e));

            JLabel lab1= new JLabel("Select input resources");
            lab1.setBounds(10, 50, 300, 20);
            JLabel lab2= new JLabel("Select output resource");
            lab2.setBounds(300, 50, 300, 20);

            JButton endButton = new JButton("END");
            endButton.addActionListener(e -> {
                if(inputRes1 != null && inputRes2 != null && outputRes != null && storageTypeInputRes1 != null && storageTypeInputRes2 != null){
                    setVisible(false);
                    dispose();
                }
                else{
                    JOptionPane.showMessageDialog(this, GameConstants.GENERIC_ERROR, "Error", JOptionPane.ERROR_MESSAGE);
                }

            });
            endButton.setBounds(200, 200, 100,20);

            backgroundPanel.add(endButton);
            backgroundPanel.add(lab1);
            backgroundPanel.add(lab2);
            backgroundPanel.add(inputResource1);
            backgroundPanel.add(inputResource2);
            backgroundPanel.add(storageInputResource1);
            backgroundPanel.add(storageInputResource2);
            backgroundPanel.add(outputResource);
            this.add(backgroundPanel);

            this.addWindowListener(this);
            this.setSize(500, 300);
            this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        }


        //Handles output leaderCardProduction Resource
        else{
            JComboBox<Resource> outputResource = new JComboBox<>();
            for(Resource r:Resource.values()){
                if((r!=Resource.FAITH)&&(r!=Resource.WHITE)&&(r!=Resource.GENERIC)) {
                    outputResource.addItem(r);
                }
            }

            outputResource.setBounds(180, 100, 100,20);
            outputResource.addActionListener(e -> setResource(3, e));
            JLabel lab2= new JLabel("Select output resource");
            lab2.setBounds(150, 50, 300, 20);

            JButton b = new JButton("END");
            b.addActionListener(e -> {
                if(outputRes != null){
                    setVisible(false);
                    dispose();
                }

            });
            b.setBounds(200, 150, 50,20);

            backgroundPanel.add(b);
            backgroundPanel.add(lab2);
            backgroundPanel.add(outputResource);
            this.add(backgroundPanel);
            this.setSize(500, 300);
        }
    }

    /**
     * Return a results array list used for Generic Power.
     *
     * @return the result array list
     */
    @SuppressWarnings("JavaDoc")
    public ArrayList<Pair<Resource, StorageType>> showResults(){
        setVisible(true);
        ArrayList<Pair<Resource, StorageType>> selectedRes = new ArrayList<>();
        Pair<Resource, StorageType> res1 = new Pair<>(inputRes1, storageTypeInputRes1);
        Pair<Resource, StorageType> res2 = new Pair<>(inputRes2, storageTypeInputRes2);
        Pair<Resource, StorageType> out = new Pair<>(outputRes, null);
        selectedRes.add(res1);
        selectedRes.add(res2);
        selectedRes.add(out);
        return selectedRes;
    }

    /**
     * Return a single results resource, used for leaderCard Production.
     *
     * @return the resource
     */
    @SuppressWarnings("JavaDoc")
    public Resource showSingleResults(){
        setVisible(true);
        return outputRes;
    }

    /**
     * When a resource is selected in the view, it's updated here
     */
    @SuppressWarnings("JavaDoc")
    private void setResource(int index, ActionEvent event){
        JComboBox eventCaller = (JComboBox) event.getSource();
        //-1 and -2 refers to res1 and res2 storageType, 1 and 2 to res1 and res2 Resources, and 3 to outputResource
        try{
            if(index == -1){
                storageTypeInputRes1 = (StorageType) eventCaller.getSelectedItem();
            }
            if(index == -2){
                storageTypeInputRes2 = (StorageType) eventCaller.getSelectedItem();
            }
            if(index == 1){
                inputRes1 = (Resource) eventCaller.getSelectedItem();
            }
            if(index == 2){
                inputRes2 = (Resource) eventCaller.getSelectedItem();
            }
            if(index == 3){
                outputRes = (Resource) eventCaller.getSelectedItem();
            }
        }
        catch( ClassCastException ignored){}

    }

    /**
     * Returns whether or not the Dialog has been closed
     *
     * @return Has the dialog been closed?
     */
    public boolean hasBeenClosed()
    {
        return hasClosed;
    }

    /**
     * Make banner if the player has selected some resources that he doesn't own.
     */
    @SuppressWarnings("JavaDoc")
    public void makeBannerWrongResources(){

        JButton wrongResourcesButton= new JButton("You do not have enough resources in your storages, try again.");
        wrongResourcesButton.setBackground(Color.RED);
        wrongResourcesButton.setBounds(20, 10, 400, 20);
        for(MouseListener mouseListener:wrongResourcesButton.getMouseListeners())wrongResourcesButton.removeMouseListener(mouseListener);
        backgroundPanel.add(wrongResourcesButton);

        //remove it after 3 seconds
        Timer timer=new Timer();
        TimerTask timerTask= new TimerTask() {
            @Override
            public void run() {
                backgroundPanel.remove(wrongResourcesButton);
                repaint();
            }
        };
        timer.schedule(timerTask,6000);
    }

    /**
     * The Background panel.
     */
    static class BackgroundPanel extends JPanel
    {
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            g.drawImage(ImageRepository.getInstance().getGenericProductionDialogBackground(), 0,0,this.getWidth(),this.getHeight(),this);
        }
    }

    @Override
    public void windowClosing(WindowEvent e) { hasClosed = true; }

    /* ----------- Unused Events ----------- */

    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) { }

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}


}
