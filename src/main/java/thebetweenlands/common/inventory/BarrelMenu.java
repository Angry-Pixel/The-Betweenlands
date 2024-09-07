package thebetweenlands.common.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import thebetweenlands.common.block.entity.BarrelBlockEntity;
import thebetweenlands.common.registries.MenuRegistry;

import java.util.Objects;

public class BarrelMenu extends AbstractContainerMenu {

	private final BarrelBlockEntity barrel;

	public BarrelMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
		this(containerId, playerInventory, (BarrelBlockEntity) Objects.requireNonNull(Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getBlockEntity(buf.readBlockPos()) : null));
	}

	public BarrelMenu(int containerId, Inventory playerInventory, BarrelBlockEntity barrel) {
		super(MenuRegistry.BARREL.get(), containerId);
		this.barrel = barrel;

		for (int l = 0; l < 3; ++l) {
			for (int j1 = 0; j1 < 9; ++j1) {
				this.addSlot(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
			}
		}

		for (int i1 = 0; i1 < 9; ++i1) {
			this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
		}
	}

	public FluidTank getTank() {
		return this.barrel.fluidTank;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();

			if (index >= 27 && index < 36) {
				if (!this.moveItemStackTo(itemstack1, 0, 27, false)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= 0 && index < 27) {
				if (!this.moveItemStackTo(itemstack1, 27, 36, false)) {
					return ItemStack.EMPTY;
				}
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, itemstack1);
		}

		return itemstack;
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}
}
