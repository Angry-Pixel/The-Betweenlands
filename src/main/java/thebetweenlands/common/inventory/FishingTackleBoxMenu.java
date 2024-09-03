package thebetweenlands.common.inventory;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import thebetweenlands.common.inventory.slot.FilteredSlot;
import thebetweenlands.common.registries.MenuRegistry;

import java.util.Optional;

public class FishingTackleBoxMenu extends AbstractContainerMenu {

	private final Container box;
	private final Player player;
	private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 2, 2);
	private final ResultContainer resultSlots = new ResultContainer();

	public FishingTackleBoxMenu(int containerId, Inventory playerInventory) {
		this(containerId, playerInventory, new SimpleContainer(21));
	}

	public FishingTackleBoxMenu(int containerId, Inventory playerInventory, Container box) {
		super(MenuRegistry.FISHING_TACKLE_BOX.get(), containerId);
		checkContainerSize(box, 16);
		box.startOpen(playerInventory.player);
		this.box = box;
		this.player = playerInventory.player;

		this.addSlot(new ResultSlot(playerInventory.player, this.craftSlots, this.resultSlots, 0, 152, 46));

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				this.addSlot(new Slot(this.craftSlots, j + i * 2, 98 + j * 18, 36 + i * 18));
			}
		}

		for (int j = 0; j < 4; ++j) {
			for (int k = 0; k < 4; ++k) {
				this.addSlot(new FilteredSlot(box, k + j * 4, 8 + k * 18, 18 + j * 18, stack -> stack.getItem().canFitInsideContainerItems()));
			}
		}

		for (int l = 0; l < 3; ++l) {
			for (int j1 = 0; j1 < 9; ++j1) {
				this.addSlot(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 104 + l * 18));
			}
		}

		for (int i1 = 0; i1 < 9; ++i1) {
			this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 162));
		}
	}

	@Override
	public void slotsChanged(Container container) {
		//copy of CraftingMenu.slotChangedCraftingGrid
		if (this.player.level() instanceof ServerLevel level) {
			CraftingInput craftinginput = this.craftSlots.asCraftInput();
			ServerPlayer serverplayer = (ServerPlayer)this.player;
			ItemStack itemstack = ItemStack.EMPTY;
			Optional<RecipeHolder<CraftingRecipe>> optional = level.getServer()
				.getRecipeManager()
				.getRecipeFor(RecipeType.CRAFTING, craftinginput, level, (RecipeHolder<CraftingRecipe>) null);
			if (optional.isPresent()) {
				RecipeHolder<CraftingRecipe> recipeholder = optional.get();
				CraftingRecipe craftingrecipe = recipeholder.value();
				if (resultSlots.setRecipeUsed(level, serverplayer, recipeholder)) {
					ItemStack itemstack1 = craftingrecipe.assemble(craftinginput, level.registryAccess());
					if (itemstack1.isItemEnabled(level.enabledFeatures())) {
						itemstack = itemstack1;
					}
				}
			}

			this.resultSlots.setItem(0, itemstack);
			this.setRemoteSlot(0, itemstack);
			serverplayer.connection.send(new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), 0, itemstack));
		}
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		if (!player.level().isClientSide()) {
			this.clearContainer(player, this.craftSlots);
		}
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack stack1 = slot.getItem();
			stack = stack1.copy();

			if (!stack1.getItem().canFitInsideContainerItems()) {
				return ItemStack.EMPTY;
			}

			if (index == 0) {
				if (!this.moveItemStackTo(stack1, 5, 57, true))
					return ItemStack.EMPTY;
				slot.onQuickCraft(stack1, stack);
			} else if (index >= 1 && index < 5) {
				if (!this.moveItemStackTo(stack1, 5, 57, false))
					return ItemStack.EMPTY;
			} else if (index >= 5 && index < 21) {
				if (!this.moveItemStackTo(stack1, 21, 57, true))
					return ItemStack.EMPTY;
			} else if (!this.moveItemStackTo(stack1, 5, 21, false))
				return ItemStack.EMPTY;

			if (stack1.isEmpty())
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();

			if (stack1.getCount() == stack.getCount())
				return ItemStack.EMPTY;

			slot.onTake(player, stack1);
		}

		return stack;
	}

	@Override
	public boolean stillValid(Player player) {
		return this.box.stillValid(player);
	}

	@Override
	public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
		return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
	}
}
