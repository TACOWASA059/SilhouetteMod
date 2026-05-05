package com.github.tacowasa059.silhouettemod.client;

import com.github.tacowasa059.silhouettemod.Silhouettemod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Silhouettemod.MODID, value = Dist.CLIENT)
public final class ClientSilhouetteEvents {
    private ClientSilhouetteEvents() {
    }

    @SubscribeEvent
    public static void clearTargetsOnLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientSilhouetteTargets.clear();
    }
}
