package thebetweenlands.common.registries;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

public class ItemColorRegistry implements ItemColor {

	public static final ItemColor INSTANCE = new ItemColorRegistry();
	
	@Override
	public int getColor(ItemStack p_92672_, int p_92673_) {
		return 0x8EB971;
	}
	
	public static void register() {
		Minecraft.getInstance().getItemColors().register(INSTANCE, BlockRegistry.LEAVES_WEEDWOOD_TREE.get());
		Minecraft.getInstance().getItemColors().register(INSTANCE, BlockRegistry.LEAVES_RUBBER_TREE.get());
    	Minecraft.getInstance().getItemColors().register(INSTANCE, BlockRegistry.SWAMP_GRASS.get());
    	Minecraft.getInstance().getItemColors().register(INSTANCE, BlockRegistry.POISON_IVY.get());
    	Minecraft.getInstance().getItemColors().register(INSTANCE, BlockRegistry.MOSS.get());
		Minecraft.getInstance().getItemColors().register(INSTANCE, BlockRegistry.HANGER.get());
		Minecraft.getInstance().getItemColors().register(INSTANCE, BlockRegistry.SWAMP_TALLGRASS.get());
    }
}
