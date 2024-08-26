package thebetweenlands.util;


import net.minecraft.world.phys.Vec3;

public interface ISpline {
	/**
	 * Interpolates and returns the position for the specified parameter t
	 *
	 * @param t Interpolation parameter, range [0, 1]
	 * @return
	 */
	Vec3 interpolate(float t);

	/**
	 * Returns the first derivative of the spline at the specified parameter t
	 *
	 * @param t Interpolation parameter, range [0, 1]
	 * @return
	 */
	Vec3 derivative(float t);

	/**
	 * Returns the number of segments
	 *
	 * @return
	 */
	int getNumSegments();

	/**
	 * Returns all nodes of this spline
	 *
	 * @return
	 */
	Vec3[] getNodes();

	/**
	 * Returns whether this spline can return the arc length
	 *
	 * @return
	 */
	boolean hasArcLength();

	/**
	 * Returns the arc length of this spline if supported, see {@link #hasArcLength()}
	 *
	 * @return
	 */
	double getArcLength();
}
