package thebetweenlands.event.render;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.entities.particles.EntityThemFX;
import thebetweenlands.event.debugging.DebugHandler;
import thebetweenlands.message.MessageSyncWeather;
import thebetweenlands.utils.confighandler.ConfigHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ThemHandler {
	public static final ThemHandler INSTANCE = new ThemHandler();
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderWorld(RenderWorldLastEvent event) {
		if(Minecraft.getMinecraft().thePlayer.dimension != ConfigHandler.DIMENSION_ID) return;
		boolean denseFog = false;
		if((!TheBetweenlands.DEBUG && MessageSyncWeather.hasDenseFog) ||
				(DebugHandler.INSTANCE.denseFog && TheBetweenlands.DEBUG && !MessageSyncWeather.hasDenseFog) ||
				(!DebugHandler.INSTANCE.denseFog && TheBetweenlands.DEBUG && MessageSyncWeather.hasDenseFog)) {
			denseFog = true;
		}
		if(denseFog && FogHandler.INSTANCE.getCurrentFogEnd() < 80.0f) {
			int probability = (int)(80 - FogHandler.INSTANCE.getCurrentFogEnd()) * 6 + 10;
			if(Minecraft.getMinecraft().theWorld.rand.nextInt(probability) == 0) {
				double xOff = Minecraft.getMinecraft().theWorld.rand.nextInt(50) - 25;
				double zOff = Minecraft.getMinecraft().theWorld.rand.nextInt(50) - 25;
				double sx = Minecraft.getMinecraft().renderViewEntity.posX + xOff;
				double sz = Minecraft.getMinecraft().renderViewEntity.posZ + zOff;
				double sy = Minecraft.getMinecraft().theWorld.getHeightValue((int)sx, (int)sz) + 1.0f + Minecraft.getMinecraft().theWorld.rand.nextFloat() * 2.5f;
				Minecraft.getMinecraft().effectRenderer.addEffect(new EntityThemFX(
						Minecraft.getMinecraft().theWorld, sx, sy, sz));
			}
		}
	}
}
