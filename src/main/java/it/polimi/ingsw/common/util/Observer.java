package it.polimi.ingsw.common.util;

/**
 * Standard interface for Observer Design Pattern
 */
public interface Observer<T> {
    /**
     * announces that the observable has changed his state
     * @param message object received from the observable
     */
    void update(T message);
}
