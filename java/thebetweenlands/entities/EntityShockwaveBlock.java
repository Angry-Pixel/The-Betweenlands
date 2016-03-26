package thebetweenlands.entities;

import java.util.List;
import java.util.UUID;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityShockwaveBlock extends Entity implements IEntityAdditionalSpawnData {
	public static final int OWNER_DW = 18;
	public Block blockID;
	public int blockMeta;
	public int originX, originY, originZ, jumpDelay;
	private double waveStartX, waveStartZ;

	public EntityShockwaveBlock(World world) {
		super(world);
		setSize(1.0F, 1.0F);
		setBlock(Blocks.stone, 0);
	}

	public void setBlock(Block blockID, int blockMeta) {
		this.blockID = blockID;
		this.blockMeta = blockMeta;
	}

	public void setOwner(String ownerUUID) {
		this.dataWatcher.updateObject(OWNER_DW, ownerUUID);
	}

	public String getOwnerUUID() {
		return this.dataWatcher.getWatchableObjectString(OWNER_DW);
	}

	public Entity getOwner() {
		try {
			UUID uuid = UUID.fromString(this.getOwnerUUID());
			return uuid == null ? null : this.getEntityByUUID(uuid);
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
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

	@Override
	public void onUpdate() {
		this.motionX = 0;
		this.motionZ = 0;
		this.posX = this.lastTickPosX;
		this.posZ = this.lastTickPosZ;
		this.isAirBorne = true;
		if (!this.worldObj.isRemote) {
			if (this.ticksExisted == this.jumpDelay) {
				this.motionY += 0.5D;
			}

			if (this.ticksExisted > this.jumpDelay) {
				this.motionY -= 0.15D;

				if (this.posY <= this.originY) {
					this.worldObj.setBlock(this.originX, this.originY, this.originZ, this.blockID, this.blockMeta, 3);
					this.setDead();
				}
			}
		}

		if(this.motionY > 0.1D && !this.worldObj.isRemote) {
			DamageSource damageSource;
			Entity owner = this.getOwner();
			if(owner != null) {
				damageSource = new EntityDamageSourceIndirect("player", this, owner);
			} else {
				damageSource = DamageSource.generic;
			}
			List<EntityLivingBase> entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.boundingBox);
			for(EntityLivingBase entity : entities) {
				if (entity != null) {
					if (entity instanceof EntityLivingBase) {
						if(entity.attackEntityFrom(damageSource, 10F)) {
							float knockback = 1.5F;
							Vec3 dir = Vec3.createVectorHelper(this.posX - this.waveStartX, 0, this.posZ - this.waveStartZ);
							dir = dir.normalize();
							entity.motionX = dir.xCoord * knockback;
							entity.motionY = 0.5D;
							entity.motionZ = dir.zCoord * knockback;
						}
					}
				}
			}
		}

		this.moveEntity(this.motionX, this.motionY, this.motionZ);
	}

	@Override
	public boolean handleWaterMovement() {
		return false;
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity entity) {
		return null;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return this.boundingBox;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		data.writeInt(Block.getIdFromBlock(this.blockID));
		data.writeInt(this.blockMeta);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		this.blockID = Block.getBlockById(data.readInt());
		this.blockMeta = data.readInt();
	}

	public void setOrigin(int x, int y, int z, int delay, double waveStartX, double waveStartZ, Entity source) {
		this.originX = x;
		this.originY = y;
		this.originZ = z;
		this.jumpDelay = delay;
		this.waveStartX = waveStartX;
		this.waveStartZ = waveStartZ;
		this.setOwner(source.getUniqueID().toString());
	}

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(OWNER_DW, "");
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound data) {
		this.blockID = Block.getBlockById(data.getInteger("blockID"));
		this.blockMeta = data.getInteger("blockMeta");	
		this.originX = data.getInteger("originX");
		this.originY = data.getInteger("originY");
		this.originZ = data.getInteger("originZ");
		this.waveStartX = data.getDouble("waveStartX");
		this.waveStartZ = data.getDouble("waveStartZ");
		this.jumpDelay = data.getInteger("jumpDelay");
		this.setOwner(data.getString("ownerUUID"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound data) {
		data.setInteger("blockID", Block.getIdFromBlock(this.blockID));
		data.setInteger("blockMeta", this.blockMeta);
		data.setInteger("originX", this.originX);
		data.setInteger("originY", this.originY);
		data.setInteger("originZ", this.originZ);
		data.setDouble("waveStartX", this.waveStartX);
		data.setDouble("waveStartZ", this.waveStartZ);
		data.setInteger("jumpDelay", this.jumpDelay);
		data.setString("ownerUUID", this.getOwnerUUID());
	}
}