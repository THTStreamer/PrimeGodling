package com.primegodling.primegodling.common.data.skill;

import com.primegodling.primegodling.common.config.SkillConfig;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class CosmicAwarenessSkill extends Skill {

    private double getPresenceRange(ManasSkillInstance instance, LivingEntity entity) {
        double baseRange = SkillConfig.COMMON.cosmicAwarenessRange.get();
        if (instance.isMastered(entity)) {
            baseRange *= 1.5;
        }
        return baseRange;
    }

    public CosmicAwarenessSkill() {
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
        if (EnergyHelper.isOutOfEnergy(entity, instance, 0, 5.0f)) {
            instance.setToggled(false);
            instance.onToggleOff(entity);
            return;
        }
        double range = getPresenceRange(instance, entity);
        for (LivingEntity e : entity.level().getEntitiesOfClass(LivingEntity.class,
                entity.getBoundingBox().inflate(range),
                e -> e.hasEffect(MobEffects.INVISIBILITY) && e.isAlive())) {
            e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 30, 0, false, false, true));
        }
        if (instance.isMastered(entity)) {
            entity.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 60, 0, false, false, true));
        }
        instance.addMasteryPoint(entity);
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level().isClientSide) return;
        AttributeHelper.addPresenceSense(entity, getPresenceRange(instance, entity));
    }

    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level().isClientSide) return;
        AttributeHelper.removePresenceSense(entity, getPresenceRange(instance, entity));
        entity.removeEffect(MobEffects.NIGHT_VISION);
        if (entity.level() instanceof ServerLevel serverLevel) {
            double range = getPresenceRange(instance, entity);
            for (LivingEntity e : entity.level().getEntitiesOfClass(LivingEntity.class,
                    entity.getBoundingBox().inflate(range),
                    e -> e != entity && e.isAlive())) {
                e.removeEffect(MobEffects.GLOWING);
            }
        }
    }

    @Override
    public boolean onBeingTargeted(ManasSkillInstance instance, Changeable<LivingEntity> targetingEntity, LivingEntity self) {
        if (self.level().isClientSide) return false;
        if (!instance.isToggled()) return false;
        LivingEntity targeter = targetingEntity.get();
        if (targeter != null && targeter.isAlive()) {
            targeter.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, false, false, true));
        }
        return true;
    }
}
