/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.BleachQueue;

import net.minecraft.class_711;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;

public class AutoArmor extends Module {

	private int tickDelay = 0;

	public AutoArmor() {
		super("AutoArmor", KEY_UNBOUND, ModuleCategory.PLAYER, "Automatically equips armor.",
				new SettingToggle("AntiBreak", false).withDesc("Unequips your armor when its about to break."),
				new SettingToggle("Delay", true).withDesc("Adds a delay between equipping armor pieces.").withChildren(
						new SettingSlider("Delay", 0, 20, 1, 0).withDesc("How many ticks between putting on armor pieces.")));
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		if (mc.field_3805.playerScreenHandler != mc.field_3805.openScreenHandler || !BleachQueue.isEmpty("autoarmor_equip"))
			return;

		if (tickDelay > 0) {
			tickDelay--;
			return;
		}

		tickDelay = (getSetting(1).asToggle().getState() ? getSetting(1).asToggle().getChild(0).asSlider().getValueInt() : 0);

		/* [Slot type, [Armor slot, Armor prot, New armor slot, New armor prot]] */
		Map<Integer, int[]> armorMap = new HashMap<>(4);
		armorMap.put(3, new int[] { 36, getProtection(mc.field_3805.inventory.getInvStack(36)), -1, -1 });
		armorMap.put(2, new int[] { 37, getProtection(mc.field_3805.inventory.getInvStack(37)), -1, -1 });
		armorMap.put(1, new int[] { 38, getProtection(mc.field_3805.inventory.getInvStack(38)), -1, -1 });
		armorMap.put(0, new int[] { 39, getProtection(mc.field_3805.inventory.getInvStack(39)), -1, -1 });

		/* Anti Break */
		if (getSetting(0).asToggle().getState()) {
			for (Entry<Integer, int[]> e: armorMap.entrySet()) {
				ItemStack is = mc.field_3805.inventory.getInvStack(e.getValue()[0]);
				int armorSlot = (e.getValue()[0] - 34) + (39 - e.getValue()[0]) * 2;

				if (is != null && is.isDamageable() && is.getMaxDamage() - is.getDamage() < 7) {
					/* Look for an empty slot to quick move to */
					int forceMoveSlot = -1;
					for (int s = 0; s < 36; s++) {
						if (mc.field_3805.inventory.getInvStack(s) == null) {
							mc.interactionManager.clickSlot(mc.field_3805.openScreenHandler.syncId, armorSlot, 0, 1, mc.field_3805);
							return;
						} else if (forceMoveSlot == -1) {
							ItemStack is1 = mc.field_3805.inventory.getInvStack(s);
							if (is1 != null && (is1.getItem() instanceof ToolItem || is1.getItem() instanceof ArmorItem))
								forceMoveSlot = s;
						}
					}

					/* Bruh no empty spots, then force move to a non-totem/tool/armor item */
					if (forceMoveSlot != -1) {
						//System.out.println(forceMoveSlot);
						mc.interactionManager.clickSlot(mc.field_3805.openScreenHandler.syncId,
								forceMoveSlot < 9 ? 36 + forceMoveSlot : forceMoveSlot, 0, 4, mc.field_3805);
						mc.interactionManager.clickSlot(mc.field_3805.openScreenHandler.syncId, armorSlot, 0, 1, mc.field_3805);
						return;
					}

					/* No spots to move to, yeet the armor to not cause any bruh moments */
					mc.interactionManager.clickSlot(mc.field_3805.openScreenHandler.syncId, armorSlot, 0, 4, mc.field_3805);
					return;
				}
			}
		}

		for (int s = 0; s < 36; s++) {
			int prot = getProtection(mc.field_3805.inventory.getInvStack(s));

			if (prot > 0) {
				int slot = ((ArmorItem) mc.field_3805.inventory.getInvStack(s).getItem()).slot;

				for (Entry<Integer, int[]> e: armorMap.entrySet()) {
					if (e.getKey() == slot) {
						if (prot > e.getValue()[1] && prot > e.getValue()[3]) {
							e.getValue()[2] = s;
							e.getValue()[3] = prot;
						}
					}
				}
			}
		}

		for (Entry<Integer, int[]> e: armorMap.entrySet()) {
			if (e.getValue()[2] != -1) {
				if (e.getValue()[1] == -1 && e.getValue()[2] < 9) {
					if (e.getValue()[2] != mc.field_3805.inventory.selectedSlot) {
						mc.field_3805.inventory.selectedSlot = e.getValue()[2];
						mc.field_3805.field_1667.sendPacket(new class_711(e.getValue()[2]));
					}

					mc.interactionManager.clickSlot(mc.field_3805.openScreenHandler.syncId, 36 + e.getValue()[2], 0, 1, mc.field_3805);
				} else if (mc.field_3805.playerScreenHandler == mc.field_3805.openScreenHandler) {
					/* Convert inventory slots to container slots */
					int armorSlot = (e.getValue()[0] - 34) + (39 - e.getValue()[0]) * 2;
					int newArmorslot = e.getValue()[2] < 9 ? 36 + e.getValue()[2] : e.getValue()[2];

					mc.interactionManager.clickSlot(mc.field_3805.openScreenHandler.syncId, newArmorslot, 0, 0, mc.field_3805);
					mc.interactionManager.clickSlot(mc.field_3805.openScreenHandler.syncId, armorSlot, 0, 0, mc.field_3805);
					if (e.getValue()[1] != -1)
						mc.interactionManager.clickSlot(mc.field_3805.openScreenHandler.syncId, newArmorslot, 0, 0, mc.field_3805);
				}

				return;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private int getProtection(ItemStack is) {
		if (is == null)
			return -1;

		if (is.getItem() instanceof ArmorItem) {
			int prot = 0;

			if (is.getMaxDamage() - is.getDamage() < 7 && getSetting(0).asToggle().getState()) {
				return 0;
			}

			if (is.hasEnchantments()) {
				for (Entry<Enchantment, Integer> e: (Set<Entry<Enchantment, Integer>>) EnchantmentHelper.get(is).entrySet()) {
					if (e.getKey() instanceof ProtectionEnchantment)
						prot += e.getValue();
				}
			}

			return ((ArmorItem) is.getItem()).protection + prot;
		} else if (!is.isEmpty()) {
			return 0;
		}

		return -1;
	}
}
