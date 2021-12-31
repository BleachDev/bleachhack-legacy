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

public class EventInteract extends Event {

	public static class InteractItem extends EventInteract {
	}

	public static class InteractBlock extends EventInteract {

		protected BlockPos pos;

		public BlockPos getPos() {
			return pos;
		}

		public InteractBlock(BlockPos pos) {
			this.pos = pos;
		}
	}

	public static class AttackBlock extends EventInteract {

		protected BlockPos pos;

		public BlockPos getPos() {
			return pos;
		}

		public AttackBlock(BlockPos pos) {
			this.pos = pos;
		}
	}

	public static class BreakBlock extends EventInteract {

		protected BlockPos pos;

		public BlockPos getPos() {
			return pos;
		}

		public BreakBlock(BlockPos pos) {
			this.pos = pos;
		}
	}
}
