package thebetweenlands.common.entity;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;

public class EntityShockwaveBlock extends Entity implements IEntityAdditionalSpawnData {
	private static final DataParameter<String> OWNER_DW = EntityDataManager.<String>createKey(EntityShockwaveBlock.class, DataSerializers.STRING);

	public Block block;
	public int blockMeta;
	public int jumpDelay;
	public BlockPos origin;
	private double waveStartX, waveStartZ;

	public EntityShockwaveBlock(World world) {
		super(world);
		this.setSize(1.0F, 1.0F);
		this.setBlock(Blocks.STONE, 0);
		this.noClip = true;
	}

	public void setBlock(Block blockID, int blockMeta) {
		this.block = blockID;
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
		for (int i = 0; i < world.loadedEntityList.size(); ++i) {
			Entity entity = (Entity)world.loadedEntityList.get(i);
			if (id.equals(entity.getUniqueID())) {
				return entity;
			}
		}
		return null;
	}

	@Override
	public void onUpdate() {
		this.world.profiler.startSection("entityBaseTick");

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionX = 0;
		this.motionZ = 0;
		this.lastTickPosX = this.posX;
		this.lastTickPosY = this.posY;
		this.lastTickPosZ = this.posZ;

		if(this.ticksExisted >= this.jumpDelay) {
			if(this.ticksExisted == this.jumpDelay && this.motionY <= 0.0D) {
				this.motionY += 0.25D;
			} else {
				this.motionY -= 0.05D;

				if(!this.world.isRemote && (this.posY <= this.origin.getY() || this.onGround)) {
					this.setDead();
				}
			}
		} else {
			this.motionY = 0.0D;
		}

		if(this.posY < -64.0D) {
			this.setDead();
		}

		if(this.posY + this.motionY <= this.origin.getY()) {
			this.motionY = 0.0D;
			this.setLocationAndAngles(this.posX, this.origin.getY(), this.posZ, 0, 0);
		} else {
			this.move(MoverType.SELF, 0, this.motionY, 0);
		}

		if(this.motionY > 0.1D && !this.world.isRemote) {
			DamageSource damageSource;
			Entity owner = getOwner();
			if(owner != null) {
				damageSource = new EntityDamageSourceIndirect("player", this, owner);
			} else {
				damageSource = new EntityDamageSource("bl.shockwave", this);
			}
			List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, getEntityBoundingBox().grow(0.1D, 0.1D, 0.1D));
			for(EntityLivingBase entity : entities) {      
				if (entity != null) {                      
					if (entity instanceof EntityLivingBase) {
						if(entity.attackEntityFrom(damageSource, 10F)) {
							float knockback = 1.5F;
							Vec3d dir = new Vec3d(this.posX - this.waveStartX, 0, this.posZ - this.waveStartZ);
							dir = dir.normalize();
							entity.motionX = dir.x * knockback;
							entity.motionY = 0.5D;
							entity.motionZ = dir.z * knockback;
							if (entity.getHealth() <= 0 && owner instanceof EntityPlayerMP) {
								AdvancementCriterionRegistry.SHOCKWAVE_KILL.trigger((EntityPlayerMP) owner, entity);
							}
						}
					}
				}
			}
		}

		this.firstUpdate = false;
		this.world.profiler.endSection();
	}

	@Override
	public void setDead() {
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
	@Nullable
	public AxisAlignedBB getCollisionBox(Entity entityIn) {
		return entityIn.getEntityBoundingBox();
	}

	@Override
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox() {
		return this.getEntityBoundingBox();
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		PacketBuffer buffer = new PacketBuffer(data);
		buffer.writeInt(Block.getIdFromBlock(this.block));
		buffer.writeInt(this.blockMeta);
		buffer.writeBlockPos(this.origin);
		buffer.writeInt(this.jumpDelay);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		PacketBuffer buffer = new PacketBuffer(data);
		this.block = Block.getBlockById(buffer.readInt());
		this.blockMeta = buffer.readInt();
		this.origin = buffer.readBlockPos();
		this.jumpDelay = buffer.readInt();
	}

	public void setOrigin(BlockPos pos, int delay, double waveStartX, double waveStartZ, Entity source) {
		this.origin = pos;
		this.jumpDelay = delay;
		this.waveStartX = waveStartX;
		this.waveStartZ = waveStartZ;
		this.setOwner(source.getUniqueID().toString());
	}

	@Override
	protected void entityInit() {
		this.getDataManager().register(OWNER_DW, "");
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound data) {
		this.block = Block.getBlockById(data.getInteger("blockID"));
		if(this.block == null)
			this.block = Blocks.STONE;
		this.blockMeta = data.getInteger("blockMeta");
		this.origin = new BlockPos(data.getInteger("originX"), data.getInteger("originY"), data.getInteger("originZ"));
		this.waveStartX = data.getDouble("waveStartX");
		this.waveStartZ = data.getDouble("waveStartZ");
		this.jumpDelay = data.getInteger("jumpDelay");
		setOwner(data.getString("ownerUUID"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound data) {
		data.setInteger("blockID", Block.getIdFromBlock(this.block));
		data.setInteger("blockMeta", this.blockMeta);
		data.setInteger("originX", this.origin.getX());
		data.setInteger("originY", this.origin.getY());
		data.setInteger("originZ", this.origin.getZ());
		data.setDouble("waveStartX", this.waveStartX);
		data.setDouble("waveStartZ", this.waveStartZ);
		data.setInteger("jumpDelay", this.jumpDelay);
		data.setString("ownerUUID", this.getOwnerUUID());
	}
}