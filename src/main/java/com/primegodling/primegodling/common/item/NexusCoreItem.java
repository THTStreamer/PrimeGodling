package com.primegodling.primegodling.common.item;

import com.primegodling.primegodling.common.data.RaceRegistry;
import com.primegodling.primegodling.common.integration.FTBIntegration;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NexusCoreItem extends Item {

    public NexusCoreItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            int eaten = serverPlayer.getPersistentData().getInt("primegodling:nexus_cores_eaten");
            if (eaten >= RaceRegistry.NEXUS_CORES_REQUIRED) {
                serverPlayer.displayClientMessage(
                    Component.literal("§eYou have already absorbed enough Nexus Cores. Evolve in the Evolution menu!"),
                    true);
                return InteractionResultHolder.consume(stack);
            }
            serverPlayer.getPersistentData().putInt("primegodling:nexus_cores_eaten", eaten + 1);
            int now = eaten + 1;
            FTBIntegration.onConsumeNexusCore(serverPlayer, now);
            serverPlayer.displayClientMessage(
                Component.literal("§eYou absorb the Nexus Core. §7(" + now + "/" + RaceRegistry.NEXUS_CORES_REQUIRED + ")"),
                true);
            if (now < RaceRegistry.NEXUS_CORES_REQUIRED) {
                int remaining = RaceRegistry.NEXUS_CORES_REQUIRED - now;
                serverPlayer.displayClientMessage(
                    Component.literal("§7" + remaining + " more required to unlock the Divine Nexus."),
                    false);
            } else {
                serverPlayer.displayClientMessage(
                    Component.literal("§6✦ All " + RaceRegistry.NEXUS_CORES_REQUIRED + " Nexus Cores absorbed! Evolve to Primordial Supreme God in the Evolution menu."),
                    false);
            }
            stack.shrink(1);
        }
        return InteractionResultHolder.consume(stack);
    }
}
