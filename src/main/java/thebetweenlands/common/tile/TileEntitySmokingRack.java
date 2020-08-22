package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.container.BlockSmokingRack;

public class TileEntitySmokingRack extends TileEntity implements ITickable, IInventory {
	public NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(7, ItemStack.EMPTY);
	
	public int MAX_SMOKING_TIME = 240;
	public int smoking_progress = 0;
	public boolean active;

	public TileEntitySmokingRack() {
		super();
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}
	
    @Override
    public void update() {
    	
        BlockPos pos = this.getPos();

        if (getWorld().isRemote) 
            return;

        if (getWorld().getBlockState(pos.down()).getBlock() == Blocks.FIRE && !active)
        	active = true;

        if (getWorld().getBlockState(pos.down()).getBlock() != Blocks.FIRE && active)
        	active = false;
        
		if (getWorld().getBlockState(pos).getBlock() instanceof BlockSmokingRack && (canSmokeSlots(1, 4) || canSmokeSlots(2, 5) || canSmokeSlots(3, 6))) {
			setSmokingProgress(getSmokingProgress() + 1);
			if (getSmokingProgress() >= MAX_SMOKING_TIME) {
				if(canSmokeSlots(1, 4))
					smokeItem(1, 4);
				else if(canSmokeSlots(2, 5))
					smokeItem(2, 5);
				else if(canSmokeSlots(3, 6))
					smokeItem(3, 6);
			}
		}
		else { 
			if (getSmokingProgress() > 0)
				setSmokingProgress(0);
		}

    }
    
	private boolean canSmokeSlots(int input, int output) {
		if (!active)
			return false;
		if (!hasFuel())
			return false;
		if (getItems().get(input).isEmpty())
			return false;
		if (!getItems().get(output).isEmpty())
				return false;
		return true;
	}
    
	private boolean hasFuel() { //temp debug
		ItemStack fuelStack = getItems().get(0);
		if (!fuelStack.isEmpty())
			return true;
		else if (getSmokingProgress() > 0)
			setSmokingProgress(0);
		return false;
	}
	
    public void smokeItem(int input, int output) {
		if (canSmokeSlots(input, output)) {
			ItemStack fuelStack = getItems().get(0);
			ItemStack itemstack = getItems().get(input);
			ItemStack result = new ItemStack(Items.DIAMOND); // temp result
			ItemStack itemstack2 = getItems().get(output);

			if (itemstack2.isEmpty())
				getItems().set(output, result);
			setSmokingProgress(0);
			fuelStack.shrink(1);
			itemstack.shrink(1);
		}
    }

	public void setSmokingProgress(int duration) {
		smoking_progress = duration;
		markForUpdate();
	}

	private int getSmokingProgress() {
		return smoking_progress;
	}

    @SideOnly(Side.CLIENT)
    public int getSmokingProgressScaled(int index, int count) {
        return getSmokingProgress() * count / MAX_SMOKING_TIME;
    }

    public void markForUpdate() {
        IBlockState state = this.getWorld().getBlockState(this.getPos());
        this.getWorld().notifyBlockUpdate(this.getPos(), state, state, 3);
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
		smoking_progress = nbt.getInteger("smoking_progress");
	}

	public NBTTagCompound saveToNbt(NBTTagCompound nbt) {
		ItemStackHelper.saveAllItems(nbt, inventory, false);
        nbt.setInteger("smoking_progress", smoking_progress);
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

	@Override
	public String getName() {
		return new TextComponentTranslation("tile.thebetweenlands.smoking_rack.name").getFormattedText();
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
