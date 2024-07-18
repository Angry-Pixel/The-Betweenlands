package thebetweenlands.common.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import thebetweenlands.common.block.entity.LootPotBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.LootTableRegistry;

public class CavePotsFeature extends Feature<NoneFeatureConfiguration> {
	public CavePotsFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		return false;
	}

	public boolean generate(WorldGenLevel level, RandomSource rand, BlockPos pos) {
		if (pos.getY() > 70)
			return false;

		for (int xx = -2; xx <= 2; xx++) {
			for (int zz = -2; zz <= 2; zz++) {
				for (int yy = -1; yy <= 1; yy++) {
					double dist = Mth.sqrt(xx * xx + yy * yy + zz * zz);
					if (rand.nextInt(Mth.ceil(dist / 1.4D) + 1) == 0) {
						BlockPos offset = pos.offset(xx, yy, zz);
						if (level.getBlockState(offset).isAir()) {
							BlockState surface = level.getBlockState(offset.below());
							if (surface.is(BlockRegistry.BETWEENSTONE) || surface.is(BlockRegistry.PITSTONE)) { //TODO: replace with Tag check?
								this.setBlock(level, offset, getRandomPot(rand));
								if (level.getBlockEntity(offset) instanceof LootPotBlockEntity lootPot) {
									lootPot.setLootTable(LootTableRegistry.CAVE_POT, rand.nextLong());
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

	private BlockState getRandomPot(RandomSource rand) {
		return switch (rand.nextInt(3)) {
			case 1 -> BlockRegistry.LOOT_POT_2.get().defaultBlockState();
			case 2 -> BlockRegistry.LOOT_POT_3.get().defaultBlockState();
			default -> BlockRegistry.LOOT_POT_1.get().defaultBlockState();
		};
	}
}
