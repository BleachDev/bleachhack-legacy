/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.entity.NoteBlockBlockEntity;
import net.minecraft.util.math.Direction;

import org.bleachhack.command.Command;
import org.bleachhack.event.events.EventTick;
import org.bleachhack.event.events.EventWorldRender;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingMode;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.BlockPos;
import org.bleachhack.util.NotebotUtils.Instrument;
import org.bleachhack.util.io.BleachFileMang;
import org.bleachhack.util.render.Renderer;
import org.bleachhack.util.render.color.QuadColor;
import org.bleachhack.util.world.WorldUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Notebot extends Module {

	/* All the lines of the file [tick:pitch:instrument] */
	private List<int[]> notes = new ArrayList<>();

	/* All unique instruments and pitches [pitch:instrument] */
	private List<int[]> tunes = new ArrayList<>();

	/* Map of noteblocks to hit when playing and the pitch of each [blockpos:pitch] */
	private Map<BlockPos, Integer> blockTunes = new HashMap<>();
	private int timer = -10;
	private int tuneDelay = 0;

	private int tick;

	public static String filePath = "";

	public Notebot() {
		super("Notebot", KEY_UNBOUND, ModuleCategory.MISC, "Plays those noteblocks nicely.",
				new SettingToggle("Tune", true).withDesc("Tunes the noteblocks before and while playing.").withChildren(
						new SettingMode("Tune", "Normal", "Wait-1", "Wait-2", "Batch-5", "All").withDesc("How to tune the noteblocks.")),
				new SettingToggle("Loop", false).withDesc("Loop the song you're playing."),
				new SettingToggle("NoInstruments", false).withDesc("Ignores instruments."),
				new SettingToggle("AutoPlay", false).withDesc("Auto plays a random song after one is finished."));
	}

	@Override
	public void onEnable(boolean inWorld) {
		if (!inWorld)
			return;

		super.onEnable(inWorld);
		blockTunes.clear();

		if (!mc.interactionManager.hasStatusBars()) {
			BleachLogger.error("Not In Survival Mode!");
			setEnabled(false);
			return;
		} else if (filePath.isEmpty()) {
			BleachLogger.error("No Song Loaded!, Use " + Command.getPrefix() + "notebot to select a song.");
			setEnabled(false);
			return;
		} else {
			readFile(filePath);
		}

		timer = -10;

		List<BlockPos> noteblocks = BlockPos.streamOutwards(new BlockPos(mc.field_3805).up(), 4, 4, 4)
				.filter(this::isNoteblock)
				.collect(Collectors.toList());

		for (int[] i : tunes) {
			for (BlockPos pos: noteblocks) {
				if (blockTunes.containsKey(pos))
					continue;

				if (getSetting(2).asToggle().getState()) {
					if (!blockTunes.containsValue(i[0])) {
						blockTunes.put(pos, i[0]); 
						break;
					}
				} else {
					int instrument = getInstrument(pos).ordinal();
					if (i[1] == instrument
							&& blockTunes.entrySet().stream()
							.filter(e -> e.getValue() == i[0])
							.noneMatch(e -> getInstrument(e.getKey()).ordinal() == instrument)) {
						blockTunes.put(pos, i[0]);
						break;
					}
				}
			}
		}

		int totalTunes = getSetting(2).asToggle().getState() ? (int) tunes.stream().map(i -> i[0]).distinct().count() : tunes.size();
		if (totalTunes > blockTunes.size()) {
			BleachLogger.warn("Mapping Error: Missing " + (totalTunes - blockTunes.size()) + " Noteblocks");
		}
	}

	@BleachSubscribe
	public void onRender(EventWorldRender.Post event) {
		for (Entry<BlockPos, Integer> e : blockTunes.entrySet()) {
			if (getNote(e.getKey()) != e.getValue()) {
				Renderer.drawBoxBoth(e.getKey(), QuadColor.single(1F, 0F, 0F, 0.4F), 2.5f);
			} else {
				Renderer.drawBoxBoth(e.getKey(), QuadColor.single(0F, 1F, 0F, 0.4F), 2.5f);
			}
		}
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		tick++;

		/* Tune Noteblocks */
		int tuneMode = getSetting(0).asToggle().getChild(0).asMode().getMode();

		if (getSetting(0).asToggle().getState()) {
			for (Entry<BlockPos, Integer> e : blockTunes.entrySet()) {
				int note = getNote(e.getKey());
				if (note == -1)
					continue;

				if (note != e.getValue()) {
					if (tuneMode <= 2) {
						if (tuneMode >= 1) {
							if (tick % 2 == 0 || (tick % 3 == 0 && tuneMode == 2))
								return;
						}

						WorldUtils.rightClick(e.getKey(), Direction.UP);
					} else if (tuneMode >= 3) {
						if (tuneDelay < (tuneMode == 3 ? 3 : 5)) {
							tuneDelay++;
							return;
						}

						int neededNote = e.getValue() < note ? e.getValue() + 25 : e.getValue();
						int reqTunes = Math.min(tuneMode == 3 ? 5 : 25, neededNote - note);
						for (int i = 0; i < reqTunes; i++)
							WorldUtils.rightClick(e.getKey(), Direction.UP);

						tuneDelay = 0;
					}

					return;
				}
			}
		}

		/* Loop */
		boolean loopityloop = true;
		for (int[] n : notes) {
			if (timer - 10 < n[0]) {
				loopityloop = false;
				break;
			}
		}

		if (loopityloop) {
			if (getSetting(3).asToggle().getState()) {
				try {
					List<String> files = new ArrayList<>();
					Stream<Path> paths = Files.walk(BleachFileMang.getDir().resolve("notebot"));
					paths.forEach(p -> files.add(p.getFileName().toString()));
					paths.close();
					filePath = files.get(new Random().nextInt(files.size() - 1) + 1);
					setEnabled(false);
					setEnabled(true);
					BleachLogger.info("Now Playing: \u00a7a" + filePath);
				} catch (IOException ignored) {
				}
			} else if (getSetting(1).asToggle().getState()) {
				timer = -10;
			}
		}

		/* Play Noteblocks */
		timer++;

		List<int[]> curNotes = new ArrayList<>();
		for (int[] i : notes)
			if (i[0] == timer)
				curNotes.add(i);
		if (curNotes.isEmpty())
			return;

		for (Entry<BlockPos, Integer> e : blockTunes.entrySet()) {
			for (int[] i : curNotes) {
				if (isNoteblock(e.getKey()) && (i[1] == (getNote(e.getKey()))
						&& (getSetting(2).asToggle().getState()
								|| i[2] == (getInstrument(e.getKey()).ordinal()))))
					playBlock(e.getKey());
			}
		}
	}

	public Instrument getInstrument(BlockPos pos) {
		if (!isNoteblock(pos))
			return Instrument.HARP;

		Material var5 = mc.world.method_3776(pos.getX(), pos.getY() - 1, pos.getZ());
		if (var5 == Material.STONE) {
			return Instrument.BASS;
		}

		if (var5 == Material.NOTEBLOCK) {
			return Instrument.SNARE;
		}

		if (var5 == Material.GLASS) {
			return Instrument.HAT;
		}

		if (var5 == Material.WOOD) {
			return Instrument.BASEDRUM;
		}

		return Instrument.HARP;
	}

	public int getNote(BlockPos pos) {
		if (!isNoteblock(pos))
			return -1;

		return ((NoteBlockBlockEntity) mc.world.method_3781(pos.getX(), pos.getY(), pos.getZ())).field_558;
	}

	public boolean isNoteblock(BlockPos pos) {
		/* Checks if this block is a noteblock and the noteblock can be played */
		return Block.field_492[mc.world.method_3774(pos.getX(), pos.getY(), pos.getZ())] instanceof NoteBlock
				&& Block.field_492[mc.world.method_3774(pos.getX(), pos.getY() + 1, pos.getZ())] == null;
	}

	public void playBlock(BlockPos pos) {
		if (!isNoteblock(pos))
			return;

		mc.field_3805.swingHand();
		//mc.interactionManager.method_1235(pos.getX(), pos.getY(), pos.getZ(), 0);
	}

	public void readFile(String fileName) {
		tunes.clear();
		notes.clear();

		/* Read the file */
		BleachFileMang.createFile("notebot/" + fileName);
		List<String> lines = BleachFileMang.readFileLines("notebot/" + fileName)
				.stream().filter(s -> !(s.isEmpty() || s.startsWith("//") || s.startsWith(";"))).collect(Collectors.toList());
		for (String s : lines)
			s = s.replaceAll(" ", "");

		/* Parse note info into "memory" */
		for (String s : lines) {
			String[] s1 = s.split(":");
			try {
				notes.add(new int[] { Integer.parseInt(s1[0]), Integer.parseInt(s1[1]), Integer.parseInt(s1[2]) });
			} catch (Exception e) {
				BleachLogger.warn("Error Parsing Note: \u00a7o" + s);
			}
		}

		/* Get all unique pitches and instruments */
		for (String s : lines) {
			try {
				List<String> strings = Arrays.asList(s.split(":"));
				int[] tune = new int[] { Integer.parseInt(strings.get(1)), Integer.parseInt(strings.get(2)) };
				if (tunes.stream().noneMatch(i -> i[0] == tune[0] && i[1] == tune[1])) {
					tunes.add(tune);
				}
			} catch (Exception e) {
				BleachLogger.warn("Error trying to parse tune: \u00a7o" + s);
			}
		}
	}

}
