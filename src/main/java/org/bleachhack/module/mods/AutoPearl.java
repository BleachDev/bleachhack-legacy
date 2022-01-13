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
import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.InventoryUtils;

public class AutoPearl extends Module {
	int ticksPassed;
	boolean enabled = false;
	int lastSlot;
	int epearlSlot;

	public AutoPearl() {
		super("AutoPearl", KEY_UNBOUND, ModuleCategory.COMBAT, "automatically switches to throw enderpearl.",
				new SettingToggle("Messages", false).withDesc("Send messages when throwing enderpearl."));
	}


	@Override
	public void onEnable(boolean inWorld) {
		super.onEnable(inWorld);
		ticksPassed = 0;
		enabled = true;
	}


	@BleachSubscribe
	public void onTick(EventTick event) {
		ticksPassed++;
		if (!enabled) return;

		if (ticksPassed == 1) {
			lastSlot = mc.field_3805.inventory.selectedSlot;
			epearlSlot = InventoryUtils.getSlot(i -> mc.field_3805.inventory.getInvStack(i) != null
					&& mc.field_3805.inventory.getInvStack(i).getItem() instanceof EnderPearlItem);
			if (epearlSlot == -1) {
				BleachLogger.info("No enderpearls in hotbar");
				this.setEnabled(false);
				return;
			}
			InventoryUtils.selectSlot(epearlSlot);
		}
		if (ticksPassed == 2) {
			mc.interactionManager.method_1228(mc.field_3805, mc.world, mc.field_3805.getMainHandStack());
		}
		if (ticksPassed == 3) {
			InventoryUtils.selectSlot(lastSlot);
			if(getSetting(0).asToggle().getState()) {
				BleachLogger.info("Enderpearl thrown.");
			}
			this.setEnabled(false);
			enabled = false;
		}
	}

}
