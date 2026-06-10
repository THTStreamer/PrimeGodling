package com.primegodling.primegodling.network;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClientKillCountersCache {
    private static final Map<UUID, int[]> DATA = new ConcurrentHashMap<>();

    public static void set(UUID uuid, int demonLordKills, boolean rimuruKilled, boolean hinataKilled, int hostileMobKills) {
        DATA.put(uuid, new int[]{demonLordKills, rimuruKilled ? 1 : 0, hinataKilled ? 1 : 0, hostileMobKills});
    }

    public static int getDemonLordKills(UUID uuid) {
        int[] d = DATA.get(uuid);
        return d != null ? d[0] : 0;
    }

    public static boolean isRimuruKilled(UUID uuid) {
        int[] d = DATA.get(uuid);
        return d != null && d[1] == 1;
    }

    public static boolean isHinataKilled(UUID uuid) {
        int[] d = DATA.get(uuid);
        return d != null && d[2] == 1;
    }

    public static int getHostileMobKills(UUID uuid) {
        int[] d = DATA.get(uuid);
        return d != null ? d[3] : 0;
    }
}
