package io.github.alexf6.mitosyfauna.logic;

/**
 * Lógica geométrica y de reglas de comportamiento para los mitos nocturnos:
 * sonidos direccionales desorientadores, detección visual/jinetes, teletransporte de emboscada
 * y validaciones de robo temporal.
 */
public final class HorrorAiLogic {

    private HorrorAiLogic() {
    }

    /**
     * Vector 3D liviano para cálculos geométricos sin depender de tipos de Minecraft.
     */
    public record Vec3d(double x, double y, double z) {
    }

    /**
     * Calcula una posición de sonido desplazada detrás o a los flancos del jugador para desorientarlo.
     */
    public static Vec3d computeLureSoundPosition(
            Vec3d playerPos,
            float playerYawDegrees,
            double baseRadius,
            double unitArcSample,
            double distanceFactor,
            double verticalJitter
    ) {
        double safeRadius = Math.max(2.0, baseRadius);
        double clampedArc = clamp01(unitArcSample);
        double clampedDist = clamp01(distanceFactor);

        // En Minecraft, el frente del jugador sigue (-sin(yaw), cos(yaw)).
        // Sumamos entre 105° y 255° para ubicar el sonido detrás o a los costados.
        double relativeAngleDeg = 105.0 + clampedArc * 150.0;
        double yawRad = Math.toRadians(playerYawDegrees + relativeAngleDeg);
        double distance = safeRadius * (0.55 + 0.45 * clampedDist);

        double offsetX = -Math.sin(yawRad) * distance;
        double offsetZ = Math.cos(yawRad) * distance;
        return new Vec3d(playerPos.x() + offsetX, playerPos.y() + verticalJitter, playerPos.z() + offsetZ);
    }

    /**
     * Evalúa si el vector de mirada apunta dentro del cono visual hacia la cabeza del objetivo.
     */
    public static boolean isLookingAtEntity(Vec3d viewerEyePos, Vec3d viewerLookDir, Vec3d targetEyePos, double cosThreshold) {
        double dx = targetEyePos.x() - viewerEyePos.x();
        double dy = targetEyePos.y() - viewerEyePos.y();
        double dz = targetEyePos.z() - viewerEyePos.z();
        double distSq = dx * dx + dy * dy + dz * dz;
        if (distSq < 1.0E-6) {
            return true;
        }
        double lookLenSq = viewerLookDir.x() * viewerLookDir.x()
                + viewerLookDir.y() * viewerLookDir.y()
                + viewerLookDir.z() * viewerLookDir.z();
        if (lookLenSq < 1.0E-6) {
            return false;
        }
        double dist = Math.sqrt(distSq);
        double lookLen = Math.sqrt(lookLenSq);
        double dot = (dx * viewerLookDir.x() + dy * viewerLookDir.y() + dz * viewerLookDir.z()) / (dist * lookLen);
        return dot >= cosThreshold;
    }

    /**
     * Determina si El Sombrerón debe fijar al jugador como objetivo por ir montado o mirarlo fijamente.
     */
    public static boolean shouldSombreronAggro(
            boolean isMounted,
            boolean isLookingAtSombreron,
            double distanceSq,
            double maxRangeSq,
            boolean wearingCalmHat
    ) {
        if (distanceSq > maxRangeSq) {
            return false;
        }
        if (isLookingAtSombreron) {
            return true;
        }
        return isMounted && !wearingCalmHat;
    }

    /**
     * Calcula una coordenada de emboscada detrás de la dirección en la que mira la víctima.
     */
    public static Vec3d computeTeleportBehindPosition(
            Vec3d targetPos,
            Vec3d targetLookDir,
            double backDistance,
            double lateralOffset
    ) {
        double horizLen = Math.hypot(targetLookDir.x(), targetLookDir.z());
        double dirX;
        double dirZ;
        if (horizLen < 1.0E-5) {
            dirX = 0.0;
            dirZ = 1.0;
        } else {
            dirX = targetLookDir.x() / horizLen;
            dirZ = targetLookDir.z() / horizLen;
        }
        double dist = Math.max(1.5, backDistance);
        double sideX = -dirZ;
        double sideZ = dirX;
        return new Vec3d(
                targetPos.x() - dirX * dist + sideX * lateralOffset,
                targetPos.y(),
                targetPos.z() - dirZ * dist + sideZ * lateralOffset
        );
    }

    /**
     * Valida si El Mohán puede robar temporalmente el ítem especificado.
     */
    public static boolean canMohanSteal(
            boolean featureEnabled,
            boolean stackEmpty,
            boolean alreadyHoldingStolenItem,
            boolean isProtectedRelic,
            int stealCooldownTicks
    ) {
        return featureEnabled
                && !stackEmpty
                && !alreadyHoldingStolenItem
                && !isProtectedRelic
                && stealCooldownTicks <= 0;
    }

    private static double clamp01(double value) {
        if (Double.isNaN(value)) {
            return 0.5;
        }
        return Math.max(0.0, Math.min(1.0, value));
    }
}
