/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.mixin;

import net.minecraft.client.class_469;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.class_481;
import net.minecraft.network.class_645;
import org.bleachhack.BleachHack;
import org.bleachhack.event.events.EventSwingHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.world.World;

@Mixin(class_481.class)
public class MixinClientPlayerEntity extends ClientPlayerEntity {
	
	@Shadow public class_469 field_1667;

	public MixinClientPlayerEntity(MinecraftClient minecraftClient, World world, Session session, int i) {
		super(minecraftClient, world, session, i);
	}

	/**
	 * @author bleach
	 * @reason dabruh
	 */
	@Overwrite
	public void swingHand() {
		EventSwingHand event = new EventSwingHand();
		BleachHack.eventBus.post(event);

		if (!event.isCancelled()) {
			super.swingHand();
		}

		field_1667.sendPacket(new class_645(this, 1));
	}
}
