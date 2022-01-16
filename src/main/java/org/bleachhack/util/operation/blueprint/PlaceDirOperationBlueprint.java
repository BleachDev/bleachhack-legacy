/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.util.operation.blueprint;

import org.bleachhack.util.operation.Operation;
import org.bleachhack.util.operation.PlaceDirOperation;
import org.bleachhack.util.world.WorldUtils;

import org.bleachhack.util.BlockPos;
import net.minecraft.util.math.Direction;

public class PlaceDirOperationBlueprint extends PlaceOperationBlueprint {

	private Direction rotDir;

	public PlaceDirOperationBlueprint(int localX, int localY, int localZ, Direction localDir, int... items) {
		super(localX, localY, localZ, items);
		this.rotDir = localDir;
	}

	@Override
	public Operation create(BlockPos pos, Direction dir) {
		return new PlaceDirOperation(pos.add(
				(dir == Direction.EAST ? localX : dir == Direction.WEST ? -localX : dir == Direction.SOUTH ? -localZ : localZ),
				localY,
				(dir == Direction.EAST ? localZ : dir == Direction.WEST ? -localZ : dir == Direction.SOUTH ? localX : -localX)),
				(rotDir.getOffsetY() != 0 ? rotDir : rotDir == Direction.EAST ? dir : rotDir == Direction.SOUTH ? WorldUtils.rotateY(dir, false)
						: rotDir == Direction.WEST ? WorldUtils.opposite(dir) : WorldUtils.rotateY(dir, true)),
				items);
	}
}