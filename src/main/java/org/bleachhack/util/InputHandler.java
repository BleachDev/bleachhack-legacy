package org.bleachhack.util;

import org.lwjgl.input.Keyboard;

public class InputHandler {

	private long lastKeyNano;
	
	public int[] poll() {
		long nano = Keyboard.getEventNanoseconds();
		if (lastKeyNano != nano && Keyboard.getEventKeyState()) {
			lastKeyNano = nano;
			return new int[] { Keyboard.getEventKey(), Keyboard.getEventCharacter() };
		}
		
		return null;
	}

}
