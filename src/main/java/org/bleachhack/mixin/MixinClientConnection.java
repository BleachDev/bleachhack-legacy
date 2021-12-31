/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.mixin;

import org.bleachhack.BleachHack;
import org.bleachhack.command.Command;
import org.bleachhack.command.CommandManager;
import org.bleachhack.event.events.EventPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.class_632;
import net.minecraft.class_633;
import net.minecraft.class_648;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;

@Mixin(value = { class_632.class, class_633.class })
public class MixinClientConnection {

	@Redirect(method = "method_1769", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Packet;apply(Lnet/minecraft/network/listener/PacketListener;)V"))
	private void channelRead0(Packet packet, PacketListener listener) {
		EventPacket.Read event = new EventPacket.Read(packet);
		BleachHack.eventBus.post(event);

		if (!event.isCancelled()) {
			packet.apply(listener);
		}
	}

	@Inject(method = "method_1766", at = @At("HEAD"), cancellable = true)
	private void method_1766(Packet packet, CallbackInfo callback) {
		if (packet instanceof class_648) {
			if (!CommandManager.allowNextMsg) {
				class_648 pack = (class_648) packet;
				if (pack.field_2408.startsWith(Command.getPrefix())) {
					CommandManager.callCommand(pack.field_2408.substring(Command.getPrefix().length()));
					callback.cancel();
				}
			}

			CommandManager.allowNextMsg = false;
		}

		EventPacket.Send event = new EventPacket.Send(packet);
		BleachHack.eventBus.post(event);

		if (event.isCancelled()) {
			callback.cancel();
		}
	}
}