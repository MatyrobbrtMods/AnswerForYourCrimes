package com.matyrobbrt.answerforyourcrimes;

import com.matyrobbrt.answerforyourcrimes.punish.EffectPunishment;
import com.matyrobbrt.answerforyourcrimes.punish.HarmPunishment;
import com.matyrobbrt.answerforyourcrimes.punish.KillPunishment;
import com.matyrobbrt.answerforyourcrimes.punish.LightningPunishment;
import com.matyrobbrt.answerforyourcrimes.punish.MobSpawnPunishment;
import com.matyrobbrt.answerforyourcrimes.punish.Punishments;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Mod(AnswerForYourCrimes.MOD_ID)
public class AnswerForYourCrimes {
    public static final String MOD_ID = "answerforyourcrimes";

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MOD_ID);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Map<ResourceLocation, Integer>>> CRIMINAL_RECORD = ATTACHMENTS.register("criminal_record", () ->
            AttachmentType.<Map<ResourceLocation, Integer>>builder(p -> new HashMap<>())
                    .serialize(Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT)
                            .xmap(HashMap::new, HashMap::new))
                    .copyOnDeath()
                    .build());

    public static final Punishments PUNISHMENTS = new Punishments.Builder()
            .delayChance(0.13f)
            .add(i -> Math.max(0, i - 1) * 2, new KillPunishment())
            .add(5, new LightningPunishment())
            .add(15, new HarmPunishment())
            .add(10, new MobSpawnPunishment(List.of(
                    EntityType.BLAZE,
                    EntityType.WITCH,
                    EntityType.CREEPER,
                    EntityType.CAVE_SPIDER,
                    // Make zombies more common
                    EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.ZOMBIE
            )))
            .add(50, new EffectPunishment(MobEffects.MOVEMENT_SLOWDOWN, i -> (3 + i * 4) * 20, i -> i / 2))
            .add(55, new EffectPunishment(MobEffects.DIG_SLOWDOWN, i -> (3 + i * 4) * 20, i -> i / 2))
            .add(38, new EffectPunishment(MobEffects.BLINDNESS, i -> (8 + i * 5) * 20))
            .add(25, new EffectPunishment(MobEffects.LEVITATION, i -> (2 + i * 2) * 20))
            .add(25, new EffectPunishment(MobEffects.LEVITATION, i -> (2 + i * 2) * 20))
            .build();

    public AnswerForYourCrimes(IEventBus bus) {
        ATTACHMENTS.register(bus);
        NeoForge.EVENT_BUS.register(Listeners.class);
    }

    public static void updateRecord(ServerPlayer player, Consumer<Map<ResourceLocation, Integer>> consumer) {
        var record = player.getData(CRIMINAL_RECORD);
        consumer.accept(record);
        player.setData(CRIMINAL_RECORD, record);
    }

    public static void inform(ServerPlayer player, ResourceLocation crime, int severity) {
        var translationId = "answerforyourcrimes.notice_" + player.getRandom().nextInt(3);
        if (severity > 0) translationId += "_warned";
        player.sendSystemMessage(Component.translatable(translationId, Component.translatable(Util.makeDescriptionId("punishment", crime))));
    }
}
