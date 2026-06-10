package com.primegodling.primegodling.common.data.skill;

import io.github.manasmods.manascore.network.api.util.Changeable;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
            return;
        }
        if (entity.level() instanceof ServerLevel serverLevel) {
            double x = entity.getX();
            double y = entity.getY() + 2.0;
            double z = entity.getZ();
            serverLevel.sendParticles(ParticleTypes.END_ROD, x, y, z, 3, 1.5, 1.0, 1.5, 0.02);
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 2, 1.0, 0.5, 1.0, 0.01);
        }
        if (instance.isMastered(entity) && entity.fallDistance > 3.0f) {
            entity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0, false, false, true));
        }
    }

    @Override
    public boolean onDamageEntity(ManasSkillInstance instance, LivingEntity entity,
            LivingEntity target, net.minecraft.world.damagesource.DamageSource source,
            Changeable<Float> damage) {
        if (!instance.isToggled()) return true;
        float bonus = instance.isMastered(entity) ? 4.0f : 2.0f;
        damage.set(damage.get() + bonus);
        return true;
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            instance.addHeldAttributeModifiers(entity, 0);
            instance.setToggled(true);
        }
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
