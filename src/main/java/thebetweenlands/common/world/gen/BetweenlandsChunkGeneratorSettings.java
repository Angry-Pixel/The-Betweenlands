package thebetweenlands.common.world.gen;

import java.util.Objects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import thebetweenlands.api.world.biome.BiomeWeightGroups;

public record BetweenlandsChunkGeneratorSettings(BiomeWeightGroups biomeWeightGroups, GlobalEarlyGenerators globalGenerators) {
	public static final BetweenlandsChunkGeneratorSettings DEFAULT = new BetweenlandsChunkGeneratorSettings();
	
	public static final MapCodec<BetweenlandsChunkGeneratorSettings> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					BiomeWeightGroups.CODEC.fieldOf("biome_weight_groups").forGetter(BetweenlandsChunkGeneratorSettings::biomeWeightGroups),
					GlobalEarlyGenerators.CODEC.fieldOf("global_generators").forGetter(BetweenlandsChunkGeneratorSettings::globalGenerators)
				).apply(instance, BetweenlandsChunkGeneratorSettings::new)
			);
	
	public static final Codec<BetweenlandsChunkGeneratorSettings> CODEC = MAP_CODEC.codec();
	
	public BetweenlandsChunkGeneratorSettings() {
		this(BiomeWeightGroups.EMPTY, GlobalEarlyGenerators.EMPTY);
	}
	
	public BetweenlandsChunkGeneratorSettings(BiomeWeightGroups biomeWeightGroups, GlobalEarlyGenerators globalGenerators) {
		this.biomeWeightGroups = Objects.requireNonNull(biomeWeightGroups);
		this.globalGenerators = Objects.requireNonNull(globalGenerators);
	}
	
}
