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
import org.bleachhack.event.events.EventDamage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

	@Shadow public void method_6109(Entity entity, float f, double d, double e) {}

	@Redirect(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;method_6109(Lnet/minecraft/entity/Entity;FDD)V"))
	private void damage_setVelocity(LivingEntity entity, Entity attacker, float f, double d, double e) {
		EventDamage.Knockback event = new EventDamage.Knockback(d, 0.4, e);
		BleachHack.eventBus.post(event);
		
		if (!event.isCancelled()) {
			method_6109(entity, f, event.getVelX(), event.getVelZ());
		}
	}
	
	@Inject(method = "damage", at = @At("HEAD"), cancellable = true)
	private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callbackInfo) {
		EventDamage.Normal event = new EventDamage.Normal(source, amount);
		BleachHack.eventBus.post(event);
		
		if (event.isCancelled()) {
			callbackInfo.setReturnValue(false);
		}
	}
}
