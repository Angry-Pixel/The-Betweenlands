package thebetweenlands.common.world.surfacegenerators;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import thebetweenlands.common.world.util.BiomeBorder;

public class CorseIslandsSurfaceGenerator extends SurfaceGenerator {

	public static List<Integer> halfrange = List.of(0,1);
	public static List<Integer> fullrange = List.of(-1,1);
	public final SimplexNoise simplexnoise;
	public final PerlinNoise vainrivernoise;
	
	public CorseIslandsSurfaceGenerator(XoroshiroRandomSource source) {
		super(source);
		this.simplexnoise = new SimplexNoise(source);
		this.vainrivernoise = PerlinNoise.create(source, fullrange);
	}
	
	public SurfaceMapValues sample(int x, int z) {
		double underwaterheightout = 54;
		double surfaceheightout = 0;
		double surfacefactorout = 1;
		
		// Underwater Noise
		double mod = 4 + simplexnoise.getValue(x * 0.04, z * 0.04) * 4;
		
		underwaterheightout += mod;
		
		double surfacemod = simplexnoise.getValue(x * 0.02, z * 0.02) * 4;
		
		if (surfacemod >= 1) {
			surfaceheightout = 63 + surfacemod;
			surfacefactorout = 0;
		}
		
		return new SurfaceMapValues(surfaceheightout, underwaterheightout, surfacefactorout);
	}
}
