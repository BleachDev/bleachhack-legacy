/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.*;
import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.BlockPos;
import org.bleachhack.util.InventoryUtils;
import org.bleachhack.util.world.WorldUtils;

public class AutoFarm extends Module {

	public AutoFarm() {
		super("AutoFarm", KEY_UNBOUND, ModuleCategory.PLAYER, "Automatically does farming activities for you.",
				new SettingSlider("Range", 1, 6, 4.5, 1).withDesc("Farming reach."),
				new SettingToggle("Till", true).withDesc("Tills dirt around you.").withChildren(
						new SettingToggle("WateredOnly", false).withDesc("Only tills watered dirt.")),
				new SettingToggle("Harvest", true).withDesc("Harvests grown crops.").withChildren(
						new SettingToggle("Crops", true).withDesc("Harvests wheat, carrots, potato & beetroot."),
						new SettingToggle("StemCrops", true).withDesc("Harvests melons/pumpkins."),
						new SettingToggle("NetherWart", true).withDesc("Harvests nether wart."),
						new SettingToggle("Cocoa", true).withDesc("Harvests cocoa beans."),
						new SettingToggle("SugarCane", false).withDesc("Harvests sugar canes."),
						new SettingToggle("Cactus", false).withDesc("Harvests cactuses.")),
				new SettingToggle("Plant", true).withDesc("Plants crops around you.").withChildren(
						new SettingToggle("Crops", true).withDesc("Plants wheat, carrots, potato & beetroot."),
						new SettingToggle("StemCrops", true).withDesc("Plants melon/pumpkin stems."),
						new SettingToggle("NetherWart", true).withDesc("Plants nether wart.")),
				new SettingToggle("Bonemeal", true).withDesc("Bonemeals ungrown crop.").withChildren(
						new SettingToggle("Crops", true).withDesc("Bonemeals wheat, carrots, potato & beetroot."),
						new SettingToggle("StemCrops", true).withDesc("Bonemeals melon/pumpkin stems."),
						new SettingToggle("Cocoa", true).withDesc("Bonemeals cocoa beans."),
						new SettingToggle("Mushrooms", false).withDesc("Bonemeals mushrooms."),
						new SettingToggle("Saplings", false).withDesc("Bonemeals saplings.")));
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		double range = getSetting(0).asSlider().getValue();
		int ceilRange = MathHelper.ceil(range);
		SettingToggle tillSetting = getSetting(1).asToggle();
		SettingToggle harvestSetting = getSetting(2).asToggle();
		SettingToggle plantSetting = getSetting(3).asToggle();
		SettingToggle bonemealSetting = getSetting(4).asToggle();

		Vec3d eyePos = Vec3d.method_604(mc.field_3805.x, mc.field_3805.y + mc.field_3805.getEyeHeight(), mc.field_3805.z);
		for (BlockPos pos: BlockPos.iterateOutwards(new BlockPos(mc.field_3805).up(), ceilRange, ceilRange, ceilRange)) {
			if (eyePos.distanceTo(Vec3d.method_604(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)) > range)
				continue;

			Block block = Block.BLOCKS[mc.world.getBlock(pos.getX(), pos.getY(), pos.getZ())];
			int data = mc.world.getBlockData(pos.getX(), pos.getY(), pos.getZ());
			if (tillSetting.getState() && canTill(block) && mc.world.isAir(pos.getX(), pos.getY() + 1, pos.getZ())) {
				if (!tillSetting.getChild(0).asToggle().getState()
						|| BlockPos.stream(pos.getX() - 4, pos.getY(), pos.getZ() - 4, pos.getX() + 4, pos.getY(), pos.getZ() + 4).anyMatch(
								b -> {
									int i = mc.world.getBlock(b.getX(), b.getY(), b.getZ());
									return i == 8 || i == 9;
								})) {
					boolean hoe = InventoryUtils.selectSlot(i -> {
						ItemStack is = mc.field_3805.inventory.getInvStack(i);
						return is != null && is.getItem() instanceof HoeItem;
					});
					
					if (hoe) {
						WorldUtils.rightClick(pos, Vec3d.method_604(pos.getX(), pos.getY() + 1, pos.getZ()), Direction.UP);
						return;
					}
				}
			}

			if (harvestSetting.getState()) {
				if ((harvestSetting.getChild(0).asToggle().getState() && block instanceof CropBlock && data >= 6)
						|| (harvestSetting.getChild(1).asToggle().getState() && (block instanceof MelonBlock || block instanceof PumpkinBlock))
						|| (harvestSetting.getChild(2).asToggle().getState() && block instanceof NetherWartBlock && data >= 3)
						|| (harvestSetting.getChild(3).asToggle().getState() && block instanceof CocoaBlock && data >= 2)
						|| (harvestSetting.getChild(4).asToggle().getState() && shouldHarvestTallCrop(pos, block, Block.SUGARCANE.id))
						|| (harvestSetting.getChild(5).asToggle().getState() && shouldHarvestTallCrop(pos, block, Block.CACTUS.id))) {
					mc.interactionManager.method_1235(pos.getX(), pos.getY(), pos.getZ(), 1);
					return;
				}
			}

			if (plantSetting.getState() && mc.world.method_4685(LivingEntity.class, pos.up().toBox(), e -> e instanceof LivingEntity && e.isAlive()).isEmpty()) {
				if (block instanceof FarmlandBlock && mc.world.isAir(pos.getX(), pos.getY() + 1, pos.getZ())) {
					int slot = InventoryUtils.getSlot(i -> {
						ItemStack is = mc.field_3805.inventory.getInvStack(i);
						if (is == null)
							return false;
					
						Item item = is.getItem();

						if (plantSetting.getChild(0).asToggle().getState() && (item == Item.WHEAT_SEEDS || item == Item.CARROTS || item == Item.POTATO)) {
							return true;
						}

						return plantSetting.getChild(1).asToggle().getState() && (item == Item.PUMPKIN_SEEDS || item == Item.MELON_SEEDS);
					});

					if (slot != -1) {
						WorldUtils.placeBlock(pos.up(), slot, 0, false, false, true);
						return;
					}
				}

				if (block instanceof SoulSandBlock && mc.world.isAir(pos.getX(), pos.getY() + 1, pos.getZ()) && plantSetting.getChild(2).asToggle().getState()) {
					int slot = InventoryUtils.getSlot(i -> {
						ItemStack is = mc.field_3805.inventory.getInvStack(i);
						return is != null && is.getItem() == Item.NETHER_WART;
					});

					if (slot != -1) {
						WorldUtils.placeBlock(pos.up(), slot, 0, false, false, true);
						return;
					}
				}
			}

			if (bonemealSetting.getState()) {
				int slot = InventoryUtils.getSlot(i -> {
					ItemStack is = mc.field_3805.inventory.getInvStack(i);
					return is != null && is.getItem() instanceof DyeItem && is.getMeta() == 15;
				});

				if (slot != -1) {
					if ((bonemealSetting.getChild(0).asToggle().getState() && block instanceof CropBlock && data < 6)
							|| (bonemealSetting.getChild(1).asToggle().getState() && (block instanceof MelonBlock || block instanceof PumpkinBlock) && data < 6)
							|| (bonemealSetting.getChild(2).asToggle().getState() && block instanceof CocoaBlock && data < 2)
							|| (bonemealSetting.getChild(3).asToggle().getState() && block instanceof MushroomBlock)
							|| (bonemealSetting.getChild(4).asToggle().getState() && block instanceof SaplingBlock && canPlaceSapling(pos))) {
						boolean hand = InventoryUtils.selectSlot(slot);
						if (hand)
							WorldUtils.rightClick(pos, Vec3d.method_604(pos.getX(), pos.getY() + 1, pos.getZ()), Direction.UP);
						return;
					}
				}
			}
		}
	}

	private boolean shouldHarvestTallCrop(BlockPos pos, Block posBlock, int blockClass) {
		return posBlock.id == blockClass
				&& mc.world.getBlock(pos.getX(), pos.getY() - 1, pos.getZ()) == blockClass
				&& mc.world.getBlock(pos.getX(), pos.getY() - 2, pos.getZ()) != blockClass;
	}
	
	private boolean canPlaceSapling(BlockPos pos) {
		return !mc.world.isBoxNotEmpty(Box.of(pos.getX() - 1, pos.getY() + 1, pos.getZ() - 1, pos.getX() + 1, pos.getY() + 5, pos.getZ() + 1));
	}
	
	private boolean canTill(Block block) {
		return block == Block.DIRT || block == Block.GRASS_BLOCK;
	}
}
