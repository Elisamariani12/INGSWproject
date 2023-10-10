package it.polimi.ingsw.server.model.gamemanager;

import it.polimi.ingsw.common.model.DevelopmentCard;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.ResourceStack;
import it.polimi.ingsw.common.util.StorageType;
import it.polimi.ingsw.server.exceptions.ResourceNotFoundInPaymentBufferException;

/**
 * The type Development card request.
 */
public class DevelopmentCardRequest extends Transaction{

    private DevelopmentCard item;
    private Resource discountResource1;
    private Resource discountResource2;

    /**
     * Instantiates a new Development card request.
     *
     * @param item the item
     */
    public DevelopmentCardRequest(DevelopmentCard item) {
        this.item = item;
        discountResource1 = Resource.GENERIC;
        discountResource2 = Resource.GENERIC;
    }

    /**
     * Register discount of needed resource to buy new cards. It's given by leaderCard abilities.
     *
     * @param resource the resource
     */
    public void registerDiscount(Resource resource){
        this.discountResource1 = resource;
        this.discountResource2 = Resource.GENERIC;
    }

    /**
     * Register discount of needed resource to buy new cards. It's given by leaderCard abilities. If two discount-ability LeaderCards are placed, player has 2 discount abilities
     *
     * @param resource1 the resource 1
     * @param resource2 the resource 2
     */
    public void registerDiscount(Resource resource1, Resource resource2){
        this.discountResource1 = resource1;
        this.discountResource2 = resource2;
    }

    @Override
    public void addPaymentSource(Resource res, StorageType type) {
        super.addToBuffer(res, type);
    }

    @Override
    public void removePaymentSource(Resource res, StorageType type) throws ResourceNotFoundInPaymentBufferException {
        super.removeFromBuffer(res, type);
    }

    @Override
    public boolean isTransactionValid(){
        for (ResourceStack requiredRes : item.getRequiredResources()){
            for (ResourceStack paidRes : super.resourcesCounter() ){
                //it counts how many occurrences there are for each type
                if(paidRes.getResourceType() == requiredRes.getResourceType()){
                    //if the player has two discounts of the same resource
                    if(paidRes.getResourceType().equals(this.discountResource1) && paidRes.getResourceType().equals(this.discountResource2) && (requiredRes.getAmount() - 2) != paidRes.getAmount() ){
                        return false;
                    }
                    //if the player has just one discount
                    else if(((this.discountResource1.equals(paidRes.getResourceType()) && !this.discountResource2.equals(paidRes.getResourceType())) || (!this.discountResource1.equals(paidRes.getResourceType()) && this.discountResource2.equals(paidRes.getResourceType()))) && (requiredRes.getAmount() - 1) != paidRes.getAmount()){
                        return false;
                    }
                    //if the player has no discount
                    else if(!(this.discountResource1.equals(paidRes.getResourceType()) || this.discountResource2.equals(paidRes.getResourceType())) && requiredRes.getAmount() != paidRes.getAmount()  ){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Returns the Development Card associated to the request
     * @return Development Card
     */
    public DevelopmentCard getDevelopmentCard()
    {
        return item;
    }
}

