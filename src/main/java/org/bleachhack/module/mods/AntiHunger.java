/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import org.bleachhack.event.events.EventPacket;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingToggle;

import net.minecraft.class_695;

public class AntiHunger extends Module {

	private boolean bool = false;

	public AntiHunger() {
		super("AntiHunger", KEY_UNBOUND, ModuleCategory.PLAYER, "Minimizes the amount of hunger you use (Also makes you slide).",
				new SettingToggle("Relaxed", false).withDesc("Only activates every other ticks, might fix getting fly kicked."));
	}

	@BleachSubscribe
	public void onSendPacket(EventPacket.Send event) {
		if (event.getPacket() instanceof class_695) {
			if (mc.field_3805.velocityY != 0 && !mc.options.keyJump.pressed && (!bool || !getSetting(0).asToggle().getState())) {
				// if (((PlayerMoveC2SPacket) event.getPacket()).isOnGround())
				// event.setCancelled(true);
				boolean onGround = mc.field_3805.fallDistance >= 0.1f;
				mc.field_3805.onGround = onGround;
				((class_695) event.getPacket()).onGround = onGround;
				bool = true;
			} else {
				bool = false;
			}
		}
	}

}
