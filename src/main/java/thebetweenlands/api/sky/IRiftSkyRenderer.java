package thebetweenlands.api.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IRiftSkyRenderer {
	/**
	 * Sets the sky's clear color
	 * @param partialTicks
	 * @param world
	 * @param mc
	 */
	@OnlyIn(Dist.CLIENT)
	public void setClearColor(float partialTicks, WorldClient world, Minecraft mc);

	/**
	 * Renders the sky inside the rift
	 * @param partialTicks
	 * @param world
	 * @param mc
	 */
	@OnlyIn(Dist.CLIENT)
	public void render(float partialTicks, WorldClient world, Minecraft mc);
	
	/**
	 * Returns the sky's relative brightness between 0 and 1
	 * @param partialTicks
	 * @param world
	 * @param mc
	 * @return
	 */
	@OnlyIn(Dist.CLIENT)
	public float getSkyBrightness(float partialTicks, WorldClient world, Minecraft mc);
}
