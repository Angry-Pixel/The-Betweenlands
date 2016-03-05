package thebetweenlands.tileentities;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntitySwordStone extends TileEntity {

	public byte type; // type will be used for each sword part rendering
	public boolean canBreak;

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		if (!worldObj.isRemote) {
			if (isBlockOccupied() != null)
				if (!canBreak) {
					setCanBeBroken(true);
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}

			if (isBlockOccupied() == null)
				if (canBreak) {
					setCanBeBroken(false);
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
		}
	}

	public void setCanBeBroken(boolean isBreakable) {
		canBreak = isBreakable;
	}

	public void setType(byte blockType) {
		type = blockType;
	}

	@SuppressWarnings("unchecked")
	protected Entity isBlockOccupied() {
		List<EntityLivingBase> list = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(xCoord + 0.25D, yCoord - 3D, zCoord + 0.25D, xCoord + 0.75D, yCoord, zCoord + 0.75D));
		for (int i = 0; i < list.size(); i++) {
			Entity entity = list.get(i);
			if (entity != null)
				if (entity instanceof EntityPlayer)
					return entity;
		}
		return null;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("canBreak", canBreak);
		nbt.setByte("type", type);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		canBreak = nbt.getBoolean("canBreak");
		type = nbt.getByte("type");
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("canBreak", canBreak);
		nbt.setByte("type", type);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		canBreak = packet.func_148857_g().getBoolean("canBreak");
		type = packet.func_148857_g().getByte("type");
	}
}