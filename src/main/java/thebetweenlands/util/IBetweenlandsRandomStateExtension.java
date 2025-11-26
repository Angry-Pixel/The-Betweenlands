package thebetweenlands.util;

import net.minecraft.world.level.levelgen.RandomState;

public interface IBetweenlandsRandomStateExtension {
	public static long getLevelSeed(RandomState state) {
		return ((IBetweenlandsRandomStateExtension)(Object)state).thebetweenlands$getLevelSeed();
	}
	
	public long thebetweenlands$getLevelSeed();
}
