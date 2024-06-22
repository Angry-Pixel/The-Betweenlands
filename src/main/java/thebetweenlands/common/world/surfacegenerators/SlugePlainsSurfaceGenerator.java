package thebetweenlands.common.world.surfacegenerators;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

import java.util.ArrayList;
import java.util.List;

public class SlugePlainsSurfaceGenerator extends SurfaceGenerator {

	public static List<Integer> halfrange = List.of(0, 1);
	public static List<Integer> fullrange = List.of(-1, 1);
	public final SimplexNoise simplexnoise;
	public final PerlinNoise vainrivernoise;

	public SlugePlainsSurfaceGenerator(XoroshiroRandomSource source) {
		super(source);
		this.simplexnoise = new SimplexNoise(source);
		this.vainrivernoise = PerlinNoise.create(source, fullrange);
	}

	public SurfaceMapValues sample(int x, int z) {
		double underwaterheightout = 0;
		double surfaceheightout = 64;
		double surfacefactorout = 0;

		// Surface Noise
		double mod = simplexnoise.getValue(x * 0.06, z * 0.06) * 1;

		// Checks if bordering another biome

		surfaceheightout -= mod;

		underwaterheightout = surfaceheightout - 3;

		// River modification
		// check if bordering noise crossover to the opisit on the noise plane
		double riverratio = 0;
		List<Pair<Boolean, Boolean>> borderlist = new ArrayList<Pair<Boolean, Boolean>>();
		borderlist.add(new Pair<Boolean, Boolean>(vainrivernoise.getValue(x * 0.03, 0, z * 0.03) >= 0, false));
		borderlist.add(new Pair<Boolean, Boolean>(vainrivernoise.getValue(x * 0.04, 32, z * 0.04) >= 0, false));

		for (int tx = x - 5; tx <= x + 5; tx++) {
			for (int tz = z - 5; tz <= z + 5; tz++) {

				borderlist.set(0, new Pair<Boolean, Boolean>(borderlist.get(0).getFirst(), vainrivernoise.getValue(tx * 0.03, 0, tz * 0.03) >= 0));
				borderlist.set(1, new Pair<Boolean, Boolean>(borderlist.get(1).getFirst(), vainrivernoise.getValue(tx * 0.04, 32, tz * 0.04) >= 0));

				if (borderlist.get(0).getFirst() != borderlist.get(0).getSecond()) {
					double comparedist = Math.sqrt(Math.pow(tx - x, 2) + Math.pow(tz - z, 2));

					if (comparedist <= 5) {
						if (riverratio < 1 - comparedist / 5) {
							riverratio = 1 - comparedist / 5;
						}
					}
				}
				if (borderlist.get(1).getFirst() != borderlist.get(1).getSecond()) {
					double comparedist = Math.sqrt(Math.pow(tx - x, 2) + Math.pow(tz - z, 2));

					if (comparedist <= 3) {
						if (riverratio < 1 - comparedist / 3) {
							riverratio = 1 - comparedist / 3;
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
