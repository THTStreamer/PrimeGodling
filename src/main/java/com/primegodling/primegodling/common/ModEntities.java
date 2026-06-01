package com.primegodling.primegodling.common;

import com.primegodling.primegodling.PrimeGodling;
import com.primegodling.primegodling.common.entity.CreationBeamProjectile;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModEntities {
    private ModEntities() {}

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, PrimeGodling.MOD_ID);

    public static final Supplier<EntityType<CreationBeamProjectile>> CREATION_BEAM =
            ENTITY_TYPES.register("creation_beam", () -> EntityType.Builder
                    .<CreationBeamProjectile>of(CreationBeamProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(10)
                    .build("creation_beam"));
}
