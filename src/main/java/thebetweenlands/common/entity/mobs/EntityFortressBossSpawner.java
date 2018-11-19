package thebetweenlands.common.entity.mobs;

import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;

public class EntityFortressBossSpawner extends EntityMob implements IEntityBL {
	protected static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.<Optional<UUID>>createKey(EntityFortressBossSpawner.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	public int spawnDelay = 40;
	public final int maxSpawnDelay = 40;
	private Entity cachedOwner;

	public EntityFortressBossSpawner(World world) {
		super(world);
		this.setSize(0.4F, 0.4F);
	}

	public EntityFortressBossSpawner(World world, Entity source) {
		super(world);
		this.setOwner(source);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(OWNER, Optional.absent());
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
		nbt.setInteger("spawnDelay", this.spawnDelay);
		if(this.getOwnerUUID() != null) {
			nbt.setUniqueId("owner", this.getOwnerUUID());
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.spawnDelay = nbt.getInteger("spawnDelay");
		if(nbt.hasUniqueId("owner")) {
			this.getDataManager().set(OWNER, Optional.of(nbt.getUniqueId("owner")));
		} else {
			this.getDataManager().set(OWNER, Optional.absent());
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
			Entity owner = this.getOwner();
			if(owner != null) {
				for(int i = 0; i < 3; i++) {
					double sx = this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width;
					double sy = this.posY + this.rand.nextDouble() * (double)this.height - 0.25D;
					double sz = this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width;
					double ex = owner.posX + (this.rand.nextDouble() - 0.5D) * (double)owner.width;
					double ey = owner.posY + this.rand.nextDouble() * (double)owner.height - 0.25D;
					double ez = owner.posZ + (this.rand.nextDouble() - 0.5D) * (double)owner.width;
					this.world.spawnParticle(EnumParticleTypes.PORTAL, sx, sy, sz, ex - sx, ey - sy, ez - sz);
				}
			}
		}

		if(this.spawnDelay > 0) {
			this.spawnDelay--;
		} else {
			if(!this.world.isRemote) {
				EntityWight wight = new EntityWight(this.world);
				wight.setLocationAndAngles(this.posX, this.posY, this.posZ, 0, 0);
				wight.setCanTurnVolatile(false);
				wight.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
				wight.setHealth(wight.getMaxHealth());
				if(this.getOwner() instanceof EntityLiving) {
					wight.setAttackTarget(((EntityLiving)this.getOwner()).getAttackTarget());
				}
				this.world.spawnEntity(wight);
				this.setDead();
			} else {
				for(int i = 0; i < 6; i++) {
					this.spawnVolatileParticles();
				}
				this.setDead();
			}
		}
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
	public void travel(float strafe, float up,  float forward) {
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
	}

	@Override
	public boolean attackEntityAsMob(Entity target) {
		return false;
	}

	@Override
	protected void collideWithEntity(Entity entityIn) { }
}
