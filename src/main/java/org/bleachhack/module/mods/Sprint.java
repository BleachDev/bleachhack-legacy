package org.bleachhack.module.mods;

import org.bleachhack.event.events.EventTick;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;

public class Sprint extends Module {

    public Sprint(){

        super("Sprint", KEY_UNBOUND, ModuleCategory.MOVEMENT, "Automatically Toggles Sprint");

    }

    @BleachSubscribe
    public void onTick(EventTick event) {
        if(!mc.field_3805.isSprinting() && !mc.field_3805.isSneaking() && mc.field_3805.forwardSpeed > 0.0f){
            mc.field_3805.setSprinting(true);
        }

    }
}
