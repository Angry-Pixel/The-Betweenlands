package thebetweenlands.api.event;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import thebetweenlands.api.capability.IEquipmentCapability;

public class EquipmentChangedEvent extends EntityEvent {
	private final IEquipmentCapability cap;

	public EquipmentChangedEvent(Entity entity, IEquipmentCapability cap) {
		super(entity);
		this.cap = cap;
	}

	public IEquipmentCapability getCapability() {
		return this.cap;
	}
}
