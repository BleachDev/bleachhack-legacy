/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.util.operation;

import org.bleachhack.util.InventoryUtils;
import org.bleachhack.util.world.WorldUtils;

import org.bleachhack.util.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/**
 * THIS DOES NOT PLACE A BLOCK ON A SPECIFIC SIDE OF A BLOCK!!
 * This faces you in a direction then places a block, useful for directional blocks like pistons
 */
public class PlaceDirOperation extends PlaceOperation {

	private Direction dir;
	private boolean faced;

	public PlaceDirOperation(BlockPos pos, Direction dir, int... items) {
		super(pos, items);
		this.dir = dir;
	}

	@Override
	public boolean execute() {
		if (faced) {
			int slot = InventoryUtils.getSlot(getItemPredicate(items));

			faced = false;
			return slot != -1 && WorldUtils.placeBlock(pos, slot, 0, false, false, true);
		} else {
			Vec3d lookPos = Vec3d.method_604(mc.field_3805.x, mc.field_3805.y + mc.field_3805.getEyeHeight(), mc.field_3805.z).method_613(dir.getOffsetX(), dir.getOffsetY(), dir.getOffsetZ());
			WorldUtils.facePosPacket(lookPos.x, lookPos.y, lookPos.z);

			faced = true;
			return false;
		}
	}
}
