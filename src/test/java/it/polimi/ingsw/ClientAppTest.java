package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.polimi.ingsw.common.exception.FullResourceStackException;
import it.polimi.ingsw.common.util.ProductionPower;
import it.polimi.ingsw.common.util.Resource;
import it.polimi.ingsw.common.util.ResourceStack;
import it.polimi.ingsw.common.util.SpecialAbility;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * Unit test for simple App.
 */
public class ClientAppTest
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void productionPowerTypeIsNotNull() throws FullResourceStackException {
        ResourceStack rs1, rs2;
        rs1 = new ResourceStack(Resource.COIN, 2, 128);
        rs2 = new ResourceStack(Resource.SERVANT, 4, 128);
        ArrayList<ResourceStack> list1, list2;
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        list1.add(rs1);
        list2.add(rs2);

        ProductionPower power = new ProductionPower(list1, list2);
        assertEquals(power.getSpecialAbilityType(), SpecialAbility.PRODUCTION);
    }
}
