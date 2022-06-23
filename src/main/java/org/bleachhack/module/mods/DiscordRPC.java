/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.StringUtils;
import org.bleachhack.BleachHack;
import org.bleachhack.command.Command;
import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.mixin.AccessorMinecraftClient;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingMode;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.BlockPos;
import org.bleachhack.util.io.BleachFileHelper;

import com.google.gson.JsonElement;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.pipe.PipeStatus;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;

import net.minecraft.item.ItemStack;

public class DiscordRPC extends Module {

	private IPCClient client;
	private Thread startThread;

	private String customText1 = "top text";
	private String customText2 = "bottom text";

	private long startTime;
	private int tick;

	public DiscordRPC() {
		super("DiscordRPC", KEY_UNBOUND, ModuleCategory.MISC, true, "Discord RPC, use the " + Command.getPrefix() + "rpc command to set a custom status.",
				new SettingMode("Line1", "Playing %server%", "%server%", "%type%", "%username% ontop", "Minecraft %mcver%", "%username%", "<- bad client", "%custom%").withDesc("The top line."),
				new SettingMode("Line2", "%hp% hp - Holding %item%", "%username% - %hp% hp", "Holding %item%", "%hp% hp - At %coords%", "At %coords%", "%custom%").withDesc("The bottom line."),
				new SettingMode("Elapsed", "Normal", "Random", "Backwards", "None").withDesc("How to show elapsed time"),
				new SettingToggle("Silent", false).withDesc("Use a generic Minecraft title and image."));

		JsonElement t1 = BleachFileHelper.readMiscSetting("discordRPCTopText");
		JsonElement t2 = BleachFileHelper.readMiscSetting("discordRPCBottomText");

		if (t1 != null) {
			customText1 = t1.getAsString();
		}

		if (t2 != null) {
			customText2 = t2.getAsString();
		}
	}

	@Override
	public void onEnable(boolean inWorld) {
		super.onEnable(inWorld);

		tick = 0;
		startTime = System.currentTimeMillis();

		if (client == null) {
			BleachLogger.logger.info("Initing Discord IPC...");
			client = new IPCClient(740928841433743370L);
			client.setListener(new IPCListener() {
				@Override
				public void onReady(IPCClient client) {
					BleachLogger.logger.info("Connected to Discord!");
				}
			});
		}

		startThread = new Thread(() -> {
			try {
				client.connect();
			} catch (NoDiscordClientException e) {
				BleachLogger.error("Failed to connect to Discord!");
				setEnabled(false);
			}
		});
		startThread.start();
	}

	@Override
	public void onDisable(boolean inWorld) {
		try {
			startThread.join();
			disconnect();
		} catch (InterruptedException ignored) {}

		super.onDisable(inWorld);
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		if (client == null || client.getStatus() != PipeStatus.CONNECTED)
			return;

		if (tick % 40 == 0) {
			boolean silent = getSetting(3).asToggle().getState();
			RichPresence.Builder builder = new RichPresence.Builder()
					.setLargeImage(silent ? "mc" : "bh", silent ? "Minecraft " + BleachHack.MCVERSION : "BleachHack " + BleachHack.VERSION);

			// Top text
			builder.setDetails(getDetails());

			// Bottom text
			ItemStack currentItem = mc.field_3805.inventory.getMainHandStack();

			if (currentItem != null) {
				String customName = StringUtils.strip(currentItem.getName());
				if (customName.length() > 25) {
					customName = customName.substring(0, 23) + "..";
				}
	
				String name = currentItem.getName();
				String itemName = currentItem.isEmpty() ? "Nothing"
						: (currentItem.count > 1 ? currentItem.count + " " : "")
						+ (currentItem.hasCustomName() ? customName : name);
	
				builder.setState(getState(itemName));
			} else {
				builder.setState(getState("Nothing"));
			}

			// Start time
			if (getSetting(2).asMode().getMode() != 3) {
				builder.setStartTimestamp(OffsetDateTime.ofInstant(Instant.ofEpochMilli(getTime()), ZoneOffset.systemDefault()));
			}

			// Build
			client.sendRichPresence(builder.build());
		}

		tick++;
	}
	
	private String getDetails() {
		switch(getSetting(0).asMode().getMode()) {
			case 0: return "Playing " + (((AccessorMinecraftClient) mc).getCurrentServerEntry() == null ? "Singleplayer" : ((AccessorMinecraftClient) mc).getCurrentServerEntry().address)+" "+BleachHack.MCVERSION;
			case 1: return ((AccessorMinecraftClient) mc).getCurrentServerEntry() == null ? "Singleplayer" : ((AccessorMinecraftClient) mc).getCurrentServerEntry().address;
			case 2: return ((AccessorMinecraftClient) mc).getCurrentServerEntry() == null ? "Singleplayer" : "Multiplayer";
			case 3: return mc.field_3805.getUsername() + " Ontop!";
			case 4: return "Minecraft " + BleachHack.MCVERSION;
			case 5: return mc.field_3805.getUsername();
			case 6: return "<- bad client";
			default: return customText1;
		}
	}
	
	private String getState(String itemName) {
		switch (getSetting(1).asMode().getMode()) {
			case 0: return (int) mc.field_3805.getHealth() + " hp - Holding " + itemName;
			case 1: return mc.field_3805.getUsername() + " - " + (int) mc.field_3805.getHealth() + " hp";
			case 2: return "Holding " + itemName;
			case 3: return (int) mc.field_3805.getHealth() + " hp - At " + new BlockPos(mc.field_3805).toShortString();
			case 4: return "At " + new BlockPos(mc.field_3805).toShortString();
			default: return customText2;
		}
	}
	
	private long getTime() {
		switch (getSetting(2).asMode().getMode()) {
			case 1: return System.currentTimeMillis() - ThreadLocalRandom.current().nextInt(0, 86400000);
			case 2: return System.currentTimeMillis() - 86400000L + (long) tick * 50;
			default: return startTime;
		}
	}

	private void disconnect() {
		if (client.getStatus() == PipeStatus.CONNECTED || client.getStatus() == PipeStatus.CONNECTING)
			client.close();
	}

	public void setTopText(String text) {
		customText1 = text;
	}

	public void setBottomText(String text) {
		customText2 = text;
	}

	public String getTopText() {
		return customText1;
	}

	public String getBottomText() {
		return customText2;
	}
}
