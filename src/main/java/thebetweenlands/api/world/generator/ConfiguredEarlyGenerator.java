package thebetweenlands.api.world.generator;

import java.util.stream.Stream;

public record ConfiguredEarlyGenerator<GC extends EarlyGeneratorConfiguration, G extends EarlyGenerator<GC>>(G generator, GC configuration) {

	public Stream<ConfiguredEarlyGenerator<?, ?>> getGenerators() {
		return Stream.concat(Stream.of(this), configuration.getSubGenerators());
	}
	
}
