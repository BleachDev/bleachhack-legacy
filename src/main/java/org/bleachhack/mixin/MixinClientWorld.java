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
import org.bleachhack.event.events.EventKeyPress;
import org.bleachhack.event.events.EventTick;
import org.bleachhack.module.ModuleManager;
import org.bleachhack.util.BleachQueue;
import org.bleachhack.util.InputHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.world.ClientWorld;

@Mixin(ClientWorld.class)
public class MixinClientWorld {
	
	@Unique private InputHandler input = new InputHandler();

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void tick(CallbackInfo info) {
		int[] i = input.poll();
		if (i != null) {
			EventKeyPress.InWorld event = new EventKeyPress.InWorld((char) i[1], i[0]);
			BleachHack.eventBus.post(event);
	
			ModuleManager.handleKeyPress(i[0]);
		}
		
		BleachQueue.nextQueue();

		EventTick event2 = new EventTick();
		BleachHack.eventBus.post(event2);
		if (event2.isCancelled()) {
			info.cancel();
		}
	}
}
