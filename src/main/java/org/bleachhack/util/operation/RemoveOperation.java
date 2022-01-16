/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.util.operation;

import org.bleachhack.util.render.Renderer;
import org.bleachhack.util.render.color.QuadColor;

import org.bleachhack.util.BlockPos;
import net.minecraft.util.math.Direction;

public class RemoveOperation extends Operation {

	public RemoveOperation(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public boolean canExecute() {
		if (mc.field_3805.distanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 4.55) {
			for (Direction d: Direction.values()) {
				BlockPos opos = pos.offset(d);
				if (mc.world.isAir(opos.getX(), opos.getY(), opos.getZ())) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean execute() {
		for (Direction d: Direction.values()) {
			BlockPos opos = pos.offset(d);
			if (mc.world.isAir(opos.getX(), opos.getY(), opos.getZ())) {
				mc.interactionManager.method_1235(pos.getX(), pos.getY(), pos.getZ(), 0);
				mc.field_3805.swingHand();

				return mc.world.isAir(pos.getX(), pos.getY(), pos.getZ());
			}
		}

		return false;
	}

	@Override
	public boolean verify() {
		return mc.world.isAir(pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
	public void render() {
		Renderer.drawBoxBoth(pos, QuadColor.single(1f, 0f, 0f, 0.3f), 2.5f);
	}

}
