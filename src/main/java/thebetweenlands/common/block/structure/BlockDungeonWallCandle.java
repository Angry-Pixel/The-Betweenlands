package thebetweenlands.common.block.structure;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockDungeonWallCandle extends BlockHorizontal {

	protected static final AxisAlignedBB CANDLE_WEST_AABB = new AxisAlignedBB(0.25D, 0D, 0.25D, 1, 0.875D, 0.75D);
	protected static final AxisAlignedBB CANDLE_EAST_AABB = new AxisAlignedBB(0D, 0D, 0.25D, 0.75D, 0.875D, 0.75D);
	protected static final AxisAlignedBB CANDLE_SOUTH_AABB = new AxisAlignedBB(0.25D, 0D, 0D, 0.75D, 0.875D, 0.75D);
	protected static final AxisAlignedBB CANDLE_NORTH_AABB = new AxisAlignedBB(0.25D, 0D, 0.25D, 0.75D, 0.875D, 1D);
	public static final PropertyBool LIT = PropertyBool.create("lit");

	public BlockDungeonWallCandle() {
		this(Material.ROCK);
	}

	public BlockDungeonWallCandle(Material material) {
		super(material);
		setHardness(0.1F);
		setSoundType(SoundType.STONE);
		setHarvestLevel("pickaxe", 0);
		setDefaultState(this.getBlockState().getBaseState().withProperty(LIT, false));
		setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		state = state.getActualState(source, pos);
		switch ((EnumFacing) state.getValue(FACING)) {
		default:
		case EAST:
			return CANDLE_EAST_AABB;
		case WEST:
			return CANDLE_WEST_AABB;
		case SOUTH:
			return CANDLE_SOUTH_AABB;
		case NORTH:
			return CANDLE_NORTH_AABB;
		}
		
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
		state = state.getActualState(world, pos);
		switch ((EnumFacing) state.getValue(FACING)) {
		default:
		case EAST:
			return CANDLE_EAST_AABB.offset(pos);
		case WEST:
			return CANDLE_WEST_AABB.offset(pos);
		case SOUTH:
			return CANDLE_SOUTH_AABB.offset(pos);
		case NORTH:
			return CANDLE_NORTH_AABB.offset(pos);
		}
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

    @Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullBlock(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	 public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		if (canPlaceAt(world, pos, facing))
			return this.getDefaultState().withProperty(FACING, facing).withProperty(LIT, false);
		return this.getDefaultState();
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return true;
		} else {
			state = state.cycleProperty(LIT);
			world.setBlockState(pos, state, 3);
			if(state.getValue(LIT))
				world.playSound((EntityPlayer)null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 0.05F, 1F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
			else
				world.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.1F, 2F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
			return true;
		}
	}

	@Override
	public int getLightValue(IBlockState state) {
		return state.getValue(LIT) ? 13 : 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
		if(state.getValue(LIT)) {
			double offSetX = 0D;
			double offSetZ = 0D;
			double offSetWaxX = 0D + rand.nextDouble() * 0.125D - rand.nextDouble() * 0.125D;
			double offSetWaxZ = 0D + rand.nextDouble() * 0.125D - rand.nextDouble() * 0.125D;
			if (state.getValue(FACING) == EnumFacing.WEST)
				offSetX = 0.09375;
			if (state.getValue(FACING) == EnumFacing.EAST)
				offSetX = -0.09375;
			if (state.getValue(FACING) == EnumFacing.NORTH)
				offSetZ = 0.09375;
			if (state.getValue(FACING) == EnumFacing.SOUTH)
				offSetZ = -0.09375;

			double x = (double)pos.getX() + 0.5D;
			double y = (double)pos.getY() + 0.9375D;
			double z = (double)pos.getZ() + 0.5D;

			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + offSetX, y, z + offSetZ, 0.0D, 0.0D, 0.0D);
			world.spawnParticle(EnumParticleTypes.FLAME, x + offSetX, y, z + offSetZ, 0.0D, 0.0D, 0.0D);
			if (rand.nextInt(10) == 0)
				BLParticles.TAR_BEAST_DRIP.spawn(world , x + offSetX + offSetWaxX, y - 0.938D, z + offSetZ +offSetWaxZ).setRBGColorF(1F, 1F, 1F);
		}
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		for (EnumFacing enumfacing : FACING.getAllowedValues()) {
			if (canPlaceAt(world, pos, enumfacing))
				return true;
		}
		return false;
	}

	private boolean canPlaceAt(World world, BlockPos pos, EnumFacing facing) {
		BlockPos blockpos = pos.offset(facing.getOpposite());
		boolean isSide = facing.getAxis().isHorizontal();
		return isSide && world.getBlockState(blockpos).isSideSolid(world, blockpos, facing);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		EnumFacing facing = world.getBlockState(pos).getValue(FACING);
    	if(!canPlaceAt((World) world, pos, facing)) {
            this.dropBlockAsItem((World) world, pos, world.getBlockState(pos), 0);
            ((World) world).setBlockToAir(pos);
        }
    }

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing facing = EnumFacing.byIndex(meta & 0b111);
		if(facing.getAxis() == EnumFacing.Axis.Y) {
			facing = EnumFacing.NORTH;
		}
		return getDefaultState().withProperty(FACING, facing).withProperty(LIT, (meta & 0b1000) != 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = state.getValue(FACING).getIndex();

		if(state.getValue(LIT)) {
			meta |= 0b1000;
		}

		return meta;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING, LIT });
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}
	
	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}
}
