package thebetweenlands.forgeevent.entity;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import thebetweenlands.entities.properties.list.equipment.EquipmentInventory;

public class EquipmentChangeEvent extends EntityEvent {
	public final EquipmentInventory inventory;

	public EquipmentChangeEvent(Entity entity) {
		super(entity);
		this.inventory = EquipmentInventory.getEquipmentInventory(entity);
	}
}
