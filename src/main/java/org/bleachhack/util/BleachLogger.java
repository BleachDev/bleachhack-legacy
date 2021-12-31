/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bleachhack.BleachHack;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;

public class BleachLogger {

	public static final Logger logger = LogManager.getFormatterLogger("BleachHack");

	public static Formatting INFO_COLOR = Formatting.AQUA;
	public static Formatting WARN_COLOR = Formatting.YELLOW;
	public static Formatting ERROR_COLOR = Formatting.RED;
	
	// Info

	public static void info(String t) {
		try {
			MinecraftClient.getInstance().inGameHud.getChatHud().method_898(getBHText(INFO_COLOR) + t);
		} catch (Exception e) {
			logger.log(Level.INFO, t);
		}
	}
	
	// Warn

	public static void warn(String t) {
		try {
			MinecraftClient.getInstance().inGameHud.getChatHud().method_898(getBHText(WARN_COLOR) + t);
		} catch (Exception e) {
			logger.log(Level.WARN, t);
		}
	}
	
	// Error

	public static void error(String t) {
		try {
			MinecraftClient.getInstance().inGameHud.getChatHud().method_898(getBHText(ERROR_COLOR) + t);
		} catch (Exception e) {
			logger.log(Level.ERROR, t);
		}
	}
	
	public static void noPrefix(String text) {
		try {
			MinecraftClient.getInstance().inGameHud.getChatHud().method_898(text);
		} catch (Exception e) {
			logger.log(Level.INFO, text);
		}
	}

	private static String getBHText(Formatting formatting) {
		return formatting + "[" + BleachHack.watermark.getText() + formatting + "] ";
	}
}
