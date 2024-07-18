package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thebetweenlands.common.block.HollowLogBlock;
import thebetweenlands.common.registries.BlockRegistry;

public class SmallHollowLogFeature extends Feature<NoneFeatureConfiguration> {
	public SmallHollowLogFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		return generate(context.level(), context.random(), context.origin());
	}

	public boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		int length = rand.nextInt(3) + 3;
		int offsetX = rand.nextInt(2), offsetZ = 1 - offsetX;

		for (int l = 0; l < length; l++)
			if (!level.getBlockState(pos.offset(offsetX * l, 0, offsetZ * l)).isAir() || level.getBlockState(pos.offset(offsetX * l, -1, offsetZ * l)).isAir())
				return false;

		boolean wasLastBranch = false;
		BlockState log = BlockRegistry.HOLLOW_LOG.get().defaultBlockState();
		for (int l = 0; l < length; l++) {
			if (offsetX != 0)
				log = log.setValue(HollowLogBlock.AXIS, Direction.Axis.X);
			else if (offsetZ != 0)
				log = log.setValue(HollowLogBlock.AXIS, Direction.Axis.Z);
			this.setBlock(level, pos.offset(offsetX * l, 0, offsetZ * l), log);

			if (wasLastBranch)
				wasLastBranch = false;
			else if (rand.nextInt(6) == 0) {
				wasLastBranch = true;
				BlockState log2 = BlockRegistry.HOLLOW_LOG.get().defaultBlockState();
				if (offsetX != 0)
					log2 = log2.setValue(HollowLogBlock.AXIS, Direction.Axis.Z);
				else if (offsetZ != 0)
					log2 = log2.setValue(HollowLogBlock.AXIS, Direction.Axis.X);
				BlockPos newPos = pos.offset(offsetX * l, 0, offsetZ * l);
				if (rand.nextInt(2) == 0)
					newPos = newPos.offset(offsetZ != 0 ? 1 : 0, 0, offsetX != 0 ? 1 : 0);
				else
					newPos = newPos.offset(offsetZ != 0 ? -1 : 0, 0, offsetX != 0 ? -1 : 0);
				if (level.getBlockState(newPos).isAir())
					this.setBlock(level, newPos, log2);
			}
		}
		return true;
	}
}
