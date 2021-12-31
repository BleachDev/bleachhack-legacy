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
import org.bleachhack.event.events.EventClientMove;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

@Mixin(Entity.class)
public class MixinEntity {
	
	private boolean skipMove;
	@Shadow private void move(double velocityX, double velocityY, double velocityZ) {}

	@Inject(method = "move", at = @At("HEAD"), cancellable = true)
	private void move(double velocityX, double velocityY, double velocityZ, CallbackInfo callback) {
		if (skipMove) {
			skipMove = false;
			return;
		}

		if ((Object) this == MinecraftClient.getInstance().field_3805) {
			EventClientMove event = new EventClientMove(Vec3d.method_604(velocityX, velocityY, velocityZ));
			BleachHack.eventBus.post(event);
			
			if (!event.isCancelled()) {
				skipMove = true;
				callback.cancel();
				move(event.getVec().x, event.getVec().y, event.getVec().z);
			}
		}
	}
}
