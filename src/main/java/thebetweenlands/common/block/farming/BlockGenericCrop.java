package thebetweenlands.common.block.farming;

import java.util.Random;

import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.common.block.BlockStateContainerHelper;
import thebetweenlands.common.block.SoilHelper;
import thebetweenlands.common.block.plant.BlockStackablePlant;
import thebetweenlands.util.AdvancedStateMap;

public class BlockGenericCrop extends BlockStackablePlant implements IGrowable {
	public static final PropertyBool DECAYED = PropertyBool.create("decayed");
	public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 3);

	public BlockGenericCrop() {
		super(true);
		this.setMaxHeight(1);
		this.setDefaultState(this.getDefaultState().withProperty(DECAYED, false));
	}

	public boolean isDecayed(IBlockAccess world, BlockPos pos) {
		for(int i = 0; i < this.maxHeight + 1; i++) {
			IBlockState blockState = world.getBlockState(pos);
			if(blockState.getBlock() instanceof BlockGenericDugSoil) {
				return blockState.getValue(BlockGenericDugSoil.DECAYED);
			}
			pos = pos.down();
		}
		return false;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		state = super.getActualState(state, worldIn, pos);
		return state.withProperty(DECAYED, this.isDecayed(worldIn, pos)).withProperty(STAGE, MathHelper.floor_float(state.getValue(AGE) / 15.0f * 3.0f));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return BlockStateContainerHelper.extendBlockstateContainer(super.createBlockState(), DECAYED, STAGE);
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		return (this.maxHeight > 1 && this.isSamePlant(state)) || SoilHelper.canSustainCrop(state);
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos.down());
		return super.canPlaceBlockAt(worldIn, pos) && 
				!(state.getBlock() instanceof BlockGenericDugSoil && state.getValue(BlockGenericDugSoil.DECAYED)) &&
				!(state.getBlock() instanceof BlockGenericCrop && state.getValue(AGE) < 15);
	}

	@Override
	protected boolean canGrowUp(World world, BlockPos pos, IBlockState state, int height) {
		return !state.getValue(DECAYED) && super.canGrowUp(world, pos, state, height);
	}

	@Override
	protected void growUp(World world, BlockPos pos) {
		world.setBlockState(pos.up(), this.getDefaultState().withProperty(DECAYED, world.getBlockState(pos).getValue(DECAYED)));
	}

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		if(this.isDecayed(worldIn, pos)) {
			return false;
		}
		if(state.getValue(AGE) < 15) {
			return true;
		}
		int height;
		for (height = 1; worldIn.getBlockState(pos.down(height)).getBlock() == this; ++height);
		return this.canGrowUp(worldIn, pos, state, height);
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		int age = state.getValue(AGE) + 1;
		if(age > 15) {
			age = 15;
			int height;
			for (height = 1; worldIn.getBlockState(pos.down(height)).getBlock() == this; ++height);
			if(this.canGrowUp(worldIn, pos, state, height)) {
				this.growUp(worldIn, pos);
			}
		}
		worldIn.setBlockState(pos, state.withProperty(AGE, age));
	}

	@Override
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		super.setStateMapper(builder);
		builder.ignore(DECAYED).withPropertySuffixTrue(DECAYED, "decayed");
	}
}
