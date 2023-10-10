package it.polimi.ingsw.common.util;

/**
 * Observable Interface that accept two types of data
 */
public interface BiObservable<T,W> {

    /**
     * Add a BiObserver to a list of BiObservers
     * @param biObserver to add to the list
     */
    public void addObserver(BiObserver<T,W> biObserver);

    /**
     * Remove a BiObserver from a list of BiObservers
     * @param biObserver to remove from the list
     */
    public void removeObserver(BiObserver<T,W> biObserver);

    /**
     * Notify the observers and pass 'message1' and 'message2' to them
     * @param message1 first object to pass to the observers
     * @param message2 second object to pass to the observers
     */
    public void notify(T message1,W message2);

}
