package com.github.tacowasa059.silhouettemod.network;

import com.github.tacowasa059.silhouettemod.SilhouetteColor;
import com.github.tacowasa059.silhouettemod.client.ClientSilhouetteTargets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public record SetSilhouetteTargetsPacket(Map<UUID, SilhouetteColor> targets, List<UUID> removals, boolean clear) {
    public static SetSilhouetteTargetsPacket add(Map<UUID, SilhouetteColor> targets, boolean clear) {
        return new SetSilhouetteTargetsPacket(targets, List.of(), clear);
    }

    public static SetSilhouetteTargetsPacket remove(Collection<UUID> removals) {
        return new SetSilhouetteTargetsPacket(Map.of(), List.copyOf(removals), false);
    }

    public static SetSilhouetteTargetsPacket clearAll() {
        return new SetSilhouetteTargetsPacket(Map.of(), List.of(), true);
    }

    public static void encode(SetSilhouetteTargetsPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.clear);
        buffer.writeVarInt(packet.targets.size());
        for (Map.Entry<UUID, SilhouetteColor> target : packet.targets.entrySet()) {
            buffer.writeUUID(target.getKey());
            buffer.writeByte(target.getValue().red());
            buffer.writeByte(target.getValue().green());
            buffer.writeByte(target.getValue().blue());
        }
        buffer.writeVarInt(packet.removals.size());
        for (UUID removal : packet.removals) {
            buffer.writeUUID(removal);
        }
    }

    public static SetSilhouetteTargetsPacket decode(FriendlyByteBuf buffer) {
        boolean clear = buffer.readBoolean();
        int targetSize = buffer.readVarInt();
        Map<UUID, SilhouetteColor> targets = new HashMap<>(targetSize);
        for (int index = 0; index < targetSize; index++) {
            UUID uuid = buffer.readUUID();
            int red = buffer.readUnsignedByte();
            int green = buffer.readUnsignedByte();
            int blue = buffer.readUnsignedByte();
            targets.put(uuid, new SilhouetteColor(red, green, blue));
        }

        int removalSize = buffer.readVarInt();
        List<UUID> removals = new ArrayList<>(removalSize);
        for (int index = 0; index < removalSize; index++) {
            removals.add(buffer.readUUID());
        }
        return new SetSilhouetteTargetsPacket(targets, removals, clear);
    }

    public static void handle(SetSilhouetteTargetsPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSilhouetteTargets.apply(packet)));
        context.setPacketHandled(true);
    }
}
