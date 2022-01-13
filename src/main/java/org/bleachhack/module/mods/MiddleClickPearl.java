/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.InventoryUtils;

public class MiddleClickPearl extends Module {

	public MiddleClickPearl() {
		super("AutoPearl", KEY_UNBOUND, ModuleCategory.MISC, "middle click enderpearl.");
	}

	@Override
	public void onEnable(boolean inWorld) {
		super.onEnable(inWorld);

		int epearlSlot = InventoryUtils.getSlot(i -> mc.field_3805.inventory.getInvStack(i) != null
				&& mc.field_3805.inventory.getInvStack(i).getItem().toString().contains("EnderPearl"));
		if (epearlSlot == -1) {
			BleachLogger.info("No enderpearls in hotbar");
			this.setEnabled(false);
			return;
		}
		int lastSlot = mc.field_3805.inventory.selectedSlot;
		InventoryUtils.selectSlot(epearlSlot);
		BleachLogger.info("Throwing enderpearl");
		//RIGHT CLICK THROW EPEARL LINE CODE STUFF GOES HERE!!
		InventoryUtils.selectSlot(lastSlot);
		this.setEnabled(false);
	}


}
