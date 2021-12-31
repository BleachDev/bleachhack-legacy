/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.setting.module;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bleachhack.setting.SettingDataHandlers;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;

public class SettingBlockList extends SettingList<Block> {

	public SettingBlockList(String text, String windowText, Block... defaultBlocks) {
		this(text, windowText, null, defaultBlocks);
	}

	public SettingBlockList(String text, String windowText, Predicate<Block> filter, Block... defaultBlocks) {
		super(text, windowText, SettingDataHandlers.BLOCK, getAllBlocks(filter), defaultBlocks);
	}

	private static Collection<Block> getAllBlocks(Predicate<Block> filter) {
		List<Block> items = Arrays.asList(Block.field_492).stream().filter(i -> i != null).collect(Collectors.toList());
		return filter == null ? items : items.stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void renderItem(MinecraftClient mc, Block item, int x, int y, int w, int h) {
		if (item == null) {
			super.renderItem(mc, item, x, y, w, h);
		} else {
			new ItemRenderer().method_5762(MinecraftClient.getInstance().textRenderer, MinecraftClient.getInstance().getTextureManager(), new ItemStack(item), x + 1, y + 1);
		}
	}

	@Override
	public String getName(Block item) {
		return new ItemStack(item).getName();
	}
}
