package thebetweenlands.common.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.mobs.EntityDragonFly;
import thebetweenlands.common.network.bidirectional.MessageUpdateCarriagePuller;
import thebetweenlands.common.network.bidirectional.MessageUpdateCarriagePuller.Action;
import thebetweenlands.util.PlayerUtil;

public class EntityWeedwoodDraeton extends Entity {
	public static class Puller {
		public final EntityWeedwoodDraeton carriage;
		public EntityPullerDragonfly dragonfly;

		public final int id; //network ID of the puller, used for sync

		public double prevX, prevY, prevZ;
		public double x, y, z;
		public double motionX, motionY, motionZ;

		private int lerpSteps;
		private double lerpX;
		private double lerpY;
		private double lerpZ;

		public final float width = 0.5f;
		public final float height = 0.5f;

		public boolean isActive = true;

		public Puller(EntityWeedwoodDraeton carriage, int id) {
			this.carriage = carriage;
			this.id = id;
		}

		private AxisAlignedBB getAabb() {
			return new AxisAlignedBB(this.x - this.width / 2, this.y, this.z - this.width / 2, this.x + this.width / 2, this.y + this.height, this.z + this.width / 2);
		}

		private void setPosToAabb(AxisAlignedBB aabb) {
			this.x = aabb.minX + this.width / 2;
			this.y = aabb.minY;
			this.z = aabb.minZ + this.width / 2;
		}

		public void move(double x, double y, double z) {
			List<AxisAlignedBB> collisionBoxes = this.carriage.world.getCollisionBoxes(null, this.getAabb().expand(x, y, z));

			if (y != 0.0D) {
				int k = 0;

				for (int l = collisionBoxes.size(); k < l; ++k) {
					y = ((AxisAlignedBB)collisionBoxes.get(k)).calculateYOffset(this.getAabb(), y);
				}

				this.setPosToAabb(this.getAabb().offset(0.0D, y, 0.0D));
			}

			if (x != 0.0D) {
				int j5 = 0;

				for (int l5 = collisionBoxes.size(); j5 < l5; ++j5) {
					x = ((AxisAlignedBB)collisionBoxes.get(j5)).calculateXOffset(this.getAabb(), x);
				}

				if (x != 0.0D) {
					this.setPosToAabb(this.getAabb().offset(x, 0.0D, 0.0D));
				}
			}

			if (z != 0.0D) {
				int k5 = 0;

				for (int i6 = collisionBoxes.size(); k5 < i6; ++k5) {
					z = ((AxisAlignedBB)collisionBoxes.get(k5)).calculateZOffset(this.getAabb(), z);
				}

				if (z != 0.0D) {
					this.setPosToAabb(this.getAabb().offset(0.0D, 0.0D, z));
				}
			}
		}

		public void tickLerp() {
			if (this.lerpSteps > 0) {
				this.x = this.x + (this.lerpX - this.x) / (double)this.lerpSteps;
				this.y = this.y + (this.lerpY - this.y) / (double)this.lerpSteps;
				this.z = this.z + (this.lerpZ - this.z) / (double)this.lerpSteps;
				--this.lerpSteps;
			}
		}
	}

	public static class EntityPullerDragonfly extends EntityDragonFly implements IEntityAdditionalSpawnData {
		private int carriageId;
		private int pullerId;

		private Puller puller;

		public EntityPullerDragonfly(World world) {
			super(world);
		}

		public EntityPullerDragonfly(World world, EntityWeedwoodDraeton carriage, Puller puller) {
			super(world);
			this.carriageId = carriage.getEntityId();
			this.pullerId = puller.id;
			this.puller = puller;
			puller.dragonfly = this;
		}

		@Override
		public boolean attackEntityFrom(DamageSource source, float amount) {
			if(source == DamageSource.IN_WALL) {
				return false;
			}
			return super.attackEntityFrom(source, amount);
		}

		@Override
		public boolean writeToNBTOptional(NBTTagCompound compound) {
			//Entity is saved and handled by carriage
			return false;
		}

		@Override
		public boolean isAIDisabled() {
			return true;
		}

		@Override
		public void onUpdate() {
			super.onUpdate();

			if(this.puller == null || !this.puller.isActive) {
				if(!this.world.isRemote) {
					//Don't remove immediately if entity is already dying
					if(this.isEntityAlive()) {
						this.setDead();
					}
				} else {
					Entity entity = this.world.getEntityByID(this.carriageId);
					if(entity instanceof EntityWeedwoodDraeton) {
						Puller puller = ((EntityWeedwoodDraeton) entity).getPullerById(this.pullerId);
						if(puller != null) {
							this.puller = puller;
							puller.dragonfly = this;
						}
					}
				}
			} else {
				this.setPositionAndRotation(this.puller.x, this.puller.y, this.puller.z, 0, 0);
				this.rotationYaw = this.rotationYawHead = this.renderYawOffset = (float)Math.toDegrees(Math.atan2(this.puller.motionZ, this.puller.motionX)) - 90;
				this.rotationPitch = (float)Math.toDegrees(-Math.atan2(this.puller.motionY, Math.sqrt(this.puller.motionX * this.puller.motionX + this.puller.motionZ * this.puller.motionZ)));
			}
		}

		@Override
		public void writeSpawnData(ByteBuf buffer) {
			buffer.writeInt(this.carriageId);
			buffer.writeInt(this.pullerId);
		}

		@Override
		public void readSpawnData(ByteBuf buffer) {
			this.carriageId = buffer.readInt();
			this.pullerId = buffer.readInt();
		}
	}

	public List<Puller> pullers = new ArrayList<>();

	private int lerpSteps;
	private double lerpX;
	private double lerpY;
	private double lerpZ;
	private double lerpYaw;
	private double lerpPitch;

	private List<EntityPullerDragonfly> loadedDragonflies = new ArrayList<>();

	private int nextPullerId = 0;

	public float prevRotationRoll, rotationRoll;

	public EntityWeedwoodDraeton(World world) {
		super(world);
		this.setSize(0.75F, 0.75F);
	}

	public Puller getPullerById(int id) {
		for(Puller puller : this.pullers) {
			if(puller.id == id) {
				return puller;
			}
		}
		return null;
	}

	/**
	 * Add puller on client side
	 */
	public Puller addPuller(MessageUpdateCarriagePuller.Position pos) {
		Puller puller = new Puller(this, pos.id);

		puller.lerpX = puller.x = pos.x + this.posX;
		puller.lerpY = puller.y = pos.y + this.posY;
		puller.lerpZ = puller.z = pos.z + this.posZ;

		puller.motionX = pos.mx;
		puller.motionY = pos.my;
		puller.motionZ = pos.mz;

		this.pullers.add(puller);

		return puller;
	}

	/**
	 * Remove puller on client side
	 */
	public boolean removePullerById(int id) {
		Iterator<Puller> it = this.pullers.iterator();
		while(it.hasNext()) {
			Puller puller = it.next();
			if(puller.id == id) {
				puller.isActive = false;
				it.remove();
				return true;
			}
		}
		return false;
	}

	@Override
	protected void entityInit() {
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		this.loadedDragonflies.clear();

		this.pullers.clear();

		NBTTagList list = nbt.getTagList("Pullers", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);

			Puller puller = new Puller(this, this.nextPullerId++);

			puller.x = tag.getDouble("x");
			puller.y = tag.getDouble("y");
			puller.z = tag.getDouble("z");

			if(tag.hasKey("Dragonfly", Constants.NBT.TAG_COMPOUND)) {
				NBTTagCompound dragonflyNbt = tag.getCompoundTag("Dragonfly");

				EntityPullerDragonfly dragonfly = new EntityPullerDragonfly(this.world, this, puller);
				dragonfly.readFromNBT(dragonflyNbt);

				this.loadedDragonflies.add(dragonfly);
			}

			this.pullers.add(puller);
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		NBTTagList list = new NBTTagList();
		for(Puller puller : this.pullers) {
			NBTTagCompound tag = new NBTTagCompound();

			tag.setDouble("x", puller.x);
			tag.setDouble("y", puller.y);
			tag.setDouble("z", puller.z);

			if(puller.dragonfly != null && puller.dragonfly.isEntityAlive()) {
				NBTTagCompound dragonflyNbt = new NBTTagCompound();

				puller.dragonfly.writeToNBT(dragonflyNbt);

				tag.setTag("Dragonfly", dragonflyNbt);
			}

			list.appendTag(tag);
		}
		nbt.setTag("Pullers", list);
	}

	@Override
	public double getMountedYOffset() {
		return 0;
	}

	@Override
	public void onAddedToWorld() {
		super.onAddedToWorld();

		//Spawn dragonflies that were loaded from nbt
		if(!this.world.isRemote) {
			for(EntityPullerDragonfly dragonfly : this.loadedDragonflies) {
				this.world.spawnEntity(dragonfly);
			}

			this.loadedDragonflies.clear();
		}
	}

	@Override
	public void onEntityUpdate() {
		if(!this.world.isRemote) {
			Iterator<Puller> it = this.pullers.iterator();
			while(it.hasNext()) {
				Puller puller = it.next();
				if(puller.dragonfly == null || !puller.dragonfly.isEntityAlive()) {
					puller.isActive = false;
					it.remove();

					TheBetweenlands.networkWrapper.sendToAllTracking(new MessageUpdateCarriagePuller(this, puller, Action.REMOVE), this);
				}
			}
		}

		for(int i = 0; i < this.pullers.size(); i++) {
			Puller puller = this.pullers.get(i);
			puller.prevX = puller.x;
			puller.prevY = puller.y;
			puller.prevZ = puller.z;
		}

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		float friction = 0.98f;

		this.motionY *= friction;
		this.motionX *= friction;
		this.motionZ *= friction;

		this.handleWaterMovement();
		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		this.pushOutOfBlocks(this.posX, this.posY, this.posZ);

		if(this.canPassengerSteer()) {
			Entity controller = this.getControllingPassenger();

			if(this.world.isRemote && controller instanceof EntityLivingBase) {
				controller.fallDistance = 0;

				this.handleControllerMovement((EntityLivingBase) controller);
			}

			this.updateCarriage();

			if(!this.world.isRemote) {
				this.lerpX = this.posX;
				this.lerpY = this.posY;
				this.lerpZ = this.posZ;
				this.lerpYaw = this.rotationYaw;
				this.lerpPitch = this.rotationPitch;

				for(Puller puller : this.pullers) {
					puller.lerpX = puller.x;
					puller.lerpY = puller.y;
					puller.lerpZ = puller.z;
				}
			}
		} else if(this.world.isRemote) {
			this.motionX = this.motionY = this.motionZ = 0;

			for(Puller puller : this.pullers) {
				puller.tickLerp();
			}
		}

		if(!this.world.isRemote && this.getPassengers().isEmpty()) {
			this.motionY -= 0.005f;
		}

		if(this.world instanceof WorldServer) {
			//Send server state of pullers to non-controller players
			if(this.ticksExisted % 10 == 0) {
				for(Puller puller : this.pullers) {
					MessageUpdateCarriagePuller msg = new MessageUpdateCarriagePuller(this, puller, MessageUpdateCarriagePuller.Action.UPDATE);

					Set<? extends EntityPlayer> tracking = ((WorldServer) this.world).getEntityTracker().getTrackingPlayers(this);
					for(EntityPlayer player : tracking) {
						//Don't send to controller
						if(player instanceof EntityPlayerMP && player != this.getControllingPassenger()) {
							TheBetweenlands.networkWrapper.sendTo(msg, (EntityPlayerMP) player);
						}
					}
				}
			}
		}

		this.firstUpdate = false;
	}

	@Override
	public void onUpdate() {
		this.prevRotationRoll = this.rotationRoll;

		super.onUpdate();

		double dx = this.motionX;
		double dz = this.motionZ;
		float targetYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
		float yawOffset = (float) MathHelper.wrapDegrees(targetYaw - this.rotationYaw);
		this.rotationYaw = this.rotationYaw + yawOffset * 0.98f;

		float targetRoll = this.rotationRoll = MathHelper.clamp(yawOffset * 10.0f, -20, 20);
		float rollOffset = (float) MathHelper.wrapDegrees(targetRoll - this.rotationRoll);
		this.rotationRoll = this.rotationRoll + rollOffset * 0.75f;

		this.tickLerp();
	}

	private void tickLerp() {
		if (this.lerpSteps > 0 && !this.canPassengerSteer()) {
			double x = this.posX + (this.lerpX - this.posX) / this.lerpSteps;
			double y = this.posY + (this.lerpY - this.posY) / this.lerpSteps;
			double z = this.posZ + (this.lerpZ - this.posZ) / this.lerpSteps;

			//If the carriage is player controlled pull the pullers along
			//so they don't lag behind
			if(this.getControllingPassenger() != null) {
				double dx = x - this.posX;
				double dy = y - this.posY;
				double dz = z - this.posZ;

				for(Puller puller : this.pullers) {
					puller.x += dx;
					puller.y += dy;
					puller.z += dz;
					puller.lerpX += dx;
					puller.lerpY += dy;
					puller.lerpZ += dz;
				}
			}

			double yaw = MathHelper.wrapDegrees(this.lerpYaw - this.rotationYaw);
			this.rotationYaw = (float)(this.rotationYaw + yaw / this.lerpSteps);
			this.rotationPitch = (float)(this.rotationPitch + (this.lerpPitch - this.rotationPitch) / this.lerpSteps);

			--this.lerpSteps;

			this.setPosition(x, y, z);
			this.setRotation(this.rotationYaw, this.rotationPitch);
		}
	}

	@Override
	public void updatePassenger(Entity passenger) {
		super.updatePassenger(passenger);

		PlayerUtil.resetFloating(passenger);
		PlayerUtil.resetVehicleFloating(passenger);
	}

	@Override
	public void move(MoverType type, double x, double y, double z) {
		double startX = this.posX;
		double startY = this.posY;
		double startZ = this.posZ;

		super.move(type, x, y, z);

		float drag = 0.25f;

		for(Puller puller : this.pullers) {
			puller.move((this.posX - startX) * drag, (this.posY - startY) * drag, (this.posZ - startZ) * drag);
		}
	}

	public void setPacketRelativePullerPosition(Puller puller, float x, float y, float z, float mx, float my, float mz) {
		Entity entity = this.getControllingPassenger();

		//Only set position for non-controlling watching players
		if (entity instanceof EntityPlayer == false || !((EntityPlayer)entity).isUser()) {
			if(this.world.isRemote) {
				//interpolate on client side
				puller.lerpX = this.lerpX + x;
				puller.lerpY = this.lerpY + y;
				puller.lerpZ = this.lerpZ + z;
				puller.lerpSteps = 10;
			} else {
				puller.x = this.posX + x;
				puller.y = this.posY + y;
				puller.z = this.posZ + z;
			}

			puller.motionX = mx;
			puller.motionY = my;
			puller.motionZ = mz;
		}
	}

	protected void handleControllerMovement(EntityLivingBase controller) {
		double dx = 0;
		double dz = 0;

		boolean input = false;

		if(controller.moveForward > 0) {
			dx += Math.cos(Math.toRadians(controller.rotationYaw + 90));
			dz += Math.sin(Math.toRadians(controller.rotationYaw + 90));
			input = true;
		}
		if(controller.moveForward < 0) {
			dx += Math.cos(Math.toRadians(controller.rotationYaw - 90));
			dz += Math.sin(Math.toRadians(controller.rotationYaw - 90));
			input = true;
		}
		if(controller.moveStrafing > 0) {
			dx += Math.cos(Math.toRadians(controller.rotationYaw));
			dz += Math.sin(Math.toRadians(controller.rotationYaw));
			input = true;
		} 
		if(controller.moveStrafing < 0){
			dx += Math.cos(Math.toRadians(controller.rotationYaw + 180));
			dz += Math.sin(Math.toRadians(controller.rotationYaw + 180));
			input = true;
		}

		if(input) {
			Vec3d dir = new Vec3d(dx, Math.sin(Math.toRadians(-controller.rotationPitch)), dz).normalize();

			double moveStrength = 0.1D;

			for(Puller puller : this.pullers) {
				puller.motionX += dir.x * moveStrength * (this.rand.nextFloat() * 0.6f + 0.7f);
				puller.motionZ += dir.z * moveStrength * (this.rand.nextFloat() * 0.6f + 0.7f);
				puller.motionY += dir.y * moveStrength * (this.rand.nextFloat() * 0.6f + 0.7f);
			}
		}
	}

	protected void updateCarriage() {
		float pullerFriction = 0.9f;

		for(Puller puller : this.pullers) {
			puller.motionX *= pullerFriction;
			puller.motionY *= pullerFriction;
			puller.motionZ *= pullerFriction;

			if(puller.dragonfly != null && puller.dragonfly.getRidingEntity() != null) {
				puller.motionX = puller.motionY = puller.motionZ = 0;

				puller.x = puller.dragonfly.posX;
				puller.y = puller.dragonfly.posY;
				puller.z = puller.dragonfly.posZ;

			} else {
				float speed = (float) Math.sqrt(puller.motionX * puller.motionX + puller.motionY * puller.motionY + puller.motionZ * puller.motionZ);
				float maxSpeed = this.getMaxPullerSpeed();
				if(speed > maxSpeed) {
					puller.motionX *= 1.0f / speed * maxSpeed;
					puller.motionY *= 1.0f / speed * maxSpeed;
					puller.motionZ *= 1.0f / speed * maxSpeed;
				}

				puller.move(puller.motionX, puller.motionY, puller.motionZ);
			}

			Vec3d pullerPos = new Vec3d(puller.x, puller.y, puller.z);

			for(Puller otherPuller : this.pullers) {
				Vec3d otherPullerPos = new Vec3d(otherPuller.x, otherPuller.y, otherPuller.z);

				Vec3d diff = pullerPos.subtract(otherPullerPos);

				double dist = diff.length();

				float minDist = 1.5f;

				if(dist < minDist) {
					float pushStr = 0.75f;

					puller.motionX += diff.x * (minDist - dist) / minDist * pushStr;
					puller.motionY += diff.y * (minDist - dist) / minDist * pushStr;
					puller.motionZ += diff.z * (minDist - dist) / minDist * pushStr;
				}
			}

			Vec3d tether = new Vec3d(puller.x, puller.y, puller.z);

			Vec3d pos = new Vec3d(this.posX, this.posY, this.posZ);

			Vec3d diff = tether.subtract(pos);

			double dist = diff.length();

			float tetherLength = this.getMaxTetherLength();

			//Teleport puller to carriage if it gets too far away
			//somehow
			if(dist > tetherLength + 3) {
				puller.lerpX = puller.x = this.posX;
				puller.lerpY = puller.y = this.posY;
				puller.lerpZ = puller.z = this.posZ;
				dist = 0;
			}

			if(dist > tetherLength) {
				float pullStrength = 0.01f;

				Vec3d motion = diff.normalize().scale((dist - tetherLength) * pullStrength);
				this.motionX += motion.x;
				this.motionY += motion.y;
				this.motionZ += motion.z;

				Vec3d constrainedTetherPos = pos.add(diff.normalize().scale(tetherLength));

				puller.move(constrainedTetherPos.x - puller.x, constrainedTetherPos.y - puller.y, constrainedTetherPos.z - puller.z);

				puller.motionX -= motion.x;
				puller.motionY -= motion.y;
				puller.motionZ -= motion.z;
			}
		}

		//Send client state of pullers to server
		if(this.world.isRemote && this.canPassengerSteer() && this.ticksExisted % 10 == 0) {
			for(Puller puller : this.pullers) {
				TheBetweenlands.networkWrapper.sendToServer(new MessageUpdateCarriagePuller(this, puller, MessageUpdateCarriagePuller.Action.UPDATE));
			}
		}
	}

	public float getMaxTetherLength() {
		return 6.0f;
	}

	public float getMaxPullerSpeed() {
		return 3.0f;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
		this.lerpX = x;
		this.lerpY = y;
		this.lerpZ = z;
		this.lerpYaw = (double)yaw;
		this.lerpPitch = (double)pitch;
		this.lerpSteps = 10;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double distance) {
		return distance < 4096.0D;
	}

	@Override
	@Nullable
	public Entity getControllingPassenger() {
		return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
	}

	@Override
	public boolean shouldRiderSit() {
		return true;
	}

	@Override
	public void fall(float distance, float damageMultiplier) {
		//No fall damage to node or rider
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if(!this.world.isRemote && hand == EnumHand.MAIN_HAND) {
			if(!player.isSneaking()) {
				player.startRiding(this);
			} else {
				//Debug

				Puller puller = new Puller(this, this.nextPullerId++);
				puller.lerpX = puller.x = this.posX;
				puller.lerpY = puller.y = this.posY;
				puller.lerpZ = puller.z = this.posZ;
				this.pullers.add(puller);

				//Spawn puller dragonfly
				EntityPullerDragonfly dragonfly = new EntityPullerDragonfly(this.world, this, puller);
				dragonfly.setLocationAndAngles(this.posX, this.posY, this.posZ, 0, 0);
				this.world.spawnEntity(dragonfly);
			}
			return true;
		}
		return false;
	}
}
