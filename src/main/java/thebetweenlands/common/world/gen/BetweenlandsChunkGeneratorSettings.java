package thebetweenlands.common.world.gen;

import java.util.Objects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import thebetweenlands.api.world.BiomeWeightGroups;

public record BetweenlandsChunkGeneratorSettings(BiomeWeightGroups biomeWeightGroups) {

	public static final MapCodec<BetweenlandsChunkGeneratorSettings> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					BiomeWeightGroups.CODEC.fieldOf("biome_weight_groups").forGetter(BetweenlandsChunkGeneratorSettings::biomeWeightGroups)
				).apply(instance, BetweenlandsChunkGeneratorSettings::new)
			);
	
	public static final Codec<BetweenlandsChunkGeneratorSettings> CODEC = MAP_CODEC.codec();
	
	public BetweenlandsChunkGeneratorSettings() {
		this(BiomeWeightGroups.EMPTY);
	}
	
	public BetweenlandsChunkGeneratorSettings(BiomeWeightGroups biomeWeightGroups) {
		this.biomeWeightGroups = Objects.requireNonNull(biomeWeightGroups);
	}
	
}
