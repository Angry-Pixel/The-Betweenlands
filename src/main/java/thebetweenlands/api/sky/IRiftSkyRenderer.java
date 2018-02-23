package thebetweenlands.api.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IRiftSkyRenderer {
	/**
	 * Sets the sky's clear color
	 * @param partialTicks
	 * @param world
	 * @param mc
	 */
	@SideOnly(Side.CLIENT)
	public void setClearColor(float partialTicks, WorldClient world, Minecraft mc);

	/**
	 * Renders the sky inside the rift
	 * @param partialTicks
	 * @param world
	 * @param mc
	 */
	@SideOnly(Side.CLIENT)
	public void render(float partialTicks, WorldClient world, Minecraft mc);
}
