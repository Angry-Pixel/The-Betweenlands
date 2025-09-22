package thebetweenlands.common.component.entity.equipment;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import thebetweenlands.api.item.RadialMenuEquippable;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.AttachmentRegistry;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class EquipmentHelper {

	/**
	 * NBT containing whether the entity does *not* have any equipment.
	 * Using the inverse because old entities may not have this tag yet and so we can still do a quick check
	 * instead of having to do hasKey.
	 */
	public static final String NBT_HAS_NO_EQUIPMENT = "thebetweenlands.has_no_equipment";

	/**
	 * Returns the first item stack of the specified item, or empty if none is found
	 *
	 * @param entity
	 * @param item
	 * @return
	 */
	public static ItemStack getEquipment(EquipmentInventoryType inventory, Entity entity, Item item) {
		return getEquipment(inventory, entity, stack -> stack.is(item));
	}

	/**
	 * Returns the first item stack of the specified item, or empty if none is found
	 *
	 * @param entity
	 * @param predicate
	 * @return
	 */
	public static ItemStack getEquipment(EquipmentInventoryType inventory, Entity entity, Predicate<ItemStack> predicate) {
		EquipmentData data = entity.getData(AttachmentRegistry.EQUIPMENT);
		Container mainInv = data.getContainer(entity, inventory);
		for (int i = 0; i < mainInv.getContainerSize(); i++) {
			ItemStack stack = mainInv.getItem(i);
			if (predicate.test(stack)) {
				return stack;
			}
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Tries to equip the specified item, returns the leftover stack
	 *
	 * @param player
	 * @param target
	 * @param stack
	 * @return
	 */
	public static ItemStack equipItem(@Nullable Player player, Entity target, ItemStack stack, boolean simulate) {
		if (stack.getItem() instanceof RadialMenuEquippable equippable) {
			EquipmentData data = target.getData(AttachmentRegistry.EQUIPMENT);
			if (equippable.canEquip(stack, player, target)) {
				EquipmentInventoryType type = equippable.getEquipmentCategory(stack);

				stack = stack.copy();

				if (target instanceof ServerPlayer serverPlayer) {
					AdvancementCriteriaRegistry.EQUIP.get().trigger(serverPlayer, stack);
				}

				Container inv = data.getContainer(target, type);
				IItemHandler wrapper = new InvWrapper(inv);

				ItemStack result = ItemHandlerHelper.insertItem(wrapper, stack, true);

				if (simulate) {
					return result;
				}

				if (result.isEmpty() || result.getCount() != stack.getCount()) {
					target.getPersistentData().putBoolean(NBT_HAS_NO_EQUIPMENT, false);

					equippable.onEquip(stack, target, inv);

					return ItemHandlerHelper.insertItem(wrapper, stack, false);
				}
			}
		}

		return stack;
	}

	/**
	 * Tries to equip the specified item in the specified slot, returns the leftover stack
	 *
	 * @param player
	 * @param target
	 * @param stack
	 * @param slot
	 * @param simulate
	 * @return
	 */
	public static ItemStack equipItem(@Nullable Player player, Entity target, ItemStack stack, int slot, boolean simulate) {
		if (slot >= 0 && stack.getItem() instanceof RadialMenuEquippable equippable) {
			EquipmentData data = target.getData(AttachmentRegistry.EQUIPMENT);
			if (equippable.canEquip(stack, player, target)) {
				EquipmentInventoryType type = equippable.getEquipmentCategory(stack);

				Container inv = data.getContainer(target, type);
				if (slot < inv.getContainerSize()) {
					IItemHandler wrapper = new InvWrapper(inv);

					stack = stack.copy();

					if (target instanceof ServerPlayer serverPlayer) {
						AdvancementCriteriaRegistry.EQUIP.get().trigger(serverPlayer, stack);
					}

					ItemStack result = wrapper.insertItem(slot, stack, simulate);

					if (simulate) {
						return result;
					}

					if (result.isEmpty() || result.getCount() != stack.getCount()) {
						target.getPersistentData().putBoolean(NBT_HAS_NO_EQUIPMENT, false);

						equippable.onEquip(stack, target, inv);

						return ItemHandlerHelper.insertItem(wrapper, stack, false);
					}
				}
			}
		}

		return stack;
	}

	/**
	 * Tries to unequip the first item found
	 *
	 * @param player
	 * @param target
	 * @return
	 */
	public static ItemStack unequipItem(@Nullable Player player, Entity target, boolean simulate) {
		EquipmentData data = target.getData(AttachmentRegistry.EQUIPMENT);
		for (EquipmentInventoryType type : EquipmentInventoryType.values()) {
			Container inv = data.getContainer(player, type);

			for (int i = 0; i < inv.getContainerSize(); i++) {
				ItemStack stack = inv.getItem(i);

				if (!stack.isEmpty() && stack.getItem() instanceof RadialMenuEquippable equippable &&
					!equippable.canUnequip(stack, player, target, data.getContainer(player, equippable.getEquipmentCategory(stack)))) {
					continue;
				}

				if (simulate) {
					return stack;
				}

				if (!stack.isEmpty()) {
					if (stack.getItem() instanceof RadialMenuEquippable equippable) {
						equippable.onUnequip(stack, target, inv);
					}

					inv.setItem(i, ItemStack.EMPTY);
					return stack;
				}
			}
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Uneqips the item in the specified inventory and slot
	 *
	 * @param target
	 * @param type
	 * @param slot
	 * @return
	 */
	public static ItemStack unequipItem(@Nullable Player player, Entity target, EquipmentInventoryType type, int slot, boolean simulate) {
		EquipmentData data = target.getData(AttachmentRegistry.EQUIPMENT);
		Container inv = data.getContainer(target, type);

		if (slot >= 0 && slot < inv.getContainerSize()) {
			ItemStack stack = inv.getItem(slot);

			if (!stack.isEmpty() && stack.getItem() instanceof RadialMenuEquippable equippable &&
				!equippable.canUnequip(stack, player, target, data.getContainer(target, equippable.getEquipmentCategory(stack)))) {
				return stack;
			}

			if (simulate) {
				return stack;
			}

			if (!stack.isEmpty() && stack.getItem() instanceof RadialMenuEquippable equippable) {
				equippable.onUnequip(stack, target, inv);
			}

			inv.setItem(slot, ItemStack.EMPTY);

			return stack;
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Tries to unequip the first found item from the specified target
	 *
	 * @param player
	 * @param target
	 * @return True if successful
	 */
	public static boolean tryPlayerUnequip(Player player, Entity target) {
		ItemStack unequipped = unequipItem(player, target, false);
		if (!unequipped.isEmpty()) {
			if (!player.getInventory().add(unequipped)) {
				target.spawnAtLocation(unequipped, target.getEyeHeight());
			}

			return true;
		}
		return false;
	}
}
