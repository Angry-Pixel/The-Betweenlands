package thebetweenlands.common.world.gen.placement;

import java.util.Optional;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import thebetweenlands.common.registries.PlacementModifierRegistry;
import thebetweenlands.common.world.gen.BetweenlandsChunkGenerator;
import thebetweenlands.common.world.gen.generators.util.EarlyGeneratorHelper;
import thebetweenlands.common.world.gen.placement.util.BLPlacementModifierHelper;
import thebetweenlands.common.world.gen.placement.util.BLPlacementModifierHelper.BiomeCheckContext;
import thebetweenlands.common.world.gen.util.BiomeWeightsMap;
import thebetweenlands.common.world.gen.util.SimplexData;
import thebetweenlands.common.world.gen.util.config.SimplexNoiseConfiguration;

public class CragSpiresPlacement extends PlacementModifier {

	public static final MapCodec<CragSpiresPlacement> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					SimplexNoiseConfiguration.CODEC.fieldOf("spire_noise").forGetter(CragSpiresPlacement::spireNoise),
					Codec.DOUBLE.fieldOf("spire_height_factor").forGetter(CragSpiresPlacement::spireHeightFactor),
					ExtraCodecs.intRange(0, 16).fieldOf("spire_check_radius").forGetter(CragSpiresPlacement::spireCheckRadius),
					Codec.BOOL.fieldOf("ignore_biomes").forGetter(CragSpiresPlacement::ignoreBiomes),
					Codec.BOOL.fieldOf("use_biome_weights").forGetter(CragSpiresPlacement::useBiomeWeights)
			).apply(instance, CragSpiresPlacement::new));

	private final SimplexNoiseConfiguration spireNoise;
	private final double spireHeightFactor;
	private final int spireCheckRadius;
	private final boolean ignoreBiomes;
	private final boolean useBiomeWeights;
	
	public CragSpiresPlacement(SimplexNoiseConfiguration spireNoise, double spireHeightFactor, int spireCheckRadius, boolean ignoreBiomes, boolean useBiomeWeights) {
		this.spireNoise = spireNoise;
		this.spireHeightFactor = spireHeightFactor;
		this.spireCheckRadius = spireCheckRadius;
		this.ignoreBiomes = ignoreBiomes;
		this.useBiomeWeights = useBiomeWeights;
	}
	
	public static CragSpiresPlacement of(double spireNoiseScale, int spireNoiseOctaves, double noiseValueMultiplier, double noiseValueOffset, double spireHeightFactor, int spireCheckRadius) {
		return new CragSpiresPlacement(SimplexNoiseConfiguration.of(spireNoiseOctaves, spireNoiseScale, noiseValueMultiplier, noiseValueOffset), spireHeightFactor, spireCheckRadius, false, true);
	}
	
	public static CragSpiresPlacement of(SimplexNoiseConfiguration spireNoise, double spireHeightFactor, int spireCheckRadius) {
		return new CragSpiresPlacement(spireNoise, spireHeightFactor, spireCheckRadius, false, true);
	}

	public static CragSpiresPlacement of(SimplexNoiseConfiguration spireNoise, double spireHeightFactor, int spireCheckRadius, boolean ignoreBiomes) {
		return new CragSpiresPlacement(spireNoise, spireHeightFactor, spireCheckRadius, ignoreBiomes, true);
	}

	public static CragSpiresPlacement of(SimplexNoiseConfiguration spireNoise, double spireHeightFactor, int spireCheckRadius, boolean ignoreBiomes, boolean useBiomeWeights) {
		return new CragSpiresPlacement(spireNoise, spireHeightFactor, spireCheckRadius, ignoreBiomes, useBiomeWeights);
	}
	
	public SimplexNoiseConfiguration spireNoise() {
		return this.spireNoise;
	}
	
	public double spireHeightFactor() {
		return this.spireHeightFactor;
	}

	public int spireCheckRadius() {
		return this.spireCheckRadius;
	}

	public boolean ignoreBiomes() {
		return this.ignoreBiomes;
	}

	public boolean useBiomeWeights() {
		return this.useBiomeWeights;
	}
	
	@Override
	public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
		final int noiseSize = (16 + 2 * this.spireCheckRadius);
		
		SimplexData spireNoiseData = this.spireNoise.getNoise(context.getLevel().getSeed());

		int noiseMinX = pos.getX() - this.spireCheckRadius;
		int noiseMinZ = pos.getZ() - this.spireCheckRadius;
		
		// length is `noiseSize * noiseSize`
		double[] spireNoiseValues = EarlyGeneratorHelper.computeNoiseRawWithSize(spireNoiseData.noiseGenerator(), noiseMinX, noiseMinZ, this.spireNoise.noiseScale(), noiseSize);
		final double noiseValueMultiplier = this.spireNoise().noiseValueMultiplier();
		final double noiseValueOffset = this.spireNoise().noiseValueOffset();
		
		BiomeCheckContext biomeCheckContext = new BiomeCheckContext(context);

		Optional<BiomeWeightsMap> biomeWeights = this.getBiomeWeights(context.generator(), pos);
		
		boolean[] validBlocks = new boolean[256];
		
		// TODO this isn't the fastest way to find which cells are valid
		MutableBlockPos mutablePos = new MutableBlockPos();
		for(int x = -this.spireCheckRadius; x < 16 + this.spireCheckRadius; ++x) {
			for(int z = -this.spireCheckRadius; z < 16 + this.spireCheckRadius; ++z) {
				final int noiseX = x + this.spireCheckRadius;
				final int noiseZ = z + this.spireCheckRadius;
				final int noiseIndex = noiseX * noiseSize + noiseZ;

				final int posX = pos.getX() + x;
				final int posZ = pos.getZ() + z;

				final int chunkX = SectionPos.blockToSectionCoord(posX);
				final int chunkZ = SectionPos.blockToSectionCoord(posZ);
				
				float weight = biomeWeights.isEmpty() ? 1.0F : biomeWeights.get().getWeightsFor(chunkX, chunkZ).get(posX & 15, posZ & 15);
				
				double noise = spireNoiseValues[noiseIndex] * weight * noiseValueMultiplier + noiseValueOffset;

				// The height of the spire above the water level
				final double spireHeight = -noise * this.spireHeightFactor;
				
				// If a spire wouldn't generate here, continue
				if(spireHeight < 1) {
					continue;
				}

				// If a spire would generate here but this is the wrong biome, continue
				if(!ignoreBiomes) {
					mutablePos.set(posX, context.getHeight(Types.OCEAN_FLOOR_WG, posX, posZ), posZ);
					
					if(!BLPlacementModifierHelper.checkBiomeAt(context, mutablePos, biomeCheckContext)) {
						continue;
					}
				}

				// X bounds (inclusive min, exclusive max)
				final int minBlockX = Math.max(x - this.spireCheckRadius, 0);
				final int maxBlockX = Math.min(x + this.spireCheckRadius + 1, 16);

				// Z bounds (inclusive min, exclusive max)
				final int minBlockZ = Math.max(z - this.spireCheckRadius, 0);
				final int maxBlockZ = Math.min(z + this.spireCheckRadius + 1, 16);
				
				// Buffer for the blocks that surround a spire
				for(int blockX = minBlockX; blockX < maxBlockX; ++blockX) {
					for(int blockZ = minBlockZ; blockZ < maxBlockZ; ++blockZ) {
						final int blockIndex = blockX * 16 + blockZ;
						
						// A spire would generate within the check radius of this block, mark it as such
						validBlocks[blockIndex] = true;
					}
				}
			}
		}
		
		
		// Create a stream containing the positions of all the valid blocks
		Stream.Builder<BlockPos> streamBuilder = Stream.builder();

		for(int x = 0; x < 16; ++x) {
			for(int z = 0; z < 16; ++z) {
				final int cellIndex = x * 16 + z;

				if(validBlocks[cellIndex]) {
					final int posX = pos.getX() + x;
					final int posZ = pos.getZ() + z;
					streamBuilder.add(new BlockPos(posX, context.getHeight(Types.OCEAN_FLOOR_WG, posX, posZ), posZ));
				}
			}
		}
		
		return streamBuilder.build();
	}

	public Optional<BiomeWeightsMap> getBiomeWeights(ChunkGenerator generator, BlockPos pos) {
		if(!this.useBiomeWeights || !(generator instanceof BetweenlandsChunkGenerator blGenerator)) {
			return Optional.empty();
		}

		final ChunkPos baseChunkPos = new ChunkPos(pos);
		
		BiomeWeightsMap regionWeights = new BiomeWeightsMap(baseChunkPos, spireCheckRadius == 0 ? 0 : 1);
		
		for(int x = -this.spireCheckRadius; x < 16 + this.spireCheckRadius; ++x) {
			for(int z = -this.spireCheckRadius; z < 16 + this.spireCheckRadius; ++z) {
				int chunkX = SectionPos.blockToSectionCoord(pos.getX() + x);
				int chunkZ = SectionPos.blockToSectionCoord(pos.getZ() + z);
				
				if(regionWeights.getWeightsFor(chunkX, chunkZ) == null) {
					regionWeights.setWeightsFor(chunkX, chunkZ, blGenerator.calculateBiomeWeights(new ChunkPos(chunkX, chunkZ)));
				}
			}
		}
		
		return Optional.of(regionWeights);
	}
	
	@Override
	public PlacementModifierType<?> type() {
		return PlacementModifierRegistry.CRAG_SPIRES_PLACEMENT.get();
	}

}
