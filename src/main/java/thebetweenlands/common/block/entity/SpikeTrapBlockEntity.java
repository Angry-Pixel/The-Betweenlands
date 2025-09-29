package thebetweenlands.common.block.entity;

import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.structure.SpikeTrapBlock;
import thebetweenlands.common.datagen.tags.BLBlockTagProvider;
import thebetweenlands.common.datagen.tags.BLEntityTagProvider;
import thebetweenlands.common.datagen.tags.BLFluidTagGenerator;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.location.LocationStorage;
import thebetweenlands.compat.flan.BetweenlandsFlanCompat;

public class SpikeTrapBlockEntity extends SyncedBlockEntity {

	public static final Predicate<Entity> SPIKE_TRAP_CAN_HURT =
			EntitySelector.LIVING_ENTITY_STILL_ALIVE
			.and(EntitySelector.NO_CREATIVE_OR_SPECTATOR)
			.and(entity -> !entity.getType().is(BLEntityTagProvider.SPIKE_TRAP_IMMUNE));

	public static enum BreakBlockResult {
		IGNORE,
		BLOCK,
		BREAK;

		public static BreakBlockResult or(BreakBlockResult a, BreakBlockResult b) {
			return switch (a) {
				case IGNORE -> b;
				case BLOCK -> BLOCK;
				case BREAK -> b == BLOCK ? BLOCK : BREAK;
			};
		}
	}

	// Damage cooldown makes sure damage is always dealt in no shorter than 25 tick intervals (5 tick extension, 20 tick retraction)
	// Without the cooldown, placing a block every tick for the spikes to destroy would make it deal damage each tick (undesirable behaviour)
	// Also yes I checked that the extension & retraction is 25 ticks long in the 1.12.2 release as well
	public int damageCooldown = 0;

	public int delayedTriggerTicks = 0;

	public int prevExtendingTicks;
	public int extendingTicks;
	public boolean extending;
	public final boolean canSpook;

	public int prevSpoopAnimationTicks;
	public int spoopAnimationTicks;
	public boolean activeSpoop;

	public SpikeTrapBlockEntity(BlockPos pos, BlockState state) {
		this(pos, state, false);
	}

	public SpikeTrapBlockEntity(BlockPos pos, BlockState state, boolean canSpook) {
		super(BlockEntityRegistry.SPIKE_TRAP.get(), pos, state);
		this.canSpook = canSpook;
	}

	public static boolean canBeTargeted(Entity entity) {
		// Need to move BLEntity to a tag
		return  entity != null &&
				SPIKE_TRAP_CAN_HURT.test(entity) &&
				!(entity instanceof BLEntity);
	}

	public static boolean canTriggerTrap(Level level, BlockPos pos, BlockState state, Entity entity, int delayedTriggerTicks) {
		if(entity == null || !canBeTargeted(entity)) {
			return false;
		}

		if(entity instanceof ServerPlayer && BetweenlandsFlanCompat.INSTANCE.isModLoaded()) {
			boolean canTrigger = BetweenlandsFlanCompat.getEntityPermission(level, pos, entity, TheBetweenlands.prefix("trigger_spike_trap")).orElse(true);
			if(!canTrigger) {
				return false;
			}
		}

		if(entity.isInvisible()) {
			AABB aabb = new AABB(pos).inflate(0.0625);
			if(!aabb.intersects(entity.getBoundingBox())) {
				return false;
			}
		}

		if(shouldHaveDelayedTrigger(level, pos, state, entity) && delayedTriggerTicks < getDelayedTriggerTicks(level, pos, state, entity)) {
			return false;
		}

		return true;
	}

	public static int getDelayedTriggerTicks(Level level, BlockPos pos, BlockState state, Entity entity) {
		if(state.getValue(SpikeTrapBlock.FACING) == Direction.UP && entity instanceof LivingEntity livingEntity && ElixirEffectRegistry.EFFECT_LIGHTWEIGHT.get().isActive(livingEntity)) {
			return 20;
		}

		return -1;
	}

	public static boolean shouldHaveDelayedTrigger(Level level, BlockPos pos, BlockState state, Entity entity) {
		return getDelayedTriggerTicks(level, pos, state, entity) != -1;
	}

	public static void tick(Level level, BlockPos pos, BlockState state, SpikeTrapBlockEntity entity) {
		if (!level.isClientSide() && !level.isDebug()) {

			entity.destroyBlocksInFront(level, pos, state);

			if (level.getRandom().nextInt(500) == 0) {
				if (entity.isActive(state) && !entity.isExtending() && entity.extendingTicks == 0)
					entity.setActive(level, pos, state, false);
				else if (entity.shouldTrapTrigger(level, pos, state) == null)
					entity.setActive(level, pos, state, true);
			}

			if (entity.shouldTrapTrigger(level, pos, state) != null && entity.isActive(state))
				if (!entity.isExtending() && entity.extendingTicks == 0)
					entity.setExtending(level, pos, state, true);

		}

		entity.prevExtendingTicks = entity.extendingTicks;
		if (entity.isExtending()) {
			entity.activateBlock(level, pos, state);
			if (entity.extendingTicks == 0)
				level.playSound(null, pos, SoundRegistry.SPIKE.get(), SoundSource.BLOCKS, 1.25F, 1.0F);
			if (entity.extendingTicks < 20)
				entity.extendingTicks += 4;
			if(entity.extendingTicks >= 20) {
				entity.extendingTicks = 20;
				if(!level.isClientSide) {
					entity.setExtending(level, pos, state, false);
				}
			}
		} else {
			if (entity.extendingTicks >= 1) {
				entity.extendingTicks--;
			}
		}

		if (entity.canSpook) {
			entity.prevSpoopAnimationTicks = entity.spoopAnimationTicks;
			if (!entity.activeSpoop && level.getRandom().nextInt(11) + level.getGameTime() % 10 == 0 && entity.spoopAnimationTicks == 0)
				entity.setActiveSpoop(level, pos, state, true);
			if (entity.activeSpoop) {
				if (entity.spoopAnimationTicks < 20)
					entity.spoopAnimationTicks += 1;
				if (entity.spoopAnimationTicks == 20)
					entity.setActiveSpoop(level, pos, state, false);
			}
			if (!entity.activeSpoop)
				if (entity.spoopAnimationTicks >= 1)
					entity.spoopAnimationTicks--;
		}

		if(entity.damageCooldown > 0) {
			--entity.damageCooldown;
		}

		entity.updateSteppedOn(level, pos, state);
	}

	public void updateSteppedOn(Level level, BlockPos pos, BlockState state) {
//		BlockPos hitArea = getHitArea(level, pos, state);
//		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(hitArea).deflate(0.25D), entity -> shouldHaveDelayedTrigger(level, pos, state, entity));
		boolean occupied = this.isBlockOccupied(level, pos, state) != null;
		if(occupied) {
			if(this.delayedTriggerTicks < Integer.MAX_VALUE)
				this.delayedTriggerTicks++;
		} else if(this.delayedTriggerTicks > 20) {
			this.delayedTriggerTicks = 20;
		} else if(this.delayedTriggerTicks > 0) {
			this.delayedTriggerTicks--;
		}
	}

	public BlockPos destroyBlocksInFront(Level level, BlockPos pos, BlockState state) {
		return destroyBlocksInFront(level, pos, state, 2);
	}

	/**
	 * Attempt to destroy n blocks in front of the spike trap, breaking early if it is blocked
	 * @param level the level the spike trap is in
	 * @param pos	the position of the spike trap
	 * @param state	the state of the spike trap
	 * @param totalDistance	the number of blocks to attempt to break on the spike's front face
	 * @return the position of the block that blocked the spike trap, or the position {@code totalDistance} away from the spike trap's face if it wasn't blocked
	 */
	public BlockPos destroyBlocksInFront(Level level, BlockPos pos, BlockState state, int totalDistance) {
		Direction facing = state.getValue(SpikeTrapBlock.FACING);

		MutableBlockPos targetPos = pos.mutable();

		for(int i = 0; i < totalDistance; ++i) {
			targetPos.move(facing, 1);
			BreakBlockResult result = attemptDestroyBlock(level, pos, state, targetPos);
			if(result == BreakBlockResult.BLOCK) {
				return targetPos.immutable();
			}
		}

		return targetPos.immutable();
	}

	public boolean isBlockHardProtected(Level level, BlockPos trapPos, BlockPos targetPos) {
		if(LocationStorage.isLocationGuarded(level, null, targetPos)) {
			return true;
		}

		// *DO NOT* use BLClaimCompatHelper.restrictBlockBreak because that won't account for them both being in the same claim
		if(BetweenlandsFlanCompat.INSTANCE.isModLoaded()) {
			if(!BetweenlandsFlanCompat.areSameClaim(level, trapPos, targetPos)) {
				if(BetweenlandsFlanCompat.restrictBlockBreak(level, targetPos, null)) {
					return true;
				}

				boolean couldPistonPush = BetweenlandsFlanCompat.getEntityPermission(level, targetPos, null, BetweenlandsFlanCompat.prefix("piston_border")).orElse(true);

				if(!couldPistonPush) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Should this spike trap attempt to break the target block at the target position?
	 * @param level	the level the target block is in
	 * @param trapPos	the position of the trap block
	 * @param targetPos	the position of the target block
	 * @param targetState the state of the target block
	 * @return how to react to this block
	 */
	@SuppressWarnings("deprecation")
	public BreakBlockResult shouldAttemptDestroyBlock(Level level, BlockPos trapPos, BlockPos targetPos, BlockState targetState) {
		if(isBlockHardProtected(level, trapPos, targetPos)) {
			return BreakBlockResult.BLOCK;
		}

		// WARNING: Don't change up the order of these if statements, as they're currently set up to maximize compatibility

		// If tagged to ignore -> ignore
		if(targetState.is(BLBlockTagProvider.SPIKE_TRAPS_IGNORE)) {
			return BreakBlockResult.IGNORE;
		// If tagged to block -> block
		} else if(targetState.is(BLBlockTagProvider.SPIKE_TRAPS_BLOCKED_BY)) {
			return BreakBlockResult.BLOCK;
		// If air -> ignore
		} else if(targetState.isAir()) {
			return BreakBlockResult.IGNORE;
		// If unbreakable -> block (air can be unbreakable)
		} else if(targetState.getDestroySpeed(level, targetPos) < 0.0F) {
			return BreakBlockResult.BLOCK;
		// If liquid -> ignore (fluid is handled separately)
		} else if(!targetState.getFluidState().isEmpty() && (targetState.liquid() || targetState.getCollisionShape(level, targetPos).isEmpty() || targetState.getFluidState().createLegacyBlock().equals(targetState))) {
			return BreakBlockResult.IGNORE;
		// If none of the above -> try your best
		} else {
			return BreakBlockResult.BREAK;
		}
	}

	/**
	 * Should this spike trap attempt to break the target block at the target position based on the fluid state of the block?
	 * @param level	the level the target block is in
	 * @param targetPos	the position of the target block
	 * @param targetState the state of the target block
	 * @return how to react to this block
	 */
	public BreakBlockResult shouldAttemptDestroyFluid(Level level, BlockPos trapPos, BlockPos targetPos, BlockState targetState) {
		if(isBlockHardProtected(level, trapPos, targetPos)) {
			return BreakBlockResult.BLOCK;
		}

		FluidState fluidState = targetState.getFluidState();
		if(fluidState.isEmpty()) {
			return BreakBlockResult.IGNORE;
		} else if(fluidState.is(BLFluidTagGenerator.SPIKE_TRAPS_BLOCKED_BY)) {
			return BreakBlockResult.BLOCK;
		} else if(fluidState.is(BLFluidTagGenerator.SPIKE_TRAPS_BREAK)) {
			return BreakBlockResult.BREAK;
		} else {
			return BreakBlockResult.IGNORE;
		}
	}

	/**
	 * Attempt to destroy the block at the target position with the spike trap
	 * @param level	the level the spike trap is in
	 * @param spikeTrapPos	the position of the spike trap
	 * @param spikeTrapState	the state of the spike trap
	 * @param targetPos	the position of the block to attempt destroying
	 * @return false if it tried and failed to break a block, true otherwise
	 */
	public BreakBlockResult attemptDestroyBlock(Level level, BlockPos spikeTrapPos, BlockState spikeTrapState, BlockPos targetPos) {
		BlockState targetState = level.getBlockState(targetPos);

		BreakBlockResult shouldAttemptBreakBlock = shouldAttemptDestroyBlock(level, spikeTrapPos, targetPos, targetState);
		BreakBlockResult shouldAttemptBreakBlockBasedOnFluid = shouldAttemptDestroyFluid(level, spikeTrapPos, targetPos, targetState);
		BreakBlockResult shouldAttempt = BreakBlockResult.or(shouldAttemptBreakBlock, shouldAttemptBreakBlockBasedOnFluid);

		if(shouldAttempt != BreakBlockResult.BREAK) {
			return shouldAttempt;
		} else {
			boolean couldBreak = level.destroyBlock(targetPos, true);
			if(!couldBreak) {
				BlockState currentState = level.getBlockState(targetPos);
				if(shouldAttemptBreakBlockBasedOnFluid == BreakBlockResult.BREAK && targetState.getFluidState().createLegacyBlock().equals(currentState)) {
					couldBreak = level.setBlock(targetPos, Fluids.EMPTY.defaultFluidState().createLegacyBlock(), Block.UPDATE_ALL);
					if(couldBreak) {
						level.gameEvent(GameEvent.BLOCK_DESTROY, targetPos, GameEvent.Context.of(null, currentState));
					}
				}
			}
			if(couldBreak) {
				this.setActive(level, spikeTrapPos, spikeTrapState, true);
				this.setExtending(level, spikeTrapPos, spikeTrapState, true);
				level.levelEvent(null, 2001, targetPos, Block.getId(targetState));
			}
			return couldBreak ? BreakBlockResult.BREAK : BreakBlockResult.BLOCK;
		}
	}

	public void setExtending(Level level, BlockPos pos, BlockState state, boolean extending) {
		this.extending = extending;
		level.sendBlockUpdated(pos, state, state, 2);
	}

	public boolean isExtending() {
		return this.extending;
	}

	public boolean isActive(BlockState state) {
		return state.getValue(SpikeTrapBlock.ACTIVE);
	}

	public void setActive(Level level, BlockPos pos, BlockState state, boolean active) {
		level.setBlockAndUpdate(pos, state.setValue(SpikeTrapBlock.ACTIVE, active));
	}

	public void setActiveSpoop(Level level, BlockPos pos, BlockState state, boolean active) {
		this.activeSpoop = active;
		level.sendBlockUpdated(pos, state, state, 2);
	}

	public BlockPos getHitArea(Level level, BlockPos pos, BlockState state) {
		Direction facing = state.getValue(SpikeTrapBlock.FACING);
		BlockPos hitArea = pos.relative(facing, 1);
		return hitArea;
	}

	protected void activateBlock(Level level, BlockPos pos, BlockState state) {
		if (extendingTicks >= 1 && damageCooldown == 0) {
			BlockPos hitArea = getHitArea(level, pos, state);
			List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(hitArea), SPIKE_TRAP_CAN_HURT);
			for (LivingEntity entity : list) {
				hurtEntity(level, pos, state, entity);
			}
		}
	}

	protected void hurtEntity(Level level, BlockPos pos, BlockState state, LivingEntity entity) {
		final float damage = ElixirEffectRegistry.EFFECT_NIMBLEFEET.get().isActive(entity) ? 1 : 2;
		final boolean didHurt = entity.hurt(level.damageSources().cactus(), damage);
		if(didHurt)
			this.damageCooldown = 25; // 5 tick extension, 20 tick retraction
	}

	@Nullable
	protected Entity shouldTrapTrigger(Level level, BlockPos pos, BlockState state) {
		BlockPos hitArea = getHitArea(level, pos, state);
		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(hitArea).deflate(0.25D));
		for (Entity entity : list) {
			if(canTriggerTrap(level, pos, state, entity, this.delayedTriggerTicks))
				return entity;
		}
		return null;
	}

	@Nullable
	protected Entity isBlockOccupied(Level level, BlockPos pos, BlockState state) {
		BlockPos hitArea = getHitArea(level, pos, state);
		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(hitArea).deflate(0.25D));
		for (Entity entity : list) {
			if(canBeTargeted(entity))
				return entity;
		}
		return null;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putInt("extending_ticks", this.extendingTicks);
		tag.putBoolean("extending", this.extending);
		tag.putInt("damage_cooldown", this.damageCooldown);
		tag.putInt("delayed_trigger_ticks", this.delayedTriggerTicks);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.extendingTicks = tag.getInt("extending_ticks");
		this.extending = tag.getBoolean("extending");
		this.damageCooldown = tag.getInt("damage_cooldown");
		this.delayedTriggerTicks = tag.getInt("delayed_trigger_ticks");
	}
}
