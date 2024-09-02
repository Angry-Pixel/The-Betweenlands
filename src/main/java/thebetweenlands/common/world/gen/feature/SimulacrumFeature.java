package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import thebetweenlands.common.block.OfferingTableBlock;
import thebetweenlands.common.block.SimulacrumBlock;
import thebetweenlands.common.block.entity.GroundItemBlockEntity;
import thebetweenlands.common.block.entity.simulacrum.SimulacrumBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.SimulacrumEffectRegistry;
import thebetweenlands.common.world.gen.feature.config.SimulacrumConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimulacrumFeature extends Feature<SimulacrumConfiguration> {

	public SimulacrumFeature(Codec<SimulacrumConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<SimulacrumConfiguration> context) {
		return false;
	}

	public boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos, SimulacrumConfiguration config) {
		if (level.getBlockState(pos).isAir()) {
			for (int i = 0; i < 8 && level.getBlockState(pos).isAir(); i++) {
				pos = pos.below();
			}

			pos = pos.above();

			if (level.getBlockState(pos).isAir() && level.getBlockState(pos).isFaceSturdy(level, pos.below(), Direction.UP) && this.canGenerateHere(level, rand, pos)) {
				//SimulacrumBlock block = config.variants().get(rand.nextInt(config.variants().size()));
				Direction direction = null;

				if (this.shouldGenerateOfferingTable(rand)) {
					List<Direction> directions = new ArrayList<>(Direction.Plane.HORIZONTAL.stream().toList());
					Collections.shuffle(directions);

					for (Direction dir : directions) {
						BlockPos offset = pos.relative(dir);

						if (level.getBlockState(offset).isAir() && level.getBlockState(offset).isFaceSturdy(level, offset.below(), Direction.UP)) {
							this.setBlock(level, offset, BlockRegistry.OFFERING_TABLE.get().defaultBlockState().setValue(OfferingTableBlock.FACING, dir.getOpposite()));
							direction = dir;
							BlockEntity tile = level.getBlockEntity(offset);

							if (tile instanceof GroundItemBlockEntity) {
								if (config.lootTable() != null) {
									RandomizableContainer.setBlockEntityLootTable(level, rand, offset, config.lootTable());
								}
							}
							break;
						}
					}
				}

				BlockState state = config.variants().get(rand.nextInt(config.variants().size()))
					.setValue(SimulacrumBlock.FACING, direction == null ? Direction.Plane.HORIZONTAL.getRandomDirection(rand) : direction);
				this.setBlock(level, pos, state);

				BlockEntity tile = level.getBlockEntity(pos);
				if (tile instanceof SimulacrumBlockEntity simulacrum) {
					simulacrum.setEffect(SimulacrumEffectRegistry.EFFECTS.getEntries().stream().toList().get(rand.nextInt(SimulacrumEffectRegistry.EFFECTS.getEntries().size())).get());
				}

				int torches = rand.nextInt(3);

				for (int i = 0; i < 32 && torches > 0; i++) {
					int rx = rand.nextInt(5) - 2;
					int rz = rand.nextInt(5) - 2;

					if ((rx !=0 || rz != 0) && Mth.abs(rx) + Mth.abs(rz) <= 2) {
						BlockPos offset = pos.offset(rx, rand.nextInt(3) - 2, rz);

						if (level.getBlockState(offset).isAir() && level.getBlockState(offset).isFaceSturdy(level, offset.below(), Direction.UP)) {
							this.setBlock(level, offset, BlockRegistry.MUD_FLOWER_POT_CANDLE.get().defaultBlockState());
							torches--;
						}
					}
				}
				return true;
			}
		}
		return false;
	}

	protected boolean canGenerateHere(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		return true;
	}

	protected boolean shouldGenerateOfferingTable(RandomSource rand) {
		return rand.nextInt(3) != 0;
	}
}
