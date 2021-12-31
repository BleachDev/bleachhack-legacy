/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.util.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bleachhack.setting.module.SettingRotate;
import org.bleachhack.util.BlockPos;
import org.bleachhack.util.InventoryUtils;

import com.google.common.collect.Sets;

import net.minecraft.class_645;
import net.minecraft.class_699;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;

public class WorldUtils {

	protected static final MinecraftClient mc = MinecraftClient.getInstance();

	public static final Set<Material> FLUIDS = Sets.newHashSet(
			Material.WATER, Material.LAVA);

	public static List<Chunk> getLoadedChunks() {
		List<Chunk> chunks = new ArrayList<>();

		int viewDist = mc.options.field_974;

		for (int x = -viewDist; x <= viewDist; x++) {
			for (int z = -viewDist; z <= viewDist; z++) {
				Chunk chunk = mc.world.getChunkProvider().getChunk((int) mc.field_3805.x / 16 + x, (int) mc.field_3805.z / 16 + z);

				if (chunk != null) {
					chunks.add(chunk);
				}
			}
		}

		return chunks;
	}

	public static boolean isFluid(int x, int y, int z) {
		return FLUIDS.contains(mc.world.method_3776(x, y, z));
	}

	public static boolean doesBoxTouchBlock(Box box, Block block) {
		for (int x = (int) Math.floor(box.minX); x < Math.ceil(box.maxX); x++) {
			for (int y = (int) Math.floor(box.minY); y < Math.ceil(box.maxY); y++) {
				for (int z = (int) Math.floor(box.minZ); z < Math.ceil(box.maxZ); z++) {
					if (Block.field_492[mc.world.method_3774(x, y, z)] == block) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public static boolean doesBoxCollide(Box box) {
		for (int x = (int) Math.floor(box.minX); x < Math.ceil(box.maxX); x++) {
			for (int y = (int) Math.floor(box.minY); y < Math.ceil(box.maxY); y++) {
				for (int z = (int) Math.floor(box.minZ); z < Math.ceil(box.maxZ); z++) {
					if (Block.field_492[mc.world.method_3774(x, y, z)] != null && box.intersects(Box.method_581(x, y, z, x + 1, y + 1, z + 1))) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public static boolean placeBlock(BlockPos pos, int slot, SettingRotate sr, boolean forceLegit, boolean airPlace, boolean swingHand) {
		return placeBlock(pos, slot, !sr.getState() ? 0 : sr.getRotateMode() + 1, forceLegit, airPlace, swingHand);
	}

	public static boolean placeBlock(BlockPos pos, int slot, int rotateMode, boolean forceLegit, boolean airPlace, boolean swingHand) {
		if (pos.getY() < 0 || pos.getY() > 255 || !isBlockEmpty(pos))
			return false;

		for (Direction d : Direction.values()) {
			BlockPos ob = pos.offset(d);
			if (ob.getY() < 0 || ob.getY() > 255)
				continue;

			if (!airPlace && mc.world.method_3776(ob.getX(), ob.getY(), ob.getZ()).isReplaceable())
				continue;

			Vec3d vec = getLegitLookPos(pos.offset(d), opposite(d), true, 5);

			if (vec == null) {
				if (forceLegit) {
					continue;
				}

				vec = getLegitLookPos(pos.offset(d), opposite(d), false, 5);

				if (vec == null) {
					continue;
				}
			}

			int prevSlot = mc.field_3805.inventory.selectedSlot;
			boolean hand = InventoryUtils.selectSlot(slot);

			if (!hand) {
				return false;
			}

			if (rotateMode == 1) {
				facePosPacket(vec.x, vec.y, vec.z);
			} else if (rotateMode == 2) {
				facePos(vec.x, vec.y, vec.z);
			}

			mc.field_3805.field_1667.method_1202(new ClientCommandC2SPacket(mc.field_3805, 1));

			if (swingHand) {
				mc.field_3805.swingHand();
			} else {
				mc.field_3805.field_1667.method_1202(new class_645(mc.field_3805, 1));
			}

			rightClick(pos, vec);

			mc.field_3805.field_1667.method_1202(new ClientCommandC2SPacket(mc.field_3805, 2));
			mc.field_3805.inventory.selectedSlot = prevSlot;

			return true;
		}

		return false;
	}
	
	public static void rightClick(BlockPos pos) {
		rightClick(pos, Vec3d.method_604(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
	}
	
	public static void rightClick(BlockPos pos, Vec3d vec) {
		mc.interactionManager.method_1229(mc.field_3805, mc.world, mc.field_3805.getMainHandStack(),
				pos.getX(), pos.getY(), pos.getZ(), 0, vec);
	}

	public static Vec3d getLegitLookPos(BlockPos pos, Direction dir, boolean raycast, int res) {
		return getLegitLookPos(Box.method_581(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1), dir, raycast, res, 0.01);
	}

	public static Vec3d getLegitLookPos(Box box, Direction dir, boolean raycast, int res, double extrude) {
		Vec3d eyePos = Vec3d.method_604(mc.field_3805.x, mc.field_3805.y + mc.field_3805.getEyeHeight(), mc.field_3805.z);
		Vec3d blockPos = Vec3d.method_604(box.minX, box.minY, box.minZ).method_613(
				(dir == Direction.WEST ? -extrude : dir.getOffsetX() * (box.maxX - box.minX) + extrude),
				(dir == Direction.DOWN ? -extrude : dir.getOffsetY() * (box.maxY - box.minY) + extrude),
				(dir == Direction.NORTH ? -extrude : dir.getOffsetZ() * (box.maxZ - box.minZ) + extrude));

		for (double i = 0; i <= 1; i += 1d / (double) res) {
			for (double j = 0; j <= 1; j += 1d / (double) res) {
				Vec3d lookPos = blockPos.method_613(
						(dir.getOffsetX() != 0 ? 0 : i * (box.maxX - box.minX)),
						(dir.getOffsetY() != 0 ? 0 : dir.getOffsetZ() != 0 ? j * (box.maxY - box.minY) : i * (box.maxY - box.minY)),
						(dir.getOffsetZ() != 0 ? 0 : j * (box.maxZ - box.minZ)));

				if (eyePos.distanceTo(lookPos) > 4.55)
					continue;

				if (raycast) {
					Vec3d r = mc.world.rayTrace(eyePos, lookPos).pos;
					if (r.x == lookPos.x && r.y == lookPos.y && r.z == lookPos.z) {
						return lookPos;
					}
				} else {
					return lookPos;
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static boolean isBlockEmpty(BlockPos pos) {
		if (!mc.world.method_3776(pos.getX(), pos.getY(), pos.getZ()).isReplaceable()) {
			return false;
		}

		Box box = Box.method_581(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
		for (Entity e : (List<Entity>) mc.world.entities) {
			if (e instanceof LivingEntity && box.intersects(e.boundingBox)) {
				return false;
			}
		}

		return true;
	}

	public static boolean canPlaceBlock(BlockPos pos) {
		if (pos.getY() < 0 || pos.getY() > 255 || !isBlockEmpty(pos))
			return false;

		for (Direction d : Direction.values()) {
			BlockPos ob = pos.offset(d);
			if ((d == Direction.DOWN && pos.getY() == 0) || (d == Direction.UP && pos.getY() == 255)
					|| mc.world.method_3776(ob.getX(), ob.getY(), ob.getZ()).isReplaceable()
					|| Vec3d.method_604(mc.field_3805.x, mc.field_3805.y + mc.field_3805.getEyeHeight(), mc.field_3805.z).distanceTo(
							Vec3d.method_604(pos.getX() + 0.5 + d.getOffsetX() * 0.5,
									pos.getY() + 0.5 + d.getOffsetY() * 0.5,
									pos.getZ() + 0.5 + d.getOffsetZ() * 0.5)) > 4.55)
				continue;

			return true;
		}
		return false;
	}

	public static void facePosAuto(double x, double y, double z, SettingRotate sr) {
		if (sr.getRotateMode() == 0) {
			facePosPacket(x, y, z);
		} else {
			facePos(x, y, z);
		}
	}

	public static void facePos(double x, double y, double z) {
		double diffX = x - mc.field_3805.x;
		double diffY = y - (mc.field_3805.y + mc.field_3805.getEyeHeight());
		double diffZ = z - mc.field_3805.z;

		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

		float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

		mc.field_3805.yaw = mc.field_3805.yaw + MathHelper.wrapDegrees(yaw - mc.field_3805.yaw);
		mc.field_3805.pitch = mc.field_3805.pitch + MathHelper.wrapDegrees(pitch - mc.field_3805.pitch);
	}

	public static void facePosPacket(double x, double y, double z) {
		double diffX = x - mc.field_3805.x;
		double diffY = y - (mc.field_3805.y + mc.field_3805.getEyeHeight());
		double diffZ = z - mc.field_3805.z;

		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

		float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

		if (!mc.field_3805.hasVehicle()) {
			mc.field_3805.headYaw = mc.field_3805.yaw + MathHelper.wrapDegrees(yaw - mc.field_3805.yaw);
			mc.field_3805.bodyYaw = mc.field_3805.yaw + MathHelper.wrapDegrees(yaw - mc.field_3805.yaw);
			mc.field_3805.renderPitch = mc.field_3805.pitch + MathHelper.wrapDegrees(pitch - mc.field_3805.pitch);
		}

		mc.field_3805.field_1667.method_1202(
				new class_699(
						mc.field_3805.yaw + MathHelper.wrapDegrees(yaw - mc.field_3805.yaw),
						mc.field_3805.pitch + MathHelper.wrapDegrees(pitch - mc.field_3805.pitch), mc.field_3805.onGround));
	}
	
	public static Direction opposite(Direction d) {
		return d.ordinal() % 2 == 0 ? Direction.values()[d.ordinal() + 1] : Direction.values()[d.ordinal() - 1];
	}
}