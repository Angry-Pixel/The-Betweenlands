package thebetweenlands.common.tile;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.common.inventory.InventoryWeedwoodWorkbench;
import thebetweenlands.common.registries.TileEntityRegistry;

public class TileEntityWeedwoodWorkbench extends TileEntity {
	public NonNullList<ItemStack> craftingSlots = NonNullList.withSize(9, ItemStack.EMPTY);
	public ItemStack craftResult;
	public byte rotation = 0;

	private Set<InventoryWeedwoodWorkbench> openInventories = new HashSet<>();

	public TileEntityWeedwoodWorkbench() {
		super(TileEntityRegistry.WEEDWOOD_WORKBENCH);
	}
	
	public void openInventory(InventoryWeedwoodWorkbench inv) {
		this.openInventories.add(inv);
	}

	public void closeInventory(InventoryWeedwoodWorkbench inv) {
		this.openInventories.remove(inv);
	}

	/**
	 * Notifies *all* open inventories of the changes, fixes dupe bug as in #532
	 */
	public void onCraftMatrixChanged() {
		for(InventoryWeedwoodWorkbench inv : this.openInventories) {
			inv.eventHandler.onCraftMatrixChanged(inv);
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 0, this.writeNbt(new NBTTagCompound()));
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		this.readNBT(packet.getNbtCompound());
	}

	@Override
	public void read(NBTTagCompound nbt) {
		super.read(nbt);
		this.readNBT(nbt);
	}

	private NBTTagCompound readNBT(NBTTagCompound nbt) {
		NBTTagList items = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);

		int count = items.size();
		for (int i = 0; i < count; i++) {
			NBTTagCompound nbtItem = items.getCompound(i);
			this.craftingSlots.set(nbtItem.getByte("Slot"), new ItemStack(nbtItem));
		}

		this.onCraftMatrixChanged();

		this.rotation = nbt.getByte("Rotation");

		return nbt;
	}

	@Override
	public NBTTagCompound write(NBTTagCompound nbt) {
		nbt = super.write(nbt);
		return this.writeNbt(nbt);
	}

	private NBTTagCompound writeNbt(NBTTagCompound nbt) {
		NBTTagList items = new NBTTagList();

		for (int i = 0; i < craftingSlots.size(); i++) {
			if (!this.craftingSlots.get(i).isEmpty()) {
				NBTTagCompound nbtItem = new NBTTagCompound();
				nbtItem.setByte("Slot", (byte) i);
				this.craftingSlots.get(i).writeToNBT(nbtItem);
				items.add(nbtItem);
			}
		}

		nbt.setTag("Items", items);

		nbt.setByte("Rotation", this.rotation);

		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeNbt(super.getUpdateTag());
	}
}
