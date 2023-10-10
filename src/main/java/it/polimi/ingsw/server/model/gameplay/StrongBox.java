package it.polimi.ingsw.server.model.gameplay;

import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.ResourceStack;
import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.server.exceptions.InsufficientResourcesException;

/**
 * represents the strongbox of resources, from which they can be taken only for productions
 */
public class StrongBox {
    private ResourceStack coinStack;
    private ResourceStack stoneStack;
    private ResourceStack servantStack;
    private ResourceStack shieldStack;

    /**
     * Instantiates a new Strong box.
     */
    public StrongBox(){
        coinStack=new ResourceStack(Resource.COIN,Integer.MAX_VALUE-1);
        stoneStack=new ResourceStack(Resource.STONE,Integer.MAX_VALUE-1);
        servantStack=new ResourceStack(Resource.SERVANT,Integer.MAX_VALUE-1);
        shieldStack=new ResourceStack(Resource.SHIELD,Integer.MAX_VALUE-1);
    }

    /**
     * Returns the amount of resources of the type 'r' contained in the strong box
     *
     * @param r type of resource whose quantity is requested
     * @return amount of 'r' resources contained
     */
    public int getResourceAmount(Resource r){
        switch (r){
            case COIN:
                return coinStack.getAmount();
            case STONE:
                return stoneStack.getAmount();
            case SERVANT:
                return servantStack.getAmount();
            case SHIELD:
                return shieldStack.getAmount();
            default:
                return 0;
        }
    }

    /**
     * Sets the amount of resources of the type 'r' contained in the Strong Box
     *
     * @param r      type of resource whose quantity is set
     * @param amount amount of resources of type 'r' set
     */
    public void setResourceAmount(Resource r,int amount) {
        //to have 100% coverage, don't replace with "ignored"
        switch (r){
            case COIN:
                try{coinStack.setAmount(amount);}
                catch (FullResourceStackException e){e.printStackTrace();}
                break;
            case STONE:
                try{stoneStack.setAmount(amount);}
                catch (FullResourceStackException e){e.printStackTrace();}
                break;
            case SERVANT:
                try{servantStack.setAmount(amount);}
                catch (FullResourceStackException e){e.printStackTrace();}
                break;
            case SHIELD:
                try{shieldStack.setAmount(amount);}
                catch (FullResourceStackException e){e.printStackTrace();}
                break;
        }
    }

    /**
     * Increase by 'amount' the quantity of resources of the type 'r' contained in the Strong Box
     *
     * @param r      type of resource whose quantity is increased
     * @param amount amount added resources
     */
    public void addResourceAmount(Resource r,int amount) {
        switch (r){
            case COIN:
                try{coinStack.setAmount(amount+ (coinStack.getAmount()));}
                catch(FullResourceStackException e){e.printStackTrace();}
                break;
            case STONE:
                try{stoneStack.setAmount(amount+ (stoneStack.getAmount()));}
                catch(FullResourceStackException e){e.printStackTrace();}
                break;
            case SERVANT:
                try{servantStack.setAmount(amount+ (servantStack.getAmount()));}
                catch(FullResourceStackException e){e.printStackTrace();}
                break;
            case SHIELD:
                try{shieldStack.setAmount(amount+ (shieldStack.getAmount()));}
                catch(FullResourceStackException e){e.printStackTrace();}
                break;
        }
    }

    /**
     * Decreases by 'amount' the quantity of resources of the type 'r' contained in the Strong Box
     *
     * @param r      type of resource whose quantity is decreased
     * @param amount amount of removed resources
     * @throws InsufficientResourcesException the insufficient resources exception
     */
    public void removeResourceAmount(Resource r,int amount) throws InsufficientResourcesException {
        //if the resources in the strong box are enough
        switch (r){
            case COIN:
                if(amount> coinStack.getAmount()) { throw new InsufficientResourcesException(); }
                else {
                    try{coinStack.setAmount(coinStack.getAmount()-amount);}
                    catch(FullResourceStackException e){e.printStackTrace();}
                    break;
                }
            case STONE:
                if(amount> stoneStack.getAmount())
                    throw new InsufficientResourcesException();
                else {
                    try{stoneStack.setAmount(stoneStack.getAmount()-amount);}
                    catch(FullResourceStackException e){e.printStackTrace();}
                    break;
                }
            case SERVANT:
                if(amount> servantStack.getAmount())
                    throw new InsufficientResourcesException();
                else {
                    try{servantStack.setAmount(servantStack.getAmount()-amount);}
                    catch(FullResourceStackException e){e.printStackTrace();}
                }break;
            case SHIELD:
                if(amount> shieldStack.getAmount())
                    throw new InsufficientResourcesException();
                else{
                    try { shieldStack.setAmount(shieldStack.getAmount()-amount); }
                    catch (FullResourceStackException e){e.printStackTrace();}
                }break;
        }
    }
}
