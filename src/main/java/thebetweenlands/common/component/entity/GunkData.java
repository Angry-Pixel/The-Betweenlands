package thebetweenlands.common.component.entity;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.datamap.block.WaterPlant;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.DataMapRegistry;
import thebetweenlands.common.registries.FluidTypeRegistry;
import thebetweenlands.util.BoxIntersectionUtil;

public final class GunkData {

	public static final int GUNK_MAX = 1024;
	public static final int ENTER_WAIT_TIME = 20;
	public static final int EXIT_WAIT_TIME = 55;

	public static final Codec<GunkData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.INT.fieldOf("gunk_counter").forGetter(o -> o.gunkCounter),
		Codec.INT.fieldOf("enter_pause_timer").forGetter(o -> o.enterPauseTimer),
		Codec.INT.fieldOf("exit_pause_timer").forGetter(o -> o.exitPauseTimer),
		Codec.FLOAT.fieldOf("partial_gunk").forGetter(o -> o.partialGunk)
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
	 * Adds just the integral (integer) part of partial gunk to full gunk
	 * @param partialGunk
	 * @return true if any 
	 */
	private boolean addPartialGunkIntegralPart(double partialGunk) {
		if((int)partialGunk != 0) {
			this.increaseGunk((int)partialGunk);
			return true;
		}
		return false;
	}

	/**
	 * Adds partial gunk, and increases full gunk whenever partial gunk exceeds 1
	 * @param partialGunk the amount of partial gunk to add
	 * @return true if any full gunk was added
	 */
	public boolean addPartialGunk(double partialGunk) {
		boolean addedGunk = false;
		
		if(this.addPartialGunkIntegralPart(partialGunk)) {
			partialGunk %= 1; // extract fractional part
			addedGunk = true;
		}

		if(this.addPartialGunkIntegralPart(this.partialGunk)) {
			this.partialGunk %= 1; // extract fractional part
			addedGunk = true;
		}
		
		this.partialGunk += partialGunk;

		if(this.addPartialGunkIntegralPart(this.partialGunk)) {
			this.partialGunk %= 1; // extract fractional part
			addedGunk = true;
		}
		
		return addedGunk;
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
		
		// TODO profiler
		
		if(player.isInFluidType(FluidTypeRegistry.SWAMP_WATER.get())) {
			tickInWater(player);
		} else {
			tickOutOfWater(player);
		}
		
		tickMoveThroughWaterPlants(player);
	}
	
	public static void onPlayerTick2(PlayerTickEvent.Pre event) {
		Player player = event.getEntity();
		if(player.level().isClientSide() || !isGunkEnabled(player)) {
			return;
		}
		
//		tickMoveThroughWaterPlants(player);
	}

	public static void tickOutOfWater(Player player) {
		if(!player.hasData(AttachmentRegistry.GUNK)) {
			return;
		}
		
		GunkData gunkData = player.getData(AttachmentRegistry.GUNK);
		
		if(gunkData.exitPauseTimer > 0) {
			gunkData.exitPauseTimer--;
		} else {
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
			boolean gunkChanged = gunkData.increaseGunk(2);

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
//		Vec3 oldPlayerPos = new Vec3(player.xo, player.yo, player.zo);
		Vec3 oldPlayerPos = new Vec3(player.xOld, player.yOld, player.zOld);
		// Where the player was at the end of this tick
		Vec3 playerPos = player.position();
//		Vec3 playerPos = new Vec3(player.getX(), player.getY(), player.getZ());
		
		// How the player moved to get from where they were to where they are
		Vec3 deltaMovement = oldPlayerPos.vectorTo(playerPos);

//		TheBetweenlands.LOGGER.info("Water plant begin; movement magnitude sq = {} (valid {})", deltaMovement.lengthSqr(), deltaMovement.lengthSqr() >= 1.0E-4 * 1.0E-4);
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
		AABB oldBounds = localBounds.move(oldPlayerPos);
		AABB newBounds = localBounds.move(playerPos);
		AABB totalBounds = oldBounds.minmax(newBounds);
//		AABB totalBounds = oldBounds.expandTowards(deltaMovement);
		
		// We want to:
		// 1. Find every gunk plant between the old bound and new bounds
		// 2. Calculate the percentage of each plant's bounding box that was traveled through (and not already intersected with)
		// 3. Use that to determine the total amount of partial gunk to add

		// TODO profiler
		
		Level level = player.level();

		final Vec3 oldPlayerPosNeg = oldPlayerPos.reverse();

		GunkData gunkData = player.getData(AttachmentRegistry.GUNK);
		final int prevGunk = gunkData.getGunk();

		
		// TODO more efficient method of finding blocks (this is slow at higher speeds)
		BlockPos.betweenClosedStream(totalBounds)
			.forEachOrdered(pos -> {
				BlockState state = level.getBlockState(pos);
				WaterPlant waterPlant = state.getBlockHolder().getData(DataMapRegistry.WATER_PLANT);
//				TheBetweenlands.LOGGER.info("Water plant found pos {} {}", pos, waterPlant);
				if(waterPlant == null || waterPlant.movementGunk() == 0.0f || !Float.isFinite(waterPlant.movementGunk())) {
					return;
				}
//				TheBetweenlands.LOGGER.info("Water plant found plant {} at pos {} with value {}", state, pos, waterPlant.movementGunk());
				VoxelShape shape = state.getShape(level, pos, CollisionContext.of(player)).move(pos.getX(), pos.getY(), pos.getZ());
				List<AABB> aabbs = shape.toAabbs();
				// Total volume that the player has passed through
				double intersectionVolume = 0.0d;
				// Total volume of the block
				double totalVolume = 0.0d;
				for(AABB aabb : aabbs) {
//					TheBetweenlands.LOGGER.info("Water plant {} at pos {} has an aabb of {}", state, pos, aabb);
					final double volume = aabb.getXsize() * aabb.getYsize() * aabb.getZsize();
					// TODO volume for the center
					final double volumeIntersected = BoxIntersectionUtil.findPercentIntersectionVolume(localBounds, deltaMovement, aabb.move(oldPlayerPosNeg), false) * volume;
					
					intersectionVolume += volumeIntersected;
					totalVolume += volume;
				}
				
				double percentIntersection = intersectionVolume / totalVolume;
				
//				TheBetweenlands.LOGGER.info("Intersection with {} for {} vol and {} amount gives a total of {} partial gunk", state, percentIntersection, waterPlant.movementGunk(), percentIntersection * waterPlant.movementGunk());
				gunkData.addPartialGunk(percentIntersection * waterPlant.movementGunk());
			});

		final int newGunk = gunkData.getGunk();
		if(prevGunk != newGunk) {
			player.syncData(AttachmentRegistry.GUNK);
		}
	}
}
