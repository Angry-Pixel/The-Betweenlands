package thebetweenlands.common.block.structure;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;

public class BlockDecayPitInvisibleFloorBlockDiagonal extends BlockHorizontal implements ICustomItemBlock {
	public static final PropertyBool FLIPPED = PropertyBool.create("flipped");
		public BlockDecayPitInvisibleFloorBlockDiagonal() {
			super(Material.ROCK);
			setHardness(10.0F);
			setResistance(2000.0F);
			setSoundType(SoundType.STONE);
			setCreativeTab(BLCreativeTabs.BLOCKS);
			setDefaultState(blockState.getBaseState().withProperty(FLIPPED, false));
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
		@SideOnly(Side.CLIENT)
		public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
			return FULL_BLOCK_AABB.offset(pos);
		}

		@Override
		public IBlockState getStateFromMeta(int meta) {
			return getDefaultState().withProperty(FLIPPED, Boolean.valueOf(meta > 0));
		}

		@Override
		public int getMetaFromState(IBlockState state) {
			return state.getValue(FLIPPED) ? 1 : 0;
		}
		
		@Override
		 public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
			return getDefaultState().withProperty(FLIPPED, getFacingFromEntity(pos, placer));
		}

		public static boolean getFacingFromEntity(BlockPos pos, EntityLivingBase entity) {
			EnumFacing facing = entity.getHorizontalFacing();
			if (facing == EnumFacing.EAST || facing == EnumFacing.WEST)
				return true;
			return false;
		}

		@Override
		protected BlockStateContainer createBlockState() {
			return new BlockStateContainer(this, new IProperty[] { FLIPPED });
		}

		public static final AxisAlignedBB CORNER_NW_AABB = new AxisAlignedBB(0D, 0D, 0D, 0.25D, 1D, 0.25D);
	    public static final AxisAlignedBB CORNER_SW_AABB = new AxisAlignedBB(0D, 0D, 0.75D, 0.25D, 1D, 1D);
	    public static final AxisAlignedBB CORNER_NE_AABB = new AxisAlignedBB(0.75D, 0D, 0D, 1D, 1D, 0.25D);
	    public static final AxisAlignedBB CORNER_SE_AABB = new AxisAlignedBB(0.75D, 0D, 0.75D, 1D, 1D, 1D);
	    
	    public static final AxisAlignedBB MID_NW_AABB = new AxisAlignedBB(0.25D, 0D, 0.25D, 0.5D, 1D, 0.5D);
	    public static final AxisAlignedBB MID_SW_AABB = new AxisAlignedBB(0.25D, 0D, 0.5D, 0.5D, 1D, 0.75D);
	    public static final AxisAlignedBB MID_NE_AABB = new AxisAlignedBB(0.5D, 0D, 0.25D, 0.75D, 1D, 0.5D);
	    public static final AxisAlignedBB MID_SE_AABB = new AxisAlignedBB(0.5D, 0D, 0.5D, 0.75D, 1D, 0.75D);

	    @Override
		public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
			if (!isActualState)
				state = state.getActualState(world, pos);

			if (state.getValue(FLIPPED)) {
				addCollisionBoxToList(pos, entityBox, collidingBoxes, CORNER_NW_AABB);
				addCollisionBoxToList(pos, entityBox, collidingBoxes, MID_NW_AABB);
				addCollisionBoxToList(pos, entityBox, collidingBoxes, MID_SE_AABB);
				addCollisionBoxToList(pos, entityBox, collidingBoxes, CORNER_SE_AABB);
			}

			if (!state.getValue(FLIPPED)) {
				addCollisionBoxToList(pos, entityBox, collidingBoxes, CORNER_NE_AABB);
				addCollisionBoxToList(pos, entityBox, collidingBoxes, MID_NE_AABB);
				addCollisionBoxToList(pos, entityBox, collidingBoxes, MID_SW_AABB);
				addCollisionBoxToList(pos, entityBox, collidingBoxes, CORNER_SW_AABB);
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