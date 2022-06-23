/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.gui.window;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.bleachhack.gui.window.widget.WindowWidget;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class Window {

	public int x1;
	public int y1;
	public int x2;
	public int y2;

	public String title;
	public ItemStack icon;

	public boolean closed;
	public boolean selected = false;

	private List<WindowWidget> widgets = new ArrayList<>();

	protected boolean dragging;
	protected int dragOffX;
	protected int dragOffY;

	public Window(int x1, int y1, int x2, int y2, String title, ItemStack icon) {
		this(x1, y1, x2, y2, title, icon, false);
	}

	public Window(int x1, int y1, int x2, int y2, String title, ItemStack icon, boolean closed) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.title = title;
		this.icon = icon;
		this.closed = closed;
	}

	public List<WindowWidget> getWidgets() {
		return widgets;
	}

	public <T extends WindowWidget> T addWidget(T widget) {
		widgets.add(widget);
		return widget;
	}

	public void render(int mouseX, int mouseY) {
		TextRenderer textRend = MinecraftClient.getInstance().textRenderer;

		if (dragging) {
			x2 = (x2 - x1) + mouseX - dragOffX - Math.min(0, mouseX - dragOffX);
			y2 = (y2 - y1) + mouseY - dragOffY - Math.min(0, mouseY - dragOffY);
			x1 = Math.max(0, mouseX - dragOffX);
			y1 = Math.max(0, mouseY - dragOffY);
		}

		drawBackground(mouseX, mouseY, textRend);

		for (WindowWidget w : widgets) {
			if (w.shouldRender(x1, y1, x2, y2)) {
				w.render(x1, y1, mouseX, mouseY);
			}
		}

		boolean blockItem = icon != null && icon.getItem() instanceof BlockItem;

		/* window icon */
		if (icon != null) {
			GL11.glPushMatrix();
			GL11.glScalef(0.5f, 0.5f, 1f);
			new ItemRenderer().method_5764(MinecraftClient.getInstance().textRenderer, MinecraftClient.getInstance().getTextureManager(), icon, (x1 + (blockItem ? 3 : 2)) * 2, (y1 + 2) * 2);
			DiffuseLighting.disable();
			GL11.glPopMatrix();
		}

		/* window title */
		textRend.draw(title,
				x1 + (icon == null || icon == null ? 4 : (blockItem ? 15 : 14)), y1 + 3, -1, true);
	}

	protected void drawBackground(int mouseX, int mouseY, TextRenderer textRend) {
		/* background */
		DrawableHelper.fill(x1, y1 + 1, x1 + 1, y2 - 1, 0xff6060b0);
		horizontalGradient(x1 + 1, y1, x2 - 1, y1 + 1, 0xff6060b0, 0xff8070b0);
		DrawableHelper.fill(x2 - 1, y1 + 1, x2, y2 - 1, 0xff8070b0);
		horizontalGradient(x1 + 1, y2 - 1, x2 - 1, y2, 0xff6060b0, 0xff8070b0);

		DrawableHelper.fill(x1 + 1, y1 + 12, x2 - 1, y2 - 1, 0x90606090);

		/* title bar */
		horizontalGradient(x1 + 1, y1 + 1, x2 - 1, y1 + 12, (selected ? 0xff6060b0 : 0xff606060), (selected ? 0xff8070b0 : 0xffa0a0a0));

		/* buttons */
		textRend.draw("x", x2 - 10, y1 + 3, 0);
		textRend.draw("x", x2 - 11, y1 + 2, -1);

		textRend.draw("_", x2 - 21, y1 + 2, 0);
		textRend.draw("_", x2 - 22, y1 + 1, -1);
	}

	public boolean shouldClose(int mouseX, int mouseY) {
		return selected && mouseX > x2 - 23 && mouseX < x2 && mouseY > y1 + 2 && mouseY < y1 + 12;
	}

	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (mouseX >= x1 && mouseX <= x2 - 2 && mouseY >= y1 && mouseY <= y1 + 11) {
			dragging = true;
			dragOffX = (int) mouseX - x1;
			dragOffY = (int) mouseY - y1;
		}

		if (selected) {
			try {
				for (WindowWidget w : widgets) {
					if (w.shouldRender(x1, y1, x2, y2)) {
						w.mouseClicked(x1, y1, (int) mouseX, (int) mouseY, button);
					}
				}
			} catch (ConcurrentModificationException ignored) {}
		}
	}

	public void mouseReleased(double mouseX, double mouseY, int button) {
		dragging = false;

		if (selected) {
			for (WindowWidget w : widgets) {
				if (w.shouldRender(x1, y1, x2, y2)) {
					w.mouseReleased(x1, y1, (int) mouseX, (int) mouseY, button);
				}
			}
		}
	}

	public void tick() {
		for (WindowWidget w : widgets) {
			w.tick();
		}
	}

	public void charTyped(char chr, int modifiers) {
		if (selected) {
			for (WindowWidget w : widgets) {
				w.charTyped(chr, modifiers);
			}
		}
	}

	public void keyPressed(int keyCode, int scanCode, int modifiers) {
		if (selected) {
			for (WindowWidget w : widgets) {
				w.keyPressed(keyCode, scanCode, modifiers);
			}
		}
	}

	public static void fill(int x1, int y1, int x2, int y2) {
		fill(x1, y1, x2, y2, 0xff6060b0, 0xff8070b0, 0x00000000);
	}

	public static void fill(int x1, int y1, int x2, int y2, int fill) {
		fill(x1, y1, x2, y2, 0xff6060b0, 0xff8070b0, fill);
	}

	public static void fill(int x1, int y1, int x2, int y2, int colTop, int colBot, int colFill) {
		DrawableHelper.fill(x1, y1 + 1, x1 + 1, y2 - 1, colTop);
		DrawableHelper.fill(x1 + 1, y1, x2 - 1, y1 + 1, colTop);
		DrawableHelper.fill(x2 - 1, y1 + 1, x2, y2 - 1, colBot);
		DrawableHelper.fill(x1 + 1, y2 - 1, x2 - 1, y2, colBot);
		DrawableHelper.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, colFill);
	}

	public static void horizontalGradient(int x1, int y1, int x2, int y2, int color1, int color2) {
		float alpha1 = (color1 >> 24 & 255) / 255.0F;
		float red1   = (color1 >> 16 & 255) / 255.0F;
		float green1 = (color1 >> 8 & 255) / 255.0F;
		float blue1  = (color1 & 255) / 255.0F;
		float alpha2 = (color2 >> 24 & 255) / 255.0F;
		float red2   = (color2 >> 16 & 255) / 255.0F;
		float green2 = (color2 >> 8 & 255) / 255.0F;
		float blue2  = (color2 & 255) / 255.0F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(3008);
		GL11.glBlendFunc(770, 771);
		GL11.glShadeModel(7425);
		Tessellator tessellator = Tessellator.INSTANCE;
		tessellator.method_1405();
		tessellator.method_1401(red1, green1, blue1, alpha1);
		tessellator.method_1398(x1, y1, 0);
		tessellator.method_1398(x1, y2, 0);
		tessellator.method_1401(red2, green2, blue2, alpha2);
		tessellator.method_1398(x2, y2, 0);
		tessellator.method_1398(x2, y1, 0);
		tessellator.method_1396();
		GL11.glShadeModel(7424);
		GL11.glDisable(3042);
		GL11.glEnable(3008);
		GL11.glEnable(3553);
	}

	public static void verticalGradient(int x1, int y1, int x2, int y2, int color1, int color2) {
		float alpha1 = (color1 >> 24 & 255) / 255.0F;
		float red1   = (color1 >> 16 & 255) / 255.0F;
		float green1 = (color1 >> 8 & 255) / 255.0F;
		float blue1  = (color1 & 255) / 255.0F;
		float alpha2 = (color2 >> 24 & 255) / 255.0F;
		float red2   = (color2 >> 16 & 255) / 255.0F;
		float green2 = (color2 >> 8 & 255) / 255.0F;
		float blue2  = (color2 & 255) / 255.0F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(3008);
		GL11.glBlendFunc(770, 771);
		GL11.glShadeModel(7425);
		Tessellator tessellator = Tessellator.INSTANCE;
		tessellator.method_1405();
		tessellator.method_1401(red1, green1, blue1, alpha1);
		tessellator.method_1398(x2, y1, 0);
		tessellator.method_1398(x1, y1, 0);
		tessellator.method_1401(red2, green2, blue2, alpha2);
		tessellator.method_1398(x1, y2, 0);
		tessellator.method_1398(x2, y2, 0);
		tessellator.method_1396();
		GL11.glShadeModel(7424);
		GL11.glDisable(3042);
		GL11.glEnable(3008);
		GL11.glEnable(3553);
	}
}
