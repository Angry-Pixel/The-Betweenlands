package thebetweenlands.common.inventory.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thebetweenlands.api.item.IRuneItem;
import thebetweenlands.api.rune.INodeBlueprint;
import thebetweenlands.api.rune.gui.IRuneChainAltarContainer;
import thebetweenlands.api.rune.gui.IRuneChainAltarGui;
import thebetweenlands.api.rune.gui.IRuneContainer;
import thebetweenlands.api.rune.gui.IRuneContainerContext;
import thebetweenlands.api.rune.gui.IRuneLink;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.common.inventory.slot.SlotOutput;
import thebetweenlands.common.inventory.slot.SlotRune;
import thebetweenlands.common.tile.TileEntityRuneChainAltar;


public class ContainerRuneChainAltar extends Container implements IRuneChainAltarContainer {
	private final TileEntityRuneChainAltar altar;
	private final EntityPlayer player;

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

	private int selectedRune = -1;

	protected static class RuneContainerEntry {
		protected final IRuneContainer container;
		protected final IRuneContainerContext context;
		protected final List<Slot> slots;

		protected RuneContainerEntry(IRuneContainer container, IRuneContainerContext data, List<Slot> slots) {
			this.container = container;
			this.context = data;
			this.slots = slots;
		}
	}

	private Map<Integer, RuneContainerEntry> runeContainers = new HashMap<>();

	public ContainerRuneChainAltar(EntityPlayer player, TileEntityRuneChainAltar tile) {
		this.altar = tile;
		this.player = player;

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
				this.addSlotToContainer(new Slot(this.player.inventory, x + y * 9 + 9, 8 + x * 18, 142 + y * 18));
			}
		}

		//Player hotbar
		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(this.player.inventory, x, 8 + x * 18, 200));
		}

		//Init rune containers
		for(int i = 0; i < tile.getChainLength(); i++) {
			this.updateRuneContainer(i);
		}

		this.altar.openContainer(this);
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);

		this.altar.closeContainer();
	}

	public EntityPlayer getPlayer() {
		return this.player;
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
		int shiftHole = this.getRuneShiftHoleIndex(slotIndex - this.altar.getChainStart(), back);
		return shiftHole >= 0 ? shiftHole + this.altar.getChainStart() : -1;
	}

	public void shiftSlot(int slotIndex, boolean back) {
		this.shiftRune(slotIndex - this.altar.getChainStart(), back);
	}

	@Nullable
	public INodeBlueprint<?, RuneExecutionContext> getRuneBlueprint(int slotIndex) {
		ItemStack stack = this.inventorySlots.get(slotIndex).getStack();
		if(!stack.isEmpty() && stack.getItem() instanceof IRuneItem) {
			return ((IRuneItem) stack.getItem()).getRuneBlueprint(stack, null /*TODO*/);
		}
		return null;
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

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
		if(slotId > 0 && slotId < this.altar.getMaxChainLength() + 1 && clickType == ClickType.PICKUP && dragType == 1) {
			if(this.getSelectedSlot() == slotId) {
				this.setSelectedSlot(-1);
			} else {
				this.setSelectedSlot(slotId);
			}
			return ItemStack.EMPTY;
		}
		return super.slotClick(slotId, dragType, clickType, player);
	}

	public void setSelectedSlot(int slot) {
		this.setSelectedRune(slot - this.altar.getChainStart());
	}

	public int getSelectedSlot() {
		int selected = this.getSelectedRuneIndex();
		return selected >= 0 ? selected + this.altar.getChainStart() : -1;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.altar.isUsableByPlayer(player);
	}

	public void onSlotChanged(int slotIndex) {
		this.updateRuneContainer(slotIndex - this.altar.getChainStart());
	}

	@Override
	public int getRuneInventorySize() {
		return this.altar.getMaxChainLength();
	}

	@Override
	public int getSelectedRuneIndex() {
		return this.selectedRune >= 0 ? this.selectedRune : -1;
	}

	@Override
	public void setSelectedRune(int runeIndex) {
		if(runeIndex >= 0 && runeIndex < this.altar.getChainLength()) {
			this.selectedRune = runeIndex;
		} else {
			this.selectedRune = -1;
		}
	}

	@Override
	public ItemStack getRuneItemStack(int runeIndex) {
		return this.altar.getStackInSlot(runeIndex + this.altar.getChainStart());
	}

	@Override
	public void setRuneItemStack(int runeIndex, ItemStack stack) {
		this.altar.setInventorySlotContents(runeIndex + this.altar.getChainStart(), stack);
	}

	@Override
	public void shiftRune(int runeIndex, boolean back) {
		int chainStart = this.altar.getChainStart();
		int slotIndex = runeIndex + chainStart;

		int hole = this.getShiftHoleSlot(runeIndex + chainStart, back);

		if(hole >= 0) {
			if(back) {
				for(int i = hole; i <= slotIndex - 1; ++i) {
					RuneContainerEntry containerEntry = this.runeContainers.get(i - chainStart + 1);
					if(containerEntry != null) {
						this.runeContainers.put(i - chainStart, containerEntry);
						this.runeContainers.remove(i - chainStart + 1);
					}

					Slot slot = this.getSlot(i);
					Slot nextSlot = this.getSlot(i + 1);
					slot.putStack(nextSlot.getStack());
					nextSlot.putStack(ItemStack.EMPTY);

					if(i + 1 == this.getSelectedSlot()) {
						this.setSelectedSlot(this.getSelectedSlot() - 1);
					}

					for(RuneContainerEntry entry : this.runeContainers.values()) {
						entry.container.onRuneShifted(i - chainStart + 1, i - chainStart);
					}

					this.altar.getChainInfo().moveAllLinks(i - chainStart + 1, i);

					if(slot instanceof SlotRune && nextSlot instanceof SlotRune) {
						((SlotRune) slot).prevHoverTicks = ((SlotRune) slot).hoverTicks = ((SlotRune) nextSlot).hoverTicks;
						((SlotRune) nextSlot).prevHoverTicks = ((SlotRune) nextSlot).hoverTicks = 0;
					}
				}
			} else {
				for(int i = hole; i >= slotIndex + 1; --i) {
					RuneContainerEntry containerEntry = this.runeContainers.get(i - chainStart - 1);
					if(containerEntry != null) {
						this.runeContainers.put(i - chainStart, containerEntry);
						this.runeContainers.remove(i - chainStart - 1);
					}

					Slot slot = this.getSlot(i);
					Slot prevSlot = this.getSlot(i - 1);
					slot.putStack(prevSlot.getStack());
					prevSlot.putStack(ItemStack.EMPTY);

					if(i - 1 == this.getSelectedSlot()) {
						this.setSelectedSlot(this.getSelectedSlot() + 1);
					}

					for(RuneContainerEntry entry : this.runeContainers.values()) {
						entry.container.onRuneShifted(i - chainStart - 1, i - chainStart);
					}

					this.altar.getChainInfo().moveAllLinks(i - chainStart - 1, i);

					if(slot instanceof SlotRune && prevSlot instanceof SlotRune) {
						((SlotRune) slot).prevHoverTicks = ((SlotRune) slot).hoverTicks = ((SlotRune) prevSlot).hoverTicks;
						((SlotRune) prevSlot).prevHoverTicks = ((SlotRune) prevSlot).hoverTicks = 0;
					}
				}
			}
		}
	}

	@Override
	public int getRuneShiftHoleIndex(int runeIndex, boolean back) {
		int chainStart = this.altar.getChainStart();
		int slotIndex = runeIndex + chainStart;

		if(back) {
			for(int i = slotIndex; i > TileEntityRuneChainAltar.NON_INPUT_SLOTS - 1; --i) {
				if(i != slotIndex && !this.getSlot(i).getHasStack()) {
					return i - chainStart;
				}
			}
		} else {
			for(int i = slotIndex; i < this.altar.getMaxChainLength() + TileEntityRuneChainAltar.NON_INPUT_SLOTS; ++i) {
				if(i != slotIndex && !this.getSlot(i).getHasStack()) {
					return i - chainStart;
				}
			}
		}

		return -1;
	}

	@Override
	public Collection<Integer> getLinkedInputs(int runeIndex) {
		return this.altar.getChainInfo().getLinkedInputs(runeIndex + this.altar.getChainLength());
	}

	@Override
	public IRuneLink getLink(int runeIndex, int input) {
		return this.altar.getChainInfo().getLink(runeIndex, input);
	}

	@Override
	public boolean link(int runeIndex, int input, int outputRuneIndex, int output) {
		if(this.altar.getChainInfo().link(runeIndex, input, outputRuneIndex, output)) {
			for(RuneContainerEntry entry : this.runeContainers.values()) {
				entry.container.onMarkLinked(input, this.altar.getChainInfo().getLink(runeIndex, input));
			}
			return true;
		}
		return false;
	}

	@Override
	public IRuneLink unlink(int runeIndex, int input) {
		IRuneLink unlinked = this.altar.getChainInfo().unlink(runeIndex, input);
		if(unlinked != null) {
			for(RuneContainerEntry entry : this.runeContainers.values()) {
				entry.container.onMarkUnlinked(input, unlinked);
			}
		}
		return unlinked;
	}

	@Override
	public void unlinkAll(int runeIndex) {
		Map<Integer, IRuneLink> links = new HashMap<>();
		for(int linkedInput : this.altar.getChainInfo().getLinkedInputs(runeIndex)) {
			IRuneLink link = this.altar.getChainInfo().getLink(runeIndex, linkedInput);
			links.put(linkedInput, link);
		}
		this.altar.getChainInfo().unlinkAll(runeIndex);
		for(Entry<Integer, IRuneLink> unlinked : links.entrySet()) {
			for(RuneContainerEntry entry : this.runeContainers.values()) {
				entry.container.onMarkUnlinked(unlinked.getKey(), unlinked.getValue());
			}
		}
	}

	@Override
	public void moveAllLinks(int fromRuneIndex, int toRuneIndex) {
		this.altar.getChainInfo().moveAllLinks(fromRuneIndex, toRuneIndex);
		for(RuneContainerEntry entry : this.runeContainers.values()) {
			entry.container.onLinksMoved(fromRuneIndex, toRuneIndex);
		}
	}

	@Override
	public IRuneContainer getRuneContainer(int runeIndex) {
		RuneContainerEntry entry = this.runeContainers.get(runeIndex);
		return entry != null ? entry.container : null;
	}

	public IRuneContainerContext getRuneContainerContext(int runeIndex) {
		RuneContainerEntry entry = this.runeContainers.get(runeIndex);
		return entry != null ? entry.context : null;
	}

	protected void updateRuneContainer(int runeIndex) {
		ItemStack stack = this.getRuneItemStack(runeIndex);

		if(!stack.isEmpty() && stack.getItem() instanceof IRuneItem) {
			IRuneItem runeItem = (IRuneItem) stack.getItem();

			IRuneContainer newContainer = runeItem.getRuneMenuFactory(stack).createContainer();

			RuneContainerEntry currentContainerEntry = this.runeContainers.get(runeIndex);

			boolean isDifferentContainer = currentContainerEntry != null && !currentContainerEntry.container.getId().equals(newContainer.getId());

			if(currentContainerEntry == null || isDifferentContainer) {
				if(isDifferentContainer) {
					this.removeContainerEntry(runeIndex);
				}

				List<Slot> slots = new ArrayList<>();
				IRuneContainerContext newContainerContext = this.createRuneContainerContext(runeIndex, slots);

				newContainer.init(newContainerContext);

				this.runeContainers.put(runeIndex, new RuneContainerEntry(newContainer, newContainerContext, slots));
			}
		} else {
			this.removeContainerEntry(runeIndex);
		}
	}

	protected void removeContainerEntry(int runeIndex) {
		RuneContainerEntry entry = this.runeContainers.get(runeIndex);

		if(entry != null) {
			//Remove all links and data
			RuneChainInfo info = this.altar.getChainInfo();
			info.unlinkAll(runeIndex);
			info.removeContainerData(runeIndex);

			//Remove slots from current container
			for(Slot entrySlot : entry.slots) {
				this.inventorySlots.remove(entrySlot.slotNumber);
				this.inventoryItemStacks.remove(entrySlot.slotNumber);
			}
		}
	}

	protected IRuneContainerContext createRuneContainerContext(int runeIndex, List<Slot> slots) {
		return new IRuneContainerContext() {
			@Override
			public IRuneChainAltarContainer getRuneChainAltarContainer() {
				return ContainerRuneChainAltar.this;
			}

			@Override
			public IRuneChainAltarGui getRuneChainAltarGui() {
				//TODO Override and implement this in ContainerRuneChainAltarGui
				return null;
			}

			@Override
			public int getRuneIndex() {
				return runeIndex;
			}

			@Override
			public NBTTagCompound getData() {
				return ContainerRuneChainAltar.this.altar.getChainInfo().getContainerData(runeIndex);
			}

			@Override
			public void setData(NBTTagCompound nbt) {
				ContainerRuneChainAltar.this.altar.getChainInfo().setContainerData(runeIndex, nbt);
			}

			@Override
			public void addSlot(Slot slot) {
				slots.add(slot);
				ContainerRuneChainAltar.this.addSlotToContainer(slot);
			}
		};
	}
}