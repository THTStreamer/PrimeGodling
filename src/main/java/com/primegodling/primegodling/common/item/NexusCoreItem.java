package com.primegodling.primegodling.common.item;

import com.primegodling.primegodling.common.awakening.NexusAwakening;
import com.primegodling.primegodling.common.config.ServerConfig;
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

    private static final int RITUAL_CORE_THRESHOLD = 5000;

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
            int eaten = serverPlayer.getPersistentData().getInt("primegodling:nexus_cores_eaten");

            if (eaten >= RITUAL_CORE_THRESHOLD) {
                String result = NexusAwakening.startRitual(serverPlayer);
                serverPlayer.displayClientMessage(Component.literal(result), true);
                if (result.startsWith("§6")) {
                    stack.shrink(1);
                }
                return InteractionResultHolder.consume(stack);
            }

            serverPlayer.getPersistentData().putInt("primegodling:nexus_cores_eaten", eaten + 1);
            int now = eaten + 1;
            FTBIntegration.onConsumeNexusCore(serverPlayer, now);
            serverPlayer.displayClientMessage(
                Component.literal("§eYou absorb the Nexus Core. §7(" + now + "/" + RITUAL_CORE_THRESHOLD + ")"),
                true);
            if (now < RITUAL_CORE_THRESHOLD) {
                int remaining = RITUAL_CORE_THRESHOLD - now;
                serverPlayer.displayClientMessage(
                    Component.literal("§7" + remaining + " more required to unlock the Divine Nexus."),
                    false);
            } else {
                serverPlayer.displayClientMessage(
                    Component.literal("§6✦ All " + RITUAL_CORE_THRESHOLD + " Nexus Cores absorbed! Right-click again to start the Divine Nexus ritual."),
                    false);
            }
            stack.shrink(1);
        }
        return InteractionResultHolder.consume(stack);
    }
}
