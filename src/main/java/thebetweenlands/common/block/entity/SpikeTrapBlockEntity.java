package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.block.terrain.SludgeBlock;
import thebetweenlands.common.block.structure.SpikeTrapBlock;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;
import java.util.List;

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

	public static void tick(Level level, BlockPos pos, BlockState state, SpikeTrapBlockEntity entity) {
		if (!level.isClientSide() && !level.isDebug()) {
			Direction facing = state.getValue(SpikeTrapBlock.FACING);

			BlockState stateFacing = level.getBlockState(pos.relative(facing, 1));
			if (!stateFacing.isAir() && stateFacing.getDestroySpeed(level, pos.relative(facing, 1)) >= 0.0F && !(stateFacing.getBlock() instanceof SludgeBlock)) {
				entity.setActive(level, pos, state, true);
				entity.setStabbing(level, pos, state, true);
				level.levelEvent(null, 2001, pos.relative(facing, 1), Block.getId(stateFacing));
				level.destroyBlock(pos.relative(facing, 1), true);
			}
			BlockState stateFacing2 = level.getBlockState(pos.relative(facing, 2));
			if (!stateFacing2.isAir() && stateFacing2.getDestroySpeed(level, pos.relative(facing, 2)) >= 0.0F && !(stateFacing2.getBlock() instanceof SludgeBlock)) {
				entity.setActive(level, pos, state, true);
				entity.setStabbing(level, pos, state, true);
				level.levelEvent(null, 2001, pos.relative(facing, 2), Block.getId(state));
				level.destroyBlock(pos.relative(facing, 2), true);
			}
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
			if (entity.animationTicks == 20 && !level.isClientSide())
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
		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(hitArea));
		if (animationTicks >= 1) {
			for (LivingEntity entity : list) {
				if (entity != null)
					if (!(entity instanceof BLEntity))
						entity.hurt(level.damageSources().cactus(), 2);
			}
		}
	}

	@Nullable
	protected Entity isBlockOccupied(Level level, BlockPos pos, BlockState state) {
		Direction facing = state.getValue(SpikeTrapBlock.FACING);
		BlockPos hitArea = pos.relative(facing, 1);
		List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, new AABB(hitArea).deflate(0.25D));
		for (Entity entity : list) {
			if (entity != null)
				if (!(entity instanceof BLEntity))
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
