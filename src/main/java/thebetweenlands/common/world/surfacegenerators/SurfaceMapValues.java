package thebetweenlands.common.world.surfacegenerators;

public class SurfaceMapValues {
	public double underwaterHeight;
	public final double surfaceHeight;
	public final double surfaceFactor;

	public SurfaceMapValues(double surfaceHeight, double underwaterHeight, double surfaceFactor) {
		this.underwaterHeight = underwaterHeight;
		this.surfaceHeight = surfaceHeight;
		this.surfaceFactor = surfaceFactor;
	}
}
