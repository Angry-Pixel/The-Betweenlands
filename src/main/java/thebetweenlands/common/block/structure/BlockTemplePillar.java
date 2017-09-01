package thebetweenlands.common.block.structure;

import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.client.tab.BLCreativeTabs;

public class BlockTemplePillar extends BlockRotatedPillar {
	public static final AxisAlignedBB AABB_Y = new AxisAlignedBB(0.0625f, 0f, 0.0625f, .9375f, 1f, .9375f);
	public static final AxisAlignedBB AABB_X = new AxisAlignedBB(0f, 0.0625f, 0.0625f, 1f, .9375f, .9375f);
	public static final AxisAlignedBB AABB_Z = new AxisAlignedBB(0.0625f, 0.0625f, 0f, .9375f, .9375f, 1f);

	public BlockTemplePillar() {
		super(Material.ROCK);
		setHardness(1.5F);
		setResistance(10.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch (state.getValue(AXIS)) {
		case Y:
			return AABB_Y;
		case X:
			return AABB_X;
		default:
			return AABB_Z;
		}
	}

	@Override
	public boolean isBlockNormalCube(IBlockState worldIn) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return super.shouldSideBeRendered(blockState, blockAccess, pos, side) && (blockAccess.getBlockState(pos.offset(side)).getBlock() != this
				|| (blockState.getValue(AXIS) != blockAccess.getBlockState(pos.offset(side)).getValue(AXIS) || side.getAxis() != blockState.getValue(AXIS)));
	}

	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return side.getAxis() == base_state.getValue(AXIS);
	}
}
