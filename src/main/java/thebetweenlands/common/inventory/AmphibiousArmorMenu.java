package thebetweenlands.common.inventory;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.api.item.amphibious.AmphibiousArmorUpgrade;
import thebetweenlands.common.component.item.UpgradeDamage;
import thebetweenlands.common.inventory.container.AmphibiousArmorContainer;
import thebetweenlands.common.inventory.slot.AmphibiousArmorSlot;
import thebetweenlands.common.items.amphibious.AmphibiousArmorItem;
import thebetweenlands.common.items.amphibious.ArmorEffectHelper;
import thebetweenlands.common.registries.AmphibiousArmorUpgradeRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.MenuRegistry;

public class AmphibiousArmorMenu extends AbstractContainerMenu {

	private final AmphibiousArmorContainer container;

	private final int numSlots;
	private final ItemStack armorItem;

	public AmphibiousArmorMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
		this(containerId, playerInventory, new AmphibiousArmorContainer(ItemStack.STREAM_CODEC.decode(buf), buf.readInt()));
	}

	public AmphibiousArmorMenu(int containerId, Inventory playerInventory, AmphibiousArmorContainer container) {
		super(MenuRegistry.AMPHIBIOUS_ARMOR.get(), containerId);
		this.container = container;
		this.numSlots = container.getContainerSize();
		this.armorItem = container.getContainerStack();

		if (this.numSlots >= 1) {
			this.addSlot(new AmphibiousArmorSlot(container, 0, 57, 73));
		}
		if (this.numSlots >= 2) {
			this.addSlot(new AmphibiousArmorSlot(container, 1, 93, 73));
		}
		if (this.numSlots >= 3) {
			this.addSlot(new AmphibiousArmorSlot(container, 2, 129, 73));
		}
		if (this.numSlots >= 4) {
			this.addSlot(new AmphibiousArmorSlot(container, 3, 75, 99));
		}
		if (this.numSlots >= 5) {
			this.addSlot(new AmphibiousArmorSlot(container, 4, 111, 99));
		}

		for (int l = 0; l < 3; ++l)
			for (int j1 = 0; j1 < 9; ++j1)
				this.addSlot(new Slot(playerInventory, j1 + (l + 1) * 9, 21 + j1 * 18, 139 + l * 18));

		for (int i1 = 0; i1 < 9; ++i1)
			this.addSlot(new Slot(playerInventory, i1, 21 + i1 * 18, 197));
	}

	public int getNumSlots() {
		return this.numSlots;
	}

	public ItemStack getArmorItem() {
		return this.armorItem;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot != null && slot.hasItem()) {
			ItemStack merge = slot.getItem();
			stack = merge.copy();

			if (index >= this.numSlots) {
				this.container.setUseUpgradeFilter(true);
				if (!this.moveStackIgnoreDamage(merge, 0, this.numSlots, false)) {
					return ItemStack.EMPTY;
				}
				this.container.setUseUpgradeFilter(false);
			} else {
				//Try to merge all but last item
				if (merge.getCount() > 1) {
					ItemStack lastItem = merge.split(1);
					merge.remove(DataComponentRegistry.UPGRADE_DAMAGE);

					this.moveItemStackTo(merge, this.numSlots, this.slots.size(), false);

					lastItem.setCount(1 + merge.getCount());
					merge = lastItem;
					stack = merge.copy();

					this.container.setPauseUpgradeDamageUpdates(true);
					slot.set(merge);
					slot.setChanged();
					this.container.setPauseUpgradeDamageUpdates(false);
				}

				//Try to merge last item with upgrade damage applied
				if (merge.getCount() == 1) {
					ItemStack invItem = this.container.getContainerStack();

					if (invItem.getItem() instanceof AmphibiousArmorItem armor) {
						Holder<AmphibiousArmorUpgrade> upgrade = ArmorEffectHelper.getUpgrade(armor.getType().getSlot(), stack);

						if (upgrade != AmphibiousArmorUpgradeRegistry.NONE) {
							UpgradeDamage damage = stack.getOrDefault(DataComponentRegistry.UPGRADE_DAMAGE, UpgradeDamage.EMPTY);

							if (damage.damage() > 0) {
								//Store upgrade damage in item before merging
								merge.set(DataComponentRegistry.UPGRADE_DAMAGE, new UpgradeDamage(damage.damage(), upgrade.value().getMaxDamage()));
							}
						}
					}

					if (!this.moveItemStackTo(merge, this.numSlots, this.slots.size(), false)) {
						merge.remove(DataComponentRegistry.UPGRADE_DAMAGE);
						return ItemStack.EMPTY;
					}
				}
			}

			if (merge.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (merge.getCount() == stack.getCount()) {
				return ItemStack.EMPTY;
			}
		}

		return stack;
	}

	//[VanillaCopy] of moveItemStackTo, but remove and re-add the upgrade damage component for proper item stacking and refilling
	protected boolean moveStackIgnoreDamage(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
		boolean flag = false;
		int i = startIndex;
		if (reverseDirection) {
			i = endIndex - 1;
		}

		if (stack.isStackable()) {
			while (!stack.isEmpty() && (reverseDirection ? i >= startIndex : i < endIndex)) {
				Slot slot = this.slots.get(i);
				ItemStack itemstack = slot.getItem();
				var damage = itemstack.remove(DataComponentRegistry.UPGRADE_DAMAGE);
				if (!itemstack.isEmpty() && ItemStack.isSameItemSameComponents(stack, itemstack)) {
					int j = itemstack.getCount() + stack.getCount();
					int k = slot.getMaxStackSize(itemstack);
					if (j <= k) {
						stack.setCount(0);
						itemstack.setCount(j);
						slot.setChanged();
						flag = true;
					} else if (itemstack.getCount() < k) {
						stack.shrink(k - itemstack.getCount());
						itemstack.setCount(k);
						slot.setChanged();
						flag = true;
					}
				}
				if (damage != null) {
					itemstack.set(DataComponentRegistry.UPGRADE_DAMAGE, damage);
				}

				if (reverseDirection) {
					i--;
				} else {
					i++;
				}
			}
		}

		if (!stack.isEmpty()) {
			if (reverseDirection) {
				i = endIndex - 1;
			} else {
				i = startIndex;
			}

			while (reverseDirection ? i >= startIndex : i < endIndex) {
				Slot slot1 = this.slots.get(i);
				ItemStack itemstack1 = slot1.getItem();
				if (itemstack1.isEmpty() && slot1.mayPlace(stack)) {
					int l = slot1.getMaxStackSize(stack);
					slot1.setByPlayer(stack.split(Math.min(stack.getCount(), l)));
					slot1.setChanged();
					flag = true;
					break;
				}

				if (reverseDirection) {
					i--;
				} else {
					i++;
				}
			}
		}

		return flag;
	}

	@Override
	public boolean stillValid(Player player) {
		//Check if armor is in main inventory
		for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
			if (player.getInventory().getItem(i) == this.container.getContainerStack()) {
				return true;
			}
		}

		return false;
	}
}
