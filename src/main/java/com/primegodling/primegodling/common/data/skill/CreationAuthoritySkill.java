package com.primegodling.primegodling.common.data.skill;

import com.mojang.datafixers.util.Pair;
import com.primegodling.primegodling.common.config.SkillConfig;
import com.primegodling.primegodling.common.data.SkillRegistry;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.manascore.skill.api.Skills;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.entity.magic.MagicCircle;
import io.github.manasmods.tensura.entity.variant.MagicCircleVariant;
import io.github.manasmods.tensura.util.EnergyHelper;
import com.primegodling.primegodling.common.ModEntities;
import com.primegodling.primegodling.common.entity.CreationBeamProjectile;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class CreationAuthoritySkill extends Skill {

    private static final Set<UUID> ACTIVE_CASTS = new HashSet<>();

    private static final ResourceLocation ATK_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "ca_attack");
    private static final ResourceLocation HP_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "ca_health");
    private static final ResourceLocation ARMOR_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "ca_armor");
    private static final ResourceLocation TOUGH_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "ca_toughness");

    private static final int CAST_TIME = 60;
    private static final float MAGIC_CIRCLE_RADIUS = 18.0f;
    private static final float EXPLOSION_RADIUS = 12.0f;
    private static final float EXPLOSION_RADIUS_MASTERED = 18.0f;
    private static final int IMMUNITY_DURATION = 40;
    private static final double SKY_HEIGHT = 25.0;
    private static final double MAX_RANGE = 64.0;

    private static final String TAG_TARGET_X = "CA_TARGET_X";
    private static final String TAG_TARGET_Y = "CA_TARGET_Y";
    private static final String TAG_TARGET_Z = "CA_TARGET_Z";
    private static final String TAG_CIRCLE_X = "CA_CIRCLE_X";
    private static final String TAG_CIRCLE_Y = "CA_CIRCLE_Y";
    private static final String TAG_CIRCLE_Z = "CA_CIRCLE_Z";
    private static final String TAG_CASTING = "CA_CASTING";
    private static final String TAG_CAST_START = "CA_CAST_START";
    private static final String TAG_MODE = "CA_MODE";

    public CreationAuthoritySkill() {
        super(SkillType.ULTIMATE);
        addHeldAttributeModifier(Attributes.ATTACK_DAMAGE, ATK_ID,
            6.0, AttributeModifier.Operation.ADD_VALUE);
        addHeldAttributeModifier(Attributes.MAX_HEALTH, HP_ID,
            16.0, AttributeModifier.Operation.ADD_VALUE);
        addHeldAttributeModifier(Attributes.ARMOR, ARMOR_ID,
            4.0, AttributeModifier.Operation.ADD_VALUE);
        addHeldAttributeModifier(Attributes.ARMOR_TOUGHNESS, TOUGH_ID,
            4.0, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            instance.addHeldAttributeModifiers(entity, 0);
        }
    }

    @Override
    public void onRespawn(ManasSkillInstance instance, ServerPlayer player, boolean wasDead) {
        if (!wasDead) return;
        if (player.level().isClientSide()) return;
        instance.addHeldAttributeModifiers(player, 0);
    }

    @Override
    public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            instance.removeAttributeModifiers(entity, 0);
        }
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity, int mode, int key) {
        if (entity.level().isClientSide) return;

        if (instance.onCoolDown(mode) && !instance.canIgnoreCoolDown(entity, mode)) return;

        double energyCost = SkillConfig.COMMON.creationAuthorityEnergyCost.get();
        if (EnergyHelper.isOutOfEnergy(entity, 0.0, energyCost)) return;

        if (!(entity instanceof ServerPlayer serverPlayer)) return;

        UUID uuid = serverPlayer.getUUID();
        if (!ACTIVE_CASTS.add(uuid)) return;

        CompoundTag tag = instance.getOrCreateTag();
        if (tag.getBoolean(TAG_CASTING)) return;

        Vec3 target = getTargetPosition(entity);
        Vec3 circlePos = new Vec3(target.x, target.y + SKY_HEIGHT, target.z);

        tag.putDouble(TAG_TARGET_X, target.x);
        tag.putDouble(TAG_TARGET_Y, target.y);
        tag.putDouble(TAG_TARGET_Z, target.z);
        tag.putDouble(TAG_CIRCLE_X, circlePos.x);
        tag.putDouble(TAG_CIRCLE_Y, circlePos.y);
        tag.putDouble(TAG_CIRCLE_Z, circlePos.z);
        tag.putLong(TAG_CAST_START, serverPlayer.serverLevel().getGameTime());
        tag.putInt(TAG_MODE, mode);
        tag.putBoolean(TAG_CASTING, true);
        instance.markDirty();

        MagicCircle.castTargetedMagicCircle(
            MAGIC_CIRCLE_RADIUS,
            CAST_TIME + 10,
            circlePos,
            MagicCircleVariant.HOLY,
            true,
            entity,
            tag,
            instance,
            mode,
            Pair.of(0.0, 0.0)
        );

        instance.setCoolDown(CAST_TIME, mode);
    }

    public static void tickCasts(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (!player.isAlive()) continue;

            Skills skills = SkillAPI.getSkillsFrom(player);
            if (skills == null) continue;

            Optional<ManasSkillInstance> optInst = skills.getSkill(SkillRegistry.CREATION_AUTHORITY);
            if (optInst.isEmpty()) continue;

            ManasSkillInstance instance = optInst.get();
            CompoundTag tag = instance.getOrCreateTag();

            if (!tag.getBoolean(TAG_CASTING)) continue;

            long startTick = tag.getLong(TAG_CAST_START);
            ServerLevel level = player.serverLevel();
            long elapsed = level.getGameTime() - startTick;

            if (elapsed < 0 || elapsed > CAST_TIME * 10L) {
                tag.putBoolean(TAG_CASTING, false);
                instance.markDirty();
                ACTIVE_CASTS.remove(player.getUUID());
            } else if (elapsed >= CAST_TIME) {
                completeCast(level, player, instance, tag);
            }
        }
    }

    private static void completeCast(ServerLevel level, ServerPlayer player, ManasSkillInstance instance, CompoundTag tag) {
        boolean mastered = instance.isMastered(player);
        float radius = mastered ? EXPLOSION_RADIUS_MASTERED : EXPLOSION_RADIUS;
        int mode = tag.getInt(TAG_MODE);

        Vec3 target = new Vec3(
            tag.getDouble(TAG_TARGET_X),
            tag.getDouble(TAG_TARGET_Y),
            tag.getDouble(TAG_TARGET_Z)
        );
        Vec3 circlePos = new Vec3(
            tag.getDouble(TAG_CIRCLE_X),
            tag.getDouble(TAG_CIRCLE_Y),
            tag.getDouble(TAG_CIRCLE_Z)
        );

        float beamRange = (float) circlePos.distanceTo(target) + 5.0f;

        CreationBeamProjectile beam = new CreationBeamProjectile(ModEntities.CREATION_BEAM.get(), level);
        beam.setPos(circlePos.x, circlePos.y, circlePos.z);
        beam.setOwner(player);
        beam.setLife((int) beamRange);
        beam.setDamage(30.0f);
        beam.setSecondaryDamage(2.0f);
        beam.setSize(2.0f);
        beam.setRange(beamRange);
        beam.setExplosionRadius(0.0f);
        beam.setSkill(instance);
        beam.setMode(mode);
        beam.setFollowingOwner(true);
        beam.setTargetPos(target.x, target.y, target.z);
        beam.updateAngle();
        level.addFreshEntity(beam);

        level.explode(player, target.x, target.y, target.z, radius, Level.ExplosionInteraction.BLOCK);

        level.sendParticles(ParticleTypes.FLASH, target.x, target.y + 1.0, target.z, 1, 0, 0, 0, 0);
        level.sendParticles(ParticleTypes.SOUL, target.x, target.y + 1.0, target.z, 60, 4.0, 2.0, 4.0, 0.1);
        level.sendParticles(ParticleTypes.LARGE_SMOKE, target.x, target.y + 1.0, target.z, 30, 3.0, 1.0, 3.0, 0.05);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, target.x, target.y + 1.0, target.z, 80, 5.0, 2.0, 5.0, 0.3);

        int lingerTicks = mastered ? 100 : 60;
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK, target.x, target.y + 0.5, target.z,
            lingerTicks, radius * 0.5, 0.5, radius * 0.5, 0.15);

        level.playSound(null, target.x, target.y, target.z,
            SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 5.0f, 0.8f);
        level.playSound(null, target.x, target.y, target.z,
            SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0f, 0.6f);

        int cooldown = mastered
            ? SkillConfig.COMMON.creationAuthorityMasteredCooldown.get()
            : SkillConfig.COMMON.creationAuthorityCooldown.get();
        instance.setCoolDown(cooldown, mode);

        player.invulnerableTime = Math.max(player.invulnerableTime, IMMUNITY_DURATION);

        tag.putBoolean(TAG_CASTING, false);
        instance.markDirty();
        ACTIVE_CASTS.remove(player.getUUID());
    }

    private static Vec3 getTargetPosition(LivingEntity entity) {
        double distance = MAX_RANGE;
        HitResult hit = entity.pick(distance, 1.0f, false);
        if (hit.getType() == HitResult.Type.BLOCK) {
            return ((BlockHitResult) hit).getLocation();
        } else if (hit.getType() == HitResult.Type.ENTITY) {
            return ((EntityHitResult) hit).getEntity().position();
        } else {
            Vec3 look = entity.getLookAngle();
            Vec3 start = entity.getEyePosition();
            return start.add(look.x * distance, look.y * distance, look.z * distance);
        }
    }
}
