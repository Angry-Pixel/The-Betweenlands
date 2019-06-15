package thebetweenlands.common.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityVolarkite extends Entity {
	public float prevRotationRoll;
	public float rotationRoll;
	
	public EntityVolarkite(World world) {
		super(world);
		this.setSize(0.6F, 1.7F);
	}

	@Override
	protected void entityInit() {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
	}

	@Override
	public double getMountedYOffset() {
		return 0.325D;
	}

	@Override
	public void onEntityUpdate() {
		if(this.ticksExisted < 2) {
			//Stupid EntityTrackerEntry is broken and desyncs server position.
			//Tracker updates server side position but *does not* send the change to the client
			//when tracker.updateCounter == 0, causing a desync until the next force teleport
			//packet.......
			//By not moving the entity until then it works.
			return;
		}

		this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationRoll = this.rotationRoll;

		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		this.handleWaterMovement();

		double targetMotionY = -0.04D;
		
		this.motionY = targetMotionY + (this.motionY - targetMotionY) * 0.92D;

		float invFriction = 1.0F;
		
		if(this.onGround) {
			invFriction = 0.8F;
		}
		
		this.motionX *= invFriction;
		this.motionY *= invFriction;
		this.motionZ *= invFriction;
		
		Entity controller = this.getControllingPassenger();

		Vec3d kiteDir = new Vec3d(Math.cos(Math.toRadians(this.rotationYaw + 90)), 0, Math.sin(Math.toRadians(this.rotationYaw + 90)));
		
		double rotIncr = 0;
		
		if(controller != null) {
			if(this.motionY < 0 && !this.onGround) {
				double speedBoost = -this.motionY * 0.1D + MathHelper.clamp(Math.sin(Math.toRadians(this.rotationPitch)) * 0.5F, -0.02D, 0.02D);
				
				this.motionX += kiteDir.x * (speedBoost + 0.01D);
				this.motionZ += kiteDir.z * (speedBoost + 0.01D);
			}
			
			Vec3d controllerDir = new Vec3d(Math.cos(Math.toRadians(controller.rotationYaw + 90)), 0, Math.sin(Math.toRadians(controller.rotationYaw + 90)));
			double rotDiff = Math.toDegrees(Math.acos(kiteDir.dotProduct(controllerDir))) * -Math.signum(kiteDir.crossProduct(controllerDir).y);
			rotIncr = MathHelper.clamp(rotDiff * 0.05D, -1.0D, 1.0D);
			this.rotationYaw += rotIncr;
			
			if(!this.onGround && controller instanceof EntityLivingBase) {
				float forward = ((EntityLivingBase) controller).moveForward;
				if(forward > 0.1F) {
					this.rotationPitch = 20.0F + (this.rotationPitch - 20.0F) * 0.9F;
				} else if(forward < -0.1F) {
					this.rotationPitch = -20.0F + (this.rotationPitch + 20.0F) * 0.9F;
				}
			}
		}
		
		if(!this.onGround && Math.abs(rotIncr) > 0.1D) {
			this.rotationRoll = (float) (rotIncr * 15 + (this.rotationRoll - rotIncr * 15) * 0.9D);
		} else {
			this.rotationRoll *= 0.9F;
		}
		
		this.rotationPitch *= 0.9F;
		
		double speed = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
		
		if(speed > 0.1D) {
			double dx = this.motionX / speed;
			double dz = this.motionZ / speed;
			
			this.motionX = (kiteDir.x + (dx - kiteDir.x) * 0.9D) * speed;
			this.motionZ = (kiteDir.z + (dz - kiteDir.z) * 0.9D) * speed;
			
			double maxSpeed = 0.6D;
			if(speed > maxSpeed) {
				double targetX = dx * maxSpeed;
				double targetZ = dz * maxSpeed;
				
				this.motionX = targetX + (this.motionX - targetX) * 0.8D;
				this.motionZ = targetZ + (this.motionZ - targetZ) * 0.8D;
			}
		}

		if(!this.world.isRemote && controller == null) {
			this.onKillCommand();
		}

		this.firstUpdate = false;
	}

	@Override
	@Nullable
	public Entity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
	}

	@Override
	public boolean canPassengerSteer() {
		return false;
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	@Override
	protected void removePassenger(Entity passenger) {
		super.removePassenger(passenger);

		passenger.motionX = this.motionX;
		passenger.motionY = this.motionY;
		passenger.motionZ = this.motionZ;
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		//No fall damage to node or rider
	}
}
