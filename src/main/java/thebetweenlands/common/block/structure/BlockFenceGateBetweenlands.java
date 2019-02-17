package thebetweenlands.common.block.structure;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistryOld.IStateMappedBlock;
import thebetweenlands.util.AdvancedStateMap;

/**
 * Just a vanilla C&P because it's not extensible...
 */
public class BlockFenceGateBetweenlands extends BlockHorizontal implements IStateMappedBlock {
    public static final BooleanProperty OPEN = BooleanProperty.create("open");
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final BooleanProperty IN_WALL = BooleanProperty.create("in_wall");
    protected static final AxisAlignedBB AABB_HITBOX_ZAXIS = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D);
    protected static final AxisAlignedBB AABB_HITBOX_XAXIS = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D);
    protected static final AxisAlignedBB AABB_HITBOX_ZAXIS_INWALL = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 0.8125D, 0.625D);
    protected static final AxisAlignedBB AABB_HITBOX_XAXIS_INWALL = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 0.8125D, 1.0D);
    protected static final AxisAlignedBB AABB_COLLISION_BOX_ZAXIS = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.5D, 0.625D);
    protected static final AxisAlignedBB AABB_COLLISION_BOX_XAXIS = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.5D, 1.0D);

    public BlockFenceGateBetweenlands(IBlockState state) {
        super(state.getMaterial(), state.getMaterial().getMaterialMapColor());
        setSoundType(state.getBlock().getSoundType());
        setHardness(2.0F);
        this.setDefaultState(this.blockState.getBaseState().with(OPEN, Boolean.FALSE).with(POWERED, Boolean.FALSE).with(IN_WALL, Boolean.FALSE));
        this.setCreativeTab(BLCreativeTabs.BLOCKS);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IWorldReader source, BlockPos pos) {
        state = this.getActualState(state, source, pos);

        if (state.get(IN_WALL)) {
            return state.get(FACING).getAxis() == EnumFacing.Axis.X ? AABB_HITBOX_XAXIS_INWALL : AABB_HITBOX_ZAXIS_INWALL;
        } else {
            return state.get(FACING).getAxis() == EnumFacing.Axis.X ? AABB_HITBOX_XAXIS : AABB_HITBOX_ZAXIS;
        }
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    @Override
    public IBlockState getActualState(IBlockState state, IWorldReader worldIn, BlockPos pos) {
        EnumFacing.Axis enumfacing$axis = state.get(FACING).getAxis();

        if (enumfacing$axis == EnumFacing.Axis.Z && (canFenceGateConnectTo(worldIn, pos, EnumFacing.WEST) || canFenceGateConnectTo(worldIn, pos, EnumFacing.EAST))
                || enumfacing$axis == EnumFacing.Axis.X && (canFenceGateConnectTo(worldIn, pos, EnumFacing.NORTH) || canFenceGateConnectTo(worldIn, pos, EnumFacing.SOUTH))) {
            state = state.with(IN_WALL, Boolean.TRUE);
        }

        return state;
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation(state.get(FACING)));
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.down()).getMaterial().isSolid() ? super.canPlaceBlockAt(worldIn, pos) : false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IWorldReader worldIn, BlockPos pos) {
        if (blockState.getValue(OPEN)) {
            return NULL_AABB;
        } else {
            return blockState.getValue(FACING).getAxis() == EnumFacing.Axis.Z ? AABB_COLLISION_BOX_ZAXIS : AABB_COLLISION_BOX_XAXIS;
        }
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isPassable(IWorldReader worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).getValue(OPEN);
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    @Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        boolean flag = worldIn.isBlockPowered(pos);
        return this.getDefaultState().with(FACING, placer.getHorizontalFacing()).with(OPEN, flag).with(POWERED, flag).with(IN_WALL, Boolean.FALSE);
    }
    
    @Override
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (state.get(OPEN)) {
            state = state.with(OPEN, Boolean.FALSE);
            worldIn.setBlockState(pos, state, 10);
        } else {
            EnumFacing enumfacing = EnumFacing.fromAngle((double)playerIn.rotationYaw);

            if (state.get(FACING) == enumfacing.getOpposite()) {
                state = state.with(FACING, enumfacing);
            }

            state = state.with(OPEN, Boolean.TRUE);
            worldIn.setBlockState(pos, state, 10);
        }

        worldIn.playEvent(playerIn, state.get(OPEN) ? 1008 : 1014, pos, 0);
        return true;
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!worldIn.isRemote()) {
            boolean flag = worldIn.isBlockPowered(pos);

            if (state.get(POWERED) != flag) {
                worldIn.setBlockState(pos, state.with(POWERED, flag).with(OPEN, flag), 2);

                if (state.get(OPEN) != flag) {
                    worldIn.playEvent(null, flag ? 1008 : 1014, pos, 0);
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IWorldReader blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().with(FACING, EnumFacing.byHorizontalIndex(meta)).with(OPEN, (meta & 4) != 0).with(POWERED, (meta & 8) != 0);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i = i | ((EnumFacing) state.get(FACING)).getHorizontalIndex();

        if (state.get(POWERED)) {
            i |= 8;
        }

        if (state.get(OPEN)) {
            i |= 4;
        }

        return i;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING, OPEN, POWERED, IN_WALL});
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void setStateMapper(AdvancedStateMap.Builder builder) {
        builder.ignore(POWERED);
    }

    /* ======================================== FORGE START ======================================== */

    @Override
    public boolean canBeConnectedTo(IWorldReader world, BlockPos pos, EnumFacing facing) {
        Block connector = world.getBlockState(pos.offset(facing)).getBlock();
        return connector instanceof BlockFence || connector instanceof BlockWall || connector instanceof BlockWallBetweenlands || connector instanceof BlockFenceBetweenlands;
    }

    private boolean canFenceGateConnectTo(IWorldReader world, BlockPos pos, EnumFacing facing) {
        Block block = world.getBlockState(pos.offset(facing)).getBlock();
        return block.canBeConnectedTo(world, pos.offset(facing), facing.getOpposite());
    }

    /* ======================================== FORGE END ======================================== */

    @Override
    public BlockFaceShape getBlockFaceShape(IWorldReader world, IBlockState state, BlockPos pos, EnumFacing facing) {
        if (facing != EnumFacing.UP && facing != EnumFacing.DOWN) {
            return state.get(FACING).getAxis() == facing.rotateY().getAxis() ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.UNDEFINED;
        } else {
            return BlockFaceShape.UNDEFINED;
        }
    }
}