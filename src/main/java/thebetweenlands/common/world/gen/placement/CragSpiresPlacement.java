package thebetweenlands.common.world.gen.placement;

import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import thebetweenlands.common.registries.PlacementModifierRegistry;
import thebetweenlands.common.world.gen.feature.config.SimplexNoiseConfiguration;
import thebetweenlands.common.world.gen.generators.util.EarlyGeneratorHelper;
import thebetweenlands.common.world.gen.generators.util.SimplexData;
import thebetweenlands.common.world.gen.placement.util.BLPlacementModifierHelper;
import thebetweenlands.common.world.gen.placement.util.BLPlacementModifierHelper.BiomeCheckContext;

public class CragSpiresPlacement extends PlacementModifier {

	public static final MapCodec<CragSpiresPlacement> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					SimplexNoiseConfiguration.CODEC.fieldOf("spire_noise").forGetter(CragSpiresPlacement::spireNoise),
					Codec.DOUBLE.fieldOf("noise_value_multiplier").forGetter(CragSpiresPlacement::noiseValueMultiplier),
					Codec.DOUBLE.fieldOf("noise_value_offset").forGetter(CragSpiresPlacement::noiseValueOffset),
					Codec.DOUBLE.fieldOf("spire_height_factor").forGetter(CragSpiresPlacement::spireHeightFactor),
					ExtraCodecs.intRange(0, 16).fieldOf("spire_check_radius").forGetter(CragSpiresPlacement::spireCheckRadius),
					Codec.BOOL.optionalFieldOf("ignore_biomes", false).forGetter(CragSpiresPlacement::ignoreBiomes)
			).apply(instance, CragSpiresPlacement::new));

	private final SimplexNoiseConfiguration spireNoise;
	private final double noiseValueMultiplier;
	private final double noiseValueOffset;
	private final double spireHeightFactor;
	private final int spireCheckRadius;
	private final boolean ignoreBiomes;
	
	public CragSpiresPlacement(SimplexNoiseConfiguration spireNoise, double noiseValueMultiplier, double noiseValueOffset, double spireHeightFactor, int spireCheckRadius, boolean ignoreBiomes) {
		this.spireNoise = spireNoise;
		this.noiseValueMultiplier = noiseValueMultiplier;
		this.noiseValueOffset = noiseValueOffset;
		this.spireHeightFactor = spireHeightFactor;
		this.spireCheckRadius = spireCheckRadius;
		this.ignoreBiomes = ignoreBiomes;
	}
	
	public static CragSpiresPlacement of(double spireNoiseScale, int spireNoiseOctaves, double noiseValueMultiplier, double noiseValueOffset, double spireHeightFactor, int spireCheckRadius) {
		return new CragSpiresPlacement(new SimplexNoiseConfiguration(spireNoiseScale, spireNoiseOctaves), noiseValueMultiplier, noiseValueOffset, spireHeightFactor, spireCheckRadius, false);
	}
	
	public static CragSpiresPlacement of(SimplexNoiseConfiguration spireNoise, double noiseValueMultiplier, double noiseValueOffset, double spireHeightFactor, int spireCheckRadius) {
		return new CragSpiresPlacement(spireNoise, noiseValueMultiplier, noiseValueOffset, spireHeightFactor, spireCheckRadius, false);
	}
	
	public static CragSpiresPlacement of(SimplexNoiseConfiguration spireNoise, double noiseValueMultiplier, double noiseValueOffset, double spireHeightFactor, int spireCheckRadius, boolean ignoreBiomes) {
		return new CragSpiresPlacement(spireNoise, noiseValueMultiplier, noiseValueOffset, spireHeightFactor, spireCheckRadius, ignoreBiomes);
	}
	
	public SimplexNoiseConfiguration spireNoise() {
		return this.spireNoise;
	}
	
	public double noiseValueMultiplier() {
		return this.noiseValueMultiplier;
	}

	public double noiseValueOffset() {
		return this.noiseValueOffset;
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
	
	@Override
	public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
		final int noiseSize = (16 + 2 * this.spireCheckRadius);
		
		SimplexData spireNoiseData = this.spireNoise.getNoise(context.getLevel().getSeed());

		int noiseMinX = pos.getX() - this.spireCheckRadius;
		int noiseMinZ = pos.getZ() - this.spireCheckRadius;
		
		// length is `noiseSize * noiseSize`
		double[] spireNoiseValues = EarlyGeneratorHelper.computeNoiseRawWithSize(spireNoiseData.noiseGenerator(), noiseMinX, noiseMinZ, this.spireNoise.noiseScale(), noiseSize);
		
		BiomeCheckContext biomeCheckContext = new BiomeCheckContext(context);
		
		boolean[] validBlocks = new boolean[256];
		
		// TODO this isn't the fastest way to find which cells are valid
		MutableBlockPos mutablePos = new MutableBlockPos();
		for(int x = -this.spireCheckRadius; x < 16 + this.spireCheckRadius; ++x) {
			for(int z = -this.spireCheckRadius; z < 16 + this.spireCheckRadius; ++z) {
				final int noiseX = x + this.spireCheckRadius;
				final int noiseZ = z + this.spireCheckRadius;
				final int noiseIndex = noiseX * noiseSize + noiseZ;
				
				double noise = spireNoiseValues[noiseIndex] * this.noiseValueMultiplier + this.noiseValueOffset;

				// The height of the spire above the water level
				final double spireHeight = -noise * this.spireHeightFactor;
				
				// If a spire wouldn't generate here, continue
				if(spireHeight < 1) {
					continue;
				}

				// If a spire would generate here but this is the wrong biome, continue
				if(!ignoreBiomes) {
					final int posX = pos.getX() + x;
					final int posZ = pos.getZ() + z;
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

	@Override
	public PlacementModifierType<?> type() {
		return PlacementModifierRegistry.CRAG_SPIRES_PLACEMENT.get();
	}

}
