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
     * Format: "modid:entity_id;chance;minAmount;maxAmount"
     */
    public record MobDropEntry(String entityId, double chance, int minAmount, int maxAmount) {
        public int rollAmount(java.util.Random random) {
            if (minAmount >= maxAmount) return minAmount;
            return minAmount + random.nextInt(maxAmount - minAmount + 1);
        }
    }

    public static class Common {
        public final ModConfigSpec.BooleanValue dropsEnabled;
        public final ModConfigSpec.ConfigValue<List<? extends String>> mobDrops;

        Common(ModConfigSpec.Builder builder) {
            builder.push("drops").comment("Nexus Core mob drop settings");
            dropsEnabled = builder
                    .comment("Enable Nexus Core drops from mobs (disable to use crafting only)")
                    .define("drops_enabled", true);
            mobDrops = builder
                    .comment(
                            "List of mobs that drop Nexus Cores, in the format \"modid:entity_id;chance;minAmount;maxAmount\".",
                            "chance is a decimal between 0.0 and 1.0 (e.g. 0.05 = 5% chance).",
                            "minAmount and maxAmount define the range of cores dropped on successful roll.",
                            "Examples:",
                            "  \"tensura:goblin;0.01;1;1\"        — 1% chance, always 1 core",
                            "  \"tensura:rimuru;1.0;1;3\"          — 100% chance, 1-3 cores",
                            "  \"minecraft:wither;0.50;1;1\"       — 50% chance, 1 core"
                    )
                    .defineListAllowEmpty("mob_drops", NexusDropsConfig::defaultDrops, NexusDropsConfig::validateEntry);
            builder.pop();
        }
    }

    private static List<String> defaultDrops() {
        List<String> list = new ArrayList<>();
        // Bosses — guaranteed 1 drop each
        list.add("tensura:hinata_sakaguchi;1.0;1;1");
        list.add("tensura:ifrit;1.0;1;1");
        list.add("tensura:charybdis;1.0;1;1");
        // Mini-bosses — high chance
        list.add("tensura:wyrm;0.50;1;1");
        list.add("tensura:ogre;0.25;1;1");
        // Normal mobs — low chance
        list.add("tensura:armorsaurus;0.02;1;1");
        list.add("tensura:black_wolf;0.02;1;1");
        list.add("tensura:goblin;0.01;1;1");
        list.add("tensura:orc;0.02;1;1");
        list.add("tensura:lizardman;0.02;1;1");
        return list;
    }

    private static boolean validateEntry(Object obj) {
        if (!(obj instanceof String s)) return false;
        String[] parts = s.split(";");
        if (parts.length < 3 || parts.length > 4) return false;
        if (!parts[0].contains(":")) return false;
        try {
            double chance = Double.parseDouble(parts[1]);
            if (chance < 0.0 || chance > 1.0) return false;
            int minAmount = Integer.parseInt(parts[2]);
            if (minAmount < 1) return false;
            if (parts.length == 4) {
                int maxAmount = Integer.parseInt(parts[3]);
                if (maxAmount < minAmount) return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
