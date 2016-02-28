package thebetweenlands.entities.properties.list.equipment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.items.IEquippable;

public class EquipmentInventory {
	private final Entity entity;
	private final List<Equipment> equippedItems = new ArrayList<Equipment>();

	public EquipmentInventory(Entity entity) {
		this.entity = entity;
	}

	public List<Equipment> getEquipment() {
		return Collections.unmodifiableList(this.equippedItems);
	}

	public List<Equipment> getEquipment(EnumEquipmentCategory category) {
		List<Equipment> equippedItems = new ArrayList<Equipment>();
		for(Equipment equipment : this.equippedItems) {
			if(equipment.category == category)
				equippedItems.add(equipment);
		}
		return Collections.unmodifiableList(equippedItems);
	}

	public void saveNBTData(NBTTagCompound nbt) {
		NBTTagList itemNBTList = new NBTTagList();
		for(Equipment item : this.equippedItems) {
			NBTTagCompound itemCompound = new NBTTagCompound();
			item.writeToNBT(itemCompound);
			itemNBTList.appendTag(itemCompound);
		}
		nbt.setTag("equipment", itemNBTList);
	}

	public void loadNBTData(NBTTagCompound nbt) {
		this.equippedItems.clear();
		NBTTagList itemNBTList = nbt.getTagList("equipment", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < itemNBTList.tagCount(); i++) {
			NBTTagCompound itemCompound = itemNBTList.getCompoundTagAt(i);
			Equipment equipment = Equipment.readFromNBT(itemCompound);
			if(equipment != null) {
				this.equippedItems.add(equipment);
			}
		}
	}

	/**
	 * Adds equipment to the inventory
	 * @param equipment
	 */
	public void addEquipment(Equipment equipment) {
		((IEquippable)equipment.item.getItem()).onEquip(equipment.item, this.entity, this);
		this.equippedItems.add(equipment);
	}

	/**
	 * Removes equipment from the inventory
	 * @param equipment
	 */
	public void removeEquipment(Equipment equipment) {
		((IEquippable)equipment.item.getItem()).onUnequip(equipment.item, this.entity, this);
		this.equippedItems.remove(equipment);
	}

	/**
	 * Returns the owner of this equipment inventory
	 * @return
	 */
	public Entity getEntity() {
		return this.entity;
	}

	/**
	 * Gets the equipment inventory of the specified entity
	 * @param entity
	 * @return
	 */
	public static EquipmentInventory getEquipmentInventory(Entity entity) {
		EntityPropertiesEquipment property = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesEquipment.class);
		if(property != null) {
			return property.getEquipmentInventory();
		}
		return new EquipmentInventory(entity);
	}

	/**
	 * Tries to equip the specified item
	 * @param player
	 * @param target
	 * @param stack
	 * @return
	 */
	public static Equipment equipItem(EntityPlayer player, Entity target, ItemStack stack) {
		if(stack.getItem() instanceof IEquippable) {
			EntityPropertiesEquipment property = BLEntityPropertiesRegistry.HANDLER.getProperties(target, EntityPropertiesEquipment.class);
			if(property != null) {
				IEquippable equippable = (IEquippable) stack.getItem();
				if(equippable.canEquip(stack, player, target, property.getEquipmentInventory())) {
					ItemStack copy = stack.copy();
					copy.stackSize = 1;
					Equipment equipment = new Equipment(copy, equippable.getEquipmentCategory(stack));
					property.getEquipmentInventory().addEquipment(equipment);
					return equipment;
				}
			}
		}
		return null;
	}

	/**
	 * Tries to unequip the last item that can be uneqipped in the equipment inventory
	 * @param player
	 * @param target
	 * @return
	 */
	public static ItemStack unequipItem(EntityPlayer player, Entity target) {
		EntityPropertiesEquipment property = BLEntityPropertiesRegistry.HANDLER.getProperties(target, EntityPropertiesEquipment.class);
		if(property != null) {
			EquipmentInventory inventory = property.getEquipmentInventory();
			for(int i = inventory.getEquipment().size() - 1; i >= 0; i--) {
				Equipment equipment = inventory.getEquipment().get(i);
				IEquippable equippable = (IEquippable) equipment.item.getItem();
				if(equippable.canUnequip(equipment.item, player, target, inventory)) {
					if(!target.worldObj.isRemote) {
						inventory.removeEquipment(equipment);
					}
					return equipment.item;
				}
			}
		}
		return null;
	}

	/**
	 * Uneqips the specified item
	 * @param target
	 * @param equipment
	 * @return
	 */
	public static void unequipItem(Entity target, Equipment equipment) {
		EntityPropertiesEquipment property = BLEntityPropertiesRegistry.HANDLER.getProperties(target, EntityPropertiesEquipment.class);
		if(property != null) {
			EquipmentInventory inventory = property.getEquipmentInventory();
			IEquippable equippable = (IEquippable) equipment.item.getItem();
			if(!target.worldObj.isRemote) {
				inventory.removeEquipment(equipment);
			}
		}
	}
}