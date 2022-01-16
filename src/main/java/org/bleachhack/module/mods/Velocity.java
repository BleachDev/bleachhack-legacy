package org.bleachhack.module.mods;

import net.minecraft.class_667;
import net.minecraft.class_714;
import org.bleachhack.event.events.EventPacket;
import org.bleachhack.eventbus.BleachSubscribe;
import org.bleachhack.module.Module;
import org.bleachhack.module.ModuleCategory;


public class Velocity extends Module {

    public Velocity(){

        super("Velocity", KEY_UNBOUND, ModuleCategory.PLAYER, "No Velocity");

    }

    @BleachSubscribe
    public void EventPacket(EventPacket.Read event) {
        if (mc.field_3805 != null || mc.world != null) {
            if (event.getPacket() instanceof class_714) { //class_714 velocity
                class_714 packet = (class_714) event.getPacket();
                if (packet.id == mc.field_3805.field_3243) { //field_3243 = the entity id of the player (cringe larp aids) basically only cancels ur velocity
                    event.setCancelled(true);
                }
            }
            if (event.getPacket() instanceof class_667) {
                class_667 packet = (class_667) event.getPacket(); //class_667 = ExplosionPacket
                event.setCancelled(true);

            }
        }
    }
}



