package thebetweenlands.common.world.util.postprossesors;

import thebetweenlands.common.world.noisegenerators.VoronoiCellNoise;
import thebetweenlands.common.world.surfacegenerators.SurfaceGenerator;
import thebetweenlands.common.world.surfacegenerators.SurfaceMapValues;
import thebetweenlands.common.world.util.ScanReturn;

import java.util.List;

public class SurfacePostProssesor {

	public SurfacePostProssesor() {
	}

	public SurfaceMapValues docheck(ScanReturn scanin, VoronoiCellNoise.ReturnNoise BiomeSample, List<SurfaceGenerator> genin, int blockbiomeID, int x, int y) {
		return new SurfaceMapValues(0, 0, 0);
	}
}
