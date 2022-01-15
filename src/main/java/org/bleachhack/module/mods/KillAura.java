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
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import org.bleachhack.BleachHack;
import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.BleachQueue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class KillAura extends Module {

	private int tickDelay = 0;

	public KillAura() {
		super("KillAura", KEY_UNBOUND, ModuleCategory.COMBAT, "swing at other players.");
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		for (Entity e : (List<Entity>) mc.world.getLoadedEntities()) {
			if (e instanceof PlayerEntity && e != mc.field_3805 && mc.field_3805.distanceTo(e) <= 5.2 && !BleachHack.friendMang.has(e)) {
				mc.interactionManager.attackEntity(mc.field_3805, e);
				mc.field_3805.swingHand();
			}
		}
	}
}
