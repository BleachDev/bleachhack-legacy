/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import net.minecraft.class_689;
import net.minecraft.class_714;
import org.bleachhack.event.events.EventPacket;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.shader.ShaderEffectWrapper;

public class WitherSounds extends Module {

	private ShaderEffectWrapper shader;

	public WitherSounds() {
		super("WitherSounds", KEY_UNBOUND, ModuleCategory.MISC, "wither locations");
	}

	@SuppressWarnings("unchecked")
	@BleachSubscribe
	public void onWorldRender(EventPacket event) {
		if(event.getPacket() instanceof class_689) {
			if (mc.field_3805 == null)
				return;
			class_689 packet = (class_689) event.getPacket();
			String sound = packet.method_1819();
			if(!sound.equals("ambient.weather.thunder")) {return;}
			double x = packet.method_1820();
			double y = packet.method_1821();
			double z = packet.method_1822();
			BleachLogger.info(sound+" "+x+", "+", "+y+", "+z);
		}
	}
}