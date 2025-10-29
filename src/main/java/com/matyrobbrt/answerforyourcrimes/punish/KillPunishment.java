package com.matyrobbrt.answerforyourcrimes.punish;

import com.matyrobbrt.answerforyourcrimes.AnswerForYourCrimes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageType;

public class KillPunishment implements Punishment {
    public static final ResourceKey<DamageType> KARMA = ResourceKey.create(Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(AnswerForYourCrimes.MOD_ID, "karma"));

    @Override
    public void punish(ServerPlayer player, ResourceLocation crime, int severity) {
        player.hurt(player.damageSources().source(KARMA), Float.MAX_VALUE);
    }
}
