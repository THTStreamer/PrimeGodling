package com.primegodling.primegodling.common.data.skill;

import com.primegodling.primegodling.common.config.SkillConfig;
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
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class PrimordialFortitudeSkill extends Skill {
    private static final Logger LOGGER = LogUtils.getLogger();

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
            1.0, AttributeModifier.Operation.ADD_VALUE);
        addHeldAttributeModifier(Attributes.MAX_HEALTH, HP_ID,
            1.0, AttributeModifier.Operation.ADD_VALUE);
        addHeldAttributeModifier(Attributes.ARMOR, ARMOR_ID,
            1.0, AttributeModifier.Operation.ADD_VALUE);
        addHeldAttributeModifier(Attributes.ARMOR_TOUGHNESS, TOUGH_ID,
            1.0, AttributeModifier.Operation.ADD_VALUE);
        addHeldAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED_ID,
            1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    @Override
    public double getAttributeModifierAmplifier(ManasSkillInstance instance, LivingEntity entity,
            net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
            io.github.manasmods.manascore.skill.api.ManasSkill.AttributeTemplate template, int mode) {
        double masteredMult = instance.isMastered(entity) ? 1.5 : 1.0;
        if (attribute.value() == Attributes.ATTACK_DAMAGE.value()) {
            return SkillConfig.COMMON.primordialFortitudeAttackBonus.get() * masteredMult;
        }
        if (attribute.value() == Attributes.MAX_HEALTH.value()) {
            return SkillConfig.COMMON.primordialFortitudeHealthBonus.get() * masteredMult;
        }
        if (attribute.value() == Attributes.ARMOR.value()) {
            return SkillConfig.COMMON.primordialFortitudeArmor.get() * masteredMult;
        }
        if (attribute.value() == Attributes.ARMOR_TOUGHNESS.value()) {
            return SkillConfig.COMMON.primordialFortitudeToughness.get() * masteredMult;
        }
        if (attribute.value() == Attributes.MOVEMENT_SPEED.value()) {
            return SkillConfig.COMMON.primordialFortitudeSpeedMultiplier.get() * masteredMult;
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

    private void applyProgressionBonuses(LivingEntity entity) {
        double learningGain = SkillConfig.COMMON.primordialFortitudeLearningGain.get();
        AttributeInstance learningAttr = entity.getAttribute(TensuraAttributes.ABILITY_LEARNING_GAIN);
        if (learningAttr != null) {
            learningAttr.addOrReplacePermanentModifier(
                new AttributeModifier(LEARNING_ID, learningGain, AttributeModifier.Operation.ADD_VALUE));
        }

        double masteryGain = SkillConfig.COMMON.primordialFortitudeMasteryGain.get();
        AttributeInstance masteryAttr = entity.getAttribute(TensuraAttributes.ABILITY_MASTERY_GAIN);
        if (masteryAttr != null) {
            masteryAttr.addOrReplacePermanentModifier(
                new AttributeModifier(MASTERY_ID, masteryGain, AttributeModifier.Operation.ADD_VALUE));
        }

        double chantSpeed = SkillConfig.COMMON.primordialFortitudeChantSpeed.get();
        AttributeHelper.multiplyChantSpeed(entity, chantSpeed);
    }

    private void removeProgressionBonuses(LivingEntity entity) {
        AttributeInstance learningAttr = entity.getAttribute(TensuraAttributes.ABILITY_LEARNING_GAIN);
        if (learningAttr != null) learningAttr.removeModifier(LEARNING_ID);

        AttributeInstance masteryAttr = entity.getAttribute(TensuraAttributes.ABILITY_MASTERY_GAIN);
        if (masteryAttr != null) masteryAttr.removeModifier(MASTERY_ID);

        double chantSpeed = SkillConfig.COMMON.primordialFortitudeChantSpeed.get();
        AttributeHelper.removeChantSpeed(entity, chantSpeed);
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            LOGGER.debug("[PrimordialFortitude] Toggled ON for {}", entity.getName().getString());
            instance.addHeldAttributeModifiers(entity, 0);
            applyProgressionBonuses(entity);
        }
    }

    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            LOGGER.debug("[PrimordialFortitude] Toggled OFF for {}", entity.getName().getString());
            instance.removeAttributeModifiers(entity, 0);
            removeProgressionBonuses(entity);
        }
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level().isClientSide) return;
        if (!instance.isToggled()) return;
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
            boolean toggled = instance.isToggled();
            LOGGER.debug("[PrimordialFortitude] onTakenDamage: entity={}, toggled={}, incomingDamage={}",
                entity.getName().getString(), toggled, damage.get());
            if (!toggled) return false;
            float reduction = instance.isMastered(entity)
                ? SkillConfig.COMMON.primordialFortitudeMasteredDamageReduction.get().floatValue()
                : SkillConfig.COMMON.primordialFortitudeDamageReduction.get().floatValue();
            float originalDamage = damage.get();
            float finalDamage = originalDamage * (1.0f - reduction);
            if (finalDamage < 1.0f && originalDamage > 0.0f) {
                finalDamage = 1.0f;
            }
            damage.set(finalDamage);
            LOGGER.debug("[PrimordialFortitude] reduction={}, finalDamage={}", reduction, damage.get());
        }
        return false;
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
        LOGGER.debug("[PrimordialFortitude] Skill learned by {}, auto-toggling ON", entity.getName().getString());
        instance.setToggled(true);
    }

    @Override
    public void onRespawn(ManasSkillInstance instance, ServerPlayer player, boolean wasDead) {
        if (!wasDead) return;
        if (player.level().isClientSide()) return;
        if (instance.isToggled()) {
            instance.addHeldAttributeModifiers(player, 0);
            applyProgressionBonuses(player);
        }
    }

    @Override
    public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            instance.removeAttributeModifiers(entity, 0);
            removeProgressionBonuses(entity);
        }
    }
}
