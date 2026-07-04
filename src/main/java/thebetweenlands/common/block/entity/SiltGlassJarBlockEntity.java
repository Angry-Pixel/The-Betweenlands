package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.entity.util.NoMenuContainerBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class SiltGlassJarBlockEntity extends NoMenuContainerBlockEntity {

	private NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);
	private int itemCount = 0;

	public SiltGlassJarBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.SILT_GLASS_JAR.get(), pos, state);
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	@Override
	public int getContainerSize() {
		return 8;
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return stack.is(ItemRegistry.TINY_SLUDGE_WORM);
	}

	@Override
	public boolean canTakeItem(Container target, int slot, ItemStack stack) {
		return true;
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	public int getItemCount() {
		return this.itemCount;
	}

	public void setItemCount(int amount) {
		this.itemCount = amount;
	}

	public int calculateItemCount() {
		int amount = 0;
		for(int i = 0; i < this.getContainerSize(); i++) {
			if (!this.getItem(i).isEmpty()) {
				++amount;
			}
		}
		return amount;
	}

	public int updateItemCount() {
		int amount = this.calculateItemCount();
		
		this.setItemCount(amount);

		return amount;
	}
	
	public int updateItemCount(Level level, BlockPos pos, BlockState state) {
		return this.updateItemCount();
	}
	
	@Override
	public void setChanged() {
		this.updateItemCount();
		super.setChanged();
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, this.items, registries);
		tag.putInt("item_count", this.getItemCount());
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items, registries);
		this.setItemCount(tag.getInt("item_count"));
	}

	@Override
	protected void applyImplicitComponents(DataComponentInput input) {
		super.applyImplicitComponents(input);
		this.itemCount = input.getOrDefault(DataComponentRegistry.WORMS, 0);
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder components) {
		super.collectImplicitComponents(components);
		if (this.itemCount != 0) {
			components.set(DataComponentRegistry.WORMS, this.itemCount);
		}
	}

	@Override
	public void removeComponentsFromTag(CompoundTag tag) {
		super.removeComponentsFromTag(tag);
		tag.remove("item_count");
	}
}
