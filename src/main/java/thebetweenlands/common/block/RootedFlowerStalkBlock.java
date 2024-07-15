package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class RootedFlowerStalkBlock extends UnderwaterPlantBlock {

	private final Holder<Block> flower;

	public RootedFlowerStalkBlock(Holder<Block> flower, Properties properties) {
		super(properties);
		this.flower = flower;
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return level.getBlockState(pos.above()).is(this.flower) && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		level.setBlockAndUpdate(pos.above(), this.flower.value().defaultBlockState());
	}
}
