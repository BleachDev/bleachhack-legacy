/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.mixin;

import net.minecraft.world.BlockView;
import org.bleachhack.module.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.block.AbstractFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@Mixin(AbstractFluidBlock.class)
public class MixinAbstractFluidBlock {
	
	@Shadow private Vec3d method_333(BlockView worldView, int i, int j, int k) { return null; }

	@Overwrite
	public void method_418(World world, int i, int j, int k, Entity entity, Vec3d vec3d) {
		if (ModuleManager.getModule("NoVelocity").isEnabled())
			return;
		
		Vec3d var7 = this.method_333(world, i, j, k);
		vec3d.x += var7.x;
		vec3d.y += var7.y;
		vec3d.z += var7.z;
	}

}
