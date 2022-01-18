/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.util.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;

public class WorldRenderer {

	private static final MinecraftClient mc = MinecraftClient.getInstance();

	// Draws text in the world.
	public static void drawText(String text, int color, double x, double y, double z, double scale, boolean shadow) {
		drawText(text, color, x, y, z, 0, 0, scale, shadow);
	}

	// Draws text in the world.
	public static void drawText(String text, int color, double x, double y, double z, double offX, double offY, double scale, boolean fill) {
		Renderer.setup();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		Renderer.glFrom(x, y, z);

		GL11.glRotatef(-mc.field_3805.yaw, 0, 1, 0);
		GL11.glRotatef(mc.field_3805.pitch, 1, 0, 0);

		GL11.glTranslated(offX, offY, 0);
		GL11.glScalef(-0.025f * (float) scale, -0.025f * (float) scale, 1);

		int halfWidth = mc.textRenderer.getStringWidth(text) / 2;

		if (fill) {
			Tessellator tesselator = Tessellator.INSTANCE;

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			tesselator.method_1405();
			tesselator.method_1401(0.0F, 0.0F, 0.0F, 0.25F);
			tesselator.method_1398((double)(-halfWidth - 1), -1.0D, 0.0D);
			tesselator.method_1398((double)(-halfWidth - 1), 8.0D, 0.0D);
			tesselator.method_1398((double)(halfWidth + 1), 8.0D, 0.0D);
			tesselator.method_1398((double)(halfWidth + 1), -1.0D, 0.0D);
			tesselator.method_1396();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		} else {
			GL11.glPushMatrix();
			GL11.glTranslated(1, 1, 0);
			mc.textRenderer.draw(text, -halfWidth, 0, 0x202020);
			GL11.glPopMatrix();
		}

		mc.textRenderer.draw(text, -halfWidth, 0, color);

		Renderer.cleanup();
	}

	// Draws a 2D gui items somewhere in the world.
	public static void drawGuiItem(double x, double y, double z, double offX, double offY, double scale, ItemStack item) {
		if (item == null) {
			return;
		}

		Renderer.setup();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		Renderer.glFrom(x, y, z);

		GL11.glRotatef(-mc.field_3805.yaw, 0, 1, 0);
		GL11.glRotatef(mc.field_3805.pitch, 1, 0, 0);
		
		GL11.glTranslated(offX + scale * 0.5, offY + scale * 0.5, 0);
		GL11.glScalef(-0.055f * (float) scale, -0.055f * (float) scale, 0.001f);

		//matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180f));

		//mc.getBufferBuilders().getEntityVertexConsumers().draw();

		//Vec3f[] currentLight = shaderLight.clone();
		//DiffuseLighting.disableGuiDepthLighting();

		new ItemRenderer().method_5764(mc.textRenderer, mc.getTextureManager(), item, 0, 0);
		//mc.getItemRenderer().renderItem(item, ModelTransformation.Mode.GUI, 0xF000F0,
		//		OverlayTexture.DEFAULT_UV, matrices, mc.getBufferBuilders().getEntityVertexConsumers(), 0);

		//mc.getBufferBuilders().getEntityVertexConsumers().draw();
		
		Renderer.cleanup();
	}
}
