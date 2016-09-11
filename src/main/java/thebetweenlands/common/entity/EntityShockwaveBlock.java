package thebetweenlands.common.entity;

import java.util.List;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityShockwaveBlock extends Entity implements IEntityAdditionalSpawnData {
	private static final DataParameter<String> OWNER_DW = EntityDataManager.<String>createKey(EntitySwordEnergy.class, DataSerializers.STRING);

	public Block blockID;
	public int blockMeta;
	public int originX, originY, originZ, jumpDelay;
	private double waveStartX, waveStartZ;

	public EntityShockwaveBlock(World world) {
		super(world);
		setSize(1.0F, 1.0F);
		setBlock(Blocks.STONE, 0);
	}

	public void setBlock(Block blockID, int blockMeta) {
		this.blockID = blockID;
		this.blockMeta = blockMeta;
	}

	public void setOwner(String ownerUUID) {
		dataManager.set(OWNER_DW, ownerUUID);
	}

	public String getOwnerUUID() {
		return dataManager.get(OWNER_DW);
	}

	public Entity getOwner() {
		try {
			UUID uuid = UUID.fromString(getOwnerUUID());
			return uuid == null ? null : getEntityByUUID(uuid);
		} catch (IllegalArgumentException illegalargumentexception) {
			return null;
		}
	}

	private Entity getEntityByUUID(UUID id) {
		for (int i = 0; i < worldObj.loadedEntityList.size(); ++i) {
			Entity entity = (Entity)worldObj.loadedEntityList.get(i);
			if (id.equals(entity.getUniqueID())) {
				return entity;
			}
		}
		return null;
	}

	@Override
	public void onUpdate() {
		if(!worldObj.isRemote && (isDead || getDistance(originX + 0.5D, originY, originZ + 0.5D) > 2)) {
			if(worldObj.isAirBlock(new BlockPos(originX, originY, originZ))) {
				worldObj.setBlockState(new BlockPos(originX, originY, originZ), blockID.getStateFromMeta(blockMeta), 3);
			}
		}

		motionX = 0;
		motionZ = 0;
		posX = lastTickPosX;
		posZ = lastTickPosZ;
		isAirBorne = true;
		if (!worldObj.isRemote) {
			if (ticksExisted == jumpDelay) {
				motionY += 0.5D;
			}

			if (ticksExisted > jumpDelay) {
				motionY -= 0.25D;

				if (posY <= originY || onGround) {
					worldObj.setBlockState(new BlockPos(originX, originY, originZ), blockID.getStateFromMeta(blockMeta), 3);
					setDead();
				}
			}
		}

		if(motionY > 0.1D && !worldObj.isRemote) {
			DamageSource damageSource;
			Entity owner = getOwner();
			if(owner != null) {
				damageSource = new EntityDamageSourceIndirect("player", this, owner);
			} else {
				damageSource = DamageSource.generic;
			}
			List<EntityLivingBase> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox());
			for(EntityLivingBase entity : entities) {
				if (entity != null) {
					if (entity instanceof EntityLivingBase) {
						if(entity.attackEntityFrom(damageSource, 10F)) {
							float knockback = 1.5F;
							Vec3d dir = new Vec3d(posX - waveStartX, 0, posZ - waveStartZ);
							dir = dir.normalize();
							entity.motionX = dir.xCoord * knockback;
							entity.motionY = 0.5D;
							entity.motionZ = dir.zCoord * knockback;
						}
					}
				}
			}
		}

		moveEntity(motionX, motionY, motionZ);
	}

	@Override
	public void setDead() {
		if(worldObj.isAirBlock(new BlockPos(originX, originY, originZ))) {
			worldObj.setBlockState(new BlockPos(originX, originY, originZ), blockID.getStateFromMeta(blockMeta), 3);
		}
		super.setDead();
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
	public boolean canBePushed() {
		return false;
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		data.writeInt(Block.getIdFromBlock(blockID));
		data.writeInt(blockMeta);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		blockID = Block.getBlockById(data.readInt());
		blockMeta = data.readInt();
	}

	public void setOrigin(int x, int y, int z, int delay, double waveStartX, double waveStartZ, Entity source) {
		originX = x;
		originY = y;
		originZ = z;
		jumpDelay = delay;
		this.waveStartX = waveStartX;
		this.waveStartZ = waveStartZ;
		setOwner(source.getUniqueID().toString());
	}

	@Override
	protected void entityInit() {
		dataManager.register(OWNER_DW, "");
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound data) {
		blockID = Block.getBlockById(data.getInteger("blockID"));
		blockMeta = data.getInteger("blockMeta");	
		originX = data.getInteger("originX");
		originY = data.getInteger("originY");
		originZ = data.getInteger("originZ");
		waveStartX = data.getDouble("waveStartX");
		waveStartZ = data.getDouble("waveStartZ");
		jumpDelay = data.getInteger("jumpDelay");
		setOwner(data.getString("ownerUUID"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound data) {
		data.setInteger("blockID", Block.getIdFromBlock(blockID));
		data.setInteger("blockMeta", blockMeta);
		data.setInteger("originX", originX);
		data.setInteger("originY", originY);
		data.setInteger("originZ", originZ);
		data.setDouble("waveStartX", waveStartX);
		data.setDouble("waveStartZ", waveStartZ);
		data.setInteger("jumpDelay", jumpDelay);
		data.setString("ownerUUID", getOwnerUUID());
	}
}