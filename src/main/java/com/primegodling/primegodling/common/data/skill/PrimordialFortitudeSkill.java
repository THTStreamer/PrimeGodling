package com.primegodling.primegodling.common.data.skill;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.registry.attribute.TensuraAttributes;
import io.github.manasmods.tensura.util.AttributeHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class PrimordialFortitudeSkill extends Skill {

    private static final ResourceLocation DMG_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "fort_damage");
    private static final ResourceLocation HP_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "fort_health");
    private static final ResourceLocation ARMOR_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "fort_armor");
    private static final ResourceLocation TOUGH_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "fort_toughness");
    private static final ResourceLocation SPEED_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "fort_speed");
    private static final ResourceLocation LEARNING_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "fort_learning");
    private static final ResourceLocation MASTERY_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "fort_mastery");

    public PrimordialFortitudeSkill() {
        super(SkillType.INTRINSIC);
        addHeldAttributeModifier(Attributes.ATTACK_DAMAGE, DMG_ID,
            10.0, AttributeModifier.Operation.ADD_VALUE);
        addHeldAttributeModifier(Attributes.MAX_HEALTH, HP_ID,
            40.0, AttributeModifier.Operation.ADD_VALUE);
        addHeldAttributeModifier(Attributes.ARMOR, ARMOR_ID,
            12.0, AttributeModifier.Operation.ADD_VALUE);
        addHeldAttributeModifier(Attributes.ARMOR_TOUGHNESS, TOUGH_ID,
            8.0, AttributeModifier.Operation.ADD_VALUE);
        addHeldAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED_ID,
            0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public double getAttributeModifierAmplifier(ManasSkillInstance instance, LivingEntity entity,
            net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
            io.github.manasmods.manascore.skill.api.ManasSkill.AttributeTemplate template, int mode) {
        if (instance.isMastered(entity)) {
            return 1.5;
        }
        return 1.0;
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level().isClientSide) return;
        if (entity.tickCount % 40 == 0) {
            entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 100, 0, false, false, true));
            entity.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 100, 0, false, false, true));
        }
        if (entity.fallDistance > 0f) {
            entity.fallDistance = 0f;
        }
    }

    @Override
    public boolean onTakenDamage(ManasSkillInstance instance, LivingEntity entity,
            net.minecraft.world.damagesource.DamageSource source, Changeable<Float> damage) {
        if (!entity.level().isClientSide) {
            float reduction = instance.isMastered(entity) ? 0.95f : 0.9f;
            damage.set(damage.get() * (1.0f - reduction));
        }
        return false;
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            instance.addHeldAttributeModifiers(entity, 0);

            AttributeInstance learningAttr = entity.getAttribute(TensuraAttributes.ABILITY_LEARNING_GAIN);
            if (learningAttr != null) {
                learningAttr.addOrReplacePermanentModifier(
                    new AttributeModifier(LEARNING_ID, 50.0, AttributeModifier.Operation.ADD_VALUE));
            }

            AttributeInstance masteryAttr = entity.getAttribute(TensuraAttributes.ABILITY_MASTERY_GAIN);
            if (masteryAttr != null) {
                masteryAttr.addOrReplacePermanentModifier(
                    new AttributeModifier(MASTERY_ID, 50.0, AttributeModifier.Operation.ADD_VALUE));
            }

            AttributeHelper.multiplyChantSpeed(entity, 5.0);
        }
    }

    @Override
    public void onRespawn(ManasSkillInstance instance, ServerPlayer player, boolean wasDead) {
        if (!wasDead) return;
        if (player.level().isClientSide()) return;
        instance.addHeldAttributeModifiers(player, 0);

        AttributeInstance learningAttr = player.getAttribute(TensuraAttributes.ABILITY_LEARNING_GAIN);
        if (learningAttr != null) {
            learningAttr.addOrReplacePermanentModifier(
                new AttributeModifier(LEARNING_ID, 50.0, AttributeModifier.Operation.ADD_VALUE));
        }

        AttributeInstance masteryAttr = player.getAttribute(TensuraAttributes.ABILITY_MASTERY_GAIN);
        if (masteryAttr != null) {
            masteryAttr.addOrReplacePermanentModifier(
                new AttributeModifier(MASTERY_ID, 50.0, AttributeModifier.Operation.ADD_VALUE));
        }

        AttributeHelper.multiplyChantSpeed(player, 5.0);
    }

    @Override
    public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            instance.removeAttributeModifiers(entity, 0);

            AttributeInstance learningAttr = entity.getAttribute(TensuraAttributes.ABILITY_LEARNING_GAIN);
            if (learningAttr != null) learningAttr.removeModifier(LEARNING_ID);

            AttributeInstance masteryAttr = entity.getAttribute(TensuraAttributes.ABILITY_MASTERY_GAIN);
            if (masteryAttr != null) masteryAttr.removeModifier(MASTERY_ID);

            AttributeHelper.removeChantSpeed(entity, 5.0);
        }
    }
}
