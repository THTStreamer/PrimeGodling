package com.primegodling.primegodling.common.data.skill;

import io.github.manasmods.manascore.skill.api.ManasSkill;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.manascore.skill.api.SkillAPI;
import io.github.manasmods.tensura.ability.SkillHelper;
import io.github.manasmods.tensura.ability.skill.Skill;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class DivineDevourSkill extends Skill {

    private static final double SUCCESS_CHANCE = 0.10;
    private static final int COOLDOWN_NORMAL = 200;
    private static final int COOLDOWN_MASTERED = 100;
    private static final double RANGE = 32.0;

    public DivineDevourSkill() {
        super(SkillType.UNIQUE);
    }

    @Override
    public boolean canBeToggled(ManasSkillInstance instance, LivingEntity entity) {
        return false;
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return false;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity, int mode, int key) {
        if (entity.level().isClientSide) return;
        if (!(entity instanceof ServerPlayer player)) return;

        Vec3 start = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = start.add(look.x * RANGE, look.y * RANGE, look.z * RANGE);
        AABB aabb = player.getBoundingBox().expandTowards(look.scale(RANGE)).inflate(1.0);
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(player, start, end, aabb,
            e -> e instanceof LivingEntity && e != player, RANGE * RANGE);
        if (hit == null) return;

        LivingEntity target = (LivingEntity) hit.getEntity();

        int cooldown = instance.isMastered(entity) ? COOLDOWN_MASTERED : COOLDOWN_NORMAL;
        instance.setCoolDown(cooldown, mode);

        List<ManasSkill> targetSkills = new ArrayList<>();
        var storage = SkillAPI.getSkillsFrom(target);
        if (storage != null) {
            for (ManasSkillInstance skillInst : storage.getLearnedSkills()) {
                ManasSkill skill = skillInst.getSkill();
                if (skill != null) {
                    targetSkills.add(skill);
                }
            }
        }

        if (targetSkills.isEmpty()) {
            if (entity.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                    target.getX(), target.getY() + 1.0, target.getZ(),
                    5, 0.3, 0.5, 0.3, 0.02);
            }
            return;
        }

        if (entity.level() instanceof ServerLevel serverLevel) {
            if (player.getRandom().nextDouble() < SUCCESS_CHANCE) {
                ManasSkill stolen = targetSkills.get(player.getRandom().nextInt(targetSkills.size()));
                if (SkillHelper.learnSkill(player, stolen)) {
                    serverLevel.sendParticles(ParticleTypes.SOUL,
                        target.getX(), target.getY() + 1.0, target.getZ(),
                        20, 1.0, 1.0, 1.0, 0.1);
                    serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        player.getX(), player.getY() + 1.0, player.getZ(),
                        15, 0.5, 1.0, 0.5, 0.05);
                    serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 2.0f, 1.0f);
                    serverLevel.playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.SOUL_ESCAPE, SoundSource.HOSTILE, 1.5f, 0.8f);
                }
            } else {
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                    target.getX(), target.getY() + 1.0, target.getZ(),
                    5, 0.3, 0.5, 0.3, 0.02);
            }
        }
    }
}
