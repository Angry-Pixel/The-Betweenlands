package thebetweenlands.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

import javax.annotation.Nullable;

public abstract class NoMenuContainerBlockEntity extends BlockEntity implements RandomizableContainer {

	@Nullable
	protected ResourceKey<LootTable> lootTable;
	protected long lootTableSeed = 0L;

	public NoMenuContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	protected abstract NonNullList<ItemStack> getItems();

	protected abstract void setItems(NonNullList<ItemStack> items);

	@Nullable
	@Override
	public ResourceKey<LootTable> getLootTable() {
		return this.lootTable;
	}

	@Override
	public void setLootTable(@Nullable ResourceKey<LootTable> lootTable) {
		this.lootTable = lootTable;
	}

	@Override
	public long getLootTableSeed() {
		return this.lootTableSeed;
	}

	@Override
	public void setLootTableSeed(long seed) {
		this.lootTableSeed = seed;
	}

	@Override
	public boolean isEmpty() {
		this.unpackLootTable(null);
		for (ItemStack itemstack : this.getItems()) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getItem(int index) {
		this.unpackLootTable(null);
		return this.getItems().get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		this.unpackLootTable(null);
		ItemStack itemstack = ContainerHelper.removeItem(this.getItems(), index, count);
		if (!itemstack.isEmpty()) {
			this.setChanged();
		}

		return itemstack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		this.unpackLootTable(null);
		return ContainerHelper.takeItem(this.getItems(), index);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		this.unpackLootTable(null);
		this.getItems().set(index, stack);
		stack.limitSize(this.getMaxStackSize(stack));
		this.setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		return Container.stillValidBlockEntity(this, player);
	}

	@Override
	public void clearContent() {
		this.getItems().clear();
	}

	@Override
	protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
		super.applyImplicitComponents(componentInput);
		SeededContainerLoot seededcontainerloot = componentInput.get(DataComponents.CONTAINER_LOOT);
		if (seededcontainerloot != null) {
			this.lootTable = seededcontainerloot.lootTable();
			this.lootTableSeed = seededcontainerloot.seed();
		}
		componentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(this.getItems());
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder components) {
		super.collectImplicitComponents(components);
		if (this.lootTable != null) {
			components.set(DataComponents.CONTAINER_LOOT, new SeededContainerLoot(this.lootTable, this.lootTableSeed));
		}

		components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));
	}

	@Override
	public void removeComponentsFromTag(CompoundTag tag) {
		super.removeComponentsFromTag(tag);
		tag.remove("LootTable");
		tag.remove("LootTableSeed");
		tag.remove("Items");
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
		this.loadAdditional(packet.getTag(), registries);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag tag = super.getUpdateTag(registries);
		this.saveAdditional(tag, registries);
		return tag;
	}
}
