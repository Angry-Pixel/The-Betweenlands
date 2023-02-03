package thebetweenlands.common.item.food;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.farming.BlockDugDirt;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemPuffshroomTendril extends ItemBLFood {

	public ItemPuffshroomTendril() {
		super(6, 0.6F, false);
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack held = player.getHeldItem(hand);
		if (!world.isRemote && !held.isEmpty() && held.getItem() == this) {
			IBlockState iblockstate = world.getBlockState(pos);
			Block block = iblockstate.getBlock();
			if (block instanceof BlockDugDirt) {
				world.setBlockState(pos.up(), BlockRegistry.WEEDWOOD_BUSH.getDefaultState(), 2); //TEST until new thing exists
				held.shrink(1);
				return EnumActionResult.SUCCESS;
			} else
				return EnumActionResult.FAIL;
		}
		player.swingArm(hand);
		return EnumActionResult.PASS;
	}
}
