package it.polimi.ingsw.server.model.gameplay;

import it.polimi.ingsw.common.util.GameConstants;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.ResourceStack;
import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.server.exceptions.ResourceNotInsertableException;

/**
 * represents the stock of resources, from which they can be taken for the purchase of development cards or for productions
 */
public class WarehouseDepot {

    private ResourceStack[] resourceStorage;

    /**
     * Each cell of the array represents a layer of the warehouse
     * (resourcestack since in any case it cannot contain more than one type of resource).
     * The cell 0-->FIRST LAYER, the cell 1-->SECOND LAYER,...
     */
    public WarehouseDepot() {
        resourceStorage=new ResourceStack[GameConstants.WAREHOUSE_DEPOT_NUMBER_OF_LAYERS];
        //creates the resource stacks one by one
        for(int i=0;i<GameConstants.WAREHOUSE_DEPOT_NUMBER_OF_LAYERS;i++){
            resourceStorage[i]=new ResourceStack();
            resourceStorage[i].setType(null);
        }
    }

    /**
     * Set the maximum capacity for each layer of the warehouse
     */
    public void SetLayerMaxAmount() {
        for(int i=0;i<GameConstants.WAREHOUSE_DEPOT_NUMBER_OF_LAYERS;i++){
            //the exception should not be thrown because the maximum capacity of the layers is set at the beginning of the game
            try{resourceStorage[i].setMaxSize(GameConstants.WAREHOUSE_DEPOT_CAPACITY_OF_LAYERS[i]);}
            catch(FullResourceStackException e){e.printStackTrace();}
        }
    }

    /**
     * Returns the type of resource inserted in layer number(1,2,3)
     * @param layer index of the layer whose content type is requested
     * @return type of resource contained in the layer
     */
    public Resource getLayerType(int layer){
        return resourceStorage[layer-1].getResourceType();
    }

    /**
     * Returns the amount of resources contained in the layer number 'layer'
     * @param layer index of the layer whose content amount is requested : 1,2,3
     * @return amount of resources in the layer
     */
    public int getLayerAmount(int layer){
        return resourceStorage[layer-1].getAmount();
    }

    /**
     * Sets the type of resource that layer number 'layer' can contain
     * @param layer index of the layer whose content type we want to set
     * @param Type resource type set for the layer
     */
    public void setLayerType(int layer,Resource Type){
        resourceStorage[layer-1].setType(Type);
    }

    /**
     * Sets the amount of resources that layer number 'layer' contains
     * @param layer index of the layer whose content type we want to set
     * @param amount amount of resources put into the layer
     * @throws FullResourceStackException thrown when the resource stack is full
     */
    public void setLayerAmount(int layer,int amount) throws FullResourceStackException {
        if(amount <= resourceStorage[layer-1].getMaxSize()) {
            resourceStorage[layer - 1].setAmount(amount);
            if(amount==0)resourceStorage[layer-1].setType(null);
        }
        else
            throw new FullResourceStackException();
    }

    /**
     * Given the resource type it returns the quantity in the warehouse depot
     * @param Type type of the resource whose quantity is requested
     * @return quantity of the requested resource
     */
    public int getAmountByResource(Resource Type){
        for(int i=0;i<GameConstants.WAREHOUSE_DEPOT_NUMBER_OF_LAYERS;i++){
            if(resourceStorage[i].getResourceType()==Type)return resourceStorage[i].getAmount();
        }
        return 0;
    }

    /**
     * Try to insert in the warehouse depot the resource 'resource'. If it can not fit throws the exception 'ResourceNotInsertableException'
     * @param resource resource to insert in the warehouse depot
     * @throws ResourceNotInsertableException if the resource can not fit in the warehouse depot
     */
    public void InsertResource(Resource resource) throws ResourceNotInsertableException {
        int layerOfTypeR=-1;
        int emptylayer=-1;

        //check if there are empty layers or layers with some resources of the same type of resource
        for(int i=GameConstants.WAREHOUSE_DEPOT_NUMBER_OF_LAYERS;i>0;i--){
            if((getLayerType(i)==resource)&&(getLayerAmount(i)!=0))layerOfTypeR=i;
                else if((getLayerType(i)==null)||(getLayerAmount(i)==0))emptylayer=i;
        }

        //if there are no empty layers and no layers of the same type as resource
        if((layerOfTypeR==-1)&&(emptylayer==-1)){
            throw new ResourceNotInsertableException();
        }

        //if there is already a layer of a resource check if it is full or not
        if(layerOfTypeR!=-1){
            //if it is full
            if(getLayerAmount(layerOfTypeR)==GameConstants.WAREHOUSE_DEPOT_CAPACITY_OF_LAYERS[layerOfTypeR-1]){
                //we have to check if there are other bigger layers (not full) to switch the stacks
                if(layerOfTypeR==1){//if there are not bigger stacks
                    throw new ResourceNotInsertableException();
                }
                else{
                    boolean switchedStacks=false;

                    for(int layer=layerOfTypeR-1;layer>0;layer--){
                        //check if they are not full
                        if((getLayerAmount(layer)<GameConstants.WAREHOUSE_DEPOT_CAPACITY_OF_LAYERS[layer-1])&&(!switchedStacks)){
                            if(getLayerAmount(layer)<=getLayerAmount(layerOfTypeR)){
                                Resource tempR=getLayerType(layerOfTypeR);
                                int tempAmount=getLayerAmount(layerOfTypeR);

                                try {setLayerAmount(layerOfTypeR,getLayerAmount(layer));} catch (FullResourceStackException e) {e.printStackTrace();}
                                setLayerType(layerOfTypeR,getLayerType(layer));

                                try{setLayerAmount(layer,tempAmount+1);}catch (FullResourceStackException e){e.printStackTrace();}
                                setLayerType(layer,tempR);
                                switchedStacks=true;
                            }
                        }
                    }

                    if(!switchedStacks){
                        throw new ResourceNotInsertableException();
                    }

                }
            }
            //layer not full
            else{
                try {setLayerAmount(layerOfTypeR,getLayerAmount(layerOfTypeR)+1);}
                catch (FullResourceStackException e) {e.printStackTrace();}
            }
        }
        else {
            try {
                setLayerAmount(emptylayer,1);
                setLayerType(emptylayer,resource);
            }
            //this should not happen
            catch (FullResourceStackException e) {e.printStackTrace();}
        }

    }
}
