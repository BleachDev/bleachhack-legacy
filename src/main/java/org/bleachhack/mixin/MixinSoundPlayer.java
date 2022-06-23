/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.mixin;

import net.minecraft.client.sound.SoundLoader;
import org.bleachhack.BleachHack;
import org.bleachhack.event.events.EventSoundPlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundLoader.class)
public class MixinSoundPlayer {

	@Inject(method = "method_5994(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
	private void play(String sound, CallbackInfo ci) {
		EventSoundPlay event = new EventSoundPlay(sound);
		BleachHack.eventBus.post(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}
}
