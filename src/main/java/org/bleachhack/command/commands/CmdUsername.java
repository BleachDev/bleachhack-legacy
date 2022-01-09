/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.command.commands;

import net.minecraft.client.MinecraftClient;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bleachhack.command.Command;
import org.bleachhack.command.CommandCategory;

public class CmdUsername extends Command {

	public CmdUsername() {
		super("username", "sets offline username.", "username [username]", CommandCategory.MISC);
	}

	@Override
	public void onCommand(String alias, String[] args) throws Exception {
		try {
			FieldUtils.writeField(MinecraftClient.getInstance().getSession(), "username", args[0], true);
		} catch (IllegalAccessException ignored) {}
	}

}
