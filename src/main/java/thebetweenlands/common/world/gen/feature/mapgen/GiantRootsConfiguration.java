package thebetweenlands.common.world.gen.feature.mapgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import thebetweenlands.common.registries.BiomeRegistry;

import java.util.List;
import java.util.Set;

public class GiantRootsConfiguration extends MapGenHelperConfiguration {

	public static final Codec<GiantRootsConfiguration> CODEC = RecordCodecBuilder.create((p_67849_) -> {
		return p_67849_.group(Codec.list(TargetBlockState.CODEC).fieldOf("replaceable").forGetter((p_161027_) -> {
			return p_161027_.replaceable;
		})).apply(p_67849_, GiantRootsConfiguration::new);
	});

	// TODO: make a registry of biomes to place in this list
	// (Might make this a static global list)
	public Set<Holder<Biome>> biomes;

	public GiantRootsConfiguration(List<TargetBlockState> replaceable) {
		super(replaceable);
	}

	public Set<ResourceLocation> getBiomes() {
		return Set.of(BiomeRegistry.COARSE_ISLANDS.biome.getId(), BiomeRegistry.RAISED_ISLES.biome.getId());
	}
}
