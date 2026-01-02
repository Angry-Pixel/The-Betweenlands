package thebetweenlands.common.world.gen;

import java.util.List;
import java.util.Objects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderSet;
import thebetweenlands.api.world.biome.BiomeWeightGroups;
import thebetweenlands.api.world.generator.ConfiguredEarlyGenerator;

public record BetweenlandsChunkGeneratorSettings(BiomeWeightGroups biomeWeightGroups, List<HolderSet<ConfiguredEarlyGenerator<?, ?>>> globalGenerators) {
	public static final BetweenlandsChunkGeneratorSettings DEFAULT = new BetweenlandsChunkGeneratorSettings();
	
	public static final MapCodec<BetweenlandsChunkGeneratorSettings> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					BiomeWeightGroups.CODEC.fieldOf("biome_weight_groups").forGetter(BetweenlandsChunkGeneratorSettings::biomeWeightGroups),
					ConfiguredEarlyGenerator.LIST_OF_LISTS_CODEC.fieldOf("global_generators").forGetter(BetweenlandsChunkGeneratorSettings::globalGenerators)
				).apply(instance, BetweenlandsChunkGeneratorSettings::new)
			);
	
	public static final Codec<BetweenlandsChunkGeneratorSettings> CODEC = MAP_CODEC.codec();
	
	public BetweenlandsChunkGeneratorSettings() {
		this(BiomeWeightGroups.EMPTY, List.of());
	}
	
	public BetweenlandsChunkGeneratorSettings(BiomeWeightGroups biomeWeightGroups, List<HolderSet<ConfiguredEarlyGenerator<?, ?>>> globalGenerators) {
		this.biomeWeightGroups = Objects.requireNonNull(biomeWeightGroups);
		this.globalGenerators = Objects.requireNonNull(globalGenerators);
	}
	
}
