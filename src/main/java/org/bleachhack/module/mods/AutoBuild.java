/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import java.util.Arrays;
import java.util.List;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResultType;
import org.apache.commons.lang3.ArrayUtils;
import org.bleachhack.event.events.EventInteract;
import org.bleachhack.event.events.EventTick;
import org.bleachhack.event.events.EventWorldRender;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.mixin.AccessorMinecraftClient;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingMode;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.operation.Operation;
import org.bleachhack.util.operation.OperationList;
import org.bleachhack.util.operation.blueprint.OperationBlueprint;
import org.bleachhack.util.operation.blueprint.PlaceOperationBlueprint;
import org.bleachhack.util.render.Renderer;
import org.bleachhack.util.render.color.QuadColor;
import org.lwjgl.input.Mouse;
import org.bleachhack.util.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.math.Direction;

public class AutoBuild extends Module {

	private static final List<List<OperationBlueprint>> BLUEPRINTS = Arrays.asList(
			Arrays.asList( // Wither
					new PlaceOperationBlueprint(0, 0, 0, Block.SOULSAND.id),
					new PlaceOperationBlueprint(0, 1, 0, Block.SOULSAND.id),
					new PlaceOperationBlueprint(0, 1, -1, Block.SOULSAND.id),
					new PlaceOperationBlueprint(0, 1, 1, Block.SOULSAND.id),
					new PlaceOperationBlueprint(0, 2, -1, Item.SKULL.id),
					new PlaceOperationBlueprint(0, 2, 0, Item.SKULL.id),
					new PlaceOperationBlueprint(0, 2, 1, Item.SKULL.id)),
			Arrays.asList( // WitherH
					new PlaceOperationBlueprint(0, 0, 0, Block.SOULSAND.id),
					new PlaceOperationBlueprint(1, 0, 0, Block.SOULSAND.id),
					new PlaceOperationBlueprint(1, 0, -1, Block.SOULSAND.id),
					new PlaceOperationBlueprint(1, 0, 1, Item.SKULL.id),
					new PlaceOperationBlueprint(2, 0, 0, Item.SKULL.id),
					new PlaceOperationBlueprint(2, 0, 1, Item.SKULL.id)),
			Arrays.asList( // Iron Golem
					new PlaceOperationBlueprint(0, 0, 0, Block.IRON_BLOCK.id),
					new PlaceOperationBlueprint(0, 1, 0, Block.IRON_BLOCK.id),
					new PlaceOperationBlueprint(0, 1, -1, Block.IRON_BLOCK.id),
					new PlaceOperationBlueprint(0, 1, 1, Block.IRON_BLOCK.id),
					new PlaceOperationBlueprint(0, 2, 0, Block.PUMPKIN.id)),
			Arrays.asList( // Snow Golem
					new PlaceOperationBlueprint(0, 0, 0, Block.SNOW_BLOCK.id),
					new PlaceOperationBlueprint(0, 1, 0, Block.SNOW_BLOCK.id),
					new PlaceOperationBlueprint(0, 2, 0, Block.PUMPKIN.id)),
			Arrays.asList( // Nomad Hut
					new PlaceOperationBlueprint(-2, 0, -1, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(-1, 0, -2, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(0, 0, -2, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(1, 0, -2, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(2, 0, -1, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(2, 0, 0, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(2, 0, 1, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(1, 0, 2, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(0, 0, 2, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(-1, 0, 2, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(-2, 0, 1, Block.OBSIDIAN.id),
					
					new PlaceOperationBlueprint(-2, 1, -1, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(-1, 1, -2, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(1, 1, -2, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(2, 1, -1, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(2, 1, 1, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(1, 1, 2, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(-1, 1, 2, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(-2, 1, 1, Block.OBSIDIAN.id),
					
					new PlaceOperationBlueprint(-2, 2, -1, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(-1, 2, -2, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(0, 2, -2, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(1, 2, -2, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(2, 2, -1, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(2, 2, 0, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(2, 2, 1, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(1, 2, 2, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(0, 2, 2, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(-1, 2, 2, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(-2, 2, 1, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(-2, 2, 0, Block.OBSIDIAN.id),
					
					new PlaceOperationBlueprint(-2, 3, 0, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(-1, 3, 0, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(-1, 3, -1, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(-1, 3, 1, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(0, 3, 0, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(0, 3, -1, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(0, 3, 1, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(1, 3, 0, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(1, 3, -1, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(1, 3, 1, Block.OBSIDIAN.id),
					new PlaceOperationBlueprint(2, 3, 0, Block.OBSIDIAN.id)));

	private OperationList current = null;
	private BlockHitResult ray = null;
	private boolean active = false;

	public AutoBuild() {
		super("AutoBuild", KEY_UNBOUND, ModuleCategory.WORLD, "Auto builds structures.",
				new SettingMode("Build", "Wither", "WitherH", "IronGolem", "SnowGolem", "NomadHut").withDesc("What to build"),
				new SettingToggle("Repeat", false).withDesc("Lets you build multiple things without having to re-enable the module."));
	}

	@Override
	public void onDisable(boolean inWorld) {
		current = null;
		ray = null;
		active = false;

		super.onDisable(inWorld);
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		if (!active) {
			ray = mc.field_3805.method_6105(40, ((AccessorMinecraftClient) mc).getTricker().tickDelta);
			if (ray == null || ray.field_595 != HitResultType.TILE)
				return;
			
			Direction dir = Direction.values()[ray.side];

			if (dir.getOffsetY() != 0) {
				dir = Math.abs(ray.x - (int) mc.field_3805.x) > Math.abs(ray.z - (int) mc.field_3805.z)
						? ray.x - (int) mc.field_3805.x > 0 ? Direction.EAST : Direction.WEST
								: ray.z - (int) mc.field_3805.z > 0 ? Direction.SOUTH : Direction.NORTH;
			}

			current = OperationList.create(BLUEPRINTS.get(getSetting(0).asMode().getMode()), new BlockPos(ray.x, ray.y, ray.z).offset(Direction.values()[ray.side]), dir);

			if (mc.currentScreen == null && (Mouse.isButtonDown(0) || Mouse.isButtonDown(1))) {
				active = true;
			}
		} else {
			if (current.executeNext() && current.isDone()) {
				setEnabled(false);

				if (getSetting(1).asToggle().getState()) {
					setEnabled(true);
				}
			}
		}
	}

	@BleachSubscribe
	public void onRender(EventWorldRender.Post event) {
		if (current != null) {
			//RenderUtils.drawOutlineBox(current.getBox(), 1f, 1f, 0f, 0.5f);

			for (Operation o: current.getRemainingOps()) {
				o.render();
			}

			Renderer.drawBoxOutline(current.getNext().pos.toBox().expand(-0.01, -0.01, -0.01), QuadColor.single(1f, 1f, 0f, 0.5f), 3f);
		}

		if (ray != null && !active) {
			BlockPos pos = new BlockPos(ray.x, ray.y, ray.z);

			Renderer.drawBoxFill(pos, QuadColor.single(1f, 1f, 0f, 0.3f), ArrayUtils.remove(Direction.values(), ray.side));
		}
	}

	@BleachSubscribe
	public void onInteract(EventInteract.InteractBlock event) {
		if (ray != null && !active) {
			event.setCancelled(true);
		}
	}
}