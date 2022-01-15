/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import net.minecraft.entity.effect.StatusEffect;
import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.module.ModuleManager;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.BlockPos;
import org.bleachhack.util.InventoryUtils;

import static org.bleachhack.util.world.WorldUtils.facePosPacket;

public class AutoAutoPotion extends Module {
	int ticksPassed;

	public AutoAutoPotion() {
		super("AutoAutoPotion", KEY_UNBOUND, ModuleCategory.COMBAT, "auto auto throws potions.");
	}



	@BleachSubscribe
	public void onTick(EventTick event) {
		ticksPassed++;
		if(mc.field_3805 == null) {return;}
		if(ticksPassed % 20 == 0) {
			if(!mc.field_3805.hasStatusEffect(StatusEffect.STRENGTH)) {
				ModuleManager.getModule("AutoPotion").setEnabled(true);
			} else {
				int duration = mc.field_3805.getEffectInstance(StatusEffect.STRENGTH).getDuration() / 20;
				if(duration <= 3) {
					ModuleManager.getModule("AutoPotion").setEnabled(true);
				}
			}
		}
	}


}
