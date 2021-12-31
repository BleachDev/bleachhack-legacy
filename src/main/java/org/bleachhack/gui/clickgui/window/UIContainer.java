package org.bleachhack.gui.clickgui.window;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;

public class UIContainer {

	public Map<String, UIWindow> windows = new LinkedHashMap<>();

	public UIContainer() {
	}

	public void render() {
		for (UIWindow w: windows.values()) {
			if (!w.shouldClose()) {
				w.renderUI();
			}
		}
	}

	public void updatePositions(int width, int height) {
		windows.forEach((id, window) -> {
			window.x1 = getLeft(id, width, height);
			window.y1 = getTop(id, width, height);
			window.x2 = getRight(id, width, height);
			window.y2 = getBottom(id, width, height);
		});
	}
	
	public String getIdFromWindow(UIWindow window) {
		return windows.entrySet().stream()
				.filter(p -> p.getValue() == window)
				.map(Map.Entry::getKey)
				.findFirst().orElse(null);
	}

	protected int getLeft(String id, int width, int height, String... passIds) {
		UIWindow window = windows.get(id);

		for (Entry<String, Integer> atm: window.position.getAttachments().entrySet()) {
			if (ArrayUtils.contains(passIds, id)) continue;
			if (atm.getKey().equals("l")) return 0;
			if (atm.getKey().equals("r")) return width - window.getSize()[0];
			if (atm.getKey().equals("c")) return width / 2 - window.getSize()[0] / 2;
			if (atm.getValue() == 1) return getRight(atm.getKey(), width, height, ArrayUtils.add(passIds, id));
			if (atm.getValue() == 3) return getLeft(atm.getKey(), width, height, ArrayUtils.add(passIds, id)) - window.getSize()[0];
		}

		return (int) (width * window.position.xPercent);
	}

	protected int getRight(String id, int width, int height, String... passIds) {
		UIWindow window = windows.get(id);

		for (Entry<String, Integer> atm: window.position.getAttachments().entrySet()) {
			if (ArrayUtils.contains(passIds, id)) continue;
			if (atm.getKey().equals("l")) return window.getSize()[0];
			if (atm.getKey().equals("r")) return width;
			if (atm.getKey().equals("c")) return width / 2 + window.getSize()[0] / 2;
			if (atm.getValue() == 1) return getRight(atm.getKey(), width, height, ArrayUtils.add(passIds, id)) + window.getSize()[0];
			if (atm.getValue() == 3) return getLeft(atm.getKey(), width, height, ArrayUtils.add(passIds, id));
		}

		return (int) (width * window.position.xPercent) + window.getSize()[0];
	}

	protected int getTop(String id, int width, int height, String... passIds) {
		UIWindow window = windows.get(id);

		for (Entry<String, Integer> atm: window.position.getAttachments().entrySet()) {
			if (ArrayUtils.contains(passIds, id)) continue;
			if (atm.getKey().equals("t")) return 0;
			if (atm.getKey().equals("b")) return getScreenBottom(height) - window.getSize()[1];
			if (atm.getValue() == 0) return getTop(atm.getKey(), width, height, ArrayUtils.add(passIds, id)) - window.getSize()[1];
			if (atm.getValue() == 2) return getBottom(atm.getKey(), width, height, ArrayUtils.add(passIds, id));
		}

		return (int) (height * window.position.yPercent);
	}

	protected int getBottom(String id, int width, int height, String... passIds) {
		UIWindow window = windows.get(id);

		for (Entry<String, Integer> atm: window.position.getAttachments().entrySet()) {
			if (ArrayUtils.contains(passIds, id)) continue;
			if (atm.getKey().equals("t")) return window.getSize()[1];
			if (atm.getKey().equals("b")) return getScreenBottom(height);
			if (atm.getValue() == 0) return getTop(atm.getKey(), width, height, ArrayUtils.add(passIds, id));
			if (atm.getValue() == 2) return getBottom(atm.getKey(), width, height, ArrayUtils.add(passIds, id)) + window.getSize()[1];
		}

		return (int) (height * window.position.yPercent) + window.getSize()[1];
	}

	public int getScreenBottom(int height) {
		return height - (MinecraftClient.getInstance().currentScreen instanceof ChatScreen ? 14 : 0);
	}
}
