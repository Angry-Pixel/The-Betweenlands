package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.container.BlockSmokingRack;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.recipe.misc.SmokingRackRecipe;
import thebetweenlands.common.registries.BlockRegistry;

public class TileEntitySmokingRack extends TileEntity implements ITickable, IInventory {
	public NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(7, ItemStack.EMPTY);
	
	public int MAX_SMOKING_TIME = 200; // 10 seconds per moss for a 64 stack = over 10.6 min IRL
	public int curing_modifier_1 = 1;
	public int curing_modifier_2 = 1;
	public int curing_modifier_3 = 1;
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

        if (getWorld().getBlockState(pos.down()).getBlock() == BlockRegistry.PEAT_SMOULDERING && !active) {
        	active = true;
        	markForUpdate();
        }

        if (getWorld().getBlockState(pos.down()).getBlock() != BlockRegistry.PEAT_SMOULDERING && active) {
        	active = false;
        	markForUpdate();
        }

		if (getWorld().getBlockState(pos).getBlock() instanceof BlockSmokingRack && active) {
			if (updateFuelState()) {
				setSmokeProgress(getSmokeProgress() + 1);
				
				if(this.getSmokeProgress() % 10 == 0) {
					markForUpdate();
				}
				
				if (getSmokeProgress() > MAX_SMOKING_TIME) { // not equal because stuff needs to work on 1 fuel item use
					consumeFuel();
				}
			}

			for(int i = 0; i < 3; i++) {
				if (canSmokeSlots(1 + i, 4 + i)) {
					updateCuringModifier(1 + i);
					
					setSlotProgress(1 + i, getSlotProgress(1 + i) + 1);
					
					if (getSlotProgress(1 + i) >= MAX_SMOKING_TIME * curing_modifier_1) {
						smokeItem(1 + i, 4 + i);
					}
				} else {
					if (getSlotProgress(1 + i) > 0) {
						setSlotProgress(1 + i, 0);
						markForUpdate();
					}
				}
			}

		} else { // just reset all progress if the fuel runs out or inactive I suppose
			if (getSmokeProgress() > 0) {
				setSmokeProgress(0);
				markForUpdate();
			}

			for(int i = 0; i < 3; i++) {
				if (getSlotProgress(1 + i) > 0) {
					setSlotProgress(1 + i, 0);
					markForUpdate();
				}
			}
		}
    }

    private void setSlotProgress(int slot, int counter) {
		switch (slot) {
		case 0:
			break;
		case 1:
			slot_1_progress = counter;
			break;
		case 2:
			slot_2_progress = counter;
			break;
		case 3:
			slot_3_progress = counter;
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
		ItemStack stack = this.getItems().get(slot);
		if(!stack.isEmpty() && stack.getItem() instanceof ItemMob && ((ItemMob) stack.getItem()).hasEntityData(stack)) {
			return ((ItemMob) stack.getItem()).createCapturedEntity(this.world, 0, 0, 0, stack, false);
		}
		return null;
	}

	public void consumeFuel() {
    	ItemStack fuelStack = getItems().get(0);
		setSmokeProgress(0);
		markForUpdate();
		fuelStack.shrink(1);
    }

	private boolean canSmokeSlots(int input, int output) {
		if (!active || !updateFuelState() || getItems().get(input).isEmpty() || !getItems().get(output).isEmpty())
			return false;
		else {
			ItemStack result = SmokingRackRecipe.getRecipeOutput(getItems().get(input));
			if (result.isEmpty())
				return false;
			else {
				ItemStack stack = this.getItems().get(input);
				if(!stack.isEmpty() && stack.getItem() instanceof ItemMob && ((ItemMob) stack.getItem()).hasEntityData(stack)) {
					NBTTagCompound entityNbt = ((ItemMob) stack.getItem()).getEntityData(stack);
					
					if(entityNbt != null && entityNbt.hasKey("fishColour") && (entityNbt.getByte("fishColour") == 0 || entityNbt.getByte("fishColour") == 1)) {
						return false;
					}
				}
				return true;
			}
		}
	}

	private int updateCuringModifier(int slot) {
		int modifier = SmokingRackRecipe.getSmokingRecipe(getItems().get(slot)).getSmokingTime(getItems().get(slot));
		if(modifier <= 0) // just in case
			modifier = 1;
		switch (slot) {
		case 0:
			break;
		case 1:
			if (curing_modifier_1 != modifier) {
				curing_modifier_1 = modifier;
				markForUpdate();
			}
			break;
		case 2:
			if (curing_modifier_2 != modifier) {
				curing_modifier_2 = modifier;
				markForUpdate();
			}
			break;
		case 3:
			if (curing_modifier_3 != modifier) {
				curing_modifier_3 = modifier;
				markForUpdate();
			}
			break;
		}
		return modifier;
	}

	public boolean updateFuelState() {
		ItemStack fuelStack = getItems().get(0);
		if (!fuelStack.isEmpty()) {
			return true;
		} else if (getSmokeProgress() > 0) {
			setSmokeProgress(0);
			markForUpdate();
		}
		return false;
	}

    public void smokeItem(int input, int output) {	
		if (canSmokeSlots(input, output)) {
			ItemStack itemstack = getItems().get(input);
			ItemStack result = SmokingRackRecipe.getRecipeOutput(itemstack);
			ItemStack itemstack2 = getItems().get(output);
			if (itemstack2.isEmpty())
				getItems().set(output, result.copy());
			setSlotProgress(input, 0);
			markForUpdate();
			itemstack.shrink(1);
		}
    }

	public void setSmokeProgress(int duration) {
		smoke_progress = duration;
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
        return getSlotProgress(1) * count / (MAX_SMOKING_TIME * curing_modifier_1);
    }

    @SideOnly(Side.CLIENT)
    public int getItemProgressScaledMid(int index, int count) {
        return getSlotProgress(2) * count / (MAX_SMOKING_TIME * curing_modifier_2);
    }

    @SideOnly(Side.CLIENT)
    public int getItemProgressScaledBottom(int index, int count) {
        return getSlotProgress(3) * count / (MAX_SMOKING_TIME * curing_modifier_3);
    }

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(getPos()).grow(0D, 1D, 0D);
	}

    public void markForUpdate() {
        IBlockState state = this.getWorld().getBlockState(this.getPos());
        this.getWorld().notifyBlockUpdate(this.getPos(), state, state, 2);
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
		active = nbt.getBoolean("active");
		smoke_progress = nbt.getInteger("smoke_progress");
		slot_1_progress = nbt.getInteger("slot_1_progress");
		slot_2_progress = nbt.getInteger("slot_2_progress");
		slot_3_progress = nbt.getInteger("slot_3_progress");

		curing_modifier_1 = nbt.getInteger("curing_modifier_1");
		curing_modifier_2 = nbt.getInteger("curing_modifier_2");
		curing_modifier_3 = nbt.getInteger("curing_modifier_3");
	}

	public NBTTagCompound saveToNbt(NBTTagCompound nbt) {
		ItemStackHelper.saveAllItems(nbt, inventory, false);
		nbt.setBoolean("active", active);
		nbt.setInteger("smoke_progress", smoke_progress);
		nbt.setInteger("slot_1_progress", slot_1_progress);
		nbt.setInteger("slot_2_progress", slot_2_progress);
		nbt.setInteger("slot_3_progress", slot_3_progress);

		nbt.setInteger("curing_modifier_1", curing_modifier_1);
		nbt.setInteger("curing_modifier_2", curing_modifier_2);
		nbt.setInteger("curing_modifier_3", curing_modifier_3);
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
		for (ItemStack itemstack : inventory)
			if (!itemstack.isEmpty())
				return false;
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
