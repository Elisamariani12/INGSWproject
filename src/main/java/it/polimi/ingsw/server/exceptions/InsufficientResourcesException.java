package it.polimi.ingsw.server.exceptions;

@SuppressWarnings("JavaDoc")
public class InsufficientResourcesException extends Exception
{
    /**
     * Generic Exception constructor
     */
    public InsufficientResourcesException()
    {
        super();
    }

    /**
     * Exception constructor with message
     * @param message Message associated with the exception
     */
    public InsufficientResourcesException(String message)
    {
        super(message);
    }
}
