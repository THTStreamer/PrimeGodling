package com.primegodling.primegodling.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.primegodling.primegodling.network.SyncAwakenedPayload;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class DivineNexusCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        CommandNode<CommandSourceStack> tensura = dispatcher.getRoot().getChild("tensura");
        if (tensura == null) return;
        CommandNode<CommandSourceStack> edit = tensura.getChild("edit");
        if (edit == null) return;
        CommandNode<CommandSourceStack> awakening = edit.getChild("awakening");
        if (awakening == null) return;

        CommandNode<CommandSourceStack> entityArg = awakening.getChild("entity");
        if (entityArg == null) return;

        LiteralArgumentBuilder<CommandSourceStack> divineNexus = LiteralArgumentBuilder.<CommandSourceStack>literal("DivineNexus");

        divineNexus.then(LiteralArgumentBuilder.<CommandSourceStack>literal("awakened")
            .then(RequiredArgumentBuilder.<CommandSourceStack, Boolean>argument("set", BoolArgumentType.bool())
                .requires(src -> src.hasPermission(2))
                .executes(ctx -> {
                    ServerPlayer target = EntityArgument.getPlayer(ctx, "entity");
                    boolean value = BoolArgumentType.getBool(ctx, "set");
                    target.getPersistentData().putBoolean("primegodling:awakened_nexus", value);
                    PacketDistributor.sendToAllPlayers(new SyncAwakenedPayload(target.getUUID(), value));
                    ctx.getSource().sendSuccess(() -> Component.literal("§aSet DivineNexus awakened to " + value + " for " + target.getName().getString()), true);
                    return 1;
                })
            )
        );

        divineNexus.then(LiteralArgumentBuilder.<CommandSourceStack>literal("nexuscores")
            .then(LiteralArgumentBuilder.<CommandSourceStack>literal("set")
                .then(RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("amount", IntegerArgumentType.integer(0))
                    .requires(src -> src.hasPermission(2))
                    .executes(ctx -> {
                        ServerPlayer target = EntityArgument.getPlayer(ctx, "entity");
                        int amount = IntegerArgumentType.getInteger(ctx, "amount");
                        target.getPersistentData().putInt("primegodling:nexus_cores_eaten", amount);
                        ctx.getSource().sendSuccess(() -> Component.literal("§aSet nexus cores eaten to " + amount + " for " + target.getName().getString()), true);
                        return 1;
                    })
                )
            )
            .then(LiteralArgumentBuilder.<CommandSourceStack>literal("add")
                .then(RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("amount", IntegerArgumentType.integer(0))
                    .requires(src -> src.hasPermission(2))
                    .executes(ctx -> {
                        ServerPlayer target = EntityArgument.getPlayer(ctx, "entity");
                        int amount = IntegerArgumentType.getInteger(ctx, "amount");
                        int current = target.getPersistentData().getInt("primegodling:nexus_cores_eaten");
                        target.getPersistentData().putInt("primegodling:nexus_cores_eaten", current + amount);
                        ctx.getSource().sendSuccess(() -> Component.literal("§aAdded " + amount + " nexus cores to " + target.getName().getString() + " (now " + (current + amount) + ")"), true);
                        return 1;
                    })
                )
            )
        );

        entityArg.addChild(divineNexus.build());
    }
}
