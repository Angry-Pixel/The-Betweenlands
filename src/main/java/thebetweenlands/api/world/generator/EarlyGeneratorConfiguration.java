package thebetweenlands.api.world.generator;

import java.util.stream.Stream;

public interface EarlyGeneratorConfiguration {
	default Stream<ConfiguredEarlyGenerator<?, ?>> getSubGenerators() {
		return Stream.empty();
	}
}
