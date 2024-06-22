package thebetweenlands.common.world.gen.biome.decorator.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import thebetweenlands.common.features.BetweenlandsDecoratorConfiguration;
import thebetweenlands.common.generation.trees.WeedwoodTree;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.gen.feature.legacy.WorldGenNibbletwigTree;

import java.util.Random;

public class SwamplandsDecoratorFeature extends DecoratorFeature {

	WorldGenNibbletwigTree generator = new WorldGenNibbletwigTree();

	public SwamplandsDecoratorFeature(Codec<BetweenlandsDecoratorConfiguration> p_65786_) {
		super(p_65786_, false);
	}

	// Here we put all our feature generator functions
	@Override
	public boolean place(FeaturePlaceContext<BetweenlandsDecoratorConfiguration> context) {

		Random rand = context.random();
		BlockPos pos = context.origin(); // used to find our chunk
		WorldGenLevel level = context.level();
		ChunkGenerator chunk = context.chunkGenerator();

		// if is a betweenlands chunk gen
		if (chunk instanceof ChunkGeneratorBetweenlands) {
			ChunkGeneratorBetweenlands blchunk = (ChunkGeneratorBetweenlands) chunk;

			int biome = blchunk.biomesForGeneration[0];

			// Run features

			// Weedwood tree
			for (int i = 0; i <= 80; i++) {
				BlockPos tempPos = pos.offset(rand.nextInt(16) - 8, 0, rand.nextInt(16) - 8);

				if (canShortThingsGenerateHere(blchunk, tempPos)) {
					if (1 == blchunk.betweenlandsBiomeProvider.biomeCache.getBiome(tempPos.getX(), tempPos.getZ(), 0)) {

						// TODO: Find surface
						tempPos = tempPos.offset(0, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, tempPos.getX(), tempPos.getZ()), 0);

						if (level.getBlockState(tempPos).getBlock() == Blocks.AIR && level.getBlockState(tempPos.below()).getBlock() != Blocks.AIR) {
							WeedwoodTree.generate(level, tempPos, rand, false);
						}
					}
				}
			}

			// Nibbiletwig tree
			for (int i = 0; i <= 100; i++) {
				BlockPos tempPos = pos.offset(rand.nextInt(16) - 8, 0, rand.nextInt(16) - 8);

				if (canShortThingsGenerateHere(blchunk, tempPos)) {

					// TODO: Find surface
					tempPos = tempPos.offset(0, level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, tempPos.getX(), tempPos.getZ()), 0);

					if (level.getBlockState(tempPos).getBlock() == Blocks.AIR && level.getBlockState(tempPos.below()).getBlock() != Blocks.AIR) {
						generator.generate(level, rand, tempPos);
					}
				}
			}
		}


		return true;
	}
}
