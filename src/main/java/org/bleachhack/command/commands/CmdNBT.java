/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.command.commands;

import net.minecraft.util.hit.BlockHitResult;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bleachhack.command.Command;
import org.bleachhack.command.CommandCategory;
import org.bleachhack.command.exception.CmdSyntaxException;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.BlockPos;
import org.bleachhack.util.io.BleachJsonHelper;

import com.google.common.io.ByteStreams;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class CmdNBT extends Command {

	public CmdNBT() {
		super("nbt", "NBT stuff.", "nbt get [hand/block/entity] | nbt copy [hand/block/entity] | nbt set <nbt> | nbt wipe", CommandCategory.MISC);
	}

	@Override
	public void onCommand(String alias, String[] args) throws Exception {
		if (args.length == 0) {
			throw new CmdSyntaxException();
		}

		if (args[0].equalsIgnoreCase("get")) {
			if (args.length != 2) {
				throw new CmdSyntaxException();
			}

			NbtCompound nbt = getNbt(args[1]);

			if (nbt != null) {
				String stringNbt = nbt.toString();
				BleachLogger.info("\u00a76\u00a7lNBT: \u00a76\n" + stringNbt);
			}
		} else if (args[0].equalsIgnoreCase("copy")) {
			if (args.length != 2) {
				throw new CmdSyntaxException();
			}

			NbtCompound nbt = getNbt(args[1]);

			if (nbt != null) {
				Screen.setClipboard(nbt.toString());
				BleachLogger.info("\u00a76Copied\n\u00a7f" + nbt + "\n\u00a76to clipboard.");
			}
		} else if (args[0].equalsIgnoreCase("set")) {
			if (!mc.interactionManager.hasCreativeInventory()) {
				BleachLogger.error("You must be in creative mode to set NBT!");
				return;
			}

			if (args.length < 2) {
				throw new CmdSyntaxException();
			}

			ItemStack item = mc.field_3805.getMainHandStack();
			item.setNbt((NbtCompound) NbtCompound.readNbt(ByteStreams.newDataInput(StringUtils.join(ArrayUtils.subarray(args, 1, args.length), ' ').getBytes())));
			BleachLogger.info("\u00a76Set NBT of " + item.getName() + " to\n" + BleachJsonHelper.formatJson(item.getNbt().toString()));
		} else if (args[0].equalsIgnoreCase("wipe")) {
			if (!mc.interactionManager.hasCreativeInventory()) {
				BleachLogger.error("You must be in creative mode to wipe NBT!");
				return;
			}

			mc.field_3805.getMainHandStack().setNbt(new NbtCompound());
		} else {
			throw new CmdSyntaxException();
		}
	}

	private NbtCompound getNbt(String arg) {
		if (arg.equalsIgnoreCase("hand")) {
			return mc.field_3805.getMainHandStack().getNbt();
		} else if (arg.equalsIgnoreCase("block")) {
			BlockHitResult target = mc.result;
			if (target.pos == null) {
				BlockPos pos = new BlockPos(target.pos);
				BlockEntity b = mc.world.method_3781(pos.getX(), pos.getY(), pos.getZ());

				if (b != null) {
					NbtCompound c = new NbtCompound();
					b.toNbt(c);
					return c;
				} else {
					return new NbtCompound();
				}
			}

			BleachLogger.error("Not looking at a block.");
			return null;
		} else if (arg.equalsIgnoreCase("entity")) {
			BlockHitResult target = mc.result;
			if (target.entity != null) {
				NbtCompound c = new NbtCompound();
				target.entity.writePlayerData(c);
				return c;
			}

			BleachLogger.error("Not looking at an entity.");
			return null;
		}

		throw new CmdSyntaxException();
	}
}
