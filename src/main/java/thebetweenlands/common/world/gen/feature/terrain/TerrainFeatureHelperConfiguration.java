package thebetweenlands.common.world.gen.feature.terrain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.world.gen.feature.FeatureHelperConfiguration;

import java.util.List;

public class TerrainFeatureHelperConfiguration extends FeatureHelperConfiguration {

	public static final Codec<TerrainFeatureHelperConfiguration> CODEC = RecordCodecBuilder.create((p_67849_) -> {
		return p_67849_.group(
			Codec.list(TargetBlockState.CODEC).fieldOf("replaceable").forGetter((conf) -> {
				return conf.replaceable;
			}),
			ResourceLocation.CODEC.fieldOf("targetBiome").forGetter((conf) -> {
				return conf.targetBiome;
			})).apply(p_67849_, TerrainFeatureHelperConfiguration::new);
	});

	public ResourceLocation targetBiome;

	public TerrainFeatureHelperConfiguration(List<TargetBlockState> replaceable, ResourceLocation targetBiome) {
		super(replaceable);
		this.targetBiome = targetBiome;
	}

	public TerrainFeatureHelperConfiguration(ResourceLocation targetBiome) {
		super(SURFACE);
		this.targetBiome = targetBiome;
	}
}
