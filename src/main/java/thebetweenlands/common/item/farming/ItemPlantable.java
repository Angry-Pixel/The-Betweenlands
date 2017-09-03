package thebetweenlands.common.item.farming;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public abstract class ItemPlantable extends Item {
	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		Block block = worldIn.getBlockState(pos).getBlock();
		boolean isReplacing = block.isReplaceable(worldIn, pos);
		BlockPos facingOffset = pos.offset(facing);
		if (isReplacing || (worldIn.isAirBlock(facingOffset) || worldIn.getBlockState(facingOffset).getBlock().isReplaceable(worldIn, facingOffset))) {
			BlockPos newPos = isReplacing ? pos : facingOffset;
			block = worldIn.getBlockState(newPos).getBlock();
			Block placeBlock = this.getBlock(stack, playerIn, worldIn, newPos);
			if (placeBlock != null && block != placeBlock && placeBlock.canPlaceBlockAt(worldIn, newPos)) {
				if (!worldIn.isRemote) {
					worldIn.setBlockState(newPos, this.getBlockState(placeBlock, stack, playerIn, worldIn, newPos));
					worldIn.playSound((EntityPlayer)null, (float)pos.getX() + 0.5F, (float)pos.getY() + 0.5F, (float)pos.getZ() + 0.5F, placeBlock.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (placeBlock.getSoundType().getVolume() + 1.0F) / 2.0F, placeBlock.getSoundType().getPitch() * 0.8F);
					stack.shrink(1);
				}
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.FAIL;
	}

	@Nullable
	protected abstract Block getBlock(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos);

	protected IBlockState getBlockState(Block block, ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos) {
		return block.getDefaultState();
	}
}
