package it.polimi.ingsw.server.model.gamemanager;

import it.polimi.ingsw.common.exception.ResourceTypeNotSupportedException;
import it.polimi.ingsw.common.util.Pair;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.ResourceStack;
import it.polimi.ingsw.common.util.StorageType;
import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.server.exceptions.ResourceNotFoundInPaymentBufferException;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Transaction.
 */
public abstract class Transaction {
    private List<Pair<Resource, StorageType>> paymentBuffer;

    /**
     * Instantiates a new Transaction.
     */
    public Transaction() {
        this.paymentBuffer= new ArrayList<>();
    }

    /**
     * Add payment source.
     *
     * @param res  the single resource selected by the player
     * @param type the type of the storage from where the resource come
     */
    public abstract void addPaymentSource(Resource res, StorageType type);

    /**
     * Remove payment source.
     *
     * @param res  the single resource selected by the player
     * @param type the type of the storage from where the resource was coming
     * @throws ResourceNotFoundInPaymentBufferException the resource not foundin payment buffer
     */
    public abstract void removePaymentSource(Resource res, StorageType type) throws ResourceNotFoundInPaymentBufferException;

    /**
     * Check if transaction is valid, a transaction is valid if paid resources are equals to needed resources
     *
     * @return the boolean
     */
    public abstract boolean isTransactionValid();

    /**
     * Add a resource to the buffer.
     *
     * @param res  the single resource selected by the player
     * @param type the type of the storage from where the resource come
     */
    protected void addToBuffer(Resource res, StorageType type){
        if(res == Resource.FAITH || res == Resource.GENERIC || res == Resource.WHITE){
            throw new ResourceTypeNotSupportedException();
        }
        Pair<Resource, StorageType> bufferMember = new Pair<>(res, type);
        paymentBuffer.add(bufferMember);
    }

    /**
     * Remove a resource from the buffer.
     *
     * @param res  the single resource selected by the player
     * @param type the type of the storage from where the resource come
     * @throws ResourceNotFoundInPaymentBufferException the resource not found in payment buffer
     */
    protected void removeFromBuffer(Resource res, StorageType type) throws ResourceNotFoundInPaymentBufferException {
        Pair<Resource, StorageType> toRemove = null;
        for( Pair<Resource, StorageType> p : paymentBuffer){
            if(p.getFirst().equals(res)){
                if(p.getSecond().equals(type)){
                    toRemove=p;
                }
            }
        }
        if(toRemove == null){
            throw new ResourceNotFoundInPaymentBufferException();
        }
        paymentBuffer.remove(toRemove);
    }

    /**
     * Resources counter list.
     *
     * @return the list of counted resources
     */
    protected List<ResourceStack> resourcesCounter() {
        List<ResourceStack> resourcesCount = new ArrayList<>();
        Resource[] resourceTypes = Resource.values();

        for (Resource r : resourceTypes){
            // used to remove un-meaningful resources
            if(!(r.equals(Resource.GENERIC) || r.equals(Resource.FAITH) || r.equals(Resource.WHITE))){
                resourcesCount.add(new ResourceStack(r));
            }
        }

        for( Pair<Resource, StorageType> couple : paymentBuffer){
            for (ResourceStack resourceStack : resourcesCount) {
                if (resourceStack.getResourceType() == couple.getFirst()) {
                    try {
                        resourceStack.setAmount(resourceStack.getAmount() + 1);
                    } catch (FullResourceStackException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return resourcesCount;
    }
}
