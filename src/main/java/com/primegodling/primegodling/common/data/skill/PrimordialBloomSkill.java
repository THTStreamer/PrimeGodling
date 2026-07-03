package com.primegodling.primegodling.common.data.skill;

import com.primegodling.primegodling.common.config.SkillConfig;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.util.EnergyHelper;
import io.github.manasmods.tensura.util.SubordinateHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class PrimordialBloomSkill extends Skill {

    private static final int BLOOM_INTERVAL = 20;
    private static final double SUBORDINATE_RANGE = 32.0;

    public PrimordialBloomSkill() {
        super(SkillType.INTRINSIC);
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return instance.isToggled();
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level().isClientSide) return;

        double maxMagicule = EnergyHelper.getMaxMagicule(entity);
        if (maxMagicule <= 0) return;
        int regenPercent = SkillConfig.COMMON.primordialBloomRegenRate.get();
        double regenPerCall = maxMagicule * regenPercent / 100.0;
        EnergyHelper.gainMagicule(entity, regenPerCall, EnergyHelper.GainType.NORMAL);

        if (entity.tickCount % BLOOM_INTERVAL != 0) return;

        if (entity.level() instanceof ServerLevel serverLevel) {
            double x = entity.getX();
            double y = entity.getY() + 1.0;
            double z = entity.getZ();
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 8, 2.0, 1.0, 2.0, 0.02);
            serverLevel.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, x, y + 0.5, z, 6, 1.5, 0.5, 1.5, 0.01);

            if (instance.isMastered(entity) && entity instanceof ServerPlayer bloomOwner) {
                grantSubordinateBuffs(bloomOwner, serverLevel);
                grantNamerBuffs(bloomOwner);
            }
        }
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
    }

    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
    }

    private void grantSubordinateBuffs(ServerPlayer bloomOwner, ServerLevel level) {
        for (LivingEntity nearby : level.getEntitiesOfClass(LivingEntity.class,
                bloomOwner.getBoundingBox().inflate(SUBORDINATE_RANGE),
                e -> e != bloomOwner && e.isAlive() && e.hasCustomName() && SubordinateHelper.isSubordinate(e, bloomOwner))) {
            applyBloomBuffs(nearby);
        }
    }

    private void grantNamerBuffs(ServerPlayer bloomOwner) {
        LivingEntity namer = SubordinateHelper.getSubordinateOwner(bloomOwner);
        if (namer != null && namer.isAlive() && namer != bloomOwner) {
            applyBloomBuffs(namer);
        }
    }

    private void applyBloomBuffs(LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 1, false, false, true));
        double maxMagicule = EnergyHelper.getMaxMagicule(target);
        if (maxMagicule > 0) {
            int regenPercent = SkillConfig.COMMON.primordialBloomRegenRate.get();
            double regenAmount = maxMagicule * regenPercent / 100.0;
            EnergyHelper.gainMagicule(target, regenAmount, EnergyHelper.GainType.NORMAL);
        }
    }
}
