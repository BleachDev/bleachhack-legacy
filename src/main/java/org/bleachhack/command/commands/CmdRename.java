/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.command.commands;

import org.apache.commons.lang3.StringUtils;
import org.bleachhack.command.Command;
import org.bleachhack.command.CommandCategory;
import org.bleachhack.util.BleachLogger;

import net.minecraft.item.ItemStack;

public class CmdRename extends Command {

	public CmdRename() {
		super("rename", "Renames an item, use \"&\" for color.", "rename <name>", CommandCategory.CREATIVE);
	}

	@Override
	public void onCommand(String alias, String[] args) throws Exception {
		if (!mc.interactionManager.hasCreativeInventory()) {
			BleachLogger.error("Not In Creative Mode!");
			return;
		}

		ItemStack i = mc.field_3805.inventory.getMainHandStack();

		i.method_4622(StringUtils.join(args, ' ').replace("&", "\u00a7").replace("\u00a7\u00a7", "&"));
		BleachLogger.info("Renamed Item");
	}

}
