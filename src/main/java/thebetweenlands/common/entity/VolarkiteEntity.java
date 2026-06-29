package thebetweenlands.common.entity;

import java.util.Iterator;

import javax.annotation.Nullable;

import com.sk89q.worldedit.jlibnoise.Mth;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import thebetweenlands.common.item.misc.VolarkiteItem;
import thebetweenlands.common.registries.BlockRegistry;

public class VolarkiteEntity extends Entity {
	public float prevRotationRoll;
	public float rotationRoll;

	protected int updraftTicks = 0;
	protected int downdraftTicks = 0;
	protected int draftSourcePos = 0;

	protected int userInAirTicks = 20;

	public VolarkiteEntity(EntityType<? extends Entity> type, Level level) {
		super(type, level);
		//this.setSize(0.6F, 1.8F);
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
		double yOffset = 0.01D;
		if (passenger != null && passenger == this.getControllingPassenger())
			yOffset -= passenger.getVehicleAttachmentPoint(this).y();
		return new Vec3(0.0D, yOffset, 0.0D);
	}

	public void handleRiderDismount(LivingEntity rider) {
		float yaw = rider.getYRot();
		float pitch = rider.getXRot();

		rider.stopRiding();

		//Set rider's position to volarkite position
		rider.moveTo(this.getX(), this.getY(), this.getZ(), yaw, pitch);
		rider.setDeltaMovement(this.getDeltaMovement());
		rider.setOnGround(this.onGround());
	}

/* TODO ERRRRRR....
	@Override
	public void updatePassenger(Entity passenger) {
		super.updatePassenger(passenger);

		PlayerUtil.resetFloating(passenger);
		PlayerUtil.resetVehicleFloating(passenger);
	}
*/

	@Override
	 public void baseTick() {
		super.baseTick();
		this.xOld = this.getX();
		this.yOld = this.getY();
		this.zOld = this.getZ();
		this.xRotO = this.getXRot();
		this.yRotO = this.getYRot();
		this.prevRotationRoll = this.rotationRoll;

		Entity passenger = this.getControllingPassenger();
		Entity riding = this.getVehicle();

		if(!this.level().isClientSide()) {
			//Allow player to "dismount" when the volarkite is just riding the player while walking
			if(riding != null && riding.isCrouching()) {
				this.ejectPassengers();
			}

			boolean hasUpdraft = this.updraftTicks > 0;

			if(this.onGround() && !hasUpdraft) {
				this.userInAirTicks = 0;

				if(passenger != null) {
					this.ejectPassengers();
					this.startRiding(passenger, true);
				}
			} else {
				if(riding != null && (hasUpdraft || (riding.getDeltaMovement().y < 0 && this.userInAirTicks++ > 3 && riding.fallDistance > 0.55f))) {
					this.setDeltaMovement(riding.getDeltaMovement());


					this.ejectPassengers();

					riding.startRiding(this);

					this.setDeltaMovement(riding.getDeltaMovement());
					hasImpulse = true;
					this.hurtMarked = true;

					//this.getServer().getPlayerList().sendPacketToAllPlayers(new SPacketSetPassengers(riding));
				}
			}

			if(this.getServer() != null && this.getVehicle() != null) {
				this.getVehicle().hurtMarked = true;

				//this.getServer().getPlayerList().sendPacketToAllPlayers(new SPacketSetPassengers(this.getRidingEntity()));
			}
		}

		if(riding != null) {
			this.setYRot(riding.yRotO);
		}

		double targetMotionY = -0.04D;
		setDeltaMovement(getDeltaMovement().add(0, targetMotionY + (getDeltaMovement().y() - targetMotionY) * 0.92D, 0));

		this.move(MoverType.SELF, this.getDeltaMovement());
		this.updateInWaterStateAndDoWaterCurrentPushing();

		float invFriction = 1.0F;

		if(this.onGround()) {
			invFriction *= 0.8F;
		}
		if(this.isInWater() || this.isInLava()) {
			invFriction *= 0.8F;
			if (this.level().getFluidState(BlockPos.containing(this.getX(), this.getY() + this.getBbHeight() + 0.75D, this.getZ())).isSource() ||  !this.level().getFluidState(BlockPos.containing(this.getX(), this.getY() + this.getBbHeight() + 0.75D, this.getZ())).isEmpty()) {
				invFriction *= 0.5F; 
			}
		}
		this.setDeltaMovement(getDeltaMovement().multiply(invFriction, invFriction, invFriction));


		Entity controller = passenger != null ? passenger : riding;

		Vec3 kiteDir = new Vec3(Math.cos(Math.toRadians(this.getYRot() + 90)), 0, Math.sin(Math.toRadians(this.getYRot() + 90)));

		double rotIncr = 0;

		boolean hasValidUser = false;

		if(controller != null) {
			controller.fallDistance = 0;

			if(this.getDeltaMovement().y() < 0 && !this.onGround()) {
				double speedBoost = -this.getDeltaMovement().y() * 0.1D + Mth.clamp(Math.sin(Math.toRadians(this.getXRot())) * 0.5F, -0.02D, 0.02D);
				
				this.setDeltaMovement(getDeltaMovement().add(kiteDir.x * (speedBoost + 0.01D), 0, kiteDir.z * (speedBoost + 0.01D)));
				hasImpulse = true;
			}

			Vec3 controllerDir = new Vec3(Math.cos(Math.toRadians(controller.getYRot() + 90)), 0, Math.sin(Math.toRadians(controller.getYRot() + 90)));
			double rotDiff = Math.toDegrees(Math.acos(kiteDir.dot(controllerDir))) * -Math.signum(kiteDir.cross(controllerDir).y);
			rotIncr = Math.clamp(rotDiff * 0.05D, -1.0D, 1.0D);
			this.yRotO = this.getYRot();
			this.setYRot((float) (this.getYRot() + rotIncr));
			
			if (!this.onGround() && controller instanceof LivingEntity livingController) {
			    float forward = livingController.zza; 
			    
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


			Iterator<ItemStack> it = ((LivingEntity) controller).getHandSlots().iterator();
			while(it.hasNext()) {
				ItemStack stack = it.next();
				if(!stack.isEmpty() && stack.getItem() instanceof VolarkiteItem && ((VolarkiteItem) stack.getItem()).canRideKite(stack, controller)) {
					hasValidUser = true;
					break;
				}
			}
		}

		if(!this.onGround() && Math.abs(rotIncr) > 0.1D) {
			this.rotationRoll = (float) (rotIncr * 15 + (this.rotationRoll - rotIncr * 15) * 0.9D);
		} else {
			this.rotationRoll *= 0.9F;
		}

		this.setXRot(this.getXRot() * 0.9F);

		this.updateUpdraft();

		double speed = Math.sqrt(this.getDeltaMovement().x() * this.getDeltaMovement().x() + this.getDeltaMovement().z() * this.getDeltaMovement().z());

		if(speed > 0.1D) {
			double dx = this.getDeltaMovement().x()  / speed;
			double dz = this.getDeltaMovement().z()  / speed;
			this.setDeltaMovement((kiteDir.x + (dx - kiteDir.x) * 0.9D) * speed, getDeltaMovement().y(), (kiteDir.z + (dz - kiteDir.z) * 0.9D) * speed);

			double maxSpeed = 0.6D;
			if(speed > maxSpeed) {
				double targetX = dx * maxSpeed;
				double targetZ = dz * maxSpeed;

				this.setDeltaMovement(targetX + (this.getDeltaMovement().x()  - targetX) * 0.8D, getDeltaMovement().y(), targetZ + (this.getDeltaMovement().z()  - targetZ) * 0.8D);

			}
			hasImpulse = true;
		}

		if(!this.level().isClientSide() && !hasValidUser) {
			this.kill();
		}

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
			}
//TODO This need changing to use tags probably
			else if (state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE) || state.is(Blocks.LAVA) || state.is(BlockRegistry.OCTINE_ORE.get()) || state.is(BlockRegistry.OCTINE_BLOCK.get()) || state.is(BlockRegistry.SMOULDERING_PEAT.get())) {
				this.updraftTicks = 25;
				hasSource = true;
			}

			else if (state.is(net.minecraft.tags.BlockTags.ICE) || state.is(Blocks.SNOW) || state.is(Blocks.SNOW_BLOCK)) {
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

		if(this.updraftTicks > 0 || this.downdraftTicks > 0) {
			if(this.getDeltaMovement().y() < 1.0D) {
				setDeltaMovement(getDeltaMovement().add(0, this.downdraftTicks > 0 ? -0.03D : 0.1D, 0));
			}

			if(this.level().isClientSide()) {
				for(int i = 0; i < (this.downdraftTicks > 0 ? 2 : 10); i++) {
					float offsetX = this.level().getRandom().nextFloat() - 0.5F;
					float offsetZ = this.level().getRandom().nextFloat() - 0.5F;

					float len = (float)Math.sqrt(offsetX*offsetX + offsetZ*offsetZ);

					offsetX /= len;
					offsetZ /= len;

					this.level().addParticle(ParticleTypes.SMOKE, this.getX() + offsetX, this.draftSourcePos + (this.getY() + (this.downdraftTicks > 0 ? 2.4D : 1) - this.draftSourcePos) * this.level().getRandom().nextFloat(), this.getZ() + offsetZ, this.getDeltaMovement().x() , this.getDeltaMovement().y() + (this.downdraftTicks > 0 ? -0.15D : 0.25D), this.getDeltaMovement().z() );
				}
			}
		}

		if(this.updraftTicks > 0) {
			this.updraftTicks--;
		}

		if(this.downdraftTicks > 0) {
			this.downdraftTicks--;
		}
	}

	@Override
	public void tick() {
		super.tick();

		while(this.getYRot()- this.yRotO < -180.0F) {
			this.yRotO -= 360.0F;
		}

		while(this.getYRot()- this.yRotO >= 180.0F) {
			this.yRotO += 360.0F;
		}
	}

	@Override
	@Nullable
	public LivingEntity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : (LivingEntity)this.getPassengers().get(0);
	}

    @Override
    public boolean isControlledByLocalInstance() {
        return getControllingPassenger() instanceof Player ? ((Player)getControllingPassenger()).isLocalPlayer() : !this.level().isClientSide;
    }

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	@Override
	protected void removePassenger(Entity passenger) {
		super.removePassenger(passenger);
		passenger.fallDistance = 0;
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

	private static boolean isMountingEvent = false;

	@SubscribeEvent
	public static void onMountEvent(EntityMountEvent event) {
		if(!isMountingEvent) {
			isMountingEvent = true;

			try {
				if(event.isDismounting()) {
					Entity mount = event.getEntityBeingMounted();
					Entity rider = event.getEntityMounting();

					if(mount instanceof VolarkiteEntity && rider instanceof LivingEntity) {
						event.setCanceled(true);
						((VolarkiteEntity) mount).handleRiderDismount((LivingEntity) rider);
					}
				}
			} finally {
				isMountingEvent = false;
			}
		}
	}
}
