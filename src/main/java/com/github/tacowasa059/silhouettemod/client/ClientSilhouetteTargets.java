package com.github.tacowasa059.silhouettemod.client;

import com.github.tacowasa059.silhouettemod.network.SetSilhouetteTargetsPacket;
import com.github.tacowasa059.silhouettemod.SilhouetteColor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ClientSilhouetteTargets {
    private static final Map<UUID, SilhouetteColor> TARGETS = new HashMap<>();

    private ClientSilhouetteTargets() {
    }

    public static void apply(SetSilhouetteTargetsPacket packet) {
        if (packet.clear()) {
            TARGETS.clear();
        }

        TARGETS.putAll(packet.targets());
        for (UUID removal : packet.removals()) {
            TARGETS.remove(removal);
        }
    }

    public static boolean contains(UUID uuid) {
        return TARGETS.containsKey(uuid);
    }

    public static SilhouetteColor color(UUID uuid) {
        return TARGETS.getOrDefault(uuid, SilhouetteColor.BLACK);
    }

    public static void clear() {
        TARGETS.clear();
    }
}
