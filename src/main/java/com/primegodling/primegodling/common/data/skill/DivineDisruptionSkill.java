package com.primegodling.primegodling.common.data.skill;

import com.primegodling.primegodling.common.config.SkillConfig;
import io.github.manasmods.manascore.skill.api.ManasSkillInstance;
import io.github.manasmods.tensura.ability.skill.Skill;
import io.github.manasmods.tensura.storage.TensuraStorages;
import io.github.manasmods.tensura.storage.ep.IExistence;
import io.github.manasmods.tensura.util.EnergyHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class DivineDisruptionSkill extends Skill {

    public DivineDisruptionSkill() {
        super(SkillType.UNIQUE);
    }

    @Override
    public boolean canTick(ManasSkillInstance instance, LivingEntity entity) {
        return false;
    }

    @Override
    public void onPressed(ManasSkillInstance instance, LivingEntity entity, int mode, int key) {
        if (entity.level().isClientSide) return;
        if (!(entity instanceof ServerPlayer player)) return;

        double maxMagicule = EnergyHelper.getMaxMagicule(entity);
        if (maxMagicule <= 0) return;

        double activationCost = instance.isMastered(entity)
                ? SkillConfig.COMMON.divineDisruptionActivationCostMastered.get()
                : SkillConfig.COMMON.divineDisruptionActivationCost.get();
        double costAmount = maxMagicule * activationCost;

        if (EnergyHelper.isOutOfEnergy(entity, 0.0, costAmount)) return;

        EnergyHelper.drainEnergy(entity, entity, costAmount, true,
                EnergyHelper.DrainType.MAGICULE, EnergyHelper.GainType.NORMAL);

        instance.getOrCreateTag().putBoolean("divine_disruption_active", true);
        instance.getOrCreateTag().putInt("divine_disruption_tick", 0);

        if (entity.level() instanceof ServerLevel serverLevel) {
            spawnActivationBurst(serverLevel, entity);
            serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.PLAYERS, 1.5f, 0.6f);
        }
    }

    @Override
    public boolean onHeld(ManasSkillInstance instance, LivingEntity entity, int mode, int key) {
        if (entity.level().isClientSide) return true;
        if (!(entity instanceof ServerPlayer player)) return true;

        int tick = instance.getOrCreateTag().getInt("divine_disruption_tick");
        tick++;
        instance.getOrCreateTag().putInt("divine_disruption_tick", tick);

        int costInterval = SkillConfig.COMMON.divineDisruptionCostInterval.get();
        if (tick % costInterval == 0) {
            double maxMagicule = EnergyHelper.getMaxMagicule(entity);
            if (maxMagicule <= 0) {
                releaseSkill(instance, entity);
                return false;
            }

            double tickCost = instance.isMastered(entity)
                    ? SkillConfig.COMMON.divineDisruptionTickCostMastered.get()
                    : SkillConfig.COMMON.divineDisruptionTickCost.get();
            double costAmount = maxMagicule * tickCost;

            if (EnergyHelper.isOutOfEnergy(entity, 0.0, costAmount)) {
                releaseSkill(instance, entity);
                return false;
            }
            EnergyHelper.drainEnergy(entity, entity, costAmount, true,
                    EnergyHelper.DrainType.MAGICULE, EnergyHelper.GainType.NORMAL);
        }

        if (entity.level() instanceof ServerLevel serverLevel) {
            applyDisruptionAura(serverLevel, entity);
            spawnHeldParticles(serverLevel, entity, tick);
        }

        return true;
    }

    @Override
    public void onRelease(ManasSkillInstance instance, LivingEntity entity, int mode, int key, int heldTicks) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            spawnReleaseEffect(serverLevel, entity);
            serverLevel.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.2f, 0.5f);
        }
        releaseSkill(instance, entity);
    }

    private void releaseSkill(ManasSkillInstance instance, LivingEntity entity) {
        instance.getOrCreateTag().putBoolean("divine_disruption_active", false);
        instance.getOrCreateTag().putInt("divine_disruption_tick", 0);
    }

    private void applyDisruptionAura(ServerLevel level, LivingEntity caster) {
        double aoeRadius = SkillConfig.COMMON.divineDisruptionAoeRadius.get();
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                caster.getBoundingBox().inflate(aoeRadius),
                e -> e != caster && e.isAlive());

        for (LivingEntity target : targets) {
            nullifyMagicule(target);
        }
    }

    private void nullifyMagicule(LivingEntity target) {
        IExistence existence = TensuraStorages.getExistenceFrom(target);
        if (existence != null) {
            double currentMagicule = existence.getMagicule();
            if (currentMagicule > 0) {
                existence.setMagicule(0);
                existence.markDirty();
            }
        }
    }

    private void spawnActivationBurst(ServerLevel level, LivingEntity caster) {
        double x = caster.getX();
        double y = caster.getY() + 1.0;
        double z = caster.getZ();

        // Central implosion burst
        level.sendParticles(ParticleTypes.REVERSE_PORTAL, x, y, z, 12, 0.3, 0.3, 0.3, 0.15);
        level.sendParticles(ParticleTypes.SCULK_SOUL, x, y, z, 15, 1.0, 1.0, 1.0, 0.08);
        level.sendParticles(ParticleTypes.SOUL, x, y, z, 20, 2.0, 1.5, 2.0, 0.05);
        level.sendParticles(ParticleTypes.DRAGON_BREATH, x, y, z, 10, 1.5, 1.0, 1.5, 0.03);

        // Expanding ring of particles
        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 12) {
            double px = x + Math.cos(angle) * 2.0;
            double pz = z + Math.sin(angle) * 2.0;
            level.sendParticles(ParticleTypes.PORTAL, px, y, pz, 3, 0.1, 0.1, 0.1, 0.05);
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, px, y, pz, 2, 0.0, 0.3, 0.0, 0.08);
        }

        // Vertical pillar of soul energy
        for (double dy = 0; dy < 3.0; dy += 0.3) {
            level.sendParticles(ParticleTypes.SCULK_SOUL, x, y + dy, z, 2, 0.2, 0.0, 0.2, 0.02);
        }
    }

    private void spawnHeldParticles(ServerLevel level, LivingEntity caster, int tick) {
        double x = caster.getX();
        double y = caster.getY() + 1.0;
        double z = caster.getZ();

        double aoeRadius = SkillConfig.COMMON.divineDisruptionAoeRadius.get();
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                caster.getBoundingBox().inflate(aoeRadius),
                e -> e != caster && e.isAlive());

        // Pulsing aura around caster (every 5 ticks)
        if (tick % 5 == 0) {
            double pulseRadius = 1.5 + Math.sin(tick * 0.15) * 0.5;
            for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 8) {
                double px = x + Math.cos(angle) * pulseRadius;
                double pz = z + Math.sin(angle) * pulseRadius;
                level.sendParticles(ParticleTypes.SCULK_SOUL, px, y, pz, 1, 0.0, 0.05, 0.0, 0.01);
            }
        }

        // Tendrils flowing from targets to caster
        for (LivingEntity target : targets) {
            double tx = target.getX();
            double ty = target.getY() + target.getBbHeight() * 0.5;
            double tz = target.getZ();

            double dx = tx - x;
            double dy = ty - y;
            double dz = tz - z;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist < 0.5) dist = 0.5;

            // Particles along the tendril path (fewer per tick for performance)
            int count = Math.max(1, (int)(dist * 0.3));
            for (int i = 0; i < count; i++) {
                double t = (i + 0.5) / count;
                double wave = Math.sin(t * Math.PI * 2 + tick * 0.2) * 0.2;
                double px = x + dx * t + wave;
                double py = y + dy * t + Math.sin(t * Math.PI) * 0.15;
                double pz = z + dz * t + wave;
                level.sendParticles(ParticleTypes.DRAGON_BREATH, px, py, pz, 1, 0.0, 0.0, 0.0, 0.0);
            }

            // Target crackling effect
            if (tick % 4 == 0) {
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK, tx, ty, tz, 4, 0.3, 0.3, 0.3, 0.04);
                level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, tx, ty + 0.5, tz, 2, 0.2, 0.2, 0.2, 0.01);
            }
        }

        // Ambient void particles around caster
        if (tick % 3 == 0) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double r = 1.0 + level.random.nextDouble() * 1.5;
            double px = x + Math.cos(angle) * r;
            double pz = z + Math.sin(angle) * r;
            level.sendParticles(ParticleTypes.PORTAL, px, y + level.random.nextDouble() * 0.5, pz, 1, 0.0, 0.02, 0.0, 0.02);
        }
    }

    private void spawnReleaseEffect(ServerLevel level, LivingEntity caster) {
        double x = caster.getX();
        double y = caster.getY() + 1.0;
        double z = caster.getZ();

        double aoeRadius = SkillConfig.COMMON.divineDisruptionAoeRadius.get();
        // Expanding shockwave of particles
        for (double angle = 0; angle < Math.PI * 2; angle += Math.PI / 16) {
            for (double r = 1.0; r <= aoeRadius; r += 2.0) {
                double px = x + Math.cos(angle) * r;
                double pz = z + Math.sin(angle) * r;
                double speed = 0.05 + r * 0.005;
                double vx = Math.cos(angle) * speed;
                double vz = Math.sin(angle) * speed;
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK, px, y, pz, 1, vx, 0.01, vz, 0.02);
            }
        }

        // Soul fire burst at caster
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 25, 1.5, 1.0, 1.5, 0.06);
        level.sendParticles(ParticleTypes.SCULK_SOUL, x, y, z, 20, 2.0, 1.5, 2.0, 0.04);

        // Dark mist rising
        for (double dy = 0; dy < 4.0; dy += 0.5) {
            level.sendParticles(ParticleTypes.DRAGON_BREATH, x, y + dy, z, 4, 1.0, 0.1, 1.0, 0.01);
        }
    }

    @Override
    public void onLearnSkill(ManasSkillInstance instance, LivingEntity entity) {
    }

    @Override
    public void onForgetSkill(ManasSkillInstance instance, LivingEntity entity) {
    }

    @Override
    public void onRespawn(ManasSkillInstance instance, ServerPlayer player, boolean wasDead) {
    }
}
