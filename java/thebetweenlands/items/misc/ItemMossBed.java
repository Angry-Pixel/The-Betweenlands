package thebetweenlands.items.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BlockMossBed;
import thebetweenlands.creativetabs.ModCreativeTabs;

public class ItemMossBed extends Item {

	public ItemMossBed() {
		setCreativeTab(ModCreativeTabs.items);
	}

	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

		if (world.isRemote)
			return true;
		else if (side != 1)
			return false;
		else {
			++y;
			BlockMossBed blockMossBed = (BlockMossBed) BLBlockRegistry.mossBed;
			int rotation = MathHelper .floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			byte offSetX = 0;
			byte offSetZ = 0;

			if (rotation == 0)
				offSetZ = 1;

			if (rotation == 1)
				offSetX = -1;

			if (rotation == 2)
				offSetZ = -1;

			if (rotation == 3)
				offSetX = 1;

			if (player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x + offSetX, y, z + offSetZ, side, stack)) {
				if (world.isAirBlock(x, y, z) && world.isAirBlock(x + offSetX, y, z + offSetZ) && World.doesBlockHaveSolidTopSurface(world, x, y - 1, z) && World.doesBlockHaveSolidTopSurface(world, x + offSetX, y - 1, z + offSetZ)) {
					world.setBlock(x, y, z, blockMossBed, rotation, 3);
					if (world.getBlock(x, y, z) == blockMossBed)
						world.setBlock(x + offSetX, y, z + offSetZ, blockMossBed, rotation + 8, 3);
					--stack.stackSize;
					return true;
				} else
					return false;
			} else {
				return false;
			}
		}
	}
}
