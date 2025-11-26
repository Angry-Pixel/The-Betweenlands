package thebetweenlands.common.world.gen.warp;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public record BLBiomeData(Holder<Biome> biome, TerrainPoint terrainPoint) {
	public static final MapCodec<BLBiomeData> MAP_CODEC = RecordCodecBuilder.<BLBiomeData>mapCodec((pair) -> pair.group(
			Biome.CODEC.fieldOf("biome").forGetter(BLBiomeData::biome),
			TerrainPoint.CODEC.fieldOf("parameters").forGetter(BLBiomeData::terrainPoint)
		).apply(pair, BLBiomeData::new));

	public static final Codec<BLBiomeData> CODEC = MAP_CODEC.codec();
}
