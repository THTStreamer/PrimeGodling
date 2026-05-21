package com.primegodling.primegodling.common.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class NexusDropsConfig {
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
            container.registerConfig(ModConfig.Type.SERVER, SERVER_SPEC, "primegodling/nexus-drops.toml");
        }
    }

    /**
     * Parsed entry from the mobDrops config list.
     */
    public record MobDropEntry(String entityId, double chance) {}

    public static class Common {
        public final ModConfigSpec.BooleanValue dropsEnabled;
        public final ModConfigSpec.ConfigValue<List<? extends String>> mobDrops;

        Common(ModConfigSpec.Builder builder) {
            builder.push("drops").comment("Nexus Core mob drop settings");
            dropsEnabled = builder
                    .comment("Enable Nexus Core drops from mobs (disable to use crafting only)")
                    .define("drops_enabled", false);
            mobDrops = builder
                    .comment(
                            "List of mobs that drop Nexus Cores, in the format \"modid:entity_id;chance\".",
                            "chance is a decimal between 0.0 and 1.0 (e.g. 0.05 = 5% chance).",
                            "Examples:",
                            "  \"tensura:goblin;0.01\"    — 1% chance from goblins",
                            "  \"tensura:ogre;0.05\"      — 5% chance from ogres",
                            "  \"tensura:rimuru;0.50\"    — 50% chance from Rimuru",
                            "  \"minecraft:wither;0.25\"  — 25% chance from vanilla Wither"
                    )
                    .defineListAllowEmpty("mob_drops", NexusDropsConfig::defaultDrops, NexusDropsConfig::validateEntry);
            builder.pop();
        }
    }

    private static List<String> defaultDrops() {
        List<String> list = new ArrayList<>();
        list.add("tensura:armorsaurus;0.02");
        list.add("tensura:black_wolf;0.02");
        list.add("tensura:goblin;0.01");
        list.add("tensura:ogre;0.05");
        list.add("tensura:orc;0.02");
        list.add("tensura:lizardman;0.02");
        list.add("tensura:wyrm;0.08");
        list.add("tensura:dragon;0.10");
        list.add("tensura:ifrit;0.10");
        list.add("tensura:hinata_sakaguchi;0.25");
        list.add("tensura:rimuru;0.50");
        return list;
    }

    private static boolean validateEntry(Object obj) {
        if (!(obj instanceof String s)) return false;
        String[] parts = s.split(";");
        if (parts.length != 2) return false;
        if (!parts[0].contains(":")) return false;
        try {
            double chance = Double.parseDouble(parts[1]);
            return chance >= 0.0 && chance <= 1.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
