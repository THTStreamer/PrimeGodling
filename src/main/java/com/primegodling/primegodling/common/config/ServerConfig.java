package com.primegodling.primegodling.common.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ServerConfig {
    public static final ModConfigSpec SERVER_SPEC;
    public static final Common COMMON;

    static {
        Pair<Common, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON = pair.getLeft();
        SERVER_SPEC = pair.getRight();
    }

    public static void register() {
        ModContainer container = ModLoadingContext.get().getActiveContainer();
        if (container != null) {
            container.registerConfig(ModConfig.Type.SERVER, SERVER_SPEC, "primegodling/server.toml");
        }
    }

    public static class Common {
        public final ModConfigSpec.BooleanValue craftingEnabled;

        Common(ModConfigSpec.Builder builder) {
            builder.push("general").comment("General mod settings");
            craftingEnabled = builder
                    .comment("Enable the Nexus Core crafting recipe (echo shards + diamonds + nether star)")
                    .define("crafting_enabled", true);
            builder.pop();
        }
    }
}
