package thebetweenlands.items.crops;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class ItemAspectrusCropSeed extends ItemBLGenericSeed {
	public ItemAspectrusCropSeed(int healAmount, float saturation) {
		super(healAmount, saturation, BLBlockRegistry.aspectrusCrop, BLBlockRegistry.farmedDirt);
		this.setCreativeTab(ModCreativeTabs.plants);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (side != 1)
			return false;
		else if (player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x, y + 1, z, side, stack)) {
			int meta = world.getBlockMetadata(x, y, z);
			if (world.getBlock(x, y, z) == this.soilId && meta >= 4 && meta <= 10 && meta != 7 && meta != 8 && world.getBlock(x, y + 1, z) == BLBlockRegistry.rubberTreePlankFence) {
				world.setBlock(x, y + 1, z, this.cropId);
				--stack.stackSize;
				return true;
			} else
				return false;
		} else
			return false;
	}
}
