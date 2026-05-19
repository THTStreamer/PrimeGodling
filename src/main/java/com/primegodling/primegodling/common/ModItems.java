package com.primegodling.primegodling.common;

import com.primegodling.primegodling.PrimeGodling;
import com.primegodling.primegodling.common.item.NexusCoreItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModItems {

    private ModItems() {}

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, PrimeGodling.MOD_ID);

    public static final Supplier<Item> NEXUS_CORE = ITEMS.register("nexus_core", NexusCoreItem::new);
}
