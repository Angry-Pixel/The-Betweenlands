package thebetweenlands.common.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public class SlotPassthroughCraftingInput extends SlotCrafting {
	private final EntityPlayer player;
	private final InventoryCrafting craftMatrix;

	private final IInventory resultInventory;
	private final IInventory persistentInventory;

	private final Container eventHandler;
	
	private boolean craftingTake;

	public SlotPassthroughCraftingInput(EntityPlayer player, InventoryCrafting craftingMatrix, InventoryCraftResult resultInventory, int slotIndex, int xPosition, int yPosition, IInventory persistentInventory, Container eventHandler) {
		super(player, craftingMatrix, new InventoryCraftResult() {
			@Override
			public String getName() {
				return resultInventory.getName();
			}

			@Override
			public boolean hasCustomName() {
				return resultInventory.hasCustomName();
			}

			@Override
			public ITextComponent getDisplayName() {
				return resultInventory.getDisplayName();
			}

			@Override
			public int getSizeInventory() {
				return resultInventory.getSizeInventory();
			}

			@Override
			public boolean isEmpty() {
				return resultInventory.isEmpty() && this.getStackInSlot(slotIndex).isEmpty();
			}

			@Override
			public ItemStack getStackInSlot(int index) {
				ItemStack persistent = persistentInventory.getStackInSlot(slotIndex);
				if(!persistent.isEmpty()) {
					return persistent;
				}
				return resultInventory.getStackInSlot(index);
			}

			@Override
			public ItemStack decrStackSize(int index, int count) {
				ItemStack persistent = persistentInventory.getStackInSlot(slotIndex);
				if(!persistent.isEmpty()) {
					if(count > persistent.getCount()) {
						persistentInventory.setInventorySlotContents(slotIndex, ItemStack.EMPTY);
						eventHandler.onCraftMatrixChanged(this);
						return persistent;
					} else {
						ItemStack result = persistent.splitStack(count);
						persistentInventory.setInventorySlotContents(slotIndex, persistent);
						eventHandler.onCraftMatrixChanged(this);
						return result;
					}
				}

				ItemStack result = resultInventory.decrStackSize(index, count);

				if(result.getCount() > count) {
					//Excess number of items extracted, store rest in persistent inventory
					ItemStack corrected = result.splitStack(count);
					persistentInventory.setInventorySlotContents(slotIndex, result);
					eventHandler.onCraftMatrixChanged(this);
					return corrected;
				}

				return result;
			}

			@Override
			public ItemStack removeStackFromSlot(int index) {
				ItemStack persistent = persistentInventory.getStackInSlot(slotIndex);
				if(!persistent.isEmpty()) {
					persistentInventory.setInventorySlotContents(slotIndex, ItemStack.EMPTY);
					eventHandler.onCraftMatrixChanged(this);
					return persistent;
				}
				return resultInventory.removeStackFromSlot(index);
			}

			@Override
			public void setInventorySlotContents(int index, ItemStack stack) {
				resultInventory.setInventorySlotContents(index, stack);
			}

			@Override
			public int getInventoryStackLimit() {
				return resultInventory.getInventoryStackLimit();
			}

			@Override
			public void markDirty() {
				resultInventory.markDirty();
			}

			@Override
			public boolean isUsableByPlayer(EntityPlayer player) {
				return resultInventory.isUsableByPlayer(player);
			}

			@Override
			public void openInventory(EntityPlayer player) {
				resultInventory.openInventory(player);
			}

			@Override
			public void closeInventory(EntityPlayer player) {
				resultInventory.closeInventory(player);
			}

			@Override
			public boolean isItemValidForSlot(int index, ItemStack stack) {
				return resultInventory.isItemValidForSlot(index, stack);
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
				persistentInventory.setInventorySlotContents(slotIndex, ItemStack.EMPTY);
				resultInventory.clear();
			}
		}, slotIndex, xPosition, yPosition);

		this.player = player;
		this.craftMatrix = craftingMatrix;
		this.resultInventory = resultInventory;
		this.persistentInventory = persistentInventory;
		this.eventHandler = eventHandler;
		
		this.craftingTake = !this.hasPersistentItem();
	}

	public boolean hasPersistentItem() {
		return !this.persistentInventory.getStackInSlot(this.getSlotIndex()).isEmpty();
	}

	@Override
	public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
		this.onCrafting(stack);

		if(this.craftingTake) {
			this.craftingTake = false;

			net.minecraftforge.common.ForgeHooks.setCraftingPlayer(thePlayer);
			NonNullList<ItemStack> remainingStacks = CraftingManager.getRemainingItems(this.craftMatrix, thePlayer.world);
			net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

			for(int i = 0; i < remainingStacks.size(); ++i) {
				ItemStack currentStack = this.craftMatrix.getStackInSlot(i);
				ItemStack remainingStack = remainingStacks.get(i);

				if(!currentStack.isEmpty()) {
					this.craftMatrix.decrStackSize(i, 1);
					currentStack = this.craftMatrix.getStackInSlot(i);
				}

				if(!remainingStack.isEmpty()) {
					if(currentStack.isEmpty()) {
						this.craftMatrix.setInventorySlotContents(i, remainingStack);
					} else if(ItemStack.areItemsEqual(currentStack, remainingStack) && ItemStack.areItemStackTagsEqual(currentStack, remainingStack)) {
						remainingStack.grow(currentStack.getCount());
						this.craftMatrix.setInventorySlotContents(i, remainingStack);
					} else if(!this.player.inventory.addItemStackToInventory(remainingStack)) {
						this.player.dropItem(remainingStack, false);
					}
				}
			}
		}

		if(!this.hasPersistentItem()) {
			this.craftingTake = true;
			this.craftMatrix.eventHandler.onCraftMatrixChanged(this.craftMatrix);
		}

		return stack;
	}

	@Override
	public void putStack(ItemStack stack) {
		//On server side this is only called when the player puts something in the slot
		if(!this.player.world.isRemote) {
			//Store stack persistently
			this.persistentInventory.setInventorySlotContents(this.getSlotIndex(), stack);
			this.eventHandler.onCraftMatrixChanged(this.inventory);
			this.onSlotChanged();
		} else {
			super.putStack(stack);
		}
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
		//Allow putting in stacks if slot is empty
		return this.inventory.getStackInSlot(0).isEmpty();
	}
}
