package thebetweenlands.common.block.misc;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.terrain.BlockRubberLog;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityRubberTap;

public class BlockRubberTap extends BlockHorizontal implements ITileEntityProvider {
	public static final PropertyInteger AMOUNT = PropertyInteger.create("amount", 0, 15);

	protected static final AxisAlignedBB TAP_WEST_AABB = new AxisAlignedBB(0.4D, 0.0D, 0.15D, 1.0D, 1.0D, 0.85D);
	protected static final AxisAlignedBB TAP_EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.15D, 0.6D, 1.0D, 0.85D);
	protected static final AxisAlignedBB TAP_SOUTH_AABB = new AxisAlignedBB(0.15D, 0.0D, 0.0D, 0.85D, 1.0D, 0.6D);
	protected static final AxisAlignedBB TAP_NORTH_AABB = new AxisAlignedBB(0.15D, 0.0D, 0.4D, 0.85D, 1.0D, 1.0D);

	/**
	 * The number of ticks it requires to fill up to the next step (15 steps in total)
	 */
	public final int ticksPerStep;

	@SuppressWarnings("deprecation")
	public BlockRubberTap(IBlockState material, int ticksPerStep) {
		super(material.getMaterial());
		this.setDefaultState(this.getBlockState().getBaseState().withProperty(AMOUNT, 0));
		this.setSoundType(material.getBlock().getSoundType());
		this.setHardness(2.0F);
		this.ticksPerStep = ticksPerStep;
		this.setCreativeTab(null);
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		if (this.canPlaceAt(worldIn, pos, facing)) {
			return this.getDefaultState().withProperty(FACING, facing);
		} else {
			for (EnumFacing enumfacing : FACING.getAllowedValues()) {
				if(this.canPlaceAt(worldIn, pos, enumfacing))
					return this.getDefaultState().withProperty(FACING, enumfacing);
			}
			return this.getDefaultState();
		}
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		this.checkForDrop(worldIn, pos, state);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		for (EnumFacing enumfacing : FACING.getAllowedValues()) {
			if (this.canPlaceAt(worldIn, pos, enumfacing)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		if (this.checkForDrop(worldIn, pos, state)) {
			EnumFacing facing = (EnumFacing)state.getValue(FACING);
			EnumFacing.Axis axis = facing.getAxis();
			EnumFacing oppositeFacing = facing.getOpposite();
			if (axis.isVertical() || !this.canPlaceOn(worldIn, pos.offset(oppositeFacing))) {
				this.dropBlockAsItem(worldIn, pos, state, 0);
				worldIn.setBlockToAir(pos);
			}
		}
	}

	protected boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
		if (state.getBlock() == this && this.canPlaceAt(worldIn, pos, (EnumFacing)state.getValue(FACING))) {
			return true;
		} else {
			if (worldIn.getBlockState(pos).getBlock() == this) {
				this.dropBlockAsItem(worldIn, pos, state, 0);
				worldIn.setBlockToAir(pos);
			}
			return false;
		}
	}

	private boolean canPlaceAt(World worldIn, BlockPos pos, EnumFacing facing) {
		BlockPos blockPos = pos.offset(facing.getOpposite());
		boolean isHorizontal = facing.getAxis().isHorizontal();
		return isHorizontal && this.canPlaceOn(worldIn, blockPos);
	}

	private boolean canPlaceOn(World worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos);
		return state.getBlock() == BlockRegistry.LOG_RUBBER && state.getValue(BlockRubberLog.NATURAL);
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING, AMOUNT});
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
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		if(worldIn instanceof ChunkCache) {
			TileEntity te = worldIn.getTileEntity(pos);
			if(te != null && te instanceof TileEntityRubberTap) {
				FluidStack drained = ((TileEntityRubberTap)te).drain(Fluid.BUCKET_VOLUME, false);
				if(drained != null) {
					int amount = (int)((float)drained.amount / (float)Fluid.BUCKET_VOLUME * 15.0F);
					state = state.withProperty(AMOUNT, amount);
				} else {
					state = state.withProperty(AMOUNT, 0);
				}
			}
		}
		return state;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityRubberTap();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch ((EnumFacing)state.getValue(FACING)) {
		default:
		case EAST:
			return TAP_EAST_AABB;
		case WEST:
			return TAP_WEST_AABB;
		case SOUTH:
			return TAP_SOUTH_AABB;
		case NORTH:
			return TAP_NORTH_AABB;
		}
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		return this.getBoundingBox(blockState, worldIn, pos);
	}
}
