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
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class EclipticMasterySkill extends Skill {
    private static final Logger LOGGER = LogUtils.getLogger();

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
        return instance.isToggled();
    }

    @Override
    public void onTick(ManasSkillInstance instance, LivingEntity entity) {
        if (entity.level().isClientSide) return;
        if (!instance.isToggled()) return;
        instance.addMasteryPoint(entity);
    }

    @Override
    public void onToggleOn(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            LOGGER.debug("[EclipticMastery] Toggled ON for {}", entity.getName().getString());
            instance.addHeldAttributeModifiers(entity, 0);
        }
    }

    @Override
    public void onToggleOff(ManasSkillInstance instance, LivingEntity entity) {
        if (!entity.level().isClientSide()) {
            LOGGER.debug("[EclipticMastery] Toggled OFF for {}", entity.getName().getString());
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
        if (damage.get() < 1.0f && damage.get() > 0.0f) {
            damage.set(1.0f);
        }
        return false;
    }

    @Override
    public boolean onBeingDamaged(ManasSkillInstance instance, LivingEntity entity,
            net.minecraft.world.damagesource.DamageSource source, float damage) {
        if (entity.level().isClientSide) return true;
        boolean toggled = instance.isToggled();
        LOGGER.debug("[EclipticMastery] onBeingDamaged: entity={}, toggled={}, mastered={}, incomingDamage={}, source={}",
            entity.getName().getString(), toggled, instance.isMastered(entity), damage, source.getMsgId());
        if (!toggled) return true;
        double chance = instance.isMastered(entity)
            ? SkillConfig.COMMON.eclipticMasteryMasteredNegateChance.get()
            : SkillConfig.COMMON.eclipticMasteryNegateChance.get();
        double roll = entity.getRandom().nextDouble();
        LOGGER.debug("[EclipticMastery] negateChance={}, roll={}, negated={}", chance, roll, roll < chance);
        if (roll < chance) {
            if (entity.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT,
                    entity.getX(), entity.getY() + 1.0, entity.getZ(),
                    15, 1.0, 1.0, 1.0, 0.3);
            }
            return false;
        }
        return true;
    }
}
