package com.primegodling.primegodling.common.data.skill;

import com.primegodling.primegodling.common.config.SkillConfig;
import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EclipticMasterySkill extends Skill {

    private static final ResourceLocation ARMOR_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "ecliptic_armor");
    private static final ResourceLocation TOUGHNESS_ID = ResourceLocation.fromNamespaceAndPath("primegodling", "ecliptic_toughness");

    public EclipticMasterySkill() {
        super(SkillType.INTRINSIC);
        addHeldAttributeModifier(Attributes.ARMOR, ARMOR_ID,
            1.0, AttributeModifier.Operation.ADD_VALUE);
        addHeldAttributeModifier(Attributes.ARMOR_TOUGHNESS, TOUGHNESS_ID,
            1.0, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public double getAttributeModifierAmplifier(ManasSkillInstance instance, LivingEntity entity,
            net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
            io.github.manasmods.manascore.skill.api.ManasSkill.AttributeTemplate template, int mode) {
        double masteredMult = instance.isMastered(entity) ? 2.0 : 1.0;
        if (attribute.value() == Attributes.ARMOR.value()) {
            return SkillConfig.COMMON.eclipticMasteryArmor.get() * masteredMult;
        }
        if (attribute.value() == Attributes.ARMOR_TOUGHNESS.value()) {
            return SkillConfig.COMMON.eclipticMasteryToughness.get() * masteredMult;
        }
        return 1.0;
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return false;
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

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
    }

    @Override
    public void onRespawn(ManasSkillInstance instance, ServerPlayer player, boolean wasDead) {
        if (!wasDead) return;
        if (player.level().isClientSide()) return;
        if (instance.isToggled()) {
            instance.addHeldAttributeModifiers(player, 0);
        }
    }

    @Override
    public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            instance.removeAttributeModifiers(entity, 0);
        }
    }

    @Override
    public boolean onTakenDamage(ManasSkillInstance instance, LivingEntity entity,
            net.minecraft.world.damagesource.DamageSource source, Changeable<Float> damage) {
        if (entity.level().isClientSide) return false;
        if (!instance.isToggled()) return false;
        if (source.getEntity() instanceof LivingEntity attacker) {
            float reflect = instance.isMastered(entity)
                ? SkillConfig.COMMON.eclipticMasteryMasteredReflectDamage.get().floatValue()
                : SkillConfig.COMMON.eclipticMasteryReflectDamage.get().floatValue();
            attacker.hurt(entity.damageSources().thorns(entity), reflect);
        }
        return false;
    }

    @Override
    public boolean onBeingDamaged(ManasSkillInstance instance, LivingEntity entity,
            net.minecraft.world.damagesource.DamageSource source, float damage) {
        if (entity.level().isClientSide) return false;
        if (!instance.isToggled()) return false;
        double chance = instance.isMastered(entity)
            ? SkillConfig.COMMON.eclipticMasteryMasteredNegateChance.get()
            : SkillConfig.COMMON.eclipticMasteryNegateChance.get();
        if (entity.getRandom().nextDouble() < chance) {
            if (entity.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT,
                    entity.getX(), entity.getY() + 1.0, entity.getZ(),
                    15, 1.0, 1.0, 1.0, 0.3);
            }
            return true;
        }
        return false;
    }
}
