package thebetweenlands.common.handler;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.item.IEquippable;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.EquipmentHelper;
import thebetweenlands.common.network.serverbound.MessageEquipItem;
import thebetweenlands.common.registries.CapabilityRegistry;

public class ItemEquipmentHandler {
	private ItemEquipmentHandler() { }

	@SubscribeEvent
	public static void onLivingUpdated(LivingEvent.LivingUpdateEvent event) {
		Entity entity = event.getEntity();
		
		if(!entity.getEntityData().getBoolean(EquipmentHelper.NBT_HAS_NO_EQUIPMENT)) {
			IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
			
			if (cap != null) {
				for (EnumEquipmentInventory invType : EnumEquipmentInventory.VALUES) {
					IInventory inventory = cap.getInventory(invType);

					if (inventory instanceof ITickable) {
						((ITickable) inventory).update();
					}
				}
				
				if((entity.ticksExisted + entity.getEntityId()) % 100 == 0) {
					boolean hasEquipment = false;
					
					loop: for(EnumEquipmentInventory invType : EnumEquipmentInventory.VALUES) {
						IInventory inventory = cap.getInventory(invType);
						
						for(int i = 0; i < inventory.getSizeInventory(); i++) {
							if(!inventory.getStackInSlot(i).isEmpty()) {
								hasEquipment = true;
								break loop;
							}
						}
					}
					
					//Put equipment ticking back to sleep until items are added again
					if(!hasEquipment) {
						entity.getEntityData().setBoolean(EquipmentHelper.NBT_HAS_NO_EQUIPMENT, true);
					}
				}
			} else {
				entity.getEntityData().setBoolean(EquipmentHelper.NBT_HAS_NO_EQUIPMENT, true);
			}
		}
	}

	@SubscribeEvent
	public static void onEntityInteract(EntityInteract event) {
		EntityPlayer player = event.getEntityPlayer();
		Entity target = event.getTarget();

		if(player != null && target != null && target instanceof EntityPlayer == false) {
			ItemStack heldItem = event.getItemStack();

			if(!player.isSneaking() && !heldItem.isEmpty()) {
				if(heldItem.getItem() instanceof IEquippable) {
					IEquippable equippable = (IEquippable) heldItem.getItem();

					if(equippable.canEquipOnRightClick(heldItem, player, target)) {
						ItemStack result = EquipmentHelper.equipItem(player, target, heldItem, false);

						if(result.isEmpty() || result.getCount() != heldItem.getCount()) {
							if(!player.capabilities.isCreativeMode) {
								player.setHeldItem(event.getHand(), result);
							}

							player.swingArm(event.getHand());
						}
					}
				}
			} else if(player.isSneaking() && heldItem.isEmpty()) {
				if(EquipmentHelper.tryPlayerUnequip(player, target)) {
					player.swingArm(EnumHand.MAIN_HAND);
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onItemUse(PlayerInteractEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if(event instanceof PlayerInteractEvent.RightClickBlock) {
			tryEquip(player, event.getHand(), false);
		} else if(event instanceof PlayerInteractEvent.RightClickEmpty) {
			if(!tryEquip(player, EnumHand.MAIN_HAND, true)) {
				tryEquip(player, EnumHand.OFF_HAND, true);
			}
		}
	}

	private static boolean tryEquip(EntityPlayer player, EnumHand hand, boolean packet) {
		ItemStack heldItem = player.getHeldItem(hand);

		if(player != null && !heldItem.isEmpty() && heldItem.getItem() instanceof IEquippable) {
			IEquippable equippable = (IEquippable) heldItem.getItem();

			if(equippable.canEquipOnRightClick(heldItem, player, player)) {
				if(packet) {
					if(player.world.isRemote) {
						ItemStack result = EquipmentHelper.equipItem(player, player, heldItem, true);

						if(result.isEmpty() || result.getCount() != heldItem.getCount()) {
							if(hand == EnumHand.OFF_HAND) {
								TheBetweenlands.networkWrapper.sendToServer(new MessageEquipItem(-1, player));

								player.swingArm(hand);

								return true;
							} else {
								int slot = player.inventory.getSlotFor(heldItem);
								if(slot >= 0) {
									TheBetweenlands.networkWrapper.sendToServer(new MessageEquipItem(slot, player));

									player.swingArm(hand);

									return true;
								}
							}
						}
					}
				} else {
					if(player.world.isRemote) {
						ItemStack result = EquipmentHelper.equipItem(player, player, heldItem, true);

						if(result.isEmpty() || result.getCount() != heldItem.getCount()) {
							player.swingArm(hand);
							return true;
						}
					} else {
						ItemStack result = EquipmentHelper.equipItem(player, player, heldItem, false);

						if(result.isEmpty() || result.getCount() != heldItem.getCount()) {
							if(!player.capabilities.isCreativeMode) {
								player.setHeldItem(hand, result);
							}

							player.swingArm(hand);

							player.sendStatusMessage(new TextComponentTranslation("chat.equipment.equipped", new TextComponentTranslation(heldItem.getTranslationKey() + ".name")), true);

							return true;
						}
					}
				}
			}
		}

		return false;
	}

	@SubscribeEvent
	public static void onDeathDrops(LivingDropsEvent event) {
		EntityLivingBase entity = event.getEntityLiving();

		if(entity != null && !entity.world.isRemote && !entity.world.getGameRules().getBoolean("keepInventory")) {
			IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
			if(cap != null) {
				for(EnumEquipmentInventory type : EnumEquipmentInventory.VALUES) {
					IInventory inv = cap.getInventory(type);

					for(int i = 0; i < inv.getSizeInventory(); i++) {
						ItemStack stack = inv.getStackInSlot(i);

						if(!stack.isEmpty()) {
							if(stack.getItem() instanceof IEquippable) {
								IEquippable equippable = (IEquippable) stack.getItem();
								equippable.onUnequip(stack, entity, inv);
								if(!equippable.canDrop(stack, entity, inv)) {
									continue;
								}
							}

							EntityItem equipmentDrop = new EntityItem(entity.world, entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ, stack.copy());
							equipmentDrop.setDefaultPickupDelay();
							event.getDrops().add(equipmentDrop);
						}
					}
				}
			}
		}
	}
}
