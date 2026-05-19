package com.primegodling.primegodling.common.data.skill;

import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class CreationAuthoritySkill extends Skill {

    private static final int COOLDOWN_UNMASTERED = 200;
    private static final int COOLDOWN_MASTERED = 60;
    private static final double ENERGY_COST = 5000.0;
    private static final float EXPLOSION_RADIUS = 12.0f;

    public CreationAuthoritySkill() {
        super(SkillType.ULTIMATE);
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity, int mode, int key) {
        if (entity.level().isClientSide) return;
        if (EnergyHelper.isOutOfEnergy(entity, 0.0, ENERGY_COST)) return;

        double distance = 64.0;
        HitResult hit = entity.pick(distance, 1.0f, false);
        Vec3 target;
        if (hit.getType() == HitResult.Type.BLOCK) {
            target = ((BlockHitResult) hit).getLocation();
        } else if (hit.getType() == HitResult.Type.ENTITY) {
            target = ((EntityHitResult) hit).getEntity().position();
        } else {
            Vec3 look = entity.getLookAngle();
            Vec3 start = entity.getEyePosition();
            target = start.add(look.x * distance, look.y * distance, look.z * distance);
        }

        if (entity.level() instanceof ServerLevel serverLevel) {
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (lightning != null) {
                lightning.setPos(target.x, target.y, target.z);
                lightning.setVisualOnly(false);
                serverLevel.addFreshEntity(lightning);
            }
            serverLevel.explode(entity, target.x, target.y, target.z, EXPLOSION_RADIUS, Level.ExplosionInteraction.BLOCK);
        }

        EnergyHelper.gainMagicule(entity, -ENERGY_COST, EnergyHelper.GainType.NORMAL);
        int cooldown = instance.isMastered(entity) ? COOLDOWN_MASTERED : COOLDOWN_UNMASTERED;
        instance.setCoolDown(cooldown, mode);
    }
}
