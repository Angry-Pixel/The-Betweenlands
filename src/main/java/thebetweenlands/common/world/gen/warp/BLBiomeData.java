package thebetweenlands.common.world.gen.warp;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.api.world.generator.ConfiguredEarlyGenerator;

public record BLBiomeData(Holder<Biome> biome, TerrainPoint terrainPoint, HolderSet<ConfiguredEarlyGenerator<?, ?>> generators) {
	public static final MapCodec<BLBiomeData> MAP_CODEC = RecordCodecBuilder.<BLBiomeData>mapCodec((pair) -> pair.group(
			Biome.CODEC.fieldOf("biome").forGetter(BLBiomeData::biome),
			TerrainPoint.CODEC.fieldOf("parameters").forGetter(BLBiomeData::terrainPoint),
			ConfiguredEarlyGenerator.LIST_CODEC.optionalFieldOf("generators", HolderSet.empty()).forGetter(BLBiomeData::generators)
		).apply(pair, BLBiomeData::new));

	public static final Codec<BLBiomeData> CODEC = MAP_CODEC.codec();
}
