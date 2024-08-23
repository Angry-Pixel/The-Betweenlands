package thebetweenlands.common.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.block.entity.FishTrimmingTableBlockEntity;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.MenuRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.Objects;

public class FishTrimmingTableMenu extends AbstractContainerMenu {

	private final FishTrimmingTableBlockEntity table;

	public FishTrimmingTableMenu(int i, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
		this(i, playerInventory, (FishTrimmingTableBlockEntity) Objects.requireNonNull(Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getBlockEntity(buf.readBlockPos()) : null));
	}

	public FishTrimmingTableMenu(int containerId, Inventory playerInventory, FishTrimmingTableBlockEntity table) {
		super(MenuRegistry.FISH_TRIMMING_TABLE.get(), containerId);
		table.startOpen(playerInventory.player);
		this.table = table;

		this.addSlot(new Slot(table, 0, 80, 27));

		this.addSlot(new TrimmingResultSlot(table, 1, 44, 77));
		this.addSlot(new TrimmingResultSlot(table, 2, 80, 77));
		this.addSlot(new TrimmingResultSlot(table, 3, 116, 77));
		this.addSlot(new TrimmingResultSlot(table, 4, 8, 113));

		this.addSlot(new Slot(table, 5, 152, 113) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.is(ItemRegistry.BONE_AXE);
			}
		});

		for (int l = 0; l < 3; l++) {
			for (int k = 0; k < 9; k++) {
				this.addSlot(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, l * 18 + 145));
			}
		}

		for (int i1 = 0; i1 < 9; i1++) {
			this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 203));
		}
	}

	public FishTrimmingTableBlockEntity getContainer() {
		return this.table;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot != null && slot.hasItem()) {
			ItemStack stack1 = slot.getItem();
			stack = stack1.copy();

			if (index > this.table.getContainerSize()) {
				if (stack1.is(ItemRegistry.BONE_AXE)) {
					if (!this.moveItemStackTo(stack1, 5, 6, false))
						return ItemStack.EMPTY;
				}

				if (!this.moveItemStackTo(stack1, 0, 5, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(stack1, 6, this.slots.size(), false)) {
				return ItemStack.EMPTY;
			}

			if (stack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return stack;
	}

	@Override
	public boolean stillValid(Player player) {
		return this.table.stillValid(player);
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		this.table.stopOpen(player);
	}

	public void chop(ServerPlayer player) {
		if (this.table.getStoredRecipe() != null && this.table.hasChopper() && this.table.allResultSlotsEmpty()) {

			// set slot contents 1, 2, 3 to butcher items and 4 to guts (if applicable)
			int numItems = 0;
			for (int i = 1; i <= 4; i++) {
				ItemStack result = this.table.getSlotResult(player.level(), i, numItems);
				numItems += result.getCount();
				this.getSlot(i).set(result);
			}

			// damage axe
			this.table.getItem(5).hurtAndBreak(1, player.serverLevel(), player, (item) -> {});

			// set slot contents 0 to empty last so logic works in order
			this.table.setItem(0, this.table.getSlotResult(player.level(), 0, 0));

			this.slotsChanged(this.table);

			player.level().playSound(null, this.table.getBlockPos(), SoundRegistry.FISH_CHOP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
		}
	}

	public class TrimmingResultSlot extends Slot {

		private int prevCount;

		public TrimmingResultSlot(Container table, int slot, int x, int y) {
			super(table, slot, x, y);
		}

		private void updateCount() {
			this.prevCount = this.getItem().getCount();
		}

		@Override
		public void setChanged() {
			super.setChanged();

			if (!this.container.getItem(4).isEmpty()) {
				int index = this.getSlotIndex();

				if (index == 1 || index == 2 || index == 3) {
					int removed = Math.max(0, this.prevCount - this.getItem().getCount());

					if (removed > 0) {
						this.container.getItem(4).shrink(removed);
						this.container.setChanged();

						FishTrimmingTableMenu.this.slotsChanged(this.container);
					}
				}
			}

			this.updateCount();
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return false;
		}

		@Override
		public void onTake(Player player, ItemStack stack) {
			if (player instanceof ServerPlayer sp) {
				AdvancementCriteriaRegistry.TRIM_FISH.get().trigger(sp);
			}

			if (this.getSlotIndex() == 4) {
				this.container.setItem(1, ItemStack.EMPTY);
				this.container.setItem(2, ItemStack.EMPTY);
				this.container.setItem(3, ItemStack.EMPTY);
				FishTrimmingTableMenu.this.slotsChanged(this.container);
			}

			super.onTake(player, stack);
		}

		@Override
		public boolean isFake() {
			return true;
		}
	}
}
