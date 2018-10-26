package thebetweenlands.common.entity.mobs;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;

public class EntityFortressBossTurret extends EntityMob implements IEntityBL {
	protected static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.<Optional<UUID>>createKey(EntityFortressBossTurret.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	protected static final DataParameter<Optional<UUID>> TARGET = EntityDataManager.<Optional<UUID>>createKey(EntityFortressBossTurret.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	protected static final DataParameter<Boolean> DEFLECTION_STATE = EntityDataManager.<Boolean>createKey(EntityFortressBossTurret.class, DataSerializers.BOOLEAN);

	private Entity cachedOwner;
	private Entity cachedTarget;	

	private boolean particlesSpawned = false;
	private int attackTicks = 0;
	private int attackDelay = 40;

	public EntityFortressBossTurret(World world) {
		super(world);
		float width = 0.4F;
		float height = 0.4F;
		this.setSize(width, height);
	}

	public EntityFortressBossTurret(World world, Entity source) {
		super(world);
		this.setOwner(source);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(OWNER, Optional.absent());
		this.getDataManager().register(TARGET, Optional.absent());
		this.getDataManager().register(DEFLECTION_STATE, false);
	}

	public void setDeflectable(boolean deflectable) {
		this.getDataManager().set(DEFLECTION_STATE, deflectable);
	}

	public boolean isDeflectable() {
		return this.getDataManager().get(DEFLECTION_STATE);
	}

	public void setOwner(@Nullable Entity entity) {
		this.getDataManager().set(OWNER, entity == null ? Optional.absent() : Optional.of(entity.getUniqueID()));
	}

	@Nullable
	public UUID getOwnerUUID() {
		Optional<UUID> uuid = this.getDataManager().get(OWNER);
		return uuid.isPresent() ? uuid.get() : null;
	}

	@Nullable
	public Entity getOwner() {
		UUID uuid = this.getOwnerUUID();
		if(uuid == null) {
			this.cachedOwner = null;
		} else if(this.cachedOwner == null || !this.cachedOwner.isEntityAlive() || !this.cachedOwner.getUniqueID().equals(uuid)) {
			this.cachedOwner = null;
			for(Entity entity : this.getEntityWorld().getEntitiesWithinAABB(Entity.class, this.getEntityBoundingBox().grow(64.0D, 64.0D, 64.0D))) {
				if(entity.getUniqueID().equals(uuid)) {
					this.cachedOwner = entity;
					break;
				}
			}
		}
		return this.cachedOwner;
	}

	public void setTarget(@Nullable Entity entity) {
		this.getDataManager().set(TARGET, entity == null ? Optional.absent() : Optional.of(entity.getUniqueID()));
	}

	@Nullable
	public UUID getTargetUUID() {
		Optional<UUID> uuid = this.getDataManager().get(TARGET);
		return uuid.isPresent() ? uuid.get() : null;
	}

	@Nullable
	public Entity getTarget() {
		UUID uuid = this.getTargetUUID();
		if(uuid == null) {
			this.cachedTarget = null;
		} else if(this.cachedTarget == null || !this.cachedTarget.isEntityAlive() || !this.cachedTarget.getUniqueID().equals(uuid)) {
			this.cachedTarget = null;
			for(Entity entity : this.getEntityWorld().getEntitiesWithinAABB(Entity.class, this.getEntityBoundingBox().grow(64.0D, 64.0D, 64.0D))) {
				if(entity.getUniqueID().equals(uuid)) {
					this.cachedTarget = entity;
					break;
				}
			}
		}
		return this.cachedTarget;
	}

	public int getAttackDelay() {
		return this.attackDelay;
	}

	public void setAttackDelay(int delay) {
		this.attackDelay = delay;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("attackDelay", this.attackDelay);
		nbt.setBoolean("deflectable", this.isDeflectable());
		if(this.getOwnerUUID() != null) {
			nbt.setUniqueId("owner", this.getOwnerUUID());
		}
		if(this.getTargetUUID() != null) {
			nbt.setUniqueId("target", this.getTargetUUID());
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.attackDelay = nbt.getInteger("attackDelay");
		this.setDeflectable(nbt.getBoolean("deflectable"));
		if(nbt.hasUniqueId("owner")) {
			this.getDataManager().set(OWNER, Optional.of(nbt.getUniqueId("owner")));
		} else {
			this.getDataManager().set(OWNER, Optional.absent());
		}
		if(nbt.hasUniqueId("target")) {
			this.getDataManager().set(TARGET, Optional.of(nbt.getUniqueId("target")));
		} else {
			this.getDataManager().set(TARGET, Optional.absent());
		}
	}

	@Override
	public void onUpdate() {
		if(!this.world.isRemote && (this.world.getDifficulty() == EnumDifficulty.PEACEFUL || (this.getOwner() != null && !this.getOwner().isEntityAlive()))) {
			this.setDead();
			return;
		}
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		super.onUpdate();

		if(this.world.isRemote) {
			if(!this.particlesSpawned) {
				this.particlesSpawned = true;
				for(int i = 0; i < 6; i++) {
					this.spawnVolatileParticles();
				}
			}
			if(this.world.rand.nextInt(6) == 0) {
				this.spawnFlameParticles();
			}
		}

		if(this.getTarget() == null) {
			AxisAlignedBB searchBB = this.getEntityBoundingBox().grow(16, 16, 16);
			List<EntityPlayer> eligiblePlayers = this.world.getEntitiesWithinAABB(EntityPlayer.class, searchBB);
			EntityPlayer closest = null;
			for(EntityPlayer player : eligiblePlayers) {
				if(closest == null || closest.getDistance(this) > player.getDistance(this))
					closest = player;
			}
			if(closest != null) 
				this.setTarget(closest);
		}

		if(this.getTarget() != null) {
			this.faceEntity(this.getTarget(), 360.0F, 360.0F);
			this.attackTicks++;

			if(this.attackTicks > this.attackDelay) {
				if(!this.world.isRemote) {
					if(!this.isObstructedByBoss()) {
						Vec3d diff = new Vec3d(this.posX, this.posY, this.posZ)
								.subtract(new Vec3d(this.getTarget().getEntityBoundingBox().minX + (this.getTarget().getEntityBoundingBox().maxX - this.getTarget().getEntityBoundingBox().minX) / 2.0D,
										this.getTarget().getEntityBoundingBox().minY + (this.getTarget().getEntityBoundingBox().maxY - this.getTarget().getEntityBoundingBox().minY) / 2.0D,
										this.getTarget().getEntityBoundingBox().minZ + (this.getTarget().getEntityBoundingBox().maxZ - this.getTarget().getEntityBoundingBox().minZ) / 2.0D)).normalize();
						EntityFortressBossProjectile bullet = new EntityFortressBossProjectile(this.world, this.getOwner());
						bullet.setDeflectable(this.isDeflectable());
						bullet.setLocationAndAngles(this.posX, this.posY, this.posZ, 0, 0);
						float speed = 0.5F;
						bullet.shoot(-diff.x, -diff.y, -diff.z, speed, 0.0F);
						this.world.spawnEntity(bullet);
					}
				} else {
					for(int i = 0; i < 6; i++)
						this.spawnVolatileParticles();
				}
				this.setDead();
			}
		} else {
			this.attackTicks = 0;
		}
	}

	public boolean isObstructedByBoss() {
		Vec3d ray = this.getLookVec().normalize();
		Vec3d currentPos = new Vec3d(this.posX, this.posY, this.posZ);
		Vec3d nextPos = currentPos.addVector(ray.x * 64.0D, ray.y * 64.0D, ray.z * 64.0D);
		Entity hitEntity = null;
		List<Entity> hitEntities = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().grow(64, 64, 64));
		double minDist = 0.0D;
		for (int i = 0; i < hitEntities.size(); ++i) {
			Entity entity = (Entity)hitEntities.get(i);
			if (entity.canBeCollidedWith()) {
				float f = 0.65F / 2.0F + 0.1F + 0.1F;
				AxisAlignedBB entityBB = entity.getEntityBoundingBox().grow((double)f, (double)f, (double)f);
				RayTraceResult result = entityBB.calculateIntercept(currentPos, nextPos);
				if (result != null) {
					double dst = currentPos.distanceTo(result.hitVec);
					if (dst < minDist || minDist == 0.0D) {
						hitEntity = entity;
						minDist = dst;
					}
				}
			}
		}
		if(hitEntity == null || (hitEntity instanceof EntityFortressBoss == false && hitEntity != this.getOwner())) {
			return false;
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	private void spawnFlameParticles() {
		BLParticles.GREEN_FLAME.spawn(this.world, this.posX, this.posY + 0.2F, this.posZ, ParticleArgs.get().withMotion((this.world.rand.nextFloat() - 0.5F) / 5.0F, (this.world.rand.nextFloat() - 0.5F) / 5.0F, (this.world.rand.nextFloat() - 0.5F) / 5.0F));
	}

	@SideOnly(Side.CLIENT)
	private void spawnVolatileParticles() {
		final double radius = 0.3F;
		final double cx = this.posX;
		final double cy = this.posY + 0.35D;
		final double cz = this.posZ;
		for(int i = 0; i < 8; i++) {
			double px = this.world.rand.nextFloat() * 0.7F;
			double py = this.world.rand.nextFloat() * 0.7F;
			double pz = this.world.rand.nextFloat() * 0.7F;
			Vec3d vec = new Vec3d(px, py, pz).subtract(new Vec3d(0.35F, 0.35F, 0.35F)).normalize();
			px = cx + vec.x * radius;
			py = cy + vec.y * radius;
			pz = cz + vec.z * radius;
			BLParticles.STEAM_PURIFIER.spawn(this.world, px, py, pz);
		}
	}

	@Override
	public void travel(float strafe, float up, float forward) {
		if (this.isInWater()) {
			this.moveRelative(strafe, up, forward, 0.02F);
			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.800000011920929D;
			this.motionY *= 0.800000011920929D;
			this.motionZ *= 0.800000011920929D;
		} else {
			float friction = 0.91F;

			if (this.onGround) {
				friction = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.91F;
			}

			float groundFriction = 0.16277136F / (friction * friction * friction);
			this.moveRelative(strafe, up,  forward, this.onGround ? 0.1F * groundFriction : 0.02F);
			friction = 0.91F;

			if (this.onGround) {
				friction = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().slipperiness * 0.91F;
			}

			this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double)friction;
			this.motionY *= (double)friction;
			this.motionZ *= (double)friction;
		}

		this.prevLimbSwingAmount = this.limbSwingAmount;
		double dx = this.posX - this.prevPosX;
		double dz = this.posZ - this.prevPosZ;
		float distanceMoved = MathHelper.sqrt(dx * dx + dz * dz) * 4.0F;

		if (distanceMoved > 1.0F) {
			distanceMoved = 1.0F;
		}

		this.limbSwingAmount += (distanceMoved - this.limbSwingAmount) * 0.4F;
		this.limbSwing += this.limbSwingAmount;
	}

	@Override
	public boolean attackEntityAsMob(Entity target) {
		return false;
	}
}
