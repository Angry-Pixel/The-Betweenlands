package thebetweenlands.common.world.surfacegenerators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import thebetweenlands.common.world.util.BiomeBorder;

public class ErodedMarshSurfaceGenerator extends SurfaceGenerator {

	public static List<Integer> halfrange = List.of(0,1);
	public static List<Integer> fullrange = List.of(-1,1);
	public final SimplexNoise simplexnoise;
	public final PerlinNoise vainrivernoise;
	public final Random rand;
	
	public ErodedMarshSurfaceGenerator(XoroshiroRandomSource source, Random rand) {
		super(source);
		this.rand = rand;
		this.simplexnoise = new SimplexNoise(source);
		this.vainrivernoise = PerlinNoise.create(source, fullrange);
	}
	
	public SurfaceMapValues sample(int x, int z) {
		double underwaterheightout = 62;
		double surfaceheightout = 63;
		double surfacefactorout = 1;
		
		// Surface Noise
		double mod = simplexnoise.getValue(x * 0.06, z * 0.06) * 1;
		
		underwaterheightout -= mod;
		
		// River modification
		// check if bordering noise crossover to the opisit on the noise plane
		boolean useWaterlevel = false;
		
		
		if (useWaterlevel == false && rand.nextBoolean()) {
			surfacefactorout = 0;
		}
		
		return new SurfaceMapValues(surfaceheightout, underwaterheightout, surfacefactorout);
	}
}
