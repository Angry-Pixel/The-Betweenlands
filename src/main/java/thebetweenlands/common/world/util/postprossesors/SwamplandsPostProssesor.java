package thebetweenlands.common.world.util.postprossesors;

import thebetweenlands.common.world.noisegenerators.VoronoiCellNoise;
import thebetweenlands.common.world.surfacegenerators.SurfaceGenerator;
import thebetweenlands.common.world.surfacegenerators.SurfaceMapValues;
import thebetweenlands.common.world.util.ScanReturn;

import java.util.List;

public class SwamplandsPostProssesor extends SurfacePostProssesor {

	public SwamplandsPostProssesor() {
		super();
	}

	public SurfaceMapValues docheck(ScanReturn scanin, VoronoiCellNoise.ReturnNoise BiomeSample, List<SurfaceGenerator> genin, int blockbiomeID, int x, int z) {

		SurfaceMapValues thisSurfaceMap = genin.get(blockbiomeID).sample(x, z);

		if (!scanin.active) {
			return thisSurfaceMap;
		}

		double underwaterheightout = 0;
		double additivedifference = 0;
		int indexcount = scanin.borderbiomes.size();
		int blendcount = 0;
		double highestFactor = thisSurfaceMap.surfaceFactor;

		// Go thrue all biomes and do id based conditions
		for (int index = 0; index < indexcount; index++) {

			double distance = scanin.distances.get(index);

			// get this biome distance and set factor
			if (distance < 8d && index != BiomeSample.resultIndex && scanin.borderbiomes.get(BiomeSample.resultIndex) != scanin.borderbiomes.get(index)) {

				//if (distance < 1.5d) {
				//	highestFactor = 1;
				//} else
				if (distance >= 0d && distance <= 5d) {
					double outfactor = 1d - ((double) scanin.distances.get(index) - 0d) / 5d;

					if (highestFactor < outfactor) {
						highestFactor = outfactor;
					}
				} else {
					if (highestFactor < 0) {
						highestFactor = 0;
					}
				}

				// get biome and difference
				int biome = scanin.borderbiomes.get(index);
				double diff = (thisSurfaceMap.underwaterHeight - genin.get(biome).sample(x, z).underwaterHeight) * 0.5;
				// Scale difference based on distance
				if (scanin.distances.get(index) < 6d) {
					additivedifference -= (diff * (1d - scanin.distances.get(index) / 6d));
					blendcount++;
				}
			}
		}

		// Normalise difference
		//if (blendcount != 0) {
		//	additivedifference = additivedifference / (double) blendcount;
		//}

		return new SurfaceMapValues(thisSurfaceMap.surfaceHeight, thisSurfaceMap.underwaterHeight + additivedifference, highestFactor);
	}
}
