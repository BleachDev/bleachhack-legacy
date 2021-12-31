/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.command.commands;

import org.bleachhack.command.Command;
import org.bleachhack.command.CommandCategory;
import org.bleachhack.util.BleachLogger;

public class CmdCI extends Command {

	public CmdCI() {
		super("ci", "Clears your inventory.", "ci", CommandCategory.CREATIVE,
				"clear", "clearinv");
	}

	@Override
	public void onCommand(String alias, String[] args) throws Exception {
		if (mc.interactionManager.getReachDistance() != 5f) {
			BleachLogger.error("Bruh you're not in creative.");
			return;
		}

		for (int i = 0; i < mc.field_3805.playerScreenHandler.getStacks().size(); i++) {
			mc.interactionManager.clickCreativeStack(null, i);
		}

		BleachLogger.info("Cleared all items");
	}

}
