package thebetweenlands.common.world.surfacegenerators;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

import java.util.ArrayList;
import java.util.List;

public class SwamplandsSurfaceGenerator extends SurfaceGenerator {

	public static List<Integer> halfrange = List.of(0, 1);
	public static List<Integer> fullrange = List.of(-1, 1);
	public final SimplexNoise simplexnoise;
	public final PerlinNoise vainrivernoise;

	public SwamplandsSurfaceGenerator(XoroshiroRandomSource source) {
		super(source);
		this.simplexnoise = new SimplexNoise(source);
		this.vainrivernoise = PerlinNoise.create(source, fullrange);
	}

	public SurfaceMapValues sample(int x, int z) {
		double underwaterheightout = 62;
		double surfaceheightout = 63;
		double surfacefactorout = 0;

		// Surface Noise
		double mod = (simplexnoise.getValue(x * 0.02, 0, z * 0.02) * 1.5) - 1.5;
		//if (mod > 0d) {
		//	mod = 0d;
		//}

		double rivermod = 2.5 + (vainrivernoise.getValue(x * 0.08, 64, z * 0.08) * 2.5);

		// use standard noise moddifyer
		surfaceheightout -= mod;

		// River modification
		// check if bordering noise crossover to the opisit on the noise plane
		double riverratio = 0;
		List<Pair<Boolean, Boolean>> borderlist = new ArrayList<Pair<Boolean, Boolean>>();
		borderlist.add(new Pair<Boolean, Boolean>(vainrivernoise.getValue(x * 0.02, 0, z * 0.02) >= 0, false));
		borderlist.add(new Pair<Boolean, Boolean>(vainrivernoise.getValue(x * 0.02, 32, z * 0.02) >= 0, false));

		for (int tx = (int) (x - 5 * rivermod); tx <= x + 5 * rivermod; tx++) {
			for (int tz = (int) (z - 5 * rivermod); tz <= z + 5 * rivermod; tz++) {

				borderlist.set(0, new Pair<Boolean, Boolean>(borderlist.get(0).getFirst(), vainrivernoise.getValue(tx * 0.02, 0, tz * 0.02) >= 0));
				borderlist.set(1, new Pair<Boolean, Boolean>(borderlist.get(1).getFirst(), vainrivernoise.getValue(tx * 0.02, 32, tz * 0.02) >= 0));

				if (borderlist.get(0).getFirst() != borderlist.get(0).getSecond()) {
					double comparedist = Math.sqrt(Math.pow(tx - x, 2) + Math.pow(tz - z, 2));

					if (comparedist < 2 * rivermod) {
						riverratio = 1;
					} else if (comparedist < 3 * rivermod) {
						if (riverratio < 1 - (comparedist - 2 * rivermod)) {
							riverratio = 1 - (comparedist - 2 * rivermod);
						}
					}
				}
				if (borderlist.get(1).getFirst() != borderlist.get(1).getSecond()) {
					double comparedist = Math.sqrt(Math.pow(tx - x, 2) + Math.pow(tz - z, 2));

					if (comparedist <= 1 * rivermod) {
						riverratio = 1;
					} else if (comparedist <= 2 * rivermod) {
						if (riverratio < 1 - (comparedist - 1 * rivermod)) {
							riverratio = 1 - (comparedist - 1 * rivermod);
						}
					}
				}
			}
		}

		if (riverratio != 0) {
			if (surfaceheightout > 61) {
				int dif = (int) (surfaceheightout - 61);

				surfaceheightout -= dif * riverratio;
			}
		}
		return new SurfaceMapValues(surfaceheightout, underwaterheightout, surfacefactorout);
	}
}
