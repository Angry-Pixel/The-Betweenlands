package thebetweenlands.entities.mobs.boss.fortress;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.audio.TeleporterSound;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.entities.IScreenShake;

public class EntityFortressBossTeleporter extends Entity implements IScreenShake {
	public static final int TARGET_ID_DW = 20;

	private Vec3 teleportDestination = Vec3.createVectorHelper(0, 0, 0);
	private Vec3 bossSpawnPosition = Vec3.createVectorHelper(0, 0, 0);

	private EntityPlayer target = null;

	private int teleportTicks = 0;
	private final int maxTeleportTicks = 75;

	public boolean isLookingAtPlayer = false;

	private boolean spawnedBoss = false;

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
		double lookRadius = 8.0D;

		if(!this.worldObj.isRemote) {
			if(this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL) {
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
							if(angle > 1.0D - 0.01D / dist)
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
						if(angle <= 1.0D - (0.01D + Math.pow(this.getTeleportProgress(), 3) / 10.0D) / dist)
							this.target = null;
					}
				}
			} else {
				this.target = null;
			}

			if(this.target == null) {
				this.dataWatcher.updateObject(TARGET_ID_DW, -1);
			} else {
				this.dataWatcher.updateObject(TARGET_ID_DW, this.target.getEntityId());
			}
		} else {
			Entity prevTarget = this.target;
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
			if(this.target != null && prevTarget != this.target && this.worldObj.isRemote) {
				this.playTeleportSound();
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
				if(!this.spawnedBoss) {
					this.spawnBoss();
					this.spawnedBoss = true;
				}
				this.target = null;
				this.teleportTicks = 0;
			} else if(this.worldObj.isRemote && this.worldObj.rand.nextInt(2) == 0) {
				double rx = (double)(this.worldObj.rand.nextFloat());
				double ry = (double)(this.worldObj.rand.nextFloat());
				double rz = (double)(this.worldObj.rand.nextFloat());
				double len = Math.sqrt(rx*rx+ry*ry+rz*rz);
				BLParticle.PORTAL.spawn(this.worldObj,
						(float)this.posX - this.width / 2.0F + rx, (float)this.posY + ry, (float)this.posZ - this.width / 2.0F + rz, 
						(rx-0.5D)/len*0.2D, (ry-0.5D)/len*0.2D, (rz-0.5D)/len*0.2D, 0);
			}
		} else {
			this.teleportTicks = 0;
			AxisAlignedBB checkAABB = AxisAlignedBB.getBoundingBox(this.posX-lookRadius, this.posY-lookRadius, this.posZ-lookRadius, this.posX+lookRadius, this.posY+lookRadius, this.posZ+lookRadius);
			List<EntityPlayer> players = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, checkAABB);
			EntityPlayer closestPlayer = null;
			for(EntityPlayer player : players) {
				if((closestPlayer == null || player.getDistanceToEntity(this) < closestPlayer.getDistanceToEntity(this)) && player.getDistanceToEntity(this) < lookRadius && player.canEntityBeSeen(this)) {
					closestPlayer = player;
				}
			}
			if(closestPlayer != null) {
				this.faceEntity(closestPlayer);
				if(this.worldObj.isRemote) {
					if(!this.isLookingAtPlayer) {
						for(int i = 0; i < 10; i++) {
							BLParticle.SMOKE.spawn(this.worldObj, this.posX, this.posY + this.height / 2.0D, this.posZ, (this.worldObj.rand.nextFloat() - 0.5F) / 2.5F, (this.worldObj.rand.nextFloat() - 0.5F) / 2.5F, (this.worldObj.rand.nextFloat() - 0.5F) / 2.5F, 1);
						}
					}
				}
				this.isLookingAtPlayer = true;
			} else {
				this.isLookingAtPlayer = false;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void playTeleportSound() {
		Minecraft.getMinecraft().getSoundHandler().playSound(new TeleporterSound(this, 1, 1));
	}

	public void faceEntity(Entity target) {
		double dx = target.posX - this.posX;
		double dz = target.posZ - this.posZ;
		double dy;
		if (target instanceof EntityLivingBase) {
			EntityLivingBase entitylivingbase = (EntityLivingBase)target;
			double actualPosY = entitylivingbase.posY + (entitylivingbase == TheBetweenlands.proxy.getClientPlayer() ? -0.65D : 0.0D);
			dy = actualPosY + (double)entitylivingbase.getEyeHeight() - (this.posY + (double)this.getEyeHeight());
		} else {
			dy = (target.boundingBox.minY + target.boundingBox.maxY) / 2.0D - (this.posY + (double)this.getEyeHeight());
		}
		double dist = (double)MathHelper.sqrt_double(dx * dx + dz * dz);
		float yaw = (float)(Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float)(-(Math.atan2(dy, dist) * 180.0D / Math.PI));
		this.rotationPitch = this.prevRotationPitch = pitch;
		this.rotationYaw = this.prevRotationYaw = yaw;
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
		double sx = nbt.getDouble("bossSpawnX");
		double sy = nbt.getDouble("bossSpawnY");
		double sz = nbt.getDouble("bossSpawnZ");
		this.bossSpawnPosition = Vec3.createVectorHelper(sx, sy, sz);
		this.spawnedBoss = nbt.getBoolean("spawnedBoss");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		if(this.teleportDestination != null) {
			nbt.setDouble("destinationX", this.teleportDestination.xCoord);
			nbt.setDouble("destinationY", this.teleportDestination.yCoord);
			nbt.setDouble("destinationZ", this.teleportDestination.zCoord);
		}
		if(this.bossSpawnPosition != null) {
			nbt.setDouble("bossSpawnX", this.bossSpawnPosition.xCoord);
			nbt.setDouble("bossSpawnY", this.bossSpawnPosition.yCoord);
			nbt.setDouble("bossSpawnZ", this.bossSpawnPosition.zCoord);
		}
		nbt.setBoolean("spawnedBoss", this.spawnedBoss);
	}

	public void setTeleportDestination(Vec3 destination) {
		this.teleportDestination = destination;
	}

	public Vec3 getTeleportDestination() {
		return this.teleportDestination;
	}

	public void setBossSpawnPosition(Vec3 position) {
		this.bossSpawnPosition = position;
	}

	public Vec3 getBossSpawnPosition() {
		return this.bossSpawnPosition;
	}

	private void spawnBoss() {
		EntityFortressBoss boss = new EntityFortressBoss(this.worldObj);
		boss.setPosition(this.bossSpawnPosition.xCoord, this.bossSpawnPosition.yCoord, this.bossSpawnPosition.zCoord);
		boss.setAnchor(this.bossSpawnPosition.xCoord, this.bossSpawnPosition.yCoord, this.bossSpawnPosition.zCoord, 6.0D);
		this.worldObj.spawnEntityInWorld(boss);
	}

	@Override
	public float getShakeIntensity(EntityLivingBase viewer, float partialTicks) {
		if(this.getTarget() == viewer)
			return (float)Math.pow(this.getTeleportProgress(), 3) / 2.0F;
		return 0.0F;
	}
}

