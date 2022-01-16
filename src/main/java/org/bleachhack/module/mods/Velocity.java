/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import com.google.gson.JsonSyntaxException;
import net.minecraft.class_648;
import net.minecraft.class_667;
import net.minecraft.class_714;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ArrayUtils;
import org.bleachhack.BleachHack;
import org.bleachhack.event.events.EventPacket;
import org.bleachhack.event.events.EventWorldRender;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.mixin.AccessorMinecraftClient;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingColor;
import org.bleachhack.setting.module.SettingMode;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.render.Renderer;
import org.bleachhack.util.render.color.QuadColor;
import org.bleachhack.util.shader.ShaderEffectWrapper;
import org.bleachhack.util.shader.ShaderLoader;
import org.bleachhack.util.shader.gl.Framebuffer;
import org.bleachhack.util.world.EntityUtils;

import java.io.IOException;
import java.util.List;

public class Velocity extends Module {

	public Velocity() {
		super("Velocity", KEY_UNBOUND, ModuleCategory.COMBAT, "no kb");
	}

	@BleachSubscribe
	public void EventPacket(EventPacket.Read event) {
		if (mc.field_3805 != null || mc.world != null) {
			if (event.getPacket() instanceof class_714) { //class_714 velocity
				class_714 packet = (class_714) event.getPacket();
				if (packet.id == mc.field_3805.field_3243) { //field_3243 = the entity id of the player (cringe larp aids) basically only cancels ur velocity
					event.setCancelled(true);
				}
			}
			if (event.getPacket() instanceof class_667) {
				class_667 packet = (class_667) event.getPacket(); //class_667 = ExplosionPacket
				event.setCancelled(true);
			}
		}
	}
}