package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.feature.config.ChanceConfiguration;

public class LifeGemOreFeature extends Feature<ChanceConfiguration> {

	public LifeGemOreFeature(Codec<ChanceConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<ChanceConfiguration> context) {
		return false;
	}

	public boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos, ChanceConfiguration config) {
		if (level.getBlockState(pos).is(BlockRegistry.SWAMP_WATER) && level.getBlockState(pos.below()).is(BlockRegistry.PITSTONE)) {
			boolean oreGen = rand.nextInt(1 + config.chance()) == 0;
			int height = 0;

			while (level.getBlockState(pos.offset(0, height++, 0)).is(BlockRegistry.SWAMP_WATER) && height < 8) {
				height--;
			}
			if (height >= 2) {
				height = rand.nextInt(Math.min(height - 1, 6)) + 2;
				int oreBlock = rand.nextInt(height);
				for (int i = 0; i <= height; i++) {
					BlockState state;
					if (oreGen && (i == oreBlock || rand.nextInt(1 + config.chance() / 2) == 0)) {
						state = BlockRegistry.LIFE_CRYSTAL_ORE_STALACTITE.get().defaultBlockState();
					} else {
						state = BlockRegistry.LIFE_CRYSTAL_STALACTITE.get().defaultBlockState();
					}
					level.setBlock(pos.offset(0, i, 0), state, 2);
				}
				return true;
			}
		}
		return false;
	}
}
