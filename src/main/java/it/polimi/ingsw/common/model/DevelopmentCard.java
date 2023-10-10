package it.polimi.ingsw.common.model;

import it.polimi.ingsw.common.util.BannerColor;
import it.polimi.ingsw.common.util.ProductionPower;
import it.polimi.ingsw.common.util.ResourceStack;

import java.util.Set;

/**
 * The type Development card.
 */
public class DevelopmentCard extends Card{

    private BannerColor bannerColor;
    private int cardLevel;
    private ProductionPower productionPower;

    /**
     * Instantiates a new Development card.
     *
     * @param victoryPoints        the victory points
     * @param resourceRequirements the resource requirements
     * @param bannerColor          the banner color
     * @param cardLevel            the card level
     * @param productionPower      the production power
     * @param cardID               the card id
     */
    public DevelopmentCard(int victoryPoints, Set<ResourceStack> resourceRequirements, BannerColor bannerColor, int cardLevel, ProductionPower productionPower, int cardID) {
        super(victoryPoints, resourceRequirements,cardID);
        this.bannerColor = bannerColor;
        this.cardLevel = cardLevel;
        this.productionPower = productionPower;
    }

    /**
     * Gets banner color.
     *
     * @return the banner color
     */
    public BannerColor getBannerColor() {
        return bannerColor;
    }

    /**
     * Gets card level.
     *
     * @return the card level
     */
    public int getCardLevel() {
        return cardLevel;
    }

    /**
     * Gets production power, it is ok to pass the object directly, ProductionPower is immutable .
     *
     * @return the production power
     */
    public ProductionPower getProductionPower() {
        return productionPower;
    }
}
