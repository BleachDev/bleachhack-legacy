/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.mixin;

import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import org.bleachhack.BleachHack;
import org.bleachhack.command.Command;
import org.bleachhack.command.CommandManager;
import org.bleachhack.event.events.EventPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.IntegratedConnection;
import net.minecraft.network.OutboundConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;

@Mixin(value = { IntegratedConnection.class, OutboundConnection.class })
public class MixinClientConnection {

	@Redirect(method = "applyQueuedPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Packet;apply(Lnet/minecraft/network/listener/PacketListener;)V"))
	private void applyQueuedPackets(Packet packet, PacketListener listener) {
		EventPacket.Read event = new EventPacket.Read(packet);
		BleachHack.eventBus.post(event);

		if (!event.isCancelled()) {
			packet.apply(listener);
		}
	}

	@Inject(method = "send", at = @At("HEAD"), cancellable = true)
	private void send(Packet packet, CallbackInfo callback) {
		if (packet instanceof ChatMessageS2CPacket) {
			if (!CommandManager.allowNextMsg) {
				ChatMessageS2CPacket pack = (ChatMessageS2CPacket) packet;
				if (pack.message.startsWith(Command.getPrefix())) {
					CommandManager.callCommand(pack.message.substring(Command.getPrefix().length()));
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