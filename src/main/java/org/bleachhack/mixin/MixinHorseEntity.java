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
import org.bleachhack.event.events.EventEntityControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.world.World;

@Mixin(HorseBaseEntity.class)
public abstract class MixinHorseEntity extends AnimalEntity {

	public MixinHorseEntity(World world) {
		super(world);
	}

	@Inject(method = "isSaddled", at = @At("HEAD"), cancellable = true)
	private void isSaddled(CallbackInfoReturnable<Boolean> callback) {
		if (this.rider == MinecraftClient.getInstance().field_3805) {
			EventEntityControl event = new EventEntityControl();
			BleachHack.eventBus.post(event);

			if (event.canBeControlled() != null) {
				callback.setReturnValue(event.canBeControlled());
			}
		}
	}
}