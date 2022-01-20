/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.gui;

import net.minecraft.block.Block;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import org.bleachhack.gui.window.Window;
import org.bleachhack.gui.window.WindowScreen;
import org.bleachhack.gui.window.widget.WindowButtonWidget;
import org.bleachhack.module.mods.Notebot;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.NotebotUtils;
import org.bleachhack.util.NotebotUtils.Instrument;
import org.bleachhack.util.io.BleachFileMang;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NotebotScreen extends WindowScreen {

	public List<String> files;
	public NotebotEntry entry;
	public String selected = "";
	public int page = 0;

	@Override
	public void init() {
		super.init();

		files = new ArrayList<>();

		try {
			Stream<Path> paths = Files.walk(BleachFileMang.getDir().resolve("notebot"));
			paths.forEach(p -> files.add(p.getFileName().toString()));
			paths.close();
			files.remove(0);
		} catch (IOException ignored) {
		}

		clearWindows();
		addWindow(new Window(
				width / 4,
				height / 4 - 10,
				width / 4 + width / 2,
				height / 4 + height / 2,
				"Notebot Gui", new ItemStack(Block.NOTEBLOCK)));

		getWindow(0).addWidget(new WindowButtonWidget(22, 14, 32, 24, "<", () -> {
			if (page > 0)
				page--;
		}));

		getWindow(0).addWidget(new WindowButtonWidget(77, 14, 87, 24, ">", () -> page++));
	}

	@Override
	public void render(int mouseX, int mouseY, float delta) {
		renderBackground();
		super.render(mouseX, mouseY, delta);
	}

	@Override
	public void onRenderWindow(int window, int mouseX, int mouseY) {
		super.onRenderWindow(window, mouseX, mouseY);

		if (window == 0) {
			int x = getWindow(0).x1,
					y = getWindow(0).y1 + 10,
					w = width / 2,
					h = height / 2;

			int pageEntries = 0;
			for (int i = y + 20; i < y + h - 27; i += 10)
				pageEntries++;

			drawCenteredString(textRenderer, "Page " + (page + 1), x + 55, y + 5, 0xc0c0ff);

			fillButton(x + 10, y + h - 13, x + 99, y + h - 3, 0xff3a3a3a, 0xff353535, mouseX, mouseY);
			drawCenteredString(textRenderer, "Download Songs..", x + 55, y + h - 12, 0xc0dfdf);

			int c = 0, c1 = -1;
			for (String s : files) {
				c1++;
				if (c1 < page * pageEntries)
					continue;
				if (c1 > (page + 1) * pageEntries)
					break;

				fillButton(x + 5, y + 15 + c * 10, x + 105, y + 25 + c * 10,
						Notebot.filePath.equals(s) ? 0xf0408040 : selected.equals(s) ? 0xf0202020 : 0xf0404040, 0xf0303030, mouseX, mouseY);
				if (cutText(s, 105).equals(s)) {
					drawCenteredString(textRenderer, s, x + 55, y + 16 + c * 10, -1);
				} else {
					drawWithShadow(textRenderer, cutText(s, 105), x + 5, y + 16 + c * 10, -1);
				}

				c++;
			}

			if (entry != null) {
				drawCenteredString(textRenderer, entry.fileName, x + w - w / 4, y + 10, 0xa030a0);
				drawCenteredString(textRenderer, entry.length / 20 + "s", x + w - w / 4, y + 20, 0xc000c0);
				drawCenteredString(textRenderer, "Notes: ", x + w - w / 4, y + 38, 0x80f080);

				int c2 = 0;
				for (Entry<Instrument, Integer> e : entry.notes.entrySet()) {
					drawCenteredString(textRenderer, StringUtils.capitalize(e.getKey().toString()) + " x" + e.getValue(),
							x + w - w / 4, y + 50 + c2 * 10, 0x50f050);

					if (e.getKey() == Instrument.HARP)
						new ItemRenderer().method_5764(client.textRenderer, client.getTextureManager(), new ItemStack(Block.DIRT), x + w - w / 4 + 55, y + 46 + c2 * 10);
					else if (e.getKey() == Instrument.BASEDRUM)
						new ItemRenderer().method_5764(client.textRenderer, client.getTextureManager(), new ItemStack(Block.STONE_BLOCK), x + w - w / 4 + 55, y + 46 + c2 * 10);
					else if (e.getKey() == Instrument.SNARE)
						new ItemRenderer().method_5764(client.textRenderer, client.getTextureManager(), new ItemStack(Block.SAND_BLOCK), x + w - w / 4 + 55, y + 46 + c2 * 10);
					else if (e.getKey() == Instrument.HAT)
						new ItemRenderer().method_5764(client.textRenderer, client.getTextureManager(), new ItemStack(Block.GLASS_BLOCK), x + w - w / 4 + 55, y + 46 + c2 * 10);
					else if (e.getKey() == Instrument.BASS)
						new ItemRenderer().method_5764(client.textRenderer, client.getTextureManager(), new ItemStack(Block.PLANKS), x + w - w / 4 + 55, y + 46 + c2 * 10);
					c2++;

					GuiLighting.disable();
				}

				fillButton(x + w - w / 2 + 10, y + h - 15, x + w - w / 4, y + h - 5, 0xff903030, 0xff802020, mouseX, mouseY);
				fillButton(x + w - w / 4 + 5, y + h - 15, x + w - 5, y + h - 5, 0xff308030, 0xff207020, mouseX, mouseY);
				fillButton(x + w - w / 4 - w / 8, y + h - 27, x + w - w / 4 + w / 8, y + h - 17, 0xff303080, 0xff202070, mouseX, mouseY);

				int pixels = (int) Math.round(Math.min(Math.max((w / 4d) * ((double) entry.playTick / (double) entry.length), 0), w / 4d));
				fill(x + w - w / 4 - w / 8, y + h - 27, (x + w - w / 4 - w / 8) + pixels, y + h - 17, 0x507050ff);

				drawCenteredString(textRenderer, "Delete", (int) (x + w - w / 2.8), y + h - 14, 0xff0000);
				drawCenteredString(textRenderer, "Select", x + w - w / 8, y + h - 14, 0x00ff00);
				drawCenteredString(textRenderer, (entry.playing ? "Playing" : "Play") + " (scuffed)", x + w - w / 4, y + h - 26, 0x6060ff);
			}
		}
	}

	@Override
	public void tick() {
		if (entry != null) {
			if (entry.playing) {
				entry.playTick++;
				NotebotUtils.playNote(entry.lines, entry.playTick);
			}
		}
	}

	@Override
	public boolean shouldPauseGame() {
		return false;
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if (!getWindow(0).closed) {
			int x = getWindow(0).x1,
					y = getWindow(0).y1 + 10,
					w = width / 2,
					h = height / 2;

			if (mouseX > x + 10 && mouseX < x + 99 && mouseY > y + h - 13 && mouseY < y + h - 3) {
				NotebotUtils.downloadSongs(true);
				init();
			}

			if (entry != null) {
				/* Pfft why use buttons when you can use meaningless rectangles with messy code */
				if (mouseX > x + w - w / 2 + 10 && mouseX < x + w - w / 4 && mouseY > y + h - 15 && mouseY < y + h - 5) {
					BleachFileMang.deleteFile("notebot/" + entry.fileName);
					client.openScreen(this);
				}
				if (mouseX > x + w - w / 4 + 5 && mouseX < x + w - 5 && mouseY > y + h - 15 && mouseY < y + h - 5) {
					Notebot.filePath = entry.fileName;
				}
				if (mouseX > x + w - w / 4 - w / 8 && mouseX < x + w - w / 4 + w / 8 && mouseY > y + h - 27 && mouseY < y + h - 17) {
					entry.playing = !entry.playing;
				}
			}

			int pageEntries = 0;
			for (int i = y + 20; i < y + h - 27; i += 10)
				pageEntries++;

			int c = 0;
			int c1 = -1;
			for (String s : files) {
				c1++;
				if (c1 < page * pageEntries)
					continue;
				if (mouseX > x + 5 && mouseX < x + 105 && mouseY > y + 15 + c * 10 && mouseY < y + 25 + c * 10) {
					entry = new NotebotEntry(s);
					selected = s;
				}
				c++;
			}
		}

		super.mouseClicked(mouseX, mouseY, button);
	}

	private void fillButton(int x1, int y1, int x2, int y2, int color, int colorHover, int mouseX, int mouseY) {
		fill(x1, y1, x2, y2, (mouseX > x1 && mouseX < x2 && mouseY > y1 && mouseY < y2 ? colorHover : color));
	}

	private String cutText(String text, int leng) {
		String text1 = text;
		for (int i = 0; i < text.length(); i++) {
			if (textRenderer.getStringWidth(text1) < leng)
				return text1;
			text1 = text1.replaceAll(".$", "");
		}
		return "";
	}

	public static class NotebotEntry {
		public String fileName;
		public List<String> lines = new ArrayList<>();
		public Map<Instrument, Integer> notes = new HashMap<>();
		public int length;

		public boolean playing = false;
		public int playTick = 0;

		public NotebotEntry(String file) {
			/* File and lines */
			fileName = file;
			lines = BleachFileMang.readFileLines("notebot/" + file)
					.stream().filter(s -> {
						try {
							if (Integer.parseInt(s.split(":")[2]) > 4)
								return false;
						} catch (Exception e) {
							BleachLogger.warn("Error trying to parse tune: \u00a7o" + s);
						}
	
						return !(s.isEmpty() || s.startsWith("//") || s.startsWith(";"));
						}).collect(Collectors.toList());

			/* Get length */
			int maxLeng = 0;
			for (String s : lines) {
				try {
					if (Integer.parseInt(s.split(":")[0]) > maxLeng)
						maxLeng = Integer.parseInt(s.split(":")[0]);
				} catch (Exception ignored) {
				}
			}
			length = maxLeng;

			/* Requirements */
			List<int[]> tunes = new ArrayList<>();

			for (String s : lines) {
				try {
					List<String> strings = Arrays.asList(s.split(":"));
					int[] tune = new int[] { Integer.parseInt(strings.get(1)), Integer.parseInt(strings.get(2)) };
					if (tune[1] <= 4 && tunes.stream().noneMatch(i -> i[0] == tune[0] && i[1] == tune[1])) {
						tunes.add(tune);
					}
				} catch (Exception e) {
					BleachLogger.warn("Error trying to parse tune: \u00a7o" + s);
				}
			}

			int[] instruments = new int[] { 0, 0, 0, 0, 0 };

			for (int[] i : tunes)
				instruments[i[1]] = instruments[i[1]] + 1;

			for (int i = 0; i < instruments.length; i++) {
				if (instruments[i] != 0)
					notes.put(Instrument.values()[i], instruments[i]);
			}
		}
	}
}
