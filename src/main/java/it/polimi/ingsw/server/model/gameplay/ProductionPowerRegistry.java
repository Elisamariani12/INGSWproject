package it.polimi.ingsw.server.model.gameplay;

import it.polimi.ingsw.common.util.GameConstants;
import it.polimi.ingsw.common.util.ProductionPower;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.ResourceStack;
import it.polimi.ingsw.common.exception.FullResourceStackException;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Production power registry: it's a registry that includes all the active production powers for a specific player .
 */
public class ProductionPowerRegistry {

    private ArrayList<ProductionPower> activatedProductionPowers;
    private ArrayList<Integer> alreadyUsedInActualTurnIDs;


    /**
     * Instantiates a new Production power registry.
     */
    public ProductionPowerRegistry(){
        activatedProductionPowers = new ArrayList<>();

        //INITIALIZE GENERIC PRODUCTION
        ResourceStack inputGenericProduction=new ResourceStack();
        inputGenericProduction.setType(Resource.GENERIC);
        ResourceStack outputGenericProduction=new ResourceStack();
        outputGenericProduction.setType(Resource.GENERIC);
        try {
            inputGenericProduction.setMaxSize(GameConstants.NUM_OF_INPUTS_GENERIC_PRODUCTION);
            inputGenericProduction.setAmount(GameConstants.NUM_OF_INPUTS_GENERIC_PRODUCTION);
            outputGenericProduction.setMaxSize(GameConstants.NUM_OF_OUTPUTS_GENERIC_PRODUCTION);
            outputGenericProduction.setAmount(GameConstants.NUM_OF_OUTPUTS_GENERIC_PRODUCTION);
        } catch (FullResourceStackException e) {
            e.printStackTrace();
        }
        List<ResourceStack> inputsGenericProduction=new ArrayList<>(); inputsGenericProduction.add(inputGenericProduction);
        List<ResourceStack> outputsGenericProduction=new ArrayList<>(); outputsGenericProduction.add(outputGenericProduction);
        ProductionPower generic=new ProductionPower(inputsGenericProduction,outputsGenericProduction);
        registerActivatedProductionPower(generic);

        alreadyUsedInActualTurnIDs = new ArrayList<>();
    }

    /**
     * Gets activated production power.
     *
     * @return the activated production power
     */
    public ArrayList<ProductionPower> getActivatedProductionPowers() { return activatedProductionPowers;}

    /**
     * Register a new active production power.
     *
     * @param power the power
     */
    public void registerActivatedProductionPower(ProductionPower power) {activatedProductionPowers.add(power); }

    /**
     * Remove a previously activated production power.
     *
     * @param power the power
     */
    public void removeActivatedProductionPower(ProductionPower power) { activatedProductionPowers.remove(power); }

    /**
     * Gets already used in actual turn  card IDs.
     *
     * @return the already used in actual turn Card IDs, -1 stands for Generic Power
     */
    public ArrayList<Integer> getAlreadyUsedInActualTurnIDs() {
        return (ArrayList<Integer>) new ArrayList<>(alreadyUsedInActualTurnIDs);
    }

    /**
     * Add id to already used CardId list.
     *
     * @param ID the id, -1 stands for Generic Power
     */
    public void addIDtoAlreadyUsed(int ID){
        alreadyUsedInActualTurnIDs.add(ID);
    }

    /**
     * Clear already used card IDs list.
     */
    public void clearAlreadyUsed(){
        alreadyUsedInActualTurnIDs.clear();
    }
}

