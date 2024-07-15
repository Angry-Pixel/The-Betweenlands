package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.registries.BlockEntityRegistry;

public class MudBrickAlcoveBlockEntity extends NoMenuContainerBlockEntity {

	private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
	private int urnType = 0;
	private int rotationOffset = 0;
	public int dungeonLevel = 0;

	public MudBrickAlcoveBlockEntity(BlockPos pos, BlockState blockState) {
		super(BlockEntityRegistry.MUD_BRICK_ALCOVE.get(), pos, blockState);
	}

	public void setUrnRotationOffset(int rotation) {
		this.rotationOffset = rotation;
	}

	public int getUrnRotationOffset() {
		return this.rotationOffset;
	}

	public void setupUrn(boolean hasUrn, RandomSource random) {
		if (hasUrn) {
			this.urnType = random.nextInt(3);
			this.rotationOffset = random.nextInt(41) - 20;
		}
	}

	public void setDungeonLevel(int dungeonLevel) {
		this.dungeonLevel = dungeonLevel;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if (!this.trySaveLootTable(tag)) {
			ContainerHelper.saveAllItems(tag, this.items, registries);
		}
		tag.putInt("urn_type", this.urnType);
		tag.putInt("urn_rotation", this.rotationOffset);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		if (!this.tryLoadLootTable(tag)) {
			ContainerHelper.loadAllItems(tag, this.items, registries);
		}
		this.urnType = tag.getInt("urn_type");
		this.rotationOffset = tag.getInt("urn_rotation");
	}

	@Override
	public NonNullList<ItemStack> getItems() {
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
