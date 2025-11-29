package thebetweenlands.api.world.generator;

import java.util.EnumSet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import thebetweenlands.api.world.ExtraChunkInfoTypes;

public abstract class EarlyGenerator<GC extends EarlyGeneratorConfiguration> {

	private final Codec<GC> configCodec;
	private final MapCodec<ConfiguredEarlyGenerator<GC, EarlyGenerator<GC>>> configuredCodec;
	
	public EarlyGenerator(Codec<GC> codec) {
		this.configCodec = codec;
		this.configuredCodec = codec.fieldOf("config").xmap(configuration -> new ConfiguredEarlyGenerator<>(this, configuration), ConfiguredEarlyGenerator::configuration);
	}
	
	public final Codec<GC> configCodec() {
		return this.configCodec;
	}
	
	public final MapCodec<ConfiguredEarlyGenerator<GC, EarlyGenerator<GC>>> configuredCodec() {
		return this.configuredCodec;
	}
	
	/**
	 * Tells the chunk generator to provide additional info
	 * @return
	 */
	public EnumSet<ExtraChunkInfoTypes> getRequiredExtraInfo(GC config) {
		return EnumSet.noneOf(ExtraChunkInfoTypes.class);
	}
	
	public abstract boolean place(EarlyGenerationContext<GC> context);
}
