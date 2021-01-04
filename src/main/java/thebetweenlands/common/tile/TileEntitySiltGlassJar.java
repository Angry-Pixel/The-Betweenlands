package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;

public class TileEntitySiltGlassJar extends TileEntity implements IInventory {

	public NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(8, ItemStack.EMPTY);

	public TileEntitySiltGlassJar() {
		super();
	}

	@Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        loadFromNbt(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        return saveToNbt(nbt);
    }
  
	public void loadFromNbt(NBTTagCompound nbt) {
		inventory = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
		if (nbt.hasKey("Items", 9))
			ItemStackHelper.loadAllItems(nbt, inventory);
	}

	public NBTTagCompound saveToNbt(NBTTagCompound nbt) {
		ItemStackHelper.saveAllItems(nbt, inventory, false);
		return nbt;
	}

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

	public void markForUpdate() {
		IBlockState state = this.getWorld().getBlockState(this.getPos());
		this.getWorld().notifyBlockUpdate(this.getPos(), state, state, 3);
	}

    @Override
	public String getName() {
		return new TextComponentTranslation("tile.thebetweenlands.silt_glass_jar.name").getFormattedText();
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	public NonNullList<ItemStack> getItems() {
		return inventory;
	}

	@Override
	public int getSizeInventory() {
		return inventory.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : inventory) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.get(slot);
	}

	@Override
    public ItemStack decrStackSize(int index, int count) {
		ItemStack itemstack = ItemStackHelper.getAndSplit(inventory, index, count);
		if (!itemstack.isEmpty())
			this.markDirty();
		return itemstack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStack.EMPTY;
	}

	@Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        inventory.set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit())
            stack.setCount(this.getInventoryStackLimit());
        this.markDirty();
    }

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		inventory.clear();
	}

}
