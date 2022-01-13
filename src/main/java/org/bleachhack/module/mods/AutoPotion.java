/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.BlockPos;
import org.bleachhack.util.InventoryUtils;
import org.bleachhack.util.world.WorldUtils;

import static org.bleachhack.util.world.WorldUtils.rightClick;

public class AutoPotion extends Module {
	int ticksPassed;
	boolean enabled = false;
	BlockPos pos;
	int lastSlot;

	public AutoPotion() {
		super("AutoPotion", KEY_UNBOUND, ModuleCategory.MISC, "auto throws potions.");
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
			pos = new BlockPos(mc.field_3805);
			lastSlot = mc.field_3805.inventory.selectedSlot;
			InventoryUtils.selectSlot(4);
		}
		if (ticksPassed == 2) {
			rightClick(pos, Vec3d.method_604(pos.getX() + 0.5, pos.getY() - 1, pos.getZ() + 0.5), Direction.UP);
		}
		if (ticksPassed == 3) {
			InventoryUtils.selectSlot(lastSlot);
			BleachLogger.info("Splash potions thrown.");
			this.setEnabled(false);
			enabled = false;
		}
	}

}
