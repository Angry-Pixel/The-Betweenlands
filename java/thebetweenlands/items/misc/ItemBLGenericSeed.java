package thebetweenlands.items.misc;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class ItemBLGenericSeed extends ItemSeedFood implements IPlantable {

	private Block soilId;
	private Block cropId;

	public ItemBLGenericSeed(int healAmount, float saturation, Block cropBlock, Block soilBlock) {
		super(healAmount, saturation, cropBlock, soilBlock);
        cropId = cropBlock;
        soilId = soilBlock;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		if (side != 1)
			return false;
		else if (player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x, y + 1, z, side, stack)) {
			int meta = world.getBlockMetadata(x, y, z);
			if (world.getBlock(x, y, z) == soilId && meta >= 4 && meta <= 10 && meta != 7 && meta != 8 && world.isAirBlock(x, y + 1, z)) {
				world.setBlock(x, y + 1, z, cropId);
				--stack.stackSize;
				return true;
			} else
				return false;
		} else
			return false;
	}
}
