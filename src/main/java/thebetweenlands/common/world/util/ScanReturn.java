package thebetweenlands.common.world.util;

import java.awt.Point;
import java.util.List;

import net.minecraft.resources.ResourceLocation;

public class ScanReturn {
	public final List<Integer> borderbiomes;
	public final List<Point> borderpoints;
	public final List<Double> distances;
	public final boolean active;
	
	// a simple class to hold return information from border quickscan
	public ScanReturn(boolean active, List<Point> borderpoints, List<Double> distances, List<Integer> borderbiomes) {
		this.borderbiomes = borderbiomes;
		this.borderpoints = borderpoints;
		this.distances = distances;
		this.active = active;
	}
}
