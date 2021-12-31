/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.gui;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bleachhack.gui.window.Window;
import org.bleachhack.gui.window.WindowScreen;
import org.bleachhack.mixin.AccessorTextFieldWidget;
import org.bleachhack.util.collections.MutablePairList;
import org.bleachhack.util.io.BleachFileHelper;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class EntityMenuEditScreen extends WindowScreen {

	private MutablePairList<String, String> interactions;
	private String selectedEntry;
	private String hoverEntry;
	private String deleteEntry;

	private int scroll;
	private int scrollOffset;

	private boolean addEntry;

	private String insertString;
	private String insertStartString;

	private TextFieldWidget editNameField;
	private TextFieldWidget editValueField;

	public EntityMenuEditScreen(MutablePairList<String, String> interactions) {
		this.interactions = interactions;
	}

	@Override
	public void init() {
		super.init();

		clearWindows();
		addWindow(new Window(
				width / 4,
				height / 6,
				width - width / 4,
				height - height / 6,
				"Edit Interactions", new ItemStack(Item.field_4282)));

		if (editNameField == null) {
			editNameField = new TextFieldWidget(textRenderer, 0, 0, 1000, 16);
		}

		if (editValueField == null) {
			editValueField = new TextFieldWidget(textRenderer, 0, 0, 1000, 16);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		renderBackground();
		super.render(mouseX, mouseY, delta);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onRenderWindow(int window, int mouseX, int mouseY) {
		super.onRenderWindow(window, mouseX, mouseY);

		if (window == 0) {
			int x = getWindow(0).x1;
			int y = getWindow(0).y1 + 12;
			int w = getWindow(0).x2 - getWindow(0).x1;
			int h = getWindow(0).y2 - getWindow(0).y1 - 13;

			hoverEntry = null;
			deleteEntry = null;
			scrollOffset = 0;
			addEntry = false;
			insertString = null;
			insertStartString = null;

			int seperator = (int) (x + w / 3.25);
			fill(seperator, y, seperator + 1, y + h, 0xff606090);

			textRenderer.method_956("Interactions:", x + 6, y + 5, 0xffffff);

			boolean mouseOverAdd = mouseX >= seperator - 16 && mouseX <= seperator - 3 && mouseY >= y + 3 && mouseY <= y + 15;
			Window.fill(seperator - 16, y + 3, seperator - 3, y + 15,
					mouseOverAdd ? 0x4fb070f0 : 0x60606090);
			textRenderer.method_956("\u00a7a+", seperator - 12, y + 5, 0xffffff);

			if (mouseOverAdd) {
				addEntry = true;
			}

			int maxEntries = (h - 33) / 17;
			int entries = 0;

			scroll = MathHelper.clamp(scroll, 0, interactions.size() - maxEntries);

			if (scroll > 0) {
				boolean mouseOver = mouseX >= x + 2 && mouseX <= seperator - 1 && mouseY >= y + 17 && mouseY <= y + 33;

				Window.fill(x + 3, y + 17, seperator - 2, y + 33,
						mouseOver ? 0x4fb070f0 : 0x50606090);
				drawCenteredString(textRenderer,
						"\u00a7a\u00a7l^", x + (seperator - x) / 2, y + 21, 0xffffff);

				entries++;
				if (mouseOver) {
					scrollOffset = -1;
				}
			}

			if (interactions.size() - maxEntries > 0 && scroll < interactions.size() - maxEntries) {
				boolean mouseOver = mouseX >= x + 2 && mouseX <= seperator - 1 && mouseY >= y + 17 + (maxEntries * 17) && mouseY <= y + 33 + (maxEntries * 17);

				Window.fill(x + 3, y + 17 + (maxEntries * 17), seperator - 2, y + 33 + (maxEntries * 17),
						mouseOver ? 0x4fb070f0 : 0x50606090);
				drawCenteredString(textRenderer,
						"\u00a7a\u00a7lv", x + (seperator - x) / 2, y + 21 + (maxEntries * 17), 0xffffff);

				maxEntries--;
				if (mouseOver) {
					scrollOffset = 1;
				}
			}

			int localScroll = scroll;
			for (String entry: interactions.getEntries()) {
				if (entries < localScroll) {
					localScroll--;
					continue;
				}

				int curY = y + 17 + entries * 17;
				boolean mouseOver = mouseX >= x + 2 && mouseX <= seperator - 1 && mouseY >= curY && mouseY <= curY + 16;

				Window.fill(x + 3, curY, seperator - 2, curY + 16,
						entry.equals(selectedEntry) ? 0x4f90f090 : mouseOver ? 0x4fb070f0 : 0x50606090);
				drawCenteredString(textRenderer,
						textRenderer.trimToWidth(entry, seperator - x - 6), x + (seperator - x) / 2, curY + 4, 0xffffff);

				if (mouseOver) {
					hoverEntry = entry;
				}

				entries++;
				if (entries > maxEntries) {
					break;
				}
			}

			if (selectedEntry != null) {
				textRenderer.method_956("Name:", seperator + 8, y + 5, 0xffffff);

				((AccessorTextFieldWidget) editNameField).setX(seperator + 8);
				((AccessorTextFieldWidget) editNameField).setY(y + 18);
				((AccessorTextFieldWidget) editNameField).setWidth(w - (seperator - x) - 16);
				editNameField.render();

				textRenderer.method_956("Value:", seperator + 8, y + 45, 0xffffff);

				((AccessorTextFieldWidget) editValueField).setX(seperator + 8);
				((AccessorTextFieldWidget) editValueField).setY(y + 57);
				((AccessorTextFieldWidget) editValueField).setWidth(w - (seperator - x) - 16);
				editValueField.render();

				if (!selectedEntry.equals(editNameField.getText()) && !interactions.containsKey(editNameField.getText())) {
					MutablePair<String, String> pair = interactions.getPair(selectedEntry);
					selectedEntry = editNameField.getText();
					pair.setLeft(selectedEntry);
				}

				if (!interactions.getValue(selectedEntry).equals(editValueField.getText())) {
					interactions.getPair(selectedEntry).setRight(editValueField.getText());
				}

				textRenderer.method_956("Insert:", seperator + 8, y + 85, 0xffffff);

				int line = 0;
				int curX = 0;
				for (String insert: new String[] { "%name%", "%uuid%", "%health%", "%x%", "%y%", "%z%"}) {
					int textLen = textRenderer.getStringWidth(insert);

					if (seperator + 9 + curX + textLen > x + w) {
						line++;
						curX = 0;
					}

					boolean mouseOverInsert = mouseX >= seperator + 7 + curX && mouseX <= seperator + 10 + curX + textLen && mouseY >= y + 97 + line * 14 && mouseY <= y + 108 + line * 14;
					fill(seperator + 7 + curX, y + 97 + line * 14, seperator + 10 + curX + textLen, y + 108 + line * 14, mouseOverInsert ? 0x9f6060b0 : 0x9f8070b0);
					textRenderer.method_956(insert, seperator + 9 + curX, y + 99 + line * 14, 0xffffff);

					if (mouseOverInsert) {
						insertString = insert;
					}

					curX += textLen + 7;
				}

				textRenderer.method_956("Mode:", seperator + 8, y + 120 + line * 14, 0xffffff);

				int startY = y + 132 + line * 14;
				line = 0;
				curX = 0;
				for (Pair<String, String> pair: new Pair[] { Pair.of("Normal", ""), Pair.of("Suggest", ">suggest "), Pair.of("Open Url", ">url ") }) {
					int textLen = textRenderer.getStringWidth(pair.getLeft());

					if (seperator + 9 + curX + textLen > x + w) {
						line++;
						curX = 0;
					}

					boolean mouseOverInsert = mouseX >= seperator + 7 + curX && mouseX <= seperator + 10 + curX + textLen && mouseY >= startY + line * 14 && mouseY <= startY + 11 + line * 14;
					fill(seperator + 7 + curX, startY + line * 14, seperator + 10 + curX + textLen, startY + 11 + line * 14, mouseOverInsert ? 0x9f6060b0 : 0x9f8070b0);
					textRenderer.method_956(pair.getLeft(), seperator + 9 + curX, startY + 2 + line * 14, 0xffffff);

					if (mouseOverInsert) {
						insertStartString = pair.getRight();
					}

					curX += textLen + 7;
				}

				boolean mouseOverDelete = mouseX >= x + w - 70 && mouseX <= x + w - 5 && mouseY >= y + h - 22 && mouseY <= y + h - 4;
				Window.fill(x + w - 70, y + h - 22, x + w - 5, y + h - 4, 0x60e05050, 0x60c07070, mouseOverDelete ? 0x20e05050 : 0x10e07070);
				drawCenteredString(textRenderer, "Delete", x + w - 37, y + h - 17, 0xf0f0f0);

				if (mouseOverDelete) {
					deleteEntry = selectedEntry;
				}
			}
		}
	}

	@Override
	public void keyPressed(char character, int code) {
		editNameField.keyPressed(character, code);
		editValueField.keyPressed(character, code);
		
		if (code == 1) {
			JsonObject json = new JsonObject();
			for (MutablePair<String, String> entry: interactions) {
				json.add(entry.getLeft(), new JsonPrimitive(entry.getRight()));
			}

			BleachFileHelper.saveMiscSetting("entityMenu", json);
		}

		super.keyPressed(character, code);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		editNameField.mouseClicked(mouseX, mouseY, button);
		editValueField.mouseClicked(mouseX, mouseY, button);

		if (hoverEntry != null && interactions.containsKey(hoverEntry)) {
			selectedEntry = hoverEntry;
			hoverEntry = null;

			editNameField.setText(selectedEntry);
			editValueField.setText(interactions.getValue(selectedEntry));
		}

		if (deleteEntry != null) {
			interactions.removeKey(deleteEntry);
			deleteEntry = null;
			selectedEntry = null;
		}

		if (scrollOffset != 0) {
			scroll += scrollOffset;
			scrollOffset = 0;
		}

		if (addEntry) {
			String name = "New Interaction";
			for (int toAdd = 1; interactions.containsKey(name); toAdd++) {
				name = "New Interaction (" + toAdd + ")";
			}

			interactions.add(name, "Hi %name%");
			addEntry = false;
		}

		if (insertString != null) {
			editValueField.write(insertString);
			insertString = null;
		}

		if (insertStartString != null) {
			if (editValueField.getText().startsWith(">")) {
				editValueField.setText(editValueField.getText().replaceFirst(">.*? ", ""));
			}

			editValueField.setText(insertStartString + editValueField.getText());
			insertString = null;
		}

		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void tick() {
		editNameField.tick();
		editValueField.tick();
	}

	@Override
	public boolean shouldPauseGame() {
		return false;
	}
}
