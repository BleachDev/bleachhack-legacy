/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.bleachhack.event.events.EventClientMove;
import org.bleachhack.event.events.EventPacket;
import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingMode;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.world.WorldUtils;

import net.minecraft.util.math.Vec3d;

public class AntiVoid extends Module {

	public AntiVoid() {
		super("AntiVoid", KEY_UNBOUND, ModuleCategory.MOVEMENT, "Prevents you from falling in the void.",
				new SettingMode("Mode", "Jump", "Floor", "Vanilla").withDesc("What mode to use when you're in the void."),
				new SettingToggle("AntiTP", true).withDesc("Prevents you from accidentally tping in to the void (i.e., using PacketFly)."));
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		if (mc.field_3805.y < 0) {
			switch (getSetting(0).asMode().getMode()) {
				case 0:
					mc.field_3805.velocityY = 0.42;
					break;
				case 1:
					mc.field_3805.onGround = true;
					break;
				case 2:
					for (int i = 3; i < 256; i++) {
						if (!WorldUtils.doesBoxCollide(mc.field_3805.boundingBox.offset(0, -mc.field_3805.y + i, 0))) {
							mc.field_3805.updatePosition(mc.field_3805.x, i, mc.field_3805.z);
							break;
						}
					}

					break;
			}
		}
	}

	@BleachSubscribe
	public void onSendPacket(EventPacket.Send event) {
		if (event.getPacket() instanceof PlayerMoveC2SPacket) {
			PlayerMoveC2SPacket packet = (PlayerMoveC2SPacket) event.getPacket();

			if (getSetting(1).asToggle().getState()
					&& mc.field_3805.y >= 0 && packet.y < 0) {
				event.setCancelled(true);
				return;
			}

			if (getSetting(0).asMode().getMode() == 1 && mc.field_3805.y < 0 && packet.y < mc.field_3805.y) {
				packet.y = mc.field_3805.y;
			}
		}
	}

	@BleachSubscribe
	public void onClientMove(EventClientMove event) {
		if (getSetting(1).asToggle().getState() && mc.field_3805.y >= 0 && mc.field_3805.y - event.getVec().y < 0) {
			event.setCancelled(true);
			return;
		}

		if (getSetting(0).asMode().getMode() == 1 && mc.field_3805.y < 0 && event.getVec().y < 0) {
			event.setVec(Vec3d.method_604(event.getVec().x, 0, event.getVec().z));
			mc.field_3805.addVelocity(0, -mc.field_3805.velocityY, 0);
		}
	}

}
