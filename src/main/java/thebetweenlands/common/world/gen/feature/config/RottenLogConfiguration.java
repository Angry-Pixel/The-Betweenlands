package thebetweenlands.common.world.gen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record RottenLogConfiguration(int length, int baseRadius) implements FeatureConfiguration {
	public static final Codec<RottenLogConfiguration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			ExtraCodecs.POSITIVE_INT.fieldOf("length").forGetter(obj -> obj.length),
			ExtraCodecs.POSITIVE_INT.fieldOf("base_radius").forGetter(obj -> obj.baseRadius)
		).apply(instance, RottenLogConfiguration::new));
}
