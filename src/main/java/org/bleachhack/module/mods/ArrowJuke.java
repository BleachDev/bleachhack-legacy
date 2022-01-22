/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingMode;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.world.WorldUtils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.network.packet.c2s.play.class_696;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class ArrowJuke extends Module {

	public ArrowJuke() {
		super("ArrowJuke", KEY_UNBOUND, ModuleCategory.COMBAT, "Tries to dodge arrows coming at you.",
				new SettingMode("Move", "Client", "Packet").withDesc("How to move to avoid the arrow."),
				new SettingSlider("Speed", 0.01, 2, 1, 2).withDesc("The move speed."),
				new SettingSlider("Lookahead", 1, 500, 250, 0).withDesc("How many steps in the future to look ahead."),
				new SettingToggle("Up", false).withDesc("Allows you to move up when dodging the arrow."));
	}

	@SuppressWarnings("unchecked")
	@BleachSubscribe
	public void onTick(EventTick envent) {
		for (Entity e : (List<Entity>) mc.world.getLoadedEntities()) {
			if (!(e instanceof AbstractArrowEntity) || ((AbstractArrowEntity) e).owner == mc.field_3805)
				continue;

			int mode = getSetting(0).asMode().getMode();
			int steps = getSetting(2).asSlider().getValueInt();

			Box playerBox = mc.field_3805.boundingBox.expand(0.3, 0.3, 0.3);
			List<Box> futureBoxes = new ArrayList<>(steps);

			Box currentBox = e.boundingBox;
			Vec3d currentVel = Vec3d.method_604(e.velocityX, e.velocityY, e.velocityZ);

			for (int i = 0; i < steps; i++) {
				currentBox = currentBox.offset(currentVel.x, currentVel.y, currentVel.z);
				currentVel = Vec3d.method_604(currentVel.x * 0.99, currentVel.y * 0.94, currentVel.z * 0.99);
				futureBoxes.add(currentBox);

				if (!mc.world.getEntitiesIn(null, currentBox).isEmpty() || WorldUtils.doesBoxCollide(currentBox)) {
					break;
				}
			}

			for (Box box: futureBoxes) {
				if (playerBox.intersects(box)) {
					for (Vec3d vel : getMoveVecs(Vec3d.method_604(e.velocityX, e.velocityY, e.velocityZ))) {
						Box newBox = mc.field_3805.boundingBox.offset(vel.x, vel.y, vel.z);

						if (!WorldUtils.doesBoxCollide(newBox) && futureBoxes.stream().noneMatch(playerBox.offset(vel.x, vel.y, vel.z)::intersects)) {
							if (mode == 0 && vel.y == 0) {
								mc.field_3805.setVelocityClient(vel.x, vel.y, vel.z);
							} else {
								mc.field_3805.updatePosition(mc.field_3805.x + vel.x, mc.field_3805.y + vel.y, mc.field_3805.z + vel.z);
								mc.field_3805.field_1667.sendPacket(
										new class_696(mc.field_3805.x, mc.field_3805.y, mc.field_3805.y, mc.field_3805.z, false));
							}

							return;
						}
					}
				}
			}
		}
	}

	private List<Vec3d> getMoveVecs(Vec3d arrowVec) {
		double speed = getSetting(1).asSlider().getValue();

		List<Vec3d> list = new ArrayList<>(Arrays.asList(
				arrowVec.method_613(0, -arrowVec.y, 0).normalize(),
				arrowVec.method_613(0, -arrowVec.y, 0).normalize()));
		
		list.set(0, Vec3d.method_604(list.get(0).x * speed, list.get(0).y * speed, list.get(0).z * speed));
		list.get(0).method_609((float) -Math.toRadians(90f));
		list.set(1, Vec3d.method_604(list.get(1).x * speed, list.get(1).y * speed, list.get(1).z * speed));
		list.get(1).method_609((float) Math.toRadians(90f));

		Collections.shuffle(list);

		if (getSetting(3).asToggle().getState()) {
			list.add(Vec3d.method_604(0, 2, 0));
		}

		return list;
	}

}
