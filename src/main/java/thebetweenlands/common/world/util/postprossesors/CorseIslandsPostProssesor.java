package thebetweenlands.common.world.util.postprossesors;

import thebetweenlands.common.world.noisegenerators.VoronoiCellNoise;
import thebetweenlands.common.world.surfacegenerators.SurfaceGenerator;
import thebetweenlands.common.world.surfacegenerators.SurfaceMapValues;
import thebetweenlands.common.world.util.ScanReturn;

import java.util.List;

// This Postprossesor will only do standard factor blending and biome level blending
public class CorseIslandsPostProssesor extends SurfacePostProssesor {

	final double range;
	final double slopedist;

	public CorseIslandsPostProssesor(double range, double slopedist) {
		super();
		this.range = range;
		this.slopedist = slopedist;
	}

	public SurfaceMapValues docheck(ScanReturn scanin, VoronoiCellNoise.ReturnNoise BiomeSample, List<SurfaceGenerator> genin, int blockbiomeID, int x, int z) {

		SurfaceMapValues thisSurfaceMap = genin.get(blockbiomeID).sample(x, z);

		if (!scanin.active) {
			return thisSurfaceMap;
		}

		double underwaterheightout = 0;
		double additivedifference = 0;
		int indexcount = scanin.borderbiomes.size();
		double outFactor = thisSurfaceMap.surfaceFactor;
		double surfaceheightout = thisSurfaceMap.surfaceHeight;

		// Go thrue all biomes and do id based conditions
		for (int index = 0; index < indexcount; index++) {

			double distance = scanin.distances.get(index);

			// get this biome distance and set factor
			if (distance < 12 && index != BiomeSample.resultIndex && scanin.borderbiomes.get(BiomeSample.resultIndex) != scanin.borderbiomes.get(index)) {

				// cut off range
				if (distance < this.range) {
					outFactor = 1;
				}

				// slope surface terrain
				else if (distance > this.range && distance < this.range + this.slopedist) {
					double surfaceslopefactor = (1d - ((double) distance - this.range) / this.slopedist) + 64;
					if (surfaceheightout > surfaceslopefactor) {
						surfaceheightout = surfaceslopefactor;
					}
				}

				// get biome and difference
				int biome = scanin.borderbiomes.get(index);
				double diff = (thisSurfaceMap.underwaterHeight - genin.get(biome).sample(x, z).underwaterHeight) * 0.5;
				// Scale difference based on distance
				additivedifference += (diff * (1d - scanin.distances.get(index) / 12d));
			}
		}

		// Normalise difference
		additivedifference = additivedifference / (double) indexcount;

		return new SurfaceMapValues(surfaceheightout, thisSurfaceMap.underwaterHeight - additivedifference, outFactor);
	}
}
