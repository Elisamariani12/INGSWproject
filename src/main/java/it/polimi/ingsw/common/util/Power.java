package it.polimi.ingsw.common.util;

/**
 * The type Special ability.
 */
public abstract class Power {

    private SpecialAbility specialAbilityType;


    /**
     * Instantiates a new Power.
     *
     * @param specialAbilityType the special ability type
     */
    public Power(SpecialAbility specialAbilityType) {
        this.specialAbilityType = specialAbilityType;
    }

    /**
     * Gets special ability type.
     *
     * @return the special ability type
     */
    public SpecialAbility getSpecialAbilityType() {
        return specialAbilityType;
    }


    /**
     * Print ability string.
     *
     * @return the ability toString
     */
    public String printAbility() {
        return specialAbilityType.toString();
    }
}
