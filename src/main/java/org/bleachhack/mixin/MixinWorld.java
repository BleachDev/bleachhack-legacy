/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.mixin;

import java.util.List;

import org.bleachhack.BleachHack;
import org.bleachhack.event.events.EventBlockShape;
import org.bleachhack.util.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

@Mixin(World.class)
public class MixinWorld {

	@Redirect(method = "doesBoxCollide", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;method_413(Lnet/minecraft/world/World;IIILnet/minecraft/util/math/Box;Ljava/util/List;Lnet/minecraft/entity/Entity;)V"))
	private void doesBoxCollide_swap(Block block, World world, int i, int j, int k, Box box, List<Box> list, Entity entity) {
		if (entity == MinecraftClient.getInstance().field_3805) {
			EventBlockShape event = new EventBlockShape(block, new BlockPos(i, j, k), box);
			BleachHack.eventBus.post(event);
			
			if (!event.isCancelled()) {
				block.method_413(world, event.getPos().getX(),  event.getPos().getY(), event.getPos().getZ(), event.getShape(), list, entity);
			}
		} else {
			block.method_413(world, i, j, k, box, list, entity);
		}
	}
}
