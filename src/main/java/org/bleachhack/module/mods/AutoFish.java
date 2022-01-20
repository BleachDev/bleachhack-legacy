/*
 * some licence stuff here
 */
package org.bleachhack.module.mods;

import java.util.Comparator;
import java.util.Map;

import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.mixin.AccessorFishingBobberEntity;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingMode;
import org.bleachhack.util.InventoryUtils;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AutoFish extends Module {

	private boolean threwRod;
	private boolean reeledFish;

	public AutoFish() {
		super("AutoFish", KEY_UNBOUND, ModuleCategory.PLAYER, "Automatically fishes for you.",
				new SettingMode("Mode", "Normal", "Aggressive", "Passive").withDesc("AutoFish mode."));
	}

	@Override
	public void onDisable(boolean inWorld) {
		threwRod = false;
		reeledFish = false;

		super.onDisable(inWorld);
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		if (mc.field_3805.fishHook != null) {
			threwRod = false;

			boolean caughtFish = ((AccessorFishingBobberEntity) mc.field_3805.fishHook).getField_4052() > 0;
			if (!reeledFish && caughtFish) {
				boolean hand = getHandWithRod();
				if (hand) {
					// reel back
					mc.interactionManager.method_1228(mc.field_3805, mc.world, mc.field_3805.getMainHandStack());
					reeledFish = true;
					return;
				}
			} else if (!caughtFish) {
				reeledFish = false;
			}
		}

		if (!threwRod && mc.field_3805.fishHook == null && getSetting(0).asMode().getMode() != 2) {
			boolean newHand = getSetting(0).asMode().getMode() == 1 ? InventoryUtils.selectSlot(getBestRodSlot()) : getHandWithRod();
			if (newHand) {
				// throw again
				mc.interactionManager.method_1228(mc.field_3805, mc.world, mc.field_3805.getMainHandStack());
				threwRod = true;
				reeledFish = false;
			}
		}
	}

	private boolean getHandWithRod() {
		return mc.field_3805.getMainHandStack().getItem() == Item.FISHING_ROD;
	}

	private int getBestRodSlot() {
		@SuppressWarnings("unchecked")
		int slot = InventoryUtils.getSlot(true, Comparator.comparingInt(i -> {
			ItemStack is = mc.field_3805.inventory.getInvStack(i);
			if (is == null || is.getItem() != Item.FISHING_ROD)
				return -1;

			return ((Map<Integer, Integer>) EnchantmentHelper.get(is)).values().stream().mapToInt(Integer::intValue).sum();
		}));

		if (mc.field_3805.inventory.getInvStack(slot) != null && mc.field_3805.inventory.getInvStack(slot).getItem() == Item.FISHING_ROD) {
			return slot;
		}

		return -1;
	}
}
