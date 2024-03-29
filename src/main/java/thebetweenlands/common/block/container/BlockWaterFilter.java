package thebetweenlands.common.block.container;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityWaterFilter;

public class BlockWaterFilter extends BlockContainer {

	public BlockWaterFilter() {
		super(Material.WOOD);
		setHardness(2.0F);
		setResistance(5.0F);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setSoundType(SoundType.WOOD);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityWaterFilter();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.UNDEFINED;
    }

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);
		if (world.getTileEntity(pos) instanceof TileEntityWaterFilter) {
			TileEntityWaterFilter tile = (TileEntityWaterFilter) world.getTileEntity(pos);

			if (FluidUtil.getFluidHandler(heldItem) == null && hand == EnumHand.MAIN_HAND) {

				if (world.isRemote && tile.tank.getFluid() !=null)
					player.sendStatusMessage(new TextComponentTranslation(tile.tank.getFluid().getLocalizedName() + ": "+ tile.tank.getFluidAmount()+"/"+ tile.tank.getCapacity()), true);

				if (heldItem.getItem() == ItemRegistry.SILK_FILTER || heldItem.getItem() == ItemRegistry.MOSS_FILTER) {
					if (tile.getStackInSlot(0).isEmpty()) {
						if (!world.isRemote) {
							tile.setInventorySlotContents(0, heldItem.copy());
							heldItem.shrink(1);
							tile.markForUpdate();
						}
					}
					player.swingArm(hand);
					return true;
				}

				if (!tile.getStackInSlot(0).isEmpty())
					if (!world.isRemote) 
						if (player.isSneaking()) {
							spitOutItems(world, pos, tile, 0);
							return true;
						}

				if (!tile.getStackInSlot(1).isEmpty())
					if (!world.isRemote)
						if (!player.isSneaking()) {
							spitOutItems(world, pos, tile, 1);
							return true;
						}
				
				if (!tile.getStackInSlot(2).isEmpty())
					if (!world.isRemote)
						if (!player.isSneaking()) {
							spitOutItems(world, pos, tile, 2);
							return true;
						}

				if (!tile.getStackInSlot(3).isEmpty())
					if (!world.isRemote)
						if (!player.isSneaking()) {
							spitOutItems(world, pos, tile, 3);
							return true;
						}
				
				if (!tile.getStackInSlot(4).isEmpty())
					if (!world.isRemote)
						if (!player.isSneaking()) {
							spitOutItems(world, pos, tile, 4);
							return true;
						}

				player.swingArm(hand);
				return true;
			}
		}

		final IFluidHandler fluidHandler = getFluidHandler(world, pos);
		if (fluidHandler != null) {
			FluidUtil.interactWithFluidHandler(player, hand, world, pos, side);
			return FluidUtil.getFluidHandler(player.getHeldItem(hand)) != null;
		}
		return false;
	}
	
	public void spitOutItems(World world, BlockPos pos, TileEntityWaterFilter tile, int slot) {
		ItemStack extracted = tile.getStackInSlot(slot);
		EntityItem item = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, extracted);
		item.motionX = item.motionY = item.motionZ = 0D;
		world.spawnEntity(item);
		tile.setInventorySlotContents(slot, ItemStack.EMPTY);
		tile.markForUpdate();
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntityWaterFilter tile = (TileEntityWaterFilter) world.getTileEntity(pos);
		if (tile != null) {
			InventoryHelper.dropInventoryItems(world, pos, tile);
			world.removeTileEntity(pos);
		}
		super.breakBlock(world, pos, state);
	}

	@Nullable
	private IFluidHandler getFluidHandler(IBlockAccess world, BlockPos pos) {
		TileEntityWaterFilter tileentity = (TileEntityWaterFilter) world.getTileEntity(pos);
		return tileentity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
	}

}
