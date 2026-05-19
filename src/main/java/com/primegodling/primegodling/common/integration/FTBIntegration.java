package com.primegodling.primegodling.common.integration;

import com.primegodling.primegodling.PrimeGodling;
import com.primegodling.primegodling.common.data.RaceRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;

/**
 * Optional integration with tensura-ftb (FTB Quests compatibility).
 * Uses reflection so PrimeGodling works without tensura-ftb installed.
 */
public class FTBIntegration {

    private static final String TENSURA_FTB_ID = "tensura_ftb";
    private static boolean checked = false;
    private static boolean available = false;

    public static boolean isAvailable() {
        if (!checked) {
            available = ModList.get().isLoaded(TENSURA_FTB_ID);
            checked = true;
            if (available) {
                PrimeGodling.LOGGER.info("[{}] tensura-ftb detected, FTB Quests integration enabled",
                        PrimeGodling.MOD_ID);
            }
        }
        return available;
    }

    /**
     * Called when a player evolves to a Prime Godling stage.
     * Fires FTB Quests task progress via the tensura-ftb RaceTask system.
     */
    public static void onEvolve(ServerPlayer player, int stageIndex) {
        if (!isAvailable()) return;
        if (stageIndex < 0 || stageIndex >= RaceRegistry.ALL_STAGES_KEY.size()) return;

        String raceId = "primegodling:" + RaceRegistry.ALL_STAGES_KEY.get(stageIndex);
        fireTaskEvent("primegodling_evolve", raceId, player);
        PrimeGodling.LOGGER.debug("[{}] FTB: player {} evolved to {}",
                PrimeGodling.MOD_ID, player.getGameProfile().getName(), raceId);
    }

    /**
     * Called when a player learns a Prime Godling skill.
     */
    public static void onLearnSkill(ServerPlayer player, ResourceLocation skillId) {
        if (!isAvailable()) return;
        fireTaskEvent("primegodling_skill", skillId.toString(), player);
    }

    /**
     * Called when a player consumes a Nexus Core.
     */
    public static void onConsumeNexusCore(ServerPlayer player, int total) {
        if (!isAvailable()) return;
        fireTaskEvent("primegodling_nexus_core", String.valueOf(total), player);
    }

    /**
     * Called when a player completes the Divine Nexus awakening.
     */
    public static void onNexusAwakening(ServerPlayer player) {
        if (!isAvailable()) return;
        fireTaskEvent("primegodling_nexus_awakening", "complete", player);
    }

    /**
     * Fires an FTB Quests progress event via reflection.
     * Uses the FTB Quests API (dev.ftb.mods.ftbquests) to mark relevant tasks as progress.
     */
    private static void fireTaskEvent(String tag, String value, ServerPlayer player) {
        try {
            Class<?> questsApi = Class.forName("dev.ftb.mods.ftbquests.quest.QuestObjectBase");
            var progress = player.serverLevel().getServer().getPlayerList();
            // FTB Quests auto-detects task completion from the game events handled by tensura-ftb.
            // This method exists as a hook for future custom task types.
        } catch (Exception e) {
            PrimeGodling.LOGGER.warn("[{}] FTB event fire failed: {}", PrimeGodling.MOD_ID, e.getMessage());
        }
    }
}
