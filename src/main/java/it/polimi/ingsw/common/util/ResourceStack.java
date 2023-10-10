package it.polimi.ingsw.common.util;

import it.polimi.ingsw.common.exception.FullResourceStackException;

/**
 * Mutable class used to create Resource Stacks and track their fullness.
 */
public class ResourceStack {

    private Resource type;
    private int amount;
    private int maxSize;

    /**
     * Instantiates a new Resource stack by type, amount, maxSize.
     *
     * @param type    the type
     * @param amount  the amount
     * @param maxSize the max size
     * @throws FullResourceStackException if the resource stack is full
     */
    public ResourceStack(Resource type, int amount, int maxSize) throws FullResourceStackException {
        if(amount > maxSize){
            throw new FullResourceStackException();
        }
        this.type = type;
        this.amount = amount;
        this.maxSize = maxSize;
    }

    /**
     * Instantiates a new Resource stack by type and maxSize, amount is set to zero.
     *
     * @param type    the type
     * @param maxSize the max size
     */
    public ResourceStack(Resource type, int maxSize) {
        this.type = type;
        this.amount = 0;
        this.maxSize = maxSize;
    }

    /**
     * Instantiates a new Resource stack only by type, aomunt is set to zero, maxSize to the highest value.
     *
     * @param type the type
     */
    public ResourceStack(Resource type) {
        this.type = type;
        this.amount = 0;
        this.maxSize = Integer.MAX_VALUE;
    }

    /**
     * Instantiates a new Resource stack with no parameters given: amount and MaxSize are set to zero and type of the Resource to null
     */
    public ResourceStack() {
        this.amount = 0;
        this.maxSize = Integer.MAX_VALUE;
        this.type=null;
    }

    /**
     * Gets resource type.
     *
     * @return the resource type
     */
    public Resource getResourceType() {
        return type;
    }

    /**
     * Gets amount.
     *
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Gets max size.
     *
     * @return the max size
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Sets type.
     *
     * @param type the Resource type
     */
    public void setType(Resource type) {
        this.type = type;
    }

    /**
     * Sets amount.
     *
     * @param amount the amount
     * @throws FullResourceStackException the full resource stack exception
     */
    public void setAmount(int amount) throws FullResourceStackException {
        if (amount > maxSize || amount < 0){
            throw new FullResourceStackException();
        }
        this.amount = amount;
    }

    /**
     * Sets max size.
     *
     * @param maxSize the max size
     * @throws FullResourceStackException if the resourceStack is full
     */
    public void setMaxSize(int maxSize) throws FullResourceStackException{
        if(this.getAmount()>maxSize){
            throw new FullResourceStackException();
        }
        this.maxSize = maxSize;
    }

    /**
     * Copy resource stack.
     *
     * @return the resource stack
     */
    public ResourceStack copy(){
        try {
            return new ResourceStack(this.type, this.amount, this.maxSize);
        } catch (FullResourceStackException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "ResourceStack{" +
                "type=" + type +
                ", amount=" + amount +
                ", maxSize=" + maxSize +
                '}';
    }
}


