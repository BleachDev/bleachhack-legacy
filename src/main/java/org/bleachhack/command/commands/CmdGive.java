/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.command.commands;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.apache.commons.lang3.math.NumberUtils;
import org.bleachhack.command.Command;
import org.bleachhack.command.CommandCategory;
import org.bleachhack.command.exception.CmdSyntaxException;
import org.bleachhack.util.BleachLogger;

import com.google.common.io.ByteStreams;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

public class CmdGive extends Command {

	public CmdGive() {
		super("give", "Gives you an item.", "give <item> <count> <damage> <nbt>", CommandCategory.CREATIVE);
	}

	@Override
	public void onCommand(String alias, String[] args) throws Exception {
		if (!mc.interactionManager.hasCreativeInventory()) {
			BleachLogger.error("Not In Creative Mode!");
			return;
		}

		if (args.length == 0) {
			throw new CmdSyntaxException();
		}


		ItemStack item = Arrays.stream(Item.ITEMS)
				.filter(i -> i != null)
				.map(i -> new ItemStack(i))
				.filter(i -> i.getName().toLowerCase(Locale.ENGLISH).replace(' ', '_').equals(args[0]))
				.findFirst().orElse(null);

		if (item == null) {
			item = Arrays.stream(Block.BLOCKS)
					.filter(i -> i != null)
					.map(i -> new ItemStack(i))
					.filter(i -> i.getName().toLowerCase(Locale.ENGLISH).replace(' ', '_').equals(args[0]))
					.findFirst().orElse(null);
		}

		if (item == null)
			throw new CmdSyntaxException();

		if (args.length >= 2 && NumberUtils.isNumber(args[1]))
			item.count = NumberUtils.createNumber(args[1]).intValue();
		if (args.length >= 3 && NumberUtils.isNumber(args[2]))
			item.count = NumberUtils.createNumber(args[2]).intValue();
		if (args.length >= 4)
			item.setTag((CompoundTag) Tag.method_1652(ByteStreams.newDataInput(args[3].getBytes(StandardCharsets.UTF_8))));

		mc.field_3805.inventory.insertStack(item);
	}
}
