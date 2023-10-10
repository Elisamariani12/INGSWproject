package it.polimi.ingsw.common.model;

import it.polimi.ingsw.common.util.ResourceStack;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * The type Card.
 */
@SuppressWarnings("JavaDoc")
public abstract class Card {

    private int victoryPoints;
    private Set<ResourceStack> resourceRequirements;
    private int ID;

    public Card(int victoryPoints, Set<ResourceStack> resourceRequirements, int cardID) {
        this.victoryPoints = victoryPoints;
        this.resourceRequirements = resourceRequirements;
        this.ID = cardID;
    }

    //just for testing
    public Card(int victoryPoints, Set<ResourceStack> resourceRequirements) {
        this.victoryPoints = victoryPoints;
        this.resourceRequirements = resourceRequirements;
    }

    /**
     * Gets victory points.
     *
     * @return the victory points
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * Get required resources set.
     *
     * @return the set
     */
    public Set<ResourceStack> getRequiredResources(){

        return resourceRequirements.stream().collect(Collectors.toSet());
    }

    public int getCardID() {
        return ID;
    }

    @Override
    public String toString() {
        return "Card{" +"cardID=" + ID +
                ", victoryPoints=" + victoryPoints +
                ", resourceRequirements=" + resourceRequirements +
                '}';
    }
}
