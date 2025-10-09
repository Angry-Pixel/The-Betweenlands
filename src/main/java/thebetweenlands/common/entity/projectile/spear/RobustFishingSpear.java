package thebetweenlands.common.entity.projectile.spear;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.Optional;

public class RobustFishingSpear extends FishingSpear {

	private static final EntityDataAccessor<Boolean> ANIMATED = SynchedEntityData.defineId(RobustFishingSpear.class, EntityDataSerializers.BOOLEAN);

	public int clientReturnTick;

	public RobustFishingSpear(EntityType<? extends AbstractArrow> type, Level level) {
		super(type, level);
	}

	@Override
	public void onSpawn(Level level, LivingEntity owner, ItemStack stack, boolean crit) {
		super.onSpawn(level, owner, stack, crit);
		this.getEntityData().set(ANIMATED, stack.has(DataComponentRegistry.ANIMATED));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(ANIMATED, false);
	}

	public boolean isAnimated() {
		return this.getEntityData().get(ANIMATED);
	}

	@Override
	public void tick() {
		super.tick();

		Entity entity = this.getOwner();
		if (this.getEntityData().get(ANIMATED) && (this.dealtDamage || this.isNoPhysics()) && entity != null) {
			if (!this.isAcceptableReturnOwner()) {
				if (!this.level().isClientSide() && this.pickup == AbstractArrow.Pickup.ALLOWED) {
					this.spawnAtLocation(this.getPickupItem(), 0.1F);
				}

				this.discard();
			} else {
				this.setNoPhysics(true);
				Vec3 vec3 = entity.getEyePosition().subtract(this.position());
				this.setPosRaw(this.getX(), this.getY() + vec3.y * 0.25D, this.getZ());
				if (this.level().isClientSide()) {
					this.yOld = this.getY();
				}

				double wiggleSpeed = Math.sin((double) (this.tickCount + this.getId()) * 0.055D) * 0.05D;
				double wiggleOffsetX = Math.cos(this.getYRot() * 0.01F);
				double wiggleOffsetZ = Math.sin(this.getYRot() * 0.01F);

				this.setDeltaMovement(this.getDeltaMovement().scale(0.85).add(vec3.normalize().scale(0.15D)).add(wiggleSpeed * wiggleOffsetX, 0.0D, wiggleSpeed * wiggleOffsetZ));
				if (this.clientReturnTick == 0) {
					this.playSound(SoundRegistry.SPEAR_RETURN_1.get(), 1.0F, 1.0F);
				}

				this.clientReturnTick++;
			}
		}
	}

	private boolean isAcceptableReturnOwner() {
		Entity entity = this.getOwner();
		return entity != null && entity.isAlive() && (!(entity instanceof ServerPlayer) || !entity.isSpectator());
	}

	@Override
	protected boolean tryPickup(Player player) {
		boolean flag = super.tryPickup(player);
		if (flag) {
			player.playSound(SoundRegistry.SPEAR_RETURN_2.get());
		}
		return flag;
	}

	@Override
	protected ItemStack getDefaultPickupItem() {
		return ItemRegistry.ROBUST_AMPHIBIOUS_FISHING_SPEAR.toStack();
	}

	@Override
	public void setPickupItemStack(ItemStack pickupItemStack) {
		super.setPickupItemStack(pickupItemStack);
	}

	@Override
	protected float getWaterInertia() {
		return 0.99F;
	}
}
