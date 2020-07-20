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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.block.container.BlockRuneWeavingTable;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemRuneWeavingTable extends ItemBlock {
	public ItemRuneWeavingTable() {
		super(BlockRegistry.RUNE_WEAVING_TABLE);
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
			boolean replaceable = block.isReplaceable(worldIn, pos);

			if(!replaceable) {
				pos = pos.up();
			}

			int rotation = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			EnumFacing placementFacing = EnumFacing.byHorizontalIndex(rotation);
			BlockPos secondPosition = pos.offset(placementFacing.rotateY());
			ItemStack stack = player.getHeldItem(hand);

			if(player.canPlayerEdit(pos, facing, stack) && player.canPlayerEdit(secondPosition, facing, stack)) {
				IBlockState secondState = worldIn.getBlockState(secondPosition);
				boolean secondReplaceable = secondState.getBlock().isReplaceable(worldIn, secondPosition);
				boolean placeable = replaceable || worldIn.isAirBlock(pos);
				boolean secondPlaceable = secondReplaceable || worldIn.isAirBlock(secondPosition);

				if(placeable && secondPlaceable && worldIn.getBlockState(pos.down()).isTopSolid() && worldIn.getBlockState(secondPosition.down()).isTopSolid()) {
					IBlockState placedState = BlockRegistry.RUNE_WEAVING_TABLE.getDefaultState().withProperty(BlockRuneWeavingTable.FACING, placementFacing).withProperty(BlockRuneWeavingTable.PART, BlockRuneWeavingTable.EnumPartType.MAIN);

					worldIn.setBlockState(pos, placedState, 10);
					worldIn.setBlockState(secondPosition, placedState.withProperty(BlockRuneWeavingTable.PART, BlockRuneWeavingTable.EnumPartType.FILLER), 10);

					SoundType soundtype = placedState.getBlock().getSoundType(placedState, worldIn, pos, player);
					worldIn.playSound((EntityPlayer)null, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);

					worldIn.notifyNeighborsRespectDebug(pos, block, false);
					worldIn.notifyNeighborsRespectDebug(secondPosition, secondState.getBlock(), false);

					if(player instanceof EntityPlayerMP) {
						CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)player, pos, stack);
					}

					stack.shrink(1);
					return EnumActionResult.SUCCESS;
				}
				else
				{
					return EnumActionResult.FAIL;
				}
			}
			else
			{
				return EnumActionResult.FAIL;
			}
		}
	}
}