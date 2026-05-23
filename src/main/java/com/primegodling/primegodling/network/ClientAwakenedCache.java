package com.primegodling.primegodling.network;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ClientAwakenedCache {
    private static final Map<UUID, Boolean> AWAKENED = new ConcurrentHashMap<>();

    public static void set(UUID uuid, boolean awakened) {
        AWAKENED.put(uuid, awakened);
    }

    public static boolean isAwakened(UUID uuid) {
        return AWAKENED.getOrDefault(uuid, false);
    }
}
