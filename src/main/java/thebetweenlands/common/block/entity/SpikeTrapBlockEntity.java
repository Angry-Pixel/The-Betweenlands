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
import thebetweenlands.common.block.SludgeBlock;
import thebetweenlands.common.block.SpikeTrapBlock;
import thebetweenlands.common.entities.BLEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class SpikeTrapBlockEntity extends SyncedBlockEntity {

	public int prevAnimationTicks;
	public int animationTicks;
	public boolean active;
	public byte type;

	public SpikeTrapBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.SPIKE_TRAP.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, SpikeTrapBlockEntity entity) {
		if (!level.isClientSide()) {
			Direction facing = state.getValue(SpikeTrapBlock.FACING);

			BlockState stateFacing = level.getBlockState(pos.relative(facing, 1));
			if (!stateFacing.isAir() && stateFacing.getDestroySpeed(level, pos.relative(facing, 1)) >= 0.0F && !(stateFacing.getBlock() instanceof SludgeBlock)) {
				entity.setType(level, pos, state, (byte) 1);
				entity.setActive(level, pos, state, true);
				level.levelEvent(null, 2001, pos.relative(facing, 1), Block.getId(stateFacing));
				level.destroyBlock(pos.relative(facing, 1), true);
			}
			BlockState stateFacing2 = level.getBlockState(pos.relative(facing, 2));
			if (!stateFacing2.isAir() && stateFacing2.getDestroySpeed(level, pos.relative(facing, 2)) >= 0.0F && !(stateFacing2.getBlock() instanceof SludgeBlock)) {
				entity.setType(level, pos, state, (byte) 1);
				entity.setActive(level, pos, state, true);
				level.levelEvent(null, 2001, pos.relative(facing, 2), Block.getId(state));
				level.destroyBlock(pos.relative(facing, 2), true);
			}
			if (level.getRandom().nextInt(500) == 0) {
				if (entity.type != 0 && !entity.active && entity.animationTicks == 0)
					entity.setType(level, pos, state, (byte) 0);
				else if (entity.isBlockOccupied(level, pos, state) == null)
					entity.setType(level, pos, state, (byte) 1);
			}

			if (entity.isBlockOccupied(level, pos, state) != null && entity.type != 0)
				if (!entity.active && entity.animationTicks == 0)
					entity.setActive(level, pos, state, true);

		}
		entity.prevAnimationTicks = entity.animationTicks;
		if (entity.active) {
			entity.activateBlock(level, pos, state);
			if (entity.animationTicks == 0)
				level.playSound(null, pos, SoundRegistry.SPIKE.get(), SoundSource.BLOCKS, 1.25F, 1.0F);
			if (entity.animationTicks <= 20)
				entity.animationTicks += 4;
			if (entity.animationTicks == 20 && !level.isClientSide())
				entity.setActive(level, pos, state, false);
		}
		if (!entity.active)
			if (entity.animationTicks >= 1)
				entity.animationTicks--;
	}

	public void setActive(Level level, BlockPos pos, BlockState state, boolean isActive) {
		this.active = isActive;
		level.sendBlockUpdated(pos, state, state, 2);
	}

	public void setType(Level level, BlockPos pos, BlockState state, byte blockType) {
		this.type = blockType;
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
						entity.hurt(level.damageSources().generic(), 2);
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
		tag.putBoolean("active", this.active);
		tag.putByte("type", this.type);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.animationTicks = tag.getInt("animation_ticks");
		this.active = tag.getBoolean("active");
		this.type = tag.getByte("type");
	}
}
