package thebetweenlands.common.world.gen.layer.util;

import net.minecraft.util.LinearCongruentialGenerator;
import thebetweenlands.api.world.biome.layer.context.SeedMixer;

public class LinearCongruentialSeedMixer implements SeedMixer {

	private final long worldSeed;
	
	public LinearCongruentialSeedMixer(long worldSeed) {
		this.worldSeed = worldSeed;
	}
	
	@Override
	public long mixSeed(long seedModifier) {
        long i = LinearCongruentialGenerator.next(seedModifier, seedModifier);
        i = LinearCongruentialGenerator.next(i, seedModifier);
        i = LinearCongruentialGenerator.next(i, seedModifier);
        long j = LinearCongruentialGenerator.next(this.worldSeed, i);
        j = LinearCongruentialGenerator.next(j, i);
        return LinearCongruentialGenerator.next(j, i);
	}
	
}
