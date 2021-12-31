/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.event.events;

import org.bleachhack.event.Event;
import org.bleachhack.util.BlockPos;

import net.minecraft.block.Block;
import net.minecraft.util.math.Box;

public class EventBlockShape extends Event {

	private Block block;
	private BlockPos pos;
	private Box shape;

	public EventBlockShape(Block block, BlockPos pos, Box shape) {
		this.block = block;
		this.pos = pos;
		this.shape = shape;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public BlockPos getPos() {
		return pos;
	}

	public void setPos(BlockPos pos) {
		this.pos = pos;
	}

	public Box getShape() {
		return shape;
	}

	public void setShape(Box shape) {
		this.shape = shape;
	}
}
