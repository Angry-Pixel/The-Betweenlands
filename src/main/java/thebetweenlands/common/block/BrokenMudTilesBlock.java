package thebetweenlands.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;

public class BrokenMudTilesBlock extends HorizontalDirectionalBlock {

	public BrokenMudTilesBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return null;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		double d0 = pos.getX() + 0.375D;
		double d1 = pos.getY();
		double d2 = pos.getZ() + 0.375D;
		int distance;
		for (distance = 1; distance < 10; distance++) {
			if (state.blocksMotion() && !state.liquid())
				break;

			if (distance > 1) {
				double d3 = d0 + (double) random.nextFloat() * 0.25F;
				double d5 = (d1 + distance) - 0.05D;
				double d7 = d2 + (double) random.nextFloat() * 0.25F;
				//BLParticles.CAVE_WATER_DRIP.spawn(level, d3, d5, d7).setRBGColorF(0.4118F, 0.2745F, 0.1568F);
			}
		}
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		if (!level.isClientSide()) {
			if (entity instanceof Player && !entity.isSteppingCarefully()) {
				level.playSound(null, pos, state.getSoundType(level, pos, entity).getBreakSound(), SoundSource.BLOCKS, 0.5F, 1.0F);
				level.levelEvent(2001, pos, Block.getId(BlockRegistry.MUD_TILES.get().defaultBlockState())); //this will do unless we want specific particles
				level.setBlockAndUpdate(pos, BlockRegistry.STAGNANT_WATER.get().defaultBlockState());
			}
		}
	}

	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		return level.setBlockAndUpdate(pos, BlockRegistry.STAGNANT_WATER.get().defaultBlockState());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}
