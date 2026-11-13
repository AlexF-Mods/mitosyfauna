package io.github.alexf6.mitosyfauna.logic;

/**
 * Reglas puras de cálculo económico para el intercambio de pesos colombianos
 * y los descuentos comerciales del Sombrero Vueltiao.
 */
public final class EconomyLogic {

    private EconomyLogic() {
    }

    /**
     * Calcula el precio en esmeraldas o pesos aplicando el descuento del Sombrero Vueltiao.
     */
    public static int computeDiscountedTradeCost(int baseCost, boolean wearingVueltiao) {
        int safeBase = Math.max(1, baseCost);
        if (!wearingVueltiao || safeBase == 1) {
            return safeBase;
        }
        return Math.max(1, (int) Math.floor(safeBase * 0.75));
    }

    /**
     * Calcula cuántos pesos colombianos recibe el jugador al cambiar esmeraldas.
     */
    public static int emeraldsToPesos(int emeraldCount, int pesosPerEmerald) {
        return Math.max(1, emeraldCount) * Math.max(1, pesosPerEmerald);
    }
}
