package thebetweenlands.util;

import java.util.Arrays;
import java.util.Objects;

import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BoxIntersectionUtil {

	/**
	 * Finds the percentage of the volume of {@code aabb} that is passed through by {@code localBounds} as it is offset from its starting position by {@code movementDelta}
	 * @param localBounds the local bounds, representing the player's bounding box
	 * @param movementDelta the offset that the {@code localBounds} goes through
	 * @param aabb the aabb that is passed through by {@code localBounds}
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
		
		return findIntersectionVolumeWithCenteredLocalBounds(scaledMovementDelta, targetAabb, includeStartingVolume) / volume;
	}

	/**
	 * Finds the volume of {@code aabb} that is passed through by the unit aabb, as the unit aabb moves from 0, 0, 0 to {@code movementDelta}
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
		final Intersection minXIntersectsZero = Intersection.compute(movementDelta, aabb, Axis.X, AxisIntersection.MIN_VAL_INTERSECTS_ZERO, xIntersections);
		final Intersection minXIntersectsOne  = Intersection.compute(movementDelta, aabb, Axis.X, AxisIntersection.MIN_VAL_INTERSECTS_ONE,  xIntersections);
		final Intersection maxXIntersectsZero = Intersection.compute(movementDelta, aabb, Axis.X, AxisIntersection.MAX_VAL_INTERSECTS_ZERO, xIntersections);
		final Intersection maxXIntersectsOne  = Intersection.compute(movementDelta, aabb, Axis.X, AxisIntersection.MAX_VAL_INTERSECTS_ONE,  xIntersections);

		final Intersection minYIntersectsZero = Intersection.compute(movementDelta, aabb, Axis.Y, AxisIntersection.MIN_VAL_INTERSECTS_ZERO, yIntersections);
		final Intersection minYIntersectsOne  = Intersection.compute(movementDelta, aabb, Axis.Y, AxisIntersection.MIN_VAL_INTERSECTS_ONE,  yIntersections);
		final Intersection maxYIntersectsZero = Intersection.compute(movementDelta, aabb, Axis.Y, AxisIntersection.MAX_VAL_INTERSECTS_ZERO, yIntersections);
		final Intersection maxYIntersectsOne  = Intersection.compute(movementDelta, aabb, Axis.Y, AxisIntersection.MAX_VAL_INTERSECTS_ONE,  yIntersections);

		final Intersection minZIntersectsZero = Intersection.compute(movementDelta, aabb, Axis.Z, AxisIntersection.MIN_VAL_INTERSECTS_ZERO, zIntersections);
		final Intersection minZIntersectsOne  = Intersection.compute(movementDelta, aabb, Axis.Z, AxisIntersection.MIN_VAL_INTERSECTS_ONE,  zIntersections);
		final Intersection maxZIntersectsZero = Intersection.compute(movementDelta, aabb, Axis.Z, AxisIntersection.MAX_VAL_INTERSECTS_ZERO, zIntersections);
		final Intersection maxZIntersectsOne  = Intersection.compute(movementDelta, aabb, Axis.Z, AxisIntersection.MAX_VAL_INTERSECTS_ONE,  zIntersections);
		
		// JIT loves this
		final Intersection[] sortedIntersections = new Intersection[] {
			minXIntersectsZero, minXIntersectsOne, maxXIntersectsZero, maxXIntersectsOne,
			minYIntersectsZero, minYIntersectsOne, maxYIntersectsZero, maxYIntersectsOne,
			minZIntersectsZero, minZIntersectsOne, maxZIntersectsZero, maxZIntersectsOne
		};
		
		// Sort for smallest time to largest
		Arrays.sort(sortedIntersections);
		
		return calculateVolumeFromIntersections(movementDelta, aabb, sortedIntersections, includeStartingVolume);
	}
	
	// Big bulky method
	// TODO break this method up into chunks
	/**
	 * In this method we "move" the unit aabb from (0, 0, 0) to movementDelta.
	 * We accept a sorted array of {@link Intersection Intersections} that we can iterate through in ascending time order.
	 * We use this to keep track of where the unit aabb is relative to the target aabb and use that to construct our formulas.
	 * Far from perfect, but it runs in fixed time
	 * @param movementDelta
	 * @param aabb
	 * @param sortedIntersections an array of length {@code 16} containing non-null {@link Intersection Intersections}, sorted in ascending {@link Intersection#time() time} order
	 * @param includeStartingVolume whether to include the volume that was already intersected with
	 * @return
	 */
	private static double calculateVolumeFromIntersections(Vec3 movementDelta, AABB aabb, Intersection[] sortedIntersections, boolean includeStartingVolume) {
		// Compute axis relations at time 0
		// The "Min X", "Max X", etc. being referred to is the Min X of the *unit aabb*, not of the target aabb
		AxisRelation minXAxisRelation = AxisRelation.compute(0, aabb.minX, aabb.maxX);
		AxisRelation maxXAxisRelation = AxisRelation.compute(1, aabb.minX, aabb.maxX);
		AxisRelation minYAxisRelation = AxisRelation.compute(0, aabb.minY, aabb.maxY);
		AxisRelation maxYAxisRelation = AxisRelation.compute(1, aabb.minY, aabb.maxY);
		AxisRelation minZAxisRelation = AxisRelation.compute(0, aabb.minZ, aabb.maxZ);
		AxisRelation maxZAxisRelation = AxisRelation.compute(1, aabb.minZ, aabb.maxZ);

		// TODO replace with booleans
		final AxisDirection xRelevantFace = movementDelta.x >= 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE;
		final AxisDirection yRelevantFace = movementDelta.y >= 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE;
		final AxisDirection zRelevantFace = movementDelta.z >= 0 ? AxisDirection.POSITIVE : AxisDirection.NEGATIVE;
		
		// The amount of xSize that is multiplied by time
		double xSizeCoefficient = calculateSizeCoefficient(minXAxisRelation, maxXAxisRelation, movementDelta.x);
		// The amount of xSize that is not multiplied by time
		double xSizeConstant = calculateSizeConstant(minXAxisRelation, maxXAxisRelation, aabb.minX, aabb.maxX);
		// xSize = (xSizeCoefficient * time + xSizeConstant)
		
		// The amount of ySize that is multiplied by time
		double ySizeCoefficient = calculateSizeCoefficient(minYAxisRelation, maxYAxisRelation, movementDelta.y);
		// The amount of ySize that is not multiplied by time
		double ySizeConstant = calculateSizeConstant(minYAxisRelation, maxYAxisRelation, aabb.minY, aabb.maxY);
		// ySize = (ySizeCoefficient * time + ySizeConstant)
		
		// The amount of zSize that is multiplied by time
		double zSizeCoefficient = calculateSizeCoefficient(minZAxisRelation, maxZAxisRelation, movementDelta.z);
		// The amount of zSize that is not multiplied by time
		double zSizeConstant = calculateSizeConstant(minZAxisRelation, maxZAxisRelation, aabb.minZ, aabb.maxZ);
		// zSize = (zSizeCoefficient * time + zSizeConstant)
		
		// Total volume; starts with s_x(0)*s_y(0)*s_z(0)
		// volume = (xSizeCoefficient * 0 + xSizeConstant) * (ySizeCoefficient * 0 + ySizeConstant) * (zSizeCoefficient * 0 + zSizeConstant);
		double volume = includeStartingVolume ? (xSizeConstant) * (ySizeConstant) * (zSizeConstant) : 0;
		double c = 0.0; // Kahan summation algorithm

		// When evaluating volumes and sizes, this loop uses absolute positions when possible:
		//     1. X being BELOW_MIN: xSize, timediff for this iteration calculated via minX
		//     2. X going from BELOW_MIN to INSIDE: xSize, timediff for this iteration calculated via minX
		//     3. X being INSIDE: xSize, timediff for this iteration calculated via delta * time + constant
		//     4. X going from INSIDE to ABOVE_MAX: xSize, timediff for this iteration calculated via maxX
		//     5. X being ABOVE_MAX: xSize, timediff for this iteration calculated via maxX
		
		double prevTime = 0.0;
		double prevXSize = (xSizeCoefficient * 0 + xSizeConstant);
		double prevYSize = (ySizeCoefficient * 0 + ySizeConstant);
		double prevZSize = (zSizeCoefficient * 0 + zSizeConstant);
		AxisRelation prevXRelation = selectRelation(xRelevantFace, minXAxisRelation, maxXAxisRelation);
		AxisRelation prevYRelation = selectRelation(yRelevantFace, minYAxisRelation, maxYAxisRelation);
		AxisRelation prevZRelation = selectRelation(zRelevantFace, minZAxisRelation, maxZAxisRelation);
		for (Intersection intersection : sortedIntersections) {
			// Loop invariants.
			// Note: the expression inside an assert statement is not called when assertions are disabled, so this has no performance impact
			assert xSizeCoefficient == calculateSizeCoefficient(minXAxisRelation, maxXAxisRelation, movementDelta.x) : "incorrect x size coefficient";
			assert xSizeConstant == calculateSizeConstant(minXAxisRelation, maxXAxisRelation, aabb.minX, aabb.maxX)  : "incorrect x size constant";
			assert ySizeCoefficient == calculateSizeCoefficient(minYAxisRelation, maxYAxisRelation, movementDelta.y) : "incorrect y size coefficient";
			assert ySizeConstant == calculateSizeConstant(minYAxisRelation, maxYAxisRelation, aabb.minY, aabb.maxY)  : "incorrect y size constant";
			assert zSizeCoefficient == calculateSizeCoefficient(minZAxisRelation, maxZAxisRelation, movementDelta.z) : "incorrect z size coefficient";
			assert zSizeConstant == calculateSizeConstant(minZAxisRelation, maxZAxisRelation, aabb.minZ, aabb.maxZ)  : "incorrect z size constant";
			assert prevXRelation == selectRelation(xRelevantFace, minXAxisRelation, maxXAxisRelation) : "incorrect x relation";
			assert prevYRelation == selectRelation(yRelevantFace, minYAxisRelation, maxYAxisRelation) : "incorrect y relation";
			assert prevZRelation == selectRelation(zRelevantFace, minZAxisRelation, maxZAxisRelation) : "incorrect z relation";
			
			if (intersection.isNaN()) continue;
			final double time = intersection.time();
			if (!Double.isFinite(time) || time < 0 || time < prevTime) continue;

			final Axis axis = Objects.requireNonNull(intersection.axis());
			final AxisIntersection intersectionFace = Objects.requireNonNull(intersection.intersection());
			
			// The rate of change of the unit aabb along this axis
			final double delta = movementDelta.get(axis);
			
			// Calculate the new axis relation
			final AxisRelation newAxisType = getNextAxisType(intersectionFace, delta);
			final boolean isNewRelationPreferredForVolume = newAxisType != AxisRelation.INSIDE;
			
			// Calculate the axis relations to be used as the "now" for time calculations
			AxisRelation xRelation = prevXRelation;//selectRelation(xRelevantFace, minXAxisRelation, maxXAxisRelation);
			AxisRelation yRelation = prevYRelation;//selectRelation(yRelevantFace, minYAxisRelation, maxYAxisRelation);
			AxisRelation zRelation = prevZRelation;//selectRelation(zRelevantFace, minZAxisRelation, maxZAxisRelation);

			// Should we set the min axis relation or max axis relation? (the unit aabb's "zero" face is min axis relations, unit aabb's "one" face is max axis relations)
			final boolean isMinFace = intersectionFace.isZero();
			
			// Update the axis and size coefficients with the new values
			switch (axis) {
				case X -> {
					// Update x axis relations
					if (isMinFace) {
						minXAxisRelation = newAxisType;
					} else {
						maxXAxisRelation = newAxisType;
					}
					// Update variables for volume
					if(isNewRelationPreferredForVolume) {
						// The amount of xSize that is multiplied by time
						xSizeCoefficient = calculateSizeCoefficient(minXAxisRelation, maxXAxisRelation, movementDelta.x);
						// The amount of xSize that is not multiplied by time
						xSizeConstant = calculateSizeConstant(minXAxisRelation, maxXAxisRelation, aabb.minX, aabb.maxX);
						// xSize = (xSizeCoefficient * time + xSizeConstant)
						
						if(isMinFace == (xRelevantFace == AxisDirection.NEGATIVE)) {
							xRelation = newAxisType;
						}
					}
				}
				case Y -> {
					// Update y axis relations
					if (isMinFace) {
						minYAxisRelation = newAxisType;
					} else {
						maxYAxisRelation = newAxisType;
					}
					// Update variables for volume
					if(isNewRelationPreferredForVolume) {
						// The amount of ySize that is multiplied by time
						ySizeCoefficient = calculateSizeCoefficient(minYAxisRelation, maxYAxisRelation, movementDelta.y);
						// The amount of ySize that is not multiplied by time
						ySizeConstant = calculateSizeConstant(minYAxisRelation, maxYAxisRelation, aabb.minY, aabb.maxY);
						// ySize = (ySizeCoefficient * time + ySizeConstant)
						
						if(isMinFace == (yRelevantFace == AxisDirection.NEGATIVE)) {
							yRelation = newAxisType;
						}
					}
				}
				case Z -> {
					// Update z axis relations
					if (isMinFace) {
						minZAxisRelation = newAxisType;
					} else {
						maxZAxisRelation = newAxisType;
					}
					// Update variables for volume
					if(isNewRelationPreferredForVolume) {
						// The amount of zSize that is multiplied by time
						zSizeCoefficient = calculateSizeCoefficient(minZAxisRelation, maxZAxisRelation, movementDelta.z);
						// The amount of zSize that is not multiplied by time
						zSizeConstant = calculateSizeConstant(minZAxisRelation, maxZAxisRelation, aabb.minZ, aabb.maxZ);
						// zSize = (zSizeCoefficient * time + zSizeConstant)
						
						if(isMinFace == (zRelevantFace == AxisDirection.NEGATIVE)) {
							zRelation = newAxisType;
						}
					}
				}
				default -> throw new IllegalStateException();
			}

			// Calculate the size of each axis at the current time
			final double xSize = (xSizeCoefficient * time + xSizeConstant);
			final double ySize = (ySizeCoefficient * time + ySizeConstant);
			final double zSize = (zSizeCoefficient * time + zSizeConstant);

			// Evaluate volume formula with bounds [previousTime, time], and add to `volume`
			{
				// We use the average of the current size and previous size,
				// because it works with the math (see: trapezoid equation)
				final double xSizeAverage = (xSize + prevXSize) / 2.0;
				final double ySizeAverage = (ySize + prevYSize) / 2.0;
				final double zSizeAverage = (zSize + prevZSize) / 2.0;
				// Calculate volumes
				final double xFaceVol = ySizeAverage * zSizeAverage * getTimeDifference(
						prevTime, time,
						movementDelta.x, aabb.minX, aabb.maxX,
						xRelevantFace, prevXRelation, xRelation
					);
				final double yFaceVol = xSizeAverage * zSizeAverage * getTimeDifference(
						prevTime, time,
						movementDelta.y, aabb.minY, aabb.maxY,
						yRelevantFace, prevYRelation, yRelation
					);
				final double zFaceVol = xSizeAverage * ySizeAverage * getTimeDifference(
						prevTime, time,
						movementDelta.z, aabb.minZ, aabb.maxZ,
						zRelevantFace, prevZRelation, zRelation
					);
				// Add volumes
				addXVol: {
					// Kahan summation algorithm
					final double y = xFaceVol - c;
					final double t = volume + y;
					c = (t - volume) - y;
					volume = t;
				}
//				System.out.printf("vol + xvol: %f%n", volume);
				addYVol: {
					// Kahan summation algorithm
					final double y = yFaceVol - c;
					final double t = volume + y;
					c = (t - volume) - y;
					volume = t;
				}
				addYVol: {
					// Kahan summation algorithm
					final double y = zFaceVol - c;
					final double t = volume + y;
					c = (t - volume) - y;
					volume = t;
				}
			}

			// Update the coefficients
			if(!isNewRelationPreferredForVolume) {
				switch (axis) {
					case X -> {
						// The amount of xSize that is multiplied by time
						xSizeCoefficient = calculateSizeCoefficient(minXAxisRelation, maxXAxisRelation, movementDelta.x);
						// The amount of xSize that is not multiplied by time
						xSizeConstant = calculateSizeConstant(minXAxisRelation, maxXAxisRelation, aabb.minX, aabb.maxX);
						// xSize = (xSizeCoefficient * time + xSizeConstant)
						if(isMinFace == (xRelevantFace == AxisDirection.NEGATIVE)) {
							xRelation = newAxisType;
						}
					}
					case Y -> {
						// The amount of ySize that is multiplied by time
						ySizeCoefficient = calculateSizeCoefficient(minYAxisRelation, maxYAxisRelation, movementDelta.y);
						// The amount of ySize that is not multiplied by time
						ySizeConstant = calculateSizeConstant(minYAxisRelation, maxYAxisRelation, aabb.minY, aabb.maxY);
						// ySize = (ySizeCoefficient * time + ySizeConstant)
						if(isMinFace == (yRelevantFace == AxisDirection.NEGATIVE)) {
							yRelation = newAxisType;
						}
					}
					case Z -> {
						// The amount of zSize that is multiplied by time
						zSizeCoefficient = calculateSizeCoefficient(minZAxisRelation, maxZAxisRelation, movementDelta.z);
						// The amount of zSize that is not multiplied by time
						zSizeConstant = calculateSizeConstant(minZAxisRelation, maxZAxisRelation, aabb.minZ, aabb.maxZ);
						// zSize = (zSizeCoefficient * time + zSizeConstant)
						if(isMinFace == (zRelevantFace == AxisDirection.NEGATIVE)) {
							zRelation = newAxisType;
						}
					}
					default -> throw new IllegalStateException();
				}
			}
			
			// Store relations, time, and size values used to calculate volume
			// This is so the next loop never misses/double-counts anything (or as close as we can get to that with floating points)
			prevXRelation = xRelation;
			prevYRelation = yRelation;
			prevZRelation = zRelation;
			prevXSize = xSize;
			prevYSize = ySize;
			prevZSize = zSize;
			prevTime = time;
		}
		
		// Calculate the final volume going to t=1.0
		
		{
			// We know the loop invariants still hold after the loop:
			assert xSizeCoefficient == calculateSizeCoefficient(minXAxisRelation, maxXAxisRelation, movementDelta.x) : "incorrect x size coefficient";
			assert xSizeConstant == calculateSizeConstant(minXAxisRelation, maxXAxisRelation, aabb.minX, aabb.maxX)  : "incorrect x size constant";
			assert ySizeCoefficient == calculateSizeCoefficient(minYAxisRelation, maxYAxisRelation, movementDelta.y) : "incorrect y size coefficient";
			assert ySizeConstant == calculateSizeConstant(minYAxisRelation, maxYAxisRelation, aabb.minY, aabb.maxY)  : "incorrect y size constant";
			assert zSizeCoefficient == calculateSizeCoefficient(minZAxisRelation, maxZAxisRelation, movementDelta.z) : "incorrect z size coefficient";
			assert zSizeConstant == calculateSizeConstant(minZAxisRelation, maxZAxisRelation, aabb.minZ, aabb.maxZ)  : "incorrect z size constant";
			assert prevXRelation == selectRelation(xRelevantFace, minXAxisRelation, maxXAxisRelation) : "incorrect x relation";
			assert prevYRelation == selectRelation(yRelevantFace, minYAxisRelation, maxYAxisRelation) : "incorrect y relation";
			assert prevZRelation == selectRelation(zRelevantFace, minZAxisRelation, maxZAxisRelation) : "incorrect z relation";

			final double time = 1.0;

			// The loop invariants make this code unnecessary:
			// final AxisRelation xRelation = selectRelation(xRelevantFace, minXAxisRelation, maxXAxisRelation);
			// final AxisRelation yRelation = selectRelation(yRelevantFace, minYAxisRelation, maxYAxisRelation);
			// final AxisRelation zRelation = selectRelation(zRelevantFace, minZAxisRelation, maxZAxisRelation);
			
			// Calculate the size of each axis at the current time
			final double xSize = (xSizeCoefficient * time + xSizeConstant);
			final double ySize = (ySizeCoefficient * time + ySizeConstant);
			final double zSize = (zSizeCoefficient * time + zSizeConstant);

			// Evaluate volume formula with bounds [previousTime, time], and add to `volume`
			{
				// We use the average of the current size and previous size,
				// because it works with the math (see: trapezoid equation)
				final double xSizeAverage = (xSize + prevXSize) / 2.0;
				final double ySizeAverage = (ySize + prevYSize) / 2.0;
				final double zSizeAverage = (zSize + prevZSize) / 2.0;
				// Get current relations for time difference calculations
				// Calculate volumes
				final double xFaceVol = ySizeAverage * zSizeAverage * getTimeDifference(
						prevTime, time,
						movementDelta.x, aabb.minX, aabb.maxX,
						xRelevantFace, prevXRelation, prevXRelation
					);
				final double yFaceVol = xSizeAverage * zSizeAverage * getTimeDifference(
						prevTime, time,
						movementDelta.y, aabb.minY, aabb.maxY,
						yRelevantFace, prevYRelation, prevYRelation
					);
				final double zFaceVol = xSizeAverage * ySizeAverage * getTimeDifference(
						prevTime, time,
						movementDelta.z, aabb.minZ, aabb.maxZ,
						zRelevantFace, prevZRelation, prevZRelation
					);
				// Add volumes
				addXVol: {
					// Kahan summation algorithm
					final double y = xFaceVol - c;
					final double t = volume + y;
					c = (t - volume) - y;
					volume = t;
				}
				addYVol: {
					// Kahan summation algorithm
					final double y = yFaceVol - c;
					final double t = volume + y;
					c = (t - volume) - y;
					volume = t;
				}
				addYVol: {
					// Kahan summation algorithm
					final double y = zFaceVol - c;
					final double t = volume + y;
					c = (t - volume) - y;
					volume = t;
				}
			}
		}
		
		return volume;
	}
	
	private static AxisRelation getNextAxisType(AxisIntersection intersectionFace, final double delta) {
		// Calculate the new axis type for an intersection
		switch(Objects.requireNonNull(intersectionFace)) {
			case MIN_VAL_INTERSECTS_ZERO, MIN_VAL_INTERSECTS_ONE -> {
				if (delta < 0) {
					return AxisRelation.BELOW_MIN;
				} else {
					return AxisRelation.INSIDE;
				}
			}
			case MAX_VAL_INTERSECTS_ZERO, MAX_VAL_INTERSECTS_ONE -> {
				if (delta > 0) {
					return AxisRelation.ABOVE_MAX;
				} else {
					return AxisRelation.INSIDE;
				}
			}
			default -> throw new IllegalStateException();
		}
	}
	
	// Test function for making sure findIntersectionVolumeWithCenteredLocalBounds gives the right output
	public static void main(String[] args) {
		System.out.printf("Test: expected 2.5, got %f%n", findIntersectionVolumeWithCenteredLocalBounds(new Vec3(4, 1, 0), new AABB(1, -1, -1, 3, 2, 2), true));
		System.out.printf("Test: expected 3.3, got %f%n", findIntersectionVolumeWithCenteredLocalBounds(new Vec3(2.5, 1, 0), new AABB(1, -1, -2, 3.5, 2, 3), true));
		System.out.printf("Test: expected 2.475, got %f%n", findIntersectionVolumeWithCenteredLocalBounds(new Vec3(2.5, 2, 0), new AABB(1, -1, -1, 3.5, 2, 2), true));
		System.out.printf("Test: expected 2.0, got %f%n", findIntersectionVolumeWithCenteredLocalBounds(new Vec3(1, 2, 0), new AABB(0.5, 0, -1, 4, 2, 2), true));
		System.out.printf("Test: expected 0.5, got %f%n", findIntersectionVolumeWithCenteredLocalBounds(new Vec3(0, 4, 0), new AABB(0.5, 0, 0, 2, 1, 1), true));
		System.out.printf("Test: expected 1.0, got %f%n", findIntersectionVolumeWithCenteredLocalBounds(new Vec3(0, 4, 0), new AABB(0.5, 0, 0, 2, 2, 1), true));
		// Starts outside, ends outside, tests all loop invariants for the X axis
		System.out.printf("Test: expected 1.0, got %f%n", findIntersectionVolumeWithCenteredLocalBounds(new Vec3(10, 0, 0), new AABB(2, -1, -1, 3, 3, 3), true));
		// Starts outside, ends partially inside
		System.out.printf("Test: expected 1.0, got %f%n", findIntersectionVolumeWithCenteredLocalBounds(new Vec3(2.5, 0, 0), new AABB(2, -1, -1, 3, 3, 3), true));
	}
	
	private static AxisRelation selectRelation(AxisDirection direction, AxisRelation minRelation, AxisRelation maxRelation) {
		return switch(direction) {
			case POSITIVE -> maxRelation;
			case NEGATIVE -> minRelation;
			default -> throw new NullPointerException();
		};
	}
	
	/**
	 * Calculates the distance a face moved over time.
	 * 
	 * Unless {@code newTime < prevTime}, the result will be positive.
	 * @param prevTime the lower bound of time
	 * @param newTime
	 * @param movementDelta
	 * @param aabbMin
	 * @param aabbMax
	 * @param axisDirection
	 * @param prevAxisRelation
	 * @param newAxisRelation
	 * @return
	 * @requires Double.isFinite(prevTime) && Double.isFinite(newTime)
	 * @requires Double.isFinite(movementDelta)
	 * @requires Double.isFinite(aabbMin) && Double.isFinite(aabbMax)
	 * @ensures prevTime < newTime ==> \result >= 0.0
	 * @ensures prevTime > newTime ==> \result <= 0.0
	 * @ensures prevTime == newTime ==> \result == 0.0
	 */
	private static double getTimeDifference(
			double prevTime, double newTime,
			final double movementDelta, final double aabbMin, final double aabbMax,
			AxisDirection axisDirection,
			AxisRelation prevAxisRelation, AxisRelation newAxisRelation
	) {
//		final double delta = movementDelta.get(axis);
		final double delta = movementDelta;

		// We invert the sign when delta is negative
		// This is *NOT* equivalent to taking the absolute value, because
		//     we want newTime < prevTime to still create negative values.
		final double deltaSign = Math.signum(delta);
		
		if (!Double.isFinite(deltaSign) || deltaSign == 0) return 0.0;
		
		// use xmax as an example
		if (prevAxisRelation == AxisRelation.INSIDE && newAxisRelation == AxisRelation.INSIDE) {
			// xmin = (0 + delta * newTime) - (0 + delta * prevTime) = delta * (newTime - prevTime)
			// xmax = (1 + delta * newTime) - (1 + delta * prevTime) = delta * (newTime - prevTime)
			return delta * (newTime - prevTime) * deltaSign;
		} else {
//			final double min = aabb.min(axis);
//			final double max = aabb.max(axis);
			final double min = aabbMin;
			final double max = aabbMax;
			final double newValue = newAxisRelation.select(
					min,
					max,
					delta * newTime + (axisDirection == AxisDirection.POSITIVE ? 1 : 0)
				);
			final double prevValue = prevAxisRelation.select(
					min,
					max,
					delta * prevTime + (axisDirection == AxisDirection.POSITIVE ? 1 : 0)
				);
			return (newValue - prevValue) * deltaSign;
		}
	}
	
	private static double calculateVolumeSection(
			double minTime, double maxTime,
			Vec3 movementDelta, AABB aabb,
			AxisRelation minXAxisRelation, AxisRelation maxXAxisRelation,
			AxisRelation minYAxisRelation, AxisRelation maxYAxisRelation,
			AxisRelation minZAxisRelation, AxisRelation maxZAxisRelation
		) {
		Objects.requireNonNull(minXAxisRelation);
		Objects.requireNonNull(maxXAxisRelation);
		Objects.requireNonNull(minYAxisRelation);
		Objects.requireNonNull(maxYAxisRelation);
		Objects.requireNonNull(minZAxisRelation);
		Objects.requireNonNull(maxZAxisRelation);
		// xSize = min(1 + xDelta * time, aabb.maxX) - max(0 + xDelta * time, aabb.minX)
		// ySize = min(1 + yDelta * time, aabb.maxY) - max(0 + yDelta * time, aabb.minY)
		// zSize = min(1 + zDelta * time, aabb.maxZ) - max(0 + zDelta * time, aabb.minZ)
		// or, rather:
		// xSize = clamp(1 + xDelta * time, aabb.minX, aabb.maxX) - clamp(0 + xDelta * time, aabb.minX, aabb.maxX)
		// ySize = clamp(1 + yDelta * time, aabb.minY, aabb.maxY) - clamp(0 + yDelta * time, aabb.minY, aabb.maxY)
		// zSize = clamp(1 + zDelta * time, aabb.minZ, aabb.maxZ) - clamp(0 + zDelta * time, aabb.minZ, aabb.maxZ)
		// Surface area = 2 * (xSize * ySize + xSize * zSize + ySize * zSize)
		// We want to integrate surface area with respect to time to get volume
		// Typically, volume = xSize * ySize * zSize

		// The amount of xSize that is multiplied by time
		final double xSizeCoefficient = calculateSizeCoefficient(minXAxisRelation, maxXAxisRelation, movementDelta.x);
		// The amount of xSize that is not multiplied by time
		final double xSizeConstant = calculateSizeConstant(minXAxisRelation, maxXAxisRelation, aabb.minX, aabb.maxX);
		// xSize = (xSizeCoefficient * time + xSizeConstant)
		
		// The amount of ySize that is multiplied by time
		final double ySizeCoefficient = calculateSizeCoefficient(minYAxisRelation, maxYAxisRelation, movementDelta.y);
		// The amount of ySize that is not multiplied by time
		final double ySizeConstant = calculateSizeConstant(minYAxisRelation, maxYAxisRelation, aabb.minY, aabb.maxY);
		// ySize = (ySizeCoefficient * time + ySizeConstant)
		
		// The amount of zSize that is multiplied by time
		final double zSizeCoefficient = calculateSizeCoefficient(minZAxisRelation, maxZAxisRelation, movementDelta.z);
		// The amount of zSize that is not multiplied by time
		final double zSizeConstant = calculateSizeConstant(minZAxisRelation, maxZAxisRelation, aabb.minZ, aabb.maxZ);
		// zSize = (zSizeCoefficient * time + zSizeConstant)
		
		// TODO math
		
		return 0.0;
	}
	
	private static double calculateSizeCoefficient(AxisRelation minAxisRelation, AxisRelation maxAxisRelation, double delta) {
		if (minAxisRelation == AxisRelation.INSIDE && maxAxisRelation != AxisRelation.INSIDE) {
			return -delta;
		} else if (minAxisRelation != AxisRelation.INSIDE && maxAxisRelation == AxisRelation.INSIDE) {
			return delta;
		} else {
			return 0.0;
		}
	}
	
	private static double calculateSizeConstant(AxisRelation minAxisRelation, AxisRelation maxAxisRelation, double min, double max) {
		final double maxConstant = maxAxisRelation.select(
			min, // BELOW_MIN: max axis == min + 0 * time
			max, // ABOVE_MAX: max axis == max + 0 * time
			1.0  // INSIDE: max axis = 1.0 + delta * time
		);
		final double minConstant = minAxisRelation.select(
			min, // BELOW_MIN: min axis == min + 0 * time
			max, // ABOVE_MAX: min axis == max + 0 * time
			0.0  // INSIDE: min axis = 0.0 + delta * time
		);
		return maxConstant - minConstant;
	}
	
	private static enum AxisRelation {
		BELOW_MIN,
		ABOVE_MAX,
		INSIDE;

		public final double select(double min, double max) {
			return switch(this) {
				case BELOW_MIN -> min;
				case ABOVE_MAX -> max;
				case INSIDE -> Double.NaN;
			};
		}

		public final double select(double min, double max, double inside) {
			return switch(this) {
				case BELOW_MIN -> min;
				case ABOVE_MAX -> max;
				case INSIDE -> inside;
			};
		}
		
		public static AxisRelation compute(double value, double min, double max) {
			if (value < min) return BELOW_MIN;
			else if (value > max) return ABOVE_MAX;
			else return INSIDE;
		}
	}
	
	private static record Intersection(double time, Axis axis, AxisIntersection intersection) implements Comparable<Intersection> {
		public static final Intersection NaN = new Intersection(Double.NaN, null, null);
		
		public static Intersection compute(Vec3 movementDelta, AABB aabb, Axis axis, AxisIntersection intersection, AxisFacesIntersects relevantFaces) {
			// If the face is ignored, then return NaN
			switch(intersection) {
				case MIN_VAL_INTERSECTS_ZERO, MIN_VAL_INTERSECTS_ONE -> {
					if(!relevantFaces.negativeFaceIntersects()) return NaN;
				}
				case MAX_VAL_INTERSECTS_ZERO, MAX_VAL_INTERSECTS_ONE -> {
					if(!relevantFaces.positiveFaceIntersects()) return NaN;
				}
			}
			
			final double movement = movementDelta.get(axis);
			final double time = switch(intersection) {
				case MIN_VAL_INTERSECTS_ZERO -> aabb.min(axis) / movement;
				case MIN_VAL_INTERSECTS_ONE -> (aabb.min(axis) - 1) / movement;
				case MAX_VAL_INTERSECTS_ZERO -> aabb.max(axis) / movement;
				case MAX_VAL_INTERSECTS_ONE -> (aabb.max(axis) - 1) / movement;
			};
			return new Intersection(time, axis, intersection);
		}
		
		public boolean isNaN() {
			return this == NaN || Double.isNaN(this.time());
		}
		
		@Override
		public int compareTo(Intersection other) {
			return Double.compare(this.time(), other.time());
		}
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
