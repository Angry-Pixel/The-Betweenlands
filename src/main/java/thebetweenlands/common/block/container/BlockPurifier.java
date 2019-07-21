package thebetweenlands.common.block.container;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.tile.TileEntityPurifier;

import java.util.Random;

public class BlockPurifier extends BasicBlock implements ITileEntityProvider {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockPurifier() {
		super(Material.ROCK);
		setHardness(2.0F);
		setResistance(5.0F);
		setTranslationKey("thebetweenlands.purifier");
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing()), 2);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,  EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);

		if (world.getTileEntity(pos) instanceof TileEntityPurifier) {
			TileEntityPurifier tile = (TileEntityPurifier) world.getTileEntity(pos);

			if (player.isSneaking()) {
				return false;
			}

			if (!heldItem.isEmpty()) {
				IFluidHandler handler = heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
				if(handler != null) {
					Fluid fluid = FluidRegistry.SWAMP_WATER;
					FluidStack bucketFluid = handler.drain(new FluidStack(fluid, Fluid.BUCKET_VOLUME), false);

					if (bucketFluid != null) {
						IItemHandler playerInventory = player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
						if (playerInventory != null) {
							FluidActionResult fluidActionResult = FluidUtil.tryEmptyContainerAndStow(heldItem, tile, playerInventory, Integer.MAX_VALUE, player, !world.isRemote);

							if (fluidActionResult.isSuccess()) {
								if (!world.isRemote) {
									player.setHeldItem(hand, fluidActionResult.getResult());
								}
								return true;
							}
						}
					}
				}
			}
			
			if (!world.isRemote && tile != null) {
				player.openGui(TheBetweenlands.instance, CommonProxy.GUI_PURIFIER, world, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return true;
	}

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (world.getTileEntity(pos) instanceof TileEntityPurifier) {
			TileEntityPurifier tile = (TileEntityPurifier) world.getTileEntity(pos);
			if (tile == null) {
				return 0;
			}
			return tile.lightOn ? 13 : 0;
		}
		return 0;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);

		if (tileEntity instanceof IInventory) {
			InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileEntity);
			worldIn.updateComparatorOutputLevel(pos, this);
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if (world.getTileEntity(pos) instanceof TileEntityPurifier) {
			TileEntityPurifier tile = (TileEntityPurifier) world.getTileEntity(pos);
			if (tile.isPurifying() && tile.lightOn) {
				float x = pos.getX() + 0.5F;
				float y = pos.getY() + rand.nextFloat() * 6.0F / 16.0F;
				float z = pos.getZ() + 0.5F;
				float fixedOffset = 0.25F;
				float randomOffset = rand.nextFloat() * 0.6F - 0.3F;

				BLParticles.PURIFIER_STEAM.spawn(world, (double) (x - fixedOffset), (double) y + 0.5D, (double) (z + randomOffset));
				//BLParticle.STEAM_PURIFIER.spawn(world, (double) (x - fixedOffset), (double) y + 0.5D, (double) (z + randomOffset), 0.0D, 0.0D, 0.0D, 0);
				world.spawnParticle(EnumParticleTypes.FLAME, (double) (x - fixedOffset), (double) y, (double) (z + randomOffset), 0.0D, 0.0D, 0.0D);

				BLParticles.PURIFIER_STEAM.spawn(world, (double) (x + fixedOffset), (double) y + 0.5D, (double) (z + randomOffset));
				//BLParticle.STEAM_PURIFIER.spawn(world, (double) (x + fixedOffset), (double) y + 0.5D, (double) (z + randomOffset), 0.0D, 0.0D, 0.0D, 0);
				world.spawnParticle(EnumParticleTypes.FLAME, (double) (x + fixedOffset), (double) y, (double) (z + randomOffset), 0.0D, 0.0D, 0.0D);

				BLParticles.PURIFIER_STEAM.spawn(world, (x + randomOffset), (double) y, (double) (z - fixedOffset));
				//BLParticle.STEAM_PURIFIER.spawn(world, (double) (x + randomOffset), (double) y + 0.5D, (double) (z - fixedOffset), 0.0D, 0.0D, 0.0D, 0);
				world.spawnParticle(EnumParticleTypes.FLAME, (double) (x + randomOffset), (double) y, (double) (z - fixedOffset), 0.0D, 0.0D, 0.0D);

				BLParticles.PURIFIER_STEAM.spawn(world, (double) (x + randomOffset), (double) y + 0.5D, (double) (z + fixedOffset));
				//BLParticle.STEAM_PURIFIER.spawn(world, (double) (x + randomOffset), (double) y + 0.5D, (double) (z + fixedOffset), 0.0D, 0.0D, 0.0D, 0);
				world.spawnParticle(EnumParticleTypes.FLAME, (double) (x + randomOffset), (double) y, (double) (z + fixedOffset), 0.0D, 0.0D, 0.0D);

				if (world.isAirBlock(pos.up())) {
					BLParticles.BUBBLE_PURIFIER.spawn(world, x, y + 1, z);
				}
			}
		}
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.byIndex(meta);

		if (facing.getAxis() == EnumFacing.Axis.Y) {
			facing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, facing);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityPurifier();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
        super.eventReceived(state, worldIn, pos, id, param);
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
    }
	
	@Override
	public void fillWithRain(World world, BlockPos pos) {
		if (world.provider.getDimension() == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId && world.getTileEntity(pos) instanceof TileEntityPurifier) {
			TileEntityPurifier tile = (TileEntityPurifier) world.getTileEntity(pos);
			
			if(tile != null) {
				tile.fill(new FluidStack(FluidRegistry.SWAMP_WATER, Fluid.BUCKET_VOLUME / 2), true);
			}
		}
	}
}