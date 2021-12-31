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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import java.util.TreeMap;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.Tessellator;

public abstract class WindowScreen extends Screen {

	private List<Window> windows = new ArrayList<>();

	// <Layer, Window Index>
	private Map<Integer, Integer> windowOrder = new TreeMap<>();

	public Window addWindow(Window window) {
		windows.add(window);
		windowOrder.put(windows.size() - 1, windows.size() - 1);
		return window;
	}

	public void removeWindow(int index) {
		if (index >= 0 && index < windows.size()) {
			int layer = getWindowLayer(index);

			windows.remove(index);
			windowOrder.remove(layer);
			for (Entry<Integer, Integer> e: new TreeMap<>(windowOrder).entrySet()) {
				if (e.getKey() > layer) {
					windowOrder.remove(e.getKey());
					windowOrder.put(e.getKey() - 1, e.getValue());
				}
			}
		}
	}

	public Window getWindow(int i) {
		return windows.get(i);
	}

	public void clearWindows() {
		windows.clear();
		windowOrder.clear();
	}

	public List<Window> getWindows() {
		return windows;
	}

	protected List<Integer> getWindowsBackToFront() {
		return new ArrayList<>(windowOrder.values());
	}

	protected List<Integer> getWindowsFrontToBack() {
		List<Integer> w = getWindowsBackToFront();
		Collections.reverse(w);
		return w;
	}
	
	protected int getWindowLayer(int index) {
		return windowOrder.entrySet().stream().filter(i -> i.getValue() == index).findFirst().get().getKey();
	}

	protected int getSelectedWindow() {
		for (int i = 0; i < windows.size(); i++) {
			if (!getWindow(i).closed && getWindow(i).selected) {
				return i;
			}
		}

		return -1;
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		int sel = getSelectedWindow();

		if (sel == -1) {
			for (int i: getWindowsFrontToBack()) {
				if (!getWindow(i).closed) {
					selectWindow(i);
					break;
				}
			}
		}

		boolean close = true;

		for (int w: getWindowsBackToFront()) {
			if (!getWindow(w).closed) {
				close = false;
				onRenderWindow(w, mouseX, mouseY);
			}
		}

		if (close) client.closeScreen();

		super.render(mouseX, mouseY, delta);
	}

	public void onRenderWindow(int window, int mouseX, int mouseY) {
		if (!windows.get(window).closed) {
			windows.get(window).render(mouseX, mouseY);
		}
	}

	public void selectWindow(int window) {
		for (int i = 0; i < windows.size(); i++) {
			Window w = windows.get(i);

			if (i == window) {
				w.closed = false;
				w.selected = true;
				int layer = getWindowLayer(window);

				windowOrder.remove(layer);
				for (Entry<Integer, Integer> e: new TreeMap<>(windowOrder).entrySet()) {
					if (e.getKey() > layer) {
						windowOrder.remove(e.getKey());
						windowOrder.put(e.getKey() - 1, e.getValue());
					}
				}

				windowOrder.put(windowOrder.size(), window);
			} else {
				w.selected = false;
			}
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		/* Handle what window will be selected when clicking */
		for (int wi: getWindowsFrontToBack()) {
			Window w = getWindow(wi);

			if (mouseX >= w.x1 && mouseX <= w.x2 && mouseY >= w.y1 && mouseY <= w.y2 && !w.closed) {
				if (w.shouldClose((int) mouseX, (int) mouseY)) {
					w.closed = true;
					break;
				}
				
				if (!w.selected)
					selectWindow(wi);

				w.mouseClicked(mouseX, mouseY, button);
				break;
			}
		}

		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int button) {
		for (Window w : windows) {
			w.mouseReleased(mouseX, mouseY, button);
		}

		super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public void tick() {
		for (Window w : windows) {
			w.tick();
		}

		super.tick();
	}

	@Override
	public void keyPressed(char keyCode, int scanCode) {
		for (Window w : windows) {
			w.keyPressed(keyCode, scanCode, 0);
		}

		super.keyPressed(keyCode, scanCode);
	}

	public void renderBackgroundTexture(int vOffset) {
		int colorOffset = (int) ((System.currentTimeMillis() / 75) % 100);
		if (colorOffset > 50)
			colorOffset = 50 - (colorOffset - 50);

		// smooth
		colorOffset = (int) (-(Math.cos(Math.PI * (colorOffset / 50d)) - 1) / 2 * 50);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(3008);
		GL11.glBlendFunc(770, 771);
		GL11.glShadeModel(7425);
		Tessellator tessellator = Tessellator.INSTANCE;
		tessellator.method_1405();
		tessellator.method_1398(width, 0, 0);
		tessellator.method_1401(30, 20, 80, 255);
		tessellator.method_1398(0, 0, 0);
		tessellator.method_1401(30 + colorOffset / 3, 20, 80, 255);
		tessellator.method_1398(0, height + 14, 0);
		tessellator.method_1401(90, 54, 159, 255);
		tessellator.method_1398(width, height + 14, 0);
		tessellator.method_1401(105 + colorOffset, 54, 189, 255);
		tessellator.method_1396();

		GL11.glShadeModel(7424);
		GL11.glDisable(3042);
		GL11.glEnable(3008);
		GL11.glEnable(3553);
	}
}
