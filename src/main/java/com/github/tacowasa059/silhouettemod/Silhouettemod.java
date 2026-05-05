package com.github.tacowasa059.silhouettemod;

import com.github.tacowasa059.silhouettemod.command.SilhouetteCommands;
import com.github.tacowasa059.silhouettemod.network.SilhouetteNetwork;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Silhouettemod.MODID)
public class Silhouettemod {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "silhouettemod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public Silhouettemod() {
        SilhouetteNetwork.register();
        MinecraftForge.EVENT_BUS.register(SilhouetteCommands.class);
    }
}
