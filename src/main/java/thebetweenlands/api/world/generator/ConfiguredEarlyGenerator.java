package thebetweenlands.api.world.generator;

import java.util.stream.Stream;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import thebetweenlands.api.BLRegistries;

public record ConfiguredEarlyGenerator<GC extends EarlyGeneratorConfiguration, G extends EarlyGenerator<GC>>(G generator, GC configuration) {
    public static final Codec<ConfiguredEarlyGenerator<?, ?>> DIRECT_CODEC = BLRegistries.EARLY_GENERATORS
            .byNameCodec()
            .dispatch(earlyGenerator -> earlyGenerator.generator, EarlyGenerator::configuredCodec);
    public static final Codec<Holder<ConfiguredEarlyGenerator<?, ?>>> CODEC = RegistryFileCodec.create(BLRegistries.Keys.CONFIGURED_GENERATORS, DIRECT_CODEC);
    public static final Codec<HolderSet<ConfiguredEarlyGenerator<?, ?>>> LIST_CODEC = RegistryCodecs.homogeneousList(BLRegistries.Keys.CONFIGURED_GENERATORS, DIRECT_CODEC);

	public Stream<ConfiguredEarlyGenerator<?, ?>> getGenerators() {
		return Stream.concat(Stream.of(this), configuration.getSubGenerators());
	}
	
}
