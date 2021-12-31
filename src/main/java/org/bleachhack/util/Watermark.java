/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.util;

public class Watermark {

	private String text1;
	private String text2;
	private int color1;
	private int color2;
	
	public Watermark() {
		reset(true, true);
	}

	public void reset(boolean strings, boolean colors) {
		if (strings) {
			text1 = "Bleach";
			text2 = "Hack";
		}

		if (colors) {
			color1 = 0xffbf30;
			color2 = 0xffafcc;
		}
	}

	public String getString1() {
		return text1;
	}

	public String getString2() {
		return text2;
	}

	public void setStrings(String text1, String text2) {
		this.text1 = text1;
		this.text2 = text2;
	}

	public int getColor1() {
		return color1;
	}

	public int getColor2() {
		return color2;
	}

	public void setColor(int color1, int color2) {
		this.color1 = color1;
		this.color2 = color2;
	}

	public String getText() {
		return text2 == null ? "\u00a7e" + text1 : "\u00a7e" + text1 + "\u00a7d" + text2;
	}

	public String getShortText() {
		if (text2.isEmpty()) {
			return "\u00a7e" + text1.substring(0, 2);
		} else {
			return "\u00a7e" + text1.substring(0, 1) + "\u00a7d" + text2.substring(0, 1);
		}
	}
}
