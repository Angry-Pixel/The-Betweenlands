package thebetweenlands.common.block.misc;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.common.block.BlockBLColored;
import thebetweenlands.common.item.EnumBLDyeColor;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockSamiteCanvasPanel extends BlockBLColored {
	
	public static final AxisAlignedBB AABB = new AxisAlignedBB(0.375D, 0.D, 0D, 0.625D, 1D, 1D);

	public BlockSamiteCanvasPanel(Material materialIn, SoundType soundType) {
		super(materialIn, soundType);
	}
	
	@Override
	 public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getFacingFromEntity(pos, placer) ? getDefaultState().withProperty(COLOR, EnumBLDyeColor.byMetadata(meta)) : BlockRegistry.SAMITE_CANVAS_PANEL_ROTATED.getDefaultState().withProperty(COLOR, EnumBLDyeColor.byMetadata(meta));
	}

	public static boolean getFacingFromEntity(BlockPos pos, EntityLivingBase entity) {
		EnumFacing facing = entity.getHorizontalFacing();
		if (facing == EnumFacing.EAST || facing == EnumFacing.WEST)
			return true;
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
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
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    	return BlockFaceShape.MIDDLE_POLE_THIN;
    }

	@Override
	public String getSubtypeName(int meta) {
		return "samite_canvas_panel_" + EnumBLDyeColor.byMetadata(meta).getName();
	}
}