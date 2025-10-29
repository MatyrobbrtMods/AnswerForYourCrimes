package com.matyrobbrt.answerforyourcrimes.punish;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;

import java.util.List;

public record MobSpawnPunishment(List<EntityType<?>> possibilities) implements Punishment {
    @Override
    public void punish(ServerPlayer player, ResourceLocation crime, int severity) {
        var count = 1 + player.getRandom().nextInt(severity / 3);
        for (int i = 0; i < count; i++) {
            var chosenType = Util.getRandom(possibilities, player.getRandom());
            chosenType.spawn(player.serverLevel(), player.blockPosition(), MobSpawnType.REINFORCEMENT);
        }
    }
}
