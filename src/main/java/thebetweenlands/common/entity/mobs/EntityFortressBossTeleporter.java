package thebetweenlands.common.entity.mobs;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityScreenShake;
import thebetweenlands.client.audio.TeleporterSound;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;

public class EntityFortressBossTeleporter extends Entity implements IEntityScreenShake {
	protected static final DataParameter<Integer> TARGET_ID = EntityDataManager.<Integer>createKey(EntityFortressBossTeleporter.class, DataSerializers.VARINT);

	private Vec3d teleportDestination = Vec3d.ZERO;
	private BlockPos bossSpawnPosition = BlockPos.ORIGIN;

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
		this.getDataManager().register(TARGET_ID, -1);
	}

	@Override
	public void applyEntityCollision(Entity entity) {
	}


	@Override
	public void onUpdate() {
		super.onUpdate();

		double radius = 6.0D;
		double lookRadius = 8.0D;

		if(!this.world.isRemote) {
			if(this.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
				if(this.target == null) {
					AxisAlignedBB checkAABB = new AxisAlignedBB(this.posX-radius, this.posY-radius, this.posZ-radius, this.posX+radius, this.posY+radius, this.posZ+radius);
					List<EntityPlayer> players = this.world.getEntitiesWithinAABB(EntityPlayer.class, checkAABB);
					EntityPlayer closestPlayer = null;
					for(EntityPlayer player : players) {
						if((closestPlayer == null || player.getDistance(this) < closestPlayer.getDistance(this)) && player.getDistance(this) < radius && player.canEntityBeSeen(this)) {
							Vec3d playerLook = player.getLook(1.0F).normalize();
							Vec3d vecDiff = new Vec3d(this.posX - player.posX, this.getEntityBoundingBox().minY + (double)(this.height / 2.0F) - (player.posY + (double)player.getEyeHeight()), this.posZ - player.posZ);
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
					if(this.target.getDistance(this) > radius) {
						this.target = null;
					} else {
						Vec3d playerLook = this.target.getLook(1.0F).normalize();
						Vec3d vecDiff = new Vec3d(this.posX - this.target.posX, this.getEntityBoundingBox().minY + (double)(this.height / 2.0F) - (this.target.posY + (double)this.target.getEyeHeight()), this.posZ - this.target.posZ);
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
				this.getDataManager().set(TARGET_ID, -1);
			} else {
				this.getDataManager().set(TARGET_ID, this.target.getEntityId());
			}
		} else {
			Entity prevTarget = this.target;
			Entity target = this.world.getEntityByID(this.getDataManager().get(TARGET_ID));
			if(target instanceof EntityPlayer) {
				if(this.target == null) {
					for(int i = 0; i < 60; i++) {
						this.spawnSmokeParticle(this.posX, this.posY + this.height / 2.0D, this.posZ, (this.world.rand.nextFloat() - 0.5F) / 2.5F, (this.world.rand.nextFloat() - 0.5F) / 2.5F, (this.world.rand.nextFloat() - 0.5F) / 2.5F);
					}
				}
				this.target = (EntityPlayer) target;
			} else {
				this.target = null;
			}
			if(this.target != null && prevTarget != this.target && this.world.isRemote) {
				this.playTeleportSound();
			}
		}

		if(this.target != null) {
			this.faceEntity(this.target);

			this.teleportTicks++;

			if(!this.world.isRemote && this.teleportTicks > this.maxTeleportTicks) {
				//Teleport
				if(this.target instanceof EntityPlayerMP) {
					EntityPlayerMP player = (EntityPlayerMP) this.target;
					player.dismountRidingEntity();
					player.connection.setPlayerLocation(this.teleportDestination.x, this.teleportDestination.y, this.teleportDestination.z, player.rotationYaw, player.rotationPitch);
				} else {
					this.target.dismountRidingEntity();
					this.target.setLocationAndAngles(this.teleportDestination.x, this.teleportDestination.y, this.teleportDestination.z, this.target.rotationYaw, this.target.rotationPitch);
				}
				if(!this.spawnedBoss) {
					this.spawnBoss();
					this.spawnedBoss = true;
				}
				this.target = null;
				this.teleportTicks = 0;
			} else if(this.world.isRemote && this.world.rand.nextInt(2) == 0) {
				double rx = (double)(this.world.rand.nextFloat());
				double ry = (double)(this.world.rand.nextFloat());
				double rz = (double)(this.world.rand.nextFloat());
				double len = Math.sqrt(rx*rx+ry*ry+rz*rz);
				this.spawnSmokeParticle((float)this.posX - this.width / 2.0F + rx, (float)this.posY + ry, (float)this.posZ - this.width / 2.0F + rz, 
						(rx-0.5D)/len*0.2D, (ry-0.5D)/len*0.2D, (rz-0.5D)/len*0.2D);
			}
		} else {
			this.teleportTicks = 0;
			AxisAlignedBB checkAABB = new AxisAlignedBB(this.posX-lookRadius, this.posY-lookRadius, this.posZ-lookRadius, this.posX+lookRadius, this.posY+lookRadius, this.posZ+lookRadius);
			List<EntityPlayer> players = this.world.getEntitiesWithinAABB(EntityPlayer.class, checkAABB);
			EntityPlayer closestPlayer = null;
			for(EntityPlayer player : players) {
				if((closestPlayer == null || player.getDistance(this) < closestPlayer.getDistance(this)) && player.getDistance(this) < lookRadius && player.canEntityBeSeen(this)) {
					closestPlayer = player;
				}
			}
			if(closestPlayer != null) {
				this.faceEntity(closestPlayer);
				if(this.world.isRemote) {
					if(!this.isLookingAtPlayer) {
						for(int i = 0; i < 10; i++) {
							this.spawnSmokeParticle(this.posX, this.posY + this.height / 2.0D, this.posZ, (this.world.rand.nextFloat() - 0.5F) / 2.5F, (this.world.rand.nextFloat() - 0.5F) / 2.5F, (this.world.rand.nextFloat() - 0.5F) / 2.5F);
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
		Minecraft.getMinecraft().getSoundHandler().playSound(new TeleporterSound(this, this.getTarget()));
	}

	public void faceEntity(Entity target) {
		double dx = target.posX - this.posX;
		double dz = target.posZ - this.posZ;
		double dy;
		if (target instanceof EntityLivingBase) {
			EntityLivingBase entitylivingbase = (EntityLivingBase)target;
			double actualPosY = entitylivingbase.posY;
			dy = actualPosY + (double)entitylivingbase.getEyeHeight() - (this.posY + (double)this.getEyeHeight());
		} else {
			dy = (target.getEntityBoundingBox().minY + target.getEntityBoundingBox().maxY) / 2.0D - (this.posY + (double)this.getEyeHeight());
		}
		double dist = (double)MathHelper.sqrt(dx * dx + dz * dz);
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
		this.teleportDestination = new Vec3d(dx, dy, dz);
		double sx = nbt.getDouble("bossSpawnX");
		double sy = nbt.getDouble("bossSpawnY");
		double sz = nbt.getDouble("bossSpawnZ");
		this.bossSpawnPosition = new BlockPos(sx, sy, sz);
		this.spawnedBoss = nbt.getBoolean("spawnedBoss");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		if(this.teleportDestination != null) {
			nbt.setDouble("destinationX", this.teleportDestination.x);
			nbt.setDouble("destinationY", this.teleportDestination.y);
			nbt.setDouble("destinationZ", this.teleportDestination.z);
		}
		if(this.bossSpawnPosition != null) {
			nbt.setDouble("bossSpawnX", this.bossSpawnPosition.getX());
			nbt.setDouble("bossSpawnY", this.bossSpawnPosition.getY());
			nbt.setDouble("bossSpawnZ", this.bossSpawnPosition.getZ());
		}
		nbt.setBoolean("spawnedBoss", this.spawnedBoss);
	}

	public void setTeleportDestination(Vec3d destination) {
		this.teleportDestination = destination;
	}

	public Vec3d getTeleportDestination() {
		return this.teleportDestination;
	}

	public void setBossSpawnPosition(BlockPos position) {
		this.bossSpawnPosition = position;
	}

	public BlockPos getBossSpawnPosition() {
		return this.bossSpawnPosition;
	}

	protected void spawnBoss() {
		EntityFortressBoss boss = new EntityFortressBoss(this.world);
		boss.setPosition(this.bossSpawnPosition.getX() + 0.5D, this.bossSpawnPosition.getY() + 0.5D, this.bossSpawnPosition.getZ() + 0.5D);
		boss.setAnchor(this.bossSpawnPosition, 6.0D);
		this.world.spawnEntity(boss);
	}

	@Override
	public float getShakeIntensity(Entity viewer, float partialTicks) {
		if(this.getTarget() == viewer)
			return (float)Math.pow(this.getTeleportProgress(), 3) / 2.0F;
		return 0.0F;
	}

	@SideOnly(Side.CLIENT)
	protected void spawnSmokeParticle(double x, double y, double z, double mx, double my, double mz) {
		BLParticles.PORTAL.spawn(this.world, x, y, z, ParticleArgs.get().withMotion(mx, my, mz));
	}
}

