package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockEntityRegistry;

public class LootPotBlockEntity extends NoMenuContainerBlockEntity {

	private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
	private int rotationOffset;

	public LootPotBlockEntity(BlockPos pos, BlockState blockState) {
		super(BlockEntityRegistry.LOOT_POT.get(), pos, blockState);
	}

	public void setModelRotationOffset(int rotation) {
		this.rotationOffset = rotation;
	}

	public int getModelRotationOffset() {
		return this.rotationOffset;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if (!this.trySaveLootTable(tag)) {
			ContainerHelper.saveAllItems(tag, this.items, registries);
		}
		tag.putInt("rotation", this.rotationOffset);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		if (!this.tryLoadLootTable(tag)) {
			ContainerHelper.loadAllItems(tag, this.items, registries);
		}
		this.rotationOffset = tag.getInt("rotation");
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
		return 3;
	}
}
