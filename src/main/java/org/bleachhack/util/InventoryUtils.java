/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.util;

import java.util.Comparator;
import java.util.function.IntPredicate;
import java.util.stream.IntStream;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

public class InventoryUtils {
	
	private static final MinecraftClient mc = MinecraftClient.getInstance();

	/** Returns the slot with the <b>lowest</b> comparator value **/
	public static int getSlot(boolean reverse, Comparator<Integer> comparator) {
		return IntStream.of(getInventorySlots())
				.boxed()
				.min(reverse ? comparator.reversed() : comparator).get();
	}

	/** Selects the slot with the <b>lowest</b> comparator value and returns the hand it selected **/
	public static boolean selectSlot(boolean reverse, Comparator<Integer> comparator) {
		return selectSlot(getSlot(reverse, comparator));
	}
	
	/** Returns the first slot that matches the Predicate **/
	public static int getSlot(IntPredicate filter) {
		return IntStream.of(getInventorySlots())
				.filter(filter)
				.findFirst().orElse(-1);
	}
	
	/** Selects the first slot that matches the Predicate and returns the hand it selected **/
	public static boolean selectSlot(IntPredicate filter) {
		return selectSlot(getSlot(filter));
	}
	
	public static boolean selectSlot(int slot) {
		if (slot >= 0 && slot <= 36) {
			if (slot < 9) {
				if (slot != mc.field_3805.inventory.selectedSlot) {
					mc.field_3805.inventory.selectedSlot = slot;
					mc.field_3805.field_1667.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
				}

				return true;
			} else if (mc.field_3805.playerScreenHandler == mc.field_3805.openScreenHandler) {
				for (int i = 0; i <= 8; i++) {
					if (mc.field_3805.inventory.getInvStack(i) != null) {
						mc.interactionManager.clickSlot(mc.field_3805.openScreenHandler.syncId, slot, 0, 1, mc.field_3805);

						if (i != mc.field_3805.inventory.selectedSlot) {
							mc.field_3805.inventory.selectedSlot = i;
							mc.field_3805.field_1667.sendPacket(new UpdateSelectedSlotC2SPacket(i));
						}

						return true;
					}
				}

				mc.interactionManager.clickSlot(mc.field_3805.openScreenHandler.syncId, slot, 0, 0, mc.field_3805);
				mc.interactionManager.clickSlot(mc.field_3805.openScreenHandler.syncId, 36 + mc.field_3805.inventory.selectedSlot, 0, 0, mc.field_3805);
				mc.interactionManager.clickSlot(mc.field_3805.openScreenHandler.syncId, slot, 0, 0, mc.field_3805);
				return true;
			}
		}

		return false;
	}
	
	public static int[] getInventorySlots() {
		int[] i = new int[37];
		
		// Add hand slots first
		i[0] = mc.field_3805.inventory.selectedSlot;

		for (int j = 0; j < 36; j++) {
			if (j != mc.field_3805.inventory.selectedSlot) {
				i[j + 1] = j;
			}
		}
		
		return i;
	}
}
