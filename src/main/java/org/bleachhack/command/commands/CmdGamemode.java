/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.command.commands;

import java.util.Locale;

import org.bleachhack.command.Command;
import org.bleachhack.command.CommandCategory;
import org.bleachhack.command.exception.CmdSyntaxException;
import org.bleachhack.util.BleachLogger;

import net.minecraft.world.GameMode;

public class CmdGamemode extends Command {

	public CmdGamemode() {
		super("gamemode", "Sets your clientside gamemode.", "gamemode [survival/creative/adventure]| gamemode <0-2>", CommandCategory.MISC,
				"gm");
	}

	@Override
	public void onCommand(String alias, String[] args) throws Exception {
		if (args.length == 0) {
			throw new CmdSyntaxException();
		}
		
		String lower = args[0].toLowerCase(Locale.ENGLISH);

		if (lower.equals("0") || lower.startsWith("su")) {
			mc.interactionManager.method_1233(GameMode.SURVIVAL);
			BleachLogger.info("\u00a7l\u00a7nClientside\u00a7r gamemode has been set to survival.");
		} else if (lower.equals("1") || lower.startsWith("c")) {
			mc.interactionManager.method_1233(GameMode.CREATIVE);
			BleachLogger.info("\u00a7l\u00a7nClientside\u00a7r gamemode has been set to creative.");
		} else if (lower.equals("2") || lower.startsWith("a")) {
			mc.interactionManager.method_1233(GameMode.ADVENTURE);
			BleachLogger.info("\u00a7l\u00a7nClientside\u00a7r gamemode has been set to adventure.");
		} else {
			throw new CmdSyntaxException("Unknown Gamemode!");
		}
	}

}
