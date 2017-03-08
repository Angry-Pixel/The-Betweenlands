package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import thebetweenlands.common.registries.SoundRegistry;

public class TileEntitySpikeTrap extends TileEntity implements ITickable {

	public int prevAnimationTicks;
	public int animationTicks;
	public boolean active;
	public byte type;

	@Override
	public void update() {
		if (!worldObj.isRemote) {
			if (!worldObj.isAirBlock(pos.up())) {
				setType((byte) 1);
				setActive(true);
				Block block = worldObj.getBlockState(pos.up()).getBlock();
				worldObj.playEvent(null, 2001, pos.up(), Block.getIdFromBlock(worldObj.getBlockState(pos.up()).getBlock()));
				block.dropBlockAsItem(worldObj, pos.up(), worldObj.getBlockState(pos.up()), 0);
				worldObj.setBlockToAir(pos.up());
			}
			if (!worldObj.isAirBlock(pos.up(2))) {
				setType((byte) 1);
				setActive(true);
				Block block = worldObj.getBlockState(pos.up(2)).getBlock();
				worldObj.playEvent(null, 2001, pos.up(2), Block.getIdFromBlock(worldObj.getBlockState(pos.up(2)).getBlock()));
				block.dropBlockAsItem(worldObj, pos.up(2), worldObj.getBlockState(pos.up(2)), 0);
				worldObj.setBlockToAir(pos.up(2));
			}
			if (worldObj.rand.nextInt(500) == 0) {
				if (type != 0 && !active && animationTicks == 0)
					setType((byte) 0);
				else if (isBlockOccupied() == null)
					setType((byte) 1);
			}
			
			if (isBlockOccupied() != null && type != 0)
				if(!active && animationTicks == 0)
					setActive(true);

		}
		this.prevAnimationTicks = this.animationTicks;
		if (active) {
			activateBlock();
			if (animationTicks == 0)
				worldObj.playSound(null, (double) pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundRegistry.SPIKE, SoundCategory.BLOCKS, 1.25F, 1.0F);
			if (animationTicks <= 20)
				animationTicks += 4;
			if (animationTicks == 20 && !this.worldObj.isRemote)
				setActive(false);
		}
		if (!active)
			if (animationTicks >= 1)
				animationTicks--;
	}

	public void setActive(boolean isActive) {
		active = isActive;
		worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
	}

	public void setType(byte blockType) {
		type = blockType;
		worldObj.notifyBlockUpdate(pos, worldObj.getBlockState(pos), worldObj.getBlockState(pos), 3);
	}

	protected Entity activateBlock() {
		float y = 1F / 16 * animationTicks;
		List<EntityLivingBase> list = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1D, pos.getY() + 1D + y, pos.getZ() + 1D));
		if (animationTicks >= 1)
			for (int i = 0; i < list.size(); i++) {
				Entity entity = list.get(i);
				if (entity != null)
					//if (entity instanceof EntityPlayer)
						((EntityLivingBase) entity).attackEntityFrom(DamageSource.generic, 2);
			}
		return null;
	}

	protected Entity isBlockOccupied() {
		List<EntityLivingBase> list = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.getX() + 0.25D, pos.getY(), pos.getZ() + 0.25D, pos.getX() + 0.75D, pos.getY() + 2D, pos.getZ() + 0.75D));
		for (int i = 0; i < list.size(); i++) {
			Entity entity = list.get(i);
			if (entity != null)
				//if (entity instanceof EntityPlayer)
					return entity;
		}
		return null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("animationTicks", animationTicks);
		nbt.setBoolean("active", active);
		nbt.setByte("type", type);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		animationTicks = nbt.getInteger("animationTicks");
		active = nbt.getBoolean("active");
		type = nbt.getByte("type");
	}

	@Override
    public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = new NBTTagCompound();
        return writeToNBT(nbt);
    }

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new SPacketUpdateTileEntity(pos, 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

}