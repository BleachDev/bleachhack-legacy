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

import net.minecraft.entity.Entity;

public class EventEntityRender extends Event {

	public static class Single extends EventEntityRender {

		protected Entity entity;

		public Entity getEntity() {
			return entity;
		}

		public static class Pre extends Single {

			public Pre(Entity entity) {
				this.entity = entity;
			}

			public void setEntity(Entity entity) {
				this.entity = entity;
			}
		}

		public static class Post extends Single {

			public Post(Entity entity) {
				this.entity = entity;
			}
		}

		public static class Label extends Single {

			public Label(Entity entity) {
				this.entity = entity;
			}
		}
	}

	public static class PreAll extends EventEntityRender {
	}

	public static class PostAll extends EventEntityRender {
	}
}
