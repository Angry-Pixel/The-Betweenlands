package thebetweenlands.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import thebetweenlands.herblore.Amounts;
import thebetweenlands.herblore.aspects.Aspect;

public class TileEntityAspectVial extends TileEntity {
	public static final float MAX_AMOUNT = Amounts.MAX_VIAL_ASPECT_AMOUNT;
	
	private Aspect aspect = null;

	@Override
	public boolean canUpdate() {
		return false;
	}

	/**
	 * Tries to add an amount and returns the added amount
	 * @param amount
	 * @return
	 */
	public float addAmount(float amount) {
		float canAdd = MAX_AMOUNT - this.aspect.amount;
		if(canAdd > 0.0F) {
			float added = Math.min(canAdd, amount);
			this.aspect = new Aspect(this.aspect.type, this.aspect.amount + added);
			this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
			return added;
		}
		return 0.0F;
	}

	/**
	 * Tries to remove an amount and returns the removed amount
	 * @param amount
	 * @return
	 */
	public float removeAmount(float amount) {
		float removed = Math.min(this.aspect.amount, amount);
		if(removed < this.aspect.amount) {
			this.aspect = new Aspect(this.aspect.type, this.aspect.amount - removed);
		} else {
			this.aspect = null;
		}
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		return removed;
	}

	public Aspect getAspect() {
		return this.aspect;
	}

	public void setAspect(Aspect aspect) {
		this.aspect = aspect;
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(this.aspect != null)
			this.aspect.writeToNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(nbt.hasKey("aspect")) {
			this.aspect = Aspect.readFromNBT(nbt);
		} else {
			this.aspect = null;
		}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		this.readFromNBT(packet.func_148857_g());
	}
}