package thebetweenlands.common.tile;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.block.container.BlockCrabPotFilter;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.recipe.misc.CrabPotFilterRecipeBubbler;
import thebetweenlands.common.recipe.misc.CrabPotFilterRecipeSilt;
import thebetweenlands.common.registries.BlockRegistry;

public class TileEntityCrabPotFilter extends TileEntity implements ITickable, ISidedInventory {
	public NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(3, ItemStack.EMPTY);
	
	public int MAX_FILTERING_TIME = 200; // 10 seconds per item for a 64 stack = over 10.6 min IRL

	public int bait_progress = 0;
	public int filtering_progress = 0;
	public int items_to_filter_count = 3; // logic here means 1 already in the chamber + this 

	public boolean active;
	public int horizontalIndex = 0;

    private final int baitSlot = 0;
    private final int inputSlot = 1;
    private final int outputSlot = 2;
    private final int[] resultSideSlot = { 2 };
    private final int allSideSlots[] = { 0, 1 };

	public TileEntityCrabPotFilter() {
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

        if (getWorld().getBlockState(pos.up()).getBlock() == BlockRegistry.CRAB_POT && !active) {
        	if(hasCrabInTile()) {
        		active = true;
        		markForUpdate();
        	}
        }
        
        if (getWorld().getBlockState(pos.up()).getBlock() == BlockRegistry.CRAB_POT) {
        	if(hasCrabInTile()) {
        		checkForAnmation();
        	}
        }

        if (getWorld().getBlockState(pos.up()).getBlock() != BlockRegistry.CRAB_POT && active || active && !hasCrabInTile()) {
        	active = false;
        	markForUpdate();
        }

		if (getWorld().getBlockState(pos).getBlock() instanceof BlockCrabPotFilter && active) {
			if (hasBait() && canFilterSlots(1, 2)) {
				if (getBaitProgress() == 0)
					consumeBait();
				setBaitProgress(getBaitProgress() + 1);
			}

			if (canFilterSlots(1, 2)) {
				setSlotProgress(getSlotProgress() + 1);
				if (getSlotProgress() >= MAX_FILTERING_TIME)
					filterItem(1, 2);
			} else {
				if (getSlotProgress() > 0)
					setSlotProgress(0);
			}
		}
    }

    private void checkForAnmation() {
    	TileEntityCrabPot tile = (TileEntityCrabPot) world.getTileEntity(pos.up());
    	if(tile != null && (tile.hasSiltCrab() || tile.hasBubblerCrab())) {
    		if(canFilterSlots(1, 2) && !tile.animate) {
    			tile.animate = true;
    			tile.markForUpdate();
    		}
    		if(!canFilterSlots(1, 2) && tile.animate) {
    			tile.animate = false;
    			tile.markForUpdate();
    		}
    	}
	}

	private boolean hasCrabInTile() {
    	TileEntityCrabPot tile = (TileEntityCrabPot) world.getTileEntity(pos.up());
		return tile != null && (tile.hasSiltCrab() || tile.hasBubblerCrab());
	}
    
    public ItemStack getRecipeOutput(ItemStack input, boolean checkForCrab) {
    	TileEntityCrabPot tile = (TileEntityCrabPot) world.getTileEntity(pos.up());
    	if(!checkForCrab || (tile != null && tile.hasSiltCrab()))
    		return CrabPotFilterRecipeSilt.getRecipeOutput(input);
    	if(!checkForCrab || (tile != null && tile.hasBubblerCrab()))
    		return CrabPotFilterRecipeBubbler.getRecipeOutput(input);
		return ItemStack.EMPTY;
    }

	private void setSlotProgress(int counter) {
		filtering_progress = counter;
		markForUpdate();
	}

	public int getSlotProgress() {
		return filtering_progress;
	}

	public void consumeBait() {
    	ItemStack baitStack = getItems().get(0);
		setBaitProgress(0);
		baitStack.shrink(1);
    }

	private boolean canFilterSlots(int input, int output) {
		if (!active || !hasBait() || getItems().get(input).isEmpty() || (!getItems().get(output).isEmpty() && getItems().get(output).getItem() != getRecipeOutput(getItems().get(input), true).getItem()))
			return false;
		else {
			ItemStack result = getRecipeOutput(getItems().get(input), true);
			if (result.isEmpty())
				return false;
			else {
				return true;
			}
		}
	}

	public boolean hasBait() {
		ItemStack baitStack = getItems().get(0);
		if (!baitStack.isEmpty())
			return true;
		else if (getBaitProgress() > 0)
			return true;
		return false;
	}

    public void filterItem(int input, int output) {	
		if (canFilterSlots(input, output)) {
			ItemStack itemstack = getItems().get(input);
			ItemStack result = getRecipeOutput(itemstack, true);
			ItemStack itemstack2 = getItems().get(output);
			if(!itemstack2.isEmpty() && result.getItem() == itemstack2.getItem()) { // better matching needed here, wip
				itemstack2.grow(1);
				getItems().set(output, itemstack2);
			}
			if (itemstack2.isEmpty())
				getItems().set(output, result.copy());
			setSlotProgress(0);
			if (getBaitProgress() > MAX_FILTERING_TIME * items_to_filter_count) 
				setBaitProgress(0);
			itemstack.shrink(1);
		}
    }

	public void setBaitProgress(int duration) {
		bait_progress = duration;
		markForUpdate();
	}

	public int getBaitProgress() {
		return bait_progress;
	}

    @SideOnly(Side.CLIENT)
    public int getBaitProgressScaled(int count) {
        return getBaitProgress() * count / (MAX_FILTERING_TIME * items_to_filter_count);
    }

    @SideOnly(Side.CLIENT)
    public int getFilteringProgressScaled(int count) {
        return getSlotProgress() * count / (MAX_FILTERING_TIME);
    }


	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(getPos()).grow(0D, 1D, 0D);
	}

    public void markForUpdate() {
        IBlockState state = this.getWorld().getBlockState(this.getPos());
        this.getWorld().notifyBlockUpdate(this.getPos(), state, state, 3);
    }

	public void setRotation(int horizontalIndexIn) {
		horizontalIndex = horizontalIndexIn;
	}

	public int getRotation() {
		return horizontalIndex;
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
		bait_progress = nbt.getInteger("bait_progress");
		filtering_progress = nbt.getInteger("filtering_progress");
		setRotation(nbt.getInteger("horizontalIndex"));
	}

	public NBTTagCompound saveToNbt(NBTTagCompound nbt) {
		ItemStackHelper.saveAllItems(nbt, inventory, false);
		nbt.setBoolean("active", active);
		nbt.setInteger("bait_progress", bait_progress);
		nbt.setInteger("filtering_progress", filtering_progress);
		nbt.setInteger("horizontalIndex", getRotation());
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
		return new TextComponentTranslation("tile.thebetweenlands.crab_pot_filter.name").getFormattedText();
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
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if(slot == baitSlot && EnumItemMisc.ANADIA_REMAINS.isItemOf(stack))
			return true;
		if (slot == inputSlot && !this.getRecipeOutput(stack, false).isEmpty())
			return true;
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

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return side == EnumFacing.DOWN ? resultSideSlot : allSideSlots;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing direction) {
        return direction == EnumFacing.DOWN  && slot == outputSlot;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing direction) {
		return isItemValidForSlot(slot, stack);
	}

}
