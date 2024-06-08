package thebetweenlands.common.world.util.postprossesors;

import java.util.List;

import thebetweenlands.common.world.noisegenerators.VoronoiCellNoise;
import thebetweenlands.common.world.surfacegenerators.SurfaceGenerator;
import thebetweenlands.common.world.surfacegenerators.SurfaceMapValues;
import thebetweenlands.common.world.util.ScanReturn;

// This Postprossesor will only do standard factor blending and biome level blending
public class StandardPostProssesor extends SurfacePostProssesor {

	public StandardPostProssesor() {
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
		double highestFactor = thisSurfaceMap.surfaceFactor;
		
		// Go thrue all biomes and do id based conditions
		for (int index = 0; index < indexcount; index++) {
			
			// get this biome distance and set factor
			// also make sure the index is the result index
			if (scanin.distances.get(index) < 12 && index != BiomeSample.resultIndex && scanin.borderbiomes.get(BiomeSample.resultIndex) != scanin.borderbiomes.get(index)) {
				
				double outfactor = 1d - (double)scanin.distances.get(index) / 12d;
				
				if (highestFactor < outfactor) {
					highestFactor = outfactor;
				}
			
				// get biome and difference
				int biome = scanin.borderbiomes.get(index);
				double diff = (thisSurfaceMap.underwaterHeight - genin.get(biome).sample(x, z).underwaterHeight) * 0.5;
				// Scale difference based on distance
				additivedifference -= (diff * (1d - scanin.distances.get(index) / 12d));
			}
		}
		
		// Normalise difference
		additivedifference = additivedifference / (double)indexcount;
		
		return new SurfaceMapValues(thisSurfaceMap.surfaceHeight, thisSurfaceMap.underwaterHeight + additivedifference, highestFactor);
	}
}
