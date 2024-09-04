package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thebetweenlands.common.registries.BlockRegistry;

public class SpeleothemFeature extends Feature<NoneFeatureConfiguration> {

	public SpeleothemFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		return generate(context.level(), context.random(), context.origin());
	}

	public boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		if (level.getBlockState(pos).isAir() && isBlockSupported(level.getBlockState(pos.above()))) {
			int height = 1;
			while (level.getBlockState(pos.offset(0, -height, 0)).isAir()) {
				height++;
			}
			if (height < 3) {
				return false;
			}

			boolean hasStalagmite = isBlockSupported(level.getBlockState(pos.offset(0, -height, 0)));
			int length = rand.nextInt(height < 11 ? height / 2 : 5) + 1;
			boolean isColumn = length == height / 2;

			for (int dy = 0; dy < length; dy++) {
				this.setBlock(level, pos.offset(0, -dy, 0), BlockRegistry.STALACTITE.get().defaultBlockState());

				if (hasStalagmite && (isColumn || dy < length - 1)) {
					this.setBlock(level, pos.offset(0, -height + dy + 1, 0), BlockRegistry.STALACTITE.get().defaultBlockState());
				}
			}
			return true;
		}
		return false;
	}

	private boolean isBlockSupported(BlockState state) {
		return state == BlockRegistry.BETWEENSTONE.get().defaultBlockState();
	}
}
