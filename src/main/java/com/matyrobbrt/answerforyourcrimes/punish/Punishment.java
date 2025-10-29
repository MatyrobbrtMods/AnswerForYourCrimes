package com.matyrobbrt.answerforyourcrimes.punish;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public interface Punishment {
    void punish(ServerPlayer player, ResourceLocation crime, int severity);
}
