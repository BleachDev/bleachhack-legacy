/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.command.commands;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bleachhack.command.Command;
import org.bleachhack.command.CommandCategory;
import org.bleachhack.command.exception.CmdSyntaxException;
import org.bleachhack.util.BleachLogger;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Formatting;

public class CmdEnchant extends Command {

	private static final Map<String[], Enchantment> enchantments = new LinkedHashMap<>();

	static {
		enchantments.put(new String[] { "aqua_affinity", "aqua" }, Enchantment.AQUA_AFFINITY);
		enchantments.put(new String[] { "bane_of_arthropods", "arthropods" }, Enchantment.field_4464);
		enchantments.put(new String[] { "blast", "blast_prot" }, Enchantment.BLAST_PROTECTION);
		enchantments.put(new String[] { "efficiency", "eff" }, Enchantment.EFFICIENCY);
		enchantments.put(new String[] { "feather_falling", "fall" }, Enchantment.field_4457);
		enchantments.put(new String[] { "fire_aspect" }, Enchantment.FIRE_ASPECT);
		enchantments.put(new String[] { "fire_prot" }, Enchantment.FIRE_PROTECTION);
		enchantments.put(new String[] { "flame" }, Enchantment.FLAME);
		enchantments.put(new String[] { "fortune" }, Enchantment.FORTUNE);
		enchantments.put(new String[] { "infinity" }, Enchantment.INIFINITY);
		enchantments.put(new String[] { "knockback", "knock" }, Enchantment.KNOCK_BACK);
		enchantments.put(new String[] { "looting", "loot" }, Enchantment.LOOTING);
		enchantments.put(new String[] { "loyalty" }, Enchantment.LOOTING);
		enchantments.put(new String[] { "power" }, Enchantment.POWER);
		enchantments.put(new String[] { "projectile_prot", "proj_prot" }, Enchantment.field_4459);
		enchantments.put(new String[] { "protection", "prot" }, Enchantment.field_4455);
		enchantments.put(new String[] { "punch" }, Enchantment.PUNCH);
		enchantments.put(new String[] { "respiration", "resp" }, Enchantment.RESPIRATION_);
		enchantments.put(new String[] { "sharpness", "sharp" }, Enchantment.field_4462);
		enchantments.put(new String[] { "silk_touch", "silk" }, Enchantment.SILK_TOUCH);
		enchantments.put(new String[] { "smite" }, Enchantment.field_4463);
		enchantments.put(new String[] { "thorns" }, Enchantment.THORNS);
		enchantments.put(new String[] { "unbreaking" }, Enchantment.UNBREAKING);
	}

	public CmdEnchant() {
		super("enchant", "Enchants an item.", "enchant <enchant/id> <level> | enchant all <level> | enchant list", CommandCategory.CREATIVE);
	}

	@Override
	public void onCommand(String alias, String[] args) throws Exception {
		if (!mc.interactionManager.hasCreativeInventory()) {
			BleachLogger.error("Not In Creative Mode!");
			return;
		}

		if (args.length == 0) {
			throw new CmdSyntaxException();
		}

		if (args[0].equalsIgnoreCase("list")) {
			String text = "";
			int i = 0;
			for (String[] s: enchantments.keySet()) {
				Formatting color = i % 2 == 0 ? Formatting.LIGHT_PURPLE : Formatting.AQUA;
				text += "\u00a77[" + color + String.join("\u00a77/" + color, s) + "\u00a77] ";
				i++;
			}

			BleachLogger.info(text);
			return;
		}

		int level = args.length == 1 ? 1 : Integer.parseInt(args[1]);
		ItemStack item = mc.field_3805.inventory.getMainHandStack();

		if (args[0].equalsIgnoreCase("all")) {
			for (Enchantment e : enchantments.values()) {
				enchant(item, e, level);
			}

			return;
		}

		int i = NumberUtils.toInt(args[0], -1);

		if (i != -1) {
			enchant(item, Arrays.stream(Enchantment.ALL_ENCHANTMENTS).filter(e -> e.id == i).findFirst().get(), level);
		} else {
			enchant(item, enchantments.entrySet().stream()
					.filter(e -> ArrayUtils.contains(e.getKey(), args[0]))
					.map(Entry::getValue)
					.findFirst().orElse(null), level);
		}
	}

	public void enchant(ItemStack item, Enchantment e, int level) {
		if (e == null) {
			throw new CmdSyntaxException("Invalid enchantment!");
		}

		if (item.hasNbt())
			item.setNbt(new NbtCompound());
		if (!item.getNbt().contains("Enchantments")) {
			item.getNbt().put("Enchantments", new NbtList());
		}

		NbtList listnbt = item.getNbt().getList("Enchantments");
		NbtCompound compoundnbt = new NbtCompound();
		compoundnbt.putString("id", String.valueOf(e.id));
		compoundnbt.putInt("lvl", level);
		listnbt.method_1217(compoundnbt);
	}

}
