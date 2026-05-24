package com.primegodling.primegodling.common.item;

import com.primegodling.primegodling.common.awakening.NexusAwakening;
import com.primegodling.primegodling.common.config.ServerConfig;
import com.primegodling.primegodling.common.integration.FTBIntegration;
import com.primegodling.primegodling.network.SyncNexusCoresPayload;
import io.github.manasmods.manascore.race.api.ManasRaceInstance;
import io.github.manasmods.manascore.race.api.RaceAPI;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class NexusCoreItem extends Item {

    private static final int RITUAL_CORE_THRESHOLD = 1000;

    public NexusCoreItem() {
        super(new Item.Properties().stacksTo(64));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            CompoundTag nexusTag = RaceAPI.getRaceFrom(serverPlayer)
                    .getRace()
                    .map(ManasRaceInstance::getOrCreateTag)
                    .orElse(null);
            if (nexusTag == null) return InteractionResultHolder.consume(stack);

            int eaten = nexusTag.getInt("nexus_cores_eaten");
            int now = eaten + 1;

            nexusTag.putInt("nexus_cores_eaten", now);
            FTBIntegration.onConsumeNexusCore(serverPlayer, now);
            int spent = nexusTag.getInt("nexus_cores_spent");
            PacketDistributor.sendToPlayer(serverPlayer, new SyncNexusCoresPayload(serverPlayer.getUUID(), now, spent));
            stack.shrink(1);

            if (now >= RITUAL_CORE_THRESHOLD) {
                String result = NexusAwakening.startRitual(serverPlayer);
                serverPlayer.displayClientMessage(Component.literal(result), true);
                if (!result.startsWith("§6")) {
                    serverPlayer.displayClientMessage(
                        Component.literal("§6✦ All " + RITUAL_CORE_THRESHOLD + " Nexus Cores absorbed! Right-click to start the Divine Nexus ritual."),
                        false);
                }
            } else {
                serverPlayer.displayClientMessage(
                    Component.literal("§eYou absorb the Nexus Core. §7(" + now + "/" + RITUAL_CORE_THRESHOLD + ")"),
                    true);
                int remaining = RITUAL_CORE_THRESHOLD - now;
                serverPlayer.displayClientMessage(
                    Component.literal("§7" + remaining + " more required to unlock the Divine Nexus."),
                    false);
            }
        }
        return InteractionResultHolder.consume(stack);
    }
}
