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
import org.bleachhack.event.events.EventOpenScreen;
import org.bleachhack.util.shader.gl.Framebuffer;
import org.bleachhack.util.shader.gl.GLX;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.profiler.Profiler;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

	@Shadow public int width;
	@Shadow public int height;

	@Inject(method = "initializeGame", at = @At("RETURN"))
	private void init(CallbackInfo callback) {
		BleachHack.getInstance().postInit();
	}


	@Inject(method = "openScreen", at = @At("HEAD"), cancellable = true)
	private void openScreen(Screen screen, CallbackInfo info) {
		EventOpenScreen event = new EventOpenScreen(screen);
		BleachHack.eventBus.post(event);

		if (event.isCancelled()) {
			info.cancel();
		}
	}

	// Framebuffer bruh moment 57 the despacito
	
	@Inject(method = "initializeGame", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GLX;createContext()V", shift = Shift.AFTER))
	private void initializeGame(CallbackInfo callback) {
		GLX._initCapabilities();
		Framebuffer.main = new Framebuffer(width, height, true);
	}

	@Inject(method = "method_2923", at = @At("HEAD"))
	private void method_2923(int width, int height, CallbackInfo callback) {
		Framebuffer.main.resize(this.width, this.height);
	}

	@Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V"),
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundSystem;method_5989(Lnet/minecraft/entity/LivingEntity;F)V")))
	private void runGameLoop_push(Profiler profiler, String string) {
		if ("display".equals(string)) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			GL11.glEnable(GL11.GL_BLEND);
			GLX.glBlendFuncSeparate(770, 771, 1, 0);
			
			Framebuffer.main.bind(true);
		} else if ("root".equals(string)) {
			Framebuffer.main.endWrite();
			Framebuffer.main.draw(this.width, this.height);
			Display.update();
		}

		profiler.push(string);
	}

	// Prevent the game from calling Display.update() because it by default calls it before the actual rendering???!
	@Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;update()V"))
	private void runGameLoop_update() {
	}
}
