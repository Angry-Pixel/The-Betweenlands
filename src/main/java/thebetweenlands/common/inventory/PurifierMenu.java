package thebetweenlands.common.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;
import thebetweenlands.common.inventory.slot.FilteredSlot;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.MenuRegistry;

public class PurifierMenu extends AbstractContainerMenu {

	private final Container filter;
	private final ContainerData data;

	public PurifierMenu(int containerId, Inventory playerInventory) {
		this(containerId, playerInventory, new SimpleContainer(3), new SimpleContainerData(4));
	}

	public PurifierMenu(int containerId, Inventory playerInventory, Container filter, ContainerData data) {
		super(MenuRegistry.PURIFIER.get(), containerId);
		checkContainerSize(filter, 3);
		checkContainerDataCount(data, 4);
		filter.startOpen(playerInventory.player);
		this.filter = filter;
		this.data = data;

		this.addSlot(new Slot(filter, 0, 61, 14));
		this.addSlot(new FilteredSlot(filter, 1, 61, 54, stack -> stack.is(ItemRegistry.SULFUR)));
		this.addSlot(new Slot(filter, 2, 121, 34) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false;
			}

			@Override
			public void onTake(Player player, ItemStack stack) {
				if(player instanceof ServerPlayer sp && stack.is(BLItemTagProvider.CORRODIBLE)) {
					AdvancementCriteriaRegistry.PURIFY_TOOL.get().trigger(sp);
				}
				super.onTake(player, stack);
			}
		});

		for (int l = 0; l < 3; ++l) {
			for (int j1 = 0; j1 < 9; ++j1) {
				this.addSlot(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
			}
		}

		for (int i1 = 0; i1 < 9; ++i1) {
			this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
		}

		this.addDataSlots(data);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot != null && slot.hasItem()) {
			ItemStack stack1 = slot.getItem();
			stack = stack1.copy();
			if (index == 2) {
				if (!this.moveItemStackTo(stack1, 3, 39, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(stack1, stack);
			} else if (index != 0 && index != 1) {
				if (stack1.is(ItemRegistry.SULFUR)) {
					if (!this.moveItemStackTo(stack1, 1, 2, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.moveItemStackTo(stack1, 0, 1, false)) {
					return ItemStack.EMPTY;
				} else if (index >= 3 && index < 30) {
					if (!this.moveItemStackTo(stack1, 30, 39, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 30 && index < 39 && !this.moveItemStackTo(stack1, 3, 30, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(stack1, 3, 39, false)) {
				return ItemStack.EMPTY;
			}

			if (stack1.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			slot.onTake(player, stack1);
		}

		return stack;
	}

	public float getTankFill() {
		int i = this.data.get(2);
		int j = this.data.get(3);
		return j != 0 && i != 0 ? Mth.clamp((float)i / (float)j, 0.0F, 1.0F) : 0.0F;
	}

	public boolean isPurifying() {
		return this.data.get(0) > 0;
	}

	public float getPurifyingProgress() {
		int i = this.data.get(1);
		if (i == 0) {
			i = 432;
		}

		return Mth.clamp((float)this.data.get(0) / (float)i, 0.0F, 1.0F);
	}

	@Override
	public boolean stillValid(Player player) {
		return this.filter.stillValid(player);
	}
}
