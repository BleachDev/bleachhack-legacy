/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import com.google.gson.internal.Streams;
import net.minecraft.class_711;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.bleachhack.BleachHack;
import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingMode;
import org.bleachhack.setting.module.SettingRotate;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.BleachQueue;
import org.bleachhack.util.InventoryUtils;
import org.bleachhack.util.world.EntityUtils;
import org.bleachhack.util.world.WorldUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KillAura extends Module {


	private int delay = 0;

	public KillAura() {
		super("KillAura", KEY_UNBOUND, ModuleCategory.COMBAT, "Automatically attacks entities.",
				new SettingMode("Sort", "Distance", "Health").withDesc("How to sort targets."),
				new SettingToggle("Players", true).withDesc("Attacks Players."),
				new SettingToggle("Mobs", true).withDesc("Attacks Mobs.").withChildren(
						new SettingToggle("Zombie Pigmen", false).withDesc("Attacks zombie pigmen or not.")),
				new SettingToggle("Animals", false).withDesc("Attacks Animals."),
				new SettingToggle("Projectiles", false).withDesc("Attacks Shulker Bullets"),
				new SettingToggle("MultiAura", false).withDesc("Atacks multiple entities at once.").withChildren(
						new SettingSlider("Targets", 1, 20, 3, 0).withDesc("How many targets to attack at once.")),
				new SettingToggle("Raycast", true).withDesc("Only attacks if you can see the target."),
				new SettingSlider("Range", 0, 6, 4.25, 2).withDesc("Attack range."),
				new SettingSlider("CPS", 0, 20, 8, 0).withDesc("Attack CPS if 1.9 delay is disabled."),
				new SettingToggle("AutoSword", false).withDesc("Automatically switches to sword before swinging").withChildren(
						new SettingToggle("GappleIgnore", true).withDesc("Stops sword switch when holding egap")));
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		if (!mc.field_3805.isAlive()) {
			return;
		}

		delay++;
		int reqDelay = (int) Math.rint(20 / getSetting(8).asSlider().getValue());

		boolean cooldownDone = (delay > reqDelay || reqDelay == 0);

		if (cooldownDone) {
			for (Entity e: getEntities()) {

				if(getSetting(9).asToggle().getState()) {
					if(!getSetting(9).asToggle().getChild(0).asToggle().getState() || getSetting(9).asToggle().getChild(0).asToggle().getState() && !(mc.field_3805.inventory.getMainHandStack().getItem() instanceof AppleItem)) {
						if(mc.field_3805.inventory.getMainHandStack() == null || !(mc.field_3805.inventory.getMainHandStack().getItem() instanceof SwordItem)) {
							int swordSlot = InventoryUtils.getSlot(i -> mc.field_3805.inventory.getInvStack(i) != null
									&& mc.field_3805.inventory.getInvStack(i).getItem() instanceof SwordItem);
							if (swordSlot == -1) {
								return;
							}
							InventoryUtils.selectSlot(swordSlot);
						}
					}
				}

				mc.interactionManager.attackEntity(mc.field_3805, e);
				mc.field_3805.swingHand();

				delay = 0;
			}
		}
	}

	private List<Entity> getEntities() {
		Stream<Entity> targets;

		targets = mc.world.getLoadedEntities().stream();

		Comparator<Entity> comparator;
		//if (getSetting(0).asMode().getMode() == 0) {
		//	comparator = Comparator.comparing(e -> {
		//		Vec3d center = e.getBoundingBox().getCenter();
		//		double diffX = center.x - mc.player.getX();
		//		double diffY = center.y - mc.player.getEyeY();
		//		double diffZ = center.z - mc.player.getZ();
		//		double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
		//		float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
		//		float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
		//		return Math.abs(MathHelper.wrapDegrees(yaw - mc.player.getYaw())) + Math.abs(MathHelper.wrapDegrees(pitch - mc.player.getPitch()));
		//	});
		//} else {
		//	comparator = Comparator.comparing(mc.player::distanceTo);
		//}
		comparator = Comparator.comparing(mc.field_3805::distanceTo);

		return targets
				.filter(e -> EntityUtils.isAttackable(e, true)
						&& mc.field_3805.distanceTo(e) <= getSetting(7).asSlider().getValue()
						&& (mc.field_3805.canSee(e) || !getSetting(6).asToggle().getState()))
				.filter(e -> (EntityUtils.isPlayer(e) && getSetting(1).asToggle().getState())
						|| (EntityUtils.isMob(e) && getSetting(2).asToggle().getState() && !(e instanceof ZombiePigmanEntity))
						|| ((e instanceof ZombiePigmanEntity) && getSetting(2).asToggle().getChild(0).asToggle().getState())
						|| (EntityUtils.isAnimal(e) && getSetting(3).asToggle().getState())
						|| ((e instanceof FireballEntity) && getSetting(4).asToggle().getState()))
				.sorted(comparator)
				.limit(getSetting(5).asToggle().getState() ? getSetting(5).asToggle().getChild(0).asSlider().getValueLong() : 1L)
				.collect(Collectors.toList());
	}
}
