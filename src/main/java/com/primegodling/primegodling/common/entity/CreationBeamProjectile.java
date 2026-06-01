package com.primegodling.primegodling.common.entity;

import io.github.manasmods.tensura.entity.magic.beam.BeamProjectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.awt.Color;

public class CreationBeamProjectile extends BeamProjectile {
    public CreationBeamProjectile(EntityType<? extends CreationBeamProjectile> type, Level level) {
        super(type, level);
        beamColorAndSize.clear();
        beamColorAndSize.put(new Color(255, 255, 255, 255), 0.15f);
        beamColorAndSize.put(new Color(255, 250, 235, 230), 0.30f);
        beamColorAndSize.put(new Color(255, 235, 180, 200), 0.48f);
        beamColorAndSize.put(new Color(255, 215, 0, 160), 0.65f);
        beamColorAndSize.put(new Color(255, 200, 0, 100), 0.80f);
        beamColorAndSize.put(new Color(180, 130, 0, 40), 0.92f);
    }
}
