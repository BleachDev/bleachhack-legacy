/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.command.commands;

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.hit.HitResult;

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

			CompoundTag nbt = getNbt(args[1]);

			if (nbt != null) {
				String stringNbt = nbt.toString();
				BleachLogger.info("\u00a76\u00a7lNBT: \u00a76\n" + stringNbt);
			}
		} else if (args[0].equalsIgnoreCase("copy")) {
			if (args.length != 2) {
				throw new CmdSyntaxException();
			}

			CompoundTag nbt = getNbt(args[1]);

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
			item.setTag((CompoundTag) Tag.method_1652(ByteStreams.newDataInput(StringUtils.join(ArrayUtils.subarray(args, 1, args.length), ' ').getBytes())));
			BleachLogger.info("\u00a76Set NBT of " + item.getName() + " to\n" + BleachJsonHelper.formatJson(item.getTag().toString()));
		} else if (args[0].equalsIgnoreCase("wipe")) {
			if (!mc.interactionManager.hasCreativeInventory()) {
				BleachLogger.error("You must be in creative mode to wipe NBT!");
				return;
			}

			mc.field_3805.getMainHandStack().setTag(new CompoundTag());
		} else {
			throw new CmdSyntaxException();
		}
	}

	private CompoundTag getNbt(String arg) {
		if (arg.equalsIgnoreCase("hand")) {
			return mc.field_3805.getMainHandStack().getTag();
		} else if (arg.equalsIgnoreCase("block")) {
			HitResult target = mc.result;
			if (target.pos == null) {
				BlockPos pos = new BlockPos(target.pos);
				BlockEntity b = mc.world.method_3781(pos.getX(), pos.getY(), pos.getZ());

				if (b != null) {
					CompoundTag c = new CompoundTag();
					b.toTag(c);
					return c;
				} else {
					return new CompoundTag();
				}
			}

			BleachLogger.error("Not looking at a block.");
			return null;
		} else if (arg.equalsIgnoreCase("entity")) {
			HitResult target = mc.result;
			if (target.entitiy != null) {
				CompoundTag c = new CompoundTag();
				target.entitiy.writePlayerData(c);
				return c;
			}

			BleachLogger.error("Not looking at an entity.");
			return null;
		}

		throw new CmdSyntaxException();
	}
}
