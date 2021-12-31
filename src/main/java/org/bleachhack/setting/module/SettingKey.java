/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.setting.module;

import org.bleachhack.gui.clickgui.window.ModuleWindow;
import org.bleachhack.module.Module;
import org.bleachhack.setting.SettingDataHandlers;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;

public class SettingKey extends ModuleSetting<Integer> {

	public SettingKey(int key) {
		super("Bind", key, SettingDataHandlers.INTEGER);
	}

	@Override
	public void render(ModuleWindow window, int x, int y, int len) {
		if (window.mouseOver(x, y, x + len, y + 12)) {
			DrawableHelper.fill(x + 1, y, x + len, y + 12, 0x70303070);
		}
		
		if (window.keyDown >= 0 && window.keyDown != Keyboard.KEY_ESCAPE && window.mouseOver(x, y, x + len, y + 12)) {
			setValue(window.keyDown == Keyboard.KEY_DELETE ? Module.KEY_UNBOUND : window.keyDown);
		}

		int key = getValue();
		String name = key < 0 ? "NONE" : Keyboard.getKeyName(key);
		if (name == null)
			name = "KEY" + key;
		else if (name.isEmpty())
			name = "NONE";

		MinecraftClient.getInstance().textRenderer.method_956(
				"Bind: " + name + (window.mouseOver(x, y, x + len, y + 12) ? "..." : ""), x + 3, y + 2, 0xcfe0cf);
	}

	public SettingKey withDesc(String desc) {
		setTooltip(desc);
		return this;
	}

	@Override
	public int getHeight(int len) {
		return 12;
	}
}
