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
import org.bleachhack.event.events.EventBlockEntityRender;
import org.bleachhack.event.events.EventEntityRender;
import org.bleachhack.event.events.EventRenderBlockOutline;
import org.bleachhack.event.events.EventWorldRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.CameraView;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
	
	@Shadow private void method_1369(Box box) {}

	@Redirect(method = "method_1370", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"))
	private void render_swap(Profiler profiler, String string) {
		if (string.equals("entities")) {
			BleachHack.eventBus.post(new EventEntityRender.PreAll());
		} else if (string.equals("tileentities")) {
			BleachHack.eventBus.post(new EventEntityRender.PostAll());
			BleachHack.eventBus.post(new EventBlockEntityRender.PreAll());
		}
	}

	@Inject(method = "method_1370", at = @At("HEAD"), cancellable = true)
	private void render_head(Vec3d vec3d, CameraView cameraView, float f, CallbackInfo callback) {
		EventWorldRender.Pre event = new EventWorldRender.Pre(f);
		BleachHack.eventBus.post(event);

		if (event.isCancelled()) {
			callback.cancel();
		}
	}

	@Inject(method = "method_1370", at = @At("RETURN"))
	private void render_return(Vec3d vec3d, CameraView cameraView, float f, CallbackInfo callback) {
		BleachHack.eventBus.post(new EventBlockEntityRender.PostAll());

		EventWorldRender.Post event = new EventWorldRender.Post(f);
		BleachHack.eventBus.post(event);
	}

	@Redirect(method = "method_1370", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;method_1521(Lnet/minecraft/entity/Entity;F)V"))
	private void render_render(EntityRenderDispatcher dispatcher, Entity entity, float tickDelta) {
		EventEntityRender.Single.Pre event = new EventEntityRender.Single.Pre(entity);
		BleachHack.eventBus.post(event);

		if (!event.isCancelled()) {
			dispatcher.method_1521(entity, tickDelta);
		}
	}
	
	@Redirect(method = "method_1380", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;method_1369(Lnet/minecraft/util/math/Box;)V"))
	private void renderOutline_head(WorldRenderer renderer, Box box) {
		EventRenderBlockOutline event = new EventRenderBlockOutline(box);
		BleachHack.eventBus.post(event);

		if (!event.isCancelled()) {
			method_1369(event.getBox());
		}
	}
}
