/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import net.minecraft.class_482;
import net.minecraft.class_720;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.TheNetherDimension;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bleachhack.BleachHack;
import org.bleachhack.event.events.EventPacket;
import org.bleachhack.event.events.EventRenderInGameHud;
import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.gui.clickgui.UIClickGuiScreen;
import org.bleachhack.gui.clickgui.window.UIContainer;
import org.bleachhack.gui.clickgui.window.UIWindow;
import org.bleachhack.gui.clickgui.window.UIWindow.Position;
import org.bleachhack.mixin.AccessorMinecraftClient;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.module.ModuleManager;
import org.bleachhack.setting.module.SettingColor;
import org.bleachhack.setting.module.SettingMode;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.BleachLogger;
import org.bleachhack.util.BlockPos;
import org.bleachhack.util.InventoryUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class UI extends Module {
	private ArrayList<StatusEffect> potionEffects = new ArrayList<>(Arrays.asList(StatusEffect.REGENERATION,StatusEffect.STRENGTH,StatusEffect.SPEED));

	private List<String> moduleListText = new ArrayList<>();
	private List<String> potionListText = new ArrayList<>();
	private String fpsText = "";
	private String pingText = "";
	private String coordsText = "";
	private String tpsText = "";
	private String durabilityText = "";
	private String serverText = "";
	private String timestampText = "";
	private String chunksizeText = "";

	private long prevTime = 0;
	private double tps = 20;
	private long lastPacket = 0;

	private int chunkSize;
	private long lastChunkTime;
	private ExecutorService chunkExecutor;
	private Pair<ChunkPos, Future<Integer>> chunkFuture;

	public UI() {
		super("UI", KEY_UNBOUND, ModuleCategory.RENDER, true, "Shows stuff onscreen.",
				new SettingToggle("Modulelist", true).withDesc("Shows the module list.").withChildren(                                 // 0
						new SettingToggle("InnerLine", true).withDesc("Adds an extra line to the front of the module list."),
						new SettingToggle("OuterLine", false).withDesc("Adds an outer line to the module list."),
						new SettingToggle("Fill", true).withDesc("Adds a black fill behind the module list."),
						new SettingToggle("Watermark", true).withDesc("Adds the BleachHack watermark to the module list.").withChildren(
								new SettingMode("Mode", "New", "Old").withDesc("The watermark type.")),
						new SettingSlider("HueBright", 0, 1, 1, 2).withDesc("The hue of the rainbow."),
						new SettingSlider("HueSat", 0, 1, 0.5, 2).withDesc("The saturation of the rainbow."),
						new SettingSlider("HueSpeed", 0.1, 50, 25, 1).withDesc("The speed of the rainbow.")),
				new SettingToggle("FPS", true).withDesc("Shows your FPS."),                                                            // 1
				new SettingToggle("Ping", true).withDesc("Shows your ping."),                                                          // 2
				new SettingToggle("Coords", true).withDesc("Shows your coords and nether coords.").withChildren(                                   // 10
						new SettingToggle("AntiLeak", false).withDesc("Hides coordinates if over 5k incase of bed respawn")),                                    // 3
				new SettingToggle("TPS", true).withDesc("Shows the estimated server tps."),                                            // 4
				new SettingToggle("Durability", false).withDesc("Shows durability left on the item you're holding."),                  // 5
				new SettingToggle("Server", false).withDesc("Shows the current server you are on."),                                   // 6
				new SettingToggle("Timestamp", false).withDesc("Shows the current time.").withChildren(                                // 7
						new SettingToggle("TimeZone", true).withDesc("Shows your time zone in the time."),
						new SettingToggle("Year", false).withDesc("Shows the current year in the time.")),
				new SettingToggle("ChunkSize", false).withDesc("Shows the data size of the chunk you are standing in."),               // 8
				new SettingToggle("Players", false).withDesc("Lists all the players in your render distance."),                        // 9
				new SettingToggle("Armor", true).withDesc("Shows your current armor.").withChildren(                                   // 10
						new SettingToggle("Vertical", false).withDesc("Displays your armor vertically."),
						new SettingMode("Damage", "Number", "Bar", "BarV").withDesc("How to show the armor durability.")    ,                               // 10
						new SettingToggle("Count", false).withDesc("Displays your armor vertically.")),
				new SettingToggle("Lag-Meter", true).withDesc("Shows when the server isn't responding.").withChildren(                 // 11
						new SettingMode("Animation", "Fall", "Fade", "None").withDesc("How to animate the lag meter when appearing.")),
				new SettingToggle("Inventory", false).withDesc("Renders your inventory on screen.").withChildren(                      // 12
						new SettingSlider("Background", 0, 255, 140, 0).withDesc("How opaque the background should be.")),

				new SettingToggle("PotionList", true).withDesc("Shows the potion list.").withChildren(                                 // 0
						new SettingToggle("InnerLine", true).withDesc("Adds an extra line to the front of the module list."),
						new SettingToggle("OuterLine", false).withDesc("Adds an outer line to the module list."),
						new SettingToggle("Fill", true).withDesc("Adds a black fill behind the module list."),         // 0
						new SettingToggle("Alerts", true).withDesc("Adds pop up exclamation marks when low.")));

		UIContainer container = UIClickGuiScreen.INSTANCE.getUIContainer();

		// Modulelist
		container.windows.put("modulelist",
				new UIWindow(new Position("l", 1, "t", 2), container,
						() -> getSetting(0).asToggle().getState(),
						this::getModuleListSize,
						this::drawModuleList)
		);

		// PotionList
		container.windows.put("potionlist",
				new UIWindow(new Position("l", 1, "t", 2), container,
						() -> getSetting(13).asToggle().getState(),
						this::getPotionListSize,
						this::drawPotionList)
		);

		// Info
		container.windows.put("coords",
				new UIWindow(new Position("l", 1, "b", 0), container,
						() -> getSetting(3).asToggle().getState(),
						() -> new int[] { mc.textRenderer.getStringWidth(coordsText) + 2, 10 },
						(x, y) -> mc.textRenderer.method_956(coordsText, x + 1, y + 1, 0xa0a0a0))
				);

		container.windows.put("fps",
				new UIWindow(new Position("l", 1, "coords", 0), container,
						() -> getSetting(1).asToggle().getState(),
						() -> new int[] { mc.textRenderer.getStringWidth(fpsText) + 2, 10 },
						(x, y) -> mc.textRenderer.method_956(fpsText, x + 1, y + 1, 0xa0a0a0))
				);

		container.windows.put("ping",
				new UIWindow(new Position("l", 1, "fps", 0), container,
						() -> getSetting(2).asToggle().getState(),
						() -> new int[] { mc.textRenderer.getStringWidth(pingText) + 2, 10 },
						(x, y) -> mc.textRenderer.method_956(pingText, x + 1, y + 1, 0xa0a0a0))
				);

		container.windows.put("tps",
				new UIWindow(new Position("l", 1, "ping", 0), container,
						() -> getSetting(4).asToggle().getState(),
						() -> new int[] { mc.textRenderer.getStringWidth(tpsText) + 2, 10 },
						(x, y) -> mc.textRenderer.method_956(tpsText, x + 1, y + 1, 0xa0a0a0))
				);

		container.windows.put("durability",
				new UIWindow(new Position(0.2, 0.9), container,
						() -> getSetting(5).asToggle().getState(),
						() -> new int[] { mc.textRenderer.getStringWidth(durabilityText) + 2, 10 },
						(x, y) -> mc.textRenderer.method_956(durabilityText, x + 1, y + 1, 0xa0a0a0))
				);

		container.windows.put("server",
				new UIWindow(new Position(0.2, 0.85, "durability", 0), container,
						() -> getSetting(6).asToggle().getState(),
						() -> new int[] { mc.textRenderer.getStringWidth(serverText) + 2, 10 },
						(x, y) -> mc.textRenderer.method_956(serverText, x + 1, y + 1, 0xa0a0a0))
				);

		container.windows.put("timestamp",
				new UIWindow(new Position(0.2, 0.8, "server", 0), container,
						() -> getSetting(7).asToggle().getState(),
						() -> new int[] { mc.textRenderer.getStringWidth(timestampText) + 2, 10 },
						(x, y) -> mc.textRenderer.method_956(timestampText, x + 1, y + 1, 0xa0a0a0))
				);

		container.windows.put("chunksize",
				new UIWindow(new Position(0.2, 0.75, "timestamp", 0), container,
						() -> getSetting(8).asToggle().getState(),
						() -> new int[] { mc.textRenderer.getStringWidth(chunksizeText) + 2, 10 },
						(x, y) -> mc.textRenderer.method_956(chunksizeText, x + 1, y + 1, 0xa0a0a0))
				);

		// Players
		container.windows.put("players",
				new UIWindow(new Position("l", 1, "modulelist", 2), container,
						() -> getSetting(9).asToggle().getState(),
						this::getPlayerSize,
						this::drawPlayerList)
				);

		// Armor
		container.windows.put("armor",
				new UIWindow(new Position(0.5, 0.85), container,
						() -> getSetting(10).asToggle().getState(),
						this::getArmorSize,
						this::drawArmor)
				);

		// Lag-Meter
		container.windows.put("lagmeter",
				new UIWindow(new Position(0, 0.05, "c", 1), container,
						() -> getSetting(11).asToggle().getState(),
						this::getLagMeterSize,
						this::drawLagMeter)
				);

		// Inventory
		container.windows.put("inventory",
				new UIWindow(new Position(0.7, 0.90), container,
						() -> getSetting(12).asToggle().getState(),
						this::getInventorySize,
						this::drawInventory)
				);
	}

	@Override
	public void onEnable(boolean inWorld) {
		super.onEnable(inWorld);

		chunkExecutor = Executors.newSingleThreadExecutor();
	}

	@Override
	public void onDisable(boolean inWorld) {
		chunkExecutor.shutdownNow();

		super.onDisable(inWorld);
	}

	@BleachSubscribe
	public void onTick(EventTick event) {
		// ModuleList
		moduleListText.clear();

		for (Module m : ModuleManager.getModules())
			if (m.isEnabled())
				moduleListText.add(m.getName());

		moduleListText.sort(Comparator.comparingInt(t -> -mc.textRenderer.getStringWidth(t)));

		if (getSetting(0).asToggle().getChild(3).asToggle().getState()) {
			int watermarkMode = getSetting(0).asToggle().getChild(3).asToggle().getChild(0).asMode().getMode();

			if (watermarkMode == 0) {
				moduleListText.add(0, BleachHack.watermark.getText() + " \u00a77" + BleachHack.VERSION);
			} else {
				moduleListText.add(0, "\u00a7a> BleachHack " + BleachHack.VERSION);
			}
		}

		// FPS
		int fps = ((AccessorMinecraftClient) MinecraftClient.getInstance()).getCurrentFps();
		fpsText = "FPS: \u00a7a" + Integer.toString(fps);

		// Ping
		@SuppressWarnings("unchecked")
		int ping = mc.field_3805.field_1667.field_1618.stream().filter(e -> ((class_482) e).field_1679.equals(mc.getSession().getUsername())).mapToInt(e -> ((class_482) e).field_1680).findFirst().orElse(0);
		pingText = "Ping: \u00a7a" + Integer.toString(ping);

		// Coords
		boolean nether = mc.world.dimension instanceof TheNetherDimension;
		BlockPos pos = new BlockPos(mc.field_3805);
		BlockPos pos2 = nether ? new BlockPos(mc.field_3805.x * 8, mc.field_3805.y, mc.field_3805.z * 8)
				: new BlockPos(mc.field_3805.x * 0.125, mc.field_3805.y, mc.field_3805.z * 0.125);

		coordsText = "XYZ: " +
				(nether ? "\u00a74" : "\u00a7b") + pos.getX() + " " + pos.getY() + " " + pos.getZ() +
				" \u00a77[" +
				(nether ? "\u00a7b" : "\u00a74") + pos2.getX() + " " + pos2.getY() + " " + pos2.getZ()
				+ "\u00a77]";
		if(getSetting(3).asToggle().getChild(0).asToggle().getState()) {
			if(pos.getX() >= 5000 || pos.getZ() >= 5000 || pos.getX() <= -5000 || pos.getZ() <= -5000) {
				coordsText = "XYZ: " +
						(nether ? "\u00a74" : "\u00a7b") + "***,*** " + pos.getY() + " " +
						"***,*** \u00a77[" +
						(nether ? "\u00a7b" : "\u00a74") + "***,*** " + pos2.getY() + " "
						+ "***,***\u00a77]";
			}
		}


		// TPS
		int time = (int) (System.currentTimeMillis() - lastPacket);
		String suffix = time >= 7500 ? "...." : time >= 5000 ? "..." : time >= 2500 ? ".." : time >= 1200 ? ".." : "";

		tpsText = "TPS: \u00a7a" + Double.toString(tps) + suffix;

		// Durability
		ItemStack mainhand = mc.field_3805.getMainHandStack();
		if (mainhand != null && mainhand.isDamageable()) {
			int durability = mainhand.hasTag() && mainhand.getTag().contains("dmg")
					? NumberUtils.toInt(mainhand.getTag().get("dmg").method_1653()) : mainhand.getMaxDamage() - mainhand.getDamage();

			durabilityText = "Durability: \u00a7f" + Integer.toString(durability);
		} else {
			durabilityText = "Durability: \u00a7f--";
		}

		// Server
		String server = ((AccessorMinecraftClient) mc).getCurrentServerEntry() == null ? "Singleplayer" : ((AccessorMinecraftClient) mc).getCurrentServerEntry().address;
		serverText =  "Server: " + Formatting.LIGHT_PURPLE + server;

		// Timestamp
		String timeString = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd HH:mm:ss"
				+ (getSetting(7).asToggle().getChild(0).asToggle().getState() ? " zzz" : "")
				+ (getSetting(7).asToggle().getChild(1).asToggle().getState() ? " yyyy" : "")));

		timestampText = "Time: " + Formatting.YELLOW + timeString;

		// ChunkSize
		if (chunkFuture != null && new ChunkPos((int) mc.field_3805.x >> 4, (int) mc.field_3805.z >> 4).equals(chunkFuture.getLeft())) {
			if (chunkFuture.getRight().isDone()) {
				try {
					chunkSize = chunkFuture.getRight().get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}

				chunkFuture = null;
			}
		} else if (System.currentTimeMillis() - lastChunkTime > 1500 && mc.world.getChunk((int) mc.field_3805.x >> 4, (int) mc.field_3805.z >> 4) != null) {
			lastChunkTime = System.currentTimeMillis();
			chunkFuture = Pair.of(new ChunkPos((int) mc.field_3805.x >> 4, (int) mc.field_3805.z >> 4), chunkExecutor.submit(() -> {
				return 420;
			}));
		}

		chunksizeText = "Chunk: " + (chunkSize < 1000 ? chunkSize + "B" : chunkSize / 1000d + "KB");
	}

	@BleachSubscribe
	public void onDrawOverlay(EventRenderInGameHud event) {
		if (mc.currentScreen instanceof UIClickGuiScreen) {
			return;
		}

		UIContainer container = UIClickGuiScreen.INSTANCE.getUIContainer();
		Window var1 = new Window(mc.options, mc.width, mc.height);
		container.updatePositions((int) var1.getScaledWidth(), (int) var1.getScaledHeight());
		container.render();
	}

	// --- Module List

	public int[] getModuleListSize() {
		if (moduleListText.isEmpty()) {
			return new int[] { 0, 0 };
		}

		int inner = getSetting(0).asToggle().getChild(0).asToggle().getState() ? 1 : 0;
		int outer = getSetting(0).asToggle().getChild(1).asToggle().getState() ? 4 : 3;
		return new int[] { mc.textRenderer.getStringWidth(moduleListText.get(0)) + inner + outer, moduleListText.size() * 10 };
	}

	public void drawModuleList(int x, int y) {
		if (moduleListText.isEmpty()) return;

		int arrayCount = 0;
		boolean inner = getSetting(0).asToggle().getChild(0).asToggle().getState();
		boolean outer = getSetting(0).asToggle().getChild(1).asToggle().getState();
		boolean fill = getSetting(0).asToggle().getChild(2).asToggle().getState();
		boolean rightAlign = x + mc.textRenderer.getStringWidth(moduleListText.get(0)) / 2 > new Window(mc.options, mc.width, mc.height).getScaledWidth() / 2;

		int startX = rightAlign ? x + mc.textRenderer.getStringWidth(moduleListText.get(0)) + 3 + (inner ? 1 : 0) + (outer ? 1 : 0) : x;
		for (String t : moduleListText) {
			int color = getRainbowFromSettings(arrayCount * 40);
			int textStart = (rightAlign ? startX - mc.textRenderer.getStringWidth(t) - 1 : startX + 2) + (inner ? 1 : 0) * (rightAlign ? -1 : 1);
			int outerX = rightAlign ? textStart - 3 : textStart + mc.textRenderer.getStringWidth(t) + 1;

			if (fill) {
				DrawableHelper.fill(rightAlign ? textStart - 2 : startX, y + arrayCount * 10, rightAlign ? startX : outerX, y + 10 + arrayCount * 10, 0x70003030);
			}

			if (inner) {
				DrawableHelper.fill(rightAlign ? startX - 1 : startX, y + arrayCount * 10, rightAlign ? startX : startX + 1, y + 10 + arrayCount * 10, color);
			}

			if (outer) {
				DrawableHelper.fill(outerX, y + arrayCount * 10, outerX + 1, y + 10 + arrayCount * 10, color);
			}

			mc.textRenderer.method_956(t, textStart, y + 1 + arrayCount * 10, color);
			arrayCount++;
		}
	}


	// --- Players

	public int[] getPlayerSize() {
		@SuppressWarnings("unchecked")
		List<Integer> nameLengths = ((List<PlayerEntity>) mc.world.playerEntities).stream()
		.filter(e -> e != mc.field_3805)
		.map(e -> mc.textRenderer.getStringWidth(
				e.getTranslationKey()
				+ " | "
				+ (int) e.x + " " + (int) e.y + " " + (int) e.z
				+ " (" + Math.round(mc.field_3805.squaredDistanceToEntity(e)) + "m)"))
		.collect(Collectors.toList());

		nameLengths.add(mc.textRenderer.getStringWidth("Players:"));
		nameLengths.sort(Comparator.reverseOrder());

		return new int[] { nameLengths.get(0) + 2, nameLengths.size() * 10 + 1 };
	}

	@SuppressWarnings("unchecked")
	public void drawPlayerList(int x, int y) {
		int color = getRainbowFromSettings(40);
		mc.textRenderer.method_956("Players:", x + 1, y + 1, color);

		int count = 1;
		for (Entity e : ((List<PlayerEntity>) mc.world.playerEntities).stream()
				.filter(e -> e != mc.field_3805)
				.sorted(Comparator.comparing(mc.field_3805::squaredDistanceTo))
				.collect(Collectors.toList())) {
			int dist = Math.round(mc.field_3805.squaredDistanceToEntity(e));

			String text =
					e.getTranslationKey()
					+ " \u00a77|\u00a7r "
					+ (int) e.x + " " + (int) e.y + " " + (int) e.z
					+ " (" + dist + "m)";

			int playerColor =
					0xff000000 |
					((255 - (int) Math.min(dist * 2.1, 255) & 0xFF) << 16) |
					(((int) Math.min(dist * 4.28, 255) & 0xFF) << 8);

			mc.textRenderer.method_956(text, x + 1, y + 1 + count * 10, playerColor);
			count++;
		}
	}

	// --- Lag Meter

	public int[] getLagMeterSize() {
		return new int[] { 144, 10 };
	}

	public void drawLagMeter(int x, int y) {
		long time = System.currentTimeMillis();
		if (time - lastPacket > 500) {
			String text = "Server Lagging For: " + String.format(Locale.ENGLISH, "%.2f", (time - lastPacket) / 1000d) + "s";

			int xd = x + 72 - mc.textRenderer.getStringWidth(text) / 2;
			switch (getSetting(11).asToggle().getChild(0).asMode().getMode()) {
				case 0:
					mc.textRenderer.method_956(text, xd, (int) (y + 1 + Math.min((time - lastPacket - 1200) / 20, 0)), 0xd0d0d0);
					break;
				case 1:
					mc.textRenderer.method_956(text, xd, y + 1, (MathHelper.clamp((int) (time - lastPacket - 500) / 3, 5, 255) << 24) | 0xd0d0d0);
					break;
				case 2:
					mc.textRenderer.method_956(text, xd, y + 1, 0xd0d0d0);
			}
		}
	}

	// --- Armor

	public int[] getArmorSize() {
		boolean vertical = getSetting(10).asToggle().getChild(0).asToggle().getState();
		return new int[] { vertical ? 18 : 74, vertical ? 62 : 16 };
	}

	public void drawArmor(int x, int y) {
		boolean vertical = getSetting(10).asToggle().getChild(0).asToggle().getState();

		for (int count = 0; count < mc.field_3805.inventory.armor.length; count++) {
			ItemStack is1 = mc.field_3805.inventory.armor[count];
			int color = getRainbowFromSettings(count * 40);

			if (is1 == null)
				continue;
			ItemStack is = mc.field_3805.inventory.armor[count].copy();
			if(getSetting(10).asToggle().getChild(2).asToggle().getState()) {
				is.count = InventoryUtils.countItem(is.getItem());
			}


			int curX = vertical ? x : x + count * 19;
			int curY = vertical ? y + 47 - count * 16 : y;
			new ItemRenderer().method_5764(mc.textRenderer, mc.getTextureManager(), is, curX, curY);
			GuiLighting.disable();

			int durcolor = is.isDamageable() ? 0xff000000 | hsvToRgb((float) (is.getMaxDamage() - is.getDamage()) / is.getMaxDamage() / 3.0F, 1.0F, 1.0F) : 0;

			if (is.count > 1) {
				String s = Integer.toString(is.count);
				mc.textRenderer.method_956(s, curX + 20 - mc.textRenderer.getStringWidth(s), curY, color);
			}

			if (is.isDamageable()) {
				int mode = getSetting(10).asToggle().getChild(1).asMode().getMode();
				if (mode == 0) {
					String dur = Integer.toString(is.getMaxDamage() - is.getDamage());
					mc.textRenderer.method_956(
							dur, curX + 17 - mc.textRenderer.getStringWidth(dur), curY - (vertical ? 2 : 3), durcolor);
				} else if (mode == 1) {
					int barLength = Math.round(13.0F - is.getDamage() * 13.0F / is.getMaxDamage());
					DrawableHelper.fill(curX + 2, curY + 13, curX + 15, curY + 15, 0xff000000);
					DrawableHelper.fill(curX + 2, curY + 13, curX + 2 + barLength, curY + 14, durcolor);
				} else {
					int barLength = Math.round(12.0F - is.getDamage() * 12.0F / is.getMaxDamage());
					DrawableHelper.fill(curX + 15, curY + 2, curX + 17, curY + 14, 0xff000000);
					DrawableHelper.fill(curX + 15, curY + 2, curX + 16, curY + 2 + barLength, durcolor);
				}
			}
		}
	}

	// --- Inventory

	public int[] getInventorySize() {
		return new int[] { 155, 53 };
	}

	public void drawInventory(int x, int y) {
		if (getSetting(12).asToggle().getState()) {
			DrawableHelper.fill(x + 155, y, x, y + 53,
					(getSetting(12).asToggle().getChild(0).asSlider().getValueInt() << 24) | 0x212120);

			for (int i = 0; i < 27; i++) {
				ItemStack itemStack = mc.field_3805.inventory.getInvStack(i + 9);
				int offsetX = x + 1 + (i % 9) * 17;
				int offsetY = y + 1 + (i / 9) * 17;
				new ItemRenderer().method_5764(mc.textRenderer, mc.getTextureManager(), itemStack, offsetX, offsetY);
				new ItemRenderer().method_1549(mc.textRenderer, mc.getTextureManager(), itemStack, offsetX, offsetY);
				GuiLighting.disable();
			}
		}
	}

	@BleachSubscribe
	public void readPacket(EventPacket.Read event) {
		lastPacket = System.currentTimeMillis();

		if (event.getPacket() instanceof class_720) {
			long time = System.currentTimeMillis();
			long timeOffset = Math.abs(1000 - (time - prevTime)) + 1000;
			tps = Math.round(Math.min(Math.max(20 / (timeOffset / 1000d), 0), 20) * 100d) / 100d;
			prevTime = time;
		}
	}

	public static int getRainbow(float sat, float bri, double speed, int offset) {
		double rainbowState = Math.ceil((System.currentTimeMillis() + offset) / speed) % 360;
		return 0xff000000 | hsvToRgb((float) (rainbowState / 360.0), sat, bri);
	}

	public static int getRainbowFromSettings(int offset) {
		Module ui = ModuleManager.getModule("UI");

		if (ui == null)
			return getRainbow(0.5f, 0.5f, 10, 0);

		return getRainbow(
				ui.getSetting(0).asToggle().getChild(5).asSlider().getValueFloat(),
				ui.getSetting(0).asToggle().getChild(4).asSlider().getValueFloat(),
				ui.getSetting(0).asToggle().getChild(6).asSlider().getValue(),
				offset);
	}

	public static int hsvToRgb(float h, float s, float v) {
		return SettingColor.pack(SettingColor.hsvToRgb(h, s, v));
	}


	// Potion list

	public int[] getPotionListSize() {
		if (potionListText.isEmpty()) {
			return new int[] { 0, 0 };
		}

		int inner = getSetting(13).asToggle().getChild(0).asToggle().getState() ? 1 : 0;
		int outer = getSetting(13).asToggle().getChild(1).asToggle().getState() ? 4 : 3;
		return new int[] { mc.textRenderer.getStringWidth(potionListText.get(0)) + inner + outer, potionListText.size() * 10 };
	}

	public void drawPotionList(int x, int y) {
		potionListText.clear();
		potionEffects.forEach(statusEffect -> {
			try {
				String numeral = "";
				int amplifier = mc.field_3805.getEffectInstance(statusEffect).getAmplifier();
				switch (amplifier) {
					case 0:
						numeral = "I";
						break;
					case 1:
						numeral = "II";
						break;
					case 2:
						numeral = "III";
						break;
					case 3:
						numeral = "IV";
						break;
					case 4:
						numeral = "V";
						break;
					default:
						numeral = String.valueOf(amplifier);
						break;
				}
				int duration = mc.field_3805.getEffectInstance(statusEffect).getDuration() / 20;
				String alert = "";
				if(getSetting(13).asToggle().getChild(3).asToggle().getState()) {
					switch (duration) {
						case 3:
							alert = " !";
							break;
						case 2:
							alert = " ! !";
							break;
						case 1:
							alert = " ! ! !";
							break;
					}
				}
				potionListText.add(statusEffect.getTranslationKey().substring(7) +" "+numeral+" "+getTimeInFormat(duration)+"\u00a74"+alert);
			}
			catch(Exception e) {
				//  Block of code to handle errors
			}
		});
		if (potionListText.isEmpty()) return;
		int arrayCount = 0;
		boolean inner = getSetting(13).asToggle().getChild(0).asToggle().getState();
		boolean outer = getSetting(13).asToggle().getChild(1).asToggle().getState();
		boolean fill = getSetting(13).asToggle().getChild(2).asToggle().getState();
		boolean rightAlign = x + mc.textRenderer.getStringWidth(potionListText.get(0)) / 2 > new Window(mc.options, mc.width, mc.height).getScaledWidth() / 2;

		int startX = rightAlign ? x + mc.textRenderer.getStringWidth(potionListText.get(0)) + 3 + (inner ? 1 : 0) + (outer ? 1 : 0) : x;
		for (String t : potionListText) {
			int color = getRainbowFromSettings(arrayCount * 40);
			int textStart = (rightAlign ? startX - mc.textRenderer.getStringWidth(t) - 1 : startX + 2) + (inner ? 1 : 0) * (rightAlign ? -1 : 1);
			int outerX = rightAlign ? textStart - 3 : textStart + mc.textRenderer.getStringWidth(t) + 1;

			if (fill) {
				DrawableHelper.fill(rightAlign ? textStart - 2 : startX, y + arrayCount * 10, rightAlign ? startX : outerX, y + 10 + arrayCount * 10, 0x70003030);
			}

			if (inner) {
				DrawableHelper.fill(rightAlign ? startX - 1 : startX, y + arrayCount * 10, rightAlign ? startX : startX + 1, y + 10 + arrayCount * 10, color);
			}

			if (outer) {
				DrawableHelper.fill(outerX, y + arrayCount * 10, outerX + 1, y + 10 + arrayCount * 10, color);
			}

			mc.textRenderer.method_956(t, textStart, y + 1 + arrayCount * 10, color);
			arrayCount++;
		}
	}

	public String getTimeInFormat(long _SECONDS)
	{
		if(TimeUnit.SECONDS.toHours(_SECONDS)>0)
		{
			return  String.format("%02d:%02d:%02d",
					TimeUnit.SECONDS.toHours(_SECONDS),
					TimeUnit.SECONDS.toMinutes(_SECONDS) -
							TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(_SECONDS)),
					TimeUnit.SECONDS.toSeconds(_SECONDS) -
							TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(_SECONDS)));
		}
		else {
			return  String.format("%02d:%02d",
					TimeUnit.SECONDS.toMinutes(_SECONDS) -
							TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(_SECONDS)),
					TimeUnit.SECONDS.toSeconds(_SECONDS) -
							TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(_SECONDS)));
		}

	}
}
