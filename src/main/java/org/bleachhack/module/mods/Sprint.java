package org.bleachhack.module.mods;

import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;
import org.bleachhack.module.ModuleManager;
import org.bleachhack.setting.module.SettingToggle;

public class Sprint extends Module {

    public Sprint(){

        super("Sprint", KEY_UNBOUND, ModuleCategory.MOVEMENT, "Automatically Toggles Sprint",
                new SettingToggle("Hunger Check", true).withDesc("Checks if you can actually sprint (unfucks running)")
        );

    }

    @BleachSubscribe
    public void onTick(EventTick event) {

        if(mc.field_3805.getHungerManager().getFoodLevel() <= 6 && getSetting(0).asToggle().getState()){ //checks hungerlevel so it stops sprinting if under 3 bars of hunger
            return;
        }

        if(!mc.field_3805.isSprinting() && !mc.field_3805.isSneaking() && mc.field_3805.forwardSpeed > 0.0f){
            mc.field_3805.setSprinting(true);
        }

    }
}
