package thebetweenlands.common.item.misc;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemMossBed extends Item {
	public ItemMossBed() {
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) {
			return EnumActionResult.SUCCESS;
		} else if (facing != EnumFacing.UP) {
			return EnumActionResult.FAIL;
		} else {
			IBlockState state = worldIn.getBlockState(pos);
			Block block = state.getBlock();
			boolean isReplaceable = block.isReplaceable(worldIn, pos);

			if (!isReplaceable) {
				pos = pos.up();
			}

			int rot = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			EnumFacing placementFacing = EnumFacing.getHorizontal(rot);
			BlockPos headPos = pos.offset(placementFacing);
			ItemStack stack = player.getHeldItem(hand);

			if (player.canPlayerEdit(pos, facing, stack) && player.canPlayerEdit(headPos, facing, stack)) {
				IBlockState headState = worldIn.getBlockState(headPos);
				boolean isHeadPosReplaceable = headState.getBlock().isReplaceable(worldIn, headPos);
				boolean isFootPlaceable = isReplaceable || worldIn.isAirBlock(pos);
				boolean isHeadPlaceable = isHeadPosReplaceable || worldIn.isAirBlock(headPos);

				if (isFootPlaceable && isHeadPlaceable && worldIn.getBlockState(pos.down()).isTopSolid() && worldIn.getBlockState(headPos.down()).isTopSolid()) {
					IBlockState bedState = BlockRegistry.MOSS_BED.getDefaultState().withProperty(BlockBed.OCCUPIED, Boolean.valueOf(false)).withProperty(BlockBed.FACING, placementFacing).withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT);

					if(worldIn.setBlockState(pos, bedState, 10)) {
						worldIn.setBlockState(headPos, bedState.withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD), 10);
					}

					SoundType sound = bedState.getBlock().getSoundType(bedState, worldIn, pos, player);
					worldIn.playSound((EntityPlayer)null, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

					worldIn.notifyNeighborsRespectDebug(pos, block, false);
					worldIn.notifyNeighborsRespectDebug(headPos, headState.getBlock(), false);

					if (player instanceof EntityPlayerMP) {
						CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
					}

					stack.shrink(1);
					return EnumActionResult.SUCCESS;
				} else {
					return EnumActionResult.FAIL;
				}
			} else {
				return EnumActionResult.FAIL;
			}
		}
	}
}