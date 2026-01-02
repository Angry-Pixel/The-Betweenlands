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
		long baseSeed = seedModifier;
		baseSeed = LinearCongruentialGenerator.next(baseSeed, seedModifier);
		baseSeed = LinearCongruentialGenerator.next(baseSeed, seedModifier);
		baseSeed = LinearCongruentialGenerator.next(baseSeed, seedModifier);
		
		long mixedSeed = this.worldSeed;
		mixedSeed = LinearCongruentialGenerator.next(mixedSeed, baseSeed);
		mixedSeed = LinearCongruentialGenerator.next(mixedSeed, baseSeed);
		mixedSeed = LinearCongruentialGenerator.next(mixedSeed, baseSeed);
		return mixedSeed;
	}
	
}
