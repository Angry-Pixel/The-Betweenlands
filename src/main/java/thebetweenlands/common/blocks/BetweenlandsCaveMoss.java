package thebetweenlands.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.ParticleRegistry;

public class BetweenlandsCaveMoss extends BetweenlandsHangingBlock {

	public BetweenlandsCaveMoss(Properties props, int growthChance) {
		super(props, growthChance);
	}

	@Override
	public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos pos) {
		return /*SurfaceType.UNDERGROUND.matches(blockState) || TODO: Tag*/ blockState.is(this);
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
		if (rand.nextInt(40) == 0) {
			float dripRange = 0.5F;
			float px = rand.nextFloat() - 0.5F;
			float py = rand.nextFloat();
			float pz = rand.nextFloat() - 0.5F;
			float u = Math.max(Math.abs(px), Math.abs(pz));
			px = px / u * dripRange + 0.5F;
			pz = pz / u * dripRange + 0.5F;
			level.sendParticles(ParticleRegistry.CAVE_WATER_DRIP.get(), pos.getX() + px, pos.getY() + py, pos.getZ() + pz, 1, 0.0D, 0.0D, 0.0D, 0.0D); //TODO verify
		}
	}
}
