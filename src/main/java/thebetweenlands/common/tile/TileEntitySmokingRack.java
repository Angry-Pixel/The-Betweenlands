package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.container.BlockSmokingRack;
import thebetweenlands.common.entity.mobs.EntityAnadia;
import thebetweenlands.common.registries.ItemRegistry;

public class TileEntitySmokingRack extends TileEntity implements ITickable, IInventory {
	public NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(7, ItemStack.EMPTY);
	
	public int MAX_SMOKING_TIME = 800;
	public int CURING_MODIFIER_1 = 1;
	public int CURING_MODIFIER_2 = 1;
	public int CURING_MODIFIER_3 = 1;
	public int smoke_progress = 0;
	public int slot_1_progress = 0;
	public int slot_2_progress = 0;
	public int slot_3_progress = 0;
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
        
		if (getWorld().getBlockState(pos).getBlock() instanceof BlockSmokingRack && active) {
			if (hasFuel()) {
				setSmokeProgress(getSmokeProgress() + 1);
				if (getSmokeProgress() > MAX_SMOKING_TIME) // not equal because stuff needs to work on 1 fuel item use
					consumeFuel();
			}

			if (canSmokeSlots(1, 4)) {
				setSlotProgress(1, getSlotProgress(1) + 1);
				if (getSlotProgress(1) >= MAX_SMOKING_TIME * CURING_MODIFIER_1) // 3 times
					smokeItem(1, 4);
			} else {
				if (getSlotProgress(1) > 0)
					setSlotProgress(1, 0);
			}

			if (canSmokeSlots(2, 5)) {
				setSlotProgress(2, getSlotProgress(2) + 1);
				if (getSlotProgress(2) >= MAX_SMOKING_TIME * CURING_MODIFIER_2) // 1 times
					smokeItem(2, 5);
			} else {
				if (getSlotProgress(2) > 0)
					setSlotProgress(2, 0);
			}

			if (canSmokeSlots(3, 6)) {
				setSlotProgress(3, getSlotProgress(3) + 1);
				if (getSlotProgress(3) >= MAX_SMOKING_TIME * CURING_MODIFIER_3) // 2 times
					smokeItem(3, 6);
			} else {
				if (getSlotProgress(3) > 0)
					setSlotProgress(3, 0);
			}

		} else { // just reset all progress if the fuel runs out or inactive I suppose
			if (getSmokeProgress() > 0)
				setSmokeProgress(0);
			
			if (getSlotProgress(1) > 0)
				setSlotProgress(1, 0);
			
			if (getSlotProgress(2) > 0)
				setSlotProgress(2, 0);
			
			if (getSlotProgress(3) > 0)
				setSlotProgress(3, 0);
		}
    }

    private void setSlotProgress(int slot, int counter) {
		switch (slot) {
		case 0:
			break;
		case 1:
			slot_1_progress = counter;
			markForUpdate();
			break;
		case 2:
			slot_2_progress = counter;
			markForUpdate();
			break;
		case 3:
			slot_3_progress = counter;
			markForUpdate();
			break;
		}
	}

	private int getSlotProgress(int slot) {
		switch (slot) {
		case 0:
			break;
		case 1:
			return slot_1_progress;
		case 2:
			return slot_2_progress;
		case 3:
			return slot_3_progress;
		}
		return 0;
	}

	public Entity getRenderEntity(int slot) {
		Entity entity = EntityList.createEntityFromNBT(getItems().get(slot).getTagCompound().getCompoundTag("Entity"), getWorld());
		entity.setPositionAndRotation(0D, 0D, 0D, -90, 90F);
		return entity;
	}

	public void consumeFuel() {
    	ItemStack fuelStack = getItems().get(0);
		setSmokeProgress(0);
		fuelStack.shrink(1);
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
		if (getRenderEntity(input) instanceof EntityAnadia && ((EntityAnadia)getRenderEntity(input)).getFishColour() == 2)
			return false;
		return true;
	}
    
	private boolean hasFuel() { //temp debug
		ItemStack fuelStack = getItems().get(0);
		if (!fuelStack.isEmpty())
			return true;
		else if (getSmokeProgress() > 0)
			setSmokeProgress(0);
		return false;
	}
	
    public void smokeItem(int input, int output) {	
		if (canSmokeSlots(input, output)) {
			ItemStack itemstack = getItems().get(input);
			ItemStack result = itemstack.copy(); // temp result
			ItemStack itemstack2 = getItems().get(output);
			if (itemstack2.isEmpty())
				getItems().set(output, result);
			if(result.getItem() == ItemRegistry.ANADIA && result.getTagCompound() != null && result.getTagCompound().hasKey("Entity", Constants.NBT.TAG_COMPOUND)) {
				result.getTagCompound().getCompoundTag("Entity").setByte("fishColour", (byte) 2);
			}
			setSlotProgress(input, 0);
			itemstack.shrink(1);
		}
    }

	public void setSmokeProgress(int duration) {
		smoke_progress = duration;
		markForUpdate();
	}

	private int getSmokeProgress() {
		return smoke_progress;
	}

    @SideOnly(Side.CLIENT)
    public int getSmokeProgressScaled(int index, int count) {
        return getSmokeProgress() * count / MAX_SMOKING_TIME;
    }
    
    @SideOnly(Side.CLIENT)
    public int getItemProgressScaledTop(int index, int count) {
        return getSlotProgress(1) * count / (MAX_SMOKING_TIME * CURING_MODIFIER_1);
    }
    
    @SideOnly(Side.CLIENT)
    public int getItemProgressScaledMid(int index, int count) {
        return getSlotProgress(2) * count / (MAX_SMOKING_TIME * CURING_MODIFIER_2);
    }
    
    @SideOnly(Side.CLIENT)
    public int getItemProgressScaledBottom(int index, int count) {
        return getSlotProgress(3) * count / (MAX_SMOKING_TIME * CURING_MODIFIER_3);
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
		smoke_progress = nbt.getInteger("smoke_progress");
		slot_1_progress = nbt.getInteger("slot_1_progress");
		slot_2_progress = nbt.getInteger("slot_2_progress");
		slot_3_progress = nbt.getInteger("slot_3_progress");
	}

	public NBTTagCompound saveToNbt(NBTTagCompound nbt) {
		ItemStackHelper.saveAllItems(nbt, inventory, false);
		nbt.setInteger("smoke_progress", smoke_progress);
		nbt.setInteger("slot_1_progress", slot_1_progress);
		nbt.setInteger("slot_2_progress", slot_2_progress);
		nbt.setInteger("slot_3_progress", slot_3_progress);
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
