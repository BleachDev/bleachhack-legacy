/*
 * This file is part of the BleachHack distribution (https://github.com/BleachDrinker420/BleachHack/).
 * Copyright (c) 2021 Bleach and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package org.bleachhack.module.mods;

import net.minecraft.block.Block;
import net.minecraft.class_235;
import net.minecraft.util.hit.HitResult;
import org.bleachhack.event.events.EventRenderBlockOutline;
import org.bleachhack.event.events.EventWorldRender;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.setting.module.SettingColor;
import org.bleachhack.setting.module.SettingSlider;
import org.bleachhack.setting.module.SettingToggle;
import org.bleachhack.util.render.Renderer;
import org.bleachhack.util.render.color.QuadColor;

public class BlockHighlight extends Module {

    public BlockHighlight() {
        super("BlockHighlight", KEY_UNBOUND, ModuleCategory.RENDER, "Highlights block you are looking at.",
                new SettingToggle("Fill", true).withDesc("Draws filling.").withChildren(
                        new SettingColor(
                                "Color", 255, 255, 255
                        ).withDesc("Filling color."),
                        new SettingSlider(
                                "Alpha", 0, 255, 120, 0
                        ).withDesc("Filling alpha.")
                ),
                new SettingToggle("Outline", true).withDesc("Draws outline.").withChildren(
                        new SettingColor(
                                "Color", 255, 255, 255
                        ).withDesc("Outline color."),
                        new SettingSlider(
                                "Alpha", 0, 255, 120, 0
                        ).withDesc("Outline alpha."),
                        new SettingSlider(
                                "Width", 0.1, 3, 1, 1
                        ).withDesc("Outline width.")
                ));
    }

    @BleachSubscribe
    public void onWorldRender(EventWorldRender.Post event) {
        if(mc.result != null && mc.result.type == class_235.field_602) {
            HitResult result = mc.result;
            // Definitely not pasted from minecraft
            int var6 = mc.world.method_3774(result.x, result.y, result.z);

            if(getSetting(0).asToggle().getState() && getSetting(0).asToggle().getChild(1).asSlider().getValueInt() != 0) {
                int[] color = getSetting(0).asToggle().getChild(0).asColor().getRGBArray();
                Renderer.drawBoxFill(
                        Block.field_492[ var6 ].method_427(mc.world, result.x, result.y, result.z),
                        QuadColor.single(
                                color[0], color[1], color[2],
                                getSetting(0).asToggle().getChild(1).asSlider().getValueInt()
                        )
                );
            }

            if(getSetting(1).asToggle().getState() && getSetting(1).asToggle().getChild(1).asSlider().getValueInt() != 0) {
                int[] color = getSetting(1).asToggle().getChild(0).asColor().getRGBArray();
                Renderer.drawBoxOutline(
                        Block.field_492[ var6 ].method_427(mc.world, result.x, result.y, result.z),
                        QuadColor.single(
                                color[0], color[1], color[2],
                                getSetting(1).asToggle().getChild(1).asSlider().getValueInt()
                        ),
                        getSetting(1).asToggle().getChild(2).asSlider().getValueFloat()
                );
            }

        }
    }

    @BleachSubscribe public void onOutlineRender(EventRenderBlockOutline event) {
        event.setCancelled(true);
    }

}
