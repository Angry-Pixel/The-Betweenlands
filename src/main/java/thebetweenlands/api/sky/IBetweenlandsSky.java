package thebetweenlands.api.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IBetweenlandsSky {
	@SideOnly(Side.CLIENT)
	public void render(float partialTicks, WorldClient world, Minecraft mc);

	/**
	 * Sets the rift renderer that renders the rift
	 * @param maskRenderer
	 */
	@SideOnly(Side.CLIENT)
	public void setRiftRenderer(IRiftRenderer renderer);

	/**
	 * Returns the rift renderer
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public IRiftRenderer getRiftRenderer();
}
