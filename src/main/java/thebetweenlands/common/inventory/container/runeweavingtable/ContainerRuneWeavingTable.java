package thebetweenlands.common.inventory.container.runeweavingtable;

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
import net.minecraft.util.NonNullList;
import thebetweenlands.api.capability.IRuneChainCapability;
import thebetweenlands.api.rune.INodeBlueprint;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneWeavingTableContainer;
import thebetweenlands.api.rune.IRuneWeavingTableGui;
import thebetweenlands.api.rune.IRuneChainContainerData;
import thebetweenlands.api.rune.IRuneChainData;
import thebetweenlands.api.rune.IRuneContainer;
import thebetweenlands.api.rune.IRuneContainerContext;
import thebetweenlands.api.rune.IRuneLink;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.common.herblore.rune.RuneChainData;
import thebetweenlands.common.inventory.slot.SlotRuneWeavingTableInput;
import thebetweenlands.common.inventory.slot.SlotRuneWeavingTableOutput;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.tile.TileEntityRuneWeavingTable;


public class ContainerRuneWeavingTable extends Container implements IRuneWeavingTableContainer {
	protected final TileEntityRuneWeavingTable table;
	protected final EntityPlayer player;

	public static final int [][] SLOT_POSITIONS = new int[][] {
		{8, 56}, {32, 50}, {56, 48}, {80, 46}, {104, 48}, {128, 50}, {152, 56},
		{152, 96}, {128, 102}, {104, 104}, {80, 106}, {56, 104}, {32, 102}, {8, 96}
	};

	private static final int [] OUTPUT_SLOT_POSITION = new int[] { 80, 76 };

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

	protected class RuneContainerEntry {
		protected IRuneContainer container;
		protected IRuneContainerContext context;
		protected final List<Slot> slots = new ArrayList<>();
		protected int runeIndex;
		protected INodeConfiguration configuration;

		protected RuneContainerEntry(int runeIndex) {
			this.runeIndex = runeIndex;
		}

		//TODO Implement adding/removing 
		protected void addSlotsToContainer() {
			for(Slot slot : this.slots) {
				ContainerRuneWeavingTable.this.addSlotToContainer(slot);
			}
		}

		protected void removeSlotsFromContainer() {
			for(Slot entrySlot : this.slots) {
				int index = ContainerRuneWeavingTable.this.inventorySlots.indexOf(entrySlot);
				if(index >= 0) {
					ContainerRuneWeavingTable.this.inventorySlots.remove(index);
					ContainerRuneWeavingTable.this.inventoryItemStacks.remove(index);
				}
			}
		}
	}

	private Map<Integer, RuneContainerEntry> runeContainers = new HashMap<>();

	public ContainerRuneWeavingTable(EntityPlayer player, TileEntityRuneWeavingTable tile) {
		this.table = tile;
		this.player = player;

		this.pages = new ArrayList<>();

		Page page = new Page(0);
		page.isCurrent = true;
		this.currentPage = page;
		this.pages.add(page);

		//Output slot
		this.addSlotToContainer(new SlotRuneWeavingTableOutput(this.table, 0, OUTPUT_SLOT_POSITION[0], OUTPUT_SLOT_POSITION[1], this));

		//Rune slots
		for(int i = 0; i < tile.getMaxChainLength(); i++) {
			int pageSlot = i % SLOTS_PER_PAGE;
			if(i != 0 && pageSlot == 0) {
				page = new Page(this.pages.size());
				this.pages.add(page);
			}
			Slot slot = new SlotRuneWeavingTableInput(this.table, i + 1, SLOT_POSITIONS[pageSlot][0], SLOT_POSITIONS[pageSlot][1], page);
			this.addSlotToContainer(slot);
		}

		//Player inventory
		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 9; ++x) {
				this.addSlotToContainer(new Slot(this.player.inventory, x + y * 9 + 9, 8 + x * 18, 155 + y * 18));
			}
		}

		//Player hotbar
		for (int x = 0; x < 9; ++x) {
			this.addSlotToContainer(new Slot(this.player.inventory, x, 8 + x * 18, 213));
		}

		//Init rune containers
		for(int i = 0; i < tile.getChainLength(); i++) {
			this.updateRuneContainer(i);
		}

		//TODO Need a better way than doing this in ctor
		this.table.openContainer(this);
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);

		this.table.closeContainer();
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
		int shiftHole = this.getRuneShiftHoleIndex(slotIndex - this.table.getChainStart(), back);
		return shiftHole >= 0 ? shiftHole + this.table.getChainStart() : -1;
	}

	public void shiftSlot(int slotIndex, boolean back) {
		this.shiftRune(slotIndex - this.table.getChainStart(), back);
	}

	@Nullable
	public INodeBlueprint<?, RuneExecutionContext> getRuneBlueprint(int slotIndex) {
		IRuneContainer container = this.getRuneContainer(slotIndex - this.table.getChainStart());
		return container != null ? container.getBlueprint() : null;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack prevStack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			prevStack = slotStack.copy();

			if(slotIndex < this.table.getSizeInventory()) {
				if (!this.mergeItemStack(slotStack, this.table.getSizeInventory(), this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(slotStack, 0, this.table.getSizeInventory(), false)) {
				return ItemStack.EMPTY;
			}

			if(slotStack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if(prevStack.getCount() != slotStack.getCount() && slotIndex < this.table.getSizeInventory()) {
				slot.onTake(player, slotStack);
			}
		}

		return prevStack;
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickType, EntityPlayer player) {
		if(slotId > 0 && slotId < this.table.getMaxChainLength() + 1 && clickType == ClickType.PICKUP && dragType == 1) {
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
		this.setSelectedRune(slot - this.table.getChainStart());
	}

	public int getSelectedSlot() {
		int selected = this.getSelectedRuneIndex();
		return selected >= 0 ? selected + this.table.getChainStart() : -1;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return this.table.isUsableByPlayer(player);
	}

	public void onSlotChanged(int slotIndex) {
		this.updateRuneContainer(slotIndex - this.table.getChainStart());

		if(slotIndex == 0) {
			this.updateInputsFromRuneChain();
		}
	}

	@Override
	public int getRuneInventorySize() {
		return this.table.getMaxChainLength();
	}

	@Override
	public int getSelectedRuneIndex() {
		return this.selectedRune >= 0 ? this.selectedRune : -1;
	}

	@Override
	public void setSelectedRune(int runeIndex) {
		if(runeIndex >= 0 && runeIndex < this.table.getChainLength()) {
			this.selectedRune = runeIndex;
		} else {
			this.selectedRune = -1;
		}
	}

	@Override
	public ItemStack getRuneItemStack(int runeIndex) {
		return this.table.getStackInSlot(runeIndex + this.table.getChainStart());
	}

	@Override
	public void setRuneItemStack(int runeIndex, ItemStack stack) {
		this.table.setInventorySlotContents(runeIndex + this.table.getChainStart(), stack);
	}

	@Override
	public void shiftRune(int runeIndex, boolean back) {
		int chainStart = this.table.getChainStart();
		int slotIndex = runeIndex + chainStart;

		int hole = this.getShiftHoleSlot(runeIndex + chainStart, back);

		if(hole >= 0) {
			if(back) {
				for(int i = hole; i <= slotIndex - 1; ++i) {
					RuneContainerEntry containerEntry = this.runeContainers.get(i - chainStart + 1);
					if(containerEntry != null) {
						this.runeContainers.put(i - chainStart, containerEntry);
						this.runeContainers.remove(i - chainStart + 1);
						containerEntry.runeIndex = i - chainStart;
					}

					Slot slot = this.getSlot(i);
					Slot nextSlot = this.getSlot(i + 1);
					slot.putStack(nextSlot.getStack());
					nextSlot.putStack(ItemStack.EMPTY);

					if(i + 1 == this.getSelectedSlot()) {
						this.setSelectedSlot(this.getSelectedSlot() - 1);
					}

					for(RuneContainerEntry entry : this.runeContainers.values()) {
						entry.container.onRuneMoved(i - chainStart + 1, i - chainStart);
					}

					this.table.getContainerData().moveRuneData(i - chainStart + 1, i - chainStart);

					if(slot instanceof SlotRuneWeavingTableInput && nextSlot instanceof SlotRuneWeavingTableInput) {
						((SlotRuneWeavingTableInput) slot).prevHoverTicks = ((SlotRuneWeavingTableInput) slot).hoverTicks = ((SlotRuneWeavingTableInput) nextSlot).hoverTicks;
						((SlotRuneWeavingTableInput) nextSlot).prevHoverTicks = ((SlotRuneWeavingTableInput) nextSlot).hoverTicks = 0;
					}
				}
			} else {
				for(int i = hole; i >= slotIndex + 1; --i) {
					RuneContainerEntry containerEntry = this.runeContainers.get(i - chainStart - 1);
					if(containerEntry != null) {
						this.runeContainers.put(i - chainStart, containerEntry);
						this.runeContainers.remove(i - chainStart - 1);
						containerEntry.runeIndex = i - chainStart;
					}

					Slot slot = this.getSlot(i);
					Slot prevSlot = this.getSlot(i - 1);
					slot.putStack(prevSlot.getStack());
					prevSlot.putStack(ItemStack.EMPTY);

					if(i - 1 == this.getSelectedSlot()) {
						this.setSelectedSlot(this.getSelectedSlot() + 1);
					}

					for(RuneContainerEntry entry : this.runeContainers.values()) {
						entry.container.onRuneMoved(i - chainStart - 1, i - chainStart);
					}

					this.table.getContainerData().moveRuneData(i - chainStart - 1, i - chainStart);

					if(slot instanceof SlotRuneWeavingTableInput && prevSlot instanceof SlotRuneWeavingTableInput) {
						((SlotRuneWeavingTableInput) slot).prevHoverTicks = ((SlotRuneWeavingTableInput) slot).hoverTicks = ((SlotRuneWeavingTableInput) prevSlot).hoverTicks;
						((SlotRuneWeavingTableInput) prevSlot).prevHoverTicks = ((SlotRuneWeavingTableInput) prevSlot).hoverTicks = 0;
					}
				}
			}
		}

		this.onRunesChanged();

		this.table.markDirty();
	}

	@Override
	public int getRuneShiftHoleIndex(int runeIndex, boolean back) {
		int chainStart = this.table.getChainStart();
		int slotIndex = runeIndex + chainStart;

		if(back) {
			for(int i = slotIndex; i > TileEntityRuneWeavingTable.NON_INPUT_SLOTS - 1; --i) {
				if(i != slotIndex && !this.getSlot(i).getHasStack()) {
					return i - chainStart;
				}
			}
		} else {
			for(int i = slotIndex; i < this.table.getMaxChainLength() + TileEntityRuneWeavingTable.NON_INPUT_SLOTS; ++i) {
				if(i != slotIndex && !this.getSlot(i).getHasStack()) {
					return i - chainStart;
				}
			}
		}

		return -1;
	}

	@Override
	public Collection<Integer> getLinkedInputs(int runeIndex) {
		return this.table.getContainerData().getLinkedInputs(runeIndex);
	}

	@Override
	public IRuneLink getLink(int runeIndex, int input) {
		return this.table.getContainerData().getLink(runeIndex, input);
	}

	@Override
	public boolean link(int runeIndex, int input, int outputRuneIndex, int output) {
		//TODO Validate that link is possible, especially on server side

		if(this.table.getContainerData().link(runeIndex, input, outputRuneIndex, output)) {
			for(RuneContainerEntry entry : this.runeContainers.values()) {
				entry.container.onTokenLinked(input, this.table.getContainerData().getLink(runeIndex, input));
			}

			this.onRunesChanged();

			this.table.markDirty();

			return true;
		}
		return false;
	}

	@Override
	public IRuneLink unlink(int runeIndex, int input) {
		IRuneLink unlinked = this.table.getContainerData().unlink(runeIndex, input);

		if(unlinked != null) {
			for(RuneContainerEntry entry : this.runeContainers.values()) {
				entry.container.onTokenUnlinked(input, unlinked);
			}

			this.onRunesChanged();

			this.table.markDirty();
		}

		return unlinked;
	}

	@Override
	public void unlinkAll(int runeIndex) {
		Map<Integer, IRuneLink> links = new HashMap<>();

		for(int linkedInput : this.table.getContainerData().getLinkedInputs(runeIndex)) {
			IRuneLink link = this.table.getContainerData().getLink(runeIndex, linkedInput);
			links.put(linkedInput, link);
		}

		this.table.getContainerData().unlinkAll(runeIndex);

		for(Entry<Integer, IRuneLink> unlinked : links.entrySet()) {
			for(RuneContainerEntry entry : this.runeContainers.values()) {
				entry.container.onTokenUnlinked(unlinked.getKey(), unlinked.getValue());
			}
		}

		this.onRunesChanged();

		this.table.markDirty();
	}

	@Override
	public void moveRuneData(int fromRuneIndex, int toRuneIndex) {
		this.table.getContainerData().moveRuneData(fromRuneIndex, toRuneIndex);

		for(RuneContainerEntry entry : this.runeContainers.values()) {
			entry.container.onRuneDataMoved(fromRuneIndex, toRuneIndex);
		}

		this.onRunesChanged();

		this.table.markDirty();
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

		if(!stack.isEmpty() && stack.hasCapability(CapabilityRegistry.CAPABILITY_RUNE, null)) {
			IRuneContainer newContainer = stack.getCapability(CapabilityRegistry.CAPABILITY_RUNE, null).getRuneContainerFactory().createContainer();

			RuneContainerEntry currentContainerEntry = this.runeContainers.get(runeIndex);
			
			boolean isDifferentContainer = currentContainerEntry != null && !currentContainerEntry.container.getId().equals(newContainer.getId());

			if(currentContainerEntry == null || isDifferentContainer) {
				if(isDifferentContainer) {
					this.removeContainerEntry(runeIndex);
				}

				RuneContainerEntry entry = new RuneContainerEntry(runeIndex);

				entry.container = newContainer;

				IRuneContainerContext newContainerContext = this.createRuneContainerContext(entry);

				entry.context = newContainerContext;

				newContainer.setContext(newContainerContext);

				IRuneChainContainerData containerData = ContainerRuneWeavingTable.this.table.getContainerData();

				if(containerData.hasConfigurationId(entry.runeIndex)) {
					int savedConfigurationId = containerData.getConfigurationId(entry.runeIndex);

					for(INodeConfiguration configuration : entry.container.getBlueprint().getConfigurations()) {
						if(configuration.getId() == savedConfigurationId) {
							entry.configuration = configuration;
							break;
						}
					}
				}

				if(entry.configuration == null) {
					entry.configuration = entry.container.getBlueprint().getConfigurations().get(0);
				}

				newContainer.init();

				this.runeContainers.put(runeIndex, entry);

				this.onRunesChanged();
			}
		} else {
			this.removeContainerEntry(runeIndex);
		}
	}

	protected void removeContainerEntry(int runeIndex) {
		RuneContainerEntry entry = this.runeContainers.get(runeIndex);

		if(entry != null) {
			//Remove all links and data
			IRuneChainContainerData containerData = this.table.getContainerData();

			containerData.unlinkAll(runeIndex);
			containerData.unlinkAllIncoming(runeIndex);
			containerData.removeConfigurationId(runeIndex);
			containerData.removeContainerNbt(runeIndex);

			//Remove slots from current container
			entry.removeSlotsFromContainer();

			this.runeContainers.remove(runeIndex);

			this.onRunesChanged();
		}
	}

	protected IRuneContainerContext createRuneContainerContext(RuneContainerEntry entry) {
		return new IRuneContainerContext() {
			@Override
			public IRuneWeavingTableContainer getRuneWeavingTableContainer() {
				return ContainerRuneWeavingTable.this;
			}

			@Override
			public IRuneWeavingTableGui getRuneWeavingTableGui() {
				return ContainerRuneWeavingTable.this.getRuneWeavingTableGui();
			}

			@Override
			public int getRuneIndex() {
				return entry.runeIndex;
			}

			@Override
			public ItemStack getRuneItemStack() {
				return ContainerRuneWeavingTable.this.getRuneItemStack(entry.runeIndex);
			}

			@Override
			public NBTTagCompound getData() {
				IRuneChainContainerData info = ContainerRuneWeavingTable.this.table.getContainerData();
				NBTTagCompound nbt = info.getContainerNbt(entry.runeIndex);
				if(nbt == null) {
					info.setContainerNbt(entry.runeIndex, nbt = new NBTTagCompound());
					ContainerRuneWeavingTable.this.table.markDirty();
				}
				return nbt;
			}

			@Override
			public void setData(NBTTagCompound nbt) {
				ContainerRuneWeavingTable.this.table.getContainerData().setContainerNbt(entry.runeIndex, nbt);
				ContainerRuneWeavingTable.this.table.markDirty();
			}

			@Override
			public void addSlot(Slot slot) {
				entry.slots.add(slot);
			}

			@Override
			public INodeConfiguration getConfiguration() {
				return entry.configuration;
			}

			@Override
			public void setConfiguration(INodeConfiguration configuration) {
				if(entry.configuration != configuration) {
					entry.container.onConfigurationChanged(entry.configuration, configuration);

					entry.configuration = configuration;

					ContainerRuneWeavingTable.this.table.getContainerData().setConfigurationId(entry.runeIndex, configuration.getId());

					//TODO Preferably store links per configuration
					ContainerRuneWeavingTable.this.table.getContainerData().unlinkAll(entry.runeIndex);
					ContainerRuneWeavingTable.this.table.getContainerData().unlinkAllIncoming(entry.runeIndex);

					ContainerRuneWeavingTable.this.onRunesChanged();

					ContainerRuneWeavingTable.this.table.markDirty();
				}
			}
		};
	}

	protected IRuneWeavingTableGui getRuneWeavingTableGui() {
		return null;
	}

	@Override
	public Slot getRuneSlot(int runeIndex) {
		return this.getSlot(runeIndex + this.table.getChainStart());
	}

	private boolean updateOutput = true;

	@Override
	public void onRunesChanged() {
		//TODO This needs tp be moved to and called from the tileentity so that the output also updates when a rune is inserted without container

		if(this.updateOutput) {
			this.updateOutput = false;
			
			ItemStack stack = this.table.getStackInSlot(0);

			if(!stack.isEmpty() && stack.hasCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN, null)) {
				IRuneChainCapability cap = stack.getCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN, null);

				int runeCount = this.getRuneInventorySize();

				NonNullList<ItemStack> runes = NonNullList.withSize(runeCount, ItemStack.EMPTY);
				for(int i = 0; i < runeCount; i++) {
					runes.set(i, this.getRuneItemStack(i));
				}

				RuneChainData data = new RuneChainData(runes, this.table.getContainerData());

				cap.setData(data);
			}

			this.table.setInventorySlotContents(0, stack);

			this.updateOutput = true;
		}
	}

	//TODO This too needs to be moved to and called from the tileentity, see above
	protected void updateInputsFromRuneChain() {
		ItemStack stack = this.table.getStackInSlot(0);

		if(this.updateOutput) {
			this.updateOutput = false;

			for(int i = 0; i < this.getRuneInventorySize(); i++) {
				this.setRuneItemStack(i, ItemStack.EMPTY);
			}

			if(!stack.isEmpty() && stack.hasCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN, null)) {
				IRuneChainCapability cap = stack.getCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN, null);

				IRuneChainData data = cap.getData();

				if(data != null) {
					NonNullList<ItemStack> runes = data.getRuneItems();

					//Set data *before* setting runes so that the rune containers are initialized correctly!
					this.table.setContainerData(data.getContainerData());
					
					for(int i = 0; i < runes.size(); i++) {
						this.setRuneItemStack(i, runes.get(i));
					}
				}
			}

			this.updateOutput = true;
		}
	}
}