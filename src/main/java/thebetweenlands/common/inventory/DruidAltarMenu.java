package thebetweenlands.common.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.block.container.DruidAltarBlock;
import thebetweenlands.common.block.entity.DruidAltarBlockEntity;
import thebetweenlands.common.inventory.slot.FilteredSlot;
import thebetweenlands.common.inventory.slot.SingleItemSlot;
import thebetweenlands.common.registries.MenuRegistry;

import java.util.Objects;

public class DruidAltarMenu extends AbstractContainerMenu {

	private final DruidAltarBlockEntity altar;

	public DruidAltarMenu(int i, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
		this(i, playerInventory, (DruidAltarBlockEntity) Objects.requireNonNull(Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getBlockEntity(buf.readBlockPos()) : null));
	}

	public DruidAltarMenu(int containerId, Inventory playerInventory, DruidAltarBlockEntity altar) {
		super(MenuRegistry.DRUID_ALTAR.get(), containerId);
		checkContainerSize(altar, 5);
		altar.startOpen(playerInventory.player);
		this.altar = altar;

		this.addSlot(new FilteredSlot(altar, 0, 81, 35, stack -> false));
		this.addSlot(new SingleItemSlot(altar, 1, 53, 7));
		this.addSlot(new SingleItemSlot(altar, 2, 109, 7));
		this.addSlot(new SingleItemSlot(altar, 3, 53, 63));
		this.addSlot(new SingleItemSlot(altar, 4, 109, 63));

		for (int k = 0; k < 3; k++) {
			for (int i1 = 0; i1 < 9; i1++) {
				this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
			}
		}

		for (int l = 0; l < 9; l++) {
			this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack stack1 = slot.getItem();
			stack = stack1.copy();
			if (index >= 5) {
				if (!this.moveItemStackTo(stack1, 1, 5, false)) {
					return ItemStack.EMPTY;
				}
				//Moves items from hotbar to inventory and vice versa
				if (index < 32) {
					if (!this.moveItemStackTo(stack1, 32, 41, false)) {
						return ItemStack.EMPTY;
					}
				} else {
					if (!this.moveItemStackTo(stack1, 5, 31, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!this.moveItemStackTo(stack1, 5, 41, false)) {
				return ItemStack.EMPTY;
			}
			if (stack1.getCount() == 0) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			if (stack1.getCount() != stack.getCount()) {
				slot.onTake(player, stack1);
			} else {
				return ItemStack.EMPTY;
			}
		}
		return stack;
	}

	@Override
	public boolean stillValid(Player player) {
		return this.altar.stillValid(player) && !this.altar.getBlockState().getValue(DruidAltarBlock.ACTIVE);
	}
}
