package thebetweenlands.common.tile;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import thebetweenlands.common.entity.EntitySwordEnergy;


public class TileEntityItemCage extends TileEntity implements ITickable {

	public byte type; // type will be used for each sword part rendering
	public boolean canBreak;

	@Override
	public void update() {
		if (!world.isRemote) {
			if (isBlockOccupied() != null)
				if (!canBreak)
					setCanBeBroken(true);

			if (isBlockOccupied() == null)
				if (canBreak)
					setCanBeBroken(false);
		}
	}

	public void setCanBeBroken(boolean isBreakable) {
		canBreak = isBreakable;
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
	}

	public void setType(byte blockType) {
		type = blockType;
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
	}

	@SuppressWarnings("unchecked")
	protected Entity isBlockOccupied() {
		List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.getX() + 0.25D, pos.getY() - 3D, pos.getZ() + 0.25D, pos.getX() + 0.75D, pos.getY(), pos.getZ() + 0.75D));
		for (int i = 0; i < list.size(); i++) {
			Entity entity = list.get(i);
			if (entity != null)
				if (entity instanceof EntityPlayer)
					return entity;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Entity isSwordEnergyBelow() {
		List<EntitySwordEnergy> list = world.getEntitiesWithinAABB(EntitySwordEnergy.class, new AxisAlignedBB(pos.getX() - 9D, pos.getY() - 2D, pos.getZ() - 9D, pos.getX() + 10D, pos.getY() + 3D, pos.getZ() + 10D));
		for (int i = 0; i < list.size(); i++) {
			EntitySwordEnergy entity = list.get(i);
			if (entity != null)
				return entity;
		}
		return null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("canBreak", canBreak);
		nbt.setByte("type", type);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		canBreak = nbt.getBoolean("canBreak");
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
		return new SPacketUpdateTileEntity(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}
}