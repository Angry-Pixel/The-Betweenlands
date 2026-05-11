package thebetweenlands.common.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

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
		
		return findIntersectionVolumeWithCenteredLocalBounds(movementDelta.multiply(scaleFactor), targetAabb);
	}

	/**
	 * Finds the percentage of the volume of {@code aabb} that is passed through by the unit aabb, as the unit aabb moves from 0, 0, 0 to {@code movementDelta}
	 * @param movementDelta the offset that the unit aabb goes through
	 * @param aabb the aabb that is passed through by the unit aabb
	 * @return the percentage of the volume of {@code aabb} that is passed through by the unit aabb, as the unit aabb moves from 0, 0, 0 to {@code movementDelta}
	 */
	public static double findIntersectionVolumeWithCenteredLocalBounds(Vec3 movementDelta, AABB aabb) {
		// Local bounds is AABB at [(0, 0, 0), (1, 1, 1)]

		final double minXIntersectsZero = aabb.minX / movementDelta.x;
		final double minXIntersectsOne  = (aabb.minX - 1) / movementDelta.x;
		final double maxXIntersectsZero = aabb.maxX / movementDelta.x;
		final double maxXIntersectsOne  = (aabb.maxX - 1) / movementDelta.x;

		final AxisIntersection firstXIntersection = getFirstIntersection(minXIntersectsZero, minXIntersectsOne, maxXIntersectsZero, maxXIntersectsOne);
		final AxisIntersection lastXIntersection = getLastIntersection(minXIntersectsZero, minXIntersectsOne, maxXIntersectsZero, maxXIntersectsOne);

		final double minYIntersectsZero = aabb.minY / movementDelta.y;
		final double minYIntersectsOne  = (aabb.minY - 1) / movementDelta.y;
		final double maxYIntersectsZero = aabb.maxY / movementDelta.y;
		final double maxYIntersectsOne  = (aabb.maxY - 1) / movementDelta.y;

		final AxisIntersection firstYIntersection = getFirstIntersection(minYIntersectsZero, minYIntersectsOne, maxYIntersectsZero, maxYIntersectsOne);
		final AxisIntersection lastYIntersection = getLastIntersection(minYIntersectsZero, minYIntersectsOne, maxYIntersectsZero, maxYIntersectsOne);

		final double minZIntersectsZero = aabb.minZ / movementDelta.z;
		final double minZIntersectsOne  = (aabb.minZ - 1) / movementDelta.z;
		final double maxZIntersectsZero = aabb.maxZ / movementDelta.z;
		final double maxZIntersectsOne  = (aabb.maxZ - 1) / movementDelta.z;

		final AxisIntersection firstZIntersection = getFirstIntersection(minZIntersectsZero, minZIntersectsOne, maxZIntersectsZero, maxZIntersectsOne);
		final AxisIntersection lastZIntersection = getLastIntersection(minZIntersectsZero, minZIntersectsOne, maxZIntersectsZero, maxZIntersectsOne);

		return 0.0;
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
	}

	/**
	 * Gets the {@link AxisIntersection axis intersection} that represents the double with the lowest value
	 * @param minIntersectsZero
	 * @param minIntersectsOne
	 * @param maxIntersectsZero
	 * @param maxIntersectsOne
	 * @return
	 */
	public static AxisIntersection getFirstIntersection(double minIntersectsZero, double minIntersectsOne, double maxIntersectsZero, double maxIntersectsOne) {
		AxisIntersection intersection = AxisIntersection.MIN_VAL_INTERSECTS_ZERO;
		double minValue = minIntersectsZero;
		
		if(minIntersectsOne < minValue) {
			minValue = minIntersectsOne;
			intersection = AxisIntersection.MIN_VAL_INTERSECTS_ONE;
		}
		
		if(maxIntersectsZero < minValue) {
			minValue = maxIntersectsZero;
			intersection = AxisIntersection.MAX_VAL_INTERSECTS_ZERO;
		}
		
		if(maxIntersectsOne < minValue) {
			minValue = maxIntersectsOne;
			intersection = AxisIntersection.MAX_VAL_INTERSECTS_ONE;
		}
		
		return intersection;
	}
	
	/**
	 * Gets the {@link AxisIntersection axis intersection} that represents the double with the highest value
	 * @param minIntersectsZero
	 * @param minIntersectsOne
	 * @param maxIntersectsZero
	 * @param maxIntersectsOne
	 * @return
	 */
	public static AxisIntersection getLastIntersection(double minIntersectsZero, double minIntersectsOne, double maxIntersectsZero, double maxIntersectsOne) {
		AxisIntersection intersection = AxisIntersection.MIN_VAL_INTERSECTS_ZERO;
		double maxValue = minIntersectsZero;
		
		if(minIntersectsOne > maxValue) {
			maxValue = minIntersectsOne;
			intersection = AxisIntersection.MIN_VAL_INTERSECTS_ONE;
		}
		
		if(maxIntersectsZero > maxValue) {
			maxValue = maxIntersectsZero;
			intersection = AxisIntersection.MAX_VAL_INTERSECTS_ZERO;
		}
		
		if(maxIntersectsOne > maxValue) {
			maxValue = maxIntersectsOne;
			intersection = AxisIntersection.MAX_VAL_INTERSECTS_ONE;
		}
		
		return intersection;
	}
}
