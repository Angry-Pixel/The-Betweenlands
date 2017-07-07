package thebetweenlands.common.block.structure;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.util.AdvancedStateMap;

/**
 * Just a vanilla C&P because it's not extensible...
 */
public class BlockFenceGateBetweenlands extends BlockHorizontal implements IStateMappedBlock
{
	public static final PropertyBool OPEN = PropertyBool.create("open");
	public static final PropertyBool POWERED = PropertyBool.create("powered");
	public static final PropertyBool IN_WALL = PropertyBool.create("in_wall");
	protected static final AxisAlignedBB AABB_COLLIDE_ZAXIS = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D);
	protected static final AxisAlignedBB AABB_COLLIDE_XAXIS = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D);
	protected static final AxisAlignedBB AABB_COLLIDE_ZAXIS_INWALL = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 0.8125D, 0.625D);
	protected static final AxisAlignedBB AABB_COLLIDE_XAXIS_INWALL = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 0.8125D, 1.0D);
	protected static final AxisAlignedBB AABB_CLOSED_SELECTED_ZAXIS = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.5D, 0.625D);
	protected static final AxisAlignedBB AABB_CLOSED_SELECTED_XAXIS = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.5D, 1.0D);

	public BlockFenceGateBetweenlands(IBlockState state)
	{
		super(state.getMaterial(), state.getMapColor());
		setSoundType(state.getBlock().getSoundType());
		setHardness(2.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(OPEN, Boolean.valueOf(false)).withProperty(POWERED, Boolean.valueOf(false)).withProperty(IN_WALL, Boolean.valueOf(false)));
		this.setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		state = this.getActualState(state, source, pos);
		return ((Boolean)state.getValue(IN_WALL)).booleanValue() ? (((EnumFacing)state.getValue(FACING)).getAxis() == EnumFacing.Axis.X ? AABB_COLLIDE_XAXIS_INWALL : AABB_COLLIDE_ZAXIS_INWALL) : (((EnumFacing)state.getValue(FACING)).getAxis() == EnumFacing.Axis.X ? AABB_COLLIDE_XAXIS : AABB_COLLIDE_ZAXIS);
	}

	/**
	 * Get the actual Block state of this Block at the given position. This applies properties not visible in the
	 * metadata, such as fence connections.
	 */
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		EnumFacing.Axis enumfacing$axis = ((EnumFacing)state.getValue(FACING)).getAxis();

		if (enumfacing$axis == EnumFacing.Axis.Z && 
				(worldIn.getBlockState(pos.west()).getBlock() == Blocks.COBBLESTONE_WALL || worldIn.getBlockState(pos.east()).getBlock() == Blocks.COBBLESTONE_WALL || 
				worldIn.getBlockState(pos.west()).getBlock() instanceof BlockWallBetweenlands || worldIn.getBlockState(pos.east()).getBlock() instanceof BlockWallBetweenlands) || 
				enumfacing$axis == EnumFacing.Axis.X &&
				(worldIn.getBlockState(pos.north()).getBlock() == Blocks.COBBLESTONE_WALL || worldIn.getBlockState(pos.south()).getBlock() == Blocks.COBBLESTONE_WALL || 
				worldIn.getBlockState(pos.north()).getBlock() instanceof BlockWallBetweenlands || worldIn.getBlockState(pos.south()).getBlock() instanceof BlockWallBetweenlands))
		{
			state = state.withProperty(IN_WALL, Boolean.valueOf(true));
		}

		return state;
	}

	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 */
	public IBlockState withRotation(IBlockState state, Rotation rot)
	{
		return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
	}

	/**
	 * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
	 * blockstate.
	 */
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
	{
		return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
	}

	public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
	{
		return worldIn.getBlockState(pos.down()).getMaterial().isSolid() ? super.canPlaceBlockAt(worldIn, pos) : false;
	}

	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
	{
		return ((Boolean)blockState.getValue(OPEN)).booleanValue() ? NULL_AABB : (((EnumFacing)blockState.getValue(FACING)).getAxis() == EnumFacing.Axis.Z ? AABB_CLOSED_SELECTED_ZAXIS : AABB_CLOSED_SELECTED_XAXIS);
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 */
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
	{
		return ((Boolean)worldIn.getBlockState(pos).getValue(OPEN)).booleanValue();
	}

	/**
	 * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
	 * IBlockstate
	 */
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(OPEN, Boolean.valueOf(false)).withProperty(POWERED, Boolean.valueOf(false)).withProperty(IN_WALL, Boolean.valueOf(false));
	}

	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (((Boolean)state.getValue(OPEN)).booleanValue())
		{
			state = state.withProperty(OPEN, Boolean.valueOf(false));
			worldIn.setBlockState(pos, state, 10);
		}
		else
		{
			EnumFacing enumfacing = EnumFacing.fromAngle((double)playerIn.rotationYaw);

			if (state.getValue(FACING) == enumfacing.getOpposite())
			{
				state = state.withProperty(FACING, enumfacing);
			}

			state = state.withProperty(OPEN, Boolean.valueOf(true));
			worldIn.setBlockState(pos, state, 10);
		}

		worldIn.playEvent(playerIn, ((Boolean)state.getValue(OPEN)).booleanValue() ? 1008 : 1014, pos, 0);
		return true;
	}

	/**
	 * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
	 * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
	 * block, etc.
	 */
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn)
	{
		if (!worldIn.isRemote)
		{
			boolean flag = worldIn.isBlockPowered(pos);

			if (flag || blockIn.getDefaultState().canProvidePower())
			{
				if (flag && !((Boolean)state.getValue(OPEN)).booleanValue() && !((Boolean)state.getValue(POWERED)).booleanValue())
				{
					worldIn.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(true)).withProperty(POWERED, Boolean.valueOf(true)), 2);
					worldIn.playEvent((EntityPlayer)null, 1008, pos, 0);
				}
				else if (!flag && ((Boolean)state.getValue(OPEN)).booleanValue() && ((Boolean)state.getValue(POWERED)).booleanValue())
				{
					worldIn.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(false)).withProperty(POWERED, Boolean.valueOf(false)), 2);
					worldIn.playEvent((EntityPlayer)null, 1014, pos, 0);
				}
				else if (flag != ((Boolean)state.getValue(POWERED)).booleanValue())
				{
					worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(flag)), 2);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
	{
		return true;
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(OPEN, Boolean.valueOf((meta & 4) != 0)).withProperty(POWERED, Boolean.valueOf((meta & 8) != 0));
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state)
	{
		int i = 0;
		i = i | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();

		if (((Boolean)state.getValue(POWERED)).booleanValue())
		{
			i |= 8;
		}

		if (((Boolean)state.getValue(OPEN)).booleanValue())
		{
			i |= 4;
		}

		return i;
	}

	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {FACING, OPEN, POWERED, IN_WALL});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(POWERED);
	}
}