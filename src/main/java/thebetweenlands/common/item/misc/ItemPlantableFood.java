package thebetweenlands.common.item.misc;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.common.item.food.ItemBLFood;


public abstract class ItemPlantableFood extends ItemBLFood {
	public ItemPlantableFood(int healAmount, float saturation) {
		super(healAmount, saturation, false);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		Block block = worldIn.getBlockState(pos).getBlock();
		boolean isReplacing = block.isReplaceable(worldIn, pos);
		if (isReplacing || (facing == EnumFacing.UP && worldIn.isAirBlock(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ())))) {
			BlockPos newPos = new BlockPos(pos.getX(), pos.getY() + (isReplacing ? 0 : 1), pos.getZ());
			block = worldIn.getBlockState(newPos).getBlock();
			Block placeBlock = this.getBlock(stack, playerIn, worldIn, newPos);
			if (block != placeBlock && placeBlock.canPlaceBlockAt(worldIn, new BlockPos(newPos))) {
				if (!worldIn.isRemote) {
					worldIn.setBlockState(newPos, this.getBlockState(placeBlock, stack, playerIn, worldIn, newPos));
					worldIn.playSound((EntityPlayer)null, (float)pos.getX() + 0.5F, (float)pos.getY() + 0.5F, (float)pos.getZ() + 0.5F, placeBlock.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, (placeBlock.getSoundType().getVolume() + 1.0F) / 2.0F, placeBlock.getSoundType().getPitch() * 0.8F);
					--stack.stackSize;
				}
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.FAIL;
	}

	protected abstract Block getBlock(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos);

	protected IBlockState getBlockState(Block block, ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos) {
		return block.getDefaultState();
	}
}
