package thebetweenlands.common.item.misc;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;

public abstract class ItemDoorBetweenlands extends Item {
	public ItemDoorBetweenlands() {
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (facing != EnumFacing.UP) {
			return EnumActionResult.FAIL;
		} else {
			IBlockState blockState = worldIn.getBlockState(pos);
			Block block = blockState.getBlock();

			if (!block.isReplaceable(worldIn, pos)) {
				pos = pos.offset(facing);
			}

			ItemStack stack = playerIn.getHeldItem(hand);
			if (playerIn.canPlayerEdit(pos, facing, stack) && this.getDoorBlock().canPlaceBlockAt(worldIn, pos)) {
				EnumFacing placeDir = EnumFacing.fromAngle((double)playerIn.rotationYaw);
				int xOffset = placeDir.getFrontOffsetX();
				int zOffset = placeDir.getFrontOffsetZ();
				boolean flag = xOffset < 0 && hitZ < 0.5F || xOffset > 0 && hitZ > 0.5F || zOffset < 0 && hitX > 0.5F || zOffset > 0 && hitX < 0.5F;
				ItemDoor.placeDoor(worldIn, pos, placeDir, this.getDoorBlock(), flag);
				SoundType soundtype = this.getDoorBlock().getSoundType();
				worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				stack.shrink(1);
				return EnumActionResult.SUCCESS;
			} else {
				return EnumActionResult.FAIL;
			}
		}
	}

	public abstract Block getDoorBlock();
}