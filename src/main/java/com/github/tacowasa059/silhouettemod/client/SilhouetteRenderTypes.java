package com.github.tacowasa059.silhouettemod.client;

import com.github.tacowasa059.silhouettemod.Silhouettemod;
import com.github.tacowasa059.silhouettemod.SilhouetteColor;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = Silhouettemod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SilhouetteRenderTypes extends RenderType {
    private static ShaderInstance silhouetteShader;

    private static final ShaderStateShard SILHOUETTE_SHADER = new ShaderStateShard(() -> silhouetteShader);

    private static final Function<Key, RenderType> SILHOUETTE = Util.memoize(key -> RenderType.create(
            Silhouettemod.MODID + "_silhouette",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            256,
            true,
            false,
            CompositeState.builder()
                    .setShaderState(SILHOUETTE_SHADER)
                    .setTextureState(new TextureStateShard(key.texture(), false, false))
                    .setTexturingState(new TexturingStateShard("silhouette_color", () -> setColor(key.color()), () -> setColor(SilhouetteColor.BLACK)))
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(false)
    ));

    private SilhouetteRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling,
                                  boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static RenderType silhouette(ResourceLocation texture, SilhouetteColor color) {
        return SILHOUETTE.apply(new Key(texture, color));
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(),
                ResourceLocation.tryBuild(Silhouettemod.MODID, "rendertype_silhouette"),
                DefaultVertexFormat.NEW_ENTITY), shader -> silhouetteShader = shader);
    }

    private static void setColor(SilhouetteColor color) {
        if (silhouetteShader != null && silhouetteShader.getUniform("SilhouetteColor") != null) {
            silhouetteShader.getUniform("SilhouetteColor").set(color.redFloat(), color.greenFloat(), color.blueFloat(), 1.0F);
        }
    }

    private record Key(ResourceLocation texture, SilhouetteColor color) {
    }
}
