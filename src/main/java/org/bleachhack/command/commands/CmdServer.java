/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.command.commands;

import net.minecraft.client.class_482;
import org.bleachhack.BleachHack;
import org.bleachhack.command.Command;
import org.bleachhack.command.CommandCategory;
import org.bleachhack.command.exception.CmdSyntaxException;
import org.bleachhack.mixin.AccessorMinecraftClient;
import org.bleachhack.util.BleachLogger;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CmdServer extends Command {

	public CmdServer() {
		super("server", "Server things.", "server address | server brand | server day | server ip | server motd | server ping | server permissions | server protocol | server version", CommandCategory.MISC);
	}

	@Override
	public void onCommand(String alias, String[] args) throws Exception {
		boolean sp = mc.isIntegratedServerRunning();

		if (!sp && ((AccessorMinecraftClient) mc).getCurrentServerEntry() == null) {
			BleachLogger.error("Unable to get server info.");
			return;
		}

		BleachLogger.info("Server Info");

		if (args.length == 0) {
			BleachLogger.noPrefix(createText("Address", getAddress(sp)));
			BleachLogger.noPrefix(createText("Day", getDay(sp)));
			BleachLogger.noPrefix(createText("IP", getIP(sp)));
			BleachLogger.noPrefix(createText("Motd", getMotd(sp)));
			BleachLogger.noPrefix(createText("Ping", getPing(sp)));
			BleachLogger.noPrefix(createText("Protocol", getProtocol(sp)));
			BleachLogger.noPrefix(createText("Version", getVersion(sp)));
		} else if (args[0].equalsIgnoreCase("address")) {
			BleachLogger.noPrefix(createText("Address", getAddress(sp)));
		} else if (args[0].equalsIgnoreCase("day")) {
			BleachLogger.noPrefix(createText("Day", getDay(sp)));
		} else if (args[0].equalsIgnoreCase("ip")) {
			BleachLogger.noPrefix(createText("IP", getIP(sp)));
		} else if (args[0].equalsIgnoreCase("motd")) {
			BleachLogger.noPrefix(createText("Motd", getMotd(sp)));
		} else if (args[0].equalsIgnoreCase("ping")) {
			BleachLogger.noPrefix(createText("Ping", getPing(sp)));
		} else if (args[0].equalsIgnoreCase("protocol")) {
			BleachLogger.noPrefix(createText("Protocol", getProtocol(sp)));
		} else if (args[0].equalsIgnoreCase("version")) {
			BleachLogger.noPrefix(createText("Version", getVersion(sp)));
		} else {
			throw new CmdSyntaxException("Invalid server bruh.");
		}
	}
	
	public String createText(String name, String value) {
		boolean newlines = value.contains("\n");
		return "\u00a77" + name + "\u00a7f:" + (newlines ? "\n" : " " ) + "\u00a7a" + value;
	}

	public String getAddress(boolean singleplayer) {
		if (singleplayer)
			return "Singleplayer";

		return ((AccessorMinecraftClient) mc).getCurrentServerEntry().address != null ? ((AccessorMinecraftClient) mc).getCurrentServerEntry().address : "Unknown";
	}

	public String getDay(boolean singleplayer) {
		return "Day " + (mc.world.getTimeOfDay() / 24000L);
	}

	public String getIP(boolean singleplayer) {
		try {
			if (singleplayer)
				return InetAddress.getLocalHost().getHostAddress();

			return ((AccessorMinecraftClient) mc).getCurrentServerEntry().address != null ? InetAddress.getByName(((AccessorMinecraftClient) mc).getCurrentServerEntry().address).getHostAddress() : "Unknown";
		} catch (UnknownHostException e) {
			return "Unknown";
		}
	}

	public String getMotd(boolean singleplayer) {
		if (singleplayer)
			return "-";

		return ((AccessorMinecraftClient) mc).getCurrentServerEntry().label != null ? ((AccessorMinecraftClient) mc).getCurrentServerEntry().label : "Unknown";
	}

	public String getPing(boolean singleplayer) {
		@SuppressWarnings("unchecked")
		int ping = mc.field_3805.field_1667.field_1618.stream().filter(e -> ((class_482) e).field_1679.equals(mc.getSession().getUsername())).mapToInt(e -> ((class_482) e).field_1680).findFirst().orElse(0);
		return "" + ping;
	}

	public String getProtocol(boolean singleplayer) {
		if (singleplayer)
			return "Integrated";

		return Integer.toString(((AccessorMinecraftClient) mc).getCurrentServerEntry().protocolVersion);
	}

	public String getVersion(boolean singleplayer) {
		if (singleplayer)
			return BleachHack.MCVERSION;

		return ((AccessorMinecraftClient) mc).getCurrentServerEntry().version != null ? ((AccessorMinecraftClient) mc).getCurrentServerEntry().version : "Unknown (" + BleachHack.MCVERSION + ")";
	}
}
