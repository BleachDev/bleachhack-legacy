/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.util;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Boxes {

	/** Returns the vector of the min pos of this box. **/
	public static Vec3d getMinVec(Box box) {
		return Vec3d.method_604(box.minX, box.minY, box.minZ);
	}

	/** Returns the vector of the max pos of this box. **/
	public static Vec3d getMaxVec(Box box) {
		return Vec3d.method_604(box.maxX, box.maxY, box.maxZ);
	}

	/** Offsets this box so that minX, minY and minZ are all zero. **/
	public static Box moveToZero(Box box) {
		Vec3d v = getMinVec(box);
		return Box.of(box.minX - v.x, box.minY - v.y, box.minZ - v.z, box.maxX - v.x, box.maxY - v.y, box.maxZ - v.z);
	}

	/** Returns the distance between to oppisite corners of the box. **/
	public static double getCornerLength(Box box) {
		return getMinVec(box).distanceTo(getMaxVec(box));
	}
	
	public static double getMin(Box box, Axis axis) {
		return axis == Axis.X ? box.minX : axis == Axis.Y ? box.minY : box.minZ;
	}
	
	public static double getMax(Box box, Axis axis) {
		return axis == Axis.X ? box.maxX : axis == Axis.Y ? box.maxY : box.maxZ;
	}

	/** Returns the length of an axis in the box. **/
	public static double getAxisLength(Box box, Axis axis) {
		return getMax(box, axis) - getMin(box, axis);
	}

	/** Returns a box with each axis multiplied by the amount specified. **/
	public static Box multiply(Box box, double amount) {
		return multiply(box, amount, amount, amount);
	}

	/** Returns a box with each axis multiplied by the amount specified. **/
	public static Box multiply(Box box, double x, double y, double z) {
		return box.expand(
				getAxisLength(box, Axis.X) * (x - 1) / 2d,
				getAxisLength(box, Axis.Y) * (y - 1) / 2d,
				getAxisLength(box, Axis.Z) * (z - 1) / 2d);
	}

	/** Returns a box with one of its sides stretched. **/
	public static Box stretch(Box box, Direction dir, double length) {
		switch (dir) {
			case DOWN: return Box.of(box.minX, box.minY - length, box.minZ, box.maxX, box.maxY, box.maxZ);
			case UP: return Box.of(box.minX, box.minY, box.minZ, box.maxX, box.maxY + length, box.maxZ);
			case NORTH: return Box.of(box.minX, box.minY, box.minZ - length, box.maxX, box.maxY, box.maxZ);
			case SOUTH: return Box.of(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ + length);
			case WEST: return Box.of(box.minX - length, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
			default: return Box.of(box.minX, box.minY, box.minZ, box.maxX + length, box.maxY, box.maxZ);
		}
	}
	
	public static enum Axis {
		X,
		Y,
		Z;
	}
}
