package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import thebetweenlands.common.block.BarnacleBlock;
import thebetweenlands.util.WorldGenUtil;
import thebetweenlands.common.world.gen.feature.config.BlockPlaceConfiguration;

public class BarnacleClusterFeature extends Feature<BlockPlaceConfiguration> {

	public BarnacleClusterFeature(Codec<BlockPlaceConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<BlockPlaceConfiguration> context) {
		return generate(context.level(), context.random(), context.origin(), context.config());
	}

	public boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos, BlockPlaceConfiguration config) {
		boolean generated = false;
		pos = WorldGenUtil.loopUntilSolid(level, pos);
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		for (int i = 0; i < config.attempts(); i++) {
			mutable = WorldGenUtil.randomOffset(rand, mutable, pos, config.offset(), config.offset() / 2 + 1, config.offset());

			if (level.isAreaLoaded(mutable, 1) && level.getBlockState(mutable).getFluidState().is(FluidTags.WATER) && config.state().canSurvive(level, mutable)) {
				Direction direction = Direction.BY_ID.apply(rand.nextInt(Direction.values().length));
				Direction.Axis axis = direction.getAxis();
				Direction opposite = direction.getOpposite();
				boolean invalid = false;

				if (axis.isHorizontal() && !level.getBlockState(mutable.move(opposite)).isFaceSturdy(level, mutable, direction)) {
					invalid = true;
				} else if (axis.isVertical() && !level.getBlockState(mutable.move(opposite)).isFaceSturdy(level, mutable, Direction.UP)) {
					invalid = true;
				}

				if (!invalid) {
					BlockState state = config.state().setValue(BarnacleBlock.STAGE, rand.nextInt(2) + 1).setValue(DirectionalBlock.FACING, opposite);
					this.setBlock(level, mutable, state);
					generated = true;
				}
			}
		}

		return generated;
	}
}
