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

public class LuminarchBlessingSkill extends Skill {

    public LuminarchBlessingSkill() {
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
        if (!instance.isToggled()) return;
        if (entity.level().isClientSide) return;

        int cost = SkillConfig.COMMON.luminarchBlessingCost.get();
        if (EnergyHelper.isOutOfEnergy(entity, 0.0, cost)) {
            instance.setToggled(false);
            return;
        }

        int amplifier = instance.isMastered(entity) ? 1 : 0;
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, amplifier, false, false, true));
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, amplifier, false, false, true));

        if (entity.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.GLOW,
                entity.getX(), entity.getY() + 1.5, entity.getZ(),
                2, 1.5, 0.8, 1.5, 0.01);

            int range = instance.isMastered(entity)
                ? SkillConfig.COMMON.luminarchBlessingMasteredRange.get()
                : SkillConfig.COMMON.luminarchBlessingRange.get();
            for (LivingEntity nearby : serverLevel.getEntitiesOfClass(LivingEntity.class,
                    entity.getBoundingBox().inflate(range),
                    e -> e != entity && e.isAlive())) {
                nearby.addEffect(new MobEffectInstance(MobEffects.GLOWING, 30, 0, false, false, true));
            }

            if (instance.isMastered(entity)) {
                double healRange = SkillConfig.COMMON.luminarchBlessingHealRange.get();
                for (LivingEntity nearby : serverLevel.getEntitiesOfClass(LivingEntity.class,
                        entity.getBoundingBox().inflate(healRange),
                        e -> e != entity && e.isAlive())) {
                    nearby.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, false, false, true));
                }
            }
        }
    }
}
