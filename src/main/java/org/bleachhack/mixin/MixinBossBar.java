/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.BossBarProvider;

@Mixin(BossBar.class)
public class MixinBossBar {
	
	@Overwrite
	public static void update(BossBarProvider provider, boolean darkenSky) {
	      BossBar.percent = provider.getHealth() / provider.getMaxHealth();
	      BossBar.framesToLive = 100;
	      BossBar.name = ((Entity) provider).getLocalisationKey();
	      BossBar.darkenSky = darkenSky;
	}
}
