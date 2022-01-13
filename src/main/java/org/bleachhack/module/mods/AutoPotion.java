/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import net.minecraft.class_699;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.BlockPos;
import org.bleachhack.util.InventoryUtils;
import org.bleachhack.util.world.WorldUtils;

import static org.bleachhack.util.world.WorldUtils.facePosPacket;
import static org.bleachhack.util.world.WorldUtils.rightClick;

public class AutoPotion extends Module {
	int ticksPassed;
	boolean enabled = false;
	BlockPos pos;
	int lastSlot;
	int speed;
	int strength;

	public AutoPotion() {
		super("AutoPotion", KEY_UNBOUND, ModuleCategory.MISC, "auto throws potions.",
				new SettingSlider("Strength", 1, 9, 4, 0).withDesc("Slot of strength potion."),
				new SettingSlider("Speed", 1, 9, 5, 0).withDesc("Slot of speed potion."));
	}

	@Override
	public void onEnable(boolean inWorld) {
		super.onEnable(inWorld);
		ticksPassed = 0;
		enabled = true;
		speed = getSetting(0).asSlider().getValueInt()-1;
		strength = getSetting(1).asSlider().getValueInt()-1;
	}


	@BleachSubscribe
	public void onTick(EventTick event) {
		ticksPassed++;
		if (!enabled) return;
		pos = new BlockPos(mc.field_3805);
		if (ticksPassed == 1) {
			lastSlot = mc.field_3805.inventory.selectedSlot;
			InventoryUtils.selectSlot(speed);
			facePosPacket(pos.getX(),pos.getY()-2,pos.getZ());
			mc.interactionManager.method_1228(mc.field_3805, mc.world, mc.field_3805.getMainHandStack());
		}
		if (ticksPassed == 2) {
			InventoryUtils.selectSlot(strength);
			facePosPacket(pos.getX(),pos.getY()-2,pos.getZ());
			mc.interactionManager.method_1228(mc.field_3805, mc.world, mc.field_3805.getMainHandStack());
		}
		if (ticksPassed == 3) {
			InventoryUtils.selectSlot(lastSlot);
			BleachLogger.info("Splash potions thrown.");
			this.setEnabled(false);
			enabled = false;
		}
	}


}
