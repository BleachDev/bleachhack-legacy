/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import net.minecraft.client.class_482;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CommonI18n;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.StringUtils;
import org.bleachhack.BleachHack;
import org.bleachhack.command.commands.CmdEntityStats;
import org.bleachhack.event.events.EventEntityRender;
import org.bleachhack.event.events.EventWorldRender;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingMode;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.render.Renderer;
import org.bleachhack.util.render.WorldRenderer;
import org.bleachhack.util.world.EntityUtils;

import java.util.*;
import java.util.Map.Entry;

public class Nametags extends Module {

	public Nametags() {
		super("Nametags", KEY_UNBOUND, ModuleCategory.RENDER, "Shows bigger/cooler nametags above entities.",
				new SettingMode("Health", "Number", "NumberOf", "Bar", "Percent").withDesc("How to show health."),
				new SettingToggle("Players", true).withDesc("Shows nametags over player.").withChildren(
						new SettingSlider("Size", 0.5, 5, 2, 1).withDesc("The size of the nametags."),
						new SettingToggle("Inventory", true).withDesc("Shows the equipment of the player."),
						new SettingToggle("Name", true).withDesc("Shows the name of the player."),
						new SettingToggle("Health", true).withDesc("Shows the health of the player."),
						new SettingToggle("Ping", true).withDesc("Shows the ping of the player.")),
				new SettingToggle("Animals", true).withDesc("Shows nametags over animals.").withChildren(
						new SettingSlider("Size", 0.5, 5, 1, 1).withDesc("The size of the nametags."),
						new SettingToggle("Inventory", true).withDesc("Shows the equipment of the animal."),
						new SettingToggle("Name", true).withDesc("Shows the name of the animal."),
						new SettingToggle("Health", true).withDesc("Shows the health of the animal."),
						new SettingToggle("Tamed", false).withDesc("Shows if the animal is tamed.").withChildren(
								new SettingMode("If Not", "Show", "Hide").withDesc("What to show if the animal isn't tamed.")),
						new SettingToggle("Owner", true).withDesc("Shows the owner of the pet if its tamed."),
						new SettingToggle("HorseStats", false).withDesc("Shows the entities stats if its a horse.")),
				new SettingToggle("Mobs", false).withDesc("Shows nametags over mobs.").withChildren(
						new SettingSlider("Size", 0.5, 5, 1, 1).withDesc("The size of the nametags."),
						new SettingToggle("Inventory", true).withDesc("Shows the equipment of the mob."),
						new SettingToggle("Name", true).withDesc("Shows the name of the mob."),
						new SettingToggle("Health", true).withDesc("Shows the health of the mob.")),
				new SettingToggle("Items", true).withDesc("Shows nametags for items.").withChildren(
						new SettingSlider("Size", 0.5, 5, 1, 1).withDesc("Size of the nametags."),
						new SettingToggle("CustomName", true).withDesc("Shows the items custom name if it has it."),
						new SettingToggle("ItemCount", true).withDesc("Shows how many items are in the stack.")));
	}

	@BleachSubscribe
	public void onLivingLabelRender(EventEntityRender.Single.Label event) {
		if ((event.getEntity() instanceof PlayerEntity && getSetting(1).asToggle().getState())
				|| (EntityUtils.isAnimal(event.getEntity()) && getSetting(2).asToggle().getState())
				|| (event.getEntity() instanceof Monster && getSetting(3).asToggle().getState())
				|| (event.getEntity() instanceof ItemEntity && getSetting(4).asToggle().getState()))
			event.setCancelled(true);
	}

	@SuppressWarnings("unchecked")
	@BleachSubscribe
	public void onWorldRender(EventWorldRender.Post event) {
		for (Entity entity: (List<Entity>) mc.world.getLoadedEntities()) {
			if (entity == mc.field_3805 || entity.rider == mc.field_3805 || mc.field_3805.rider == entity)
				continue;

			Vec3d inPos = Renderer.getInterpolationOffset(entity);
			Vec3d rPos = Vec3d.method_604(entity.x - inPos.x, entity.boundingBox.maxY + 0.25 - inPos.y, entity.z - inPos.z);

			Vec3d camera = Vec3d.method_604(EntityRenderDispatcher.cameraX, EntityRenderDispatcher.cameraY, EntityRenderDispatcher.cameraZ);
			double dist = entity.distanceTo(camera.x, camera.y, camera.z) / 20;

			if (entity instanceof PlayerEntity && getSetting(1).asToggle().getState()) {
				double scale = Math.max(getSetting(1).asToggle().getChild(0).asSlider().getValue() * dist, 1);

				List<String> lines = getPlayerLines((PlayerEntity) entity);
				drawLines(rPos.x, rPos.y, rPos.z, scale, lines);

				if (getSetting(1).asToggle().getChild(1).asToggle().getState()) {
					drawItems(rPos.x, rPos.y + (lines.size() + 1) * 0.25 * scale, rPos.z, scale, getMainEquipment(entity));
				}
			} else if (EntityUtils.isAnimal(entity) && getSetting(2).asToggle().getState()) {
				double scale = Math.max(getSetting(2).asToggle().getChild(0).asSlider().getValue() * dist, 1);

				List<String> lines = getAnimalLines((LivingEntity) entity);
				drawLines(rPos.x, rPos.y, rPos.z, scale, lines);
			} else if (entity instanceof Monster && getSetting(3).asToggle().getState()) {
				double scale = Math.max(getSetting(3).asToggle().getChild(0).asSlider().getValue() * dist, 1);

				List<String> lines = getMobLines((LivingEntity) entity);
				drawLines(rPos.x, rPos.y, rPos.z, scale, lines);

				if (getSetting(3).asToggle().getChild(1).asToggle().getState()) {
					drawItems(rPos.x, rPos.y + (lines.size() + 1) * 0.25 * scale, rPos.z, scale, getMainEquipment(entity));
				}
			} else if (entity instanceof ItemEntity && getSetting(4).asToggle().getState()) {
				double scale = Math.max(getSetting(4).asToggle().getChild(0).asSlider().getValue() * dist, 1);

				List<String> lines = getItemLines((ItemEntity) entity);
				drawLines(rPos.x, rPos.y, rPos.z, scale, lines);
			}
		}
	}

	private void drawLines(double x, double y, double z, double scale, List<String> lines) {
		double offset = lines.size() * 0.25 * scale;

		for (String t: lines) {
			WorldRenderer.drawText(t, 0xffffff, x, y + offset, z, scale, true);
			offset -= 0.25 * scale;
		}
	}

	private void drawItems(double x, double y, double z, double scale, List<ItemStack> items) {
		double lscale = scale * 0.4;

		for (int i = 0; i < items.size(); i++) {
			drawItem(x, y, z, i + 0.5 - items.size() / 2d, 0, lscale, items.get(i));
		}
	}

	@SuppressWarnings("unchecked")
	private void drawItem(double x, double y, double z, double offX, double offY, double scale, ItemStack item) {
		if (item == null)
			return;

		WorldRenderer.drawGuiItem(x, y, z, offX * scale, offY * scale, scale, item);

		double w = mc.textRenderer.getStringWidth("x" + item.count) / 52d;
		WorldRenderer.drawText("x" + item.count, 0xffffff,
				x, y, z, (offX - w) * scale, (offY - 0.07) * scale, scale * 1.75, false);

		int c = 0;
		for (Entry<Integer, Integer> m : ((Map<Integer, Integer>) EnchantmentHelper.get(item)).entrySet()) {
			String text = CommonI18n.translate(Enchantment.ALL_ENCHANTMENTS[m.getKey()].getTranslationKey());
			if (text.isEmpty())
				continue;

			String subText = text.substring(0, Math.min(text.length(), 2)) + m.getValue();

			WorldRenderer.drawText(subText, 0xffb0e0,
					x, y, z, (offX + 0.02) * scale, (offY + 0.75 - c * 0.34) * scale, scale * 1.4, false);
			c--;
		}
	}

	private List<ItemStack> getMainEquipment(Entity e) {
		List<ItemStack> list = new ArrayList<>(Arrays.asList(e.getArmorStacks()));
		if (e instanceof PlayerEntity)
			list.add(((PlayerEntity) e).getMainHandStack());
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<String> getPlayerLines(PlayerEntity player) {
		List<String> lines = new ArrayList<>();
		List<String> mainText = new ArrayList<>();

		class_482 playerEntry = ((List<class_482>) mc.field_3805.field_1667.field_1618).stream()
				.filter(e -> e.field_1679.equals(player.getUsername()))
				.findFirst().orElse(null);

		if (getSetting(1).asToggle().getChild(4).asToggle().getState() && playerEntry != null) { // Ping
			mainText.add(Formatting.GRAY.toString() + playerEntry.field_1680 + "ms");
		}

		if (getSetting(1).asToggle().getChild(2).asToggle().getState()) { // Name
			mainText.add((BleachHack.friendMang.has(player) ? Formatting.AQUA : Formatting.RED) + player.getUsername());
		}

		if (getSetting(1).asToggle().getChild(3).asToggle().getState()) { // Health
			if (getSetting(0).asMode().getMode() == 2) {
				lines.add(getHealthText(player));
			} else {
				mainText.add(getHealthText(player));
			}
		}

		if (!mainText.isEmpty())
			lines.add(String.join(" ", mainText));

		return lines;
	}

	public List<String> getAnimalLines(LivingEntity animal) {
		List<String> lines = new ArrayList<>();

		if (animal instanceof HorseBaseEntity || animal instanceof TameableEntity) {
			boolean tame = animal instanceof HorseBaseEntity
					? ((HorseBaseEntity) animal).isTame() : ((TameableEntity) animal).isTamed();

			UUID ownerUUID = UUID.fromString(
					animal instanceof HorseBaseEntity ? ((HorseBaseEntity) animal).getOwnerUuid() : ((TameableEntity) animal).getOwnerId());

			if (getSetting(2).asToggle().getChild(4).asToggle().getState() && !animal.isBaby()
					&& (getSetting(2).asToggle().getChild(4).asToggle().getChild(0).asMode().getMode() != 1 || tame)) {
				lines.add(0, tame ? Formatting.GREEN + "Tamed: Yes" : Formatting.RED + "Tamed: No");
			}

			if (getSetting(2).asToggle().getChild(5).asToggle().getState() && ownerUUID != null) {
				lines.add(0, Formatting.GREEN + "Owner: " + ownerUUID);
			}

			if (getSetting(2).asToggle().getChild(6).asToggle().getState() && animal instanceof HorseBaseEntity) {
				HorseBaseEntity he = (HorseBaseEntity) animal;

				lines.add(0, Formatting.GREEN.toString() +
						CmdEntityStats.getSpeed(he) + " m/s" +
						Formatting.GRAY + " | " + Formatting.RESET +
						CmdEntityStats.getJumpHeight(he) + " Jump");
			}
		}

		List<String> mainText = new ArrayList<>();

		if (getSetting(2).asToggle().getChild(2).asToggle().getState()) { // Name
			mainText.add(Formatting.GREEN + animal.getTranslationKey());
		}

		if (getSetting(2).asToggle().getChild(3).asToggle().getState()) { // Health
			if (getSetting(0).asMode().getMode() == 2) {
				lines.add(0, getHealthText(animal));
			} else {
				mainText.add(getHealthText(animal));
			}
		}

		if (!mainText.isEmpty())
			lines.add(String.join(" ", mainText));

		return lines;
	}

	public List<String> getMobLines(LivingEntity mob) {
		List<String> lines = new ArrayList<>();
		List<String> mainText = new ArrayList<>();

		if (getSetting(3).asToggle().getChild(2).asToggle().getState()) { // Name
			mainText.add(Formatting.DARK_PURPLE + mob.getTranslationKey());
		}

		if (getSetting(3).asToggle().getChild(3).asToggle().getState()) { // Health
			if (getSetting(0).asMode().getMode() == 2) {
				lines.add(getHealthText(mob));
			} else {
				mainText.add(getHealthText(mob));
			}
		}

		if (!mainText.isEmpty())
			lines.add(String.join(" ", mainText));

		return lines;
	}

	public List<String> getItemLines(ItemEntity item) {
		List<String> lines = new ArrayList<>();
		String o = CommonI18n.translate(item.method_4548().getItem().getTranslationKey(item.method_4548()) + ".name");

		if (!o.equals(item.method_4548().getName()) && getSetting(4).asToggle().getChild(1).asToggle().getState()) {
			lines.add(Formatting.GOLD + "\"" + Formatting.YELLOW + item.method_4548().getName() + Formatting.GOLD + "\"");
		}

		lines.add(Formatting.GOLD + o +
				(getSetting(4).asToggle().getChild(2).asToggle().getState() ? Formatting.YELLOW + " [x" + item.method_4548().count + "]" : ""));

		return lines;
	}

	private String getHealthText(LivingEntity e) {
		int totalHealth = (int) (e.getHealth() + e.getAbsorption());

		if (getSetting(0).asMode().getMode() == 0) {
			return getHealthColor(e) + Integer.toString(totalHealth);
		} else if (getSetting(0).asMode().getMode() == 1) {
			return getHealthColor(e) + Integer.toString(totalHealth) + Formatting.GREEN + "/" + (int) e.getMaxHealth();
		} else if (getSetting(0).asMode().getMode() == 2) {
			// Health bar
			String health = "";

			// - Add Green Normal Health
			health += Formatting.GREEN + StringUtils.repeat('|', (int) e.getHealth());

			// - Add Yellow Absorption Health
			health += Formatting.YELLOW + StringUtils.repeat('|', (int) Math.min(e.getAbsorption(), e.getMaxHealth() - e.getHealth()));

			// - Add Red Empty Health (Remove Based on absorption amount)
			health += Formatting.RED + StringUtils.repeat('|', (int) e.getMaxHealth() - totalHealth);

			// - Add "+??" to the end if the entity has extra hearts
			if (totalHealth > (int) e.getMaxHealth()) {
				health += Formatting.YELLOW + " +" + (totalHealth - (int) e.getMaxHealth());
			}

			return health;
		} else {
			return getHealthColor(e) + "" + (int) (totalHealth / e.getMaxHealth() * 100) + "%";
		}
	}

	private Formatting getHealthColor(LivingEntity entity) {
		if (entity.getHealth() + entity.getAbsorption() > entity.getMaxHealth()) {
			return Formatting.YELLOW;
		} else {
			return Formatting.RED;
			//return MathHelper.hsvToRgb((entity.getHealth() + entity.getAbsorption()) / (entity.getMaxHealth() * 3), 1f, 1f);
		}
	}
}
