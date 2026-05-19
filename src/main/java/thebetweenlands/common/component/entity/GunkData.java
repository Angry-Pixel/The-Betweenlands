package thebetweenlands.common.component.entity;

import java.util.Arrays;
import java.util.Objects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Direction.Axis;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.FluidTypeRegistry;

public final class GunkData {

	public static final int GUNK_MAX = 100;
	public static final int ENTER_WAIT_TIME = 20;
	public static final int EXIT_WAIT_TIME = 35;

	public static final Codec<GunkData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("gunk_counter").forGetter(o -> o.gunkCounter),
		Codec.INT.fieldOf("enter_pause_timer").forGetter(o -> o.enterPauseTimer),
		Codec.INT.fieldOf("exit_pause_timer").forGetter(o -> o.exitPauseTimer),
		ExtraCodecs.POSITIVE_FLOAT.fieldOf("partial_gunk").forGetter(o -> o.partialGunk)
	).apply(instance, GunkData::new));

	public static final StreamCodec<FriendlyByteBuf, GunkData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, o -> o.gunkCounter, // We only really need to sync gunkCounter...
		ByteBufCodecs.INT, o -> o.enterPauseTimer,
		ByteBufCodecs.INT, o -> o.exitPauseTimer,
		ByteBufCodecs.FLOAT, o -> o.partialGunk,
		GunkData::new
	);

	// 0-100, counts how much water gunk is on the player
	private int gunkCounter;
	// 0-20, tracks the amount of time the player can be in swamp water (not necessarily in the swim pose) before they will start to gain gunk from swimming (only in the swim pose)
	// The player will still gain gunk from swimming through plants (in the swim pose) even if this has not reached 0 yet
	// Once the player exits, increases by 1 each tick until it reaches its maximum of 20
	private int enterPauseTimer;
	// 0-35, tracks the amount of time before any out-of-water behaviours (e.g. gunk slowly decreasing) begin
	// If the player enters water, immediately set to its maximum of 35
	private int exitPauseTimer;
	// Partial gunk from moving through plants
	private float partialGunk;
	
	public GunkData() {
		this(0, ENTER_WAIT_TIME, 0, 0.0f);
	}
	
	public GunkData(int gunkCounter, int enterPauseTimer, int exitPauseTimer, float partialGunk) {
		this.gunkCounter = gunkCounter;
		this.enterPauseTimer = enterPauseTimer;
		this.exitPauseTimer = exitPauseTimer;
		this.partialGunk = partialGunk;
	}
	
	public int getGunk() {
		return this.gunkCounter;
	}
	
	public void setGunk(int gunk) {
		this.gunkCounter = Mth.clamp(gunk, 0, GUNK_MAX);
	}
	
	public boolean increaseGunk(int amount) {
		final int prevGunk = this.gunkCounter;
		final int newGunk = this.gunkCounter = Math.clamp(this.gunkCounter + amount, 0, GUNK_MAX);
		return prevGunk != newGunk;
	}
	
	/**
	 * @return {@code true} if the player is preventing from swimming (via swim pose) in Swamp Water
	 */
	public boolean isSwimmingBlocked() {
		return this.gunkCounter >= GUNK_MAX;
	}

	/**
	 * Returns whether the gunk systems should even be engaged for the target player.
	 * 
	 * <p>If {@code false}, then everything gunk-related is blanket disabled for this player</p>
	 * @param player the player to check
	 * @return {@code true} if gunk should affect this player, or {@code false} if everything gunk-related should be disabled for this player
	 */
	public static boolean isGunkEnabled(Player player) {
		return player.level().getDifficulty() != Difficulty.PEACEFUL &&
			player.level().getGameRules().getBoolean(TheBetweenlands.GUNK_GAMERULE) && BetweenlandsConfig.useGunk &&
			!player.isCreative() && !player.getAbilities().invulnerable;
	}
	
	/**
	 * Returns whether the player is in a state where gunk is "active".
	 * @param player the player to check
	 * @return
	 */
	public static boolean isGunkActive(Player player) {
		return player.isInFluidType(FluidTypeRegistry.SWAMP_WATER.get());
	}
	
	/**
	 * Returns whether this player can gain gunk
	 * @param player
	 * @return {@code false} if the player's gunk is prevented from increasing
	 */
	public static boolean canGunkIncrease(Player player) {
		// TODO lurker skin armour
		// TODO amphibious armour
		return true;
	}
	
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		if(player.level().isClientSide() || !isGunkEnabled(player)) {
			return;
		}
		
		if(player.isInFluidType(FluidTypeRegistry.SWAMP_WATER.get())) {
			tickInWater(player);
		} else {
			tickOutOfWater(player);
		}
		
		tickMoveThroughWaterPlants(player);
	}

	public static void tickOutOfWater(Player player) {
		if(!player.hasData(AttachmentRegistry.GUNK)) {
			return;
		}
		
		GunkData gunkData = player.getData(AttachmentRegistry.GUNK);
		
		if(gunkData.exitPauseTimer > 0) {
			gunkData.exitPauseTimer--;
		} else {
			// TODO adjust gunk rate
			boolean gunkChanged = gunkData.increaseGunk(-1);

			// Sync gunk data if it changed
			if(gunkChanged) {
				player.syncData(AttachmentRegistry.GUNK);
			}
		}
		
		if(gunkData.enterPauseTimer < ENTER_WAIT_TIME) {
			gunkData.enterPauseTimer++;
		}
	}

	public static void tickInWater(Player player) {
		GunkData gunkData = player.getData(AttachmentRegistry.GUNK);

		gunkData.exitPauseTimer = EXIT_WAIT_TIME;

		if(gunkData.enterPauseTimer > 0) {
			gunkData.enterPauseTimer--;
		} else if(player.isSwimming() && canGunkIncrease(player)) {
			// TODO adjust gunk rate
			boolean gunkChanged = gunkData.increaseGunk(1);

			// Sync gunk data if it changed
			if(gunkChanged) {
				player.syncData(AttachmentRegistry.GUNK);
			}
		}
	}
	
	public static void tickMoveThroughWaterPlants(Player player) {
		// Updates gunk for moving through algae and certain water plants
		// Hmm, best way to get position?
		// Either player.getPosition(0.0f) and player.getPosition(1.0f) for old and new position respectively
		// or new Vec3(player.xo, player.yo, player.zo) and player.position() for old and new position respectively
		
		// Entity.getPosition(partialTicks) seems to mostly be client-side
		
		// Where the player was at the start of this tick
		Vec3 oldPos = new Vec3(player.xo, player.yo, player.zo);
		// Where the player was at the end of this tick
		Vec3 pos = player.position();
		
		// How the player moved to get from where they were to where they are
		Vec3 deltaMovement = oldPos.vectorTo(pos);
		
		// If the player hasn't significantly moved, do nothing
		if (deltaMovement.lengthSqr() < 1.0E-4 * 1.0E-4) return;
		
		final Pose forcedPose = player.getForcedPose();
		Pose pose = forcedPose != null ? forcedPose : player.getPose();
		
		// local bounding box (centred on 0, 0) for the player in this pose
		// 0, 0, 0 is the bottom centre of this AABB
		AABB localBounds = player.getLocalBoundsForPose(pose);
		
		// TODO figure out how much of the player moved through gunk water plants and add the corresponding amount of gunk
		// Note: should be inversely related to the player's scale (or dimensions of their bounding box)
		//       that is, someone 10x the size should gain less gunk for the same movement through a single block because they are bigger
		
		// Currently, we assume the bounding box size has not changed since the start of this tick
		AABB oldBounds = localBounds.move(oldPos);
		AABB newBounds = localBounds.move(pos);
		AABB totalBounds = oldBounds.minmax(newBounds);
		
		// We want to:
		// 1. Find every gunk plant between the old bound and new bounds
		// 2. Calculate the percentage of each plant's bounding box that was traveled through (and not already intersected with)
		// 3. Use that to determine the total amount of partial gunk to add
	}
	
	// TODO probably could go into a different file
	// TODO ngl could probably process this one axis at a time (wouldn't be 100% accurate though, because we don't know the exact motion the player moved through)
	
	/**
	 * Finds the percentage of the volume of {@code aabb} that is passed through by {@code localBounds} as it is offset from its starting position by {@code movementDelta}
	 * @param localBounds the local bounds, representing the player's bounding box
	 * @param movementDelta the offset that the {@code localBounds} goes through
	 * @param aabb the aabb that is passed through by {@code localBounds}
	 * @return the percentage of the volume of {@code aabb} that is passed through by {@code localBounds} as it is offset from its starting position by {@code movementDelta}
	 */
	public static double findIntersectionVolume(AABB localBounds, Vec3 movementDelta, AABB aabb) {
		// Scale universe such that localBounds is [(0, 0, 0), (1, 1, 1)]
		Vec3 offsetPos = localBounds.getMinPosition().reverse();
		AABB offsetLocalBounds = localBounds.move(offsetPos);
		Vec3 scaleFactor = new Vec3(1.0 / offsetLocalBounds.maxX, 1.0 / offsetLocalBounds.maxY, 1.0 / offsetLocalBounds.maxZ);
		
		AABB offsetAabb = aabb.move(offsetPos);
		AABB targetAabb = new AABB(offsetAabb.getMinPosition().multiply(scaleFactor), offsetAabb.getMaxPosition().multiply(scaleFactor));
		
		if(targetAabb.getXsize() * targetAabb.getYsize() * targetAabb.getZsize() < 1E-4 * 1E-4 * 1E-4) {
			return 0;
		}

		Vec3 scaledMovementDelta = movementDelta.multiply(scaleFactor);
		
		return findIntersectionVolumeWithCenteredLocalBounds(scaledMovementDelta, targetAabb);
	}

	/**
	 * Finds the percentage of the volume of {@code aabb} that is passed through by the unit aabb, as the unit aabb moves from 0, 0, 0 to {@code movementDelta}
	 * @param movementDelta the offset that the unit aabb goes through
	 * @param aabb the aabb that is passed through by the unit aabb
	 * @return the percentage of the volume of {@code aabb} that is passed through by the unit aabb, as the unit aabb moves from 0, 0, 0 to {@code movementDelta}
	 */
	public static double findIntersectionVolumeWithCenteredLocalBounds(Vec3 movementDelta, AABB aabb) {
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
		
		return calculateVolumeFromIntersections(movementDelta, aabb, sortedIntersections);
	}
	
	// TODO should make a lot of this stuff private and probably also move it to a different class
	
	/**
	 * In this method we "move" the unit aabb from (0, 0, 0) to movementDelta.
	 * We accept a sorted array of {@link Intersection Intersections} that we can iterate through in ascending time order.
	 * We use this to keep track of where the unit aabb is relative to the target aabb and use that to construct our formulas.
	 * Far from perfect, but it runs in fixed time 
	 * @param movementDelta
	 * @param aabb
	 * @param sortedIntersections an array of length {@code 16} containing non-null {@link Intersection Intersections}, sorted in ascending {@link Intersection#time() time} order
	 * @return
	 */
	public static double calculateVolumeFromIntersections(Vec3 movementDelta, AABB aabb, Intersection[] sortedIntersections) {
		// Compute axis relations at time 0
		// The "Min X", "Max X", etc. being referred to is the Min X of the *unit aabb*, not of the target aabb
		AxisType minXAxisRelation = AxisType.compute(0, aabb.minX, aabb.maxX);
		AxisType maxXAxisRelation = AxisType.compute(1, aabb.minX, aabb.maxX);
		AxisType minYAxisRelation = AxisType.compute(0, aabb.minY, aabb.maxY);
		AxisType maxYAxisRelation = AxisType.compute(1, aabb.minY, aabb.maxY);
		AxisType minZAxisRelation = AxisType.compute(0, aabb.minZ, aabb.maxZ);
		AxisType maxZAxisRelation = AxisType.compute(1, aabb.minZ, aabb.maxZ);
		
		double volume = 0.0;
		double c = 0.0; // Kahan summation algorithm
		
		double previousTime = 0.0;
		for (Intersection intersection : sortedIntersections) {
			if (intersection.isNaN()) continue;
			final double intersectionTime = intersection.time();
			if (!Double.isFinite(intersectionTime) || intersectionTime < 0 || intersectionTime < previousTime) continue;
			
			final double time = intersection.time();
			
			// TODO evaluate formula with bounds [previousTime, time]
			
			final Axis axis = intersection.axis();
			final AxisIntersection intersectionFace = intersection.intersection();
			
			// The rate of change of the unit aabb
			final double value = movementDelta.get(axis);
			
			// Calculate the new axis type
			final AxisType newAxisType;
			switch(intersectionFace) {
				case MIN_VAL_INTERSECTS_ZERO, MIN_VAL_INTERSECTS_ONE -> {
					if (value < 0) {
						newAxisType = AxisType.BELOW_MIN;
					} else {
						newAxisType = AxisType.INSIDE;
					}
				}
				case MAX_VAL_INTERSECTS_ZERO, MAX_VAL_INTERSECTS_ONE -> {
					if (value > 0) {
						newAxisType = AxisType.ABOVE_MAX;
					} else {
						newAxisType = AxisType.INSIDE;
					}
				}
				default -> throw new IllegalStateException();
			}
			
			// Should we set the min axis relation or max axis relation? (unit zero is min axis relations, unit one is max axis relations)
			final boolean isZero = intersectionFace == AxisIntersection.MIN_VAL_INTERSECTS_ZERO || intersectionFace == AxisIntersection.MAX_VAL_INTERSECTS_ZERO;
			
			// Set the relevant axis type variable
			switch(axis) {
				case X -> {
					if (isZero) {
						minXAxisRelation = newAxisType;
					} else {
						maxXAxisRelation = newAxisType;
					}
				}
				case Y -> {
					if (isZero) {
						minYAxisRelation = newAxisType;
					} else {
						maxYAxisRelation = newAxisType;
					}
				}
				case Z -> {
					if (isZero) {
						minZAxisRelation = newAxisType;
					} else {
						maxZAxisRelation = newAxisType;
					}
				}
				default -> throw new IllegalStateException();
			}
			
			previousTime = time;
		}
		
		return volume;
	}
	
	public static double calculateVolumeSection(
			double minTime, double maxTime,
			Vec3 movementDelta, AABB aabb,
			AxisType minXAxisRelation, AxisType maxXAxisRelation,
			AxisType minYAxisRelation, AxisType maxYAxisRelation,
			AxisType minZAxisRelation, AxisType maxZAxisRelation
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
		final double xSizeCoefficient = calculateCoefficient(minXAxisRelation, maxXAxisRelation, movementDelta.x);
		// The amount of xSize that is not multiplied by time
		final double xSizeConstant = calculateConstant(minXAxisRelation, maxXAxisRelation, aabb.minX, aabb.maxX);
		// xSize = (xSizeCoefficient * time + xSizeConstant)
		
		// The amount of ySize that is multiplied by time
		final double ySizeCoefficient = calculateCoefficient(minYAxisRelation, maxYAxisRelation, movementDelta.y);
		// The amount of ySize that is not multiplied by time
		final double ySizeConstant = calculateConstant(minYAxisRelation, maxYAxisRelation, aabb.minY, aabb.maxY);
		// ySize = (ySizeCoefficient * time + ySizeConstant)
		
		// The amount of zSize that is multiplied by time
		final double zSizeCoefficient = calculateCoefficient(minZAxisRelation, maxZAxisRelation, movementDelta.z);
		// The amount of zSize that is not multiplied by time
		final double zSizeConstant = calculateConstant(minZAxisRelation, maxZAxisRelation, aabb.minZ, aabb.maxZ);
		// zSize = (zSizeCoefficient * time + zSizeConstant)
		
		// TODO math
		
		return 0.0;
	}
	
	private static double calculateCoefficient(AxisType minAxisRelation, AxisType maxAxisRelation, double delta) {
		if (minAxisRelation == AxisType.INSIDE && maxAxisRelation != AxisType.INSIDE) {
			return -delta;
		} else if (minAxisRelation != AxisType.INSIDE && maxAxisRelation == AxisType.INSIDE) {
			return delta;
		} else {
			return 0.0;
		}
	}
	
	private static double calculateConstant(AxisType minAxisRelation, AxisType maxAxisRelation, double max, double min) {
		final double maxConstant = maxAxisRelation.select(
			min, // BELOW_MIN: max axis == min + 0 * time
			max, // ABOVE_MAX: max axis == max + 0 * time
			1    // INSIDE: max axis = 1 + delta * time
		);
		final double minConstant = minAxisRelation.select(
			min, // BELOW_MIN: min axis == min + 0 * time
			max, // ABOVE_MAX: min axis == max + 0 * time
			0    // INSIDE: min axis = 0 + delta * time
		);
		return maxConstant - minConstant;
	}
	
	public static enum AxisType {
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
		
		public static AxisType compute(double value, double min, double max) {
			if (value < min) return BELOW_MIN;
			else if (value > max) return ABOVE_MAX;
			else return INSIDE;
		}
	}
	
	public static record Intersection(double time, Axis axis, AxisIntersection intersection) implements Comparable<Intersection> {
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

		if (aabb.maxY < 0 && aabb.maxY < movementDelta.y) {
			return false;
		}

		if (aabb.maxZ < 0 && aabb.maxZ < movementDelta.z) {
			return false;
		}

		if (aabb.maxY > 1 && aabb.maxY > movementDelta.y + 1) {
			return false;
		}

		if (aabb.maxZ > 1 && aabb.maxZ > movementDelta.z + 1) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Represents the type of intersection along an axis.
	 */
	public static enum AxisIntersection {
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
		
		public boolean isMin() {
			return this == MIN_VAL_INTERSECTS_ZERO || this == MIN_VAL_INTERSECTS_ONE;
		}
		
		public boolean isMax() {
			return this == MAX_VAL_INTERSECTS_ZERO || this == MAX_VAL_INTERSECTS_ONE;
		}
	}
}
