package thebetweenlands.entities.mobs.boss.fortress;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import thebetweenlands.entities.mobs.IEntityBL;

public class EntityFortressBossBlockade extends EntityMob implements IEntityBL {
	public static final int OWNER_DW = 18;
	public static final int SIZE_DW = 19;
	public static final int ROTATION_DW = 20;

	private String ownerUUID = "";
	private float rotation = 0.0F;
	private int despawnTicks = 0;
	private int maxDespawnTicks = 160;

	public EntityFortressBossBlockade(World world) {
		super(world);
		this.setSize(1.0F, 0.2F);
	}

	public EntityFortressBossBlockade(World world, Entity source) {
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
		this.dataWatcher.addObject(OWNER_DW, -1);
		this.dataWatcher.addObject(SIZE_DW, 1.0F);
		this.dataWatcher.addObject(ROTATION_DW, this.rotation);
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

	public void setTriangleSize(float size) {
		this.dataWatcher.updateObject(SIZE_DW, size);
		if(this.worldObj.isRemote) {
			double prevX = this.posX;
			double prevZ = this.posZ;
			this.setSize(size*2, this.height);
			this.setPosition(prevX, this.posY, prevZ);
		}
	}

	public float getTriangleSize() {
		return this.dataWatcher.getWatchableObjectFloat(SIZE_DW);
	}

	public void setMaxDespawnTicks(int ticks) {
		this.maxDespawnTicks = ticks;
	}

	public int getMaxDespawnTicks() {
		return this.maxDespawnTicks;
	}

	public int getDespawnTicks() {
		return this.despawnTicks;
	}

	@Override
	public String pageName() {
		return "thisShouldntHaveAPageButTheManualImplementationForcesMeToGiveItAPageName";
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
		nbt.setFloat("triangleSize", this.getTriangleSize());
		nbt.setFloat("triangleRotation", this.dataWatcher.getWatchableObjectFloat(ROTATION_DW));
		nbt.setInteger("despawnTicks", this.despawnTicks);
		nbt.setInteger("maxDespawnTicks", this.maxDespawnTicks);
		nbt.setString("ownerUUID", this.ownerUUID != null ? this.ownerUUID : "");
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		this.setTriangleSize(nbt.getFloat("triangleSize"));
		this.dataWatcher.updateObject(ROTATION_DW, nbt.getFloat("triangleRotation"));
		this.despawnTicks = nbt.getInteger("despawnTicks");
		this.maxDespawnTicks = nbt.getInteger("maxDespawnTicks");
		this.setOwner(nbt.getString("ownerUUID"));
	}

	@Override
	public void onUpdate() {
		if(!this.worldObj.isRemote && (this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL || (this.getOwner() != null && !this.getOwner().isEntityAlive()))) {
			this.setDead();
			return;
		}
		this.setTriangleSize(this.getTriangleSize());
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		super.onUpdate();

		if(!this.worldObj.isRemote) {
			this.despawnTicks++;
			if(this.despawnTicks >= this.getMaxDespawnTicks()) {
				this.setDead();
			}

			this.rotation += 1.0F;
			this.dataWatcher.updateObject(ROTATION_DW, this.rotation);

			List<EntityPlayer> targets = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.boundingBox.expand(this.getTriangleSize()*2, 0, this.getTriangleSize()*2));
			for(EntityPlayer target : targets) {
				Vec3[] vertices = this.getTriangleVertices();
				if(this.rayTraceTriangle(Vec3.createVectorHelper(target.posX - this.posX, this.posY + 1, target.posZ - this.posZ), Vec3.createVectorHelper(0, -16, 0), vertices[0], vertices[1], vertices[2])) {
					float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
					if(target.attackEntityFrom(DamageSource.magic, damage) && this.getOwner() != null && this.getOwner() instanceof EntityLivingBase && 
							((EntityLivingBase)this.getOwner()).getHealth() < ((EntityLivingBase)this.getOwner()).getMaxHealth() - damage) {
						((EntityLivingBase)this.getOwner()).heal(damage);
					}
				}
			}
		} else {
			this.rotation = this.dataWatcher.getWatchableObjectFloat(ROTATION_DW);

			for(int c = 0; c < 4; c++) {
				float r1 = this.worldObj.rand.nextFloat();
				float r2 = this.worldObj.rand.nextFloat();
				this.rotation += 15;
				Vec3[] vertices = this.getTriangleVertices();
				this.rotation -= 15;
				Vec3 rp = Vec3.createVectorHelper(0, vertices[0].yCoord, 0);
				for(int i = 0; i < 3; i++) {
					Vec3 vertex = vertices[i];
					switch(i) {
					case 0:
						rp.xCoord += vertex.xCoord * (1 - Math.sqrt(r1));
						rp.zCoord += vertex.zCoord * (1 - Math.sqrt(r1));
						break;
					case 1:
						rp.xCoord += (Math.sqrt(r1) * (1 - r2)) * vertex.xCoord;
						rp.zCoord += (Math.sqrt(r1) * (1 - r2)) * vertex.zCoord;
						break;
					case 2:
						rp.xCoord += (Math.sqrt(r1) * r2) * vertex.xCoord;
						rp.zCoord += (Math.sqrt(r1) * r2) * vertex.zCoord;
						break;
					}
				}

				double sx = this.posX + rp.xCoord;
				double sy = this.posY + rp.yCoord + 4;
				double sz = this.posZ + rp.zCoord;
				double ex = this.posX + rp.xCoord;
				double ey = this.posY + rp.yCoord;
				double ez = this.posZ + rp.zCoord;
				if(this.getOwner() != null) {
					sx = this.getOwner().posX;
					sy = this.getOwner().posY;
					sz = this.getOwner().posZ;
				}
				this.worldObj.spawnParticle("portal", sx, sy, sz, ex - sx, ey - sy, ez - sz);

				/*double dx = 0;
				double dy = -4;
				double dz = 0;
				if(this.getOwner() != null) {
					dx = -(this.getOwner().posX - rp.xCoord);
					dy = -(this.getOwner().posY - rp.yCoord);
					dz = -(this.getOwner().posZ - rp.xCoord);
				}
				this.worldObj.spawnParticle("portal",  - dx, this.posY + rp.yCoord - dy, this.posZ + rp.zCoord - dz, dx, dy, dz);*/
			}
		}
	}

	@Override
	public void moveEntityWithHeading(float strafe, float forward) {
		if (this.isInWater()) {
			this.moveFlying(strafe, forward, 0.02F);
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.800000011920929D;
			this.motionY *= 0.800000011920929D;
			this.motionZ *= 0.800000011920929D;
		} else if (this.handleLavaMovement()) {
			this.moveFlying(strafe, forward, 0.02F);
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.5D;
			this.motionY *= 0.5D;
			this.motionZ *= 0.5D;
		} else {
			float friction = 0.91F;

			if (this.onGround) {
				friction = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.91F;
			}

			float groundFriction = 0.16277136F / (friction * friction * friction);
			this.moveFlying(strafe, forward, this.onGround ? 0.1F * groundFriction : 0.02F);
			friction = 0.91F;

			if (this.onGround) {
				friction = this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness * 0.91F;
			}

			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double)friction;
			this.motionY *= (double)friction;
			this.motionZ *= (double)friction;
		}

		this.prevLimbSwingAmount = this.limbSwingAmount;
		double dx = this.posX - this.prevPosX;
		double dz = this.posZ - this.prevPosZ;
		float distanceMoved = MathHelper.sqrt_double(dx * dx + dz * dz) * 4.0F;

		if (distanceMoved > 1.0F) {
			distanceMoved = 1.0F;
		}

		this.limbSwingAmount += (distanceMoved - this.limbSwingAmount) * 0.4F;
		this.limbSwing += this.limbSwingAmount;
	}

	public Vec3[] getTriangleVertices() {
		Vec3[] vertices = new Vec3[3];
		double angle = Math.PI * 2.0D / 3.0D;
		for(int i = 0; i < 3; i++) {
			double sin = Math.sin(angle * i + Math.toRadians(this.rotation));
			double cos = Math.cos(angle * i + Math.toRadians(this.rotation));
			vertices[i] = Vec3.createVectorHelper(sin * this.getTriangleSize(), 0, cos * this.getTriangleSize());
		}
		return vertices;
	}

	@Override
	public boolean attackEntityAsMob(Entity target) {
		return false;
	}

	private boolean rayTraceTriangle(Vec3 pos, Vec3 ray, Vec3 v0, Vec3 v1, Vec3 v2) {
		final double epsilon = 0.00001;
		Vec3 diff1 = v1.subtract(v0);
		Vec3 diff2 = v2.subtract(v0);
		Vec3 rayCross = ray.crossProduct(diff2);
		double angleDifference = diff1.dotProduct(rayCross);
		if (angleDifference > -epsilon && angleDifference < epsilon)
			return false;
		double f = 1.0D / angleDifference;
		Vec3 s = pos.subtract(v0);
		double u = f * (s.dotProduct(rayCross));
		if (u < 0.0 || u > 1.0) {
			return false;
		}
		Vec3 q = s.crossProduct(diff1);
		double v = f * ray.dotProduct(q);
		if (v < 0.0 || u + v > 1.0) {
			return false;
		}
		double t = -f * diff2.dotProduct(q);
		if (t > epsilon) {
			return true;
		} else {
			return false;
		}
	}
}
