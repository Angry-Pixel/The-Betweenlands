package thebetweenlands.common.world.util.postprossesors;

import java.util.List;

import thebetweenlands.common.world.noisegenerators.VoronoiCellNoise;
import thebetweenlands.common.world.surfacegenerators.SurfaceGenerator;
import thebetweenlands.common.world.surfacegenerators.SurfaceMapValues;
import thebetweenlands.common.world.util.ScanReturn;

public class SurfacePostProssesor {

	public SurfacePostProssesor() {
	}
	
	public SurfaceMapValues docheck(ScanReturn scanin, VoronoiCellNoise.ReturnNoise BiomeSample, List<SurfaceGenerator> genin, int blockbiomeID, int x, int y) {
		return new SurfaceMapValues(0,0,0);
	}
}
