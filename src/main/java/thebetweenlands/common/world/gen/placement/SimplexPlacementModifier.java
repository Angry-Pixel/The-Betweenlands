package thebetweenlands.common.world.gen.placement;

import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import thebetweenlands.api.world.BiomeWeights;
import thebetweenlands.common.registries.PlacementModifierRegistry;
import thebetweenlands.common.world.gen.BetweenlandsChunkGenerator;
import thebetweenlands.common.world.gen.generators.util.EarlyGeneratorHelper;
import thebetweenlands.common.world.gen.generators.util.SimplexCache;
import thebetweenlands.common.world.gen.generators.util.SimplexData;
import thebetweenlands.util.ExtraCodecs;

public final class SimplexPlacementModifier extends PlacementModifier {

	public static final MapCodec<SimplexPlacementModifier> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					ExtraCodecs.POSITIVE_DOUBLE.fieldOf("noise_scale").forGetter(SimplexPlacementModifier::noiseScale),
					Codec.DOUBLE.fieldOf("noise_value_multiplier").forGetter(SimplexPlacementModifier::noiseValueMultiplier),
					Codec.DOUBLE.fieldOf("noise_value_offset").forGetter(SimplexPlacementModifier::noiseValueOffset),
					net.minecraft.util.ExtraCodecs.POSITIVE_INT.fieldOf("octaves").forGetter(SimplexPlacementModifier::octaves),
					Codec.BOOL.fieldOf("use_biome_weights").forGetter(SimplexPlacementModifier::useBiomeWeights)
			).apply(instance, SimplexPlacementModifier::new));

	private final double noiseScale;
	private final double noiseValueMultiplier;
	private final double noiseValueOffset;
	private final int octaves;
	private final boolean useBiomeWeights;

	private final SimplexCache noiseCache;
	
	private SimplexPlacementModifier(double noiseScale, double noiseValueMultiplier, double noiseValueOffset, int octaves, boolean useBiomeWeights) {
		this.noiseScale = noiseScale;
		this.noiseValueMultiplier = noiseValueMultiplier;
		this.noiseValueOffset = noiseValueOffset;
		this.octaves = octaves;
		this.useBiomeWeights = useBiomeWeights;
		
		this.noiseCache = new SimplexCache(octaves);
	}

	public final double noiseScale() {
		return this.noiseScale;
	}

	public final double noiseValueMultiplier() {
		return this.noiseValueMultiplier;
	}
	
	public final double noiseValueOffset() {
		return this.noiseValueOffset;
	}
	
	public final int octaves() {
		return this.octaves;
	}
	
	public final boolean useBiomeWeights() {
		return this.useBiomeWeights;
	}
	
	@Override
	public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {

		WorldGenLevel level = context.getLevel();
		long seed = level.getSeed();

		// Get or create noise generator for this seed
		SimplexData noise = this.noiseCache.getNoise(seed);

		final ChunkPos chunkPos = new ChunkPos(pos);
		
		final int offsetX = pos.getX() % 16;
		final int offsetZ = pos.getZ() % 16;

		// offset so that the column at [posX, posZ] will has the value of [chunkX * 16, chunkZ * 16]
		final int noiseX = chunkPos.getMinBlockX() - offsetX;
		final int noiseZ = chunkPos.getMaxBlockX() - offsetZ;
		
		// Compute noise values
		final double[] noiseValues = EarlyGeneratorHelper.computeNoiseRaw(noise.noiseGenerator(), noiseX, noiseZ, this.noiseScale());

		// Get biome weights (if applicable)
		// TODO better way of retrieving biome weights
		final BiomeWeights biomeWeights = this.useBiomeWeights() && context.generator() instanceof BetweenlandsChunkGenerator blGenerator ? blGenerator.calculateBiomeWeights(chunkPos) : null;
		final boolean useBiomeWeights = this.useBiomeWeights() && biomeWeights != null;
		
		// Get values from config
		final double noiseValueMultiplier = this.noiseValueMultiplier();
		final double noiseValueOffset = this.noiseValueOffset();
		
		final double noiseValueThreshold = noiseValueOffset / noiseValueMultiplier;
		
		Stream.Builder<BlockPos> streamBuilder = Stream.builder();
		for(int x = 0; x < 16; ++x) {
			for(int z = 0; z < 16; ++z) {
				final int index = x * 16 + z;

				float biomeWeight = useBiomeWeights ? biomeWeights.get(x, z) : 1.0F;
				
				if(noiseValues[index] * biomeWeight > noiseValueThreshold) {
					continue;
				}
				
				streamBuilder.accept(new BlockPos(chunkPos.getBlockX(x), pos.getY(), chunkPos.getBlockX(z)));
			}
		}
		
		return streamBuilder.build();
	}

	@Override
	public PlacementModifierType<?> type() {
		return PlacementModifierRegistry.SIMPLEX_COLUMNS_PLACEMENT.get();
	}

}
