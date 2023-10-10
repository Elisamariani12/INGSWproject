package it.polimi.ingsw.common.util;

/**
 * The type Ability power.
 */
public class AbilityPower extends Power {

    private Resource resourceType;

    /**
     * Instantiates a new Ability power.
     *
     * @param resourceType       the resource type
     * @param specialAbilityType the special ability type
     */
    public AbilityPower(Resource resourceType, SpecialAbility specialAbilityType) {
        super(specialAbilityType);
        this.resourceType = resourceType;
    }

    /**
     * Gets resource type.
     *
     * @return the resource type of generated resources
     */
    public Resource getResourceType() {
        return resourceType;
    }

    @Override
    public String toString() {
        return super.toString() +
                "resourceType=" + resourceType +
                '}';
    }
}
