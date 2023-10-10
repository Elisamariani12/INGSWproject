package it.polimi.ingsw.server.model.gamemanager;

import it.polimi.ingsw.server.model.gameplay.PersonalBoard;

/** Represents a player in the game session */
public class Player
{
    private String username;
    private PersonalBoard board;

    /**
     * Creates a new player with the specified username
     * @param username The player's username
     */
    public Player(String username)
    {
        this.username = username;
        board = new PersonalBoard();
    }

    /** Returns the player's chosen username
     * @return Player's username
     */
    public String getUsername()
    {
        return username;
    }

    /** Returns the player's personal board
     * @return Player's board
     */
    public PersonalBoard getBoard()
    {
        return board;
    }
}
