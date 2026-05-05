package com.github.tacowasa059.silhouettemod.network;

import com.github.tacowasa059.silhouettemod.Silhouettemod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class SilhouetteNetwork {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.tryBuild(Silhouettemod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private SilhouetteNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(0, SetSilhouetteTargetsPacket.class, SetSilhouetteTargetsPacket::encode,
                SetSilhouetteTargetsPacket::decode, SetSilhouetteTargetsPacket::handle);
    }
}
