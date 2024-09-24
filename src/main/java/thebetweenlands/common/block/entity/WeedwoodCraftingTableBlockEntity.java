package thebetweenlands.common.block.entity;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.inventory.container.WeedwoodCraftingContainer;
import thebetweenlands.common.inventory.WeedwoodCraftingMenu;
import thebetweenlands.common.registries.BlockEntityRegistry;

public class WeedwoodCraftingTableBlockEntity extends SyncedBlockEntity implements MenuProvider {

	public final NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
	public byte rotation = 0;

	public WeedwoodCraftingTableBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.WEEDWOOD_CRAFTING_TABLE.get(), pos, state);
	}

	public NonNullList<ItemStack> getItems() {
		return this.items;
	}

	private final Set<WeedwoodCraftingContainer> openInventories = new HashSet<>();

	public void openInventory(WeedwoodCraftingContainer inv) {
		this.openInventories.add(inv);
	}

	public void closeInventory(WeedwoodCraftingContainer inv) {
		this.openInventories.remove(inv);
	}

	public void slotChangedCraftingGrid() {
		for(WeedwoodCraftingContainer container : openInventories) {
			container.menu.slotChangedCraftingGrid();
		}
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("container.crafting");
	}

	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return new WeedwoodCraftingMenu(containerId, playerInventory, this, ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()));
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, this.items, registries);
		tag.putByte("rotation", this.rotation);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items.clear();
		ContainerHelper.loadAllItems(tag, this.items, registries);
		this.rotation = tag.getByte("rotation");
		this.slotChangedCraftingGrid();
	}
}
