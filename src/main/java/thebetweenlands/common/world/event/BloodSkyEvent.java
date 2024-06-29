package thebetweenlands.common.world.event;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import thebetweenlands.client.ClientEvents;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.SoundRegistry;

public class BloodSkyEvent extends TimedEnvironmentEvent {
	public static final ResourceLocation ID = TheBetweenlands.prefix("blood_sky");

	protected static final ResourceLocation[] VISION_TEXTURES = new ResourceLocation[] { TheBetweenlands.prefix("textures/events/blood_sky.png") };

	private boolean soundPlayed = true;
	private float skyTransparency = 0.0F;
	private float lastSkyTransparency = 0.0F;

	public BloodSkyEvent(BLEnvironmentEventRegistry registry) {
		super(registry);
	}

	public void setSkyTransparency(float transparency) {
		this.lastSkyTransparency = this.skyTransparency;
		this.skyTransparency = transparency;
	}

	public float getSkyTransparency(float partialTicks) {
		return this.skyTransparency + (this.skyTransparency - this.lastSkyTransparency) * partialTicks;
	}

	@Override
	public ResourceLocation getEventName() {
		return ID;
	}

	@Override
	public int getOffTime(RandomSource rnd) {
		return rnd.nextInt(400000) + 500000;
	}

	@Override
	public int getOnTime(RandomSource rnd) {
		return rnd.nextInt(8000) + 20000;
	}

	@Override
	public void tick(Level level) {
		super.tick(level);

		if(level.isClientSide()) {
			if(this.isActive()) {
				if(this.skyTransparency < 1.0F) {
					this.setSkyTransparency(this.skyTransparency + 0.003F);
				}
				if(this.skyTransparency > 1.0F) {
					this.setSkyTransparency(1.0F);
				}
			} else {
				if(this.skyTransparency > 0.0F) {
					this.setSkyTransparency(this.skyTransparency - 0.003F);
				}
				if(this.skyTransparency < 0.0F) {
					this.setSkyTransparency(0.0F);
				}
			}
		}
	}

	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		if(active) {
			ClientLevel level = ClientEvents.getClientLevel();
			if(level != null && level.isClientSide() && !this.soundPlayed) {
				level.playSound(null, ClientEvents.getClientPlayer().blockPosition(), SoundRegistry.AMBIENT_BLOOD_SKY_ROAR.get(), SoundSource.AMBIENT, 100.0F, 1.0F);
			}
			this.soundPlayed = true;
		} else {
			this.soundPlayed = false;
		}
	}

	@Override
	public ResourceLocation[] getVisionTextures() {
		return VISION_TEXTURES;
	}

	@Override
	public SoundEvent getChimesSound() {
		return SoundRegistry.CHIMES_BLOOD_SKY.get();
	}
}
