package io.github.alexf6.mitosyfauna;

import io.github.alexf6.mitosyfauna.logic.HorrorAiLogic;
import io.github.alexf6.mitosyfauna.logic.HorrorAiLogic.Vec3d;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HorrorAiLogicTest {

    @Test
    void lureSoundSpawnsBehindOrFlankingPlayerWithinConfiguredRadius() {
        Vec3d playerPos = new Vec3d(10.0, 64.0, -20.0);
        float playerYaw = 35.0F; // frente = (-sin(yaw), cos(yaw))
        double forwardX = -Math.sin(Math.toRadians(playerYaw));
        double forwardZ = Math.cos(Math.toRadians(playerYaw));

        for (double arc : new double[]{0.0, 0.25, 0.5, 0.75, 1.0}) {
            Vec3d lure = HorrorAiLogic.computeLureSoundPosition(playerPos, playerYaw, 14.0, arc, 0.5, 0.25);
            double dx = lure.x() - playerPos.x();
            double dz = lure.z() - playerPos.z();
            double dist = Math.hypot(dx, dz);
            double dotForward = (dx / dist) * forwardX + (dz / dist) * forwardZ;

            assertTrue(dist >= 14.0 * 0.54 && dist <= 14.01, "Distancia fuera de rango: " + dist);
            assertTrue(dotForward < 0.0, "El sonido direccional debe ubicarse detrás o a los flancos: " + dotForward);
        }
    }

    @Test
    void eyeContactDetectionDistinguishesDirectGazeFromLookingAway() {
        Vec3d eye = new Vec3d(0.0, 65.0, 0.0);
        Vec3d target = new Vec3d(0.0, 66.0, 12.0);
        Vec3d lookAtTarget = new Vec3d(0.0, 0.08, 1.0);
        Vec3d lookAway = new Vec3d(1.0, 0.0, 0.0);

        assertTrue(HorrorAiLogic.isLookingAtEntity(eye, lookAtTarget, target, 0.96));
        assertFalse(HorrorAiLogic.isLookingAtEntity(eye, lookAway, target, 0.96));
    }

    @Test
    void sombreronAggroPrioritizesRidersAndEyeContactWhileRespectingCalmHat() {
        double maxSq = 24.0 * 24.0;
        assertTrue(HorrorAiLogic.shouldSombreronAggro(true, false, 100.0, maxSq, false));
        assertFalse(HorrorAiLogic.shouldSombreronAggro(true, false, 100.0, maxSq, true));
        assertTrue(HorrorAiLogic.shouldSombreronAggro(false, true, 100.0, maxSq, true));
        assertFalse(HorrorAiLogic.shouldSombreronAggro(true, true, maxSq + 50.0, maxSq, false));
    }

    @Test
    void teleportBehindPositionMovesBehindVictimLookDirection() {
        Vec3d victim = new Vec3d(5.0, 64.0, 5.0);
        Vec3d lookingNorth = new Vec3d(0.0, 0.0, -1.0);
        Vec3d ambush = HorrorAiLogic.computeTeleportBehindPosition(victim, lookingNorth, 3.0, 0.5);

        assertTrue(ambush.z() > victim.z(), "Debe aparecer detrás (+Z) cuando la víctima mira al norte (-Z)");
    }
}
