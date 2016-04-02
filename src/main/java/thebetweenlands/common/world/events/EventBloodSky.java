package thebetweenlands.common.world.events;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.world.event.EnvironmentEventRegistry;
import thebetweenlands.common.world.event.TimedEnvironmentEvent;

public class EventBloodSky extends TimedEnvironmentEvent {
	private boolean soundPlayed = true;
	private float skyTransparency = 0.0F;
	private float lastSkyTransparency = 0.0F;
	
	public EventBloodSky(EnvironmentEventRegistry registry) {
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
	public String getEventName() {
		return "bloodSky";
	}

	@Override
	public int getOffTime(Random rnd) {
		return rnd.nextInt(400000) + 500000;
	}

	@Override
	public int getOnTime(Random rnd) {
		return rnd.nextInt(18000) + 15000;
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
	public void setActive(boolean active, boolean markDirty) {
		super.setActive(active, markDirty);
		if(active) {
			World world = TheBetweenlands.proxy.getClientWorld();
			if(world != null && world.isRemote && !this.soundPlayed) {
				//TODO: Sound
				world.playSound(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ, new SoundEvent(new ResourceLocation("thebetweenlands:bloodSkyRoar")), SoundCategory.AMBIENT, 100.0F, 1.0F, false);
			}
			this.soundPlayed = true;
		} else {
			this.soundPlayed = false;
		}
	}
}
