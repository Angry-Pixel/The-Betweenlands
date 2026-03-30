package thebetweenlands.common.inventory;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record PositionSupplyingMenuProvider(MenuProvider delegate, BlockPos pos) implements MenuProvider {

	public static @Nullable PositionSupplyingMenuProvider ofNullable(@Nullable MenuProvider delegate, BlockPos pos) {
		if(delegate == null) {
			return null;
		} else {
			return new PositionSupplyingMenuProvider(delegate, pos);
		}
	}
	
	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return this.delegate.createMenu(containerId, playerInventory, player);
	}

	@Override
	public Component getDisplayName() {
		return this.delegate.getDisplayName();
	}

	@Override
	public boolean shouldTriggerClientSideContainerClosingOnOpen() {
		return this.delegate.shouldTriggerClientSideContainerClosingOnOpen();
	}
	
	@Override
	public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
		this.delegate.writeClientSideData(menu, buffer);
		buffer.writeBlockPos(this.pos());
	}
	
}
