/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.gui;

import com.google.gson.JsonPrimitive;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.bleachhack.BleachHack;
import org.bleachhack.gui.effect.ParticleManager;
import org.bleachhack.gui.window.Window;
import org.bleachhack.gui.window.WindowScreen;
import org.bleachhack.gui.window.widget.WindowButtonWidget;
import org.bleachhack.gui.window.widget.WindowTextWidget;
import org.bleachhack.util.io.BleachFileHelper;
import org.bleachhack.util.io.BleachOnlineMang;

import java.util.List;
import java.util.Random;

public class BleachTitleScreen extends WindowScreen {

	private ParticleManager particleMang = new ParticleManager();
	public static boolean customTitleScreen = true;

	private static String splash = "default";
	private static int splashTicks;

	static {
		BleachOnlineMang.getResourceAsync("splashes.txt").thenAccept(st -> {
			if (st != null) {
				List<String> list = st.bodyAsLines();
				splash = list.get(new Random().nextInt(list.size()));
			}
		});
	}

	@Override
	public void init() {
		super.init();

		clearWindows();
		addWindow(new Window(width / 8,
				height / 8,
				width - width / 8,
				height - height / 8, "BleachHack", new ItemStack(Item.RECORD_CAT)));

		int w = getWindow(0).x2 - getWindow(0).x1;
		int h = getWindow(0).y2 - getWindow(0).y1;
		int maxY = MathHelper.clamp(h / 4 + 119, 0, h - 22);

		getWindow(0).addWidget(new WindowButtonWidget(w / 2 - 100, h / 4 + 38, w / 2 + 100, h / 4 + 58, I18n.translate("menu.singleplayer"), () ->
			client.openScreen(new SelectWorldScreen(client.currentScreen))
		));

		getWindow(0).addWidget(new WindowButtonWidget(w / 2 - 100, h / 4 + 62, w / 2 + 100, h / 4 + 82, I18n.translate("menu.multiplayer"), () ->
			client.openScreen(new MultiplayerScreen(client.currentScreen))
		));

		getWindow(0).addWidget(new WindowButtonWidget(w / 2 - 100, h / 4 + 86, w / 2 + 100, h / 4 + 106, I18n.translate("menu.online"), () -> {}
		));

		getWindow(0).addWidget(new WindowButtonWidget(w / 2 - 124, h / 4 + 86, w / 2 - 104, h / 4 + 106, "MC", () -> {
			customTitleScreen = !customTitleScreen;
			BleachFileHelper.saveMiscSetting("customTitleScreen", new JsonPrimitive(false));
			client.openScreen(new TitleScreen());
		}));

		getWindow(0).addWidget(new WindowButtonWidget(w / 2 - 100, maxY, w / 2 - 2, maxY + 20, I18n.translate("menu.options"), () ->
			client.openScreen(new SettingsScreen(client.currentScreen, client.options))
		));

		getWindow(0).addWidget(new WindowButtonWidget(w / 2 + 2, maxY, w / 2 + 100, maxY + 20, I18n.translate("menu.quit"), () ->
			client.scheduleStop()
		));

		// Main Text
		getWindow(0).addWidget(new WindowTextWidget("", true, WindowTextWidget.TextAlign.MIDDLE, 3f, w / 2, h / 4 - 25, 0)
				.withRenderEvent((widget, wx, wy) -> {
					String bhText = BleachHack.watermark.getText();
					((WindowTextWidget) widget).setText(bhText);
				}));

		// Version Text
		getWindow(0).addWidget(new WindowTextWidget(BleachHack.VERSION, true, WindowTextWidget.TextAlign.MIDDLE, 1.5f, w / 2, h / 4 - 6, 0xffc050));

		// Splash
		getWindow(0).addWidget(new WindowTextWidget("", true, WindowTextWidget.TextAlign.MIDDLE, 1f, w / 2 + 80, h / 4 - 3, 0xffff00)
				.withRenderEvent((widget, wx, wy) -> {
					if (splash != null) {
						WindowTextWidget windgetText = (WindowTextWidget) widget;
						windgetText.setText(splash);
					}
				}));
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		this.renderBackground();

		int copyWidth = this.textRenderer.getStringWidth("Copyright Mojang AB. Do not distribute!") + 2;
		textRenderer.draw("Copyright Mojang AB. Do not distribute!", width - copyWidth, height - 10, -1, true);
		textRenderer.draw("Fabric: " + FabricLoader.getInstance().getModContainer("fabricloader").get().getMetadata().getVersion().getFriendlyString(),
				4, height - 30, -1, true);
		textRenderer.draw("Minecraft: " + BleachHack.MCVERSION, 4, height - 20, -1, true);
		textRenderer.draw("Logged in as: \u00a7a" + client.getSession().getUsername(), 4, height - 10, -1, true);

		super.render(mouseX, mouseY, delta);

		particleMang.addParticle(mouseX, mouseY);
		particleMang.renderParticles();

	}

	@Override
	public void tick() {
		if (splash != null && splashTicks < 15)
			splashTicks++;
	}
}
