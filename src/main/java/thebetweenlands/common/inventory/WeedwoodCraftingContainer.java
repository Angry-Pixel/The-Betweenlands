package thebetweenlands.common.inventory;

import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.entity.WeedwoodCraftingTableBlockEntity;

public class WeedwoodCraftingContainer implements CraftingContainer {
	
	public final AbstractContainerMenu menu;
	private final WeedwoodCraftingTableBlockEntity tile;

	public WeedwoodCraftingContainer(AbstractContainerMenu menu, WeedwoodCraftingTableBlockEntity tile) {
		this.menu = menu;
		this.tile = tile;
	}
	
	@Override
	public int getContainerSize() {
		return 9;
	}

	@Override
	public boolean isEmpty() {
        for (ItemStack itemstack : this.getItemsMutable()) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

		return true;
	}

	protected NonNullList<ItemStack> getItemsMutable() {
		return tile.getItems();
	}
	
	@Override
	public List<ItemStack> getItems() {
		return List.copyOf(this.getItemsMutable());
	}

	@Override
	public ItemStack getItem(int slot) {
		return this.getItemsMutable().get(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
        ItemStack itemstack = ContainerHelper.removeItem(this.getItemsMutable(), slot, amount);
        if (!itemstack.isEmpty()) {
        	if(this.menu != null)
        		this.menu.slotsChanged(this);
        	if(this.tile != null)
        		this.tile.slotChangedCraftingGrid();
        }

		return itemstack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack stack = ContainerHelper.takeItem(this.getItemsMutable(), slot);
		this.tile.slotChangedCraftingGrid(); // Sorry
		return stack;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		this.getItemsMutable().set(slot, stack);
		this.tile.slotChangedCraftingGrid();
	}

	@Override
	public void setChanged() {
		this.tile.setChanged();
		BlockState state = this.tile.getLevel().getBlockState(this.tile.getBlockPos());
		this.tile.getLevel().sendBlockUpdated(this.tile.getBlockPos(), state, state, Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS);
	}

	@Override
	public boolean stillValid(Player player) {
		return this.menu.stillValid(player);
	}

	@Override
	public void clearContent() {
		this.getItemsMutable().clear();
		this.tile.slotChangedCraftingGrid();
	}

	@Override
	public void fillStackedContents(StackedContents contents) {
        for (ItemStack itemstack : this.getItemsMutable()) {
            contents.accountSimpleStack(itemstack);
        }
	}

	@Override
	public int getWidth() {
		return 3;
	}

	@Override
	public int getHeight() {
		return 3;
	}

}
