package com.github.tacowasa059.silhouettemod.mixin;

import com.github.tacowasa059.silhouettemod.SilhouetteColor;
import com.github.tacowasa059.silhouettemod.client.ClientSilhouetteTargets;
import com.github.tacowasa059.silhouettemod.client.FiguraSilhouetteContext;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.UUID;

@Pseudo
@Mixin(targets = "org.figuramc.figura.model.rendering.AvatarRenderer", remap = false)
public abstract class FiguraAvatarRendererMixin {
    @Inject(
            method = "setupRenderer(Lorg/figuramc/figura/model/rendering/PartFilterScheme;Lnet/minecraft/client/renderer/MultiBufferSource;FIFIZZ)V",
            at = @At("HEAD"),
            require = 0
    )
    private void silhouettemod$setFiguraRenderContext(CallbackInfo callback) {
        UUID target = silhouettemod$getTargetUuid();
        if (target == null) {
            FiguraSilhouetteContext.set(null);
            return;
        }
        FiguraSilhouetteContext.set(ClientSilhouetteTargets.color(target));
    }

    @Unique
    private UUID silhouettemod$getTargetUuid() {
        try {
            Object entity = silhouettemod$getField((Object) this, "entity");
            if (entity instanceof Entity minecraftEntity && ClientSilhouetteTargets.contains(minecraftEntity.getUUID())) {
                return minecraftEntity.getUUID();
            }

            Object avatar = silhouettemod$getField((Object) this, "avatar");
            Object owner = avatar == null ? null : silhouettemod$getField(avatar, "owner");
            if (owner instanceof UUID uuid && ClientSilhouetteTargets.contains(uuid)) {
                return uuid;
            }
        } catch (ReflectiveOperationException ignored) {
        }
        return null;
    }

    @Unique
    private static Object silhouettemod$getField(Object object, String name) throws ReflectiveOperationException {
        Class<?> type = object.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(name);
                field.setAccessible(true);
                return field.get(object);
            } catch (NoSuchFieldException ignored) {
                type = type.getSuperclass();
            }
        }
        return null;
    }
}
