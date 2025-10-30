package com.matyrobbrt.answerforyourcrimes.punish;

import com.matyrobbrt.answerforyourcrimes.AnswerForYourCrimes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.IntUnaryOperator;
import java.util.stream.StreamSupport;

public class Punishments {
    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1, Thread.ofVirtual().factory());

    private final float delayChance;
    private final List<WeighedPunishment> punishments;

    private Punishments(float delayChance, List<WeighedPunishment> punishments) {
        this.delayChance = delayChance;
        this.punishments = punishments;
    }

    public void punish(ResourceLocation crime, ServerPlayer player, float chance) {
        if (player.getRandom().nextFloat() > chance) {
            return;
        }
        if (StreamSupport.stream(player.getArmorSlots().spliterator(), false)
                .allMatch(s -> !s.isEmpty() && s.getItem() instanceof ArmorItem ai && ai.getMaterial().is(ArmorMaterials.LEATHER))) {
            return;
        }

        int severity = player.getData(AnswerForYourCrimes.CRIMINAL_RECORD).getOrDefault(crime, 0);

        if (player.getRandom().nextFloat() <= delayChance) {
            // TODO - do this properly with a custom scheduled event queue
            // at least 3 seconds plus a random amount of seconds based on the severity (base of 5 seconds, with 5 more seconds for each previous crime)
            EXECUTOR.schedule(() -> player.server.execute(() -> {
                player.sendSystemMessage(Component.translatable("answerforyourcrimes.not_getting_away"), true);
                execute(player, crime, severity);
            }), 3 + player.getRandom().nextInt((severity + 1) * 5), TimeUnit.SECONDS);
        } else {
            execute(player, crime, severity);
        }
    }

    private void execute(ServerPlayer player, ResourceLocation crime, int severity) {
        int maxWeight = 0;
        for (var punishment : punishments) {
            maxWeight += punishment.weightCalculator().applyAsInt(severity);
        }

        int selection = player.getRandom().nextInt(maxWeight) + 1;
        for (var punishment : punishments) {
            var weight = punishment.weightCalculator().applyAsInt(severity);
            if (weight >= selection) {
                punishment.punishment().punish(player, crime, severity);
                AnswerForYourCrimes.updateRecord(player, map -> map.put(crime, severity + 1));
                AnswerForYourCrimes.inform(player, crime, severity);
                break;
            }
            selection -= weight;
        }
    }

    private record WeighedPunishment(IntUnaryOperator weightCalculator, Punishment punishment) {

    }

    public static final class Builder {
        private final List<WeighedPunishment> punishments = new ArrayList<>();
        private float delayChance;

        public Builder delayChance(float delayChance) {
            this.delayChance = delayChance;
            return this;
        }

        public Builder add(int weight, Punishment punishment) {
            this.punishments.add(new WeighedPunishment($ -> weight, punishment));
            return this;
        }

        public Builder add(IntUnaryOperator weight, Punishment punishment) {
            this.punishments.add(new WeighedPunishment(weight, punishment));
            return this;
        }

        public Punishments build() {
            return new Punishments(delayChance, punishments);
        }
    }
}
