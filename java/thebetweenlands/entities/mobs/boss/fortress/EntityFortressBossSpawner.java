package thebetweenlands.entities.mobs.boss.fortress;

import java.util.UUID;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.entities.mobs.IEntityBL;

public class EntityFortressBossSpawner extends EntityMob implements IEntityBL {
	public static final int OWNER_DW = 18;

	private double anchorX, anchorY, anchorZ;
	public int spawnDelay = 40;
	public final int maxSpawnDelay = 40;
	private String ownerUUID = "";

	public EntityFortressBossSpawner(World world) {
		super(world);
		float width = 0.4F;
		float height = 0.4F;
		this.setSize(width, height);
	}

	public EntityFortressBossSpawner(World world, Entity source) {
		super(world);
		if(source != null)
			this.setOwner(source.getUniqueID().toString());
	}

	@Override
	protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0D);
    }
	
	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(OWNER_DW, 0);
	}

	@Override
	public String pageName() {
		return "thisShouldntHaveAPageButTheManualImplementationForcesMeToGiveItAPageName";
	}

	public void setOwner(String ownerUUID) {
		this.ownerUUID = ownerUUID == null ? "" : ownerUUID;
		Entity owner = this.getOwnerFromUUID();
		this.dataWatcher.updateObject(OWNER_DW, owner != null ? owner.getEntityId() : -1);
	}

	private Entity getOwnerFromUUID() {
		try {
			UUID uuid = UUID.fromString(this.ownerUUID);
			return uuid == null ? null : this.getEntityByUUID(uuid);
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
		}
	}

	public Entity getOwner() {
		if(this.worldObj.isRemote) {
			return this.worldObj.getEntityByID(this.dataWatcher.getWatchableObjectInt(OWNER_DW));
		} else {
			return this.getOwnerFromUUID();
		}
	}

	private Entity getEntityByUUID(UUID p_152378_1_) {
		for (int i = 0; i < this.worldObj.loadedEntityList.size(); ++i) {
			Entity entity = (Entity)this.worldObj.loadedEntityList.get(i);
			if (p_152378_1_.equals(entity.getUniqueID())) {
				return entity;
			}
		}
		return null;
	}

	public void setAnchor(double x, double y, double z) {
		this.anchorX = x;
		this.anchorY = y;
		this.anchorZ = z;
	}

	public double getAnchorX() {
		return this.anchorX;
	}

	public double getAnchorY() {
		return this.anchorY;
	}

	public double getAnchorZ() {
		return this.anchorZ;
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
		nbt.setDouble("anchorX", this.anchorX);
		nbt.setDouble("anchorY", this.anchorY);
		nbt.setDouble("anchorZ", this.anchorZ);
		nbt.setInteger("spawnDelay", this.spawnDelay);
		nbt.setString("ownerUUID", this.ownerUUID != null ? this.ownerUUID : "");
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.anchorX = nbt.getDouble("anchorX");
		this.anchorY = nbt.getDouble("anchorY");
		this.anchorZ = nbt.getDouble("anchorZ");
		this.spawnDelay = nbt.getInteger("spawnDelay");
		this.ownerUUID = nbt.getString("ownerUUID");
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		super.onSpawnWithEgg(data);
		this.anchorX = this.posX;
		this.anchorY = this.posY;
		this.anchorZ = this.posZ;
		return data;
	}

	@Override
	public void onUpdate() {
		if(!this.worldObj.isRemote && (this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL || (this.getOwner() != null && !this.getOwner().isEntityAlive()))) {
			this.setDead();
			return;
		}
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		super.onUpdate();

		if(this.worldObj.isRemote) {
			Entity owner = this.getOwner();
			if(owner != null) {
				for(int i = 0; i < 3; i++) {
					double sx = this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width;
					double sy = this.posY + this.rand.nextDouble() * (double)this.height - 0.25D;
					double sz = this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width;
					double ex = owner.posX + (this.rand.nextDouble() - 0.5D) * (double)owner.width;
					double ey = owner.posY + this.rand.nextDouble() * (double)owner.height - 0.25D;
					double ez = owner.posZ + (this.rand.nextDouble() - 0.5D) * (double)owner.width;
					this.worldObj.spawnParticle("portal", sx, sy, sz, ex - sx, ey - sy, ez - sz);
				}
			}
		}

		if(this.spawnDelay > 0) {
			this.spawnDelay--;
		} else {
			if(!this.worldObj.isRemote) {
				EntityWight wight = new EntityWight(this.worldObj);
				wight.setLocationAndAngles(this.posX, this.posY, this.posZ, 0, 0);
				wight.setCanTurnVolatile(false);
				wight.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0D);
				wight.setHealth(wight.getMaxHealth());
				this.worldObj.spawnEntityInWorld(wight);
				this.setDead();
			} else {
				for(int i = 0; i < 6; i++)
					this.spawnVolatileParticles();
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
			double px = this.worldObj.rand.nextFloat() * 0.7F;
			double py = this.worldObj.rand.nextFloat() * 0.7F;
			double pz = this.worldObj.rand.nextFloat() * 0.7F;
			Vec3 vec = Vec3.createVectorHelper(px, py, pz).subtract(Vec3.createVectorHelper(0.35F, 0.35F, 0.35F)).normalize();
			px = cx + vec.xCoord * radius;
			py = cy + vec.yCoord * radius;
			pz = cz + vec.zCoord * radius;
			BLParticle.STEAM_PURIFIER.spawn(this.worldObj, px, py, pz, 0.0D, 0.0D, 0.0D, 0);
		}
	}

	@Override
	public void moveEntityWithHeading(float strafe, float forward) {
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
	}

	@Override
	public boolean attackEntityAsMob(Entity target) {
		return false;
	}
}
