package thebetweenlands.common.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import thebetweenlands.common.TheBetweenlands;

public class TagRegistry {
	
	// Item tags
	public static class Items {
		// registers under the mod namespace
		public static TagKey<Item> tag(String tagName) {
			return ItemTags.create(new ResourceLocation(TheBetweenlands.ID, tagName));
		}
		
		// registers under the forge namespace
		public static TagKey<Item> forgeTag(String tagName) {
			return ItemTags.create(new ResourceLocation("forge", tagName));
		}
	}
	
	// Block tags
	public static class Blocks {
		public static final TagKey<Block> BETWEENSTONE_ORE_REPLACEABLE = tag("betweenstone_ore_replaceable");
		public static final TagKey<Block> PITSTONE_ORE_REPLACEABLE = tag("pitstone_ore_replaceable");
		
		//public static final TagKey<Block> 
		
		// registers under the mod namespace
		public static TagKey<Block> tag(String tagName) {
			return BlockTags.create(new ResourceLocation(tagName));
		}
		
		// registers under the forge namespace
		public static TagKey<Block> forgeTag(String tagName) {
			return BlockTags.create(new ResourceLocation("forge", tagName));
		}
	}
	
	
	// Fluid tags
	public static class Fluids {
		public static final TagKey<Fluid> BETWEENLANDS_DECAY_FLUID = tag("betweenlands_decay_fluid");	// fluids that apply decay (calls the fluid's decay multiplyer) 'double double comment'-> (im changing the stagnet water from applying an effect to using a multiplyer value)
		public static final TagKey<Fluid> SWAMP_WATER = tag("betweenlands_swamp_water");	// general tag for betweenlands water (if there is a need for more)
		
		// registers under the mod namespace
		public static TagKey<Fluid> tag(String tagName) {
			return FluidTags.create(new ResourceLocation(TheBetweenlands.ID, tagName));
		}
		
		// registers under the forge namespace
		public static TagKey<Fluid> forgeTag(String tagName) {
			return FluidTags.create(new ResourceLocation("forge", tagName));
		}
	}
}
