package thebetweenlands.api.event;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.entity.EntityEvent;
import thebetweenlands.common.component.entity.equipment.EquipmentData;

public class EquipmentChangedEvent extends EntityEvent {

	private final EquipmentData data;

	public EquipmentChangedEvent(Entity entity, EquipmentData data) {
		super(entity);
		this.data = data;
	}

	public EquipmentData getEquipmentData() {
		return this.data;
	}
}
