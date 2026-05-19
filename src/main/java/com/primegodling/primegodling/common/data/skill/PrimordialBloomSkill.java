package com.primegodling.primegodling.common.data.skill;

import com.primegodling.primegodling.common.config.SkillConfig;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.world.entity.LivingEntity;

public class PrimordialBloomSkill extends Skill {

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
        double regenPerCall = (maxMagicule * regenPercent / 100.0) * 5.0;

        EnergyHelper.gainMagicule(entity, regenPerCall, EnergyHelper.GainType.NORMAL);
    }
}
