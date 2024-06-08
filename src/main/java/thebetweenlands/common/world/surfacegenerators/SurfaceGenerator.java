package thebetweenlands.common.world.surfacegenerators;

import java.util.List;

import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

public class SurfaceGenerator {

	public static List<Integer> halfrange = List.of(0,1);
	public static List<Integer> fullrange = List.of(-1,1);
	
	public SurfaceGenerator(XoroshiroRandomSource source) {
		
	}
	
	public SurfaceMapValues sample(int x, int z) {
		return new SurfaceMapValues(0, 0, 0);
	}
}
