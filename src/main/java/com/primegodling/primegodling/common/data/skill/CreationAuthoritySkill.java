package com.primegodling.primegodling.common.data.skill;

import com.primegodling.primegodling.common.config.SkillConfig;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class CreationAuthoritySkill extends Skill {

    private static final ResourceLocation ATK_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "ca_attack");
    private static final ResourceLocation HP_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "ca_health");
    private static final ResourceLocation ARMOR_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "ca_armor");
    private static final ResourceLocation TOUGH_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "ca_toughness");

    private static final int LIGHTNING_COUNT = 5;
    private static final int LIGHTNING_COUNT_MASTERED = 9;
    private static final float EXPLOSION_RADIUS = 12.0f;
    private static final float EXPLOSION_RADIUS_MASTERED = 18.0f;
    private static final double LIGHTNING_SPREAD = 4.0;
    private static final int IMMUNITY_DURATION = 40;

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
    public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            instance.removeAttributeModifiers(entity, 0);
        }
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity, int mode, int key) {
        if (entity.level().isClientSide) return;

        double energyCost = SkillConfig.COMMON.creationAuthorityEnergyCost.get();
        if (EnergyHelper.isOutOfEnergy(entity, 0.0, energyCost)) return;

        double distance = 64.0;
        HitResult hit = entity.pick(distance, 1.0f, false);
        Vec3 target;
        if (hit.getType() == HitResult.Type.BLOCK) {
            target = ((BlockHitResult) hit).getLocation();
        } else if (hit.getType() == HitResult.Type.ENTITY) {
            target = ((EntityHitResult) hit).getEntity().position();
        } else {
            Vec3 look = entity.getLookAngle();
            Vec3 start = entity.getEyePosition();
            target = start.add(look.x * distance, look.y * distance, look.z * distance);
        }

        if (entity.level() instanceof ServerLevel serverLevel) {
            boolean mastered = instance.isMastered(entity);
            int bolts = mastered ? LIGHTNING_COUNT_MASTERED : LIGHTNING_COUNT;
            float radius = mastered ? EXPLOSION_RADIUS_MASTERED : EXPLOSION_RADIUS;

            summonLightning(serverLevel, target.x, target.y, target.z);

            for (int i = 1; i < bolts; i++) {
                double angle = (Math.PI * 2 / bolts) * i;
                double ox = Math.cos(angle) * LIGHTNING_SPREAD;
                double oz = Math.sin(angle) * LIGHTNING_SPREAD;
                summonLightning(serverLevel, target.x + ox, target.y, target.z + oz);
            }

            serverLevel.explode(entity, target.x, target.y, target.z, radius, Level.ExplosionInteraction.BLOCK);

            Vec3 center = new Vec3(target.x, target.y, target.z);
            serverLevel.sendParticles(ParticleTypes.FLASH, target.x, target.y + 1.0, target.z, 1, 0, 0, 0, 0);
            serverLevel.sendParticles(ParticleTypes.SOUL, target.x, target.y + 1.0, target.z, 60, 4.0, 2.0, 4.0, 0.1);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, target.x, target.y + 1.0, target.z, 30, 3.0, 1.0, 3.0, 0.05);
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, target.x, target.y + 1.0, target.z, 80, 5.0, 2.0, 5.0, 0.3);

            serverLevel.playSound(null, target.x, target.y, target.z,
                SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 5.0f, 0.8f);
            serverLevel.playSound(null, target.x, target.y, target.z,
                SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0f, 0.6f);

            int lingerTicks = mastered ? 100 : 60;
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, target.x, target.y + 0.5, target.z,
                lingerTicks, radius * 0.5, 0.5, radius * 0.5, 0.15);
        }

        int cooldown = instance.isMastered(entity)
            ? SkillConfig.COMMON.creationAuthorityMasteredCooldown.get()
            : SkillConfig.COMMON.creationAuthorityCooldown.get();
        instance.setCoolDown(cooldown, mode);

        entity.invulnerableTime = Math.max(entity.invulnerableTime, IMMUNITY_DURATION);
    }

    private void summonLightning(ServerLevel level, double x, double y, double z) {
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.setPos(x, y, z);
            bolt.setVisualOnly(false);
            level.addFreshEntity(bolt);
        }
    }
}
