/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.command.commands;

import org.apache.commons.lang3.math.NumberUtils;
import org.bleachhack.command.Command;
import org.bleachhack.command.CommandCategory;
import org.bleachhack.command.exception.CmdSyntaxException;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.world.WorldUtils;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

public class CmdClip extends Command {

	public CmdClip() {
		super("clip", "Teleports you a certain amount of blocks horizontally/vertically.", "clip up | clip down | clip <x distance> <y distance> <z distance>", CommandCategory.MISC);
	}

	@Override
	public void onCommand(String alias, String[] args) throws Exception {
		if (args.length == 0) {
			throw new CmdSyntaxException();
		}

		if (args[0].equalsIgnoreCase("up") || args[0].equalsIgnoreCase("down")) {
			int moveStep = args[0].equalsIgnoreCase("up") ? 1 : -1;

			Box box = mc.field_3805.boundingBox;
			if (mc.field_3805.hasVehicle()) {
				box = box.union(mc.field_3805.vehicle.boundingBox);
			}

			for (int y = MathHelper.floor(box.minY) + moveStep; y >= 0 && y <= 256; y += moveStep) {
				if (WorldUtils.doesBoxCollide(Box.method_581(box.minX, y - 1, box.minZ, box.maxX, y - 0.01, box.maxZ))
						&& !WorldUtils.doesBoxCollide(box.offset(0, -box.minY + y, 0))) {
					move(0, y - box.minY, 0);
					return;
				}
			}

			BleachLogger.error("No empty spaces to clip you to!");
		} else {
			if (args.length != 3) {
				throw new CmdSyntaxException();
			}

			if (!NumberUtils.isNumber(args[0])) {
				throw new CmdSyntaxException("Invalid x distance \"" + args[0] + "\"");
			}

			if (!NumberUtils.isNumber(args[1])) {
				throw new CmdSyntaxException("Invalid y distance \"" + args[1] + "\"");
			}

			if (!NumberUtils.isNumber(args[2])) {
				throw new CmdSyntaxException("Invalid z distance \"" + args[2] + "\"");
			}

			move(NumberUtils.createNumber(args[0]).doubleValue(),
					NumberUtils.createNumber(args[1]).doubleValue(),
					NumberUtils.createNumber(args[2]).doubleValue());
		}
	}

	private void move(double xOffset, double yOffset, double zOffset) {
		if (mc.field_3805.hasVehicle()) {
			mc.field_3805.vehicle.updatePosition(
					mc.field_3805.vehicle.x + xOffset,
					mc.field_3805.vehicle.y + yOffset,
					mc.field_3805.vehicle.z + zOffset);
		}

		mc.field_3805.updatePosition(mc.field_3805.x + xOffset, mc.field_3805.y + yOffset, mc.field_3805.z + zOffset);
	}

}
