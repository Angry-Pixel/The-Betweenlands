package thebetweenlands.common.entity.draeton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.network.bidirectional.MessageUpdateCarriagePuller;
import thebetweenlands.common.network.bidirectional.MessageUpdateCarriagePuller.Action;
import thebetweenlands.util.Matrix;
import thebetweenlands.util.PlayerUtil;

public class EntityDraeton extends Entity implements IInventory, IEntityMultiPart {
	public static class Puller {
		public final EntityDraeton carriage;
		private Entity entity;

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

		public Puller(EntityDraeton carriage, int id) {
			this.carriage = carriage;
			this.id = id;
		}

		public void setEntity(IPullerEntity entity) {
			if(entity instanceof Entity) {
				this.entity = (Entity) entity;
			}
		}

		@SuppressWarnings("unchecked")
		public <T extends Entity & IPullerEntity> T getEntity() {
			return (T) this.entity;
		}

		public AxisAlignedBB getAabb() {
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

	private static class InteractionPart extends MultiPartEntityPart {
		private final EntityDraeton draeton;

		private InteractionPart(EntityDraeton draeton) {
			super(draeton, "inventory", 0.6f, 0.5f);
			this.draeton = draeton;
		}

		@Override
		public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
			return this.draeton.interactFromMultipart(this, player, hand);
		}
	}

	public static interface IPullerEntity {
		public void setPuller(EntityDraeton carriage, Puller puller);

		public float getPull(float pull);

		public float getCarriageDrag(float drag);

		public float getDrag(float drag);

		public void releaseEntity();
	}

	private static final DataParameter<Integer> TIME_SINCE_HIT = EntityDataManager.<Integer>createKey(EntityDraeton.class, DataSerializers.VARINT);
	private static final DataParameter<Float> DAMAGE_TAKEN = EntityDataManager.<Float>createKey(EntityDraeton.class, DataSerializers.FLOAT);

	private Vec3d prevBalloonPos = Vec3d.ZERO;
	private Vec3d balloonPos = Vec3d.ZERO;
	private Vec3d balloonMotion = Vec3d.ZERO;

	private NonNullList<ItemStack> inventory;

	public List<Puller> pullers = new ArrayList<>();

	private int lerpSteps;
	private double lerpX;
	private double lerpY;
	private double lerpZ;
	private double lerpYaw;
	private double lerpPitch;

	private List<Entity> loadedPullerEntities = new ArrayList<>();

	private int nextPullerId = 0;

	public float prevRotationRoll, rotationRoll;

	private boolean descend = false;

	private final InteractionPart[] parts;

	public EntityDraeton(World world) {
		super(world);
		this.setSize(1.5F, 1.0F);
		this.inventory = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);
		this.parts = new InteractionPart[]{ new InteractionPart(this) };
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
		this.dataManager.register(TIME_SINCE_HIT, 0);
		this.dataManager.register(DAMAGE_TAKEN, 0.0f);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		//Initialise lerp to real position
		this.lerpX = this.posX;
		this.lerpY = this.posY;
		this.lerpZ = this.posZ;
		this.lerpYaw = this.rotationYaw;
		this.lerpPitch = this.rotationPitch;

		this.loadedPullerEntities.clear();

		this.pullers.clear();

		NBTTagList list = nbt.getTagList("Pullers", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);

			Puller puller = new Puller(this, this.nextPullerId++);

			Vec3d pullerPos = new Vec3d(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));

			final Vec3d pos = new Vec3d(this.posX, this.posY, this.posZ).add(this.getPullPoint(i, 1));

			//Ensure that the puller is within valid range, otherwise puller entities may become immediately unloaded after spawning
			Vec3d diff = pullerPos.subtract(pos);
			if(diff.length() > this.getMaxTetherLength()) {
				pullerPos = pos.add(diff.normalize().scale(this.getMaxTetherLength()));
			}

			puller.x = pullerPos.x;
			puller.y = pullerPos.y;
			puller.z = pullerPos.z;

			if(tag.hasKey("Entity", Constants.NBT.TAG_COMPOUND)) {
				NBTTagCompound entityNbt = tag.getCompoundTag("Entity");

				Entity entity = EntityList.createEntityFromNBT(entityNbt, this.world);

				if(entity instanceof IPullerEntity) {
					((IPullerEntity) entity).setPuller(this, puller);
					puller.setEntity((IPullerEntity) entity);

					entity.setPosition(puller.x, puller.y, puller.z);

					this.loadedPullerEntities.add(entity);
				} else {
					entity.setDead();
				}
			}

			this.pullers.add(puller);
		}

		inventory = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
		if (nbt.hasKey("Items", 9))
			ItemStackHelper.loadAllItems(nbt, inventory);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		NBTTagList list = new NBTTagList();
		for(Puller puller : this.pullers) {
			NBTTagCompound tag = new NBTTagCompound();

			tag.setDouble("x", puller.x);
			tag.setDouble("y", puller.y);
			tag.setDouble("z", puller.z);

			if(puller.entity != null && puller.entity.isEntityAlive()) {
				ResourceLocation id = EntityList.getKey(puller.entity);

				if(id != null) {
					NBTTagCompound entityNbt = new NBTTagCompound();

					puller.entity.writeToNBT(entityNbt);

					entityNbt.setString("id", id.toString());

					tag.setTag("Entity", entityNbt);
				}
			}

			list.appendTag(tag);
		}
		nbt.setTag("Pullers", list);

		ItemStackHelper.saveAllItems(nbt, inventory, false);
	}

	@Override
	public double getMountedYOffset() {
		return 0.3D;
	}

	@Override
	protected boolean canFitPassenger(Entity passenger) {
		return this.getPassengers().size() < 2;
	}

	@Override
	public void onAddedToWorld() {
		super.onAddedToWorld();

		//Initialise lerp to real position
		this.lerpX = this.posX;
		this.lerpY = this.posY;
		this.lerpZ = this.posZ;
		this.lerpYaw = this.rotationYaw;
		this.lerpPitch = this.rotationPitch;

		this.prevBalloonPos = this.balloonPos = this.getPositionVector().add(0, 2, 0);
	}

	public Vec3d getBalloonPos(float partialTicks) {
		return this.prevBalloonPos.add(this.balloonPos.subtract(this.prevBalloonPos).scale(partialTicks));
	}

	public Vec3d getCarriageRopeConnection(int i, float partialTicks) {
		Vec3d connectionPoint;
		switch(i) {
		default:
		case 4:
		case 0:
			connectionPoint = this.getRotatedPoint(new Vec3d(0.6f, 1.05f, 0.65f), partialTicks);
			break;
		case 5:
		case 1:
			connectionPoint = this.getRotatedPoint(new Vec3d(-0.6f, 1.05f, 0.65f), partialTicks);
			break;
		case 6:
		case 2:
			connectionPoint = this.getRotatedPoint(new Vec3d(0.6f, 1.05f, -0.65f), partialTicks);
			break;
		case 7:
		case 3:
			connectionPoint = this.getRotatedPoint(new Vec3d(-0.6f, 1.05f, -0.65f), partialTicks);
			break;
		}
		return connectionPoint;
	}

	public Vec3d getBalloonRopeConnection(int i, float partialTicks) {
		Matrix mat = new Matrix();
		mat.rotate((float)-Math.toRadians(this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * partialTicks), 0, 1, 0);
		mat.rotate((float)-Math.toRadians(this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks), 1, 0, 0);
		mat.rotate((float)Math.toRadians(this.prevRotationRoll + (this.rotationRoll - this.prevRotationRoll) * partialTicks), 0, 0, 1);

		Vec3d balloonPos = this.getBalloonPos(partialTicks);
		switch(i) {
		default:
		case 0:
			balloonPos = balloonPos.add(mat.transform(new Vec3d(1.0f, -0.425f, 1.5f)));
			break;
		case 1:
			balloonPos = balloonPos.add(mat.transform(new Vec3d(-1.0f, -0.425f, 1.5f)));
			break;
		case 2:
			balloonPos = balloonPos.add(mat.transform(new Vec3d(1.0f, -0.425f, -1.5f)));
			break;
		case 3:
			balloonPos = balloonPos.add(mat.transform(new Vec3d(-1.0f, -0.425f, -1.5f)));
			break;
		case 4:
			balloonPos = balloonPos.add(mat.transform(new Vec3d(1.0f, -0.35f, 0.7f)));
			break;
		case 5:
			balloonPos = balloonPos.add(mat.transform(new Vec3d(-1.0f, -0.35f, 0.7f)));
			break;
		case 6:
			balloonPos = balloonPos.add(mat.transform(new Vec3d(1.0f, -0.35f, -0.7f)));
			break;
		case 7:
			balloonPos = balloonPos.add(mat.transform(new Vec3d(-1.0f, -0.35f, -0.7f)));
			break;
		}
		return balloonPos;
	}

	@Override
	public void onEntityUpdate() {
		if(!this.world.isRemote) {
			//Spawn puller entities that were loaded from nbt
			Iterator<Entity> entityIt = this.loadedPullerEntities.iterator();
			while(entityIt.hasNext()) {
				Entity entity = entityIt.next();

				//Spawning can fail if a chunk isn't loaded yet so keep trying until it works
				if(this.world.spawnEntity(entity)) {
					entityIt.remove();
				}
			}

			//Remove dead pullers
			Iterator<Puller> it = this.pullers.iterator();
			while(it.hasNext()) {
				Puller puller = it.next();

				if(puller.entity == null || !puller.entity.isEntityAlive()) {
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

		float drag = 0.98f;
		for(Puller puller : this.pullers) {
			if(puller.getEntity() != null) {
				drag = puller.getEntity().getCarriageDrag(drag);
			}
		}

		this.motionY *= drag;
		this.motionX *= drag;
		this.motionZ *= drag;

		this.handleWaterMovement();
		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		this.pushOutOfBlocks(this.posX, this.posY, this.posZ);

		Vec3d partPos = this.getRotatedPoint(new Vec3d(0.7f, 0.8f, 0), 1).add(this.posX, this.posY, this.posZ);
		this.parts[0].onUpdate();
		this.parts[0].setPosition(partPos.x, partPos.y - this.parts[0].height / 2, partPos.z);

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

		if(!this.world.isRemote && (this.getPassengers().isEmpty() || this.pullers.isEmpty())) {
			this.motionY -= 0.005f;
		}

		if(this.world.isRemote) {
			this.balloonMotion = this.balloonMotion.add(0, 0.125f, 0).scale(0.9f);

			this.prevBalloonPos = this.balloonPos;
			this.balloonPos = this.balloonPos.add(this.balloonMotion);

			for(int i = 0; i < 8; i++) {
				Vec3d balloonConnection = this.getBalloonRopeConnection(i, 1);
				Vec3d tetherPos = this.getPositionVector().add(this.getCarriageRopeConnection(i, 1));

				Vec3d diff = balloonConnection.subtract(tetherPos);

				float tetherLength = 2.0f + (float)Math.sin(this.ticksExisted * 0.1f) * 0.05f;

				if(diff.length() > 6.0f) {
					this.balloonPos = this.getPositionVector().add(0, 1, 0);
				} else if(diff.length() > tetherLength) {
					Vec3d correction = diff.normalize().scale(-(diff.length() - tetherLength));
					this.balloonPos = this.balloonPos.add(correction.scale(0.75f));

					this.balloonMotion = this.balloonMotion.add(correction.scale(1.25f));
				}
			}
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

		if(this.getControllingPassenger() == null) {
			this.descend = false;
		}

		if (this.getTimeSinceHit() > 0) {
			this.setTimeSinceHit(this.getTimeSinceHit() - 1);
		}

		if (this.getDamageTaken() > 0.0F) {
			this.setDamageTaken(this.getDamageTaken() - 1.0F);
		}

		super.onUpdate();

		double dx = this.motionX;
		double dz = this.motionZ;
		double dy = this.motionY;

		float targetYaw;
		float targetPitch;
		float targetRoll;

		float adjustStrength = 0.1f;

		float speed = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);

		if(speed > 0.1f) {
			targetYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
			targetPitch = MathHelper.clamp((float) -Math.toDegrees(Math.atan2(Math.sqrt(dx * dx + dz * dz), dy)) + 90, -30, 30);
			targetRoll = MathHelper.clamp(MathHelper.wrapDegrees(targetYaw - this.rotationYaw) * 10.0f, -20, 20);
			adjustStrength = MathHelper.clamp(speed * 0.5f, 0.05f, 0.5f);
		} else {
			targetYaw = this.rotationYaw;
			targetPitch = 0.0f;
			targetRoll = 0.0f;
		}

		float yawOffset = (float) MathHelper.wrapDegrees(targetYaw - this.rotationYaw);
		this.rotationYaw = this.rotationYaw + yawOffset * adjustStrength;

		float pitchOffset = (float) MathHelper.wrapDegrees(targetPitch - this.rotationPitch);
		this.rotationPitch = this.rotationPitch + pitchOffset * adjustStrength * 0.5f;

		float rollOffset = (float) MathHelper.wrapDegrees(targetRoll - this.rotationRoll);
		this.rotationRoll = this.rotationRoll + rollOffset * adjustStrength * 0.5f;

		this.tickLerp();
	}

	private void tickLerp() {
		if (this.lerpSteps > 0 && this.world.isRemote && !this.canPassengerSteer()) {
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
		if(this.isPassenger(passenger)) {
			int index = this.getPassengers().indexOf(passenger);

			float yOff = 0.5f;
			Vec3d mountOffset;

			if(index <= 1) {
				mountOffset = this.getRotatedPoint(new Vec3d(0, yOff, 0.25f - index * 0.75f), 1);
			} else {
				mountOffset = this.getRotatedPoint(new Vec3d(0, yOff, 0), 1);
			}

			passenger.setPosition(this.posX + mountOffset.x, this.posY + mountOffset.y - yOff + this.getMountedYOffset() + passenger.getYOffset(), this.posZ + mountOffset.z);
		}

		if(passenger == this.getControllingPassenger()) {
			this.descend = passenger.isSprinting();
		}
		passenger.setSprinting(false);

		PlayerUtil.resetFloating(passenger);
		PlayerUtil.resetVehicleFloating(passenger);
	}

	@Override
	public void move(MoverType type, double x, double y, double z) {
		double startX = this.posX;
		double startY = this.posY;
		double startZ = this.posZ;

		super.move(type, x, y, z);

		for(Puller puller : this.pullers) {
			float drag = puller.getEntity() != null ? puller.getEntity().getDrag(0.25f) : 0.25f;

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
				puller.lerpX = puller.x = this.posX + x;
				puller.lerpY = puller.y = this.posY + y;
				puller.lerpZ = puller.z = this.posZ + z;
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
			Vec3d dir = new Vec3d(dx, Math.sin(Math.toRadians(MathHelper.clamp(/*-controller.rotationPitch + */(controller.isJumping ? 45 : 0) + (this.descend ? -45 : 0), -90, 90))), dz).normalize();

			double moveStrength = 0.1D;

			for(Puller puller : this.pullers) {
				puller.motionX += dir.x * moveStrength * (this.rand.nextFloat() * 0.6f + 0.7f);
				puller.motionZ += dir.z * moveStrength * (this.rand.nextFloat() * 0.6f + 0.7f);
				puller.motionY += dir.y * moveStrength * (this.rand.nextFloat() * 0.6f + 0.7f);
			}
		}
	}

	protected void updateCarriage() {
		int pullerIndex = 0;
		for(Puller puller : this.pullers) {
			float pullerDrag = puller.getEntity() != null ? puller.getEntity().getDrag(0.9f) : 0.9f;

			puller.motionX *= pullerDrag;
			puller.motionY *= pullerDrag;
			puller.motionZ *= pullerDrag;

			if(puller.entity != null && puller.entity.getRidingEntity() != null) {
				puller.motionX = puller.motionY = puller.motionZ = 0;

				puller.x = puller.entity.posX;
				puller.y = puller.entity.posY;
				puller.z = puller.entity.posZ;
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

			Vec3d pos = new Vec3d(this.posX, this.posY, this.posZ).add(this.getPullPoint(pullerIndex, 1));

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
				if(puller.getEntity() != null) {
					float pullStrength = puller.getEntity().getPull(0.01f);

					Vec3d motion = diff.normalize().scale((dist - tetherLength) * pullStrength);
					this.motionX += motion.x;
					this.motionY += motion.y;
					this.motionZ += motion.z;
				}

				Vec3d constrainedTetherPos = pos.add(diff.normalize().scale(tetherLength));

				puller.move(constrainedTetherPos.x - puller.x, constrainedTetherPos.y - puller.y, constrainedTetherPos.z - puller.z);

				Vec3d correction = diff.normalize().scale((dist - tetherLength) * 0.01f);

				puller.motionX -= correction.x;
				puller.motionY -= correction.y;
				puller.motionZ -= correction.z;
			}

			pullerIndex++;
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

	public Vec3d getPullPoint(int index, float partialTicks) {
		Vec3d basePoint;
		switch(index % 3) {
		default:
		case 0:
			//middle
			basePoint = new Vec3d(0, 0.875f, 1.75f);
			break;
		case 1:
			//right
			basePoint = new Vec3d(0.6f, 0.75f, 1.55f);
			break;
		case 2:
			//left
			basePoint = new Vec3d(-0.6f, 0.75f, 1.55f);
			break;
		}
		return this.getRotatedPoint(basePoint, partialTicks);
	}

	public Vec3d getRotatedPoint(Vec3d pos, float partialTicks) {
		Matrix mat = new Matrix();
		mat.translate(0, 1.5f, 0);
		mat.rotate((float)-Math.toRadians(this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * partialTicks), 0, 1, 0);
		mat.rotate((float)-Math.toRadians(this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks), 1, 0, 0);
		mat.rotate((float)Math.toRadians(this.prevRotationRoll + (this.rotationRoll - this.prevRotationRoll) * partialTicks), 0, 0, 1);
		mat.translate(0, -1.5f, 0);
		return mat.transform(pos);
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
	public boolean canBePushed() {
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
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.isEntityInvulnerable(source)) {
			return false;
		} else if (!this.world.isRemote && !this.isDead) {
			if (source instanceof EntityDamageSourceIndirect && source.getTrueSource() != null && this.isPassenger(source.getTrueSource())) {
				return false;
			} else {
				this.setTimeSinceHit(10);
				this.setDamageTaken(this.getDamageTaken() + amount * 10.0F);

				boolean isCreative = source.getTrueSource() instanceof EntityPlayer && ((EntityPlayer)source.getTrueSource()).capabilities.isCreativeMode;

				if (isCreative || this.getDamageTaken() > 40.0F) {
					if (!isCreative && this.world.getGameRules().getBoolean("doEntityDrops")) {
						//TODO Drop item
					}

					for(Puller puller : this.pullers) {
						if(puller.getEntity() != null) {
							puller.getEntity().releaseEntity();
						}
					}

					this.setDead();
				}

				return true;
			}
		} else {
			return true;
		}
	}

	@Override
	public boolean canRiderInteract() {
		//TODO Handle with multipart
		return false;
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.inventory.getCurrentItem();
		if(!this.world.isRemote && hand == EnumHand.MAIN_HAND) {
			if(!player.isSneaking() && !player.isRidingSameEntity(this)) {
				player.startRiding(this);
			} else {
				//Debug

				Puller puller = new Puller(this, this.nextPullerId++);
				puller.lerpX = puller.x = this.posX;
				puller.lerpY = puller.y = this.posY;
				puller.lerpZ = puller.z = this.posZ;
				this.pullers.add(puller);

				if (!stack.isEmpty()) {
					if (EnumItemMisc.DRAGONFLY_WING.isItemOf(stack)) {
						EntityPullerDragonfly dragonfly = new EntityPullerDragonfly(this.world, this, puller);
						puller.setEntity(dragonfly);
						dragonfly.setLocationAndAngles(this.posX, this.posY, this.posZ, 0, 0);
						this.world.spawnEntity(dragonfly);
					}

					/*Spawn puller entity
				switch(this.world.rand.nextInt(3)) {
				default:
				case 0:
					EntityPullerDragonfly dragonfly = new EntityPullerDragonfly(this.world, this, puller);
					puller.setEntity(dragonfly);
					dragonfly.setLocationAndAngles(this.posX, this.posY, this.posZ, 0, 0);
					this.world.spawnEntity(dragonfly);
					break;
				case 1:
					EntityPullerFirefly firefly = new EntityPullerFirefly(this.world, this, puller);
					puller.setEntity(firefly);
					firefly.setLocationAndAngles(this.posX, this.posY, this.posZ, 0, 0);
					this.world.spawnEntity(firefly);
					break;
				case 2:
					EntityPullerChiromaw chiromaw = new EntityPullerChiromaw(this.world, this, puller);
					puller.setEntity(chiromaw);
					chiromaw.setLocationAndAngles(this.posX, this.posY, this.posZ, 0, 0);
					this.world.spawnEntity(chiromaw);
					break;
					 */
				}
			}
			return true;
		}
		return false;
	}

	public void setDamageTaken(float damageTaken) {
		this.dataManager.set(DAMAGE_TAKEN, damageTaken);
	}

	public float getDamageTaken() {
		return this.dataManager.get(DAMAGE_TAKEN);
	}

	public void setTimeSinceHit(int timeSinceHit) {
		this.dataManager.set(TIME_SINCE_HIT, timeSinceHit);
	}

	public int getTimeSinceHit() {
		return this.dataManager.get(TIME_SINCE_HIT);
	}

	// Basic inventory stuff WIP
	public void openGUI(EntityPlayer player) {
		if (!getEntityWorld().isRemote)
			player.openGui(TheBetweenlands.instance, thebetweenlands.common.proxy.CommonProxy.GUI_DRAETON_STORAGE, player.getEntityWorld(), getEntityId(), 0, 0);
	}

	@Override
	public int getSizeInventory() {
		return inventory.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : inventory)
			if (!itemstack.isEmpty())
				return false;
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.get(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int size) {
		if (!inventory.get(slot).isEmpty()) {
			ItemStack itemstack;
			if (inventory.get(slot).getCount() <= size) {
				itemstack = inventory.get(slot);
				inventory.set(slot, ItemStack.EMPTY);
				return itemstack;
			} else {
				itemstack = inventory.get(slot).splitStack(size);
				if (inventory.get(slot).getCount() == 0)
					inventory.set(slot, ItemStack.EMPTY);
				return itemstack;
			}
		} else
			return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit())
			stack.setCount(getInventoryStackLimit());
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
		//not needed
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
	}

	@Override
	public Entity[] getParts() {
		return this.parts;
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float damage) {
		return false;
	}

	protected boolean interactFromMultipart(InteractionPart part, EntityPlayer player, EnumHand hand) {
		openGUI(player);
		return true;
	}
}
