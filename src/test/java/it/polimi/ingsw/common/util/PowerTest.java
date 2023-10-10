package it.polimi.ingsw.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PowerTest {
    Resource resource = Resource.COIN;
    SpecialAbility specialAbilityType = SpecialAbility.DISCOUNT;
    AbilityPower abilityPower = new AbilityPower(resource, specialAbilityType);

    @Test
    void getSpecialAbilityType() {
        assertEquals(abilityPower.getSpecialAbilityType(), SpecialAbility.DISCOUNT);
    }


    @Test
    void testToString() {
        abilityPower.toString();
    }
}