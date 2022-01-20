/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.class_235;
import net.minecraft.class_535;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import java.io.IOException;
import org.bleachhack.event.events.EventRenderBlockOutline;
import org.bleachhack.event.events.EventWorldRender;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.mixin.AccessorMinecraftClient;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingColor;
import org.bleachhack.setting.module.SettingMode;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.util.render.Renderer;
import org.bleachhack.util.render.color.QuadColor;
import org.bleachhack.util.shader.ShaderEffectWrapper;
import org.bleachhack.util.shader.ShaderLoader;
import org.bleachhack.util.shader.gl.Framebuffer;
import org.lwjgl.opengl.GL11;

import com.google.gson.JsonSyntaxException;

public class BlockHighlight extends Module {

	private ShaderEffectWrapper shader;

	public BlockHighlight() {
		super("BlockHighlight", KEY_UNBOUND, ModuleCategory.RENDER, "Highlights blocks that you're looking at.",
				new SettingMode("Render", "Shader", "Box").withDesc("The Render mode."),
				new SettingSlider("ShaderFill", 1, 255, 50, 0).withDesc("How opaque the fill on shader mode should be."),
				new SettingSlider("Box", 0, 5, 2, 1).withDesc("How thick the box outline should be."),
				new SettingSlider("BoxFill", 0, 255, 50, 0).withDesc("How opaque the fill on box mode should be."),
				new SettingColor("Color", 0, 128, 128).withDesc("The color of the highlight."));
	}

	@Override
	public void onEnable(boolean inWorld) {
		super.onEnable(inWorld);

		try {
			shader = new ShaderEffectWrapper(
					ShaderLoader.loadEffect(Framebuffer.main, new Identifier("bleachhack", "shaders/post/outline.json")));

			shader.getUniforms().put("Color", i -> {
				if (i != 0)
					return null;

				int[] color = getSetting(4).asColor().getRGBArray();
				return new float[] { color[0] / 255f, color[1] / 255f, color[2] / 255f, getSetting(1).asSlider().getValueInt() / 255f };
			});
		} catch (JsonSyntaxException | IOException e) {
			e.printStackTrace();
			setEnabled(false);
		}
	}

	@BleachSubscribe
	public void onRenderBlockOutline(EventRenderBlockOutline event) {
		event.setCancelled(true);
	}

	@BleachSubscribe
	public void onWorldRender(EventWorldRender.Post event) {
		int mode = getSetting(0).asMode().getMode();

		if (mc.result == null || mc.result.type != class_235.field_602 || mc.world.isAir(mc.result.x, mc.result.y, mc.result.z))
			return;

		Block block = Block.BLOCKS[mc.world.getBlock(mc.result.x, mc.result.y, mc.result.z)];
		int[] color = this.getSetting(4).asColor().getRGBArray();
		if (mode == 0) {
			shader.prepare();
			shader.clearFramebuffer("in");
			shader.clearFramebuffer("swap");
			shader.getShader().getSecondaryTarget("in").bind(false);

			BlockEntity be = mc.world.method_3781(mc.result.x, mc.result.y, mc.result.z);
			BlockEntityRenderer renderer = be != null ? BlockEntityRenderDispatcher.INSTANCE.method_1630(be) : null;
			if (renderer != null) {
				renderer.method_1631(be,
						mc.result.x - BlockEntityRenderDispatcher.field_2189,
						mc.result.y - BlockEntityRenderDispatcher.field_2190,
						mc.result.z - BlockEntityRenderDispatcher.field_2191,
						((AccessorMinecraftClient) mc).getTricker().tickDelta);
			} else {
				GL11.glPushMatrix();
				GL11.glTranslated(
						mc.result.x - BlockEntityRenderDispatcher.field_2189 + 0.5,
						mc.result.y - BlockEntityRenderDispatcher.field_2190 + 0.5,
						mc.result.z - BlockEntityRenderDispatcher.field_2191 + 0.5);
				class_535 rend = new class_535();
				EntityRenderDispatcher.field_2094.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
				rend.method_4320(block);
				rend.method_1453(block, mc.world, mc.result.x, mc.result.y, mc.result.z, 0);
				GL11.glPopMatrix();
			}

			shader.renderShader();
			shader.drawFramebuffer("in");
		} else {
			Box box = block.method_427(mc.world, mc.result.x, mc.result.y, mc.result.z);
			float width = getSetting(2).asSlider().getValueFloat();
			int fill = getSetting(3).asSlider().getValueInt();

			if (width != 0)
				Renderer.drawBoxOutline(box, QuadColor.single(color[0], color[1], color[2], 255), width);

			if (fill != 0)
				Renderer.drawBoxFill(box, QuadColor.single(color[0], color[1], color[2], fill));
		}
	}
}
