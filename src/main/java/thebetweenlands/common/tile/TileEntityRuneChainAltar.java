package thebetweenlands.common.tile;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import thebetweenlands.api.rune.IRuneChainContainerData;
import thebetweenlands.common.inventory.container.runechainaltar.ContainerRuneChainAltar;
import thebetweenlands.common.inventory.container.runechainaltar.RuneChainContainerData;
import thebetweenlands.common.registries.CapabilityRegistry;

public class TileEntityRuneChainAltar extends TileEntity implements ISidedInventory {
	private final String name;

	protected NonNullList<ItemStack> inventory;
	protected final ItemStackHandler inventoryHandler;

	protected IRuneChainContainerData containerData = new RuneChainContainerData();

	public static final int OUTPUT_SLOT = 0;
	public static final int NON_INPUT_SLOTS = 1;

	private ContainerRuneChainAltar openContainer;

	public TileEntityRuneChainAltar() {
		this(43 /*output + 3 full pages*/, "rune_chain_altar" /*TODO: Proper name??*/);
	}

	protected TileEntityRuneChainAltar(int invtSize, String name) {
		this.inventoryHandler = new ItemStackHandler(NonNullList.withSize(0, ItemStack.EMPTY)) {
			@Override
			public void setSize(int size) {
				Preconditions.checkArgument(size >= 2, "Rune chain altar inventory must have at least one input and one output slot");
				this.stacks = TileEntityRuneChainAltar.this.inventory = NonNullList.withSize(invtSize, ItemStack.EMPTY);
			}

			@Override
			public void setStackInSlot(int slot, ItemStack stack) {
				super.setStackInSlot(slot, stack);
			}

			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				if (stack.isEmpty() || (!TileEntityRuneChainAltar.this.isOutputItemAvailable() && slot >= NON_INPUT_SLOTS) || !TileEntityRuneChainAltar.this.isItemValidForSlot(slot, stack)) {
					return stack;
				}

				return super.insertItem(slot, stack, simulate);
			}

			@Override
			protected void onContentsChanged(int slot) {
				TileEntityRuneChainAltar.this.markDirty();

				if(openContainer != null) {
					openContainer.onSlotChanged(slot);
				}
			}

			@Override
			public int getSlots() {
				return TileEntityRuneChainAltar.this.getSizeInventory();
			}

			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}
		};

		this.inventoryHandler.setSize(invtSize);

		this.name = name;
	}

	public void openContainer(ContainerRuneChainAltar container) {
		this.openContainer = container;
	}

	public void closeContainer() {
		this.openContainer = null;
	}

	@Override
	public int getSizeInventory() {
		return Math.min(this.getChainLength() + 1 + NON_INPUT_SLOTS, this.inventory.size());
	}

	@Override
	public boolean isEmpty() {
		return this.inventory.isEmpty();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return this.inventoryHandler.getStackInSlot(slot);
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		if(this.openContainer != null && this.openContainer.getPlayer() != player) {
			return false;
		}
		if (this.world.getTileEntity(this.pos) != this) {
			return false;
		} else {
			return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		int[] slots = new int[getSizeInventory()];
		for (int i = 0; i < slots.length; i++) {
			slots[i] = i;
		}

		return slots;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if(slot == OUTPUT_SLOT) {
			return stack.hasCapability(CapabilityRegistry.CAPABILITY_RUNE_CHAIN, null);
		} else {
			return this.isOutputItemAvailable() && slot < this.getSizeInventory() && stack.hasCapability(CapabilityRegistry.CAPABILITY_RUNE, null);
		}
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
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
		return this.isItemValidForSlot(slot, stack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return this.inventoryHandler.extractItem(index, count, false);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return this.inventoryHandler.extractItem(index, !this.inventoryHandler.getStackInSlot(index).isEmpty() ? this.inventoryHandler.getStackInSlot(index).getCount() : 0, false);
	}

	@Override
	public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
		this.inventoryHandler.setStackInSlot(index, stack);
	}

	@Override
	public void clear() {
		for(int i = 0; i < this.inventoryHandler.getSlots(); i++) {
			this.inventoryHandler.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, net.minecraft.util.EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing) {
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) this.inventoryHandler;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.readInventoryNBT(nbt);
		this.containerData = RuneChainContainerData.readFromNBT(nbt.getCompoundTag("chainInfo"));
	}

	protected void readInventoryNBT(NBTTagCompound nbt) {
		this.clear();
		if(nbt.hasKey("Inventory", Constants.NBT.TAG_COMPOUND)) {
			this.inventoryHandler.deserializeNBT(nbt.getCompoundTag("Inventory"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		this.writeInventoryNBT(nbt);
		nbt.setTag("chainInfo", RuneChainContainerData.writeToNBT(this.containerData, new NBTTagCompound()));
		return nbt;
	}

	protected void writeInventoryNBT(NBTTagCompound nbt) {
		NBTTagCompound inventoryNbt = this.inventoryHandler.serializeNBT();
		nbt.setTag("Inventory", inventoryNbt);
	}


	//TODO Remove all this and only sync when GUI is opened
	//##################################
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setTag("chainInfo", RuneChainContainerData.writeToNBT(this.containerData, new NBTTagCompound()));
		return nbt;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("chainInfo", RuneChainContainerData.writeToNBT(this.containerData, new NBTTagCompound()));
		return new SPacketUpdateTileEntity(this.getPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.containerData = RuneChainContainerData.readFromNBT(pkt.getNbtCompound().getCompoundTag("chainInfo"));
	}
	//##################################


	public int getChainStart() {
		return NON_INPUT_SLOTS;
	}

	public int getChainLength() {
		for(int i = this.inventory.size() - 1; i >= NON_INPUT_SLOTS; i--) {
			if(!this.inventory.get(i).isEmpty()) {
				return i + 1 - NON_INPUT_SLOTS;
			}
		}
		return 0;
	}

	public int getMaxChainLength() {
		return this.inventory.size() - NON_INPUT_SLOTS;
	}

	public void setContainerData(IRuneChainContainerData data) {
		this.containerData = data;
		this.markDirty();
	}
	
	public IRuneChainContainerData getContainerData() {
		return this.containerData;
	}
	
	public boolean isOutputItemAvailable() {
		return !this.inventory.get(OUTPUT_SLOT).isEmpty();
	}
}
