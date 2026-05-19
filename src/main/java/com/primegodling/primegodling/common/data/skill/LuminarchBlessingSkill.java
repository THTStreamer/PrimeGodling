package com.primegodling.primegodling.common.data.skill;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class LuminarchBlessingSkill extends Skill {

    private static final double ENERGY_COST = 200.0;

    public LuminarchBlessingSkill() {
        super(SkillType.INTRINSIC);
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (!instance.isToggled()) return;
        if (entity.level().isClientSide) return;

        if (EnergyHelper.isOutOfEnergy(entity, 0.0, ENERGY_COST)) {
            instance.setToggled(false);
            return;
        }

        int amplifier = instance.isMastered(entity) ? 1 : 0;
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, amplifier, false, false, true));
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, amplifier, false, false, true));
    }
}
