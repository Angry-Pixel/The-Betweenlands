package thebetweenlands.common.item.misc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemRope extends Item {
	public ItemRope() {
		this.setCreativeTab(BLCreativeTabs.ITEMS);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		if(worldIn.getBlockState(pos).getBlock() == BlockRegistry.ROPE || worldIn.getBlockState(pos.down()).getBlock() == BlockRegistry.ROPE) {
			BlockPos offsetPos = pos.down();

			if(worldIn.getBlockState(pos).getBlock() != BlockRegistry.ROPE) {
				offsetPos = offsetPos.down();
			}

			while(worldIn.getBlockState(offsetPos).getBlock() == BlockRegistry.ROPE) {
				offsetPos = offsetPos.down();
			}

			if(playerIn.canPlayerEdit(offsetPos, facing, stack) && BlockRegistry.ROPE.canPlaceBlockAt(worldIn, offsetPos)) {
				if(!worldIn.isRemote) {
					worldIn.setBlockState(offsetPos, BlockRegistry.ROPE.getDefaultState());

					if(!playerIn.isCreative()) {
						stack.shrink(1);
					}
				}

				return EnumActionResult.SUCCESS;
			}
		} else if(BlockRegistry.ROPE.canPlaceBlockAt(worldIn, pos.down())) {
			if(!worldIn.isRemote) {
				worldIn.setBlockState(pos.down(), BlockRegistry.ROPE.getDefaultState());

				if(!playerIn.isCreative()) {
					stack.shrink(1);
				}
			}

			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}
}
