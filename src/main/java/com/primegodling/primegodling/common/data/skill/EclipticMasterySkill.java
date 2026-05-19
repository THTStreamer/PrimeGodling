package com.primegodling.primegodling.common.data.skill;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EclipticMasterySkill extends Skill {

    private static final ResourceLocation ARMOR_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "ecliptic_armor");
    private static final ResourceLocation TOUGHNESS_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "ecliptic_toughness");

    public EclipticMasterySkill() {
        super(SkillType.INTRINSIC);
        addHeldAttributeModifier(Attributes.ARMOR, ARMOR_ID,
            6.0, AttributeModifier.Operation.ADD_VALUE);
        addHeldAttributeModifier(Attributes.ARMOR_TOUGHNESS, TOUGHNESS_ID,
            4.0, AttributeModifier.Operation.ADD_VALUE);
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
}
