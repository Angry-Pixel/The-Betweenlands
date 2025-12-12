package thebetweenlands.common.world.gen.placement;

import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
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
import thebetweenlands.common.world.gen.placement.util.BLPlacementModifierHelper;
import thebetweenlands.common.world.gen.placement.util.BLPlacementModifierHelper.BiomeCheckContext;
import thebetweenlands.common.world.gen.util.SimplexData;

public class CragSpiresPlacementOld extends PlacementModifier {

	public static final MapCodec<CragSpiresPlacementOld> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					SimplexNoiseConfiguration.CODEC.fieldOf("spire_noise").forGetter(CragSpiresPlacementOld::spireNoise),
					Codec.DOUBLE.fieldOf("noise_value_multiplier").forGetter(CragSpiresPlacementOld::noiseValueMultiplier),
					Codec.DOUBLE.fieldOf("noise_value_offset").forGetter(CragSpiresPlacementOld::noiseValueOffset),
					Codec.DOUBLE.fieldOf("spire_height_factor").forGetter(CragSpiresPlacementOld::spireHeightFactor),
					ExtraCodecs.intRange(0, 16).fieldOf("spire_check_radius").forGetter(CragSpiresPlacementOld::spireCheckRadius),
					ExtraCodecs.intRange(2, 16).validate((i) -> 16 % i == 0 ? DataResult.success(i) : DataResult.error(() -> "\"spire_cell_size\" must divide 16")).fieldOf("spire_cell_size").forGetter(CragSpiresPlacementOld::spireCellSize),
					Codec.BOOL.optionalFieldOf("ignore_biomes", false).forGetter(CragSpiresPlacementOld::ignoreBiomes)
			).apply(instance, CragSpiresPlacementOld::new));

	private final SimplexNoiseConfiguration spireNoise;
	private final double noiseValueMultiplier;
	private final double noiseValueOffset;
	private final double spireHeightFactor;
	private final int spireCheckRadius;
	private final int spireCellSize;
	private final boolean ignoreBiomes;
	
	public CragSpiresPlacementOld(SimplexNoiseConfiguration spireNoise, double noiseValueMultiplier, double noiseValueOffset, double spireHeightFactor, int spireCheckRadius, int spireCellSize, boolean ignoreBiomes) {
		this.spireNoise = spireNoise;
		this.noiseValueMultiplier = noiseValueMultiplier;
		this.noiseValueOffset = noiseValueOffset;
		this.spireHeightFactor = spireHeightFactor;
		this.spireCheckRadius = spireCheckRadius;
		this.spireCellSize = spireCellSize;
		this.ignoreBiomes = ignoreBiomes;
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

	public int spireCellSize() {
		return this.spireCellSize;
	}
	
	public boolean ignoreBiomes() {
		return this.ignoreBiomes;
	}
	
	@Override
	public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
		if(16 % this.spireCellSize != 0) {
			throw new IllegalStateException("spireCellSize must divide 16");
		}
		
		final int noiseSize = (16 + 2 * this.spireCheckRadius);
		
		SimplexData spireNoiseData = this.spireNoise.getNoise(context.getLevel().getSeed());

		int noiseMinX = pos.getX() - this.spireCheckRadius;
		int noiseMinZ = pos.getZ() - this.spireCheckRadius;
		
		// length is `noiseSize * noiseSize`
		double[] spireNoiseValues = EarlyGeneratorHelper.computeNoiseRawWithSize(spireNoiseData.noiseGenerator(), noiseMinX, noiseMinZ, this.spireNoise.noiseScale(), noiseSize);
		
		// Split the area into "cells"
		final int cellSizeBlocks = this.spireCellSize;
		final int cellCount = 16 / cellSizeBlocks;
		final boolean[] validCells = new boolean[cellCount * cellCount];
		
		BiomeCheckContext biomeCheckContext = new BiomeCheckContext(context);
		
		// TODO this isn't the fastest way to find which cells are valid
		MutableBlockPos mutablePos = new MutableBlockPos();
		for(int cellX = 0; cellX < cellCount; ++cellX) {
			for(int cellZ = 0; cellZ < cellCount; ++cellZ) {
				final int cellIndex = cellX * cellCount + cellZ;

				// The minimum X, Z of the cell (inclusive)
				final int cellMinX = cellX * cellSizeBlocks;
				final int cellMinZ = cellZ * cellSizeBlocks;
				// The maximum X, Z of the cell (exclusive)
				final int cellMaxX = (cellX + 1) * cellSizeBlocks;
				final int cellMaxZ = (cellZ + 1) * cellSizeBlocks;
				
				// Buffer for the blocks that surround a cell
				cellCheck: for(int x = cellMinX - this.spireCheckRadius; x < cellMaxX + this.spireCheckRadius; ++x) {
					for(int z = cellMinZ - this.spireCheckRadius; z < cellMaxZ + this.spireCheckRadius; ++z) {
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
						
						// A spire would generate within the check radius of this cell, mark it as such
						validCells[cellIndex] = true;
						break cellCheck;
					}
				}
			}
		}
		
		
		// Create a stream containing the positions of all the valid cells
		Stream.Builder<BlockPos> streamBuilder = Stream.builder();

		for(int cellX = 0; cellX < cellCount; ++cellX) {
			for(int cellZ = 0; cellZ < cellCount; ++cellZ) {
				final int cellIndex = cellX * cellCount + cellZ;

				if(validCells[cellIndex]) {
					// The minimum X, Z of the cell
					final int cellMinX = cellX * cellSizeBlocks;
					final int cellMinZ = cellZ * cellSizeBlocks;

					final int posX = pos.getX() + cellMinX;
					final int posZ = pos.getZ() + cellMinZ;
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
