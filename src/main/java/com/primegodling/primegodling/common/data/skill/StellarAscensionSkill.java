package com.primegodling.primegodling.common.data.skill;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class StellarAscensionSkill extends Skill {

    private static final ResourceLocation STRENGTH_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "stellar_strength");
    private static final ResourceLocation HEALTH_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "stellar_health");

    public StellarAscensionSkill() {
        super(SkillType.INTRINSIC);
        addHeldAttributeModifier(Attributes.ATTACK_DAMAGE, STRENGTH_ID,
            4.0, AttributeModifier.Operation.ADD_VALUE);
        addHeldAttributeModifier(Attributes.MAX_HEALTH, HEALTH_ID,
            20.0, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public double getAttributeModifierAmplifier(ManasSkillInstance instance, LivingEntity entity,
            net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
            io.github.manasmods.manascore.skill.api.ManasSkill.AttributeTemplate template, int mode) {
        if (instance.isMastered(entity)) {
            return 2.0;
        }
        return 1.0;
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
        }
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            instance.addHeldAttributeModifiers(entity, 0);
            instance.setToggled(true);
        }
    }

    @Override
    public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            instance.removeAttributeModifiers(entity, 0);
        }
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            instance.addHeldAttributeModifiers(entity, 0);
        }
    }

    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            instance.removeAttributeModifiers(entity, 0);
        }
    }
}
