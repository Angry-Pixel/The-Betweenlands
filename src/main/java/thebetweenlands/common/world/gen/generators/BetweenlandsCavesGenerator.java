package thebetweenlands.common.world.gen.generators;

import java.util.EnumSet;

import com.mojang.serialization.Codec;

import thebetweenlands.api.world.ExtraChunkInfoTypes;
import thebetweenlands.api.world.generator.EarlyGenerationContext;
import thebetweenlands.api.world.generator.EarlyGenerator;
import thebetweenlands.common.world.gen.generators.config.BetweenlandsCavesGeneratorConfiguration;

public class BetweenlandsCavesGenerator extends EarlyGenerator<BetweenlandsCavesGeneratorConfiguration> {

	public BetweenlandsCavesGenerator(Codec<BetweenlandsCavesGeneratorConfiguration> codec) {
		super(codec);
	}
	
	@Override
	public EnumSet<ExtraChunkInfoTypes> getRequiredExtraInfo(BetweenlandsCavesGeneratorConfiguration config) {
		return EnumSet.of(ExtraChunkInfoTypes.BIOME_WEIGHTS, ExtraChunkInfoTypes.CARVING_MASKS);
	}

	@Override
	public boolean place(EarlyGenerationContext<BetweenlandsCavesGeneratorConfiguration> context) {
		return false;
	}

}
