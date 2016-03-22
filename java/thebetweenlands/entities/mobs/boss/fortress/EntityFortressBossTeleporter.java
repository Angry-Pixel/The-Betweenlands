package thebetweenlands.entities.mobs.boss.fortress;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;

public class EntityFortressBossTeleporter extends Entity {
	public static final int TARGET_ID_DW = 20;

	private Vec3 teleportDestination = Vec3.createVectorHelper(0, 0, 0);

	private EntityPlayer target = null;

	private int teleportTicks = 0;
	private final int maxTeleportTicks = 80;

	public EntityFortressBossTeleporter(World world) {
		super(world);
		setSize(1F, 1F);
	}

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(TARGET_ID_DW, 0);
	}

	@Override
	public void applyEntityCollision(Entity entity) {
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		double radius = 6.0D;

		if(!this.worldObj.isRemote) {
			if(this.target == null) {
				AxisAlignedBB checkAABB = AxisAlignedBB.getBoundingBox(this.posX-radius, this.posY-radius, this.posZ-radius, this.posX+radius, this.posY+radius, this.posZ+radius);
				List<EntityPlayer> players = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, checkAABB);
				EntityPlayer closestPlayer = null;
				for(EntityPlayer player : players) {
					if((closestPlayer == null || player.getDistanceToEntity(this) < closestPlayer.getDistanceToEntity(this)) && player.getDistanceToEntity(this) < radius && player.canEntityBeSeen(this)) {
						Vec3 playerLook = player.getLook(1.0F).normalize();
						Vec3 vecDiff = Vec3.createVectorHelper(this.posX - player.posX, this.boundingBox.minY + (double)(this.height / 2.0F) - (player.posY + (double)player.getEyeHeight()), this.posZ - player.posZ);
						double dist = vecDiff.lengthVector();
						vecDiff = vecDiff.normalize();
						double angle = playerLook.dotProduct(vecDiff);
						if(angle > 1.0D - 0.05D / dist)
							closestPlayer = player;
					}
				}
				if(closestPlayer != null)
					this.target = closestPlayer;
			} else {
				if(this.target.getDistanceToEntity(this) > radius) {
					this.target = null;
				} else {
					Vec3 playerLook = this.target.getLook(1.0F).normalize();
					Vec3 vecDiff = Vec3.createVectorHelper(this.posX - this.target.posX, this.boundingBox.minY + (double)(this.height / 2.0F) - (this.target.posY + (double)this.target.getEyeHeight()), this.posZ - this.target.posZ);
					double dist = vecDiff.lengthVector();
					vecDiff = vecDiff.normalize();
					double angle = playerLook.dotProduct(vecDiff);
					if(angle <= 1.0D - 0.05D / dist)
						this.target = null;
				}
			}

			if(this.target == null) {
				this.dataWatcher.updateObject(TARGET_ID_DW, -1);
			} else {
				this.dataWatcher.updateObject(TARGET_ID_DW, this.target.getEntityId());
			}
		} else {
			Entity target = this.worldObj.getEntityByID(this.dataWatcher.getWatchableObjectInt(TARGET_ID_DW));
			if(target instanceof EntityPlayer) {
				if(this.target == null) {
					for(int i = 0; i < 60; i++) {
						BLParticle.SMOKE.spawn(this.worldObj, this.posX, this.posY + this.height / 2.0D, this.posZ, (this.worldObj.rand.nextFloat() - 0.5F) / 2.5F, (this.worldObj.rand.nextFloat() - 0.5F) / 2.5F, (this.worldObj.rand.nextFloat() - 0.5F) / 2.5F, 1);
					}
				}
				this.target = (EntityPlayer) target;
			} else {
				this.target = null;
			}
		}

		if(this.target != null) {
			this.faceEntity(this.target);

			this.teleportTicks++;

			if(!this.worldObj.isRemote && this.teleportTicks > this.maxTeleportTicks) {
				//Teleport
				if(this.target instanceof EntityPlayerMP) {
					EntityPlayerMP player = (EntityPlayerMP) this.target;
					player.mountEntity(null);
					player.playerNetServerHandler.setPlayerLocation(this.teleportDestination.xCoord, this.teleportDestination.yCoord, this.teleportDestination.zCoord, player.rotationYaw, player.rotationPitch);
				} else {
					this.target.mountEntity(null);
					this.target.setLocationAndAngles(this.teleportDestination.xCoord, this.teleportDestination.yCoord, this.teleportDestination.zCoord, this.target.rotationYaw, this.target.rotationPitch);
				}
				this.target = null;
				this.teleportTicks = 0;
			}
		} else {
			this.teleportTicks = 0;
		}
	}

	public void faceEntity(Entity target) {
		double dx = target.posX - this.posX;
		double dz = target.posZ - this.posZ;
		double dy;
		if (target instanceof EntityLivingBase) {
			EntityLivingBase entitylivingbase = (EntityLivingBase)target;
			dy = entitylivingbase.posY + (double)entitylivingbase.getEyeHeight() - (this.posY + (double)this.getEyeHeight());
		} else {
			dy = (target.boundingBox.minY + target.boundingBox.maxY) / 2.0D - (this.posY + (double)this.getEyeHeight());
		}
		double dist = (double)MathHelper.sqrt_double(dx * dx + dz * dz);
		float yaw = (float)(Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float)(-(Math.atan2(dy, dist) * 180.0D / Math.PI));
		this.rotationPitch = pitch;
		this.rotationYaw = yaw;
	}

	public float getTeleportProgress() {
		return this.teleportTicks / (float)this.maxTeleportTicks;
	}

	public EntityPlayer getTarget() {
		return this.target;
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		double dx = nbt.getDouble("destinationX");
		double dy = nbt.getDouble("destinationY");
		double dz = nbt.getDouble("destinationZ");
		this.teleportDestination = Vec3.createVectorHelper(dx, dy, dz);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		if(this.teleportDestination != null) {
			nbt.setDouble("destinationX", this.teleportDestination.xCoord);
			nbt.setDouble("destinationY", this.teleportDestination.yCoord);
			nbt.setDouble("destinationZ", this.teleportDestination.zCoord);
		}
	}

	public void setTeleportDestination(Vec3 destination) {
		this.teleportDestination = destination;
	}

	public Vec3 getTeleportDestination() {
		return this.teleportDestination;
	}
}

