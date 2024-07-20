package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
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
import thebetweenlands.common.registries.BlockEntityRegistry;

import java.util.List;

public class DecayPitGroundChainBlockEntity extends SyncedBlockEntity {

	public int animationTicksChain = 0;
	public int animationTicksChainPrev = 0;
	private int length = 5;
	private boolean moving = false;
	private boolean slow = false;
	private boolean raising = false;
	private boolean broken = false;
	private int breakTimer = 0;

	public DecayPitGroundChainBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.DECAY_PIT_GROUND_CHAIN.get(), pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, DecayPitGroundChainBlockEntity entity) {
		entity.animationTicksChainPrev = entity.animationTicksChain;
		if (entity.isMoving()) {
			if (entity.isSlow())
				entity.animationTicksChain++;
			else if (entity.isBroken())
				entity.animationTicksChain += 32;
			else
				entity.animationTicksChain += 8;
		}

		List<Entity> collided = entity.getEntityCollidedWithChains(level, entity.getHangingLengthCollision(pos, 0.625F, 5F, 0.625F));
		if (!collided.isEmpty())
			entity.checkCollisions(level, collided);

		if (entity.animationTicksChainPrev >= 128) {
			entity.animationTicksChain = entity.animationTicksChainPrev = 0;
			if (!entity.isBroken())
				entity.setMoving(false);
		}

		if (!level.isClientSide() && entity.isBroken()) {
			entity.breakTimer++;
			if (entity.breakTimer > 32) {
				if (entity.breakTimer % 4 == 0) {
					entity.setLength(entity.getLength() - 1);
					level.sendBlockUpdated(pos, state, state, 3);
				}
				if (entity.getLength() <= 0) {
					level.removeBlock(pos, false);
				}
			}
		}
	}

	public List<Entity> getEntityCollidedWithChains(Level level, AABB chainBox) {
		return level.getEntitiesOfClass(Entity.class, chainBox);
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

	public AABB getHangingLengthCollision(BlockPos pos, double x, double y, double z) {
		return new AABB(pos.getX() + 0.5D - x * 0.5D, pos.getY(), pos.getZ() + 0.5D + -z * 0.5D, pos.getX() + 0.5D + x * 0.5D, pos.getY() + y, pos.getZ() + 0.5D + z * 0.5D);
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public boolean isMoving() {
		return moving;
	}

	public void setSlow(boolean slow) {
		this.slow = slow;
	}

	public boolean isSlow() {
		return slow;
	}

	public void setRaising(boolean raising) {
		this.raising = raising;
	}

	public boolean isRaising() {
		return raising;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getLength() {
		return length;
	}

	public void setBroken(boolean broken) {
		this.broken = broken;
	}

	public boolean isBroken() {
		return broken;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putInt("animation_ticks_chain", this.animationTicksChain);
		tag.putInt("animation_ticks_chain_prev", this.animationTicksChainPrev);
		tag.putInt("length", this.getLength());
		tag.putBoolean("raising", this.isRaising());
		tag.putBoolean("moving", this.isMoving());
		tag.putBoolean("broken", this.isBroken());
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.animationTicksChain = tag.getInt("animation_ticks_chain");
		this.animationTicksChainPrev = tag.getInt("animation_ticks_chain_prev");
		this.setLength(tag.getInt("length"));
		this.setRaising(tag.getBoolean("raising"));
		this.setMoving(tag.getBoolean("moving"));
		this.setBroken(tag.getBoolean("broken"));
	}
}
