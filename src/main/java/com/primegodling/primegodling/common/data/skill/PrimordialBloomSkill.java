package com.primegodling.primegodling.common.data.skill;

import com.primegodling.primegodling.common.config.SkillConfig;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class PrimordialBloomSkill extends Skill {

    private static final int BLOOM_INTERVAL = 20;

    public PrimordialBloomSkill() {
        super(SkillType.INTRINSIC);
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
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

            if (instance.isMastered(entity)) {
                for (LivingEntity nearby : serverLevel.getEntitiesOfClass(LivingEntity.class,
                        entity.getBoundingBox().inflate(5.0),
                        e -> e != entity && e instanceof net.minecraft.world.entity.player.Player)) {
                    nearby.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, false, false, true));
                }
            }
        }
    }
}
