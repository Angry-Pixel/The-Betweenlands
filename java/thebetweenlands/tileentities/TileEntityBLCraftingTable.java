package thebetweenlands.tileentities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

public class TileEntityBLCraftingTable extends TileEntity {
	public ItemStack[] crfSlots = new ItemStack[9];
	public ItemStack crfResult;
	public byte rotation = 0;

	@Override
	public boolean canUpdate() {
		return false;
	}

	@Override
	public Packet getDescriptionPacket() {
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, this.writeNbt(new NBTTagCompound()));
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readNbt(pkt.func_148857_g());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.readNbt(nbt);
	}

	private NBTTagCompound readNbt(NBTTagCompound nbt) {
		NBTTagList items = nbt.getTagList("items", Constants.NBT.TAG_COMPOUND);

		int count = items.tagCount();
		for( int i = 0; i < count; i++ ) {
			NBTTagCompound nbtItem = items.getCompoundTagAt(i);
			this.crfSlots[nbtItem.getByte("slot")] = ItemStack.loadItemStackFromNBT(nbtItem);
		}

		this.rotation = nbt.getByte("rotation");

		return nbt;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		this.writeNbt(nbt);
	}

	private NBTTagCompound writeNbt(NBTTagCompound nbt) {
		NBTTagList items = new NBTTagList();
		for( int i = 0; i < crfSlots.length; i++ ) {
			if( this.crfSlots[i] != null ) {
				NBTTagCompound nbtItem = new NBTTagCompound();
				nbtItem.setByte("slot", (byte) i);
				this.crfSlots[i].writeToNBT(nbtItem);
				items.appendTag(nbtItem);
			}
		}

		nbt.setTag("items", items);

		nbt.setByte("rotation", this.rotation);

		return nbt;
	}
}
