package com.primegodling.primegodling;

import com.primegodling.primegodling.client.ClientProxy;
import com.primegodling.primegodling.common.ModItems;
import com.primegodling.primegodling.common.config.RaceConfig;
import com.primegodling.primegodling.common.config.SkillConfig;
import com.primegodling.primegodling.common.data.ModRaces;
import com.primegodling.primegodling.common.data.ModSkills;
import com.primegodling.primegodling.common.integration.PrimeGodlingConfig;
import com.primegodling.primegodling.common.integration.TensuraIntegration;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.slf4j.Logger;

@Mod(PrimeGodling.MOD_ID)
public class PrimeGodling {
    public static final String MOD_ID = "primegodling";
    public static final ResourceLocation MOD_RL = ResourceLocation.fromNamespaceAndPath(MOD_ID, "root");
    public static final Logger LOGGER = LogUtils.getLogger();

    public PrimeGodling(IEventBus bus) {
        RaceConfig.register();
        SkillConfig.register();

        ModItems.ITEMS.register(bus);
        ModRaces.init();
        ModSkills.init();

        if (FMLLoader.getDist() == Dist.CLIENT) {
            ClientProxy.init(bus);
        }
        TensuraIntegration.register(bus);

        NeoForge.EVENT_BUS.addListener(PrimeGodling::onLivingDeath);
    }

    private static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer killer) {
            LivingEntity killed = event.getEntity();

            if (killed instanceof ServerPlayer targetPlayer) {
                io.github.manasmods.tensura.storage.ep.IExistence targetExistence = io.github.manasmods.tensura.storage.TensuraStorages.getExistenceFrom(targetPlayer);
                if (targetExistence != null && targetExistence.isTrueDemonLord()) {
                    int count = killer.getPersistentData().getInt("primegodling:demon_lord_kills") + 1;
                    killer.getPersistentData().putInt("primegodling:demon_lord_kills", count);
                    killer.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "§dYou killed an Awakened Demon Lord! §7(" + count + "/3)"));
                }
            }

            ResourceLocation killedId = net.minecraft.world.entity.EntityType.getKey(killed.getType());
            if (killedId.equals(ResourceLocation.parse("tensura:hinata_sakaguchi"))) {
                killer.getPersistentData().putBoolean("primegodling:hinata_killed", true);
                killer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§dYou killed Hinata Sakaguchi!"));
            }
            if (killedId.equals(ResourceLocation.parse("tensura:rimuru"))
                    || killedId.equals(ResourceLocation.parse("tensura:rimuru_tempest"))
                    || killedId.equals(ResourceLocation.parse("tensura:true_dragon_rimuru"))) {
                killer.getPersistentData().putBoolean("primegodling:rimuru_killed", true);
                killer.sendSystemMessage(net.minecraft.network.chat.Component.literal("§dYou killed Rimuru Tempest!"));
            }

            if (killed instanceof Monster) {
                int count = killer.getPersistentData().getInt("primegodling:hostile_mob_kills") + 1;
                killer.getPersistentData().putInt("primegodling:hostile_mob_kills", count);
            }
        }
    }
}
