package thebetweenlands.common.inventory;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import thebetweenlands.api.recipes.CenserRecipe;
import thebetweenlands.common.block.entity.CenserBlockEntity;
import thebetweenlands.common.inventory.slot.FakeSlot;
import thebetweenlands.common.inventory.slot.FilteredSlot;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.MenuRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CenserMenu extends AbstractContainerMenu {

	private final CenserBlockEntity censer;
	private final ContainerData data;

	public CenserMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
		this(containerId, playerInventory, (CenserBlockEntity) Objects.requireNonNull(Minecraft.getInstance().level != null ? Minecraft.getInstance().level.getBlockEntity(buf.readBlockPos()) : null), new SimpleContainerData(4));
	}

	public CenserMenu(int containerId, Inventory playerInventory, CenserBlockEntity censer, ContainerData data) {
		super(MenuRegistry.CENSER.get(), containerId);
		checkContainerSize(censer, 3);
		checkContainerDataCount(data, 4);
		censer.startOpen(playerInventory.player);
		this.censer = censer;
		this.data = data;

		this.addSlot(new FilteredSlot(censer, 0, 80, 139, stack -> stack.is(ItemRegistry.SULFUR)));
		this.addSlot(new Slot(censer, 1, 44, 103));
		this.addSlot(new FakeSlot(censer, 2, 80, 103) {
			@Override
			public int getMaxStackSize() {
				return 1;
			}
		});

		for (int l = 0; l < 3; ++l) {
			for (int j1 = 0; j1 < 9; ++j1) {
				this.addSlot(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 175 + l * 18));
			}
		}

		for (int i1 = 0; i1 < 9; ++i1) {
			this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 233));
		}
		this.addDataSlots(data);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		if(index == 2) {
			return ItemStack.EMPTY;
		}
		ItemStack newStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			newStack = slotStack.copy();
			if (index > 2) {
				if (slotStack.is(ItemRegistry.SULFUR)) {
					if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.moveItemStackTo(slotStack, 1, 2, true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(slotStack, 3, 39, false))
				return ItemStack.EMPTY;
			if (slotStack.getCount() == 0)
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
			if (slotStack.getCount() != newStack.getCount())
				slot.onTake(player, slotStack);
			else
				return ItemStack.EMPTY;
		}
		return newStack;
	}

	public float getBurnProgress() {
		int i = this.data.get(0);
		int j = this.data.get(1);
		return j != 0 && i != 0 ? Mth.clamp((float)i / (float)j, 0.0F, 1.0F) : 0.0F;
	}

	public float getFogProgress() {
		return Mth.clamp((float)this.data.get(2) / (float)this.data.get(3), 0.0F, 1.0F);
	}

	public boolean isLit() {
		return this.data.get(0) > 0;
	}

	public int getEffectColor() {
		if (this.censer.getCurrentRecipe() != null) {
			return this.censer.getCurrentRecipe().getEffectColor(this.censer.getCurrentRecipeContext(), this.censer, CenserRecipe.EffectColorType.GUI);
		}
		return -1;
	}

	public boolean shouldRenderCover() {
		return this.censer.getCurrentRecipe() == null;
	}

	public FluidStack getTankFluid() {
		return this.censer.getFluidInTank(0);
	}

	public List<Component> getExtraTooltips(Slot slot) {
		List<Component> tooltip = new ArrayList<>();
		if (slot.getSlotIndex() == 2) {
			if (this.censer.getCurrentRecipe() != null) {
				List<Component> recipeTips = new ArrayList<>();
				this.censer.getCurrentRecipe().getLocalizedEffectText(this.censer.getCurrentRecipeContext(), this.censer, recipeTips);
				if (!recipeTips.isEmpty()) {
					tooltip.add(Component.empty());
					tooltip.add(Component.translatable("block.thebetweenlands.censer.current_effect").withStyle(ChatFormatting.DARK_PURPLE));
					tooltip.addAll(recipeTips);
				}
			}
		}
		return tooltip;
	}

	@Override
	public boolean stillValid(Player player) {
		return this.censer.stillValid(player);
	}
}
