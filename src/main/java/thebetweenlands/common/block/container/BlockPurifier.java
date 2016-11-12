package thebetweenlands.common.block.container;

import java.util.Random;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.tile.TileEntityPurifier;

public class BlockPurifier extends BasicBlock implements ITileEntityProvider {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockPurifier() {
		super(Material.ROCK);
		setHardness(2.0F);
		setResistance(5.0F);
		setUnlocalizedName("thebetweenlands.purifier");
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing()), 2);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return true;
		}
		if (world.getTileEntity(pos) instanceof TileEntityPurifier) {
			TileEntityPurifier tile = (TileEntityPurifier) world.getTileEntity(pos);

			if (player.isSneaking()) {
				return false;
			}

			if (heldItem != null) {
				ItemStack newItem = tile.fillTankWithBucket(heldItem.copy());
				world.markChunkDirty(pos, tile);
				if (!ItemStack.areItemStacksEqual(heldItem, newItem)) {
					if (!player.capabilities.isCreativeMode) {
						player.inventory.setInventorySlotContents(player.inventory.currentItem, newItem);
					}
					return true;
				}
			}
			if (tile != null) {
				player.openGui(TheBetweenlands.INSTANCE, CommonProxy.GUI_PURIFIER, world, pos.getX(), pos.getY(), pos.getZ());
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
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		IInventory tile = (IInventory) world.getTileEntity(pos);
		if (tile != null) {
			for (int i = 0; i < tile.getSizeInventory(); i++) {
				ItemStack stack = tile.getStackInSlot(i);
				if (stack != null) {
					if (!world.isRemote && world.getGameRules().getBoolean("doTileDrops")) {
						float f = 0.7F;
						double xOffset = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double yOffset = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						double zOffset = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
						EntityItem item = new EntityItem(world, pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset, stack);
						item.setDefaultPickupDelay();
						world.spawnEntityInWorld(item);
					}
				}
			}
		}
		super.breakBlock(world, pos, state);
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

				//                if (world.isAirBlock(pos.up())) {
				//                    BLParticle.BUBBLE_PRUIFIER.spawn(world, x, y + 1, z, 0.1D, 0.0D, 0.1D, 0);
				//                }
			}
		}
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.getFront(meta);

		if (facing.getAxis() == EnumFacing.Axis.Y) {
			facing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, facing);
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
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

	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}
}