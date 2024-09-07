package thebetweenlands.common.world.gen.feature.config;

import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record ChanceConfiguration(int chance) implements FeatureConfiguration {

	public static final Codec<ChanceConfiguration> CODEC = ExtraCodecs.POSITIVE_INT
		.fieldOf("chance")
		.xmap(ChanceConfiguration::new, obj -> obj.chance)
		.codec();
}
