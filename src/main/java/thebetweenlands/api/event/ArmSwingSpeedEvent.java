package thebetweenlands.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class ArmSwingSpeedEvent extends LivingEvent implements ICancellableEvent {

	private float speed;

	public ArmSwingSpeedEvent(LivingEntity living) {
		super(living);
		this.speed = 1.0F;
	}

	public float getSpeed() {
		return this.speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
}
