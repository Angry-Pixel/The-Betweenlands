package thebetweenlands.common.world.gen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record RottenLogConfiguration(int length, int lengthOffset, int baseRadius, int baseRadiusOffset) implements FeatureConfiguration {
	public static final Codec<RottenLogConfiguration> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			ExtraCodecs.POSITIVE_INT.fieldOf("length").forGetter(obj -> obj.length),
			ExtraCodecs.NON_NEGATIVE_INT.fieldOf("length_offset").forGetter(obj -> obj.lengthOffset),
			ExtraCodecs.POSITIVE_INT.fieldOf("base_radius").forGetter(obj -> obj.baseRadius),
			ExtraCodecs.NON_NEGATIVE_INT.fieldOf("base_radius_offset").forGetter(obj -> obj.baseRadiusOffset)
		).apply(instance, RottenLogConfiguration::new));
}
