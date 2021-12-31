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
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.widget.TextFieldWidget;

@Mixin(TextFieldWidget.class)
public interface AccessorTextFieldWidget {
	
	@Accessor
	public abstract int getX();
	
	@Accessor
	public abstract int getY();

	@Accessor @Mutable
	public abstract void setX(int x);
	
	@Accessor @Mutable
	public abstract void setY(int y);
	
	@Accessor @Mutable
	public abstract void setWidth(int w);
}
