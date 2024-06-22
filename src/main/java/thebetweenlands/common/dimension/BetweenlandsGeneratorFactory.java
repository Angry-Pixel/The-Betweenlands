package thebetweenlands.common.dimension;

import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.util.CubicSpline;
import net.minecraft.world.level.biome.TerrainShaper;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouterWithOnlyNoises;
import net.minecraft.world.level.levelgen.NoiseSamplingSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.NoiseSlider;
import net.minecraftforge.common.world.ForgeWorldPreset.IBasicChunkGeneratorFactory;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.BetweenlandsBiomeProvider;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands.BetweenlandsGeneratorSettings;

public class BetweenlandsGeneratorFactory implements IBasicChunkGeneratorFactory {

	@Override
	public ChunkGenerator createChunkGenerator(RegistryAccess registryAccess, long seed) {

		return new ChunkGeneratorBetweenlands(null, null, new BetweenlandsBiomeProvider(registryAccess, seed), seed,
			new Holder.Direct<>(new NoiseGeneratorSettings(new NoiseSettings(-64, 256, new NoiseSamplingSettings(0, 0, 0, 0),
				new NoiseSlider(0, 0, 0), new NoiseSlider(0, 0, 0), 1, 1, new TerrainShaper(CubicSpline.constant(0), CubicSpline.constant(0), CubicSpline.constant(0))),
				BlockRegistry.PITSTONE.get().defaultBlockState(), BlockRegistry.SWAMP_WATER_BLOCK.get().defaultBlockState(),
				new NoiseRouterWithOnlyNoises(DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0), DensityFunctions.constant(0)),
				SurfaceRuleData.nether(), 63, false, false, false, false)), new BetweenlandsGeneratorSettings(-28, false, true));
	}

}
