package thebetweenlands.common.block.structure;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;

public class BlockDecayPitInvisibleFloorBlockL1 extends BlockHorizontal implements ICustomItemBlock {

		public BlockDecayPitInvisibleFloorBlockL1() {
			super(Material.ROCK);
			setHardness(10.0F);
			setResistance(2000.0F);
			setSoundType(SoundType.STONE);
			setCreativeTab(BLCreativeTabs.BLOCKS);
			setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		}

		@Override
		public boolean isOpaqueCube(IBlockState state) {
			return false;
		}

		@Override
	    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
			return false;
		}

		@Override
	    public boolean causesSuffocation(IBlockState state) {
	    	return false;
	    }

		@Override
	    public boolean isFullCube(IBlockState state){
	        return false;
	    }

		@Nullable
		@Override
		public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
			return NULL_AABB;
		}

		@Override
		public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
			return false;
		}

		@Override
		public EnumBlockRenderType getRenderType(IBlockState state) {
			return EnumBlockRenderType.INVISIBLE;
		}

		@Override
		@SideOnly(Side.CLIENT)
		public BlockRenderLayer getRenderLayer() {
			return BlockRenderLayer.CUTOUT;
		}

		@Override
		public IBlockState getStateFromMeta(int meta) {
			// S = 0, W = 1, N = 2, E = 3
			return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
		}

		@Override
		public int getMetaFromState(IBlockState state) {
			// S = 0, W = 1, N = 2, E = 3
			int meta = 0;
			meta = meta | ((EnumFacing) state.getValue(FACING)).getIndex();
			return meta;
		}

		@Override
		 public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
			return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
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
			return new BlockStateContainer(this, new IProperty[] {FACING});
		}

    public static final AxisAlignedBB BOX_S1 = new AxisAlignedBB(0D, 0D, 0D, 0.25D, 1D, 0.4375D);
    public static final AxisAlignedBB BOX_S2 = new AxisAlignedBB(0.25D, 0D, 0D, 0.5D, 1D, 0.375D);
    public static final AxisAlignedBB BOX_S3 = new AxisAlignedBB(0.5D, 0D, 0D, 0.75D, 1D, 0.3125D);
    public static final AxisAlignedBB BOX_S4 = new AxisAlignedBB(0.75D, 0D, 0D, 1D, 1D, 0.25D);

    public static final AxisAlignedBB BOX_N1 = new AxisAlignedBB(0D, 0D, 0.75D, 0.25D, 1D, 1D);
    public static final AxisAlignedBB BOX_N2 = new AxisAlignedBB(0.25D, 0D, 0.6875D, 0.5D, 1D, 1D);
    public static final AxisAlignedBB BOX_N3 = new AxisAlignedBB(0.5D, 0D, 0.625D, 0.75D, 1D, 1D);
    public static final AxisAlignedBB BOX_N4 = new AxisAlignedBB(0.75D, 0D, 0.5625D, 1D, 1D, 1D);

    public static final AxisAlignedBB BOX_E1 = new AxisAlignedBB(0D, 0D, 0D, 0.25D, 1D, 0.25D);
    public static final AxisAlignedBB BOX_E2 = new AxisAlignedBB(0D, 0D, 0.25D, 0.3125D, 1D, 0.5D);
    public static final AxisAlignedBB BOX_E3 = new AxisAlignedBB(0D, 0D, 0.5D, 0.375D, 1D, 0.75D);
    public static final AxisAlignedBB BOX_E4 = new AxisAlignedBB(0D, 0D, 0.75D, 0.4375D, 1D, 1D);

    public static final AxisAlignedBB BOX_W1 = new AxisAlignedBB(0.5625D, 0D, 0D, 1D, 1D, 0.25D);
    public static final AxisAlignedBB BOX_W2 = new AxisAlignedBB(0.625D, 0D, 0.25D, 1D, 1D, 0.5D);
    public static final AxisAlignedBB BOX_W3 = new AxisAlignedBB(0.6875D, 0D, 0.5D, 1D, 1D, 0.75D);
    public static final AxisAlignedBB BOX_W4 = new AxisAlignedBB(0.75D, 0D, 0.75D, 1D, 1D, 1D);

    @Override
	public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
		if (!isActualState)
			state = state.getActualState(world, pos);

		if (state.getValue(FACING) == EnumFacing.NORTH) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_N1);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_N2);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_N3);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_N4);
		}

		if (state.getValue(FACING) == EnumFacing.SOUTH) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_S1);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_S2);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_S3);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_S4);
		}

		if (state.getValue(FACING) == EnumFacing.EAST) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_E1);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_E2);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_E3);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_E4);
		}

		if (state.getValue(FACING) == EnumFacing.WEST) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_W1);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_W2);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_W3);
			addCollisionBoxToList(pos, entityBox, collidingBoxes, BOX_W4);
		}
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public ItemBlock getItemBlock() {
        return null;
    }
}