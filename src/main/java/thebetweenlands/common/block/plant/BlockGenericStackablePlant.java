package thebetweenlands.common.block.plant;

import java.util.Random;

import net.minecraft.block.Block;
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
import thebetweenlands.common.block.BlockStateContainerHelper;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;

public class BlockGenericStackablePlant extends BlockGenericPlant implements IStateMappedBlock {
	public static final PropertyBool IS_TOP = PropertyBool.create("is_top");
	public static final PropertyBool IS_BOTTOM = PropertyBool.create("is_bottom");
	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 15);
	protected int maxHeight = 3;
	protected boolean breaksLower = false;

	public BlockGenericStackablePlant(boolean breaksLower) {
		super();
		this.breaksLower = breaksLower;
		this.setDefaultState(this.blockState.getBaseState().withProperty(IS_TOP, true).withProperty(IS_BOTTOM, false));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[]{AGE, IS_TOP, IS_BOTTOM});
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		boolean isTop = worldIn.getBlockState(pos.up()).getBlock() != this;
		boolean isBottom = worldIn.getBlockState(pos.down()).getBlock() != this;
		return state.withProperty(IS_TOP, isTop).withProperty(IS_BOTTOM, isBottom);
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
		BlockPos offsetPos;
		//Up
		for (int offset = 1; this.isSamePlant(worldIn.getBlockState(offsetPos = pos.up(offset)).getBlock()); ++offset) {
			if (!player.capabilities.isCreativeMode) {
				worldIn.destroyBlock(offsetPos, true);
			} else {
				worldIn.setBlockToAir(offsetPos);
			}
		}
		if(this.breaksLower) {
			//Down
			for (int offset = 1; this.isSamePlant(worldIn.getBlockState(offsetPos = pos.down(offset)).getBlock()); ++offset) {
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
	 * Returns true if the specified block should be counted as the same plant
	 * @param block
	 * @return
	 */
	protected boolean isSamePlant(Block block) {
		return block == this;
	}

	/**
	 * Sets the maximum height this plant should naturally grow
	 * @param maxHeight
	 * @return
	 */
	public BlockGenericStackablePlant setMaxHeight(int maxHeight) {
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
	protected boolean canSustainBush(IBlockState state) {
		return state.getBlock() == this || SoilHelper.canSustainPlant(state);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
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
		return world.isAirBlock(pos.up()) && height < this.maxHeight;
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
        return super.canPlaceBlockAt(worldIn, pos) && height <= this.maxHeight;
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
		builder.ignore(AGE);
	}
}
