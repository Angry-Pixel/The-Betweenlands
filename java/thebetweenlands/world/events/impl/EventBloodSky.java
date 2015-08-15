package thebetweenlands.world.events.impl;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.world.events.EnvironmentEventRegistry;
import thebetweenlands.world.events.TimedEnvironmentEvent;

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
		return rnd.nextInt(180000) + 130000;
	}

	@Override
	public int getOnTime(Random rnd) {
		return rnd.nextInt(18000) + 12000;
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
				world.playSound(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ, "thebetweenlands:bloodSkyRoar", 100.0F, 1.0F, false);
			}
			this.soundPlayed = true;
		} else {
			this.soundPlayed = false;
		}
	}
}
