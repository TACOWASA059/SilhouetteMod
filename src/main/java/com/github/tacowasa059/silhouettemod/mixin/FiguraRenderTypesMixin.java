package com.github.tacowasa059.silhouettemod.mixin;

import com.github.tacowasa059.silhouettemod.SilhouetteColor;
import com.github.tacowasa059.silhouettemod.client.FiguraSilhouetteContext;
import com.github.tacowasa059.silhouettemod.client.SilhouetteRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "org.figuramc.figura.model.rendering.texture.RenderTypes", remap = false)
public abstract class FiguraRenderTypesMixin {
    @Inject(method = "get", at = @At("RETURN"), cancellable = true, require = 0)
    private void silhouettemod$useSilhouetteRenderType(ResourceLocation texture, CallbackInfoReturnable<RenderType> callback) {
        SilhouetteColor color = FiguraSilhouetteContext.get();
        if (color != null && texture != null && callback.getReturnValue() != null) {
            callback.setReturnValue(SilhouetteRenderTypes.silhouette(texture, color));
        }
    }
}
