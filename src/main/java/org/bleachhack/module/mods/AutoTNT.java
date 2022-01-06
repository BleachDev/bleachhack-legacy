/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import net.minecraft.block.Block;
import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.util.BlockPos;
import org.bleachhack.util.InventoryUtils;
import org.bleachhack.util.world.WorldUtils;

import java.util.*;

public class AutoTNT extends Module {

	private List<Long> blacklist = new ArrayList<>();

	public AutoTNT() {
		super("AutoTNT", KEY_UNBOUND, ModuleCategory.MISC, "Automatically does the brhu.",
				new SettingSlider("Distance", 1, 5, 3, 0).withDesc("How far away from eachother the tnt should be."));
	}

	@Override
	public void onDisable(boolean inWorld) {
		blacklist.clear();

		super.onDisable(inWorld);
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		int tntSlot = InventoryUtils.getSlot(i -> mc.field_3805.inventory.getInvStack(i) != null
				&& mc.field_3805.inventory.getInvStack(i).field_4378 == Block.field_399.field_466);
		if (tntSlot == -1)
			return;

		int dist = getSetting(0).asSlider().getValueInt();
		for (int i = -3; i < 4; i++)  {
			for (int j = -3; j < 4; j++)  {
				int x = (int) mc.field_3805.x - (int) mc.field_3805.x % dist - i * dist;
				int z = (int) mc.field_3805.z - (int) mc.field_3805.z % dist - j * dist;
				
				boolean skip = false;
				for (Long l: blacklist) {
					if (x == (int) (l >> 32) && z == (int) (l & 0xffffffff)) {
						skip = true;
						break;
					}
				}
				
				if (skip)
					continue;

				for (int k = -3; k < 4; k++) {
					int y = (int) mc.field_3805.y + k;
					if (mc.field_3805.squaredDistanceTo(x + 0.5, y + 0.5, z + 0.5) < 4.25
							&& WorldUtils.placeBlock(new BlockPos(x, y, z), tntSlot, 0, false, false, true)) {
						blacklist.add(((long) x << 32) | (long) z);
						return;
					}
				}
			}
		}
	}

}
