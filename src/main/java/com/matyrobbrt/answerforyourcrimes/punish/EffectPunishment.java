package com.matyrobbrt.answerforyourcrimes.punish;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.function.IntUnaryOperator;

public record EffectPunishment(Holder<MobEffect> effect, IntUnaryOperator duration, IntUnaryOperator amplifier) implements Punishment {
    public EffectPunishment(Holder<MobEffect> effect, IntUnaryOperator duration) {
        this(effect, duration, i -> 0);
    }

    @Override
    public void punish(ServerPlayer player, ResourceLocation crime, int severity) {
        player.addEffect(new MobEffectInstance(effect, duration.applyAsInt(severity), amplifier.applyAsInt(severity)));
    }
}
