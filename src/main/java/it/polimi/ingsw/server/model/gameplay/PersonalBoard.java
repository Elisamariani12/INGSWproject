package it.polimi.ingsw.server.model.gameplay;

import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.SpecialAbility;

/**
 * represents the personal board of the player.
 */
public class PersonalBoard {
    private WarehouseDepot warehouseDepot;
    private StrongBox strongBox;
    private FaithTrack faithTrack;
    private LeaderCardSpace leaderCardSpace;
    private DevelopmentCardSpace developmentCardSpace;
    private ProductionPowerRegistry productionPowerRegistry;

    /**
     * Instantiates a new Personal board initialising the private variables
     */
    public PersonalBoard() {
        warehouseDepot=new WarehouseDepot();
        strongBox=new StrongBox();
        faithTrack=new FaithTrack();
        leaderCardSpace=new LeaderCardSpace();
        developmentCardSpace=new DevelopmentCardSpace();
        productionPowerRegistry=new ProductionPowerRegistry();
    }

    /**
     * Calculates the total amount of resource of type 'resource' in all the storages
     *
     * @param resource type of the resource whose quantity is requested
     * @return total amount of the requested resource
     */
    public int getTotalStoredAmount(Resource resource){
        int resourcetotal=0;

        //sum the resources in the strongbox
        switch(resource){
            case STONE:
                resourcetotal+=strongBox.getResourceAmount(Resource.STONE);break;
            case SERVANT:
                resourcetotal+=strongBox.getResourceAmount(Resource.SERVANT);break;
            case COIN:
                resourcetotal+=strongBox.getResourceAmount(Resource.COIN);break;
            case SHIELD:
                resourcetotal+=strongBox.getResourceAmount(Resource.SHIELD);break;
        }

        //sum the resources in the warehouse in all the layers
        resourcetotal+=warehouseDepot.getAmountByResource(resource);

        //add the resources into the possible additional storages of the leadercards
        resourcetotal+=leaderCardSpace.getActiveStorageCards().stream()
                        .filter((x)->(x.getFirst().getPower().getSpecialAbilityType()== SpecialAbility.STORAGE))
                        .filter((x)->(x.getSecond().getResourceType()==resource))
                        .mapToInt((x)->(x.getSecond().getAmount()))
                        .sum();

        return resourcetotal;
    }

    /**
     * Gets warehouse depot.
     *
     * @return the warehouse depot
     */
    public WarehouseDepot getWarehouseDepot() {
        return warehouseDepot;
    }

    /**
     * Gets faith track.
     *
     * @return the faith track
     */
    public FaithTrack getFaithTrack() {
        return faithTrack;
    }

    /**
     * Gets production power registry.
     *
     * @return the production power registry
     */
    public ProductionPowerRegistry getProductionPowerRegistry() {
        return productionPowerRegistry;
    }

    /**
     * Gets leader card space.
     *
     * @return the leader card space
     */
    public LeaderCardSpace getLeaderCardSpace() {
        return leaderCardSpace;
    }

    /**
     * Gets development card space.
     *
     * @return the development card space
     */
    public DevelopmentCardSpace getDevelopmentCardSpace() {
        return developmentCardSpace;
    }

    /**
     * Gets strong box.
     *
     * @return the strong box
     */
    public StrongBox getStrongBox() {
        return strongBox;
    }
}
