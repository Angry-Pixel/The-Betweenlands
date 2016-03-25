package thebetweenlands.event.item;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.equipment.EntityPropertiesEquipment;
import thebetweenlands.entities.properties.list.equipment.Equipment;
import thebetweenlands.entities.properties.list.equipment.EquipmentInventory;
import thebetweenlands.items.IEquippable;
import thebetweenlands.network.base.SubscribePacket;
import thebetweenlands.network.packet.client.PacketEquipment;

public class ItemEquipmentHandler {
	public static final ItemEquipmentHandler INSTANCE = new ItemEquipmentHandler();

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		if(event.phase == Phase.END) {
			this.tickEquipment(event.world);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if(event.phase == Phase.END) {
			World world = TheBetweenlands.proxy.getClientWorld();
			if(world != null && !Minecraft.getMinecraft().isGamePaused()) {
				this.tickEquipment(world);
			}
		}
	}

	private void tickEquipment(World world) {
		for(int i = 0; i < world.loadedEntityList.size(); i++) {
			Entity entity = (Entity) world.loadedEntityList.get(i);
			EntityPropertiesEquipment property = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesEquipment.class);
			if(property != null) {
				EquipmentInventory equipmentInventory = property.getEquipmentInventory();
				for(Equipment equipment : equipmentInventory.getEquipment()) {
					if(equipment.item != null && equipment.item.getItem() instanceof IEquippable) {
						((IEquippable)equipment.item.getItem()).onEquipmentTick(equipment.item, entity);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityInteract(EntityInteractEvent event) {
		if(event.entityPlayer != null && event.target != null && event.target instanceof EntityPlayer == false) {
			if(!event.entityPlayer.isSneaking() && event.entityPlayer.getHeldItem() != null) {
				if(event.entityPlayer.getHeldItem().getItem() instanceof IEquippable) {
					EntityPropertiesEquipment property = BLEntityPropertiesRegistry.HANDLER.getProperties(event.target, EntityPropertiesEquipment.class);
					if(property != null) {
						if(((IEquippable)event.entityPlayer.getHeldItem().getItem()).canEquipOnRightClick(event.entityPlayer.getHeldItem(), event.entityPlayer, event.target, property.getEquipmentInventory())) {
							tryPlayerEquip(event.entityPlayer, event.target, event.entityPlayer.getHeldItem());
							if(event.entityPlayer.getHeldItem().stackSize <= 0)
								event.entityPlayer.setCurrentItemOrArmor(0, null);
						}
					}
				}
			} else if(event.entityPlayer.isSneaking() && event.entityPlayer.getHeldItem() == null) {
				tryPlayerUnequip(event.entityPlayer, event.target);
			}
		}
	}

	@SubscribeEvent
	public void onItemUse(PlayerInteractEvent event) {
		if((event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) && event.entityPlayer != null && event.entityPlayer.getHeldItem() != null) {
			if(event.entityPlayer.getHeldItem().getItem() instanceof IEquippable) {
				EntityPropertiesEquipment property = BLEntityPropertiesRegistry.HANDLER.getProperties(event.entityPlayer, EntityPropertiesEquipment.class);
				if(property != null) {
					if(((IEquippable)event.entityPlayer.getHeldItem().getItem()).canEquipOnRightClick(event.entityPlayer.getHeldItem(), event.entityPlayer, event.entityPlayer, property.getEquipmentInventory())) {
						tryPlayerEquip(event.entityPlayer, event.entityPlayer, event.entityPlayer.getHeldItem());
						if(event.entityPlayer.getHeldItem().stackSize <= 0)
							event.entityPlayer.setCurrentItemOrArmor(0, null);
					}
				}
			}
		}
	}

	public static boolean tryPlayerEquip(EntityPlayer player, Entity target, ItemStack stack) {
		if(EquipmentInventory.equipItem(player, target, stack) != null) {
			if(!player.capabilities.isCreativeMode)
				stack.stackSize--;
			player.swingItem();
			return true;
		}
		return false;
	}

	public static boolean tryPlayerUnequip(EntityPlayer player, Entity target) {
		ItemStack unequipped = EquipmentInventory.unequipItem(player, target);
		if(unequipped != null) {
			if(!player.inventory.addItemStackToInventory(unequipped))
				target.entityDropItem(unequipped, target.getEyeHeight());
			player.swingItem();
			return true;
		}
		return false;
	}

	@SubscribeEvent
	public void onDeath(LivingDeathEvent event) {
		EntityLivingBase entity = event.entityLiving;
		if(entity != null && !entity.worldObj.isRemote) {
			EquipmentInventory equipmentInventory = EquipmentInventory.getEquipmentInventory(event.entity);
			for(Equipment equipment : equipmentInventory.getEquipment()) {
				if(((IEquippable)equipment.item.getItem()).canDrop(equipment.item, entity, equipmentInventory)) {
					entity.entityDropItem(equipment.item.copy(), entity.getEyeHeight());
				}
			}
		}
	}

	@SubscribePacket
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
	}
}
