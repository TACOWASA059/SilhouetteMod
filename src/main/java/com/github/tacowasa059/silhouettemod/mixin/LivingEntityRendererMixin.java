package com.github.tacowasa059.silhouettemod.mixin;

import com.github.tacowasa059.silhouettemod.client.ClientSilhouetteTargets;
import com.github.tacowasa059.silhouettemod.client.SilhouetteRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    private static final List<?> SILHOUETTE_NO_LAYERS = List.of();

    @Shadow
    @Final
    protected List<?> layers;

    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void silhouettemod$getRenderType(LivingEntity entity, boolean bodyVisible, boolean translucent, boolean glowing,
                                             CallbackInfoReturnable<RenderType> callback) {
        if (!ClientSilhouetteTargets.contains(entity.getUUID())) {
            return;
        }

        callback.setReturnValue(SilhouetteRenderTypes.silhouette(getTexture(entity), ClientSilhouetteTargets.color(entity.getUUID())));
    }

    @Redirect(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;layers:Ljava/util/List;")
    )
    private List<?> silhouettemod$getLayers(LivingEntityRenderer<?, ?> renderer, LivingEntity entity, float entityYaw,
                                            float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        return ClientSilhouetteTargets.contains(entity.getUUID()) ? SILHOUETTE_NO_LAYERS : this.layers;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private ResourceLocation getTexture(LivingEntity entity) {
        return ((EntityRenderer) (Object) this).getTextureLocation(entity);
    }
}
