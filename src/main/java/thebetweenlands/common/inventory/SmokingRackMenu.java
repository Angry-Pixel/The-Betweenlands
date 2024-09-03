package thebetweenlands.common.inventory;

import net.minecraft.util.Mth;
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
import thebetweenlands.common.inventory.slot.SmokingRackOutputSlot;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.MenuRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

public class SmokingRackMenu extends AbstractContainerMenu {

	private final ContainerData data;
	private final Container rack;
	private final Level level;

	public SmokingRackMenu(int i, Inventory playerInventory) {
		this(i, playerInventory, new SimpleContainer(7), new SimpleContainerData(7));
	}

	public SmokingRackMenu(int containerId, Inventory playerInventory, Container rack, ContainerData data) {
		super(MenuRegistry.SMOKING_RACK.get(), containerId);
		checkContainerSize(rack, 7);
		checkContainerDataCount(data, 7);
		rack.startOpen(playerInventory.player);
		this.rack = rack;
		this.data = data;
		this.level = playerInventory.player.level();

		//fuel
		this.addSlot(new FilteredSlot(rack, 0, 26, 70, stack -> stack.is(BlockRegistry.FALLEN_LEAVES.asItem())));

		//input
		this.addSlot(new SingleItemSlot(rack, 1, 62, 34));
		this.addSlot(new SingleItemSlot(rack, 2, 62, 52));
		this.addSlot(new SingleItemSlot(rack, 3, 62, 70));

		//output
		this.addSlot(new SmokingRackOutputSlot(rack, 4, 134, 34));
		this.addSlot(new SmokingRackOutputSlot(rack, 5, 134, 52));
		this.addSlot(new SmokingRackOutputSlot(rack, 6, 134, 70));

		for (int k = 0; k < 3; k++) {
			for (int i1 = 0; i1 < 9; i1++) {
				this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 102 + k * 18));
			}
		}

		for (int l = 0; l < 9; l++) {
			this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 160));
		}

		this.addDataSlots(data);
	}

	public float getProgressForIndex(int index) {
		int i = this.data.get(index);
		int j = this.data.get(index + 3);
		return j != 0 && i != 0 ? Mth.clamp((float)i / (float)j, 0.0F, 1.0F) : 0.0F;
	}

	public float getSmokingProgress() {
		return Mth.clamp(this.data.get(0) / 200.0F, 0.0F, 1.0F);
	}

	public boolean isSmoking() {
		return this.data.get(0) > 0;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack stack1 = slot.getItem();
			stack = stack1.copy();

			if (index >= 4 && index < 7) {
				if (!this.moveItemStackTo(stack1, 7, 43, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(stack1, stack);
			} else if (index > 7) {
				if (stack1.is(BlockRegistry.FALLEN_LEAVES.asItem())) {
					if (!this.moveItemStackTo(stack1, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (this.canSmoke(stack1)) {
					if (!this.moveItemStackTo(stack1, 1, 4, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 7 && index < 34) {
					if (!this.moveItemStackTo(stack1, 34, 43, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index >= 34 && index < 43 && !this.moveItemStackTo(stack1, 7, 34, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(stack1, 7, 43, false)) {
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

	private boolean canSmoke(ItemStack stack) {
		return this.level.getRecipeManager().getRecipeFor(RecipeRegistry.SMOKING_RECIPE.get(), new SingleRecipeInput(stack), level).isPresent();
	}

	@Override
	public boolean stillValid(Player player) {
		return this.rack.stillValid(player);
	}
}
