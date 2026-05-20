package com.primegodling.primegodling.common.data.skill;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.util.AttributeHelper;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class CosmicAwarenessSkill extends Skill {

    private static final double PRESENCE_RANGE = 200.0;

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
        for (LivingEntity e : entity.level().getEntitiesOfClass(LivingEntity.class,
                entity.getBoundingBox().inflate(PRESENCE_RANGE),
                e -> e.hasEffect(MobEffects.INVISIBILITY) && e.isAlive())) {
            e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 30, 0, false, false, true));
        }
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level().isClientSide) return;
        AttributeHelper.addPresenceSense(entity, PRESENCE_RANGE);
    }

    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level().isClientSide) return;
        AttributeHelper.removePresenceSense(entity, PRESENCE_RANGE);
    }

    @Override
    public boolean onBeingTargeted(ManasSkillInstance instance, Changeable<LivingEntity> targetingEntity, LivingEntity self) {
        if (self.level().isClientSide) return false;
        LivingEntity targeter = targetingEntity.get();
        if (targeter != null && targeter.isAlive()) {
            targeter.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0, false, false, true));
        }
        return false;
    }
}
