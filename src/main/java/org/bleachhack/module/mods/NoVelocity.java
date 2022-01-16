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
import org.bleachhack.mixin.AccessorClass_667;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.setting.module.SettingToggle;

import net.minecraft.class_667;
import net.minecraft.class_714;

public class NoVelocity extends Module {

	public NoVelocity() {
		super("NoVelocity", KEY_UNBOUND, ModuleCategory.PLAYER, "If you take some damage, you don't move.",
				new SettingToggle("Knockback", true).withDesc("Reduces knockback from other entities.").withChildren(
						new SettingSlider("VelXZ", 0, 100, 0, 1).withDesc("How much horizontal velocity to keep."),
						new SettingSlider("VelY", 0, 100, 0, 1).withDesc("How much vertical velocity  to keep.")),
				new SettingToggle("Explosions", true).withDesc("Reduces explosion velocity.").withChildren(
						new SettingSlider("VelXZ", 0, 100, 0, 1).withDesc("How much horizontal velocity to keep."),
						new SettingSlider("VelY", 0, 100, 0, 1).withDesc("How much vertical velocity to keep.")),
				new SettingToggle("Fluids", true).withDesc("Reduces how much you get pushed from fluids."));
	}

	@BleachSubscribe
	public void readPacket(EventPacket.Read event) {
		if (mc.field_3805 == null)
			return;

		if (event.getPacket() instanceof class_714 && getSetting(0).asToggle().getState()) {
			class_714 packet = (class_714) event.getPacket();
			if (packet.id == mc.field_3805.field_3243) {
				double velXZ = getSetting(0).asToggle().getChild(0).asSlider().getValue() / 100;
				double velY = getSetting(0).asToggle().getChild(1).asSlider().getValue() / 100;
				
				double pvelX = (packet.velocityX / 8000d - mc.field_3805.velocityX) * velXZ;
				double pvelY = (packet.velocityY / 8000d - mc.field_3805.velocityY) * velY;
				double pvelZ = (packet.velocityZ / 8000d - mc.field_3805.velocityZ) * velXZ;

				packet.velocityX = (int) (pvelX * 8000 + mc.field_3805.velocityX * 8000);
				packet.velocityY = (int) (pvelY * 8000 + mc.field_3805.velocityY * 8000);
				packet.velocityZ = (int) (pvelZ * 8000 + mc.field_3805.velocityZ * 8000);
			}
		} else if (event.getPacket() instanceof class_667 && getSetting(1).asToggle().getState()) {
			class_667 packet = (class_667) event.getPacket();
			AccessorClass_667 apacket = (AccessorClass_667) packet;

			double velXZ = getSetting(1).asToggle().getChild(0).asSlider().getValue() / 100;
			double velY = getSetting(1).asToggle().getChild(1).asSlider().getValue() / 100;
			
			apacket.setPlayerVelocityX((float) (packet.getPlayerVelocityX() * velXZ));
			apacket.setPlayerVelocityY((float) (packet.getPlayerVelocityY() * velY));
			apacket.setPlayerVelocityZ((float) (packet.getPlayerVelocityZ() * velXZ));
		}
	}

	// Fluid handling in MixinAbstractFluid
}
