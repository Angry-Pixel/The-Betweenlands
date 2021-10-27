package thebetweenlands.common.inventory.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import thebetweenlands.common.inventory.slot.SlotOutput;
import thebetweenlands.common.inventory.slot.SlotRestrictionListNoMeta;
import thebetweenlands.common.inventory.slot.SlotRestrictionNoMeta;
import thebetweenlands.common.item.misc.ItemMob;
import thebetweenlands.common.registries.AdvancementCriterionRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.tile.TileEntityFishTrimmingTable;

public class ContainerFishTrimmingTable extends Container {

	private final EntityPlayer player;
	public List<ItemStack> acceptedItems = new ArrayList<>();

	public ItemStack axe = new ItemStack(ItemRegistry.BONE_AXE);
	public TileEntityFishTrimmingTable trimmingTable;

	private class SlotTrimmingOutput extends SlotOutput {
		private int prevCount;

		public SlotTrimmingOutput(IInventory inventoryIn, int index, int xPosition, int yPosition, Container container) {
			super(inventoryIn, index, xPosition, yPosition, container);
			this.updateCount();
		}

		private void updateCount() {
			this.prevCount = this.getStack().getCount();
		}

		@Override
		public void onSlotChanged() {
			super.onSlotChanged();

			if(!trimmingTable.getStackInSlot(4).isEmpty()) {
				int index = this.getSlotIndex();

				if(index == 1 || index == 2 || index == 3) {
					int removed = Math.max(0, this.prevCount - this.getStack().getCount());

					if(removed > 0) {
						trimmingTable.getStackInSlot(4).shrink(removed);
						trimmingTable.markDirty();

						ContainerFishTrimmingTable.this.onCraftMatrixChanged(trimmingTable);
					}
				}
			}

			this.updateCount();
		}

		@Override
		public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
			if(thePlayer instanceof EntityPlayerMP) {
				AdvancementCriterionRegistry.TRIM_FISH.trigger((EntityPlayerMP) thePlayer);
			}

			if(this.getSlotIndex() == 4) {
				trimmingTable.setInventorySlotContents(1, ItemStack.EMPTY);
				trimmingTable.setInventorySlotContents(2, ItemStack.EMPTY);
				trimmingTable.setInventorySlotContents(3, ItemStack.EMPTY);
				ContainerFishTrimmingTable.this.onCraftMatrixChanged(trimmingTable);
			}

			return super.onTake(thePlayer, stack);
		}
	}

	public ContainerFishTrimmingTable(EntityPlayer player, TileEntityFishTrimmingTable tile) {
		InventoryPlayer playerInventory = player.inventory;
		this.player = player;
		trimmingTable = tile;
		acceptedItems.add(new ItemStack(ItemRegistry.ANADIA));
		acceptedItems.add(new ItemStack(ItemRegistry.SILT_CRAB));
		acceptedItems.add(new ItemStack(ItemRegistry.BUBBLER_CRAB));

		//input
		addSlotToContainer(new SlotRestrictionListNoMeta(trimmingTable, 0, 80, 27, getItemList(), 1, this));

		//output
		addSlotToContainer(new SlotTrimmingOutput(trimmingTable, 1, 44, 77, this));
		addSlotToContainer(new SlotTrimmingOutput(trimmingTable, 2, 80, 77, this));
		addSlotToContainer(new SlotTrimmingOutput(trimmingTable, 3, 116, 77, this));
		addSlotToContainer(new SlotTrimmingOutput(trimmingTable, 4, 8, 113, this));

		//tool
		addSlotToContainer(new SlotRestrictionNoMeta(trimmingTable, 5, 152, 113, axe, 1));

		for (int l = 0; l < 3; ++l)
			for (int j1 = 0; j1 < 9; ++j1)
				this.addSlotToContainer(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 145 + l * 18));

		for (int i1 = 0; i1 < 9; ++i1)
			this.addSlotToContainer(new Slot(playerInventory, i1, 8 + i1 * 18, 203));
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack stack1 = slot.getStack();
			stack = stack1.copy();

			if (slotIndex > 5) {
				if ((stack1.getItem() == ItemRegistry.ANADIA || stack1.getItem() == ItemRegistry.SILT_CRAB || stack1.getItem() == ItemRegistry.BUBBLER_CRAB) && ((ItemMob) stack1.getItem()).hasEntityData(stack1)) {
					if (!this.mergeItemStack(stack1, 0, 1, false))
						return ItemStack.EMPTY;
				}

				if (stack1.getItem() == ItemRegistry.BONE_AXE) {
					if (!this.mergeItemStack(stack1, 5, 6, false))
						return ItemStack.EMPTY;
				}

				if (!this.mergeItemStack(stack1, 1, 5, false))
					return ItemStack.EMPTY;

			} else if (!mergeItemStack(stack1, 6, inventorySlots.size(), false))
				return ItemStack.EMPTY;

			if (stack1.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (stack1.getCount() == stack.getCount())
				return ItemStack.EMPTY;

			ItemStack stack2 = slot.onTake(player, stack1);

			if (slotIndex == 1 || slotIndex == 2 || slotIndex == 3 || slotIndex == 4)
				player.dropItem(stack2, false);
		}

		return stack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		BlockPos pos = this.trimmingTable.getPos();
		if(playerIn.world.getTileEntity(pos) != this.trimmingTable) {
			return false;
		} else {
			return playerIn.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
		}
	}

	public List<ItemStack> getItemList() {
		return acceptedItems;
	}

	public void chop(EntityPlayerMP player) {
		if((this.trimmingTable.hasAnadia() || this.trimmingTable.hasSiltCrab() || this.trimmingTable.hasBubblerCrab()) && this.trimmingTable.hasChopper() && this.trimmingTable.allResultSlotsEmpty()) {

			// set slot contents 1, 2, 3 to butcher items and 4 to guts
			int numItems = 0;
			for(int i = 1; i <= 4; i++) {
				ItemStack result = this.trimmingTable.getSlotResult(i, numItems);
				numItems += result.getCount();
				this.getSlot(i).putStack(result);
			}

			// damage axe
			this.trimmingTable.getStackInSlot(5).damageItem(1, player);

			// set slot contents 0 to empty last so logic works in order
			this.trimmingTable.setInventorySlotContents(0, this.trimmingTable.getSlotResult(0, 0));

			this.onCraftMatrixChanged(trimmingTable);

			player.world.playSound(null, this.trimmingTable.getPos(), SoundRegistry.FISH_CHOP, SoundCategory.BLOCKS, 1F, 1F);
		}
	}
}