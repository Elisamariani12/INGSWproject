package it.polimi.ingsw.common.util;

/**
 * Standard interface for Observable Design Pattern
 */
public interface Observable<T> {

    /**
     * Add an observer to a list of observers
     * @param observer observer to add to the list
     */
    public void addObserver(Observer<T> observer);

    /**
     * Remove an observer from a list of observers
     * @param observer observer to remove from the list
     */
    public void removeObserver(Observer<T> observer);

    /**
     * Notify the observers and pass 'message' to them
     * @param message the object to pass to the observers
     */
    public void notify(T message);

}
