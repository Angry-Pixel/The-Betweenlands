package thebetweenlands.common.block.misc;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockAmatePaperPane extends BlockConnectedPane {
	protected static final AxisAlignedBB[] AABB_BY_INDEX = new AxisAlignedBB[] {new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.625D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.375D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.625D), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};

	public BlockAmatePaperPane() {
		super(Material.CLOTH, true);
		this.setSoundType(SoundType.CLOTH);
		this.setHardness(0.3F);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
		return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.CUTOUT_MIPPED;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
		if (!isActualState)
		{
			state = this.getActualState(state, worldIn, pos);
		}

		addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[0]);

		if (((Boolean)state.getValue(NORTH)).booleanValue())
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[getBoundingBoxIndex(EnumFacing.NORTH)]);
		}

		if (((Boolean)state.getValue(SOUTH)).booleanValue())
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[getBoundingBoxIndex(EnumFacing.SOUTH)]);
		}

		if (((Boolean)state.getValue(EAST)).booleanValue())
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[getBoundingBoxIndex(EnumFacing.EAST)]);
		}

		if (((Boolean)state.getValue(WEST)).booleanValue())
		{
			addCollisionBoxToList(pos, entityBox, collidingBoxes, AABB_BY_INDEX[getBoundingBoxIndex(EnumFacing.WEST)]);
		}
	}

	private static int getBoundingBoxIndex(EnumFacing facing) {
		return 1 << facing.getHorizontalIndex();
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		state = this.getActualState(state, source, pos);
		return AABB_BY_INDEX[getBoundingBoxIndex(state)];
	}

	private static int getBoundingBoxIndex(IBlockState state)
	{
		int i = 0;

		if (((Boolean)state.getValue(NORTH)).booleanValue())
		{
			i |= getBoundingBoxIndex(EnumFacing.NORTH);
		}

		if (((Boolean)state.getValue(EAST)).booleanValue())
		{
			i |= getBoundingBoxIndex(EnumFacing.EAST);
		}

		if (((Boolean)state.getValue(SOUTH)).booleanValue())
		{
			i |= getBoundingBoxIndex(EnumFacing.SOUTH);
		}

		if (((Boolean)state.getValue(WEST)).booleanValue())
		{
			i |= getBoundingBoxIndex(EnumFacing.WEST);
		}

		return i;
	}

	@Override
	public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos) {
		return true;
	}
}
