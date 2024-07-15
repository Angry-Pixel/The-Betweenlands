package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;

public class RootedFlowerBlock extends PlantBlock {

	private final Holder<Block> root;

	public RootedFlowerBlock(Holder<Block> root, Properties properties) {
		super(properties);
		this.root = root;
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
		return level.getBlockState(pos.below()).is(BlockRegistry.SWAMP_WATER) || level.getBlockState(pos.below()).is(this.root);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return level.getBlockState(pos.below()).is(this.root) && this.root.value().defaultBlockState().canSurvive(level, pos);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		level.setBlockAndUpdate(pos.below(), this.root.value().defaultBlockState());
	}
}
