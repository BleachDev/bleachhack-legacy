/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum ModuleCategory {
	PLAYER(new ItemStack(Item.field_4344)),
	RENDER(new ItemStack(Item.field_4345)),
	COMBAT(new ItemStack(Item.field_4346)),
	MOVEMENT(new ItemStack(Item.field_4347)),
	EXPLOITS(new ItemStack(Item.field_4348)),
	MISC(new ItemStack(Item.field_4350)),
	WORLD(new ItemStack(Item.field_4351));
	
	private final ItemStack item;
	
	ModuleCategory(ItemStack item) {
		this.item = item;
	}
	
	public ItemStack getItem() {
		return item;
	}
}
