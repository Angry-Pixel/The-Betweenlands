package thebetweenlands.entities;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityShockwaveBlock extends EntityFlying implements IEntityAdditionalSpawnData {

	public Block blockID;
	public int blockMeta;

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
		if (!worldObj.isRemote) {
			if (ticksExisted <= 3)
				motionY += 0.20000000149011612D;
			motionX = 0;
			motionZ = 0;
			if (ticksExisted > 3)
				motionY -= 0.15D;

			if (onGround || ticksExisted > 10) {
				worldObj.setBlock((int) (posX - 0.5D), (int) posY, (int) (posZ - 0.5D), blockID, blockMeta, 3);
				setDead();

			}
		}
		super.onUpdate();
	}

	@Override
	protected void collideWithEntity(Entity entity) {
		super.collideWithEntity(entity);
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
}