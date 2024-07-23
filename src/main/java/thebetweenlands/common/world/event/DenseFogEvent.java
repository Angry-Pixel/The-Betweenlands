package thebetweenlands.common.world.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.SoundRegistry;

public class DenseFogEvent extends TimedEnvironmentEvent {

	protected static final ResourceLocation[] VISION_TEXTURES = new ResourceLocation[]{TheBetweenlands.prefix("textures/events/dense_fog.png")};

	private float fade = 1.0F;
	private float lastFade = 1.0F;

	@Override
	public int getOffTime(RandomSource rnd) {
		return rnd.nextInt(10000) + 30000;
	}

	@Override
	public int getOnTime(RandomSource rnd) {
		return rnd.nextInt(5000) + 5000;
	}

	@Override
	public void tick(Level level) {
		super.tick(level);
		if (level.isClientSide()) {
			if (this.isActive()) {
				if (this.fade > 0.0F) {
					this.lastFade = this.fade;
					this.fade -= 0.002F;
				} else {
					this.fade = 0.0F;
					this.lastFade = 0.0F;
				}
			} else {
				if (this.fade < 1.0F) {
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

	public static boolean isDenseFog(Level level) {
		if (level != null) {
//			WorldProviderBetweenlands provider = WorldProviderBetweenlands.getProvider(level);
//			if (provider != null) {
//				return provider.getEnvironmentEventRegistry().denseFog.isActive();
//			}
		}
		return false;
	}

	@Override
	public ResourceLocation[] getVisionTextures() {
		return VISION_TEXTURES;
	}

	@Override
	public SoundEvent getChimesSound() {
		return SoundRegistry.CHIMES_DENSE_FOG.get();
	}
}
