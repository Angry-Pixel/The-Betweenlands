package thebetweenlands.common.event.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.capability.equipment.IEquipmentCapability;
import thebetweenlands.common.item.equipment.IEquippable;
import thebetweenlands.common.registries.CapabilityRegistry;

public class ItemEquipmentHandler {
	private ItemEquipmentHandler() { }

	@SubscribeEvent
	public static void onWorldTick(TickEvent.WorldTickEvent event) {
		if(event.phase == Phase.END) {
			tickEquipment(event.world);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event) {
		if(event.phase == Phase.END) {
			World world = TheBetweenlands.proxy.getClientWorld();
			if(world != null && !Minecraft.getMinecraft().isGamePaused()) {
				tickEquipment(world);
			}
		}
	}

	private static void tickEquipment(World world) {
		for(int i = 0; i < world.loadedEntityList.size(); i++) {
			Entity entity = (Entity) world.loadedEntityList.get(i);
			if(entity.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
				IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
				for(EnumEquipmentInventory invType : EnumEquipmentInventory.values()) {
					IInventory inventory = cap.getInventory(invType);
					int items = inventory.getSizeInventory();
					for(int c = 0; c < items; c++) {
						ItemStack item = inventory.getStackInSlot(c);
						if(item != null && item.getItem() instanceof IEquippable) {
							((IEquippable)item.getItem()).onEquipmentTick(item, entity);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onEntityInteract(EntityInteract event) {
		EntityPlayer player = event.getEntityPlayer();
		Entity target = event.getTarget();

		if(player != null && target != null && target instanceof EntityPlayer == false) {
			ItemStack heldItem = event.getItemStack();

			if(!player.isSneaking() && heldItem != null) {
				if(heldItem.getItem() instanceof IEquippable) {
					if(target.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
						IEquipmentCapability cap = target.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);

						IEquippable equippable = (IEquippable) heldItem.getItem();

						if(equippable.canEquipOnRightClick(heldItem, player, target, cap.getInventory(equippable.getEquipmentCategory(heldItem)))) {
							if(tryPlayerEquip(player, event.getHand(), target, heldItem)) {
								event.setCanceled(true);
							}

							if(heldItem.stackSize <= 0) {
								player.setHeldItem(event.getHand(), null);
							}
						}
					}
				}
			} else if(player.isSneaking() && heldItem == null) {
				if(tryPlayerUnequip(player, target)) {
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onItemUse(PlayerInteractEvent event) {
		if(event instanceof PlayerInteractEvent.RightClickBlock || event instanceof PlayerInteractEvent.RightClickEmpty) {
			EntityPlayer player = event.getEntityPlayer();
			ItemStack heldItem = event.getItemStack();

			if(player != null && heldItem != null && heldItem.getItem() instanceof IEquippable) {
				if(player.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
					IEquipmentCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
					IEquippable equippable = (IEquippable) heldItem.getItem();

					if(equippable.canEquipOnRightClick(heldItem, player, player, cap.getInventory(equippable.getEquipmentCategory(heldItem)))) {
						tryPlayerEquip(player, event.getHand(), player, heldItem);
					}
				}
			}
		}
	}

	public static boolean tryPlayerEquip(EntityPlayer player, EnumHand hand, Entity target, ItemStack stack) {
		ItemStack equipped = IEquipmentCapability.equipItem(player, target, stack);
		if(equipped == null || equipped.stackSize != stack.stackSize) {
			if(!player.capabilities.isCreativeMode) {
				player.setHeldItem(hand, equipped);
			}

			player.swingArm(hand);

			return true;
		}

		return false;
	}

	public static boolean tryPlayerUnequip(EntityPlayer player, Entity target) {
		ItemStack unequipped = IEquipmentCapability.unequipItem(player, target);
		if(unequipped != null) {
			if(!player.inventory.addItemStackToInventory(unequipped)) {
				target.entityDropItem(unequipped, target.getEyeHeight());
			}

			player.swingArm(EnumHand.MAIN_HAND);

			return true;
		}
		return false;
	}

	@SubscribeEvent
	public static void onDeathDrops(LivingDropsEvent event) {
		EntityLivingBase entity = event.getEntityLiving();

		if(entity != null && !entity.worldObj.isRemote && !entity.worldObj.getGameRules().getBoolean("keepInventory")) {
			if(entity.hasCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null)) {
				IEquipmentCapability cap = entity.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);

				for(EnumEquipmentInventory type : EnumEquipmentInventory.values()) {
					IInventory inv = cap.getInventory(type);

					for(int i = 0; i < inv.getSizeInventory(); i++) {
						ItemStack stack = inv.getStackInSlot(i);

						if(stack != null) {
							if(stack.getItem() instanceof IEquippable && !((IEquippable) stack.getItem()).canDrop(stack, entity, inv)) {
								continue;
							}

							EntityItem equipmentDrop = new EntityItem(entity.worldObj, entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ, stack.copy());
							equipmentDrop.setDefaultPickupDelay();
							event.getDrops().add(equipmentDrop);
						}
					}
				}
			}
		}
	}

	/*@SubscribePacket
	public static void onPacketEquipment(PacketEquipment packet) {
		if(packet.getContext().getServerHandler() != null) {
			EntityPlayer sender = packet.getContext().getServerHandler().playerEntity;
			if(sender != null) {
				EquipmentInventory equipmentInventory = EquipmentInventory.getEquipmentInventory(sender);
				switch(packet.getMode()) {
				default:
				case 0:
					if(packet.getSlot() < sender.inventory.getSizeInventory()) {
						ItemStack item = sender.inventory.getStackInSlot(packet.getSlot());
						if(item != null) {
							if(item.getItem() instanceof IEquippable) {
								IEquippable equippable = (IEquippable) item.getItem();
								if(equippable.canEquip(item, sender, sender, equipmentInventory)) {
									tryPlayerEquip(sender, sender, item);
									if(item.stackSize <= 0)
										sender.inventory.setInventorySlotContents(packet.getSlot(), null);
								}
							}
						}
					}
					break;
				case 1:
					if(packet.getSlot() < equipmentInventory.getEquipment().size()) {
						Equipment equipment = equipmentInventory.getEquipment().get(packet.getSlot());
						if(equipment != null && equipment.equippable.canUnequip(equipment.item, sender, sender, equipmentInventory)) {
							EquipmentInventory.unequipItem(sender, equipment);
							if(!sender.inventory.addItemStackToInventory(equipment.item))
								sender.entityDropItem(equipment.item, sender.getEyeHeight());
						}
					}
					break;
				}
			}
		}
	}*/
}
