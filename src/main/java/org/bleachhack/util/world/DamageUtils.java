/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.util.world;

import com.google.common.collect.Multimap;

import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class DamageUtils {

	private static final MinecraftClient mc = MinecraftClient.getInstance();

	public static float getItemAttackDamage(ItemStack stack) {
		@SuppressWarnings("unchecked")
		float damage = 1f
		+ ((Multimap<String, AttributeModifier>) stack.getAttributes()).get(EntityAttributes.GENERIC_ATTACK_DAMAGE.getId())
		.stream()
		.map(e -> (float) e.getAmount())
		.findFirst().orElse(0f);

		return damage + EnchantmentHelper.method_5492(mc.field_3805, mc.field_3805);
	}

	public static float getAttackDamage(PlayerEntity attacker, Entity target) {
		@SuppressWarnings("unchecked")
		float damage = (float) attacker.getAttributeContainer().get(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue()
		+ ((Multimap<String, AttributeModifier>) attacker.getMainHandStack().getAttributes()).get(EntityAttributes.GENERIC_ATTACK_DAMAGE.getId())
		.stream()
		.map(e -> (float) e.getAmount())
		.findFirst().orElse(0f);

		float enchDamage = target instanceof LivingEntity ? EnchantmentHelper.method_5492(attacker, (LivingEntity) target) : 0;

		if (damage <= 0f && enchDamage <= 0f) {
			return 0f;
		}

		// Crits
		if (attacker.fallDistance > 0.0F
				&& !attacker.onGround
				&& !attacker.isClimbing()
				&& !attacker.isTouchingWater()
				&& !attacker.hasStatusEffect(StatusEffect.BLINDNESS.id)
				&& !attacker.hasVehicle()
				&& !attacker.isSprinting()
				&& target instanceof LivingEntity) {
			damage *= 1.5f;
		}

		damage += enchDamage;

		if (target instanceof LivingEntity) {
			LivingEntity livingTarget = (LivingEntity) target;

			// Enchantments
			if (livingTarget.hasStatusEffect(StatusEffect.RESISTANCE)) {
				int resistance = 25 - (livingTarget.getEffectInstance(StatusEffect.RESISTANCE).getAmplifier() + 1) * 5;
				float resistance_1 = damage * resistance;
				damage = Math.max(resistance_1 / 25f, 0f);
			}
		}

		return damage;
	}

	public static boolean willKill(LivingEntity target, float damage) {
		return damage >= target.getHealth() + target.getAbsorption();
	}

	public static boolean willGoBelowHealth(LivingEntity target, float damage, float minHealth) {
		return target.getHealth() + target.getAbsorption() - damage < minHealth;
	}
}
