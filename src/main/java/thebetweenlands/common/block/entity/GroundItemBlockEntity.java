package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.ContainerSingleItem;
import thebetweenlands.common.registries.BlockEntityRegistry;

public class GroundItemBlockEntity extends SyncedBlockEntity implements ContainerSingleItem.BlockContainerSingleItem {

	private ItemStack item = ItemStack.EMPTY;

	public GroundItemBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.GROUND_ITEM.get(), pos, state);
	}

	@Override
	public BlockEntity getContainerBlockEntity() {
		return this;
	}

	@Override
	public ItemStack getTheItem() {
		return this.item;
	}

	@Override
	public void setTheItem(ItemStack item) {
		this.item = item;
		this.setChanged();
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if (!this.getTheItem().isEmpty()) {
			tag.put("item", this.getTheItem().save(registries));
		}
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if (tag.contains("item", Tag.TAG_COMPOUND)) {
			this.item = ItemStack.parse(registries, tag.getCompound("item")).orElse(ItemStack.EMPTY);
		} else {
			this.item = ItemStack.EMPTY;
		}
	}
}
