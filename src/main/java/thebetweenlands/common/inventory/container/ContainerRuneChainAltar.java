package thebetweenlands.common.inventory.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.inventory.slot.SlotOutput;
import thebetweenlands.common.inventory.slot.SlotRune;
import thebetweenlands.common.tile.TileEntityRuneChainAltar;


public class ContainerRuneChainAltar extends Container {
	private final TileEntityRuneChainAltar altar;

	public static final int [][] SLOT_POSITIONS = new int[][] {
		{8, 43}, {32, 37}, {56, 35}, {80, 33}, {104, 35}, {128, 37}, {152, 43},
		{152, 83}, {128, 89}, {104, 91}, {80, 93}, {56, 91}, {32, 89}, {8, 83}
	};

	private static final int [] OUTPUT_SLOT_POSITION = new int[] { 80, 63 };

	public static final int SLOTS_PER_PAGE = SLOT_POSITIONS.length;

	public static class Page {
		private boolean isCurrent = false;
		private List<Slot> slots = new ArrayList<>();
		public final int index;
		public boolean interactable = true;

		private Page(int index) {
			this.index = index;
		}

		public boolean isCurrent() {
			return this.isCurrent;
		}

		public boolean isInteractable() {
			return this.isCurrent && this.interactable;
		}
	}

	private Page currentPage;
	private final List<Page> pages;

	public ContainerRuneChainAltar(InventoryPlayer playerInventory, TileEntityRuneChainAltar tile) {
		super();

		this.altar = tile;

		this.pages = new ArrayList<>();

		Page page = new Page(0);
		page.isCurrent = true;
		this.currentPage = page;
		this.pages.add(page);

		//Output slot
		this.addSlotToContainer(new SlotOutput(this.altar, 0, OUTPUT_SLOT_POSITION[0], OUTPUT_SLOT_POSITION[1], this));

		//Rune slots
		for(int i = 0; i < tile.getMaxChainLength(); i++) {
			int pageSlot = i % SLOTS_PER_PAGE;
			if(i != 0 && pageSlot == 0) {
				page = new Page(this.pages.size());
				this.pages.add(page);
			}
			Slot slot = new SlotRune(this.altar, i + 1, SLOT_POSITIONS[pageSlot][0], SLOT_POSITIONS[pageSlot][1], page);
			this.addSlotToContainer(slot);
		}

		//Player inventory
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 142 + y * 18));
			}
		}

		//Player hotbar
		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(playerInventory, x, 8 + x * 18, 200));
		}
	}

	public Page getCurrentPage() {
		return this.currentPage;
	}

	public void setCurrentPage(int page) {
		this.currentPage.isCurrent = false;
		this.currentPage = this.pages.get(page);
		this.currentPage.isCurrent = true;
	}

	public int getPages() {
		return this.pages.size();
	}

	public int getShiftHoleSlot(int slotIndex, boolean back) {
		if(back) {
			for(int i = slotIndex; i > TileEntityRuneChainAltar.NON_INPUT_SLOTS - 1; --i) {
				if(i != slotIndex && !this.getSlot(i).getHasStack()) {
					return i;
				}
			}
		} else {
			for(int i = slotIndex; i < this.altar.getMaxChainLength() + TileEntityRuneChainAltar.NON_INPUT_SLOTS; ++i) {
				if(i != slotIndex && !this.getSlot(i).getHasStack()) {
					return i;
				}
			}
		}
		return -1;
	}

	public void shift(int slotIndex, boolean back) {
		//TODO Also shift links!!
		int hole = this.getShiftHoleSlot(slotIndex, back);
		if(hole >= 0) {
			if(back) {
				for(int i = hole; i <= slotIndex - 1; ++i) {
					Slot slot = this.getSlot(i);
					Slot nextSlot = this.getSlot(i + 1);
					slot.putStack(nextSlot.getStack());
					nextSlot.putStack(ItemStack.EMPTY);
				}
			} else {
				for(int i = hole; i >= slotIndex + 1; --i) {
					Slot slot = this.getSlot(i);
					Slot prevSlot = this.getSlot(i - 1);
					slot.putStack(prevSlot.getStack());
					prevSlot.putStack(ItemStack.EMPTY);
				}
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			itemstack = slotStack.copy();

			if (slotIndex < this.altar.getSizeInventory()) {
				if (!this.mergeItemStack(slotStack, this.altar.getSizeInventory(), this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(slotStack, 0, this.altar.getSizeInventory(), false)) {
				return ItemStack.EMPTY;
			}

			if (slotStack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	//TODO See ContainerAnimator
	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value) {
		super.updateProgressBar(id, value);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.altar.isUsableByPlayer(player);
	}
}