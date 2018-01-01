package thebetweenlands.util;

import net.minecraft.util.math.Vec3d;

public interface ISpline {
	/**
	 * Interpolates and returns the position for the specified parameter t
	 * @param t Interpolation parameter, range [0, 1]
	 * @return
	 */
	public Vec3d interpolate(float t);

	/**
	 * Returns the first derivative of the spline at the specified parameter t
	 * @param t Interpolation parameter, range [0, 1]
	 * @return
	 */
	public Vec3d derivative(float t);

	/**
	 * Returns the number of segments
	 * @return
	 */
	public int getNumSegments();
	
	/**
	 * Returns all nodes of this spline
	 * @return
	 */
	public Vec3d[] getNodes();
	
	/**
	 * Returns whether this spline can return the arc length
	 * @return
	 */
	public boolean hasArcLength();
	
	/**
	 * Returns the arc length of this spline if supported, see {@link #hasArcLength()}
	 * @return
	 */
	public double getArcLength();
}
