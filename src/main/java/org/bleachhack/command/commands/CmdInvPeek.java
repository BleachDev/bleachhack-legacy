/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.command.commands;

import java.util.List;

import org.bleachhack.command.Command;
import org.bleachhack.command.CommandCategory;
import org.bleachhack.command.exception.CmdSyntaxException;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.BleachQueue;
import net.minecraft.client.gui.screen.ingame.SurvivalInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class CmdInvPeek extends Command {

	public CmdInvPeek() {
		super("invpeek", "Shows the inventory of another player in your render distance.", "invpeek <player>", CommandCategory.MISC,
				"playerpeek", "invsee", "inv");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCommand(String alias, String[] args) throws Exception {
		if (args.length == 0) {
			throw new CmdSyntaxException();
		}

		for (PlayerEntity e: (List<PlayerEntity>) mc.world.playerEntities) {
			if (e.getTranslationKey().equalsIgnoreCase(args[0])) {
				BleachQueue.add(() -> {
					BleachLogger.info("Opened inventory for " + e.getTranslationKey());

					mc.openScreen(new SurvivalInventoryScreen(e) {
						public void mouseClicked(int mouseX, int mouseY, int button) {
						}
					});
				});

				return;
			}
		}

		BleachLogger.error("Player " + args[0] + " not found!");
	}

}
