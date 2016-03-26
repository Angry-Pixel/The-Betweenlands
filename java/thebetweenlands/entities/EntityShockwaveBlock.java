package thebetweenlands.entities;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityShockwaveBlock extends EntityFlying implements IEntityAdditionalSpawnData {

	public Block blockID;
	public int blockMeta;
	public int originX, originY, originZ, jumpDelay;

	public EntityShockwaveBlock(World world) {
		super(world);
		setSize(0.9F, 0.9F);
		setBlock(Blocks.stone, 0);
		experienceValue = 0;
	}

	public void setBlock(Block blockID, int blockMeta) {
		this.blockID = blockID;
		this.blockMeta = blockMeta;
	}

	@Override
	public void onUpdate() {
		motionX = 0;
		motionZ = 0;
		posX = lastTickPosX;
		posZ = lastTickPosZ;
		if (!worldObj.isRemote) {
			if (ticksExisted == jumpDelay)
				motionY += 0.5D;
			
			if (ticksExisted > jumpDelay) {
				motionY -= 0.15D;

				if (posY <= originY) {
					worldObj.setBlock(originX, originY, originZ, blockID, blockMeta, 3);
					setDead();
				}
			}
		}
		super.onUpdate();
	}

	@Override
	protected void collideWithEntity(Entity entity) {
		super.collideWithEntity(entity);
		if (entity != null)
			if (entity instanceof EntityLivingBase && !(entity instanceof EntityShockwaveBlock)) {
				float Knockback = 0.5F;
				entity.attackEntityFrom(DamageSource.causeMobDamage((EntityLivingBase) entity), 10F);
				entity.addVelocity(-MathHelper.sin(entity.rotationYaw * -3.141593F + worldObj.rand.nextInt(3) + 0.141593F / 180.0F) * Knockback, 0.25D, MathHelper.cos(entity.rotationYaw * -3.141593F + worldObj.rand.nextInt(3) + 0.141593F / 180.0F) * Knockback);
			}
	}

	@Override
	public boolean canBePushed() {
		return true;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound data) {
		super.writeEntityToNBT(data);
		data.setInteger("blockID", Block.getIdFromBlock(blockID));
		data.setInteger("blockMeta", blockMeta);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound data) {
		super.readEntityFromNBT(data);
		blockID = Block.getBlockById(data.getInteger("blockID"));
		blockMeta = data.getInteger("blockMeta");
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

	public void setOrigin(int x, int y, int z, int delay) {
		originX = x;
		originY = y;
		originZ = z;
		jumpDelay = delay;
	}
}