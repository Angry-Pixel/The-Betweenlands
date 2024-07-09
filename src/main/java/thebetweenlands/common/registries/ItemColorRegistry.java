package thebetweenlands.common.registries;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public class ItemColorRegistry implements ItemColor {

	public static final ItemColor INSTANCE = new ItemColorRegistry();

	@Override
	public int getColor(ItemStack p_92672_, int p_92673_) {
		return 0x8EB971;
	}
	
	// TODO Add to event bus
	@SubscribeEvent
	public static void onItemColorRegister(RegisterColorHandlersEvent.Item event) {
		event.register(INSTANCE, BlockRegistry.LEAVES_WEEDWOOD_TREE.get());
		event.register(INSTANCE, BlockRegistry.LEAVES_RUBBER_TREE.get());
		event.register(INSTANCE, BlockRegistry.SWAMP_GRASS.get());
		event.register(INSTANCE, BlockRegistry.POISON_IVY.get());
		event.register(INSTANCE, BlockRegistry.MOSS.get());
		event.register(INSTANCE, BlockRegistry.HANGER.get());
		event.register(INSTANCE, BlockRegistry.SWAMP_TALLGRASS.get());
	}
}
