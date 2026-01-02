package thebetweenlands.common.world.gen.layer.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

public record BLWeightPoint(Holder<Biome> biome, short weight) {
	public static final Codec<BLWeightPoint> CODEC = RecordCodecBuilder.create((instance) ->
		instance.group(
			Biome.CODEC.fieldOf("biome").forGetter(BLWeightPoint::biome),
			Codec.SHORT.fieldOf("weight").forGetter(BLWeightPoint::weight)
		).apply(instance, BLWeightPoint::new));

	public static BLWeightPoint of(Holder<Biome> biome, short weight) {
		return new BLWeightPoint(biome, weight);
	}

	public static BLWeightPoint of(Holder<Biome> biome, int weight) {
		return BLWeightPoint.of(biome, (short)weight);
	}

	public static BLWeightPoint of(HolderGetter<Biome> registry, ResourceKey<Biome> biome, int weight) {
		return BLWeightPoint.of(registry.getOrThrow(biome), (short)weight);
	}
}
