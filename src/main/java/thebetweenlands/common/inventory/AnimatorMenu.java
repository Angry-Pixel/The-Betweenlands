package thebetweenlands.common.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.block.entity.AnimatorBlockEntity;
import thebetweenlands.common.inventory.slot.FilteredSlot;
import thebetweenlands.common.inventory.slot.SingleItemSlot;
import thebetweenlands.common.item.misc.LifeCrystalItem;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.MenuRegistry;

import java.util.Objects;

public class AnimatorMenu extends AbstractContainerMenu {
	private final AnimatorBlockEntity animator;
	private final ContainerData data;

	public AnimatorMenu(int i, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
		this(i, playerInventory, (AnimatorBlockEntity) Objects.requireNonNull(Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getBlockEntity(buf.readBlockPos()) : null), new SimpleContainerData(7));
	}

	public AnimatorMenu(int containerId, Inventory playerInventory, AnimatorBlockEntity animator, ContainerData data) {
		super(MenuRegistry.ANIMATOR.get(), containerId);
		checkContainerSize(animator, 3);
		checkContainerDataCount(data, 7);
		animator.startOpen(playerInventory.player);
		this.animator = animator;
		this.data = data;

		this.addSlot(new SingleItemSlot(animator, AnimatorBlockEntity.FOCAL_SLOT, 79, 23));
		this.addSlot(new FilteredSlot(animator, AnimatorBlockEntity.LIFE_CRYSTAL_SLOT, 34, 57, animator::isValidLifeCrystal));
		this.addSlot(new FilteredSlot(animator, AnimatorBlockEntity.FUEL_SLOT, 124, 57, animator::isValidFuel));

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

	public AnimatorBlockEntity getAnimator() {
		return this.animator;
	}

	public float getCrystalLife() {
		return this.data.get(2);
	}

	public int getLifeCount() {
		return this.data.get(6);
	}

	public int getFuelProgress() {
		return this.data.get(0);
	}

	public int getFuelDuration() {
		return Math.max(this.data.get(1), 1);
	}

	public double getBurnProgress() {
		return (this.data.get(4) + (this.getFuelProgress() / (double)this.getFuelDuration())) / (double) this.data.get(5);
	}
	
	@Override
	public void removed(Player player) {
		super.removed(player);
		this.animator.stopOpen(player);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack stack1 = slot.getItem();
			stack = stack1.copy();
			if (index > 2) {
				if (stack1.is(ItemRegistry.SULFUR))
					if (!this.moveItemStackTo(stack1, 2, 3, true))
						return ItemStack.EMPTY;
				if (stack1.getItem() instanceof LifeCrystalItem)
					if (!this.moveItemStackTo(stack1, 1, 2, true))
						return ItemStack.EMPTY;
				if (stack1.getCount() == 1 && !stack1.is(ItemRegistry.SULFUR) && !(stack1.getItem() instanceof LifeCrystalItem))
					if (!this.moveItemStackTo(stack1, 0, 1, true))
						return ItemStack.EMPTY;
			} else if (!this.moveItemStackTo(stack1, 3, this.slots.size(), false))
				return ItemStack.EMPTY;
			if (stack1.getCount() == 0)
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
			if (stack1.getCount() != stack.getCount())
				slot.onTake(player, stack1);
			else
				return ItemStack.EMPTY;
		}
		return stack;
	}

	@Override
	public boolean stillValid(Player player) {
		return this.animator.stillValid(player);
	}
}
