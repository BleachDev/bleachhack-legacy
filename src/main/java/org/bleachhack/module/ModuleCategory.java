/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum ModuleCategory {
	PLAYER(new ItemStack(Item.SKULL)),
	RENDER(new ItemStack(Block.GLASS_BLOCK)),
	COMBAT(new ItemStack(Item.DIAMOND_SWORD)),
	MOVEMENT(new ItemStack(Item.DIAMOND_BOOTS)),
	EXPLOITS(new ItemStack(Block.COMMAND_BLOCK)),
	MISC(new ItemStack(Block.ACTIVATOR_RAIL)),
	WORLD(new ItemStack(Block.GRASS_BLOCK));
	
	private final ItemStack item;
	
	ModuleCategory(ItemStack item) {
		this.item = item;
	}
	
	public ItemStack getItem() {
		return item;
	}
}
