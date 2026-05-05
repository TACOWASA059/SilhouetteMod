package com.github.tacowasa059.silhouettemod.mixin;

import com.github.tacowasa059.silhouettemod.client.ClientSilhouetteTargets;
import com.github.tacowasa059.silhouettemod.client.SilhouetteRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
    @Redirect(
            method = "renderHand",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"),
            require = 2
    )
    private VertexConsumer silhouettemod$getHandBuffer(MultiBufferSource bufferSource, RenderType renderType,
                                                       PoseStack poseStack, MultiBufferSource originalBufferSource, int packedLight,
                                                       AbstractClientPlayer player, ModelPart arm, ModelPart sleeve) {
        if (!ClientSilhouetteTargets.contains(player.getUUID())) {
            return bufferSource.getBuffer(renderType);
        }

        return bufferSource.getBuffer(SilhouetteRenderTypes.silhouette(player.getSkinTextureLocation(), ClientSilhouetteTargets.color(player.getUUID())));
    }
}
