package thebetweenlands.common.world.gen;

import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraftforge.event.terraingen.InitNoiseGensEvent.Context;

public class ContextBetweenlands extends Context {
	private final NoiseGeneratorPerlin surfaceNoise;

	public ContextBetweenlands(NoiseGeneratorOctaves lperlin1, NoiseGeneratorOctaves lperlin2,
			NoiseGeneratorOctaves perlin, NoiseGeneratorPerlin surfaceNoise, NoiseGeneratorOctaves scale, NoiseGeneratorOctaves depth) {
		super(lperlin1, lperlin2, perlin, scale, depth);
		this.surfaceNoise = surfaceNoise;
	}

	public NoiseGeneratorPerlin getSurfaceNoise() {
		return this.surfaceNoise;
	}

	@Override
	public ContextBetweenlands clone() {
		return new ContextBetweenlands(getLPerlin1(), getLPerlin2(), getPerlin(), getSurfaceNoise(), getScale(), getDepth());
	}
}
