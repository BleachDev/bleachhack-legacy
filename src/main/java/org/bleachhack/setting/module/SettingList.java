/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.setting.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.bleachhack.gui.clickgui.window.ModuleWindow;
import org.bleachhack.gui.window.Window;
import org.bleachhack.gui.window.WindowScreen;
import org.bleachhack.gui.window.widget.WindowButtonWidget;
import org.bleachhack.gui.window.widget.WindowScrollbarWidget;
import org.bleachhack.gui.window.widget.WindowTextFieldWidget;
import org.bleachhack.setting.SettingDataHandler;
import org.bleachhack.setting.SettingDataHandlers;
import org.bleachhack.util.io.BleachFileHelper;

import java.util.*;

public abstract class SettingList<T> extends ModuleSetting<LinkedHashSet<T>> {

	protected String windowText;
	protected Set<T> itemPool;

	@SuppressWarnings("unchecked")
	public SettingList(String text, String windowText, SettingDataHandler<T> itemHandler, Collection<T> itemPool, T... defaultItems) {
		super(text, new LinkedHashSet<>(Arrays.asList(defaultItems)), v -> (LinkedHashSet<T>) v.clone(), SettingDataHandlers.ofCollection(itemHandler, LinkedHashSet::new));
		this.windowText = windowText;
		this.itemPool = new LinkedHashSet<>(itemPool);
	}

	public void render(ModuleWindow window, int x, int y, int len) {
		if (window.mouseOver(x, y, x + len, y + 12)) {
			DrawableHelper.fill(x + 1, y, x + len, y + 12, 0x70303070);
		}

		MinecraftClient.getInstance().textRenderer.method_956(getName(), x + 3, y + 2, 0xcfe0cf);

		MinecraftClient.getInstance().textRenderer.method_956("...", x + len - 7, y + 2, 0xcfd0cf);

		if (window.mouseOver(x, y, x + len, y + 12) && window.lmDown) {
			window.mouseReleased(window.mouseX, window.mouseY, 1);
			//MinecraftClient.getInstance().currentScreen.mouseReleased(window.mouseX, window.mouseY, 0);
			MinecraftClient.getInstance().openScreen(new ListWidowScreen(MinecraftClient.getInstance().currentScreen));
		}
	}

	public boolean contains(T item) {
		return getValue().contains(item);
	}

	public void renderItem(MinecraftClient mc, T item, int x, int y, int w, int h) {
		mc.textRenderer.method_956("?", x + 5, y + 4, -1);
	}

	/**
	 * The human readable name for this item, the internal name is used for read/writing.
	 */
	public abstract String getName(T item);

	public SettingList<T> withDesc(String desc) {
		setTooltip(desc);
		return this;
	}

	public int getHeight(int len) {
		return 12;
	}

	private class ListWidowScreen extends WindowScreen {

		private Screen parent;
		private WindowTextFieldWidget inputField;
		private WindowScrollbarWidget scrollbar;

		private T toDeleteItem;
		private T toAddItem;

		public ListWidowScreen(Screen parent) {
			this.parent = parent;
		}

		@Override
		public void init() {
			super.init();

			clearWindows();

			addWindow(new Window(
					(int) (width / 3.25),
					height / 12,
					(int) (width - width / 3.25),
					height - height / 12,
					windowText, new ItemStack(Item.field_4282)));

			int x2 = getWindow(0).x2 - getWindow(0).x1;
			int y2 = getWindow(0).y2 - getWindow(0).y1;

			getWindow(0).addWidget(new WindowButtonWidget(x2 - 50, y2 - 22, x2 - 5, y2 - 5, "Reset", () -> {
				getValue().clear();
				getValue().addAll(defaultValue);
				BleachFileHelper.SCHEDULE_SAVE_MODULES.set(true);
			}));

			getWindow(0).addWidget(new WindowButtonWidget(x2 - 100, y2 - 22, x2 - 55, y2 - 5, "Clear", () -> {
				getValue().clear();
				BleachFileHelper.SCHEDULE_SAVE_MODULES.set(true);
			}));

			getWindow(0).addWidget(new WindowButtonWidget(x2 - 150, y2 - 22, x2 - 105, y2 - 5, "Add All", () -> {
				getValue().clear();
				getValue().addAll(itemPool);
				BleachFileHelper.SCHEDULE_SAVE_MODULES.set(true);
			}));

			inputField = getWindow(0).addWidget(new WindowTextFieldWidget(5, y2 - 22, x2 / 3, 17, inputField != null ? inputField.textField.getText() : ""));

			scrollbar = getWindow(0).addWidget(new WindowScrollbarWidget(x2 - 11, 12, 0, y2 - 39, scrollbar == null ? 0 : scrollbar.getPageOffset()));
		}

		@Override
		public void render(int mouseX, int mouseY, float delta) {
			renderBackground();
			super.render(mouseX, mouseY, delta);
		}

		@Override
		public void onRenderWindow(int window, int mouseX, int mouseY) {
			super.onRenderWindow(window, mouseX, mouseY);

			toAddItem = null;
			toDeleteItem = null;

			if (window == 0) {
				int x1 = getWindow(0).x1;
				int y1 = getWindow(0).y1;
				int x2 = getWindow(0).x2;
				int y2 = getWindow(0).y2;

				int maxEntries = Math.max(1, (y2 - y1) / 21 - 1);
				int renderEntries = 0;
				int entries = 0;

				scrollbar.setTotalHeight(getValue().size() * 21);
				int offset = scrollbar.getPageOffset();

				for (T e: getValue()) {
					if (entries >= offset / 21 && renderEntries < maxEntries) {
						drawEntry(e, x1 + 6, y1 + 15 + entries * 21 - offset, x2 - x1 - 19, 20, mouseX, mouseY);
						renderEntries++;
					}

					entries++;
				}

				//Window.horizontalGradient(matrix, x1 + 1, y2 - 25, x2 - 1, y2 - 1, 0x70606090, 0x00606090);
				Window.horizontalGradient(x1 + 1, y2 - 27, x2 - 1, y2 - 26, 0xff606090, 0x50606090);

				if (inputField.textField.isFocused()) {
					Set<T> toDraw = new LinkedHashSet<>();

					for (T e: itemPool) {
						if (toDraw.size() >= 10)
							break;

						if (!getValue().contains(e) && getName(e).toLowerCase(Locale.ENGLISH).contains(inputField.textField.getText().toLowerCase(Locale.ENGLISH))) {
							toDraw.add(e);
						}
					}

					int curY = y1 + inputField.y1 - 4 - toDraw.size() * 17;
					int longest = toDraw.stream().mapToInt(e -> textRenderer.getStringWidth(getName(e))).max().orElse(0);

					for (T e: toDraw) {
						drawSearchEntry(e, x1 + inputField.x1, curY, longest + 23, 16, mouseX, mouseY);
						curY += 17;
					}
				}
			}
		}

		private void drawEntry(T item, int x, int y, int width, int height, int mouseX, int mouseY) {
			boolean mouseOverDelete = mouseX >= x + width - 14 && mouseX <= x + width - 1 && mouseY >= y + 2 && mouseY <= y + height - 2;
			Window.fill(x + width - 14, y + 2, x + width - 1, y + height - 2, mouseOverDelete ? 0x4fb070f0 : 0x60606090);

			if (mouseOverDelete) {
				toDeleteItem = item;
			}

			renderItem(client, item, x, y, height, height);

			drawWithShadow(textRenderer, getName(item), x + height + 4, y + 4, -1);
			drawWithShadow(textRenderer, "\u00a7cx", x + width - 10, y + 5, -1);
		}

		private void drawSearchEntry(T item, int x, int y, int width, int height, int mouseX, int mouseY) {
			boolean mouseOver = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
			DrawableHelper.fill(x, y - 1, x + width, y + height, mouseOver ? 0xdf8070d0 : 0xb0606090);

			if (mouseOver) {
				toAddItem = item;
			}

			renderItem(client, item, x, y, height, height);

			drawWithShadow(textRenderer, getName(item), x + height + 4, y + 4, -1);
		}

		@Override
		public void keyPressed(char character, int code) {
			if (code == 1) {
				this.client.openScreen(parent);
			} else {
				super.keyPressed(character, code);
			}
		}

		@Override
		public boolean shouldPauseGame() {
			return false;
		}

		@Override
		public void mouseClicked(int mouseX, int mouseY, int button) {
			if (toAddItem != null) {
				getValue().add(toAddItem);
				inputField.textField.setFocused(true);
				BleachFileHelper.SCHEDULE_SAVE_MODULES.set(true);
				return;
			} else if (toDeleteItem != null) {
				getValue().remove(toDeleteItem);
				BleachFileHelper.SCHEDULE_SAVE_MODULES.set(true);
			}

			super.mouseClicked(mouseX, mouseY, button);
		}
	}
}
