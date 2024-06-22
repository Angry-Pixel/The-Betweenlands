package thebetweenlands.common.world.surfacegenerators;

import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

import java.util.List;

public class DeepWatersSurfaceGenerator extends SurfaceGenerator {

	public static List<Integer> halfrange = List.of(0, 1);
	public static List<Integer> fullrange = List.of(-1, 1);
	public final SimplexNoise simplexnoise;
	public final PerlinNoise vainrivernoise;

	public DeepWatersSurfaceGenerator(XoroshiroRandomSource source) {
		super(source);
		this.simplexnoise = new SimplexNoise(source);
		this.vainrivernoise = PerlinNoise.create(source, fullrange);
	}

	public SurfaceMapValues sample(int x, int z) {
		double underwaterheightout = 54;
		double surfaceheightout = 0;
		double surfacefactorout = 0;

		// Surface Noise
		double mod = 4 + simplexnoise.getValue(x * 0.04, z * 0.04) * 4;

		underwaterheightout += mod;

		surfacefactorout = 1;
		return new SurfaceMapValues(surfaceheightout, underwaterheightout, surfacefactorout);
	}
}
