package thebetweenlands.common.entity.mobs;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.common.registries.EntityRegistry;

public class EntityPrimordialMalevolenceSpawner extends EntityMob implements IEntityBL {
	protected static final DataParameter<Optional<UUID>> OWNER = EntityDataManager.<Optional<UUID>>createKey(EntityPrimordialMalevolenceSpawner.class, DataSerializers.OPTIONAL_UNIQUE_ID);

	public int spawnDelay = 40;
	public final int maxSpawnDelay = 40;
	private Entity cachedOwner;

	public EntityPrimordialMalevolenceSpawner(World world) {
		super(EntityRegistry.PRIMORDIAL_MALEVOLENCE_SPAWNER, world);
		this.setSize(0.4F, 0.4F);
	}

	public EntityPrimordialMalevolenceSpawner(World world, Entity source) {
		super(EntityRegistry.PRIMORDIAL_MALEVOLENCE_SPAWNER, world);
		this.setOwner(source);
	}

	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
	}

	@Override
	protected void registerData() {
		super.registerData();
		this.getDataManager().register(OWNER, Optional.empty());
	}

	public void setOwner(@Nullable Entity entity) {
		this.getDataManager().set(OWNER, entity == null ? Optional.empty() : Optional.of(entity.getUniqueID()));
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
		} else if(this.cachedOwner == null || !this.cachedOwner.isAlive() || !this.cachedOwner.getUniqueID().equals(uuid)) {
			this.cachedOwner = null;
			for(Entity entity : this.getEntityWorld().getEntitiesWithinAABB(Entity.class, this.getBoundingBox().grow(64.0D, 64.0D, 64.0D))) {
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
	public void writeAdditional(NBTTagCompound nbt) {
		super.writeAdditional(nbt);
		nbt.setInt("spawnDelay", this.spawnDelay);
		if(this.getOwnerUUID() != null) {
			nbt.setUniqueId("owner", this.getOwnerUUID());
		}
	}

	@Override
	public void readAdditional(NBTTagCompound nbt) {
		super.readAdditional(nbt);
		this.spawnDelay = nbt.getInt("spawnDelay");
		if(nbt.hasUniqueId("owner")) {
			this.getDataManager().set(OWNER, Optional.of(nbt.getUniqueId("owner")));
		} else {
			this.getDataManager().set(OWNER, Optional.empty());
		}
	}

	@Override
	public void tick() {
		if(!this.world.isRemote() && (this.world.getDifficulty() == EnumDifficulty.PEACEFUL || (this.getOwner() != null && !this.getOwner().isAlive()))) {
			this.remove();
			return;
		}

		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;

		super.tick();

		if(this.world.isRemote()) {
			Entity owner = this.getOwner();
			if(owner != null) {
				for(int i = 0; i < 3; i++) {
					double sx = this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width;
					double sy = this.posY + this.rand.nextDouble() * (double)this.height - 0.25D;
					double sz = this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width;
					double ex = owner.posX + (this.rand.nextDouble() - 0.5D) * (double)owner.width;
					double ey = owner.posY + this.rand.nextDouble() * (double)owner.height - 0.25D;
					double ez = owner.posZ + (this.rand.nextDouble() - 0.5D) * (double)owner.width;
					this.world.spawnParticle(Particles.PORTAL, sx, sy, sz, ex - sx, ey - sy, ez - sz);
				}
			}
		}

		if(this.spawnDelay > 0) {
			this.spawnDelay--;
		} else {
			if(!this.world.isRemote()) {
				EntityWight wight = new EntityWight(this.world);
				wight.setLocationAndAngles(this.posX, this.posY, this.posZ, 0, 0);
				wight.setCanTurnVolatile(false);
				wight.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
				wight.setHealth(wight.getMaxHealth());
				if(this.getOwner() instanceof EntityLiving) {
					wight.setAttackTarget(((EntityLiving)this.getOwner()).getAttackTarget());
				}
				this.world.spawnEntity(wight);
				this.remove();
			} else {
				for(int i = 0; i < 6; i++) {
					this.spawnVolatileParticles();
				}
				this.remove();
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
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
