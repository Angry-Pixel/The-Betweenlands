package thebetweenlands.common.block.structure;

import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.common.block.BasicBlock;
import thebetweenlands.common.item.ItemBlockSlab;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockSlabBetweenlands extends BasicBlock implements BlockRegistry.ICustomItemBlock {
	public static final PropertyEnum<EnumBlockHalfBL> HALF = PropertyEnum.<EnumBlockHalfBL>create("half", EnumBlockHalfBL.class);
	protected static final AxisAlignedBB AABB_BOTTOM_HALF = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
	protected static final AxisAlignedBB AABB_TOP_HALF = new AxisAlignedBB(0.0D, 0.5D, 0.0D, 1.0D, 1.0D, 1.0D);

	@SuppressWarnings("deprecation")
	public BlockSlabBetweenlands(Block block) {
		super(block.getMaterial(block.getDefaultState()));
		this.setSoundType(block.getSoundType());
		this.setDefaultState(blockState.getBaseState().withProperty(HALF, EnumBlockHalfBL.BOTTOM));
		this.setHardness(2.0F);
		this.setResistance(10.0F);
		this.useNeighborBrightness = true;
	}

	@Nonnull
	@Override
	public ItemBlock getItemBlock() {
		return new ItemBlockSlab(this);
	}

	@Override
	protected boolean canSilkHarvest() {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return state.getValue(HALF).equals(EnumBlockHalfBL.FULL);
	}

	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		return (state.getValue(HALF).equals(EnumBlockHalfBL.BOTTOM) && face == EnumFacing.DOWN) || (state.getValue(HALF).equals(EnumBlockHalfBL.TOP) && face == EnumFacing.UP) || state.getValue(HALF).equals(EnumBlockHalfBL.FULL);
	}

	@Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		IBlockState state = getStateFromMeta(meta);
		return state.getValue(HALF).equals(EnumBlockHalfBL.FULL) ? state : (facing != EnumFacing.DOWN && (facing == EnumFacing.UP || (double) hitY <= 0.5D) ? state.withProperty(HALF, EnumBlockHalfBL.BOTTOM) : state.withProperty(HALF, EnumBlockHalfBL.TOP));
	}

	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random) {
		return state.getValue(HALF).equals(EnumBlockHalfBL.FULL) ? 2 : 1;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return state.getValue(HALF).equals(EnumBlockHalfBL.FULL);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumBlockHalfBL half = state.getValue(HALF);
		switch (half) {
			case TOP:
				return AABB_TOP_HALF;
			case BOTTOM:
				return AABB_BOTTOM_HALF;
			default:
				return FULL_BLOCK_AABB;
		}
	}

	@Override
	public boolean isTopSolid(IBlockState state) {
		return state.getValue(HALF).equals(EnumBlockHalfBL.FULL) || state.getValue(HALF).equals(EnumBlockHalfBL.TOP);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        if (state.getValue(HALF) == EnumBlockHalfBL.FULL) {
            return BlockFaceShape.SOLID;
        } else if (face == EnumFacing.UP && state.getValue(HALF) == EnumBlockHalfBL.TOP) {
            return BlockFaceShape.SOLID;
        } else {
            return face == EnumFacing.DOWN && state.getValue(HALF) == EnumBlockHalfBL.BOTTOM ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
        }
    }

	@Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
	    ItemStack heldItem = playerIn.getHeldItem(hand);
		if (!heldItem.isEmpty() && ((state.getValue(HALF).equals(EnumBlockHalfBL.TOP) && facing.equals(EnumFacing.DOWN)) || (state.getValue(HALF).equals(EnumBlockHalfBL.BOTTOM) && facing.equals(EnumFacing.UP)))){
			if (heldItem.getItem() == Item.getItemFromBlock(this)) {
				worldIn.setBlockState(pos, state.withProperty(HALF, EnumBlockHalfBL.FULL));
				if(!playerIn.capabilities.isCreativeMode)
					heldItem.setCount(heldItem.getCount() - 1);
				SoundType soundtype = this.getSoundType(state, worldIn, pos, playerIn);
				worldIn.playSound(playerIn, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
				return true;
			}
		}
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, HALF);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(HALF, EnumBlockHalfBL.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(HALF).ordinal();
	}

	public static enum EnumBlockHalfBL implements IStringSerializable {
		TOP("top"),
		BOTTOM("bottom"),
		FULL("full");

		private final String name;

		private EnumBlockHalfBL(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}

		@Override
		public String getName() {
			return this.name;
		}


		public static EnumBlockHalfBL byMetadata(int metadata) {
			if (metadata < 0 || metadata >= values().length) {
				metadata = 0;
			}
			return values()[metadata];
		}
	}
}
