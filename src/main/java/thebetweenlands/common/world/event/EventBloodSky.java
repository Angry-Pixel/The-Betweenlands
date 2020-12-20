package thebetweenlands.common.world.event;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.SoundRegistry;

public class EventBloodSky extends TimedEnvironmentEvent {
	public static final ResourceLocation ID = new ResourceLocation(ModInfo.ID, "blood_sky");
	private boolean soundPlayed = true;
	private float skyTransparency = 0.0F;
	private float lastSkyTransparency = 0.0F;

	public EventBloodSky(BLEnvironmentEventRegistry registry) {
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
	public int getOffTime(Random rnd) {
		return rnd.nextInt(400000) + 500000;
	}

	@Override
	public int getOnTime(Random rnd) {
		return rnd.nextInt(8000) + 20000;
	}

	@Override
	public void update(World world) {
		super.update(world);
		
		if(world.isRemote) {
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
			World world = TheBetweenlands.proxy.getClientWorld();
			if(world != null && world.isRemote && !this.soundPlayed) {
				world.playSound(Minecraft.getMinecraft().player.posX, Minecraft.getMinecraft().player.posY, Minecraft.getMinecraft().player.posZ, SoundRegistry.AMBIENT_BLOOD_SKY_ROAR, SoundCategory.AMBIENT, 100.0F, 1.0F, false);
			}
			this.soundPlayed = true;
		} else {
			this.soundPlayed = false;
		}
	}

	@Override
	public ResourceLocation getVisionTexture() {
		return new ResourceLocation("thebetweenlands:textures/events/blood_sky.png");
	}
}
