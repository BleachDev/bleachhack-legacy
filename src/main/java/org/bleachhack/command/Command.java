/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.command;

import net.minecraft.client.MinecraftClient;
import org.apache.commons.lang3.ArrayUtils;
import org.bleachhack.setting.option.Option;

import java.util.stream.Stream;

public abstract class Command {

	protected final MinecraftClient mc = MinecraftClient.getInstance();

	private String[] aliases;
	private String description;
	private String syntax;
	private CommandCategory category;

	public Command(String alias, String desc, String syntax, CommandCategory category, String... moreAliases) {
		this.aliases = ArrayUtils.add(moreAliases, 0, alias);
		this.description = desc;
		this.syntax = syntax;
		this.category = category;
	}

	public static String getPrefix() {
		return Option.CHAT_COMMAND_PREFIX.getValue();
	}

	public String[] getAliases() {
		return aliases;
	}

	public String getDescription() {
		return description;
	}

	public String getSyntax() {
		return syntax;
	}

	public CommandCategory getCategory() {
		return category;
	}

	public boolean hasAlias(String alias) {
		return Stream.of(aliases).anyMatch(alias::equalsIgnoreCase);
	}

	public String getHelpTooltip() {
		return "\u00a77Category: " + getCategory() + "\n"
				+ "Aliases: \u00a7f" + getPrefix() + String.join(" \u00a77/\u00a7f " + getPrefix(), getAliases()) + "\n"
				+ "Usage: \u00a7f" + getSyntax() + "\n"
				+ "Description: \u00a7f" + getDescription();
	}

	public abstract void onCommand(String alias, String[] args) throws Exception;
}
