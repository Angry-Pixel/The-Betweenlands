package thebetweenlands.api.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IRiftMaskRenderer {
	/**
	 * Renders the sky mask
	 * @param partialTicks
	 * @param world
	 * @param mc
	 */
	@SideOnly(Side.CLIENT)
	public void renderMask(float partialTicks, WorldClient world, Minecraft mc);

	/**
	 * Renders the rift overlay
	 * @param partialTicks
	 * @param world
	 * @param mc
	 */
	@SideOnly(Side.CLIENT)
	public void renderOverlay(float partialTicks, WorldClient world, Minecraft mc);

	/**
	 * Sets the rift's color
	 * @param partialTicks
	 * @param world
	 * @param mc
	 */
	@SideOnly(Side.CLIENT)
	public void setRiftColor(float partialTicks, WorldClient world, Minecraft mc);
}
