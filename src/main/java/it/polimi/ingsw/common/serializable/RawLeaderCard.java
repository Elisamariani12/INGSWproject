package it.polimi.ingsw.common.serializable;

import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.util.*;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * Serializable representation of a leader card (only to be used with GSON)
 */
public class RawLeaderCard
{
    private int victoryPoints;
    private LinkedHashSet<ResourceStack> resourceRequirements;
    private List<Triplet<BannerColor,Integer,Integer>> bannerRequirements;
    private SpecialAbility abilityType;
    private Resource resourceType;
    private ProductionPower productionPower;
    private int ID;

    /**
     * Instantiates a serializable leader card with the specified parameters
     * @param victoryPoints Victory Points granted by the card
     * @param resourceRequirements Set of required Resource Stacks
     * @param bannerRequirements List of Pairs representing banner color and amount
     * @param abilityType Type of Special Ability the card grants
     * @param resourceType Resource type associated to the ability (null if none)
     * @param productionPower Production Power if ability is production_power (null if none)
     * @param ID the id of the leaderCard
     */
    public RawLeaderCard(int victoryPoints, LinkedHashSet<ResourceStack> resourceRequirements, List<Triplet<BannerColor,Integer, Integer>> bannerRequirements, SpecialAbility abilityType, Resource resourceType, ProductionPower productionPower, int ID)
    {
        this.victoryPoints = victoryPoints;
        this.resourceRequirements = resourceRequirements;
        this.bannerRequirements = bannerRequirements;
        this.abilityType = abilityType;
        this.resourceType = resourceType;
        this.productionPower = productionPower;
        this.ID = ID;
    }

    /**
     * Generates an actual LeaderCard from this
     * @return LeaderCard from raw data
     */
    public LeaderCard generateLeaderCard()
    {
        Power power;

        if(abilityType == SpecialAbility.PRODUCTION)
            power = productionPower;
        else
            power = new AbilityPower(resourceType, abilityType);

        return new LeaderCard(victoryPoints, resourceRequirements, bannerRequirements, power, ID);
    }
}
