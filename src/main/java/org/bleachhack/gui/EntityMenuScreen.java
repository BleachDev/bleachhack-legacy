/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.gui;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.SurvivalInventoryScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.bleachhack.module.ModuleManager;
import org.bleachhack.module.mods.EntityMenu;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.Boxes;
import org.bleachhack.util.Boxes.Axis;
import org.bleachhack.util.OperatingSystem;
import org.bleachhack.util.collections.MutablePairList;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="https://github.com/lasnikprogram">Lasnik</a>
 */
public class EntityMenuScreen extends Screen {

	private LivingEntity entity;
	private String focusedString;
	private int focusedDot = -1;
	private float yaw, pitch;

	public EntityMenuScreen(LivingEntity entity) {
		this.entity = entity;
	}

	public void init() {
		super.init();
		yaw = client.field_3805.yaw;
		pitch = client.field_3805.pitch;
	}

	public void tick() {
		if (!Mouse.isButtonDown(2)) {
			keyPressed('1', 1);
		}
	}

	@Override
	protected void keyPressed(char character, int code) {
		if (code == 1) {
			// This makes the magic
			if (focusedString != null) {
				DecimalFormat coordFormat = new DecimalFormat("#.##");

				String message = ((EntityMenu) ModuleManager.getModule("EntityMenu"))
						.interactions.getValue(focusedString)
						.replaceAll("%name%", entity.getTranslationKey())
						.replaceAll("%uuid%", entity.getUuid().toString())
						.replaceAll("%health%", String.valueOf((int) entity.getHealth()))
						.replaceAll("%x%", coordFormat.format(entity.x))
						.replaceAll("%y%", coordFormat.format(entity.y))
						.replaceAll("%z%", coordFormat.format(entity.z));

				if (message.startsWith(">suggest ")) {
					client.openScreen(new ChatScreen(message.substring(9)));
				} else if (message.startsWith(">url ")) {
					try {
						OperatingSystem.getOS().open(new URI(message.substring(5)));
					} catch (Exception e) {
						BleachLogger.error("Invalid url \"" + message.substring(5) + "\"");
					}

					client.openScreen(null);
				} else {
					client.field_3805.method_1262(message);
					client.openScreen(null);
				}
			} else {
				client.openScreen(null);
			}
		} else {
			super.keyPressed(character, code);
		}
	}

	@Override
	public boolean shouldPauseGame() {
		return false;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		// Draw entity
		int entitySize = (int) (120 / Boxes.getCornerLength(entity.boundingBox));
		int entityHeight = entitySize / 2 - (int) (10 / Boxes.getAxisLength(entity.boundingBox, Axis.Y));
		SurvivalInventoryScreen.renderEntity(
				width / 2, height / 2 + entityHeight,
				entitySize,
				(float) (width / 2) - mouseX, (float) (height / 2 + entityHeight - 45) - mouseY,
				entity);

		drawDots((int) (Math.min(height, width) / 2 * 0.75), mouseX, mouseY);

		GL11.glPushMatrix();
		GL11.glScalef(2.5f, 2.5f, 1f);
		drawCenteredString(textRenderer, entity.getTranslationKey() /*"Interaction Screen"*/, width / 5, 5, 0xFFFFFFFF);
		GL11.glPopMatrix();

		Vector2 center = new Vector2(width / 2, height / 2);
		Vector2 mouse = new Vector2(mouseX, mouseY).subtract(center).normalize();

		int scale = Math.max(1, client.options.guiScale);

		// Move crossHair based on distance between mouse and center. But with limit
		float hypot = (float) Math.hypot(width / 2 - mouseX, height / 2 - mouseY);
		mouse.multiply(Math.min(hypot, 1f) / scale * 200f);

		client.field_3805.yaw = yaw + mouse.x / 3;
		client.field_3805.pitch = MathHelper.clamp(pitch + mouse.y / 3, -90f, 90f);
		super.render(mouseX, mouseY, delta);
	}

	private void drawDots(int radius, int mouseX, int mouseY) {
		MutablePairList<String, String> map = ((EntityMenu) ModuleManager.getModule("EntityMenu")).interactions;
		List<Vector2> pointList = new ArrayList<>();
		String[] cache = new String[map.size()];

		int i = 0;
		double lowestDistance = Double.MAX_VALUE;
		for (String string: map.getEntries()) {
			// Just some fancy calculations to get the positions of the dots
			double s = (double) i / map.size() * 2 * Math.PI;
			int x = (int) Math.round(radius * Math.cos(s) + width / 2);
			int y = (int) Math.round(radius * Math.sin(s) + height / 2);
			drawTextField(x, y, string);

			// Calculate lowest distance between mouse and dot
			if (Math.hypot(x - mouseX, y - mouseY) < lowestDistance) {
				lowestDistance = Math.hypot(x - mouseX, y - mouseY);
				focusedDot = i;
			}

			cache[i] = string;
			pointList.add(new Vector2(x, y));
			i++;
		}

		// Go through all point and if it is focused -> drawing different color, changing closest string value
		for (Vector2 point: pointList) {
			if (pointList.get(focusedDot).equals(point)) {
				drawDot((int) point.x, (int) point.y, 0xFF4CFF00);
				this.focusedString = cache[focusedDot];
			} else {
				drawDot((int) point.x, (int) point.y, 0xFF0094FF);
			}
		}
	}

	private void drawRect(int startX, int startY, int width, int height, int colorInner,int colorOuter) {
		drawHorizontalLine(startX, startX + width, startY, colorOuter);
		drawHorizontalLine(startX, startX + width, startY + height, colorOuter);
		drawVerticalLine(startX, startY, startY + height, colorOuter);
		drawVerticalLine(startX + width, startY, startY + height, colorOuter);
		fill(startX + 1, startY + 1, startX + width, startY + height, colorInner);
	}

	private void drawTextField(int x, int y, String text) {
		if (x >= width / 2) {
			drawRect(x + 10, y - 8, textRenderer.getStringWidth(text) + 3, 15, 0x80808080, 0xFF000000);
			drawWithShadow(textRenderer, text, x + 12, y - 4, 0xFFFFFFFF);
		} else {
			drawRect(x - 14 - textRenderer.getStringWidth(text), y - 8, textRenderer.getStringWidth(text) + 3, 15, 0x80808080, 0xFF000000);
			drawWithShadow(textRenderer, text, x - 12 - textRenderer.getStringWidth(text), y - 4, 0xFFFFFFFF);
		}
	}

	// Literally drawing it in code
	private void drawDot(int centerX, int centerY, int colorInner) {
		// Black background
		fill(centerX - 1, centerY - 5, centerX + 2, centerY + 6, 0xff000000);
		fill(centerX - 3, centerY - 4, centerX + 4, centerY + 5, 0xff000000);
		fill(centerX - 4, centerY - 3, centerX + 5, centerY + 4, 0xff000000);
		fill(centerX - 5, centerY - 1, centerX + 6, centerY + 2, 0xff000000);

		// Fill
		fill(centerX - 1, centerY - 4, centerX + 2, centerY + 5, colorInner);
		fill(centerX - 3, centerY - 3, centerX + 4, centerY + 4, colorInner);
		fill(centerX - 4, centerY - 1, centerX + 5, centerY + 2, colorInner);

		// Light overlay
		fill(centerX - 1, centerY - 3, centerX + 1, centerY - 2, 0x80ffffff);
		fill(centerX - 2, centerY - 2, centerX - 1, centerY - 1, 0x80ffffff);
		//fill(matrix, centerX - 3, centerY - 1, centerX - 2, centerY, 0x80ffffff);
	}
}


// Creating my own Vector class beacause I couldn't find a good one in minecrafts code
class Vector2 {

	public final float x, y;

	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2 normalize() {
		float mag = getMag();

		if (mag == 0 || mag == 1)
			return this;

		return divide(mag);
	}

	public Vector2 subtract(Vector2 vec) {
		return new Vector2(this.x - vec.x, this.y - vec.y);
	}

	public Vector2 divide(float n) {
		return new Vector2(this.x / n, this.y / n);
	}

	public Vector2 multiply(float n) {
		return new Vector2(this.x * n, this.y * n);
	}

	private float getMag() {
		return (float) Math.sqrt(x * x + y * y);
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + Float.floatToIntBits(x);
		result = 31 * result + Float.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;

		Vector2 other = (Vector2) obj;
		return Float.floatToIntBits(x) == Float.floatToIntBits(other.x)
				&& Float.floatToIntBits(y) == Float.floatToIntBits(other.y);
	}
}
