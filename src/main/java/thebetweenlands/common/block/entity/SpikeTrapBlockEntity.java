package thebetweenlands.common.block.entity;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.block.structure.SpikeTrapBlock;
import thebetweenlands.common.datagen.tags.BLBlockTagProvider;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class SpikeTrapBlockEntity extends SyncedBlockEntity {

	public int prevAnimationTicks;
	public int animationTicks;
	public boolean stabbing;
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
		return EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(entity) &&
				EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity) &&
				!(entity instanceof BLEntity);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, SpikeTrapBlockEntity entity) {
		if (!level.isClientSide() && !level.isDebug()) {
			
			entity.destroyBlocksInFront(level, pos, state);
			
			if (level.getRandom().nextInt(500) == 0) {
				if (entity.isActive(state) && !entity.stabbing && entity.animationTicks == 0)
					entity.setActive(level, pos, state, false);
				else if (entity.isBlockOccupied(level, pos, state) == null)
					entity.setActive(level, pos, state, true);
			}

			if (entity.isBlockOccupied(level, pos, state) != null && entity.isActive(state))
				if (!entity.stabbing && entity.animationTicks == 0)
					entity.setStabbing(level, pos, state, true);

		}
		entity.prevAnimationTicks = entity.animationTicks;
		if (entity.stabbing) {
			entity.activateBlock(level, pos, state);
			if (entity.animationTicks == 0)
				level.playSound(null, pos, SoundRegistry.SPIKE.get(), SoundSource.BLOCKS, 1.25F, 1.0F);
			if (entity.animationTicks <= 20)
				entity.animationTicks += 4;
			if (entity.animationTicks >= 20 && !level.isClientSide())
				entity.setStabbing(level, pos, state, false);
		} else {
			if (entity.animationTicks >= 1) {
				entity.animationTicks--;
			}
		}

		if (entity.canSpook) {
			entity.prevSpoopAnimationTicks = entity.spoopAnimationTicks;
			if (!entity.activeSpoop && level.getRandom().nextInt(11) + level.getGameTime() % 10 == 0 && entity.spoopAnimationTicks == 0)
				entity.setActiveSpoop(level, pos, state, true);
			if (entity.activeSpoop) {
				if (entity.spoopAnimationTicks <= 20)
					entity.spoopAnimationTicks += 1;
				if (entity.spoopAnimationTicks == 20)
					entity.setActiveSpoop(level, pos, state, false);
			}
			if (!entity.activeSpoop)
				if (entity.spoopAnimationTicks >= 1)
					entity.spoopAnimationTicks--;
		}
	}

	public BlockPos destroyBlocksInFront(Level level, BlockPos pos, BlockState state) {
		return destroyBlocksInFront(level, pos, state, 2);
	}
	
	/**
	 * Attempt to destroy n blocks in front of the spike trap, breaking early if it is blocked
	 * @param level The level the spike trap is in
	 * @param pos	The position of the spike trap
	 * @param state	The state of the spike trap
	 * @param totalDistance	The number of blocks to attempt to break on the spike's front face
	 * @return the position of the block that blocked the spike trap, or the position {@code totalDistance} away from the spike trap's face if it wasn't blocked
	 */
	public BlockPos destroyBlocksInFront(Level level, BlockPos pos, BlockState state, int totalDistance) {
		Direction facing = state.getValue(SpikeTrapBlock.FACING);

		MutableBlockPos targetPos = pos.mutable();
		
		for(int i = 0; i < totalDistance; ++i) {
			targetPos.move(facing, 1);
			boolean notBlocked = attemptDestroyBlock(level, pos, state, targetPos);
			if(!notBlocked) {
				return targetPos.immutable();
			}
		}
		
		return targetPos.immutable();
	}
	
	/**
	 * Attempt to destroy the block at the target position with the spike trap
	 * @param level	The level the spike trap is in
	 * @param spikeTrapPos	The position of the spike trap
	 * @param spikeTrapState	The state of the spike trap
	 * @param targetPos	The position of the block to attempt destroying
	 * @return false if it tried and failed to break a block, true otherwise
	 */
	public boolean attemptDestroyBlock(Level level, BlockPos spikeTrapPos, BlockState spikeTrapState, BlockPos targetPos) {
		BlockState targetState = level.getBlockState(targetPos);
		
		if(targetState.is(BLBlockTagProvider.SPIKE_TRAPS_BLOCKED_BY)) {
			return false;
		} else if(targetState.is(BLBlockTagProvider.SPIKE_TRAPS_IGNORE) || targetState.isAir()) {
			return true;
		} else if(targetState.getDestroySpeed(level, targetPos) >= 0.0F) { // TODO - Figure out if checking for destroy speed is still necessary or needs to be replaced
			this.setActive(level, spikeTrapPos, spikeTrapState, true);
			this.setStabbing(level, spikeTrapPos, spikeTrapState, true);
			level.levelEvent(null, 2001, targetPos, Block.getId(targetState));
			return level.destroyBlock(targetPos, true);
		} else {
			return true;
		}
	}

	public void setStabbing(Level level, BlockPos pos, BlockState state, boolean stabbing) {
		this.stabbing = stabbing;
		level.sendBlockUpdated(pos, state, state, 2);
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

	protected void activateBlock(Level level, BlockPos pos, BlockState state) {
		Direction facing = state.getValue(SpikeTrapBlock.FACING);
		BlockPos hitArea = pos.relative(facing, 1);
		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(hitArea), SpikeTrapBlockEntity::canBeTargeted);
		if (animationTicks >= 1) {
			for (LivingEntity entity : list) {
					entity.hurt(level.damageSources().cactus(), 2);
			}
		}
	}

	@Nullable
	protected Entity isBlockOccupied(Level level, BlockPos pos, BlockState state) {
		Direction facing = state.getValue(SpikeTrapBlock.FACING);
		BlockPos hitArea = pos.relative(facing, 1);
		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(hitArea).deflate(0.25D), SpikeTrapBlockEntity::canBeTargeted);
		for (Entity entity : list) {
				return entity;
		}
		return null;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putInt("animation_ticks", this.animationTicks);
		tag.putBoolean("stabbing", this.stabbing);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.animationTicks = tag.getInt("animation_ticks");
		this.stabbing = tag.getBoolean("stabbing");
	}
}
