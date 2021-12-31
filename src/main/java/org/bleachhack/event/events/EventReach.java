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

public class EventReach extends Event {

	private float reach;

	public EventReach(float reach) {
		this.reach = reach;
	}

	public float getReach() {
		return reach;
	}

	public void setReach(float reach) {
		this.reach = reach;
	}
}
