package thebetweenlands.common.handler;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import thebetweenlands.api.item.RadialMenuEquippable;
import thebetweenlands.common.component.entity.equipment.EquipmentData;
import thebetweenlands.common.component.entity.equipment.EquipmentHelper;
import thebetweenlands.common.component.entity.equipment.EquipmentInventoryType;
import thebetweenlands.common.inventory.EquipmentInventory;
import thebetweenlands.common.network.serverbound.EquipItemPacket;
import thebetweenlands.common.registries.AttachmentRegistry;

import javax.annotation.Nullable;

public class ItemEquipmentHandler {

	public static void init() {
		NeoForge.EVENT_BUS.addListener(ItemEquipmentHandler::onLivingUpdated);
		NeoForge.EVENT_BUS.addListener(ItemEquipmentHandler::onDeathDrops);
		NeoForge.EVENT_BUS.addListener(ItemEquipmentHandler::onEntityInteract);
		NeoForge.EVENT_BUS.addListener(ItemEquipmentHandler::onItemUseBlock);
		NeoForge.EVENT_BUS.addListener(ItemEquipmentHandler::onItemUseEmpty);
	}

	private static void onLivingUpdated(EntityTickEvent.Post event) {
		Entity entity = event.getEntity();

		if (!entity.getPersistentData().getBoolean(EquipmentHelper.NBT_HAS_NO_EQUIPMENT)) {
			EquipmentData data = entity.getData(AttachmentRegistry.EQUIPMENT);

			for (EquipmentInventoryType invType : EquipmentInventoryType.values()) {
				EquipmentInventory inventory = data.getContainer(entity, invType);
				inventory.tick();
			}

			if ((entity.tickCount + entity.getId()) % 100 == 0) {
				boolean hasEquipment = false;

				loop:
				for (EquipmentInventoryType invType : EquipmentInventoryType.values()) {
					Container inventory = data.getContainer(entity, invType);

					for (int i = 0; i < inventory.getContainerSize(); i++) {
						if (!inventory.getItem(i).isEmpty()) {
							hasEquipment = true;
							break loop;
						}
					}
				}

				//Put equipment ticking back to sleep until items are added again
				if (!hasEquipment) {
					entity.getPersistentData().putBoolean(EquipmentHelper.NBT_HAS_NO_EQUIPMENT, true);
				}
			}
		}
	}

	private static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		Player player = event.getEntity();
		Entity target = event.getTarget();

		if (!(target instanceof Player)) {
			ItemStack heldItem = event.getItemStack();

			if (!player.isSecondaryUseActive() && !heldItem.isEmpty()) {
				if (heldItem.getItem() instanceof RadialMenuEquippable equippable) {

					if (equippable.canEquipOnRightClick(heldItem, player, target)) {
						ItemStack result = EquipmentHelper.equipItem(player, target, heldItem, false);

						if (result.isEmpty() || result.getCount() != heldItem.getCount()) {
							if (!player.isCreative()) {
								player.setItemInHand(event.getHand(), result);
							}

							event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide()));
						}
					}
				}
			} else if (player.isSecondaryUseActive() && heldItem.isEmpty()) {
				if (EquipmentHelper.tryPlayerUnequip(player, target)) {
					event.setCanceled(true);
					event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide()));
				}
			}
		}
	}

	private static void onItemUseBlock(PlayerInteractEvent.RightClickBlock event) {
		InteractionResult result = tryEquip(event.getEntity(), event.getHand(), true);
		if (result != null) {
			event.setCanceled(true);
			event.setCancellationResult(result);
		}
	}

	private static void onItemUseEmpty(PlayerInteractEvent.RightClickEmpty event) {
		if (tryEquip(event.getEntity(), InteractionHand.MAIN_HAND, true) == null) {
			tryEquip(event.getEntity(), InteractionHand.OFF_HAND, true);
		}
	}

	@Nullable
	private static InteractionResult tryEquip(Player player, InteractionHand hand, boolean packet) {
		ItemStack heldItem = player.getItemInHand(hand);

		if (!heldItem.isEmpty() && heldItem.getItem() instanceof RadialMenuEquippable equippable) {
			if (equippable.canEquipOnRightClick(heldItem, player, player)) {
				if (packet) {
					if (player.level().isClientSide()) {
						ItemStack result = EquipmentHelper.equipItem(player, player, heldItem, true);

						if (result.isEmpty() || result.getCount() != heldItem.getCount()) {
							if (hand == InteractionHand.OFF_HAND) {
								PacketDistributor.sendToServer(new EquipItemPacket(player.getId(), -1, EquipItemPacket.EQUIP_MODE, equippable.getEquipmentCategory(heldItem)));
								return InteractionResult.SUCCESS;
							} else {
								int slot = player.getInventory().findSlotMatchingItem(heldItem);
								if (slot >= 0) {
									PacketDistributor.sendToServer(new EquipItemPacket(player.getId(), slot, EquipItemPacket.EQUIP_MODE, equippable.getEquipmentCategory(heldItem)));
									return InteractionResult.SUCCESS;
								}
							}
						}
					}
				} else {
					if (player.level().isClientSide()) {
						ItemStack result = EquipmentHelper.equipItem(player, player, heldItem, true);

						if (result.isEmpty() || result.getCount() != heldItem.getCount()) {
							return InteractionResult.SUCCESS;
						}
					} else {
						ItemStack result = EquipmentHelper.equipItem(player, player, heldItem, false);

						if (result.isEmpty() || result.getCount() != heldItem.getCount()) {
							if (!player.isCreative()) {
								player.setItemInHand(hand, result);
							}

							player.displayClientMessage(Component.translatable("equipment.thebetweenlands.equipped", heldItem.getDisplayName()), true);

							return InteractionResult.CONSUME;
						}
					}
				}
			}
		}

		return null;
	}

	private static void onDeathDrops(LivingDropsEvent event) {
		LivingEntity entity = event.getEntity();

		if (!event.isCanceled() && !entity.level().isClientSide() && !entity.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
			EquipmentData data = entity.getData(AttachmentRegistry.EQUIPMENT);
			for (EquipmentInventoryType type : EquipmentInventoryType.values()) {
				Container inv = data.getContainer(entity, type);

				for (int i = 0; i < inv.getContainerSize(); i++) {
					ItemStack stack = inv.getItem(i);

					if (!stack.isEmpty()) {
						if (stack.getItem() instanceof RadialMenuEquippable equippable) {
							equippable.onUnequip(stack, entity, inv);
							if (!equippable.canDrop(stack, entity, inv)) {
								continue;
							}
						}

						ItemEntity equipmentDrop = new ItemEntity(entity.level(), entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(), stack.copy());
						equipmentDrop.setDefaultPickUpDelay();
						event.getDrops().add(equipmentDrop);
					}
				}

				inv.clearContent();
			}
		}
	}
}
