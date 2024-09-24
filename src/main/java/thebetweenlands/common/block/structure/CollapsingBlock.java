package thebetweenlands.common.block.structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.SoundRegistry;

public class CollapsingBlock extends FallingBlock {

	public static final MapCodec<CollapsingBlock> CODEC = simpleCodec(CollapsingBlock::new);

	public CollapsingBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends FallingBlock> codec() {
		return CODEC;
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {

	}

	@Override
	protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		return state;
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
			level.playSound(null, pos, SoundRegistry.CRUMBLE.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
			FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(level, pos, state);
			this.falling(fallingblockentity);
		}
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		if (!level.isClientSide()) {
			if (entity instanceof Player && !entity.isSteppingCarefully()) {
				level.scheduleTick(pos, this, this.getDelayAfterPlace());
			}
		}
	}

	@Override
	public void onLand(Level level, BlockPos pos, BlockState state, BlockState replace, FallingBlockEntity entity) {
		if (!level.isClientSide()) {
			level.playSound(null, pos, this.soundType.getStepSound(), SoundSource.BLOCKS, (this.soundType.getVolume() + 1.0F) / 2.0F, this.soundType.getPitch() * 0.8F);
			level.levelEvent(null, 2001, pos.above(), Block.getId(level.getBlockState(pos)));
			level.removeBlock(pos, false);
		}
	}
}
