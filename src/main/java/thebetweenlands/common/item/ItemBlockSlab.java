package thebetweenlands.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.structure.BlockSlabBetweenlands;

public class ItemBlockSlab extends ItemBlock {
	public ItemBlockSlab(BlockSlabBetweenlands block) {
		super(block);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		BlockSlabBetweenlands slab = (BlockSlabBetweenlands) getBlock();
		ItemStack itemstack = player.getHeldItem(hand);

		if (!itemstack.isEmpty() && player.canPlayerEdit(pos.offset(facing), facing, itemstack)) {

			IBlockState blockState = worldIn.getBlockState(pos);
			if (blockState.getBlock() == slab) {

				BlockSlabBetweenlands.EnumBlockHalfBL type = blockState.getValue(BlockSlabBetweenlands.HALF);
				if ((facing == EnumFacing.UP && type.equals(BlockSlabBetweenlands.EnumBlockHalfBL.BOTTOM) || facing == EnumFacing.DOWN && type.equals(BlockSlabBetweenlands.EnumBlockHalfBL.TOP))) {
					IBlockState newState = blockState.withProperty(BlockSlabBetweenlands.HALF, BlockSlabBetweenlands.EnumBlockHalfBL.FULL);
					AxisAlignedBB axisalignedbb = newState.getCollisionBoundingBox(worldIn, pos);

					if (axisalignedbb != Block.NULL_AABB && worldIn.checkNoEntityCollision(axisalignedbb.offset(pos)) && worldIn.setBlockState(pos, newState, 11)) {
						SoundType soundtype = slab.getSoundType(newState, worldIn, pos, player);
						worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
						itemstack.shrink(1);

						if (player instanceof EntityPlayerMP) {
							CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, itemstack);
						}
					}

					return EnumActionResult.SUCCESS;
				}
			}

			return this.tryPlace(player, itemstack, worldIn, pos.offset(facing)) ? EnumActionResult.SUCCESS : super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
		} else {
			return EnumActionResult.FAIL;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
		BlockPos blockpos = pos;
		IBlockState blockState = worldIn.getBlockState(pos);

		if (blockState.getBlock() == getBlock()) {
			boolean flag = blockState.getValue(BlockSlabBetweenlands.HALF) == BlockSlabBetweenlands.EnumBlockHalfBL.TOP;

			if ((side == EnumFacing.UP && !flag || side == EnumFacing.DOWN && flag)) {
				return true;
			}
		}

		pos = pos.offset(side);
		blockState = worldIn.getBlockState(pos);
		return blockState.getBlock() == getBlock() || super.canPlaceBlockOnSide(worldIn, blockpos, side, player, stack);
	}

	private boolean tryPlace(EntityPlayer player, ItemStack stack, World worldIn, BlockPos pos) {
		IBlockState blockState = worldIn.getBlockState(pos);

		if (blockState.getBlock() == getBlock()) {
			IBlockState newState = blockState.withProperty(BlockSlabBetweenlands.HALF, BlockSlabBetweenlands.EnumBlockHalfBL.FULL);
			AxisAlignedBB axisalignedbb = newState.getCollisionBoundingBox(worldIn, pos);

			if (axisalignedbb != Block.NULL_AABB && worldIn.checkNoEntityCollision(axisalignedbb.offset(pos)) && worldIn.setBlockState(pos, newState, 11)) {
				SoundType soundtype = getBlock().getSoundType(newState, worldIn, pos, player);
				worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				stack.shrink(1);
			}

			return true;
		}

		return false;
	}
}