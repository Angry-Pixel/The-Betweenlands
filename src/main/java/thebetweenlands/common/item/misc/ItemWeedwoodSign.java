package thebetweenlands.common.item.misc;

import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry;

public class ItemWeedwoodSign extends Item {
	public ItemWeedwoodSign() {
		this.maxStackSize = 16;
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public EnumActionResult onItemUse( EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = playerIn.getHeldItem(hand);
		IBlockState iblockstate = worldIn.getBlockState(pos);
		boolean flag = iblockstate.getBlock().isReplaceable(worldIn, pos);

		if (facing != EnumFacing.DOWN && (iblockstate.getMaterial().isSolid() || flag) && (!flag || facing == EnumFacing.UP)) {
			pos = pos.offset(facing);

			if (playerIn.canPlayerEdit(pos, facing, stack) && BlockRegistry.STANDING_WEEDWOOD_SIGN.canPlaceBlockAt(worldIn, pos)) {
				if (worldIn.isRemote) {
					return EnumActionResult.SUCCESS;
				} else {
					pos = flag ? pos.down() : pos;

					if (facing == EnumFacing.UP) {
						int i = MathHelper.floor((double)((playerIn.rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
						worldIn.setBlockState(pos, BlockRegistry.STANDING_WEEDWOOD_SIGN.getDefaultState().withProperty(BlockStandingSign.ROTATION, Integer.valueOf(i)), 11);
					} else {
						worldIn.setBlockState(pos, BlockRegistry.WALL_WEEDWOOD_SIGN.getDefaultState().withProperty(BlockWallSign.FACING, facing), 11);
					}

					stack.shrink(1);
					TileEntity tileentity = worldIn.getTileEntity(pos);

					if (tileentity instanceof TileEntitySign && !ItemBlock.setTileEntityNBT(worldIn, playerIn, pos, stack)) {
						playerIn.openEditSign((TileEntitySign)tileentity);
					}

					return EnumActionResult.SUCCESS;
				}
			} else {
				return EnumActionResult.FAIL;
			}
		} else {
			return EnumActionResult.FAIL;
		}
	}
}