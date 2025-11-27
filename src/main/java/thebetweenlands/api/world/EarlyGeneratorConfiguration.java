package thebetweenlands.api.world;

import java.util.stream.Stream;

public interface EarlyGeneratorConfiguration {
	default Stream<ConfiguredEarlyGenerator<?, ?>> getSubGenerators() {
		return Stream.empty();
	}
}
