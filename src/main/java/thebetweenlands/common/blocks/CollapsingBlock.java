package thebetweenlands.common.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

//TODO: incomplete?
public class CollapsingBlock extends FallingBlock {

	public static final MapCodec<CollapsingBlock> CODEC = BlockBehaviour.simpleCodec(CollapsingBlock::new);

	public CollapsingBlock(Properties props) {
		super(props);
	}

	@Override
	protected MapCodec<? extends FallingBlock> codec() {
		return CODEC;
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		if (!level.isClientSide()) {
			if (entity instanceof Player && !entity.isSteppingCarefully()) {
				level.scheduleTick(pos, this, 3); //TODO: because tick rate methods were removed in favour of ints, verify the delay
			}
		}
	}

	@Override
	public void onLand(Level level, BlockPos pos, BlockState state, BlockState replace, FallingBlockEntity entity) {
		if (!level.isClientSide()) {
			level.playLocalSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, soundType.getStepSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, false);
			level.levelEvent(null, 2001, pos.above(), Block.getId(level.getBlockState(pos)));
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		}
	}
}
