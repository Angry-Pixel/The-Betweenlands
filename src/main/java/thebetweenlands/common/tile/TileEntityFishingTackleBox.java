package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import thebetweenlands.common.entity.EntityFishingTackleBoxSeat;

public class TileEntityFishingTackleBox extends TileEntity implements ITickable, IInventory {

	public NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(16, ItemStack.EMPTY);

    public final float MAX_OPEN = 120f;
    public final float MIN_OPEN = 0f;
    public final float OPEN_SPEED = 10f;
    public final float CLOSE_SPEED = 10f;

    private boolean open = false;
    private float lid_angle = 0.0f;

    public TileEntityFishingTackleBox() {
    	super();
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public void update() {
        lid_angle = open ? Math.min(lid_angle + OPEN_SPEED, MAX_OPEN) : Math.max(lid_angle - CLOSE_SPEED, MIN_OPEN);
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
        open = nbt.getBoolean("open");
        lid_angle = nbt.getFloat("lid_angle");
	}

	public NBTTagCompound saveToNbt(NBTTagCompound nbt) {
		ItemStackHelper.saveAllItems(nbt, inventory, false);
        nbt.setBoolean("open", open);
        nbt.setFloat("lid_angle", lid_angle);
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

    public float getLidAngle(float partialTicks) {
        return open ? Math.min(lid_angle + OPEN_SPEED * partialTicks, MAX_OPEN) : Math.max(lid_angle - CLOSE_SPEED * partialTicks, MIN_OPEN);
    }

	public void seatPlayer(EntityPlayer player, BlockPos pos) {
		EntityFishingTackleBoxSeat entitySeat = new EntityFishingTackleBoxSeat(world);
		entitySeat.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ()  + 0.5D);
		entitySeat.setSeatOffset(0.1F);
		world.spawnEntity(entitySeat);
		player.startRiding(entitySeat, true);
	}

	@Override
	public String getName() {
		return new TextComponentTranslation("tile.thebetweenlands.fishing_tackle_box.name").getFormattedText();
	}

	@Override
	public boolean hasCustomName() {
		return false;
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
		return 64;
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
