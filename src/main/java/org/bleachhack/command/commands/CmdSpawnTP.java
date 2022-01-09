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

public class CmdSpawnTP extends Command {

	public CmdSpawnTP() {
		super("spawntp", "teleports you to spawn", "spawntp", CommandCategory.MISC);
	}

	@Override
	public void onCommand(String alias, String[] args) throws Exception {
		mc.field_3805.updatePosition(Double.NaN, Double.NaN, Double.NaN); //spawntp?
	}

}
