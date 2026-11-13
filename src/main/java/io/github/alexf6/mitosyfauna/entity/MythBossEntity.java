package io.github.alexf6.mitosyfauna.entity;

import io.github.alexf6.mitosyfauna.MitosYFaunaConfig;
import io.github.alexf6.mitosyfauna.logic.HorrorAiLogic;
import io.github.alexf6.mitosyfauna.logic.HorrorAiLogic.Vec3d;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Clase base compartida para los cuatro mitos y leyendas nocturnos del mod.
 * Centraliza la barra de jefe opcional por proximidad, la vida configurable en spawn,
 * los sonidos direccionales desorientadores y la validación de aparición en superficie nocturna.
 */
public abstract class MythBossEntity extends Monster {

    private static final double BOSS_BAR_RANGE = 28.0;

    private final ServerBossEvent bossEvent;
    private int horrorSoundCooldown = 40;

    protected MythBossEntity(
            EntityType<? extends Monster> entityType,
            Level level,
            BossEvent.BossBarColor barColor
    ) {
        super(entityType, level);
        this.bossEvent = new ServerBossEvent(this.getDisplayName(), barColor, BossEvent.BossBarOverlay.PROGRESS);
        this.bossEvent.setVisible(true);
        this.xpReward = 25;
    }

    protected abstract double configuredMaxHealth();

    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            EntitySpawnReason spawnReason,
            SpawnGroupData spawnData
    ) {
        AttributeInstance maxHealth = this.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            double configured = configuredMaxHealth();
            maxHealth.setBaseValue(configured);
            this.setHealth((float) configured);
        }
        return super.finalizeSpawn(level, difficulty, spawnReason, spawnData);
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }

    @Override
    protected void customServerAiStep(ServerLevel level) {
        super.customServerAiStep(level);
        updateBossBar(level);

        if (horrorSoundCooldown > 0) {
            horrorSoundCooldown--;
        }
    }

    private void updateBossBar(ServerLevel level) {
        if (!MitosYFaunaConfig.get().enableMythBossBars() || !this.isAlive()) {
            this.bossEvent.setVisible(false);
            this.bossEvent.removeAllPlayers();
            return;
        }
        this.bossEvent.setVisible(true);
        this.bossEvent.setName(this.getDisplayName());
        this.bossEvent.setProgress(Math.max(0.0F, Math.min(1.0F, this.getHealth() / this.getMaxHealth())));

        AABB box = this.getBoundingBox().inflate(BOSS_BAR_RANGE);
        List<ServerPlayer> nearby = level.getEntitiesOfClass(ServerPlayer.class, box, Player::isAlive);
        Set<ServerPlayer> nearbySet = new HashSet<>(nearby);

        for (ServerPlayer tracked : List.copyOf(this.bossEvent.getPlayers())) {
            if (!nearbySet.contains(tracked)) {
                this.bossEvent.removePlayer(tracked);
            }
        }
        for (ServerPlayer player : nearby) {
            this.bossEvent.addPlayer(player);
        }
    }

    /**
     * Emite un sonido posicional desplazado detrás o a los flancos de la víctima para desorientarla.
     */
    protected void tryPlayDirectionalLure(
            ServerLevel level,
            Player target,
            SoundEvent sound,
            float volume,
            float pitch,
            int nextCooldownTicks
    ) {
        if (horrorSoundCooldown > 0 || !MitosYFaunaConfig.get().enableHorrorDirectionalSounds()) {
            return;
        }
        double radius = MitosYFaunaConfig.get().directionalSoundRadius();
        Vec3d lurePos = HorrorAiLogic.computeLureSoundPosition(
                new Vec3d(target.getX(), target.getY() + 0.6, target.getZ()),
                target.getYRot(),
                radius,
                this.random.nextDouble(),
                this.random.nextDouble(),
                (this.random.nextDouble() - 0.5) * 1.5
        );
        level.playSound(null, lurePos.x(), lurePos.y(), lurePos.z(), sound, SoundSource.HOSTILE, volume, pitch);
        horrorSoundCooldown = nextCooldownTicks;
    }

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        this.bossEvent.removeAllPlayers();
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        this.bossEvent.removeAllPlayers();
    }

    /**
     * Valida aparición nocturna al aire libre, permitiendo bosques densos bajo copas de hojas
     * (sin exigir cielo totalmente despejado sobre la cabeza) pero evitando cuevas subterráneas.
     */
    public static boolean checkNocturnalMythSpawnRules(
            EntityType<? extends Monster> type,
            ServerLevelAccessor level,
            EntitySpawnReason spawnReason,
            BlockPos pos,
            RandomSource random
    ) {
        if (!Monster.checkMonsterSpawnRules(type, level, spawnReason, pos, random)) {
            return false;
        }
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
        return pos.getY() >= surfaceY - 1;
    }
}
