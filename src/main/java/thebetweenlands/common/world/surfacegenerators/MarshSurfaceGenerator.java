package thebetweenlands.common.world.surfacegenerators;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MarshSurfaceGenerator extends SurfaceGenerator {

	public static List<Integer> halfrange = List.of(0, 1);
	public static List<Integer> fullrange = List.of(-1, 1);
	public final SimplexNoise simplexnoise;
	public final PerlinNoise vainrivernoise;
	public final Random rand;

	public MarshSurfaceGenerator(XoroshiroRandomSource source, Random rand) {
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
		double riverratio = 0;
		List<Pair<Boolean, Boolean>> borderlist = new ArrayList<Pair<Boolean, Boolean>>();
		borderlist.add(new Pair<Boolean, Boolean>(vainrivernoise.getValue(x * 0.09, 0, z * 0.09) >= 0, false));
		borderlist.add(new Pair<Boolean, Boolean>(vainrivernoise.getValue(x * 0.09, 32, z * 0.09) >= 0, false));

		for (int tx = x - 5; tx <= x + 5; tx++) {
			for (int tz = z - 5; tz <= z + 5; tz++) {

				// Move Scanpositions again with random
				int rtx = tx + rand.nextInt(3);
				int rtz = tz + rand.nextInt(3);

				double riversize = 1 + vainrivernoise.getValue(x * 0.6, 64, z * 0.6) * 0.5;

				borderlist.set(0, new Pair<Boolean, Boolean>(borderlist.get(0).getFirst(), vainrivernoise.getValue(rtx * 0.09, 0, rtz * 0.09) >= 0));
				borderlist.set(1, new Pair<Boolean, Boolean>(borderlist.get(1).getFirst(), vainrivernoise.getValue(rtx * 0.09, 32, rtz * 0.09) >= 0));

				if (borderlist.get(0).getFirst() != borderlist.get(0).getSecond()) {
					double comparedist = Math.sqrt(Math.pow(rtx - x, 2) + Math.pow(rtz - z, 2));

					if (comparedist < 3 * riversize) {
						useWaterlevel = true;
					}
				}
				if (borderlist.get(1).getFirst() != borderlist.get(1).getSecond()) {
					double comparedist = Math.sqrt(Math.pow(rtx - x, 2) + Math.pow(rtz - z, 2));

					if (comparedist <= 2 * riversize) {
						useWaterlevel = true;
					}
				}
			}
		}

		if (useWaterlevel == false) {
			surfacefactorout = 0;
		}
		return new SurfaceMapValues(surfaceheightout, underwaterheightout, surfacefactorout);
	}
}
