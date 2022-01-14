/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ExperienceBottleItem;
import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.BlockPos;
import org.bleachhack.util.InventoryUtils;

import static org.bleachhack.util.world.WorldUtils.facePosPacket;

public class XPBomb extends Module {
	int ticksPassed;
	boolean enabled = false;
	int lastSlot;
	int slot;

	public XPBomb() {
		super("XPBomb", KEY_UNBOUND, ModuleCategory.COMBAT, "auto throws xp.");
	}

	@Override
	public void onEnable(boolean inWorld) {
		super.onEnable(inWorld);
		ticksPassed = 0;
		enabled = true;
		slot = InventoryUtils.getSlot(i -> mc.field_3805.inventory.getInvStack(i) != null
				&& mc.field_3805.inventory.getInvStack(i).getItem() instanceof ExperienceBottleItem);
		if (slot == -1) {
			BleachLogger.info("No xp bottles in hotbar");
			this.setEnabled(false);
			return;
		}
	}


	@BleachSubscribe
	public void onTick(EventTick event) {
		ticksPassed++;
		if (!enabled) return;
		if (ticksPassed == 1) {
			lastSlot = mc.field_3805.inventory.selectedSlot;
			InventoryUtils.selectSlot(slot);
			for (int i = 0; i < 500; i++) {
				mc.interactionManager.method_1228(mc.field_3805, mc.world, mc.field_3805.getMainHandStack());
			}
		}
		if (ticksPassed == 2) {
			InventoryUtils.selectSlot(lastSlot);
			this.setEnabled(false);
			enabled = false;
		}
	}


}
