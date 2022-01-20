/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.command.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bleachhack.command.Command;
import org.bleachhack.command.CommandCategory;
import org.bleachhack.util.BleachLogger;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;

public class CmdRbook extends Command {

	public CmdRbook() {
		super("rbook", "Generates a random book.", "rbook <pages> <start char> <end char> <chrs/page>", CommandCategory.MISC,
				"randombook", "book");
	}

	@Override
	public void onCommand(String alias, String[] args) throws Exception {
		ItemStack item = mc.field_3805.inventory.getMainHandStack();

		if (item.getItem() != Item.WRITABLE_BOOK) {
			BleachLogger.error("Not Holding A Writable Book!");
			return;
		}

		int pages = args.length >= 1 && NumberUtils.isNumber(args[0]) ? NumberUtils.createNumber(args[0]).intValue() : 100;
		int startChar = args.length >= 2 && NumberUtils.isNumber(args[1]) ? NumberUtils.createNumber(args[1]).intValue() : 0;
		int endChar = args.length >= 3 && NumberUtils.isNumber(args[2]) ? NumberUtils.createNumber(args[2]).intValue() : 0x10FFFF;
		int pageChars = args.length >= 4 && NumberUtils.isNumber(args[3]) ? NumberUtils.createNumber(args[3]).intValue() : 210;

		ListTag pages1 = new ListTag();

		for (int t = 0; t < pages; t++)
			pages1.method_1217(new StringTag(RandomStringUtils.random(pageChars, startChar, endChar, false, false)));
		
		item.putSubTag("pages", pages1);

		ByteArrayOutputStream var3 = new ByteArrayOutputStream();
		DataOutputStream var4 = new DataOutputStream(var3);

		Packet.writeStack(item, var4);
		mc.field_3805.field_1667.sendPacket(new CustomPayloadC2SPacket("MC|BEdit", var3.toByteArray()));

		BleachLogger.info("Written book (" + pages + " pages, " + pageChars + " chars/page)");
	}

}
