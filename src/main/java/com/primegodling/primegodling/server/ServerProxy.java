package com.primegodling.primegodling.server;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * Server-side proxy — stub kept for future server-only logic such as
 * tick-based EP-checks, Divine Nexus crafting, and mandatory side-guards.
 */
public class ServerProxy {
    public static void init() {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            // serverlogic
        }
    }
}