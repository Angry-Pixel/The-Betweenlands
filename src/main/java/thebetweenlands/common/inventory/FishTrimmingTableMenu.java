package thebetweenlands.common.inventory;

import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.FishTrimmingTableBlockEntity;
import thebetweenlands.common.inventory.slot.FilteredSlot;
import thebetweenlands.common.inventory.slot.HiddenResultSlot;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.MenuRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class FishTrimmingTableMenu extends AbstractContainerMenu {

	private static final int FISH_SLOT = 0;
	private static final int OUTPUT_START_SLOT = 1;
	private static final int OUTPUT_END_SLOT = 3;
	private static final int REMAINS_SLOT = 4;
	private static final int CHOPPER_SLOT = 5;
	private static final int RESULT_PREVIEW_START_SLOT = 6;
	private static final int RESULT_PREVIEW_END_SLOT = 8;
	private static final int PLAYER_INVENTORY_START_SLOT = 9;
	
	private final FishTrimmingTableBlockEntity table;
	private final ContainerData containerData;
	private final Player player; // Used by vanilla, see CrafterMenu#player
	// Container for the fish trim results
	private final Container resultContainer = new SimpleContainer(3);

	public FishTrimmingTableMenu(int i, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
		this(i, playerInventory, (FishTrimmingTableBlockEntity) Objects.requireNonNull(Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getBlockEntity(buf.readBlockPos()) : null), new SimpleContainer(1), new SimpleContainerData(FishTrimmingTableBlockEntity.DATA_FIELD_COUNT));
	}

	public FishTrimmingTableMenu(int containerId, Inventory playerInventory, FishTrimmingTableBlockEntity table, Container remainsAccess, ContainerData containerData) {
		super(MenuRegistry.FISH_TRIMMING_TABLE.get(), containerId);
		checkContainerSize(table, FishTrimmingTableBlockEntity.SLOT_COUNT);
		checkContainerDataCount(containerData, FishTrimmingTableBlockEntity.DATA_FIELD_COUNT);
		checkContainerSize(remainsAccess, 1);
		table.startOpen(playerInventory.player);
		this.player = playerInventory.player;
		this.table = table;
		this.containerData = containerData;

		this.addSlot(new Slot(table, FishTrimmingTableBlockEntity.FISH_SLOT, 80, 27) {
            @Override
            public void setChanged() {
                super.setChanged();
                FishTrimmingTableMenu.this.slotsChanged(this.container);
            }
        });

		this.addSlot(new TrimmingResultSlot(table, FishTrimmingTableBlockEntity.OUTPUT_SLOT_1, 44, 77));
		this.addSlot(new TrimmingResultSlot(table, FishTrimmingTableBlockEntity.OUTPUT_SLOT_2, 80, 77));
		this.addSlot(new TrimmingResultSlot(table, FishTrimmingTableBlockEntity.OUTPUT_SLOT_3, 116, 77));
		this.addSlot(new RemainsResultSlot(remainsAccess, 0, 8, 113));
		
		this.addSlot(new FilteredSlot(table, FishTrimmingTableBlockEntity.CHOPPER_SLOT, 152, 113, table::isChopper) {
            @Override
            public void setChanged() {
                super.setChanged();
                FishTrimmingTableMenu.this.slotsChanged(this.container);
            }
        });

		// Slots for the result item preview
		this.addSlot(new HiddenResultSlot(this.resultContainer, 0, 44, 95));
		this.addSlot(new HiddenResultSlot(this.resultContainer, 1, 80, 95));
		this.addSlot(new HiddenResultSlot(this.resultContainer, 2, 116, 95));

		for (int l = 0; l < 3; l++) {
			for (int k = 0; k < 9; k++) {
				this.addSlot(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, l * 18 + 145));
			}
		}

		for (int i1 = 0; i1 < 9; i1++) {
			this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 203));
		}
		
		this.addDataSlots(containerData);

		this.refreshRecipeResult();
	}
	
	public boolean isChopperValid() {
		return this.containerData.get(FishTrimmingTableBlockEntity.FIELD_CHOPPER_VALID) != 0;
	}
	
	public boolean hasRecipe() {
		return this.containerData.get(FishTrimmingTableBlockEntity.FIELD_HAS_RECIPE) != 0;
	}
	
	public boolean canChop() {
		return this.containerData.get(FishTrimmingTableBlockEntity.FIELD_CAN_CHOP) != 0;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		if(RESULT_PREVIEW_START_SLOT <= index && index <= RESULT_PREVIEW_END_SLOT) return ItemStack.EMPTY;
		
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot != null && slot.hasItem()) {
			ItemStack stack1 = slot.getItem();
			stack = stack1.copy();

			if (index > this.table.getContainerSize()) {
				if (stack1.is(ItemRegistry.BONE_AXE)) {
					if (!this.moveItemStackTo(stack1, CHOPPER_SLOT, CHOPPER_SLOT + 1, false))
						return ItemStack.EMPTY;
				}

				if (!this.moveItemStackTo(stack1, FISH_SLOT, CHOPPER_SLOT, false)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(stack1, PLAYER_INVENTORY_START_SLOT, this.slots.size(), false)) {
				return ItemStack.EMPTY;
			}

			if (stack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			slot.onTake(player, stack1);
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
	
	// See CrafterMenu#refreshRecipeResult
	private void refreshRecipeResult() {
		TheBetweenlands.LOGGER.info("Player: {}", this.player);
		if(this.player instanceof ServerPlayer serverPlayer) {
			this.resultContainer.setItem(0, this.table.getSlotResult(serverPlayer.level(), FishTrimmingTableBlockEntity.OUTPUT_SLOT_1));
			this.resultContainer.setItem(1, this.table.getSlotResult(serverPlayer.level(), FishTrimmingTableBlockEntity.OUTPUT_SLOT_2));
			this.resultContainer.setItem(2, this.table.getSlotResult(serverPlayer.level(), FishTrimmingTableBlockEntity.OUTPUT_SLOT_3));
		}
	}
	
	@Override
	public void slotsChanged(Container container) {
		this.refreshRecipeResult(); // See CrafterMenu#slotsChanged
		super.slotsChanged(container);
	}
	
	@Override
	public boolean clickMenuButton(Player player, int id) {
		if(id == 0) {
			if(!player.level().isClientSide()) {
				this.chop((ServerPlayer)player);
			}
			return true;
		}
		return super.clickMenuButton(player, id);
	}
	
	public void chop(ServerPlayer player) {
		if (this.table.getStoredRecipe() != null && this.table.hasChopper() && this.table.allResultSlotsEmpty()) {

			// set slot contents 1, 2, 3 to butcher items
			int numItems = 0;
			for (int i = OUTPUT_START_SLOT; i <= OUTPUT_END_SLOT; i++) {
				ItemStack result = this.table.getSlotResult(player.level(), i);
				numItems += result.getCount();
				this.getSlot(i).set(result);
			}
			
			// set remains items (if applicable)
			this.table.setRemains(this.table.getRemainsItemResult(player.level()), numItems);

			// damage axe
			this.table.getItem(FishTrimmingTableBlockEntity.CHOPPER_SLOT).hurtAndBreak(1, player.serverLevel(), player, (item) -> {});

			// set fish to empty last so logic works in order
			this.table.setItem(FishTrimmingTableBlockEntity.FISH_SLOT, this.table.getSlotResult(player.level(), FishTrimmingTableBlockEntity.FISH_SLOT));

			this.table.setChanged();
			this.slotsChanged(this.table);
			this.table.markUpdated();

			player.level().playSound(null, this.table.getBlockPos(), SoundRegistry.FISH_CHOP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
		}
	}
	
	public class RemainsResultSlot extends Slot {

		public RemainsResultSlot(Container container, int slot, int x, int y) {
			super(container, slot, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return false;
		}
		
		@Override
		public void setChanged() {
			super.setChanged();
			
			FishTrimmingTableMenu.this.slotsChanged(this.container);
		}

		@Override
		public void onTake(Player player, ItemStack stack) {
			if (player instanceof ServerPlayer sp) {
				AdvancementCriteriaRegistry.TRIM_FISH.get().trigger(sp);
			}

			super.onTake(player, stack);
		}
		
		@Override
		public boolean isFake() {
			return true;
		}
	}
	
	public class TrimmingResultSlot extends Slot {

		private final FishTrimmingTableBlockEntity table;

		public TrimmingResultSlot(FishTrimmingTableBlockEntity table, int slot, int x, int y) {
			super(table, slot, x, y);
			this.table = table;
		}

		@Override
		public void setChanged() {
			super.setChanged();

			FishTrimmingTableMenu.this.slotsChanged(this.container);
			this.table.markUpdated();
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

			if(stack.getCount() > 0) {
				this.table.removeRemains(stack.getCount());
			}
			
			super.onTake(player, stack);
		}
	}
}
