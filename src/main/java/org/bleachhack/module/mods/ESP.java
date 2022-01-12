/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import java.io.IOException;
import java.util.List;

import org.bleachhack.BleachHack;
import org.bleachhack.event.events.EventWorldRender;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.mixin.AccessorMinecraftClient;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingColor;
import org.bleachhack.setting.module.SettingMode;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.render.Renderer;
import org.bleachhack.util.render.color.QuadColor;
import org.bleachhack.util.shader.ShaderEffectWrapper;
import org.bleachhack.util.shader.ShaderLoader;
import org.bleachhack.util.shader.gl.Framebuffer;
import org.bleachhack.util.world.EntityUtils;
import com.google.gson.JsonSyntaxException;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Identifier;

public class ESP extends Module {

	private ShaderEffectWrapper shader;

	public ESP() {
		super("ESP", KEY_UNBOUND, ModuleCategory.RENDER, "Highlights Entities in the world.",
				new SettingMode("Render", "Shader", "Box").withDesc("The Render mode."),
				new SettingSlider("ShaderFill", 1, 255, 50, 0).withDesc("How opaque the fill on shader mode should be."),
				new SettingSlider("Box", 0, 5, 2, 1).withDesc("How thick the box outline should be."),
				new SettingSlider("BoxFill", 0, 255, 50, 0).withDesc("How opaque the fill on box mode should be."),

				new SettingToggle("Players", true).withDesc("Highlights Players.").withChildren(
						new SettingColor("Player Color", 255, 75, 75).withDesc("Outline color for players."),
						new SettingColor("Friend Color", 0, 255, 255).withDesc("Outline color for friends.")),

				new SettingToggle("Mobs", false).withDesc("Highlights Mobs.").withChildren(
						new SettingColor("Color", 128, 25, 128).withDesc("Outline color for mobs.")),

				new SettingToggle("Animals", false).withDesc("Highlights Animals").withChildren(
						new SettingColor("Color", 75, 255, 75).withDesc("Outline color for animals.")),

				new SettingToggle("Items", true).withDesc("Highlights Items.").withChildren(
						new SettingColor("Color", 255, 200, 50).withDesc("Outline color for items.")),

				new SettingToggle("Crystals", true).withDesc("Highlights End Crystals.").withChildren(
						new SettingColor("Color", 255, 50, 255).withDesc("Outline color for crystals.")),

				new SettingToggle("Vehicles", false).withDesc("Highlights Vehicles.").withChildren(
						new SettingColor("Color", 150, 150, 150).withDesc("Outline color for vehicles (minecarts/boats).")));

		try {
			shader = new ShaderEffectWrapper(
					ShaderLoader.loadEffect(Framebuffer.main, new Identifier("bleachhack", "shaders/post/entity_outline.json")));
		} catch (JsonSyntaxException | IOException e) {
			throw new RuntimeException("Failed to initialize ESP Shader! loaded too early?", e);
		}
	}

	@SuppressWarnings("unchecked")
	@BleachSubscribe
	public void onWorldRender(EventWorldRender.Post event) {
		if (getSetting(0).asMode().getMode() == 0) {
			shader.prepare();
			shader.clearFramebuffer("main");

			shader.getShader().getSecondaryTarget("main").bind(false);

			for (Entity e: (List<Entity>) mc.world.getLoadedEntities()) {
				int[] color = getColor(e);

				if (color != null) {
					float f = ((AccessorMinecraftClient) mc).getTricker().tickDelta;
					double var3 = e.prevTickX + (e.x - e.prevTickX) * (double)f;
					double var5 = e.prevTickY + (e.y - e.prevTickY) * (double)f;
					double var7 = e.prevTickZ + (e.z - e.prevTickZ) * (double)f;
					float var9 = e.prevYaw + (e.yaw - e.prevYaw) * f;

					//shader.getUniforms().put("Color", () -> new float[] { color[0] / 255f, color[1] / 255f, color[2] / 255f, getSetting(1).asSlider().getValueInt() / 255f });

					EntityRenderDispatcher.field_2094.method_1519(e).render(
							e, var3 - EntityRenderDispatcher.field_2095, var5 - EntityRenderDispatcher.field_2096, var7 - EntityRenderDispatcher.field_2097, var9, f);
				}
			}

			shader.renderShader();
			shader.drawFramebuffer("main");
		} else {
			float width = getSetting(2).asSlider().getValueFloat();
			int fill = getSetting(3).asSlider().getValueInt();

			for (Entity e: (List<Entity>) mc.world.loadedEntities) {
				int[] color = getColor(e);
				if (color != null) {
					if (width != 0)
						Renderer.drawBoxOutline(e.boundingBox, QuadColor.single(color[0], color[1], color[2], 255), width);

					if (fill != 0)
						Renderer.drawBoxFill(e.boundingBox, QuadColor.single(color[0], color[1], color[2], fill));
				}
			}
		}
	}

	private int[] getColor(Entity e) {
		if (e == mc.field_3805)
			return null;

		if (e instanceof PlayerEntity && getSetting(4).asToggle().getState()) {
			return getSetting(4).asToggle().getChild(BleachHack.friendMang.has(e) ? 1 : 0).asColor().getRGBArray();
		} else if (e instanceof Monster && getSetting(5).asToggle().getState()) {
			return getSetting(5).asToggle().getChild(0).asColor().getRGBArray();
		} else if (EntityUtils.isAnimal(e) && getSetting(6).asToggle().getState()) {
			return getSetting(6).asToggle().getChild(0).asColor().getRGBArray();
		} else if (e instanceof ItemEntity && getSetting(7).asToggle().getState()) {
			return getSetting(7).asToggle().getChild(0).asColor().getRGBArray();
		} else if (e instanceof EndCrystalEntity && getSetting(8).asToggle().getState()) {
			return getSetting(8).asToggle().getChild(0).asColor().getRGBArray();
		} else if ((e instanceof BoatEntity || e instanceof AbstractMinecartEntity) && getSetting(9).asToggle().getState()) {
			return getSetting(9).asToggle().getChild(0).asColor().getRGBArray();
		}

		return null;
	}
}