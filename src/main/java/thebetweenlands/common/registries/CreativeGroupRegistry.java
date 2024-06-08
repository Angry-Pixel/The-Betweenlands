package thebetweenlands.common.registries;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class CreativeGroupRegistry {
	
	// Blocks tab
	public static final CreativeModeTab BETWEELANDS_BLOCKS = new CreativeModeTab("betweelands_blocks_tab") {
		
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(BlockRegistry.SWAMP_GRASS.get());
		}
	};

	// Plants tab
	public static final CreativeModeTab BETWEELANDS_PLANTS = new CreativeModeTab("betweelands_plants_tab") {
		
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ItemRegistry.WEEDWOOD_SAPLING.get());
		}
	};

	public static final CreativeModeTab BETWEELANDS_SPECIAL = new CreativeModeTab("betweelands_special_tab") {

		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ItemRegistry.RECORD_ASTATOS.get());
		}
	};
}
