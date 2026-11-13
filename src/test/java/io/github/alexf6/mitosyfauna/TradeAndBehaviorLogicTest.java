package io.github.alexf6.mitosyfauna;

import io.github.alexf6.mitosyfauna.logic.EconomyLogic;
import io.github.alexf6.mitosyfauna.logic.HorrorAiLogic;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TradeAndBehaviorLogicTest {

    @Test
    void mohanStealValidationEnforcesAllSafeguards() {
        assertTrue(HorrorAiLogic.canMohanSteal(true, false, false, false, 0));
        assertFalse(HorrorAiLogic.canMohanSteal(false, false, false, false, 0));
        assertFalse(HorrorAiLogic.canMohanSteal(true, true, false, false, 0));
        assertFalse(HorrorAiLogic.canMohanSteal(true, false, true, false, 0));
        assertFalse(HorrorAiLogic.canMohanSteal(true, false, false, true, 0));
        assertFalse(HorrorAiLogic.canMohanSteal(true, false, false, false, 12));
    }

    @Test
    void sombreroVueltiaoAppliesDiscountWithMinimumPriceFloor() {
        assertEquals(28, EconomyLogic.computeDiscountedTradeCost(28, false));
        assertEquals(21, EconomyLogic.computeDiscountedTradeCost(28, true));
        assertEquals(3, EconomyLogic.computeDiscountedTradeCost(4, true));
        assertEquals(1, EconomyLogic.computeDiscountedTradeCost(1, true));
        assertEquals(1, EconomyLogic.computeDiscountedTradeCost(0, true));
    }

    @Test
    void pesoExchangeCalculationIsConsistent() {
        assertEquals(16, EconomyLogic.emeraldsToPesos(1, 16));
        assertEquals(48, EconomyLogic.emeraldsToPesos(3, 16));
        assertEquals(1, EconomyLogic.emeraldsToPesos(0, 0));
    }
}
