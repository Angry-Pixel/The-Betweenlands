package thebetweenlands.common.block.entity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.inventory.WeedwoodCraftingMenu;
import thebetweenlands.common.registries.BlockEntityRegistry;

public class WeedwoodCraftingTableBlockEntity extends BlockEntity implements MenuProvider {

	public NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
	public byte rotation = 0;

	public WeedwoodCraftingTableBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.WEEDWOOD_CRAFTING_TABLE.get(), pos, state);
	}

	public NonNullList<ItemStack> getItems() {
		return this.items;
	}

	public void slotChangedCraftingGrid() {
		
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
