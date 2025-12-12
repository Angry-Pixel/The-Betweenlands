package thebetweenlands.common.world.gen.warp;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.api.world.generator.ConfiguredEarlyGenerator;

public record BLBiomeData(Holder<Biome> biome, TerrainPoint terrainPoint, List<HolderSet<ConfiguredEarlyGenerator<?, ?>>> generators) {
	public static final MapCodec<BLBiomeData> MAP_CODEC = RecordCodecBuilder.<BLBiomeData>mapCodec((pair) -> pair.group(
			Biome.CODEC.fieldOf("biome").forGetter(BLBiomeData::biome),
			TerrainPoint.CODEC.fieldOf("parameters").forGetter(BLBiomeData::terrainPoint),
			ConfiguredEarlyGenerator.LIST_OF_LISTS_CODEC.optionalFieldOf("generators", List.of()).forGetter(BLBiomeData::generators)
		).apply(pair, BLBiomeData::new));

	public static final Codec<BLBiomeData> CODEC = MAP_CODEC.codec();
	
	public BLBiomeData(Holder<Biome> biome, TerrainPoint terrainPoint) {
		this(biome, terrainPoint, List.of());
	}
	
	public static Builder builder(HolderGetter<ConfiguredEarlyGenerator<?, ?>> generatorRegistry, HolderGetter<Biome> registry, int weight, float depth, float scale, ResourceKey<Biome> biome) {
		return new Builder(generatorRegistry, registry.getOrThrow(biome), weight, depth, scale);
	}
	
	public static final class Builder {
		private final HolderGetter<ConfiguredEarlyGenerator<?, ?>> generatorRegistry;
		private final Holder<Biome> biome;
		private final int weight;
		private final float depth;
		private final float scale;
		
		private final List<List<Holder<ConfiguredEarlyGenerator<?, ?>>>> generators;
		
		public Builder(HolderGetter<ConfiguredEarlyGenerator<?, ?>> generatorRegistry, Holder<Biome> biome, int weight, float depth, float scale) {
			this.generatorRegistry = generatorRegistry;
			this.biome = biome;
			this.weight = weight;
			this.depth = depth;
			this.scale = scale;
			
			this.generators = new ArrayList<>();
		}

		public Builder addGenerator(int index, Holder<ConfiguredEarlyGenerator<?, ?>> generator) {
			while(this.generators.size() <= index) {
				this.generators.add(new ArrayList<>());
			}
			
			this.generators.get(index).add(generator);
			
			return this;
		}

		public Builder addGenerator(int index, ResourceKey<ConfiguredEarlyGenerator<?, ?>> generatorKey) {
			return this.addGenerator(index, generatorRegistry.getOrThrow(generatorKey));
		}

		public Builder addGenerator(ResourceKey<ConfiguredEarlyGenerator<?, ?>> generatorKey) {
			return this.addGenerator(this.generators.size(), generatorRegistry.getOrThrow(generatorKey));
		}

		public BLBiomeData build() {
			return new BLBiomeData(biome, new TerrainPoint((short)weight, depth, scale), this.generators.stream().map(HolderSet::direct).collect(ImmutableList.toImmutableList()));
		}
	}
}
