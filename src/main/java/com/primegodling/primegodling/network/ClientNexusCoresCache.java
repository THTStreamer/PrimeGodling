package com.primegodling.primegodling.network;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClientNexusCoresCache {
    private static final Map<UUID, int[]> DATA = new ConcurrentHashMap<>();

    public static void set(UUID uuid, int eaten, int spent) {
        DATA.put(uuid, new int[]{eaten, spent});
    }

    public static int getEaten(UUID uuid) {
        int[] d = DATA.get(uuid);
        return d != null ? d[0] : 0;
    }

    public static int getSpent(UUID uuid) {
        int[] d = DATA.get(uuid);
        return d != null ? d[1] : 0;
    }
}
