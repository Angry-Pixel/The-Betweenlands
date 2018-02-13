package thebetweenlands.api.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class ArmSwingSpeedEvent extends LivingEvent {
	private float speed;

	public ArmSwingSpeedEvent(EntityLivingBase living) {
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
