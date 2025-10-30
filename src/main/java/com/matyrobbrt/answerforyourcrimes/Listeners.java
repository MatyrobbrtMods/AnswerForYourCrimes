package com.matyrobbrt.answerforyourcrimes;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.brewing.PlayerBrewedPotionEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

public class Listeners {
    @SubscribeEvent
    static void onKill(LivingDeathEvent event) {
        if (event.getEntity().getType().getCategory().isFriendly() && event.getSource().getEntity() instanceof ServerPlayer player) {
            punish(player, "murder_friendly", 0.01f);
        }
    }

    @SubscribeEvent
    static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (event.getSlot() == EquipmentSlot.OFFHAND) {
                if (event.getTo().canPerformAction(ItemAbilities.SHIELD_BLOCK)) {
                    punish(player, "using_shield", 0.05f);
                }
            } else if (event.getSlot() != EquipmentSlot.MAINHAND) {
                if (event.getTo().getItem() instanceof ArmorItem it && it.getMaterial().is(ArmorMaterials.CHAIN)) {
                    punish(player, "using_chain_armor", 0.4f);
                }
            }
        }
    }

    @SubscribeEvent
    static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && event.getTo() != event.getFrom()) {
            punish(player, "changing_dimensions", 0.025f);
        }
    }

    @SubscribeEvent
    static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getSource().typeHolder().is(DamageTypes.WITHER) && event.getEntity() instanceof ServerPlayer player && event.getEntity().getRandom().nextBoolean()) {
            player.removeData(AnswerForYourCrimes.CRIMINAL_RECORD);
            player.sendSystemMessage(Component.translatable("answerforyourcrimes.forgiveness"));
        }
    }

    @SubscribeEvent
    static void onItemDrop(ItemTossEvent event) {
        if (event.getPlayer() instanceof ServerPlayer sp) {
            punish(sp, "drop_item", 0.005f);
        }
    }

    @SubscribeEvent
    static void onMount(EntityMountEvent event) {
        if (event.isMounting() && event.getEntityMounting() instanceof ServerPlayer sp) {
            punish(sp, "mounting", 0.01f);
        }
    }

    @SubscribeEvent
    static void onPotionBrewed(PlayerBrewedPotionEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            punish(sp, "brew_potion", 0.02f);
        }
    }

    @SubscribeEvent
    static void onStruckByLightning(EntityStruckByLightningEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            punish(sp, "struck_by_lightning", 0.03f);
        }
    }

    @SubscribeEvent
    static void onBreak(BlockDropsEvent event) {
        if (event.getBreaker() instanceof ServerPlayer sp && event.getTool().isEmpty()) {
            punish(sp, "fist_break", 0.0005f);
        }
    }

    private static void punish(ServerPlayer player, String id, float chance) {
        if (!player.isCreative()) {
            AnswerForYourCrimes.PUNISHMENTS.punish(ResourceLocation.fromNamespaceAndPath(AnswerForYourCrimes.MOD_ID, id), player, chance);
        }
    }
}
