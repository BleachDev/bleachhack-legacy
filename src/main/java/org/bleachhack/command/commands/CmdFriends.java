/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.command.commands;

import org.bleachhack.BleachHack;
import org.bleachhack.command.Command;
import org.bleachhack.command.CommandCategory;
import org.bleachhack.command.exception.CmdSyntaxException;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.io.BleachFileHelper;

import java.util.Locale;

public class CmdFriends extends Command {

	public CmdFriends() {
		super("friends", "Manage friends.", "friends add <user> | friends remove <user> | friends list | friends clear", CommandCategory.MISC,
				"friend");
	}

	@Override
	public void onCommand(String alias, String[] args) throws Exception {
		if (args.length == 0 || args.length > 2) {
			throw new CmdSyntaxException();
		}

		if (args[0].equalsIgnoreCase("add")) {
			if (args.length < 2) {
				throw new CmdSyntaxException("No username selected");
			}

			BleachHack.friendMang.add(args[1]);
			BleachLogger.info("Added \"" + args[1] + "\" to the friend list");
		} else if (args[0].equalsIgnoreCase("remove")) {
			if (args.length < 2) {
				throw new CmdSyntaxException("No username selected");
			}

			BleachHack.friendMang.remove(args[1].toLowerCase(Locale.ENGLISH));
			BleachLogger.info("Removed \"" + args[1] + "\" from the friend list");
		} else if (args[0].equalsIgnoreCase("list")) {
			if (BleachHack.friendMang.getFriends().isEmpty()) {
				BleachLogger.info("You don't have any friends :(");
			} else {
				String text = "Friends:";

				for (String f : BleachHack.friendMang.getFriends()) {
					text += BleachLogger.INFO_COLOR + "\n> " + f;
				}

				BleachLogger.info(text);
			}
		} else if (args[0].equalsIgnoreCase("clear")) {
			BleachHack.friendMang.getFriends().clear();

			BleachLogger.info("Cleared Friend list");
		} else {
			throw new CmdSyntaxException();
		}

		BleachFileHelper.SCHEDULE_SAVE_FRIENDS.set(true);
	}

}
