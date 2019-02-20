package thebetweenlands.common.item;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class BLItemGroups {
	public static final ItemGroup BLOCKS = new ItemGroup("thebetweenlands.block") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.STONE);
		}
	};
	public static final ItemGroup ITEMS = new ItemGroup("thebetweenlands.item") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.STONE);
		}
	};
	public static final ItemGroup GEAR = new ItemGroup("thebetweenlands.gear") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.STONE);
		}
	};
	public static final ItemGroup SPECIALS = new ItemGroup("thebetweenlands.special") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.STONE);
		}
	};
	public static final ItemGroup PLANTS = new ItemGroup("thebetweenlands.plants") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.STONE);
		}
	};
	public static final ItemGroup HERBLORE = new ItemGroup("thebetweenlands.herblore") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.STONE);
		}
	};
}
