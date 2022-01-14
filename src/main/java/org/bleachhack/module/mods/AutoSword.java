/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import net.minecraft.class_711;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import org.bleachhack.event.events.EventPacket;
import org.bleachhack.event.events.EventSwingHand;
import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.BleachQueue;
import org.bleachhack.util.InventoryUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AutoSword extends Module {

	public AutoSword() {
		super("AutoSword", KEY_UNBOUND, ModuleCategory.PLAYER, "Automatically equips armor.");
	}

	@BleachSubscribe
	public void onTick(EventSwingHand event) {
		if(mc.result != null && mc.result.entity instanceof PlayerEntity) {
			if(mc.field_3805.inventory.getMainHandStack() == null || !(mc.field_3805.inventory.getMainHandStack().getItem() instanceof SwordItem)) {
				int swordSlot = InventoryUtils.getSlot(i -> mc.field_3805.inventory.getInvStack(i) != null
						&& mc.field_3805.inventory.getInvStack(i).getItem() instanceof SwordItem);
				if (swordSlot == -1) {
					return;
				}
				InventoryUtils.selectSlot(swordSlot);
			}
		}
	}

}
