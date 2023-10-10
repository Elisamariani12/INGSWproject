package it.polimi.ingsw.common.model;

import it.polimi.ingsw.common.util.ActionTokenType;

/** Represents an in-game Action Token */
public class ActionToken
{
    private ActionTokenType type;

    /**
     * Creates a new action token of the specified type
     * @param type The type of Action Token
     */
    public ActionToken(ActionTokenType type)
    {
        this.type = type;
    }

    /**
     * Returns the type of action token the instance corresponds to
     * @return Type of the action token
     */
    public ActionTokenType getType()
    {
        return type;
    }
}
