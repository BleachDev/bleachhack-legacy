/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.mixin;

import org.bleachhack.gui.BleachTitleScreen;
import org.bleachhack.util.io.BleachFileHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.gson.JsonPrimitive;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;

@Mixin(TitleScreen.class)
public class MixinTitleScreen extends Screen {

	@SuppressWarnings("unchecked")
	@Inject(method = "init()V", at = @At("HEAD"))
	private void init(CallbackInfo info) {
		if (BleachTitleScreen.customTitleScreen) {
			client.openScreen(new BleachTitleScreen());
		} else {
			buttons.add(new ButtonWidget(72, width / 2 - 124, height / 4 + 96, 20, 20, "BH"));
		}
	}

	@Override
	public void buttonClicked(ButtonWidget button) {
		if (button.id == 72) {
			BleachTitleScreen.customTitleScreen = !BleachTitleScreen.customTitleScreen;
			BleachFileHelper.saveMiscSetting("customTitleScreen", new JsonPrimitive(true));
			client.openScreen(new TitleScreen());
		}

		super.buttonClicked(button);
	}
}
