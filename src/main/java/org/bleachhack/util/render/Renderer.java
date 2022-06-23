/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.util.render;

import org.bleachhack.mixin.AccessorMinecraftClient;
import org.bleachhack.util.BlockPos;
import org.bleachhack.util.Boxes;
import org.bleachhack.util.render.color.LineColor;
import org.bleachhack.util.render.color.QuadColor;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Renderer {

	// -------------------- Fill + Outline Boxes --------------------

	public static void drawBoxBoth(BlockPos blockPos, QuadColor color, float lineWidth, Direction... excludeDirs) {
		drawBoxBoth(blockPos.toBox(), color, lineWidth, excludeDirs);
	}

	public static void drawBoxBoth(Box box, QuadColor color, float lineWidth, Direction... excludeDirs) {
		QuadColor outlineColor = color.clone();
		outlineColor.overwriteAlpha(255);

		drawBoxBoth(box, color, outlineColor, lineWidth, excludeDirs);
	}

	public static void drawBoxBoth(BlockPos blockPos, QuadColor fillColor, QuadColor outlineColor, float lineWidth, Direction... excludeDirs) {
		drawBoxBoth(blockPos.toBox(), fillColor, outlineColor, lineWidth, excludeDirs);
	}

	public static void drawBoxBoth(Box box, QuadColor fillColor, QuadColor outlineColor, float lineWidth, Direction... excludeDirs) {
		drawBoxFill(box, fillColor, excludeDirs);
		drawBoxOutline(box, outlineColor, lineWidth, excludeDirs);
	}

	// -------------------- Fill Boxes --------------------

	public static void drawBoxFill(BlockPos blockPos, QuadColor color, Direction... excludeDirs) {
		drawBoxFill(blockPos.toBox(), color, excludeDirs);
	}

	public static void drawBoxFill(Box box, QuadColor color, Direction... excludeDirs) {
		setup();

		glFrom(box.minX, box.minY, box.minZ);

		Tessellator tessellator = Tessellator.INSTANCE;

		// Fill
		tessellator.method_1408(7);
		Vertexer.vertexBoxQuads(tessellator, Boxes.moveToZero(box), color, excludeDirs);
		tessellator.method_1396();

		cleanup();
	}

	// -------------------- Outline Boxes --------------------

	public static void drawBoxOutline(BlockPos blockPos, QuadColor color, float lineWidth, Direction... excludeDirs) {
		drawBoxOutline(blockPos.toBox(), color, lineWidth, excludeDirs);
	}

	public static void drawBoxOutline(Box box, QuadColor color, float lineWidth, Direction... excludeDirs) {
		setup();

		glFrom(box.minX, box.minY, box.minZ);

		Tessellator tessellator = Tessellator.INSTANCE;

		// Outline
		tessellator.method_1408(1);
		Vertexer.vertexBoxLines(tessellator, Boxes.moveToZero(box), color, excludeDirs);
		tessellator.method_1396();

		cleanup();
	}

	// -------------------- Quads --------------------

	public static void drawQuadFill(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, int cullMode, QuadColor color) {
		setup();

		glFrom(x1, y1, z1);

		Tessellator tessellator = Tessellator.INSTANCE;

		// Fill
		tessellator.method_1408(7);
		Vertexer.vertexQuad(tessellator,
				0f, 0f, 0f,
				(float) (x2 - x1), (float) (y2 - y1), (float) (z2 - z1),
				(float) (x3 - x1), (float) (y3 - y1), (float) (z3 - z1),
				(float) (x4 - x1), (float) (y4 - y1), (float) (z4 - z1),
				cullMode, color);
		tessellator.method_1396();

		cleanup();
	}

	public static void drawQuadOutline(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, float lineWidth, QuadColor color) {
		setup();

		glFrom(x1, y1, z1);

		Tessellator tessellator = Tessellator.INSTANCE;

		int[] colors = color.getAllColors();

		// Outline
		
		GL11.glLineWidth(lineWidth);

		tessellator.method_1408(1);
		Vertexer.vertexLine(tessellator, 0f, 0f, 0f, (float) (x2 - x1), (float) (y2 - y1), (float) (z2 - z1), LineColor.gradient(colors[0], colors[1]));
		Vertexer.vertexLine(tessellator, (float) (x2 - x1), (float) (y2 - y1), (float) (z2 - z1), (float) (x3 - x1), (float) (y3 - y1), (float) (z3 - z1), LineColor.gradient(colors[1], colors[2]));
		Vertexer.vertexLine(tessellator, (float) (x3 - x1), (float) (y3 - y1), (float) (z3 - z1), (float) (x4 - x1), (float) (y4 - y1), (float) (z4 - z1), LineColor.gradient(colors[2], colors[3]));
		Vertexer.vertexLine(tessellator, (float) (x4 - x1), (float) (y4 - y1), (float) (z4 - z1), 0f, 0f, 0f, LineColor.gradient(colors[3], colors[0]));
		tessellator.method_1396();

		cleanup();
	}

	// -------------------- Lines --------------------

	public static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2, LineColor color, float width) {
		setup();

		glFrom(x1, y1, z1);

		Tessellator tessellator = Tessellator.INSTANCE;

		// Line
		GL11.glLineWidth(width);

		tessellator.method_1408(3);
		Vertexer.vertexLine(tessellator, 0f, 0f, 0f, (float) (x2 - x1), (float) (y2 - y1), (float) (z2 - z1), color);
		tessellator.method_1396();

		cleanup();
	}

	// -------------------- Utils --------------------

	public static void glFrom(double x, double y, double z) {
		//Entity camera = MinecraftClient.getInstance().field_6279;
		//GL11.glRotatef(camera.pitch, 0f, 0f, 1f);
		//GL11.glRotatef(camera.yaw + 180.0F, 1f, 0f, 0f);

		GL11.glTranslated(x - EntityRenderDispatcher.cameraX, y - EntityRenderDispatcher.cameraY, z - EntityRenderDispatcher.cameraZ);
	}

	public static Vec3d getInterpolationOffset(Entity e) {
		/*if (MinecraftClient.getInstance().isPaused()) {
			return Vec3d.ZERO;
		}*/

		double tickDelta = ((AccessorMinecraftClient) MinecraftClient.getInstance()).getTricker().tickDelta;
		return Vec3d.method_604(
				e.x - lerp(tickDelta, e.prevTickX, e.x),
				e.y - lerp(tickDelta, e.prevTickY, e.y),
				e.z - lerp(tickDelta, e.prevTickZ, e.z));
	}

	public static void setup() {
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthFunc(GL11.GL_ALWAYS);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glShadeModel(GL11.GL_SMOOTH);
	}

	public static void cleanup() {
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_FOG);
		GL11.glPopMatrix();
	}
	
	private static double lerp(double delta, double start, double end) {
		return start + delta * (end - start);
	}
}
