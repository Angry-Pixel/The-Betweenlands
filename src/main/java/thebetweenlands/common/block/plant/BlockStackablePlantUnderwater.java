package thebetweenlands.common.block.plant;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.Fluid;
import thebetweenlands.common.block.BlockStateContainerHelper;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.registries.FluidRegistry;

public class BlockStackablePlantUnderwater extends BlockPlantUnderwater {
	public static final PropertyBool IS_TOP = BlockStackablePlant.IS_TOP;
	public static final PropertyBool IS_BOTTOM = BlockStackablePlant.IS_BOTTOM;
	public static final PropertyInteger AGE = BlockStackablePlant.AGE;
	protected int maxHeight = -1;
	protected boolean breaksLower = false;

	public BlockStackablePlantUnderwater() {
		this(FluidRegistry.SWAMP_WATER, Material.WATER, false);
	}

	public BlockStackablePlantUnderwater(boolean breaksLower) {
		this(FluidRegistry.SWAMP_WATER, Material.WATER, breaksLower);
	}

	public BlockStackablePlantUnderwater(Fluid fluid, Material materialIn) {
		this(fluid, materialIn, false);
	}

	public BlockStackablePlantUnderwater(Fluid fluid, Material materialIn, boolean breaksLower) {
		super(fluid, materialIn);
		this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, 0).withProperty(IS_TOP, true).withProperty(IS_BOTTOM, false));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return BlockStateContainerHelper.extendBlockstateContainer((ExtendedBlockState) super.createBlockState(), new IProperty[]{AGE, IS_TOP, IS_BOTTOM}, new IUnlistedProperty[0]);
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		boolean isTop = !this.isSamePlant(worldIn.getBlockState(pos.up()).getBlock());
		boolean isBottom = !this.isSamePlant(worldIn.getBlockState(pos.down()).getBlock());
		return state.withProperty(IS_TOP, isTop).withProperty(IS_BOTTOM, isBottom);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		//Up
		int height;
		for (height = 1; this.isSamePlant(worldIn.getBlockState(pos.up(height)).getBlock()); ++height);
		for (int offset = height; offset > 0; offset--) {
			if (!player.capabilities.isCreativeMode) {
				worldIn.destroyBlock(pos.up(offset), true);
			} else {
				worldIn.setBlockToAir(pos.up(offset));
			}
		}
		if(this.breaksLower) {
			//Down
			BlockPos offsetPos;
			for (int offset = 1; this.isSamePlant(worldIn.getBlockState(offsetPos = pos.down(offset)).getBlock()); offset++) {
				if (!player.capabilities.isCreativeMode) {
					worldIn.destroyBlock(offsetPos, true);
				} else {
					worldIn.setBlockToAir(offsetPos);
				}
			}
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	/**
	 * Returns true if the specified block should be considered as the same plant
	 * @param block
	 * @return
	 */
	protected boolean isSamePlant(Block block) {
		return block == this;
	}

	/**
	 * Sets the maximum height this plant should naturally grow.
	 * Set to -1 if the plant should grow until it reaches the surface.
	 * @param maxHeight
	 * @return
	 */
	public BlockStackablePlantUnderwater setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
		return this;
	}

	/**
	 * Returns the maximum height this plant should naturally grow
	 * @return
	 */
	public int getMaxHeight() {
		return this.maxHeight;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		this.checkAndDropBlock(worldIn, pos, state);

		int height;
		for (height = 1; worldIn.getBlockState(pos.down(height)).getBlock() == this; ++height);

		if (this.canGrow(worldIn, pos, state, height)) {
			int currentAge = ((Integer)state.getValue(AGE)).intValue();

			if (currentAge == 15) {
				this.growUp(worldIn, pos);
				worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(0)), 4);
			} else {
				worldIn.setBlockState(pos, state.withProperty(AGE, Integer.valueOf(currentAge + 1)), 4);
			}
		}
	}

	/**
	 * Returns whether the plant can grow higher
	 * @param world
	 * @param pos
	 * @param state
	 * @param height
	 * @return
	 */
	protected boolean canGrow(World world, BlockPos pos, IBlockState state, int height) {
		return world.getBlockState(pos.up()) != this && world.getBlockState(pos.up()).getMaterial() == Material.WATER && (this.maxHeight == -1 || height < this.maxHeight);
	}

	/**
	 * Grows the plant one block higher
	 * @param world
	 * @param pos Position of the currently highest block of the plant
	 */
	protected void growUp(World world, BlockPos pos) {
		world.setBlockState(pos.up(), this.getDefaultState());
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		int height;
		for (height = 1; worldIn.getBlockState(pos.down(height)).getBlock() == this; ++height);
		return super.canPlaceBlockAt(worldIn, pos) && (this.maxHeight == -1 || height - 1 < this.maxHeight);
	}

	@Override
	protected boolean canSustainPlant(IBlockState state) {
		return state.getBlock() == this || SoilHelper.canSustainUnderwaterPlant(state);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(AGE, Integer.valueOf(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((Integer)state.getValue(AGE)).intValue();
	}

	@Override
	public void setStateMapper(Builder builder) {
		super.setStateMapper(builder);
		builder.ignore(AGE);
	}
}
