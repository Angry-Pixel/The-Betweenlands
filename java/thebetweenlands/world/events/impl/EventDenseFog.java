package thebetweenlands.world.events.impl;

import java.util.Random;

import net.minecraft.world.World;
import thebetweenlands.world.events.EnvironmentEventRegistry;
import thebetweenlands.world.events.TimedEnvironmentEvent;

public class EventDenseFog extends TimedEnvironmentEvent {
	private float fade = 1.0F;
	private float lastFade = 1.0F;

	public EventDenseFog(EnvironmentEventRegistry registry) {
		super(registry);
	}

	@Override
	public String getEventName() {
		return "denseFog";
	}

	@Override
	public int getOffTime(Random rnd) {
		return rnd.nextInt(40000) + 10000;
	}

	@Override
	public int getOnTime(Random rnd) {
		return rnd.nextInt(20000) + 6000;
	}

	@Override
	public void update(World world) {
		super.update(world);
		if(world.isRemote) {
			if(this.isActive()) {
				if(this.fade > 0.0F) {
					this.lastFade = this.fade;
					this.fade -= 0.002F;
				} else {
					this.fade = 0.0F;
					this.lastFade = 0.0F;
				}
			} else {
				if(this.fade < 1.0F) {
					this.lastFade = this.fade;
					this.fade += 0.002F;
				} else {
					this.fade = 1.0F;
					this.lastFade = 1.0F;
				}
			}
		}
	}
	
	public float getFade(float partialTicks) {
		return this.fade + (this.fade - this.lastFade) * partialTicks;
	}
}
