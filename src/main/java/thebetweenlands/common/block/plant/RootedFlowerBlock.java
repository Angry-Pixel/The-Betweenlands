package thebetweenlands.common.block.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;

import javax.annotation.Nullable;

public class RootedFlowerBlock extends PlantBlock {

	private final Holder<Block> root;

	public RootedFlowerBlock(Holder<Block> root, Properties properties) {
		super(PlantBlock.GRASS_SHAPE, false, properties);
		this.root = root;
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
		return level.getBlockState(pos).is(BlockRegistry.SWAMP_WATER) || level.getBlockState(pos).is(Blocks.WATER) || level.getBlockState(pos).is(this.root);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		return level.getBlockState(currentPos.below()).is(this.root) ? super.updateShape(state, facing, facingState, level, currentPos, facingPos) : Blocks.AIR.defaultBlockState();
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		level.setBlockAndUpdate(pos.below(), this.root.value().defaultBlockState().setValue(UnderwaterPlantBlock.IS_SWAMP_WATER, level.getFluidState(pos.below()).is(FluidRegistry.SWAMP_WATER_STILL.get())));
	}
}
