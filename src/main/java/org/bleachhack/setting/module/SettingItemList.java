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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SettingItemList extends SettingList<Item> {

	public SettingItemList(String text, String windowText, Item... defaultItems) {
		this(text, windowText, null, defaultItems);
	}

	public SettingItemList(String text, String windowText, Predicate<Item> filter, Item... defaultItems) {
		super(text, windowText, SettingDataHandlers.ITEM, getAllItems(filter), defaultItems);
	}

	private static Collection<Item> getAllItems(Predicate<Item> filter) {
		List<Item> items = Arrays.asList(Item.field_4343).stream().filter(i -> i != null).collect(Collectors.toList());
		return filter == null ? items : items.stream().filter(filter).collect(Collectors.toList());
	}

	@Override
	public void renderItem(MinecraftClient mc, Item item, int x, int y, int w, int h) {
		if (item == null) {
			super.renderItem(mc, item, x, y, w, h);
		} else {
			new ItemRenderer().method_5762(MinecraftClient.getInstance().textRenderer, MinecraftClient.getInstance().getTextureManager(), new ItemStack(item), x + 1, y + 1);
		}
	}

	@Override
	public String getName(Item item) {
		return new ItemStack(item).getName();
	}
}
