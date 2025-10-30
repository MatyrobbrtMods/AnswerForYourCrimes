package com.matyrobbrt.answerforyourcrimes.punish;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.phys.Vec3;

public class LightningPunishment implements Punishment {
    @Override
    public void punish(ServerPlayer player, ResourceLocation crime, int severity) {
        LightningBolt lightningbolt = EntityType.LIGHTNING_BOLT.create(player.level());
        if (lightningbolt != null) {
            lightningbolt.moveTo(Vec3.atBottomCenterOf(player.blockPosition()));
            player.level().addFreshEntity(lightningbolt);
        }
    }
}
