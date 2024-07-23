package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockEntityRegistry;

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
	public int getMaxStackSize() {
		return 1;
	}

	public int getItemCount() {
		return this.itemCount;
	}

	public void setItemCount(int amount) {
		this.itemCount = amount;
	}

	public void updateItemCount(Level level, BlockPos pos, BlockState state) {
		int prevCount = this.getItemCount();

		int amount = 0;
		for(int i = 0; i < this.getItems().size(); i++) {
			if (!this.getItem(i).isEmpty()) {
				this.setItemCount(++amount);
			}
		}

		if (this.isEmpty()) {
			this.setItemCount(0);
		}

		if(prevCount != this.getItemCount()) {
			level.sendBlockUpdated(pos, state, state, 2);
		}
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
}
