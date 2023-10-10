package it.polimi.ingsw.common.model;

import it.polimi.ingsw.common.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * The type Leader card.
 */
public class LeaderCard extends Card {
    //BannerColor - Level - Amount
    private List<Triplet<BannerColor,Integer,Integer>> bannerRequirements;
    private Power power;

    /**
     * Instantiates a new Leader card.
     *
     * @param victoryPoints        the victory points
     * @param resourceRequirements the resource requirements
     * @param bannerRequirements   the banner requirements
     * @param power                the power
     * @param cardID               the card id
     */
    public LeaderCard(int victoryPoints, Set<ResourceStack> resourceRequirements, List<Triplet<BannerColor,Integer,Integer>> bannerRequirements, Power power, int cardID) {
        super(victoryPoints, resourceRequirements, cardID);
        this.bannerRequirements = bannerRequirements;
        this.power = power;
    }

    /**
     * Gets banner requirements.
     *
     * @return the banner requirements
     */
    public List<Triplet<BannerColor,Integer, Integer>> getBannerRequirements() {
        List<Triplet<BannerColor,Integer,Integer>> returned = new ArrayList<>();
        for(Triplet<BannerColor,Integer,Integer> triplet: bannerRequirements){
            Triplet<BannerColor,Integer,Integer> tripletToCopy=new Triplet<>(triplet.getFirst(),triplet.getSecond(),triplet.getThird());
            returned.add(tripletToCopy);
        }
        return returned;
    }

    /**
     * Gets power, it is ok to pass the object directly, Power is immutable
     *
     * @return the power
     */
    public Power getPower() {
        return power;
    }

    @Override
    public String toString() {
        return  super.toString() +
                "LeaderCard{" +
                "bannerRequirements=" + bannerRequirements +
                ", power=" + power +
                '}';
    }

    //############################ METHODS USED IN CLI ######################################
    /** Returns a complete string with all Leader card requirements
     * @return Complete intelligible string of requirements
     */
    public String getRequirementString()
    {
        StringBuilder buffer = new StringBuilder();
        //Banner Requirements
        for(Triplet<BannerColor, Integer, Integer> req : this.getBannerRequirements())
        {
            BannerColor color = req.getFirst();
            int level = req.getSecond();
            int amount = req.getThird();
            buffer.append(getBannerRequirementsString(color, level, amount) + '\n');
        }
        //Resource requirements
        for(ResourceStack stack : this.getRequiredResources())
        {
            buffer.append(getResourceRequirementString(stack) + '\n');
        }
        //Remove last \n
        buffer.deleteCharAt(buffer.length() - 1);
        return buffer.toString();
    }

    /** Returns an intelligible string corresponding to the resource requirement
     * @param stack Resource stack
     * @return Intelligible string corresponding to the resource stack
     */
    private String getResourceRequirementString(ResourceStack stack)
    {
        return stack.getAmount() + "x" + stack.getResourceType().toString();
    }

    /** Returns an intelligible string corresponding to the banner color and amount
     * @param color Required banner color
     * @param level Banner level
     * @param amount Amount of banners required
     * @return Intelligible string corresponding to the color-amount couple
     */
    private String getBannerRequirementsString(BannerColor color, int level, int amount)
    {
        return amount + "x" + color.toString() + " lv." + level;
    }

    /** Used to get leader card type as an intelligible string
     * @return Intelligible string corresponding to the Special Ability type
     */
    public String getTypeString()
    {
        SpecialAbility ability=this.power.getSpecialAbilityType();
        switch (ability)
        {
            case STORAGE:
                return "Storage";
            case DISCOUNT:
                return "Discount";
            case PRODUCTION:
                return "Prod.Pow.";
            case WHITE_MARBLE_SUBSTITUTION:
                return "Marb.Sub.";
            default:
                return "MISSING!";
        }
    }

    /** Returns an intelligible string corresponding to the card's power
     * @return Intelligible string corresponding to the card's power
     */
    public String getPowerString()
    {
        switch (this.getPower().getSpecialAbilityType())
        {
            case STORAGE: return "2x" + ((AbilityPower)this.getPower()).getResourceType();
            case PRODUCTION:
                ProductionPower prod = (ProductionPower) this.getPower();
                StringBuilder buffer = new StringBuilder();
                for(ResourceStack stack : prod.getRequirements())
                {
                    buffer.append(getResourceRequirementString(stack) + '\n');
                }
                //Remove last \n
                buffer.deleteCharAt(buffer.length() - 1);
                buffer.append("=>\n");
                for(ResourceStack stack : prod.getReward())
                {
                    buffer.append(getResourceRequirementString(stack) + '\n');
                }
                //Remove last \n
                buffer.deleteCharAt(buffer.length() - 1);
                return buffer.toString();
            case DISCOUNT: return "-1x" + ((AbilityPower)this.getPower()).getResourceType();
            case WHITE_MARBLE_SUBSTITUTION: return "-W = " + ((AbilityPower)this.getPower()).getResourceType();
            default: return null;
        }
    }

}

