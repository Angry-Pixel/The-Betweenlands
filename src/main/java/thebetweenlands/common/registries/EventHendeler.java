package thebetweenlands.common.registries;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import thebetweenlands.common.entitys.EntitySwampHag;

@Mod.EventBusSubscriber(modid = "thebetweenlands", bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHendeler {

	@SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
		event.put(EntityRegistry.SWAMP_HAG.get(), EntitySwampHag.createMobAttributes().build());
        event.put(EntityRegistry.GECKO.get(), EntitySwampHag.createMobAttributes().build());
        event.put(EntityRegistry.WIGHT.get(), EntitySwampHag.createMobAttributes().build());
    }


}
