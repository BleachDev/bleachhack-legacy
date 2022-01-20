/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.util.operation;

import org.bleachhack.util.InventoryUtils;
import org.bleachhack.util.render.Renderer;
import org.bleachhack.util.render.color.QuadColor;
import org.bleachhack.util.world.WorldUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.class_535;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.item.ItemStack;

import java.util.function.IntPredicate;

import org.bleachhack.mixin.AccessorMinecraftClient;
import org.bleachhack.util.BlockPos;
import net.minecraft.util.math.Box;

public class PlaceOperation extends Operation {

	protected int[] items;

	public PlaceOperation(BlockPos pos, int... items) {
		this.pos = pos;
		this.items = items;
	}

	@Override
	public boolean canExecute() {
		if (mc.field_3805.distanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 4.55)
			return false;

		return InventoryUtils.getSlot(getItemPredicate(items)) != -1;
	}

	@Override
	public boolean execute() {
		int slot = InventoryUtils.getSlot(getItemPredicate(items));

		return WorldUtils.placeBlock(pos, slot, 0, false, false, true);
	}

	@Override
	public boolean verify() {
		return true;
	}

	public int[] getItems() {
		return items;
	}

	@Override
	public void render() {
		Block block = Block.BLOCKS[getItems()[0]];
		if (block != null) {
			BlockEntity be = mc.world.method_3781(pos.getX(), pos.getY(), pos.getZ());
			BlockEntityRenderer renderer = be != null ? BlockEntityRenderDispatcher.INSTANCE.method_1630(be) : null;
			if (renderer != null) {
				renderer.method_1631(be,
						pos.getX() - BlockEntityRenderDispatcher.field_2189,
						pos.getY() - BlockEntityRenderDispatcher.field_2190,
						pos.getZ() - BlockEntityRenderDispatcher.field_2191,
						((AccessorMinecraftClient) mc).getTricker().tickDelta);
			} else {
				GL11.glPushMatrix();
				GL11.glTranslated(
						pos.getX() - BlockEntityRenderDispatcher.field_2189 + 0.5,
						pos.getY() - BlockEntityRenderDispatcher.field_2190 + 0.5,
						pos.getZ() - BlockEntityRenderDispatcher.field_2191 + 0.5);
				class_535 rend = new class_535();
				rend.method_4320(block);
				rend.method_1453(block, mc.world, pos.getX(), pos.getY(), pos.getZ(), 0);
				GL11.glPopMatrix();
			}
		}

		Box box = block != null ? block.method_427(mc.world, pos.getX(), pos.getY(), pos.getZ()) : pos.toBox();
		Renderer.drawBoxFill(box, QuadColor.single(0.45f, 0.7f, 1f, 0.4f));
	}
	
	protected static IntPredicate getItemPredicate(int[] items) {
		return i -> {
			ItemStack is = mc.field_3805.inventory.getInvStack(i);
			if (is != null) {
				for (int b: items) {
					if (is.getItem().id == b)
						return true;
				}
			}

			return false;
		};
	}
}
