package thebetweenlands.util;

import net.minecraft.world.level.levelgen.RandomState;

public interface IBetweenlandsRandomStateExtension {
	static long getLevelSeed(RandomState state) {
		return ((IBetweenlandsRandomStateExtension)(Object)state).thebetweenlands$getLevelSeed();
	}

	long thebetweenlands$getLevelSeed();
}
