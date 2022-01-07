/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import java.util.Map.Entry;

import org.bleachhack.command.Command;
import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.gui.EntityMenuScreen;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.collections.MutablePairList;
import org.bleachhack.util.io.BleachFileHelper;
import org.lwjgl.input.Mouse;

import com.google.gson.JsonElement;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * @author <a href="https://github.com/lasnikprogram">Lasnik</a>
 */
public class EntityMenu extends Module {
	
	// fuck maps
	public MutablePairList<String, String> interactions = new MutablePairList<>();

	private boolean buttonHeld;
	
	public EntityMenu() {
		super("EntityMenu", KEY_UNBOUND, ModuleCategory.MISC, "An interaction screen when looking at an entity and pressing the middle mouse button. Customizable via the " + Command.getPrefix() + "entitymenu command.",
				new SettingToggle("PlayersOnly", false).withDesc("Only opens the menu when clicking on players."));
	
		JsonElement je = BleachFileHelper.readMiscSetting("entityMenu");
		
		if (je != null && je.isJsonObject()) {
			for (Entry<String, JsonElement> entry: je.getAsJsonObject().entrySet()) {
				if (entry.getValue().isJsonPrimitive()) {
					interactions.add(entry.getKey(), entry.getValue().getAsString());
				}
			}
		}
	}
	
	@BleachSubscribe
	public void onTick(EventTick event) {
		if (Mouse.isButtonDown(2) && !buttonHeld) {
			buttonHeld = true;
			
			Entity lookingAt = mc.result == null ? null : mc.result.entity;
			BleachLogger.info(lookingAt + "");
			
			if (lookingAt != null) {
				if (lookingAt instanceof LivingEntity && (lookingAt instanceof PlayerEntity || !getSetting(0).asToggle().getState())) {
					mc.openScreen(new EntityMenuScreen((LivingEntity) lookingAt));
				}
			}
		} else if (!Mouse.isButtonDown(2)) {
			buttonHeld = false;
		}
	}
}
