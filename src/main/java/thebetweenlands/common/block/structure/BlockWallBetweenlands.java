package thebetweenlands.common.block.structure;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;

/**
 * Just a vanilla C&P because it's not extensible...
 */
public class BlockWallBetweenlands extends Block {
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty NORTH = BooleanProperty.create("north");
	public static final BooleanProperty EAST = BooleanProperty.create("east");
	public static final BooleanProperty SOUTH = BooleanProperty.create("south");
	public static final BooleanProperty WEST = BooleanProperty.create("west");
	protected static final AxisAlignedBB[] AABB_BY_INDEX = new AxisAlignedBB[] {new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.0D, 0.0D, 0.25D, 0.75D, 1.0D, 1.0D), new AxisAlignedBB(0.25D, 0.0D, 0.0D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.3125D, 0.0D, 0.0D, 0.6875D, 0.875D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 0.75D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 1.0D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 1.0D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.3125D, 1.0D, 0.875D, 0.6875D), new AxisAlignedBB(0.0D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D), new AxisAlignedBB(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};
	protected static final AxisAlignedBB[] CLIP_AABB_BY_INDEX = new AxisAlignedBB[] {AABB_BY_INDEX[0].setMaxY(1.5D), AABB_BY_INDEX[1].setMaxY(1.5D), AABB_BY_INDEX[2].setMaxY(1.5D), AABB_BY_INDEX[3].setMaxY(1.5D), AABB_BY_INDEX[4].setMaxY(1.5D), AABB_BY_INDEX[5].setMaxY(1.5D), AABB_BY_INDEX[6].setMaxY(1.5D), AABB_BY_INDEX[7].setMaxY(1.5D), AABB_BY_INDEX[8].setMaxY(1.5D), AABB_BY_INDEX[9].setMaxY(1.5D), AABB_BY_INDEX[10].setMaxY(1.5D), AABB_BY_INDEX[11].setMaxY(1.5D), AABB_BY_INDEX[12].setMaxY(1.5D), AABB_BY_INDEX[13].setMaxY(1.5D), AABB_BY_INDEX[14].setMaxY(1.5D), AABB_BY_INDEX[15].setMaxY(1.5D)};


	public BlockWallBetweenlands(IBlockState state) {
		super(state.getMaterial());
		setSoundType(state.getBlock().getSoundType());
		setHardness(2.0F);
		this.setDefaultState(this.blockState.getBaseState().with(UP, Boolean.FALSE).with(NORTH, Boolean.FALSE).with(EAST, Boolean.FALSE).with(SOUTH, Boolean.FALSE).with(WEST, Boolean.FALSE));
		setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IWorldReader source, BlockPos pos) {
		state = this.getActualState(state, source, pos);
		return AABB_BY_INDEX[getAABBIndex(state)];
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
		if (!isActualState) {
			state = this.getActualState(state, worldIn, pos);
		}

		addCollisionBoxToList(pos, entityBox, collidingBoxes, CLIP_AABB_BY_INDEX[getAABBIndex(state)]);
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IWorldReader worldIn, BlockPos pos) {
		blockState = this.getActualState(blockState, worldIn, pos);
		return CLIP_AABB_BY_INDEX[getAABBIndex(blockState)];
	}

	private static int getAABBIndex(IBlockState state) {
		int i = 0;

		if (state.get(NORTH)) {
			i |= 1 << EnumFacing.NORTH.getHorizontalIndex();
		}

		if (state.get(EAST)) {
			i |= 1 << EnumFacing.EAST.getHorizontalIndex();
		}

		if (state.get(SOUTH)) {
			i |= 1 << EnumFacing.SOUTH.getHorizontalIndex();
		}

		if (state.get(WEST)) {
			i |= 1 << EnumFacing.WEST.getHorizontalIndex();
		}

		return i;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isPassable(IWorldReader worldIn, BlockPos pos)
	{
		return false;
	}

	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks for render
	 */
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	private boolean canConnectTo(IWorldReader worldIn, BlockPos pos, EnumFacing facing) {
		IBlockState iblockstate = worldIn.getBlockState(pos);
		Block block = iblockstate.getBlock();
		BlockFaceShape blockfaceshape = iblockstate.getBlockFaceShape(worldIn, pos, facing);
		boolean flag = blockfaceshape == BlockFaceShape.MIDDLE_POLE_THICK || blockfaceshape == BlockFaceShape.MIDDLE_POLE && (block instanceof BlockFenceGate || block instanceof BlockFenceGateBetweenlands);
		return !isExcepBlockForAttachWithPiston(block) && blockfaceshape == BlockFaceShape.SOLID || flag;
	}

	protected static boolean isExcepBlockForAttachWithPiston(Block block) {
		return Block.isExceptBlockForAttachWithPiston(block) || block == Blocks.BARRIER || block == Blocks.MELON_BLOCK || block == Blocks.PUMPKIN || block == Blocks.LIT_PUMPKIN;
	}

	/**
	 * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
	 * returns the metadata of the dropped item based on the old metadata of the block.
	 */
	@Override
	public int damageDropped(IBlockState state)
	{
		return 0;
	}

	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IWorldReader blockAccess, BlockPos pos, EnumFacing side)
	{
		return side == EnumFacing.DOWN ? super.shouldSideBeRendered(blockState, blockAccess, pos, side) : true;
	}

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState();
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	/**
	 * Get the actual Block state of this Block at the given position. This applies properties not visible in the
	 * metadata, such as fence connections.
	 */
	@Override
	public IBlockState getActualState(IBlockState state, IWorldReader worldIn, BlockPos pos) {
		boolean flag =  canWallConnectTo(worldIn, pos, EnumFacing.NORTH);
		boolean flag1 = canWallConnectTo(worldIn, pos, EnumFacing.EAST);
		boolean flag2 = canWallConnectTo(worldIn, pos, EnumFacing.SOUTH);
		boolean flag3 = canWallConnectTo(worldIn, pos, EnumFacing.WEST);
		boolean flag4 = flag && !flag1 && flag2 && !flag3 || !flag && flag1 && !flag2 && flag3;
		return state.with(UP, !flag4 || !worldIn.isAirBlock(pos.up())).with(NORTH, flag).with(EAST, flag1).with(SOUTH, flag2).with(WEST, flag3);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {UP, NORTH, EAST, WEST, SOUTH});
	}
	
	@Override
	public boolean canPlaceTorchOnTop(IBlockState state, IWorldReader world, BlockPos pos)
    {
		return true;
    }

    @Override
	public BlockFaceShape getBlockFaceShape(IWorldReader world, IBlockState state, BlockPos pos, EnumFacing facing) {
		return facing != EnumFacing.UP && facing != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE_THICK : BlockFaceShape.CENTER_BIG;
	}

	/* ======================================== FORGE START ======================================== */

	@Override
	public boolean canBeConnectedTo(IWorldReader world, BlockPos pos, EnumFacing facing) {
		Block connector = world.getBlockState(pos.offset(facing)).getBlock();
		return connector instanceof BlockWall || connector instanceof BlockFenceGate || connector instanceof BlockWallBetweenlands || connector instanceof BlockFenceGateBetweenlands;
	}

	private boolean canWallConnectTo(IWorldReader world, BlockPos pos, EnumFacing facing) {
		BlockPos other = pos.offset(facing);
		Block block = world.getBlockState(other).getBlock();
		return block.canBeConnectedTo(world, other, facing.getOpposite()) || canConnectTo(world, other, facing.getOpposite());
	}

    /* ======================================== FORGE END ======================================== */
}