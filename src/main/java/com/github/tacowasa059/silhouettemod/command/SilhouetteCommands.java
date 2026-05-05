package com.github.tacowasa059.silhouettemod.command;

import com.github.tacowasa059.silhouettemod.SilhouetteTargets;
import com.github.tacowasa059.silhouettemod.SilhouetteColor;
import com.github.tacowasa059.silhouettemod.network.SetSilhouetteTargetsPacket;
import com.github.tacowasa059.silhouettemod.network.SilhouetteNetwork;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.UUID;

public final class SilhouetteCommands {
    private SilhouetteCommands() {
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("silhouette")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("add")
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .executes(context -> addTargets(context.getSource(), EntityArgument.getEntities(context, "targets"), SilhouetteColor.BLACK))
                                .then(Commands.argument("red", IntegerArgumentType.integer(0, 255))
                                        .then(Commands.argument("green", IntegerArgumentType.integer(0, 255))
                                                .then(Commands.argument("blue", IntegerArgumentType.integer(0, 255))
                                                        .executes(context -> addTargets(context.getSource(), EntityArgument.getEntities(context, "targets"),
                                                                new SilhouetteColor(
                                                                        IntegerArgumentType.getInteger(context, "red"),
                                                                        IntegerArgumentType.getInteger(context, "green"),
                                                                        IntegerArgumentType.getInteger(context, "blue")
                                                                ))))))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .executes(context -> removeTargets(context.getSource(), EntityArgument.getEntities(context, "targets")))))
                .then(Commands.literal("clear")
                        .executes(context -> clearTargets(context.getSource()))));
    }

    private static int addTargets(CommandSourceStack source, Collection<? extends Entity> entities, SilhouetteColor color) {
        SilhouetteTargets targets = SilhouetteTargets.get(source.getServer());
        int changed = 0;
        Map<UUID, SilhouetteColor> changedTargets = new java.util.HashMap<>();
        for (Entity entity : entities) {
            UUID uuid = entity.getUUID();
            if (targets.add(uuid, color)) {
                changed++;
                changedTargets.put(uuid, color);
            }
        }

        if (!changedTargets.isEmpty()) {
            SilhouetteNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), SetSilhouetteTargetsPacket.add(changedTargets, false));
        }

        int result = changed;
        source.sendSuccess(() -> Component.translatable("commands.silhouettemod.silhouette.add", result, color.red(), color.green(), color.blue()), true);
        return result;
    }

    private static int removeTargets(CommandSourceStack source, Collection<? extends Entity> entities) {
        SilhouetteTargets targets = SilhouetteTargets.get(source.getServer());
        int changed = 0;
        List<UUID> changedTargets = new java.util.ArrayList<>();
        for (Entity entity : entities) {
            UUID uuid = entity.getUUID();
            if (targets.remove(uuid)) {
                changed++;
                changedTargets.add(uuid);
            }
        }

        if (!changedTargets.isEmpty()) {
            SilhouetteNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), SetSilhouetteTargetsPacket.remove(changedTargets));
        }

        int result = changed;
        source.sendSuccess(() -> Component.translatable("commands.silhouettemod.silhouette.remove", result), true);
        return result;
    }

    private static int clearTargets(CommandSourceStack source) {
        SilhouetteTargets targets = SilhouetteTargets.get(source.getServer());
        int count = targets.all().size();
        targets.clear();
        SilhouetteNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), SetSilhouetteTargetsPacket.clearAll());
        source.sendSuccess(() -> Component.translatable("commands.silhouettemod.silhouette.clear", count), true);
        return count;
    }

    @SubscribeEvent
    public static void syncToLoggedInPlayer(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SilhouetteTargets targets = SilhouetteTargets.get(player.server);
            if (targets.all().isEmpty()) {
                return;
            }
            SilhouetteNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                    SetSilhouetteTargetsPacket.add(targets.allWithColors(), true));
        }
    }

    @SubscribeEvent
    public static void syncTrackedEntity(PlayerEvent.StartTracking event) {
        Entity target = event.getTarget();
        if (event.getEntity() instanceof ServerPlayer player) {
            SilhouetteTargets targets = SilhouetteTargets.get(player.server);
            if (!targets.contains(target.getUUID())) {
                return;
            }
            SilhouetteNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                    SetSilhouetteTargetsPacket.add(Map.of(target.getUUID(), targets.color(target.getUUID())), false));
        }
    }
}
