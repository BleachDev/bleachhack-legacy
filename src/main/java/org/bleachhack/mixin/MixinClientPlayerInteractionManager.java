/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.mixin;

import org.bleachhack.BleachHack;
import org.bleachhack.event.events.EventBlockBreakCooldown;
import org.bleachhack.event.events.EventInteract;
import org.bleachhack.event.events.EventReach;
import org.bleachhack.util.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

	@Shadow private int blockBreakingCooldown;

	@Redirect(method = "method_1239", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", ordinal = 0))
	private int updateBlockBreakingProgress(ClientPlayerInteractionManager clientPlayerInteractionManager) {
		EventBlockBreakCooldown event = new EventBlockBreakCooldown(blockBreakingCooldown);
		BleachHack.eventBus.post(event);

		return event.getCooldown();
	}

	@Inject(method = "method_1223", at = @At("HEAD"), cancellable = true)
	private void breakBlock(int i, int j, int k, int l, CallbackInfoReturnable<Boolean> callback) {
		EventInteract.BreakBlock event = new EventInteract.BreakBlock(new BlockPos(i, j, k));
		BleachHack.eventBus.post(event);

		if (event.isCancelled()) {
			callback.setReturnValue(false);
		}
	}

	@Inject(method = { "method_1235", "method_1239" }, at = @At("HEAD"), cancellable = true)
	private void attackBlock(int i, int j, int k, int l, CallbackInfo callback) {
		EventInteract.AttackBlock event = new EventInteract.AttackBlock(new BlockPos(i, j, k));
		BleachHack.eventBus.post(event);

		if (event.isCancelled()) {
			callback.cancel();
		}
	}

	@Inject(method = "method_1229", at = @At("HEAD"), cancellable = true)
	private void interactBlock(PlayerEntity playerEntity, World world, ItemStack itemStack, int i, int j, int k, int l, Vec3d vec3d, CallbackInfoReturnable<Boolean> callback) {
		EventInteract.InteractBlock event = new EventInteract.InteractBlock(new BlockPos(i, j, k));
		BleachHack.eventBus.post(event);

		if (event.isCancelled()) {
			callback.setReturnValue(false);
		}
	}

	@Inject(method = "method_1228", at = @At("HEAD"), cancellable = true)
	private void interactItem(PlayerEntity player, World world, ItemStack itemStack, CallbackInfoReturnable<Boolean> callback) {
		EventInteract.InteractItem event = new EventInteract.InteractItem();
		BleachHack.eventBus.post(event);

		if (event.isCancelled()) {
			callback.setReturnValue(false);
		}
	}

	@Inject(method = "getReachDistance", at = @At("RETURN"), cancellable = true)
	private void getReachDistance(CallbackInfoReturnable<Float> callback) {
		EventReach event = new EventReach(callback.getReturnValueF());
		BleachHack.eventBus.post(event);

		callback.setReturnValue(event.getReach());
	}
}
