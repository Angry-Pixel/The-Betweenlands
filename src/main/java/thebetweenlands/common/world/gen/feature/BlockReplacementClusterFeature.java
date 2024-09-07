package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import thebetweenlands.util.WorldGenUtil;
import thebetweenlands.common.world.gen.feature.config.BlockReplacementConfiguration;

public class BlockReplacementClusterFeature extends Feature<BlockReplacementConfiguration> {

	public BlockReplacementClusterFeature(Codec<BlockReplacementConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<BlockReplacementConfiguration> context) {
		return generate(context.level(), context.random(), context.origin(), context.config());
	}

	public boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos, BlockReplacementConfiguration config) {
		boolean generated = false;
		pos = WorldGenUtil.loopUntilSolid(level, pos);
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		for (int i = 0; i < config.attempts; i++) {
			mutable = WorldGenUtil.randomOffset(rand, mutable, pos, config.offset, config.offset / 2 + 1, config.offset);

			if (level.isAreaLoaded(mutable, 1)) {
				BlockState state = level.getBlockState(mutable);
				for (OreConfiguration.TargetBlockState target : config.targets) {
					if (target.target.test(state, rand)) {
						BlockState setState = target.state;
						if (config.inherit) {
							setState.getBlock().withPropertiesOf(state);
						}
						this.setBlock(level, mutable, setState);
						generated = true;
					}
				}
			}
		}
		return generated;
	}
}
