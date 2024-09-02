package thebetweenlands.common.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import thebetweenlands.common.inventory.slot.FilteredSlot;
import thebetweenlands.common.inventory.slot.SingleItemSlot;
import thebetweenlands.common.items.LifeCrystalItem;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.MenuRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public class MortarMenu extends AbstractContainerMenu {

	private final ContainerData data;
	private final Container mortar;
	private final Level level;

	public MortarMenu(int i, Inventory playerInventory) {
		this(i, playerInventory, new SimpleContainer(4), new SimpleContainerData(6));
	}

	public MortarMenu(int containerId, Inventory playerInventory, Container mortar, ContainerData data) {
		super(MenuRegistry.MORTAR.get(), containerId);
		checkContainerSize(mortar, 4);
		checkContainerDataCount(data, 1);
		mortar.startOpen(playerInventory.player);
		this.mortar = mortar;
		this.data = data;
		this.level = playerInventory.player.level();

		this.addSlot(new Slot(mortar, 0, 35, 36));
		this.addSlot(new FilteredSlot(mortar, 1, 79, 36, stack -> stack.is(ItemRegistry.PESTLE)) {
			@Override
			public void onTake(Player player, ItemStack stack) {
				super.onTake(player, stack);
				stack.remove(DataComponentRegistry.PESTLE_ACTIVE);
			}

			@Override
			public int getMaxStackSize() {
				return 1;
			}
		});
		this.addSlot(new FilteredSlot(mortar, 2, 123, 36, stack -> false));
		this.addSlot(new FilteredSlot(mortar, 3, 79, 8, stack -> stack.getItem() instanceof LifeCrystalItem));

		for (int k = 0; k < 3; k++) {
			for (int i1 = 0; i1 < 9; i1++) {
				this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 7 + i1 * 18, 83 + k * 18));
			}
		}

		for (int l = 0; l < 9; l++) {
			this.addSlot(new Slot(playerInventory, l, 7 + l * 18, 141));
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
			if (index == 1) {
				if (stack1.is(ItemRegistry.PESTLE)) {
					if (stack1.has(DataComponentRegistry.PESTLE_ACTIVE)) {
						stack1.remove(DataComponentRegistry.PESTLE_ACTIVE);
					}
				}
			}
			if (index == 2) {
				if (!this.moveItemStackTo(stack1, 4, 40, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(stack1, stack);
			} else if (index > 3) {
				if (this.level.getRecipeManager().getRecipeFor(RecipeRegistry.MORTAR_RECIPE.get(), new SingleRecipeInput(stack1), this.level).isPresent()) {
					if (!this.moveItemStackTo(stack1, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (stack1.is(ItemRegistry.PESTLE)) {
					if (!this.moveItemStackTo(stack1, 1, 2, false)) {
						return ItemStack.EMPTY;
					}
				} else if (stack1.getItem() instanceof LifeCrystalItem) {
					if (!this.moveItemStackTo(stack1, 3, 4, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 4 && index < 31) {
					if (!this.moveItemStackTo(stack1, 31, 40, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 31 && index < 40 && !this.moveItemStackTo(stack1, 4, 31, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(stack1, 4, 40, false)) {
				return ItemStack.EMPTY;
			}

			if (stack1.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (stack1.getCount() == stack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, stack1);
		}
		return stack;
	}

	public int getProgress() {
		return this.data.get(0);
	}

	@Override
	public boolean stillValid(Player player) {
		return this.mortar.stillValid(player);
	}
}
