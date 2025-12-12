package thebetweenlands.common.world.gen.generators;

import com.mojang.serialization.Codec;

import thebetweenlands.api.world.generator.EarlyGenerationContext;
import thebetweenlands.api.world.generator.EarlyGenerator;
import thebetweenlands.common.world.gen.generators.config.CoarseIslandsGeneratorConfiguration;

public class CoarseIslandsGenerator extends EarlyGenerator<CoarseIslandsGeneratorConfiguration> {

	public CoarseIslandsGenerator(Codec<CoarseIslandsGeneratorConfiguration> codec) {
		super(codec);
	}

	@Override
	public boolean place(EarlyGenerationContext<CoarseIslandsGeneratorConfiguration> context) {
		return false;
	}

}
