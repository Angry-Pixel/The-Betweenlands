package thebetweenlands.common.item.misc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
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

	@SuppressWarnings("deprecation")
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return EnumActionResult.SUCCESS;
		} else if (facing != EnumFacing.UP) {
			return EnumActionResult.FAIL;
		} else {
			IBlockState headBlockState = world.getBlockState(pos);
			Block headBlock = headBlockState.getBlock();
			boolean isBlockReplaceable = headBlock.isReplaceable(world, pos);

			if (!isBlockReplaceable) {
				pos = pos.up();
			}

			int rotation = MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			EnumFacing playerFacing = EnumFacing.getHorizontal(rotation);
			BlockPos offsetPos = pos.offset(playerFacing);

			ItemStack stack = player.getHeldItem(hand);
			if (player.canPlayerEdit(pos, facing, stack) && player.canPlayerEdit(offsetPos, facing, stack)) {
				boolean isFootBlockReplaceable = world.getBlockState(offsetPos).getBlock().isReplaceable(world, offsetPos);
				boolean canPlaceHead = isBlockReplaceable || world.isAirBlock(pos);
				boolean canPlaceFoot = isFootBlockReplaceable || world.isAirBlock(offsetPos);
				
				if (canPlaceHead && canPlaceFoot && world.getBlockState(pos.down()).isTopSolid() && world.getBlockState(offsetPos.down()).isTopSolid()) {
					IBlockState placedFootBlockState = BlockRegistry.MOSS_BED.getDefaultState().withProperty(BlockBed.OCCUPIED, Boolean.valueOf(false)).withProperty(BlockBed.FACING, playerFacing).withProperty(BlockBed.PART, BlockBed.EnumPartType.FOOT);

					if (world.setBlockState(pos, placedFootBlockState, 11)) {
						IBlockState placedHeadBlockState = placedFootBlockState.withProperty(BlockBed.PART, BlockBed.EnumPartType.HEAD);
						world.setBlockState(offsetPos, placedHeadBlockState, 11);
					}

					SoundType sound = placedFootBlockState.getBlock().getSoundType(placedFootBlockState, world, pos, player);
					world.playSound((EntityPlayer)null, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
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