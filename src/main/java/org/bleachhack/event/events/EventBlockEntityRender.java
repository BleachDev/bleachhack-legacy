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

import net.minecraft.block.entity.BlockEntity;

public class EventBlockEntityRender extends Event {

	public static class Single extends EventBlockEntityRender {

		protected BlockEntity blockEntity;

		public BlockEntity getBlockEntity() {
			return blockEntity;
		}

		public static class Pre extends Single {

			public Pre(BlockEntity blockEntity) {
				this.blockEntity = blockEntity;
			}

			public void setBlockEntity(BlockEntity blockEntity) {
				this.blockEntity = blockEntity;
			}
		}

		public static class Post extends Single {
			public Post(BlockEntity blockEntity) {
				this.blockEntity = blockEntity;
			}
		}
	}

	public static class PreAll extends EventEntityRender {
	}

	public static class PostAll extends EventEntityRender {
	}
}
