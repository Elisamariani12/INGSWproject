package it.polimi.ingsw.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbilityPowerTest {

    AbilityPower pow = new AbilityPower(Resource.COIN, SpecialAbility.DISCOUNT);
    @Test
    void getResourceType() {
        assertEquals(pow.getResourceType(), Resource.COIN);
    }

    @Test
    void testToString() {
        pow.toString();
        assert(true);
    }
}
