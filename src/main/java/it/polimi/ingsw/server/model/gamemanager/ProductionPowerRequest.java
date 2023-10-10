package it.polimi.ingsw.server.model.gamemanager;

import it.polimi.ingsw.common.util.ProductionPower;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.ResourceStack;
import it.polimi.ingsw.common.util.StorageType;
import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.server.exceptions.ResourceNotFoundInPaymentBufferException;

import java.util.List;

/**
 * The type Production power request.
 */
public class ProductionPowerRequest extends Transaction{

    private ProductionPower selectedPower;

    /**
     * Instantiates a new Production power request.
     *
     * @param selectedPower the selected power
     */
    public ProductionPowerRequest(ProductionPower selectedPower) {
        super();
        this.selectedPower = selectedPower;
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
    public boolean isTransactionValid() {
        int genericResourceCount = 0;
        int genericCorrespondence = 0;
        List<ResourceStack> resourceCount = null;
        resourceCount = super.resourcesCounter();
        for (ResourceStack requiredRes : selectedPower.getRequirements()) {
            if (requiredRes.getResourceType() != Resource.GENERIC && requiredRes.getResourceType() != Resource.FAITH && requiredRes.getResourceType() != Resource.WHITE){
                for (ResourceStack paidRes : resourceCount) {
                    if (paidRes.getResourceType() == requiredRes.getResourceType()) {
                        if (paidRes.getAmount() - requiredRes.getAmount() >= 0) {
                            try {
                                paidRes.setAmount(paidRes.getAmount() - requiredRes.getAmount());
                            } catch (FullResourceStackException e) {
                                e.printStackTrace();
                            }
                        } else {
                            return false;
                        }

                    }
                }
            }
            else{
                genericResourceCount = genericResourceCount + requiredRes.getAmount();
            }
        }

        for(ResourceStack paidRes : resourceCount){
            genericCorrespondence = genericCorrespondence + paidRes.getAmount();
        }

        return genericCorrespondence == genericResourceCount;

    }

    /**
     * Returns the production power associated to the request
     * @return Production Power
     */
    public ProductionPower getProductionPower()
    {
        return selectedPower;
    }
}
