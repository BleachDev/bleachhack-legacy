/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.event.events;

import org.bleachhack.event.Event;

public class EventKeyPress extends Event {

	private char character;
	private int key;

	public EventKeyPress(char character, int key) {
		this.character = character;
		this.key = key;
	}

	public int getKey() {
		return key;
	}

	public char getChar() {
		return character;
	}

	/*public static class Global extends EventKeyPress {

		private int action;
		private int modifiers;

		public Global(int key, int scanCode, int action, int modifiers) {
			super(key, scanCode);
			this.action = action;
			this.modifiers = modifiers;
		}

		public int getAction() {
			return action;
		}

		public int getModifiers() {
			return modifiers;
		}

	}*/

	public static class InWorld extends EventKeyPress {

		public InWorld(char character, int key) {
			super(character, key);
		}

	}

	public static class InChat extends EventKeyPress {

		public InChat(char character, int key) {
			super(character, key);
		}

	}
}
