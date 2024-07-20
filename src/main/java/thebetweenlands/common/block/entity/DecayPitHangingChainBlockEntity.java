package thebetweenlands.common.block.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thebetweenlands.client.audio.DecayPitChainSoundInstance;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.BlockRegistry;

import java.util.List;

public class DecayPitHangingChainBlockEntity extends SyncedBlockEntity {

	public int animationTicksChain = 0;
	public int animationTicksChainPrev = 0;
	private int progress = 0;
	public static final float MOVE_UNIT = 0.0078125F; // unit of movement
	private boolean moving = false;
	private boolean slow = false;
	private boolean broken = false;
	public boolean playChainSound = true;

	public DecayPitHangingChainBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.DECAY_PIT_HANGING_CHAIN.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, DecayPitHangingChainBlockEntity entity) {
		entity.animationTicksChainPrev = entity.animationTicksChain;

		if (entity.isMoving()) {
			if (entity.isSlow())
				entity.animationTicksChain++;
			else if (entity.isBroken())
				entity.animationTicksChain += 32;
			else
				entity.animationTicksChain += 8;
		}

		if (entity.isBroken() && entity.getProgress() > -512)
			entity.setProgress(entity.getProgress() - 32);

		if (entity.isBroken() && entity.getProgress() <= -512)
			if (!level.isClientSide())
				level.setBlockAndUpdate(pos, BlockRegistry.COMPACTED_MUD.get().defaultBlockState());

		for (Direction dir : Direction.Plane.HORIZONTAL) {
			entity.handleChainCollision(level, entity.getHangingLengthCollision(pos, dir.getStepX(), dir.getStepZ(), 2F + entity.getProgress() * MOVE_UNIT));
		}

		if (entity.animationTicksChainPrev >= 128) {
			entity.animationTicksChain = entity.animationTicksChainPrev = 0;
			if (!entity.isBroken())
				entity.setMoving(false);
		}

		if (entity.animationTicksChainPrev == 0 && entity.isMoving() && entity.isSlow())
			if (!entity.playChainSound)
				entity.playChainSound = true;

		if (entity.isBroken() && entity.getProgress() >= 640)
			if (!entity.playChainSound)
				entity.playChainSound = true;

		if (level.isClientSide() && entity.playChainSound) {
			if (!entity.isBroken())
				entity.playChainSound(level, pos);
			else
				entity.playChainSoundFinal(level, pos);
			entity.playChainSound = false;
		}
	}

	public void playChainSound(Level level, BlockPos pos) {
		SoundInstance chainSound = new DecayPitChainSoundInstance(this);
		Minecraft.getInstance().getSoundManager().play(chainSound);
	}

	public void playChainSoundFinal(Level level, BlockPos pos) {
		//TODO Add final chain sound/other thing
	}

	public void handleChainCollision(Level level, AABB chainBox) {
		List<Entity> list = level.getEntitiesOfClass(Entity.class, chainBox);
		if (!list.isEmpty()) {
			this.checkCollisions(level, list);
		}
	}

	private void checkCollisions(Level level, List<Entity> list) {
		for (Entity entity : list) {
			if (entity instanceof AbstractArrow arrow) { // just arrows for now
				arrow.deflect(ProjectileDeflection.MOMENTUM_DEFLECT, arrow, arrow.getOwner(), false); //TODO see if this is a viable alternative for arrow deflection
				level.playSound(null, arrow.blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.5F, 3F);
				// this.ticksInAir = 0;
			}
		}
	}

	public AABB getHangingLengthCollision(BlockPos pos, double offX, double offZ, float extended) {
		return new AABB(pos.getX() + offX + 0.1875D, pos.getY() - extended, pos.getZ() + offZ + 0.1875D, pos.getX() + offX + 0.8125D, pos.getY(), pos.getZ() + offZ + 0.8125D);
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getProgress() {
		return progress;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public boolean isMoving() {
		return this.moving;
	}

	public void setSlow(boolean slow) {
		this.slow = slow;
	}

	public boolean isSlow() {
		return this.slow;
	}

	public void setBroken(boolean broken) {
		this.broken = broken;
	}

	public boolean isBroken() {
		return this.broken;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putInt("animation_ticks_chain", this.animationTicksChain);
		tag.putInt("animation_ticks_chain_prev", this.animationTicksChainPrev);
		tag.putInt("progress", this.getProgress());
		tag.putBoolean("broken", this.isBroken());
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.animationTicksChain = tag.getInt("animation_ticks_chain");
		this.animationTicksChainPrev = tag.getInt("animation_ticks_chain_prev");
		this.setProgress(tag.getInt("progress"));
		this.setBroken(tag.getBoolean("broken"));
	}
}
