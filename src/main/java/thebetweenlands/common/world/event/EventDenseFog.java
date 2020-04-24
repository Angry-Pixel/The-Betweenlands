package thebetweenlands.common.world.event;

import java.util.Random;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.world.WorldProviderBetweenlands;

public class EventDenseFog extends TimedEnvironmentEvent {
	public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "dense_fog");
	
	private float fade = 1.0F;
	private float lastFade = 1.0F;

	public EventDenseFog(BLEnvironmentEventRegistry registry) {
		super(registry);
	}

	@Override
	public ResourceLocation getEventName() {
		return ID;
	}

	@Override
	public int getOffTime(Random rnd) {
		return rnd.nextInt(10000) + 30000;
	}

	@Override
	public int getOnTime(Random rnd) {
		return rnd.nextInt(5000) + 5000;
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
	
	public static boolean isDenseFog(World world) {
		if(world != null) {
			WorldProviderBetweenlands provider = WorldProviderBetweenlands.getProvider(world);
			if(provider != null) {
				return provider.getEnvironmentEventRegistry().denseFog.isActive();
			}
		}
		return false;
	}
}
