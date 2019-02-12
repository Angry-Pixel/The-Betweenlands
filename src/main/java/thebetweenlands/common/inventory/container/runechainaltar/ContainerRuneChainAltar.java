package thebetweenlands.common.inventory.container.runechainaltar;

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
import thebetweenlands.api.rune.IRuneChainAltarContainer;
import thebetweenlands.api.rune.IRuneChainAltarGui;
import thebetweenlands.api.rune.IRuneChainContainerData;
import thebetweenlands.api.rune.IRuneChainData;
import thebetweenlands.api.rune.IRuneContainer;
import thebetweenlands.api.rune.IRuneContainerContext;
import thebetweenlands.api.rune.IRuneLink;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.common.herblore.rune.RuneChainData;
import thebetweenlands.common.inventory.slot.SlotRuneChainAltarInput;
import thebetweenlands.common.inventory.slot.SlotRuneChainAltarOutput;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.tile.TileEntityRuneChainAltar;


public class ContainerRuneChainAltar extends Container implements IRuneChainAltarContainer {
	protected final TileEntityRuneChainAltar altar;
	protected final EntityPlayer player;

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
				ContainerRuneChainAltar.this.addSlotToContainer(slot);
			}
		}

		protected void removeSlotsFromContainer() {
			for(Slot entrySlot : this.slots) {
				int index = ContainerRuneChainAltar.this.inventorySlots.indexOf(entrySlot);
				if(index >= 0) {
					ContainerRuneChainAltar.this.inventorySlots.remove(index);
					ContainerRuneChainAltar.this.inventoryItemStacks.remove(index);
				}
			}
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
		this.addSlotToContainer(new SlotRuneChainAltarOutput(this.altar, 0, OUTPUT_SLOT_POSITION[0], OUTPUT_SLOT_POSITION[1], this));

		//Rune slots
		for(int i = 0; i < tile.getMaxChainLength(); i++) {
			int pageSlot = i % SLOTS_PER_PAGE;
			if(i != 0 && pageSlot == 0) {
				page = new Page(this.pages.size());
				this.pages.add(page);
			}
			Slot slot = new SlotRuneChainAltarInput(this.altar, i + 1, SLOT_POSITIONS[pageSlot][0], SLOT_POSITIONS[pageSlot][1], page);
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

		//TODO Need a better way than doing this in ctor
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
		IRuneContainer container = this.getRuneContainer(slotIndex - this.altar.getChainStart());
		return container != null ? container.getBlueprint() : null;
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		ItemStack prevStack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			prevStack = slotStack.copy();

			if(slotIndex < this.altar.getSizeInventory()) {
				if (!this.mergeItemStack(slotStack, this.altar.getSizeInventory(), this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(slotStack, 0, this.altar.getSizeInventory(), false)) {
				return ItemStack.EMPTY;
			}

			if(slotStack.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if(prevStack.getCount() != slotStack.getCount() && slotIndex < this.altar.getSizeInventory()) {
				slot.onTake(player, slotStack);
			}
		}

		return prevStack;
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

		if(slotIndex == 0) {
			this.updateInputsFromRuneChain();
		}
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

					this.altar.getContainerData().moveRuneData(i - chainStart + 1, i - chainStart);

					if(slot instanceof SlotRuneChainAltarInput && nextSlot instanceof SlotRuneChainAltarInput) {
						((SlotRuneChainAltarInput) slot).prevHoverTicks = ((SlotRuneChainAltarInput) slot).hoverTicks = ((SlotRuneChainAltarInput) nextSlot).hoverTicks;
						((SlotRuneChainAltarInput) nextSlot).prevHoverTicks = ((SlotRuneChainAltarInput) nextSlot).hoverTicks = 0;
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

					this.altar.getContainerData().moveRuneData(i - chainStart - 1, i - chainStart);

					if(slot instanceof SlotRuneChainAltarInput && prevSlot instanceof SlotRuneChainAltarInput) {
						((SlotRuneChainAltarInput) slot).prevHoverTicks = ((SlotRuneChainAltarInput) slot).hoverTicks = ((SlotRuneChainAltarInput) prevSlot).hoverTicks;
						((SlotRuneChainAltarInput) prevSlot).prevHoverTicks = ((SlotRuneChainAltarInput) prevSlot).hoverTicks = 0;
					}
				}
			}
		}

		this.onRunesChanged();

		this.altar.markDirty();
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
		return this.altar.getContainerData().getLinkedInputs(runeIndex);
	}

	@Override
	public IRuneLink getLink(int runeIndex, int input) {
		return this.altar.getContainerData().getLink(runeIndex, input);
	}

	@Override
	public boolean link(int runeIndex, int input, int outputRuneIndex, int output) {
		//TODO Validate that link is possible, especially on server side

		if(this.altar.getContainerData().link(runeIndex, input, outputRuneIndex, output)) {
			for(RuneContainerEntry entry : this.runeContainers.values()) {
				entry.container.onMarkLinked(input, this.altar.getContainerData().getLink(runeIndex, input));
			}

			this.onRunesChanged();

			this.altar.markDirty();

			return true;
		}
		return false;
	}

	@Override
	public IRuneLink unlink(int runeIndex, int input) {
		IRuneLink unlinked = this.altar.getContainerData().unlink(runeIndex, input);

		if(unlinked != null) {
			for(RuneContainerEntry entry : this.runeContainers.values()) {
				entry.container.onMarkUnlinked(input, unlinked);
			}

			this.onRunesChanged();

			this.altar.markDirty();
		}

		return unlinked;
	}

	@Override
	public void unlinkAll(int runeIndex) {
		Map<Integer, IRuneLink> links = new HashMap<>();

		for(int linkedInput : this.altar.getContainerData().getLinkedInputs(runeIndex)) {
			IRuneLink link = this.altar.getContainerData().getLink(runeIndex, linkedInput);
			links.put(linkedInput, link);
		}

		this.altar.getContainerData().unlinkAll(runeIndex);

		for(Entry<Integer, IRuneLink> unlinked : links.entrySet()) {
			for(RuneContainerEntry entry : this.runeContainers.values()) {
				entry.container.onMarkUnlinked(unlinked.getKey(), unlinked.getValue());
			}
		}

		this.onRunesChanged();

		this.altar.markDirty();
	}

	@Override
	public void moveRuneData(int fromRuneIndex, int toRuneIndex) {
		this.altar.getContainerData().moveRuneData(fromRuneIndex, toRuneIndex);

		for(RuneContainerEntry entry : this.runeContainers.values()) {
			entry.container.onRuneDataMoved(fromRuneIndex, toRuneIndex);
		}

		this.onRunesChanged();

		this.altar.markDirty();
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

				IRuneChainContainerData containerData = ContainerRuneChainAltar.this.altar.getContainerData();

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
			IRuneChainContainerData containerData = this.altar.getContainerData();

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
			public IRuneChainAltarContainer getRuneChainAltarContainer() {
				return ContainerRuneChainAltar.this;
			}

			@Override
			public IRuneChainAltarGui getRuneChainAltarGui() {
				return ContainerRuneChainAltar.this.getRuneChainAltarGui();
			}

			@Override
			public int getRuneIndex() {
				return entry.runeIndex;
			}

			@Override
			public ItemStack getRuneItemStack() {
				return ContainerRuneChainAltar.this.getRuneItemStack(entry.runeIndex);
			}

			@Override
			public NBTTagCompound getData() {
				IRuneChainContainerData info = ContainerRuneChainAltar.this.altar.getContainerData();
				NBTTagCompound nbt = info.getContainerNbt(entry.runeIndex);
				if(nbt == null) {
					info.setContainerNbt(entry.runeIndex, nbt = new NBTTagCompound());
					ContainerRuneChainAltar.this.altar.markDirty();
				}
				return nbt;
			}

			@Override
			public void setData(NBTTagCompound nbt) {
				ContainerRuneChainAltar.this.altar.getContainerData().setContainerNbt(entry.runeIndex, nbt);
				ContainerRuneChainAltar.this.altar.markDirty();
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

					ContainerRuneChainAltar.this.altar.getContainerData().setConfigurationId(entry.runeIndex, configuration.getId());

					//TODO Preferably store links per configuration
					ContainerRuneChainAltar.this.altar.getContainerData().unlinkAll(entry.runeIndex);
					ContainerRuneChainAltar.this.altar.getContainerData().unlinkAllIncoming(entry.runeIndex);

					ContainerRuneChainAltar.this.onRunesChanged();

					ContainerRuneChainAltar.this.altar.markDirty();
				}
			}
		};
	}

	protected IRuneChainAltarGui getRuneChainAltarGui() {
		return null;
	}

	@Override
	public Slot getRuneSlot(int runeIndex) {
		return this.getSlot(runeIndex + this.altar.getChainStart());
	}

	private boolean updateOutput = true;

	@Override
	public void onRunesChanged() {
		//TODO This needs tp be moved to and called from the tileentity so that the output also updates when a rune is inserted without container

		if(this.updateOutput) {
			this.updateOutput = false;
			
			ItemStack stack = this.altar.getStackInSlot(0);

			if(!stack.isEmpty() && stack.hasCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN, null)) {
				IRuneChainCapability cap = stack.getCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN, null);

				int runeCount = this.getRuneInventorySize();

				NonNullList<ItemStack> runes = NonNullList.withSize(runeCount, ItemStack.EMPTY);
				for(int i = 0; i < runeCount; i++) {
					runes.set(i, this.getRuneItemStack(i));
				}

				RuneChainData data = new RuneChainData(runes, this.altar.getContainerData());

				cap.setData(data);
			}

			this.altar.setInventorySlotContents(0, stack);

			this.updateOutput = true;
		}
	}

	//TODO This too needs to be moved to and called from the tileentity, see above
	protected void updateInputsFromRuneChain() {
		ItemStack stack = this.altar.getStackInSlot(0);

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
					this.altar.setContainerData(data.getContainerData());
					
					for(int i = 0; i < runes.size(); i++) {
						this.setRuneItemStack(i, runes.get(i));
					}
				}
			}

			this.updateOutput = true;
		}
	}
}