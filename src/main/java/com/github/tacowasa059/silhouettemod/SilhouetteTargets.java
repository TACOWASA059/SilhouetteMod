package com.github.tacowasa059.silhouettemod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class SilhouetteTargets extends SavedData {
    private static final String DATA_NAME = Silhouettemod.MODID + "_targets";
    private static final String TARGETS_KEY = "Targets";

    private final Map<UUID, SilhouetteColor> targets = new HashMap<>();

    public static SilhouetteTargets get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(SilhouetteTargets::load, SilhouetteTargets::new, DATA_NAME);
    }

    private static SilhouetteTargets load(CompoundTag tag) {
        SilhouetteTargets data = new SilhouetteTargets();
        ListTag list = tag.getList(TARGETS_KEY, Tag.TAG_COMPOUND);
        for (int index = 0; index < list.size(); index++) {
            CompoundTag entry = list.getCompound(index);
            if (entry.hasUUID("UUID")) {
                data.targets.put(entry.getUUID("UUID"), new SilhouetteColor(
                        entry.contains("Red") ? entry.getInt("Red") : 0,
                        entry.contains("Green") ? entry.getInt("Green") : 0,
                        entry.contains("Blue") ? entry.getInt("Blue") : 0
                ));
            }
        }
        return data;
    }

    public boolean add(UUID uuid, SilhouetteColor color) {
        SilhouetteColor previous = targets.put(uuid, color);
        boolean changed = !color.equals(previous);
        if (changed) {
            setDirty();
        }
        return changed;
    }

    public boolean remove(UUID uuid) {
        boolean changed = targets.remove(uuid) != null;
        if (changed) {
            setDirty();
        }
        return changed;
    }

    public void clear() {
        if (!targets.isEmpty()) {
            targets.clear();
            setDirty();
        }
    }

    public boolean contains(UUID uuid) {
        return targets.containsKey(uuid);
    }

    public SilhouetteColor color(UUID uuid) {
        return targets.getOrDefault(uuid, SilhouetteColor.BLACK);
    }

    public Collection<UUID> all() {
        return Set.copyOf(targets.keySet());
    }

    public Map<UUID, SilhouetteColor> allWithColors() {
        return Map.copyOf(targets);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Map.Entry<UUID, SilhouetteColor> target : targets.entrySet()) {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("UUID", target.getKey());
            entry.putInt("Red", target.getValue().red());
            entry.putInt("Green", target.getValue().green());
            entry.putInt("Blue", target.getValue().blue());
            list.add(entry);
        }
        tag.put(TARGETS_KEY, list);
        return tag;
    }
}
