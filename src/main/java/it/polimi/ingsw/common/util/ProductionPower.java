package it.polimi.ingsw.common.util;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Production ability.
 */
public class ProductionPower extends Power
{
    private List<ResourceStack> requirements;
    private List<ResourceStack> reward;

    /**
     * Instantiates a new Production power.
     *
     * @param requirements the requirements of the production power
     * @param reward       the reward of the production power
     */
    public ProductionPower(List<ResourceStack> requirements, List<ResourceStack> reward) {
        super(SpecialAbility.PRODUCTION);
        this.requirements = requirements;
        this.reward = reward;
    }

    /**
     * Gets requirements needed to activate the production.
     *
     * @return the requirements
     */
    public List<ResourceStack> getRequirements() {
        return requirements.stream().map(ResourceStack::copy).collect(Collectors.toList());
    }

    /**
     * Return production rewards .
     *
     * @return the reward
     */
    public List<ResourceStack> getReward() {
        return reward.stream().map(x -> x.copy()).collect(Collectors.toList());
    }

}
