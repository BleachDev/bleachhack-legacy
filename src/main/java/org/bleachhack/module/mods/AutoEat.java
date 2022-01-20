/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.InventoryUtils;

import net.minecraft.item.FoodItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AutoEat extends Module {

	private boolean eating;

	public AutoEat() {
		super("AutoEat", KEY_UNBOUND, ModuleCategory.PLAYER, "Automatically eats food for you.",
				new SettingToggle("Hunger", true).withDesc("Eats when you're bewlow a certain amount of hunger.").withChildren(
						new SettingSlider("Hunger", 0, 19, 14, 0).withDesc("The maximum hunger to eat at.")),
				new SettingToggle("Health", false).withDesc("Eats when you're bewlow a certain amount of health.").withChildren(
						new SettingSlider("Health", 0, 19, 14, 0).withDesc("The maximum health to eat at.")),
				new SettingToggle("Gapples", true).withDesc("Eats golden apples.").withChildren(
						new SettingToggle("Prefer", false).withDesc("Prefers golden apples avobe regular food.")),
				new SettingToggle("Poisonous", false).withDesc("Eats poisonous food."));
	}

	@Override
	public void onDisable(boolean inWorld) {
		mc.options.keyUse.pressed = false;

		super.onDisable(inWorld);
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		if (eating && mc.options.keyUse.pressed && !mc.field_3805.isUsingItem()) {
			eating = false;
			mc.options.keyUse.pressed = false;
		}

		if (getSetting(0).asToggle().getState() && mc.field_3805.getHungerManager().getFoodLevel() <= getSetting(0).asToggle().getChild(0).asSlider().getValueInt()) {
			startEating();
		} else if (getSetting(1).asToggle().getState() && (int) mc.field_3805.getHealth() + (int) mc.field_3805.getAbsorption() <= getSetting(1).asToggle().getChild(0).asSlider().getValueInt()) {
			startEating();
		}
	}

	private void startEating() {
		boolean gapples = getSetting(2).asToggle().getState();
		boolean preferGapples = getSetting(2).asToggle().getChild(0).asToggle().getState();
		boolean poison = getSetting(3).asToggle().getState();

		int slot = -1;
		int hunger = -1;
		for (int s: InventoryUtils.getInventorySlots()) {
			ItemStack item = mc.field_3805.inventory.getInvStack(s);
			Item food = item == null ? null : mc.field_3805.inventory.getInvStack(s).getItem();

			if (!(food instanceof FoodItem))
				continue;

			int h = preferGapples && food == Item.GOLDEN_APPLE
					? Integer.MAX_VALUE : ((FoodItem) food).method_3337();

			if (h <= hunger
					|| (!gapples && food == Item.GOLDEN_APPLE)
					|| (!poison && isPoisonous(food)))
				continue;

			slot = s;
			hunger = h;
		}

		if (hunger != -1) {
			if (slot == mc.field_3805.inventory.selectedSlot || slot == 40) {
				mc.options.keyUse.pressed = true;
				mc.interactionManager.method_1228(mc.field_3805, mc.world, mc.field_3805.getMainHandStack());
				eating = true;
			} else {
				InventoryUtils.selectSlot(slot);
			}
		}
	}

	private boolean isPoisonous(Item food) {
		return food == Item.SPIDEY_EYE || food == Item.POISONOUS_POTATO || food == Item.ROTTEN_FLESH;
	}
}
