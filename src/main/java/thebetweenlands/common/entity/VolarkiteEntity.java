package thebetweenlands.common.entity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.datagen.tags.BLBlockTagProvider;
import thebetweenlands.common.item.misc.VolarkiteItem;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.util.PlayerUtil;

public class VolarkiteEntity extends Entity {
	public float zRotO;
	public float zRot;

	protected int updraftTicks = 0;
	protected int downdraftTicks = 0;
	protected int draftSourcePos = 0;

	protected int userInAirTicks = 20;

	public VolarkiteEntity(EntityType<? extends Entity> type, Level level) {
		super(type, level);

	}

	@Override
	protected void defineSynchedData(Builder builder) {
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
	}

	@Override
	public Vec3 getPassengerAttachmentPoint(Entity passenger, EntityDimensions dimensions, float scale) {
		if (passenger instanceof Player)
			return new Vec3(0.0D, 0.6D, 0.0D);
		return super.getPassengerAttachmentPoint(passenger, dimensions, scale);
	}

	@Override
	public Vec3 getVehicleAttachmentPoint(Entity vehicle) {
		if (vehicle instanceof Player)
			return new Vec3(0.0D, 1.8D, 0.0D);
		return super.getVehicleAttachmentPoint(vehicle);
	}

	public void handleRiderDismount(LivingEntity rider) {
		float yaw = rider.getYRot();
		float pitch = rider.getXRot();

		rider.stopRiding();
		// Set rider's position to volarkite position
		rider.moveTo(this.getX(), this.getY(), this.getZ(), yaw, pitch);
		rider.setDeltaMovement(this.getDeltaMovement());
		rider.setOnGround(this.onGround());
	}

	@Override
	protected void positionRider(Entity passenger, MoveFunction callback) {
		super.positionRider(passenger, callback);
		PlayerUtil.resetFloating(passenger);
	 	PlayerUtil.resetVehicleFloating(passenger);
	}

	@Override
	public void baseTick() {
		this.xOld = this.getX();
		this.yOld = this.getY();
		this.zOld = this.getZ();
		this.xRotO = this.getXRot();
		this.yRotO = this.getYRot();
		this.zRotO = this.zRot;

		Entity passenger = this.getControllingPassenger();
		Entity riding = this.getVehicle();

		if (!this.level().isClientSide()) {
			// Allow player to "dismount" when crouching
			if (riding != null && riding.isCrouching())
				this.stopRiding();

			boolean hasUpdraft = this.updraftTicks > 0;

			if (this.onGround() && !hasUpdraft) {
				this.userInAirTicks = 0;

				if (passenger != null) {
					passenger.stopRiding();
					this.startRiding(passenger, true);
				}
			} else {
				if (riding != null && (hasUpdraft || (riding.getDeltaMovement().y < 0 && this.userInAirTicks++ > 3 && riding.fallDistance > 0.55f))) {
					Vec3 existingMovement = riding.getDeltaMovement();
					this.stopRiding();
					riding.startRiding(this);
					this.setDeltaMovement(existingMovement);
					this.hasImpulse = true;
					this.getServer().getPlayerList().broadcastAll(new ClientboundSetPassengersPacket(riding));
				}
			}

			if (this.getServer() != null && this.getVehicle() != null) {
				this.getServer().getPlayerList().broadcastAll(new ClientboundSetPassengersPacket(this.getVehicle()));
			}
		}

		double targetMotionY = -0.04D;
		this.setDeltaMovement(this.getDeltaMovement().x(), targetMotionY + (this.getDeltaMovement().y() - targetMotionY) * 0.92D, this.getDeltaMovement().z());

		this.move(MoverType.SELF, this.getDeltaMovement());
		this.updateInWaterStateAndDoWaterCurrentPushing();

		float invFriction = 1.0F;

		if (this.onGround())
			invFriction *= 0.8F;

		if (this.isInWater() || this.isInLava()) {
			invFriction *= 0.8F;

			if (this.level().getFluidState(BlockPos.containing(this.getX(), this.getY() + this.getBbHeight() + 0.75D, this.getZ())).isSource() || !this.level().getFluidState(BlockPos.containing(this.getX(), this.getY() + this.getBbHeight() + 0.75D, this.getZ())).isEmpty())
				invFriction *= 0.5F;

		}

		this.setDeltaMovement(this.getDeltaMovement().multiply(invFriction, invFriction, invFriction));
		Entity controller = passenger != null ? passenger : riding;
		Vec3 kiteDir = new Vec3(Math.cos(Math.toRadians(this.getYRot() + 90)), 0, Math.sin(Math.toRadians(this.getYRot() + 90)));
		double rotIncr = 0;
		boolean hasValidUser = false;

		if (controller instanceof LivingEntity entity) {
			entity.resetFallDistance();

			if (this.level().isClientSide()) {
				this.setYRot(entity.yBodyRot);
				if (controller == passenger) {
					Vec3 controllerDir = new Vec3(Math.cos(Math.toRadians(entity.getYRot() + 90)), 0, Math.sin(Math.toRadians(entity.getYRot() + 90)));
					double dotProduct = Math.clamp(kiteDir.dot(controllerDir), -1.0D, 1.0D);
					double rotDiff = Math.toDegrees(Math.acos(dotProduct)) * -Math.signum(kiteDir.cross(controllerDir).y);

					if (Double.isNaN(rotDiff))
						rotDiff = 0.0D;

					rotIncr = Math.clamp(rotDiff * 0.05D, -1.0D, 1.0D);
					this.setYRot((float) (this.getYRot() + rotIncr));
				}
			}

			if (this.getDeltaMovement().y() < 0 && !this.onGround()) {
				double speedBoost = -this.getDeltaMovement().y() * 0.1D + Mth.clamp(Math.sin(Math.toRadians(this.getXRot())) * 0.5F, -0.02D, 0.02D);
				this.setDeltaMovement(getDeltaMovement().add(kiteDir.x * (speedBoost + 0.01D), 0, kiteDir.z * (speedBoost + 0.01D)));
				this.hasImpulse = true;
			}

			if (!this.onGround()) {
				float forward = entity.zza;

				if (forward > 0.1F) {
					float newPitch = 20.0F + (this.getXRot() - 20.0F) * 0.9F;
					this.setXRot(newPitch);

					Vec3 currentMovement = this.getDeltaMovement();
					this.setDeltaMovement(currentMovement.x, currentMovement.y - 0.01D, currentMovement.z);

				} else if (forward < -0.1F) {
					float newPitch = -20.0F + (this.getXRot() + 20.0F) * 0.9F;
					this.setXRot(newPitch);
				}
			}

			for (ItemStack stack : entity.getHandSlots()) {
				if (!stack.isEmpty() && stack.getItem() instanceof VolarkiteItem kite && kite.canRideKite(stack, controller)) {
					hasValidUser = true;
					break;
				}
			}
		}

		if (!this.onGround() && Math.abs(rotIncr) > 0.1D)
			this.zRot = (float) (rotIncr * 15 + (this.zRot - rotIncr * 15) * 0.9D);
		else
			this.zRot *= 0.9F;

		this.setXRot(this.getXRot() * 0.9F);
		this.updateUpdraft();
		double speed = Math.sqrt(this.getDeltaMovement().x() * this.getDeltaMovement().x() + this.getDeltaMovement().z() * this.getDeltaMovement().z());

		if (speed > 0.1D) {
			double dx = this.getDeltaMovement().x() / speed;
			double dz = this.getDeltaMovement().z() / speed;
			this.setDeltaMovement((kiteDir.x + (dx - kiteDir.x) * 0.9D) * speed, getDeltaMovement().y(), (kiteDir.z + (dz - kiteDir.z) * 0.9D) * speed);
			double maxSpeed = 0.6D;

			if (speed > maxSpeed) {
				double targetX = dx * maxSpeed;
				double targetZ = dz * maxSpeed;
				this.setDeltaMovement(targetX + (this.getDeltaMovement().x() - targetX) * 0.8D, getDeltaMovement().y(), targetZ + (this.getDeltaMovement().z() - targetZ) * 0.8D);
			}

			this.hasImpulse = true;
		}

		if (!this.level().isClientSide() && !hasValidUser)
			this.discard();

		this.firstTick = false;
	}

	protected void updateUpdraft() {
		int range = 10;

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
		pos.set(Mth.floor(this.getX()), Mth.floor(this.getY()), Mth.floor(this.getZ()));

		for (int i = 0; i <= range; i++) {
			BlockState state = this.level().getBlockState(pos);
			boolean hasSource = false;
			FluidState fluidState = state.getFluidState();

			if (!fluidState.isEmpty()) {
				int temperature = fluidState.getType().getFluidType().getTemperature();

				if (temperature > 373 /* roughly 100°C */) {
					this.updraftTicks = 25;
					hasSource = true;
				} else if (temperature < 272.15 /* roughly -1°C */) {
					this.downdraftTicks = 25;
					hasSource = true;
				}
			} else if (state.is(BLBlockTagProvider.CREATES_VOLARKITE_UPDRAFTS)) {
				this.updraftTicks = 25;
				hasSource = true;
			} else if (state.is(BLBlockTagProvider.CREATES_VOLARKITE_DOWNDRAFTS)) {
				this.downdraftTicks = 25;
				hasSource = true;
			} else if (!state.isAir()) {
				break;
			}

			if (hasSource) {
				this.draftSourcePos = pos.getY();
				break;
			}

			pos.set(pos.getX(), pos.getY() - 1, pos.getZ());
		}

		if (this.updraftTicks > 0 || this.downdraftTicks > 0) {
			if (this.getDeltaMovement().y() < 1.0D)
				setDeltaMovement(getDeltaMovement().add(0, this.downdraftTicks > 0 ? -0.03D : 0.1D, 0));

			if (this.level().isClientSide()) {
				for (int i = 0; i < (this.downdraftTicks > 0 ? 2 : 10); i++) {
					float offsetX = this.level().getRandom().nextFloat() - 0.5F;
					float offsetZ = this.level().getRandom().nextFloat() - 0.5F;
					float len = (float) Math.sqrt(offsetX * offsetX + offsetZ * offsetZ);
					offsetX /= len;
					offsetZ /= len;
					this.level().addParticle(ParticleTypes.SMOKE, this.getX() + offsetX, this.draftSourcePos + (this.getY() + (this.downdraftTicks > 0 ? 2.4D : 1) - this.draftSourcePos) * this.level().getRandom().nextFloat(), this.getZ() + offsetZ, this.getDeltaMovement().x(), this.getDeltaMovement().y() + (this.downdraftTicks > 0 ? -0.15D : 0.25D), this.getDeltaMovement().z());
				}
			}
		}

		if (this.updraftTicks > 0)
			this.updraftTicks--;

		if (this.downdraftTicks > 0)
			this.downdraftTicks--;
	}

	@Override
	public void tick() {
		super.tick();

		while (this.getYRot() - this.yRotO < -180.0F)
			this.yRotO -= 360.0F;

		while (this.getYRot() - this.yRotO >= 180.0F)
			this.yRotO += 360.0F;
	}

	@Override
	@Nullable
	public LivingEntity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : (LivingEntity) this.getPassengers().getFirst();
	}

	@Override
	public boolean isControlledByLocalInstance() {
		return this.getControllingPassenger() instanceof Player player ? player.isLocalPlayer() : !this.level().isClientSide();
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	@Override
	protected void removePassenger(Entity passenger) {
		super.removePassenger(passenger);
		passenger.resetFallDistance();
		passenger.setDeltaMovement(this.getDeltaMovement());
	}

	@Override
	public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		return super.shouldRenderAtSqrDistance(distance) || (this.getControllingPassenger() != null && this.getControllingPassenger().shouldRenderAtSqrDistance(distance));
	}

	@Override
	public @Nullable ItemStack getPickResult() {
		return new ItemStack(ItemRegistry.VOLARKITE.get());
	}
}
