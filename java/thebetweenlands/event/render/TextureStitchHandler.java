package thebetweenlands.event.render;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;
import thebetweenlands.blocks.BLBlockRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TextureStitchHandler {
	public static final TextureStitchHandler INSTANCE = new TextureStitchHandler();
	
	//event.map.registerIcon(p_94245_1_)
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void postStitch(TextureStitchEvent.Pre event) {
		System.out.println("Stitching test: " + event.map.getTextureType());
	}
}
