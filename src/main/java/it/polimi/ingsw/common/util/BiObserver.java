package it.polimi.ingsw.common.util;

/**
 * Observer Interface that accept two types of data
 */
public interface BiObserver<T,W> {
    /**
     * Announces that the observable has changed his state
     * @param message1 first object received from the observable
     * @param message2 second object received from the observable
     */
    void update(T message1, W message2);
}
