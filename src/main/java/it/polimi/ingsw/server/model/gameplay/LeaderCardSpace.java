package it.polimi.ingsw.server.model.gameplay;


import it.polimi.ingsw.common.model.LeaderCard;
import it.polimi.ingsw.common.util.*;
import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.server.exceptions.InsufficientResourcesException;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents the space where all Leader cards assigned to a player are placed
 */
public class LeaderCardSpace {
    private Set<Triplet<LeaderCard,LeaderCardStatus,ResourceStack>> givenCards;

    /**
     * Instantiates a new LeaderCardSpace.
     */
    public LeaderCardSpace(){
        givenCards=new HashSet<>();
    }

    /**
     * Returns a set of all the Leader cards currently in use
     *
     * @return Set of active Leader cards
     */
    public Set<LeaderCard> getActiveCards(){
        return givenCards.stream()
                .filter((x)->(x.getSecond()==LeaderCardStatus.IN_USE))
                .map(Triplet::getFirst)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a set of the Leader cards chosen by the user
     *
     * @return Set of chosen Leader cards
     */
    public Set<LeaderCard> getChosenCards(){
        return givenCards.stream()
                .filter((x)->(x.getSecond()==LeaderCardStatus.CHOSEN)||(x.getSecond()==LeaderCardStatus.IN_USE))
                .map(Triplet::getFirst)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a set of the active Leader cards that have storage as special
     * ability and the resources that they contain
     *
     * @return Set of active Leader cards with storage ability and the contained resources
     */
    public Set<Pair<LeaderCard, ResourceStack>> getActiveStorageCards(){
        return givenCards.stream()
                .filter((x) -> (x.getSecond() == (LeaderCardStatus.IN_USE)))
                .filter((x) -> (x.getFirst().getPower().getSpecialAbilityType() == SpecialAbility.STORAGE))
                .map((x)->(x.eliminate2element()))
                .collect(Collectors.toSet());
    }

    /**
     * Returns a set of all the Leader cards assigned to the user
     *
     * @return Set of all Leader cards
     */
    public Set<LeaderCard> getAllCards(){
        return givenCards.stream()
                .map(Triplet::getFirst)
                .collect(Collectors.toSet());
    }

    /**
     * Adds a Leader card to those assigned to the user
     *
     * @param card The leader card to add
     */
    public void addCard(LeaderCard card){
        ResourceStack additionalstorage;
        if(card.getPower() instanceof AbilityPower) {
            //set the type of resources and the maxsize if the new card has storage special ability
            additionalstorage= new ResourceStack(((AbilityPower) card.getPower()).getResourceType(), 2);
        }
        else{
            //if the card has not an additional storage,maxsize=0 and type of the resource=white
            additionalstorage= new ResourceStack(null, 0);
        }
        Triplet<LeaderCard, LeaderCardStatus, ResourceStack> t=new Triplet<>(card,LeaderCardStatus.DISCARDED,additionalstorage);
        givenCards.add(t);
    }

    /**
     * Set the status of a Leader card, among those assigned to the user
     *
     * @param card   The card whose status is to be changed
     * @param status The status to be assigned to the card
     */
    public void setCardStatus(LeaderCard card, LeaderCardStatus status){
        for(Triplet<LeaderCard, LeaderCardStatus, ResourceStack> t:givenCards){
            if(t.getFirst().equals(card)){
                t.setSecond(status);
            }

        }
    }

    /**
     * Returns the resources that are actually contained in the additional card storage
     *
     * @param card The Leader card that contains the resources of interest
     * @return Stack of resources contained in the additional storage
     */
    public ResourceStack ResourcesFromCard(LeaderCard card){
        for(Triplet<LeaderCard, LeaderCardStatus, ResourceStack> triplet:givenCards){
            if(triplet.getFirst().equals(card)){
                return triplet.getThird();
            }
        }
        return null;
    }

    /**
     * Puts resources in the additional storage of the Leader card
     *
     * @param card   The Leader card with the additional storage to fill
     * @param amount resources to put in the card additional storage
     * @throws FullResourceStackException the full resource stack exception
     */
    public void setResourcesIntoCard(LeaderCard card, int amount) throws FullResourceStackException {
        for(Triplet<LeaderCard, LeaderCardStatus, ResourceStack> triplet:givenCards){
            if(triplet.getFirst().equals(card)&&(card.getPower().getSpecialAbilityType()==SpecialAbility.STORAGE))
                triplet.getThird().setAmount((triplet.getThird().getAmount())+amount);
        }
    }

    /**
     * Removes resources from the card's additional storage
     *
     * @param card   the card with storage from which to withdraw resources
     * @param amount resources to be taken from storage
     * @throws InsufficientResourcesException the insufficient resources exception
     */
    public void removeResourcesFromCard(LeaderCard card,int amount) throws InsufficientResourcesException {
        for(Triplet<LeaderCard, LeaderCardStatus, ResourceStack> triplet:givenCards){
            if(triplet.getFirst().equals(card)&&(card.getPower().getSpecialAbilityType()==SpecialAbility.STORAGE)){
                if(amount<=triplet.getThird().getAmount()) {
                    //the exception should not be thrown because the resources are removed, not added
                    try {
                        triplet.getThird().setAmount(triplet.getThird().getAmount() - amount);
                    } catch (FullResourceStackException e) {e.printStackTrace();}
                }
                else
                    throw new InsufficientResourcesException();
            }
        }
    }
}
