package thebetweenlands.common.world.gen;

import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import net.minecraftforge.event.terraingen.InitNoiseGensEvent.Context;

public class ContextBetweenlands extends Context {
	private final NoiseGeneratorPerlin surfaceNoise;
	private final NoiseGeneratorSimplex treeNoise;
	private final NoiseGeneratorSimplex speleothemDensityNoise;

	public ContextBetweenlands(NoiseGeneratorOctaves lperlin1, NoiseGeneratorOctaves lperlin2,
			NoiseGeneratorOctaves perlin, NoiseGeneratorPerlin surfaceNoise, NoiseGeneratorOctaves scale, NoiseGeneratorOctaves depth,
			NoiseGeneratorSimplex treeNoise, NoiseGeneratorSimplex speleothemDensityNoise) {
		super(lperlin1, lperlin2, perlin, scale, depth);
		this.surfaceNoise = surfaceNoise;
		this.treeNoise = treeNoise;
		this.speleothemDensityNoise = speleothemDensityNoise;
	}

	public NoiseGeneratorPerlin getSurfaceNoise() {
		return this.surfaceNoise;
	}

	public NoiseGeneratorSimplex getTreeNoise() {
		return this.treeNoise;
	}

	public NoiseGeneratorSimplex getSpeleothemDensityNoise() {
		return this.speleothemDensityNoise;
	}

	@Override
	public ContextBetweenlands clone() {
		return new ContextBetweenlands(getLPerlin1(), getLPerlin2(), getPerlin(), getSurfaceNoise(), getScale(), getDepth(), getTreeNoise(), getSpeleothemDensityNoise());
	}
}
