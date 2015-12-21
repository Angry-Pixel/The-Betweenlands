package thebetweenlands.items.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.manual.IManualEntryItem;

public class ItemRope extends Item implements IManualEntryItem {
	public ItemRope() {
		this.setUnlocalizedName("thebetweenlands.itemRope");
		setTextureName("thebetweenlands:rope");
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (world.getBlock(x, y, z) == BLBlockRegistry.rope) {
			while (world.getBlock(x, y, z) == BLBlockRegistry.rope) {
				y--;
			}
			if (world.getBlock(x, y, z) == Blocks.air && player.canPlayerEdit(x, y, z, side, stack)) {
				if(!world.isRemote) {
					world.setBlock(x, y, z, BLBlockRegistry.rope);
					stack.stackSize--;
				}
				return true;
			}
		} else if (world.getBlock(x, y, z).isBlockSolid(world, x, y, z, side) && world.getBlock(x, y - 1, z) == Blocks.air) {
			if(!world.isRemote) {
				world.setBlock(x, y - 1, z, BLBlockRegistry.rope);
				stack.stackSize--;
			}
			return true;
		}
		return false;
	}

	@Override
	public String manualName(int meta) {
		return "rope";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[]{2};
	}

	@Override
	public int metas() {
		return 0;
	}
}
