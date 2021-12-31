/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.gui.clickgui;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.bleachhack.BleachHack;
import org.bleachhack.command.Command;
import org.bleachhack.gui.clickgui.window.ClickGuiWindow;
import org.bleachhack.gui.clickgui.window.ModuleWindow;
import org.bleachhack.gui.window.Window;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.module.ModuleManager;
import org.bleachhack.util.io.BleachFileHelper;

import net.minecraft.client.gui.widget.TextFieldWidget;

public class ModuleClickGuiScreen extends ClickGuiScreen {
	
	public static ModuleClickGuiScreen INSTANCE = new ModuleClickGuiScreen();

	private TextFieldWidget searchField;

	public void init() {
		super.init();

		searchField = new TextFieldWidget(textRenderer, 2, 14, 100, 12 /* @LasnikProgram is author lol */);
		searchField.setVisible(false);;
		searchField.setMaxLength(20);
	}

	public void initWindows() {
		int len = ModuleManager.getModule("ClickGui").getSetting(0).asSlider().getValueInt();
		
		int y = 50;
		for (ModuleCategory c: ModuleCategory.values()) {
			addWindow(new ModuleWindow(ModuleManager.getModulesInCat(c), 30, y, len, StringUtils.capitalize(c.name().toLowerCase()), c.getItem()));
			y += 16;
		}

		for (Window w: getWindows()) {
			if (w instanceof ClickGuiWindow) {
				((ClickGuiWindow) w).hiding = true;
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		BleachFileHelper.SCHEDULE_SAVE_CLICKGUI.set(true);

		searchField.setVisible(ModuleManager.getModule("ClickGui").getSetting(1).asToggle().getState());

		if (ModuleManager.getModule("ClickGui").getSetting(1).asToggle().getState()) {
			Set<Module> seachMods = new HashSet<>();
			if (!searchField.getText().isEmpty()) {
				for (Module m : ModuleManager.getModules()) {
					if (m.getName().toLowerCase(Locale.ENGLISH).contains(searchField.getText().toLowerCase(Locale.ENGLISH).replace(" ", ""))) {
						seachMods.add(m);
					}
				}
			}

			for (Window w : getWindows()) {
				if (w instanceof ModuleWindow) {
					((ModuleWindow) w).setSearchedModule(seachMods);
				}
			}
		}

		int len = ModuleManager.getModule("ClickGui").getSetting(0).asSlider().getValueInt();
		for (Window w : getWindows()) {
			if (w instanceof ModuleWindow) {
				((ModuleWindow) w).setLen(len);
			}
		}

		super.render(mouseX, mouseY, delta);
		
		searchField.render();

		textRenderer.draw("BleachHack-" + BleachHack.VERSION + "-" + BleachHack.MCVERSION, 3, 3, 0x305090);
		textRenderer.draw("BleachHack-" + BleachHack.VERSION + "-" + BleachHack.MCVERSION, 2, 2, 0x6090d0);

		if (ModuleManager.getModule("ClickGui").getSetting(2).asToggle().getState()) {
			textRenderer.method_956("Current prefix is: \"" + Command.getPrefix() + "\" (" + Command.getPrefix() + "help)", 2, height - 20, 0x99ff99);
			textRenderer.method_956("Use " + Command.getPrefix() + "clickgui to reset the clickgui", 2, height - 10, 0x9999ff);
		}
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		searchField.mouseClicked(mouseX, mouseY, button);
		super.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public void keyPressed(char character, int code) {
		searchField.keyPressed(character, code);
		super.keyPressed(character, code);
	}
}
