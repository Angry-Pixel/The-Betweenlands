package thebetweenlands.common.block.misc;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.common.block.BlockBLColored;
import thebetweenlands.common.item.EnumBLDyeColor;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockSamiteCanvasPanelRotated extends BlockBLColored {
	
	public static final AxisAlignedBB AABB = new AxisAlignedBB(0.D, 0D, 0.375D, 1D, 1D, 0.625D);

	public BlockSamiteCanvasPanelRotated(Material materialIn, SoundType soundType) {
		super(materialIn, soundType);
		setCreativeTab(null);
	}

	@Override
	 public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getFacingFromEntity(pos, placer) ? getDefaultState().withProperty(COLOR, EnumBLDyeColor.byMetadata(meta)) : BlockRegistry.SAMITE_CANVAS_PANEL.getDefaultState().withProperty(COLOR, EnumBLDyeColor.byMetadata(meta));
	}

	public static boolean getFacingFromEntity(BlockPos pos, EntityLivingBase entity) {
		EnumFacing facing = entity.getHorizontalFacing();
		if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH)
			return true;
		return false;
	}

	@Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(BlockRegistry.SAMITE_CANVAS_PANEL, 1, this.damageDropped(state));
    }

	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(BlockRegistry.SAMITE_CANVAS_PANEL);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@Override
	public int quantityDropped(Random rand) {
		return 1;
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
    	return BlockFaceShape.UNDEFINED;
    }

	@Override
	public String getSubtypeName(int meta) {
		return "samite_canvas_panel_rotated_" + EnumBLDyeColor.byMetadata(meta).getName();
	}

}