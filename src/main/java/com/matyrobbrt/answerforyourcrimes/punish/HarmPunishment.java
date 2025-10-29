package com.matyrobbrt.answerforyourcrimes.punish;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class HarmPunishment implements Punishment {
    @Override
    public void punish(ServerPlayer player, ResourceLocation crime, int severity) {
        // We don't intend to kill them, just hurt them
        float hurtAmount = Math.max(
                0f, Math.min(player.getRandom().nextFloat() * (player.getHealth() * (0.5f + severity * 0.04f)), player.getHealth() - 1)
        );
        player.hurt(player.damageSources().generic(), hurtAmount);
    }
}
