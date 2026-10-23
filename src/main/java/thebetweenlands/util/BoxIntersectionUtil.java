package thebetweenlands.util;

import java.util.Arrays;
import java.util.Objects;

import com.sk89q.worldedit.jlibnoise.MathHelper;

import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BoxIntersectionUtil {

	/**
	 * Finds the percentage of the volume of {@code localBounds} that {@code aabb} passes through, as {@code localBounds} is offset from its starting position by {@code movementDelta}
	 * @param localBounds the local bounds, representing the player's bounding box, as it moves to 
	 * @param movementDelta the offset that the {@code localBounds} goes through
	 * @param aabb the aabb that passes through {@code localBounds}
	 * @param includeStartingVolume whether to include the volume of {@code aabb} that {@code localBounds} initially occupies
	 * @return the percentage of the volume of {@code aabb} that is passed through by {@code localBounds} as it is offset from its starting position by {@code movementDelta}
	 */
	public static double findPercentIntersectionVolume(AABB localBounds, Vec3 movementDelta, AABB aabb, boolean includeStartingVolume) {
		// Scale universe such that localBounds is [(0, 0, 0), (1, 1, 1)]
		Vec3 offsetPos = localBounds.getMinPosition().reverse();
		AABB offsetLocalBounds = localBounds.move(offsetPos);
		Vec3 scaleFactor = new Vec3(1.0 / offsetLocalBounds.maxX, 1.0 / offsetLocalBounds.maxY, 1.0 / offsetLocalBounds.maxZ);
		
		AABB offsetAabb = aabb.move(offsetPos);
		AABB targetAabb = new AABB(offsetAabb.getMinPosition().multiply(scaleFactor), offsetAabb.getMaxPosition().multiply(scaleFactor));
		
		final double volume = targetAabb.getXsize() * targetAabb.getYsize() * targetAabb.getZsize();
		if(volume < 1E-4 * 1E-4 * 1E-4) {
			return 0;
		}

		Vec3 scaledMovementDelta = movementDelta.multiply(scaleFactor);
		
		return findIntersectionVolumeWithCenteredLocalBounds(scaledMovementDelta, targetAabb, includeStartingVolume);
	}

	/**
	 * Finds the volume of the unit aabb that {@code aabb} passes through, as the unit aabb moves from 0, 0, 0 to {@code movementDelta}
	 * @param movementDelta the offset that the unit aabb goes through
	 * @param aabb the aabb that is passed through by the unit aabb
	 * @param includeStartingVolume whether to include the volume that the unit aabb initially occupies
	 * @return the percentage of the volume of {@code aabb} that is passed through by the unit aabb, as the unit aabb moves from 0, 0, 0 to {@code movementDelta}
	 */
	public static double findIntersectionVolumeWithCenteredLocalBounds(Vec3 movementDelta, AABB aabb, boolean includeStartingVolume) {
		// Local bounds is AABB at [(0, 0, 0), (1, 1, 1)]
		
		if(!withinIntersectionBounds(movementDelta, aabb)) {
			// We are entirely outside of the bounding box
			return 0;
		}
		
		// Determine which faces are actually relevant
		// If there are faces we don't intersect with then don't consider them for the calculations which require intersections
		final AxisFacesIntersects xIntersections = getAABBIntersectionFaces(movementDelta, aabb, Axis.X);
		final AxisFacesIntersects yIntersections = getAABBIntersectionFaces(movementDelta, aabb, Axis.Y);
		final AxisFacesIntersects zIntersections = getAABBIntersectionFaces(movementDelta, aabb, Axis.Z);
		
		// Time axis
		// * At time 0, the player bounding box is [(0, 0, 0), (1, 1, 1)]
		// * At time 1, the player bounding box is [(0, 0, 0) + movementDelta, (1, 1, 1) + movementDelta]
		
		// Get the time values for the first and last intersections along each axis
		final double minXIntersectsZero = computeIntersectionTime(movementDelta, aabb, Axis.X, AxisIntersection.MIN_VAL_INTERSECTS_ZERO, xIntersections);
		final double minXIntersectsOne  = computeIntersectionTime(movementDelta, aabb, Axis.X, AxisIntersection.MIN_VAL_INTERSECTS_ONE,  xIntersections);
		final double maxXIntersectsZero = computeIntersectionTime(movementDelta, aabb, Axis.X, AxisIntersection.MAX_VAL_INTERSECTS_ZERO, xIntersections);
		final double maxXIntersectsOne  = computeIntersectionTime(movementDelta, aabb, Axis.X, AxisIntersection.MAX_VAL_INTERSECTS_ONE,  xIntersections);

		final double minYIntersectsZero = computeIntersectionTime(movementDelta, aabb, Axis.Y, AxisIntersection.MIN_VAL_INTERSECTS_ZERO, yIntersections);
		final double minYIntersectsOne  = computeIntersectionTime(movementDelta, aabb, Axis.Y, AxisIntersection.MIN_VAL_INTERSECTS_ONE,  yIntersections);
		final double maxYIntersectsZero = computeIntersectionTime(movementDelta, aabb, Axis.Y, AxisIntersection.MAX_VAL_INTERSECTS_ZERO, yIntersections);
		final double maxYIntersectsOne  = computeIntersectionTime(movementDelta, aabb, Axis.Y, AxisIntersection.MAX_VAL_INTERSECTS_ONE,  yIntersections);

		final double minZIntersectsZero = computeIntersectionTime(movementDelta, aabb, Axis.Z, AxisIntersection.MIN_VAL_INTERSECTS_ZERO, zIntersections);
		final double minZIntersectsOne  = computeIntersectionTime(movementDelta, aabb, Axis.Z, AxisIntersection.MIN_VAL_INTERSECTS_ONE,  zIntersections);
		final double maxZIntersectsZero = computeIntersectionTime(movementDelta, aabb, Axis.Z, AxisIntersection.MAX_VAL_INTERSECTS_ZERO, zIntersections);
		final double maxZIntersectsOne  = computeIntersectionTime(movementDelta, aabb, Axis.Z, AxisIntersection.MAX_VAL_INTERSECTS_ONE,  zIntersections);
		
		// JIT loves this
		final double[] sortedIntersections = new double[] {
			0,
			minXIntersectsZero, minXIntersectsOne, maxXIntersectsZero, maxXIntersectsOne,
			minYIntersectsZero, minYIntersectsOne, maxYIntersectsZero, maxYIntersectsOne,
			minZIntersectsZero, minZIntersectsOne, maxZIntersectsZero, maxZIntersectsOne,
			1
		};
		
		// Sort for smallest time to largest
		Arrays.sort(sortedIntersections);
		
		return calculateVolumeFromIntersections(movementDelta, aabb, sortedIntersections, includeStartingVolume);
	}
	
	/**
	 * Calculates the volume of a trapezoid with the specified dimensions
	 * @param width1 the width of the base rectangle
	 * @param width2 the width of the top rectangle
	 * @param length1 the length of the base rectangle
	 * @param length2 the length of the top rectangle
	 * @param height the height of the trapezoid
	 * @return
	 */
	private static double trapezoidVolume(double width1, double width2, double length1, double length2, double height) {
		final double widthDiff = Math.abs(width2 - width1);
		final double lengthDiff = Math.abs(length2 - length1);
		final double corner = widthDiff * lengthDiff / 3.0;
		
		return Math.fma(width1 + width2, length1 + length2, corner) * (height / 4.0);
	}

	
	/**
	 * Context class used to easily pass around variables
	 */
	private static final class VolumeSolveContext {
		private static final ThreadLocal<VolumeSolveContext> INSTANCE = ThreadLocal.<VolumeSolveContext>withInitial(VolumeSolveContext::new);
		
		// for each <axis> of x, y, and z, there is a "primary" plane and two "secondary" planes
		// The primary plane is always relevant, but the secondary planes are only necessary to account for the initial area.
		// * Planes[0] is the primary plane:
		//       It is the face of the target aabb on this <axis> which moves towards 0, 0
		// * Planes[1] is the "subtractive" secondary plane:
		//       It shares the same position as the primary plane along <axis>, but its dimensions are a projection of [0, 0] -> [1, 1] onto the primary plane
		//       The volume it passes through is subtracted from the final result
		// * Planes[2] is the "additive" secondary plane:
		//       It's position on <axis> is either 1 or 0, and its dimensions are a projection of [0, 0] -> [1, 1] onto the primary plane
		//       The volume it passes through is added to the final result
		// If there is no initial intersection between the target AABB and the unit aabb, then Planes[1] and Planes[2] are equal and cancel out.
		
		public final double[][] xPlanes = new double[3][6];
		public final double[][] yPlanes = new double[3][6];
		public final double[][] zPlanes = new double[3][6];
		
		public boolean hasInitialIntersection = false;
		public Vec3 movementDelta;
		public AABB targetAABB;
		
		private static void setPlane(double[] plane, double x1, double y1, double z1, double x2, double y2, double z2) {
			plane[0] = x1; plane[1] = y1; plane[2] = z1;
			plane[3] = x2; plane[4] = y2; plane[5] = z2;
		}
		
		/**
		 * Sets up this context with the relevant movement delta and target aabb
		 * @param movementDelta
		 * @param targetAABB
		 */
		public void setup(Vec3 movementDelta, AABB targetAABB) {
			this.movementDelta = movementDelta;
			this.targetAABB = targetAABB;
			
			this.hasInitialIntersection = !(targetAABB.maxX < 0 || 1 < targetAABB.minX) &&
					!(targetAABB.maxY < 0 || 1 < targetAABB.minY) &&
					!(targetAABB.maxZ < 0 || 1 < targetAABB.minZ);
			
			this.setupPlanes(movementDelta, targetAABB);
		}
		
		private void setupPlanes(Vec3 movementDelta, AABB aabb) {
			// Fetch AABB bounds
			// Get "clamped" values
			final double minX = Math.max(aabb.minX, 0.0),
			             minY = Math.max(aabb.minY, 0.0),
			             minZ = Math.max(aabb.minZ, 0.0);
			
			final double maxX = Math.min(aabb.maxX, 1.0),
			             maxY = Math.min(aabb.maxY, 1.0),
			             maxZ = Math.min(aabb.maxZ, 1.0);

			// Calculate primary plane primary axis values
			final double primaryX = (movementDelta.x >= 0) ? aabb.minX : aabb.maxX;
			final double primaryY = (movementDelta.y >= 0) ? aabb.minY : aabb.maxY;
			final double primaryZ = (movementDelta.z >= 0) ? aabb.minZ : aabb.maxZ;
			
			// Calculate secondary plane clamped position
			final double secondaryX = (movementDelta.x >= 0) ? Math.max(aabb.minX, 1.0) : Math.min(aabb.maxX, 0.0);
			final double secondaryY = (movementDelta.y >= 0) ? Math.max(aabb.minY, 1.0) : Math.min(aabb.maxY, 0.0);
			final double secondaryZ = (movementDelta.z >= 0) ? Math.max(aabb.minZ, 1.0) : Math.min(aabb.maxZ, 0.0);

			// Primary planes don't clamp any values
			
			// x primary plane
			setPlane(xPlanes[0],
					primaryX, aabb.minY, aabb.minZ,
					primaryX, aabb.maxY, aabb.maxZ);
			// x subtractive plane
			setPlane(xPlanes[1],
					primaryX, minY, minZ,
					primaryX, maxY, maxZ);
			// x additive plane
			setPlane(xPlanes[2],
					secondaryX, minY, minZ,
					secondaryX, maxY, maxZ);

			// y primary plane
			setPlane(yPlanes[0],
					aabb.minX, primaryY, aabb.minZ,
					aabb.maxX, primaryY, aabb.maxZ);
			// y subtractive plane
			setPlane(yPlanes[1],
					minX, primaryY, minZ,
					maxX, primaryY, maxZ);
			// y additive plane
			setPlane(yPlanes[2],
					minX, secondaryY, minZ,
					maxX, secondaryY, maxZ);

			// z primary plane
			setPlane(zPlanes[0],
					aabb.minX, aabb.minY, primaryZ,
					aabb.maxX, aabb.maxY, primaryZ);
			// z subtractive plane
			setPlane(zPlanes[1],
					minX, minY, primaryZ,
					maxX, maxY, primaryZ);
			// z additive plane
			setPlane(zPlanes[2],
					minX, minY, secondaryZ,
					maxX, maxY, secondaryZ);
		}
	}
	
	/**
	 * Calculate the size of the intersection between a plane and the unit cube along an axis.
	 * In other words: if the plane at time {@code delta} were an aabb, and X was the intersection of aabb and the unit aabb,
	 *                 then this method finds the length of one of X's sides
	 * @param plane
	 * @param delta
	 * @param time
	 * @param axis
	 * @return
	 */
	private static double calculateSize(double[] plane, Vec3 delta, Axis axis, double time) {
		final double minPos = axis.choose(plane[0], plane[1], plane[2]);
		final double maxPos = axis.choose(plane[3], plane[4], plane[5]);
		final double offset = delta.get(axis) * time;

		return Math.clamp(1 + offset, minPos, maxPos) - Math.clamp(offset, minPos, maxPos);
	}
	
	/**
	 * Calculate the difference between the clamped position of the plane from time 1 to time 2, along an axis.
	 * @param plane
	 * @param delta
	 * @param time1
	 * @param time2
	 * @param axis
	 * @return
	 */
	private static double calculateClampedDistance(double[] plane, Vec3 delta, double currentTime, double prevTime, Axis axis) {
		// Select the primary value
		final double pos = axis.choose(plane[0], plane[1], plane[2]);
		assert pos == axis.choose(plane[3], plane[4], plane[5]) : "primary value must be the same at both min and max positions";
		final double deltaValue = delta.get(axis);

		return Math.abs(Math.clamp(pos - currentTime * deltaValue, 0.0, 1.0) - Math.clamp(pos - prevTime * deltaValue, 0.0, 1.0));
	}
	
	private static double calculatePlaneVolume(VolumeSolveContext ctx, double currentTime, double prevTime, double[] plane, Axis axis) {
		// Across all 9 planes, we perform a lot of duplicate size calculations here,
		// but I'd rather have something readable and working before trying to optimize this
		double width1, width2;
		double length1, length2;
		
		final Vec3 delta = ctx.movementDelta;
		
		// Find width and height of plane at different times
		switch (axis) {
			case X -> {
				width1  = calculateSize(plane, delta, Axis.Y, prevTime);
				width2  = calculateSize(plane, delta, Axis.Y, currentTime);
				length1 = calculateSize(plane, delta, Axis.Z, prevTime);
				length2 = calculateSize(plane, delta, Axis.Z, currentTime);
			}
			case Y -> {
				width1  = calculateSize(plane, delta, Axis.X, prevTime);
				width2  = calculateSize(plane, delta, Axis.X, currentTime);
				length1 = calculateSize(plane, delta, Axis.Z, prevTime);
				length2 = calculateSize(plane, delta, Axis.Z, currentTime);
			}
			case Z -> {
				width1  = calculateSize(plane, delta, Axis.X, prevTime);
				width2  = calculateSize(plane, delta, Axis.X, currentTime);
				length1 = calculateSize(plane, delta, Axis.Y, prevTime);
				length2 = calculateSize(plane, delta, Axis.Y, currentTime);
			}
			default -> throw new IllegalStateException("unreachable");
		}

		// Calculate how much the position changed along the primary (normal) axis
		double height = calculateClampedDistance(plane, delta, currentTime, prevTime, axis);

		// Calculate the volume of the relevant trapezoid
		return trapezoidVolume(width1, width2, length1, length2, height);
	}
	
	/**
	 * Precision summation via Kahan's Summation Algorithm
	 * @param out an array containing the {@code total} and correction sum {@code c]
	 * @param input the input array
	 */
	@SafeVarargs
	private static void precisionSum(double[] out, double ...input) {
		assert out.length == 2;
		// Get total and correction sum
		double total = out[0];
		double c = out[1];
		
		for (double value : input) {
			final double y = value - c;
			final double t = total + y;
			c = (t - total) - y;
			total = t;
		}
		
		// Put total and correction sum in output
		out[0] = total;
		out[1] = c;
	}
	
	/**
	 * In this method we "move" the unit aabb from (0, 0, 0) to movementDelta.
	 * We accept a sorted array of {@link Intersection Intersections} that we can iterate through in ascending time order.
	 * We use this to keep track of where the unit aabb is relative to the target aabb and use that to construct our formulas.
	 * Far from perfect, but it runs in fixed time
	 * @param movementDelta
	 * @param aabb
	 * @param sortedIntersections an array of length {@code 18} containing values in range {@code 0.0} to {@code 1.0} (or {@link Double#NaN NaN}),
	 *                            representing the times that the one of the unit aabb's edges intersect with the target aabb.
	 *                            Additionally, this array should be sorted from smallest to largest ({@code NaN}s last).
	 * @param includeStartingVolume whether to include the volume that was already intersected with
	 * @return
	 */
	private static double calculateVolumeFromIntersections(Vec3 movementDelta, AABB aabb, double[] sortedIntersections, boolean includeStartingVolume) {
		assert sortedIntersections.length == 18 : "there are 18 intersection times";
		assert sortedIntersections[0] == 0.0 : "first intersection time is always 0";
		
		// Get / create our context
		final VolumeSolveContext ctx = Objects.requireNonNull(VolumeSolveContext.INSTANCE.get());
		// Setup the context
		ctx.setup(movementDelta, aabb);
		
		// Volume (+ c for Kahan summation)
		double[] totalVolume = new double[2];
		
		if (includeStartingVolume) {
			// Find starting volume
			double xSize = Math.clamp(aabb.maxX, 0.0, 1.0) - Math.clamp(aabb.minX, 0.0, 1.0);
			double ySize = Math.clamp(aabb.maxY, 0.0, 1.0) - Math.clamp(aabb.minY, 0.0, 1.0);
			double zSize = Math.clamp(aabb.maxZ, 0.0, 1.0) - Math.clamp(aabb.minZ, 0.0, 1.0);
			totalVolume[0] = xSize * ySize * zSize;

			ctx.hasInitialIntersection = false;
		}
		
		double prevTime = 0.0;
		
		// Start at i = 1, because we need to access both the current value and the previous value
		// The previous value is always 0
		for (int i = 1; i < sortedIntersections.length; ++i) {
			assert sortedIntersections[i - 1] == prevTime : "prevTime invariant";
			
			// Get current time
			double currentTime = sortedIntersections[i];
			// If we have reached NaN, then we have reached the end of our data
			if (Double.isNaN(currentTime)) break;
			// If the current time and previous time are equal, do nothing
			if (prevTime == currentTime) continue;

			// Sum primary plane volumes
			double xVolume = calculatePlaneVolume(ctx, currentTime, prevTime, ctx.xPlanes[0], Axis.X);
			double yVolume = calculatePlaneVolume(ctx, currentTime, prevTime, ctx.yPlanes[0], Axis.Y);
			double zVolume = calculatePlaneVolume(ctx, currentTime, prevTime, ctx.zPlanes[0], Axis.Z);

			// If we have an initial intersection, also add the subtractive and additive secondary planes
			double xSecondaryVol = 0.0, ySecondaryVol = 0.0, zSecondaryVol = 0.0;
			if (ctx.hasInitialIntersection) {
				// Calculate secondary volumes in different variables for precision reasons
				xSecondaryVol -= calculatePlaneVolume(ctx, currentTime, prevTime, ctx.xPlanes[1], Axis.X);
				xSecondaryVol += calculatePlaneVolume(ctx, currentTime, prevTime, ctx.xPlanes[2], Axis.X);

				ySecondaryVol -= calculatePlaneVolume(ctx, currentTime, prevTime, ctx.yPlanes[1], Axis.Y);
				ySecondaryVol += calculatePlaneVolume(ctx, currentTime, prevTime, ctx.yPlanes[2], Axis.Y);
				
				zSecondaryVol -= calculatePlaneVolume(ctx, currentTime, prevTime, ctx.zPlanes[1], Axis.Z);
				zSecondaryVol += calculatePlaneVolume(ctx, currentTime, prevTime, ctx.zPlanes[2], Axis.Z);
			}
			
			// Precision sum
			precisionSum(totalVolume, xVolume, yVolume, zVolume, xSecondaryVol, ySecondaryVol, zSecondaryVol);
			
			// Update prevTime
			prevTime = currentTime;
		}
		
		// Last processed time should be exactly 1.0
		assert prevTime == 1.0;
		
		return totalVolume[0];
	}

	// I wrote these tests for a different problem (find volume of target aabb that is passed through), which I realised wasn't useful for what I wanted it for
	// In future, it might be worth implementing that original problem and reusing these tests
//	// Test function for making sure findIntersectionVolumeWithCenteredLocalBounds gives the right output
//	public static void main(String[] args) {
//		System.out.printf("Test: expected 2.5, got %f%n", findIntersectionVolumeWithCenteredLocalBounds(new Vec3(4, 1, 0), new AABB(1, -1, -1, 3, 2, 2), true));
//		System.out.printf("Test: expected 3.3, got %f%n", findIntersectionVolumeWithCenteredLocalBounds(new Vec3(2.5, 1, 0), new AABB(1, -1, -2, 3.5, 2, 3), true));
//		System.out.printf("Test: expected 2.475, got %f%n", findIntersectionVolumeWithCenteredLocalBounds(new Vec3(2.5, 2, 0), new AABB(1, -1, -1, 3.5, 2, 2), true));
//		System.out.printf("Test: expected 2.0, got %f%n", findIntersectionVolumeWithCenteredLocalBounds(new Vec3(1, 2, 0), new AABB(0.5, 0, -1, 4, 2, 2), true));
//		System.out.printf("Test: expected 0.5, got %f%n", findIntersectionVolumeWithCenteredLocalBounds(new Vec3(0, 4, 0), new AABB(0.5, 0, 0, 2, 1, 1), true));
//		System.out.printf("Test: expected 1.0, got %f%n", findIntersectionVolumeWithCenteredLocalBounds(new Vec3(0, 4, 0), new AABB(0.5, 0, 0, 2, 2, 1), true));
//		// Starts outside, ends outside, tests all loop invariants for the X axis
//		System.out.printf("Test: expected 1.0, got %f%n", findIntersectionVolumeWithCenteredLocalBounds(new Vec3(10, 0, 0), new AABB(2, -1, -1, 3, 3, 3), true));
//		// Starts outside, ends partially inside
//		System.out.printf("Test: expected 1.0, got %f%n", findIntersectionVolumeWithCenteredLocalBounds(new Vec3(2.5, 0, 0), new AABB(2, -1, -1, 3, 3, 3), true));
//	}
	
	/**
	 * Computes the time that the specified intersection occurs, if it is within the range [0.0, 1.0] and the face is not ignored.
	 * If the intersection happens outside of the [0.0, 1.0] time range or the intersection cannot occur, returns {@link Double#NaN} instead.
	 * @param movementDelta
	 * @param aabb
	 * @param axis
	 * @param intersection
	 * @param relevantFaces
	 * @return
	 */
	public static double computeIntersectionTime(Vec3 movementDelta, AABB aabb, Axis axis, AxisIntersection intersection, AxisFacesIntersects relevantFaces) {
		// If the face is ignored, then return NaN
		if (intersection.isMin() && !relevantFaces.negativeFaceIntersects()) {
			return Double.NaN;
		} else if (intersection.isMax() && !relevantFaces.positiveFaceIntersects()) {
			return Double.NaN;
		}
		
		// Calculate the time that the intersection happens
		final double movement = movementDelta.get(axis);
		final double time = switch(intersection) {
			case MIN_VAL_INTERSECTS_ZERO -> aabb.min(axis) / movement;
			case MIN_VAL_INTERSECTS_ONE -> (aabb.min(axis) - 1) / movement;
			case MAX_VAL_INTERSECTS_ZERO -> aabb.max(axis) / movement;
			case MAX_VAL_INTERSECTS_ONE -> (aabb.max(axis) - 1) / movement;
		};
		
		// If the time isn't in range [0, 1], then return NaN
		return time >= 0 && time <= 1 ? time : Double.NaN;
	}

	/**
	 * Returns which sides of the aabb will intersect with [(0, 0, 0), (1, 1, 1)] as it moves from (0, 0, 0) to movementDelta.
	 * Only finds the sides that will intersect on the specified axis, for faster checking elsewhere
	 * @param movementDelta
	 * @param aabb
	 * @param axis
	 * @return
	 */
	public static AxisFacesIntersects getAABBIntersectionFaces(Vec3 movementDelta, AABB aabb, Axis axis) {
		// Get bounds for the target aabb
		final double movement = movementDelta.get(axis);
		final double minBound = Math.min(0, movement);
		final double maxBound = 1 + Math.max(0, movement);
		// Check the positive face
		final double maxFace = aabb.max(axis);
		final boolean hasPositiveFace = minBound <= maxFace && maxFace <= maxBound;
		// Check the negative face
		final double minFace = aabb.min(axis);
		final boolean hasNegativeFace = minBound <= minFace && minFace <= maxBound;
		
		return AxisFacesIntersects.of(hasPositiveFace, hasNegativeFace);
	}
	
	public static enum AxisFacesIntersects {
		NO_FACES_INTERSECT(false, false),
		POSITIVE_FACE_INTERSECTS(true, false),
		NEGATIVE_FACE_INTERSECTS(false, true),
		BOTH_FACES_INTERSECT(true, true);

		private final boolean positiveFaceIntersects;
		private final boolean negativeFaceIntersects;
		
		private AxisFacesIntersects(boolean positiveFaceIntersects, boolean negativeFaceIntersects) {
			this.positiveFaceIntersects = positiveFaceIntersects;
			this.negativeFaceIntersects = negativeFaceIntersects;
		}

		public boolean positiveFaceIntersects() {
			return this.positiveFaceIntersects;
		}

		public boolean negativeFaceIntersects() {
			return this.negativeFaceIntersects;
		}
		
		public AxisFacesIntersects withPositive(boolean positive) {
			if(this.negativeFaceIntersects()) {
				return positive ? BOTH_FACES_INTERSECT : NEGATIVE_FACE_INTERSECTS;
			} else {
				return positive ? POSITIVE_FACE_INTERSECTS : NO_FACES_INTERSECT;
			}
		}
		
		public AxisFacesIntersects withNegative(boolean negative) {
			if(this.positiveFaceIntersects()) {
				return negative ? BOTH_FACES_INTERSECT : POSITIVE_FACE_INTERSECTS;
			} else {
				return negative ? NEGATIVE_FACE_INTERSECTS : NO_FACES_INTERSECT;
			}
		}
		
		public static AxisFacesIntersects of(final boolean positiveFaceIntersects, final boolean negativeFaceIntersects) {
			if(positiveFaceIntersects) {
				return negativeFaceIntersects ? BOTH_FACES_INTERSECT : POSITIVE_FACE_INTERSECTS;
			} else {
				return negativeFaceIntersects ? NEGATIVE_FACE_INTERSECTS : NO_FACES_INTERSECT;
			}
		}
	}

	/**
	 * Heuristic check that determines if the aabb is outside of the range of [(0, 0, 0), (1, 1, 1)] as it moves from (0, 0, 0) to movementDelta.
	 * <p>If {@code true}, does not guarantee that there will be an intersection between aabb and [(0, 0, 0), (1, 1, 1)]</p>
	 * <p>If {@code false}, guarantees that there will <strong>not</strong> be an intersection between aabb and [(0, 0, 0), (1, 1, 1)]</p>
	 * @param movementDelta
	 * @param aabb
	 * @return
	 */
	public static boolean withinIntersectionBounds(Vec3 movementDelta, AABB aabb) {
		// If it's outside of our x range
		if (aabb.minX > 1 && aabb.minX > movementDelta.x + 1) {
			// Too far in front of x
			return false;
		}
		
		if (aabb.maxX < 0 && aabb.maxX < movementDelta.x) {
			// Too far behind x
			return false;
		}
		
		// If it's outside of our y range
		if (aabb.minY> 1 && aabb.minY > movementDelta.y + 1) {
			// Too far in front of y
			return false;
		}
		
		if (aabb.maxY < 0 && aabb.maxY < movementDelta.y) {
			// Too far behind y
			return false;
		}

		// If it's outside of our z range
		if (aabb.minZ > 1 && aabb.minZ > movementDelta.z + 1) {
			// Too far in front of z
			return false;
		}
		
		if (aabb.maxZ < 0 && aabb.maxZ < movementDelta.z) {
			// Too far behind z
			return false;
		}
		
		return true;
	}
	
	/**
	 * Represents the type of intersection along an axis.
	 */
	private static enum AxisIntersection {
		MIN_VAL_INTERSECTS_ZERO,
		MIN_VAL_INTERSECTS_ONE,
		MAX_VAL_INTERSECTS_ZERO,
		MAX_VAL_INTERSECTS_ONE;
		
		public double getIntersection(double minIntersectsZero, double minIntersectsOne, double maxIntersectsZero, double maxIntersectsOne) {
			return switch(this) {
				case MIN_VAL_INTERSECTS_ZERO -> minIntersectsZero;
				case MIN_VAL_INTERSECTS_ONE -> minIntersectsOne;
				case MAX_VAL_INTERSECTS_ZERO -> maxIntersectsZero;
				case MAX_VAL_INTERSECTS_ONE -> maxIntersectsOne;
			};
		}
		
		public AxisIntersection getInverse() {
			return switch(this) {
				case MIN_VAL_INTERSECTS_ZERO -> MIN_VAL_INTERSECTS_ZERO;
				case MIN_VAL_INTERSECTS_ONE -> MAX_VAL_INTERSECTS_ZERO;
				case MAX_VAL_INTERSECTS_ZERO -> MIN_VAL_INTERSECTS_ONE;
				case MAX_VAL_INTERSECTS_ONE -> MAX_VAL_INTERSECTS_ONE;
			};
		}

		// Gets the face of the *unit cube* that this intersection is on
		public AxisDirection getAxisDirection() {
			return switch(this) {
				case MIN_VAL_INTERSECTS_ZERO -> AxisDirection.NEGATIVE;
				case MIN_VAL_INTERSECTS_ONE -> AxisDirection.POSITIVE;
				case MAX_VAL_INTERSECTS_ZERO -> AxisDirection.NEGATIVE;
				case MAX_VAL_INTERSECTS_ONE -> AxisDirection.POSITIVE;
			};
		}
		
		public boolean isMin() {
			return this == MIN_VAL_INTERSECTS_ZERO || this == MIN_VAL_INTERSECTS_ONE;
		}
		
		public boolean isMax() {
			return this == MAX_VAL_INTERSECTS_ZERO || this == MAX_VAL_INTERSECTS_ONE;
		}
		
		public boolean isZero() {
			return this == MIN_VAL_INTERSECTS_ZERO || this == MAX_VAL_INTERSECTS_ZERO;
		}
		
		public boolean isOne() {
			return this == MIN_VAL_INTERSECTS_ONE || this == MAX_VAL_INTERSECTS_ONE;
		}
	}
}
