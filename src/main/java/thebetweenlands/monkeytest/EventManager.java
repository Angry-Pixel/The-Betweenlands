package thebetweenlands.monkeytest;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.proxy.ClientProxy;
import thebetweenlands.common.TheBetweenlands;

public class EventManager {

 
  public static long ticks = 0;

	//TEMP
  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onTextureStitch(TextureStitchEvent event) {
	  System.out.println("*****PARTICLE BEAM TEXTURE REGISTERED HERE*****");
    event.getMap().registerSprite(ParticlePuzzleBeam.texture);
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onTick(TickEvent.ClientTickEvent event) {
    if (event.side == Side.CLIENT && event.phase == TickEvent.Phase.START) {
      ClientProxy.particleRenderer.updateParticles();
      ticks++;
    }
  }


  static float tickCounter = 0;
  static EntityPlayer clientPlayer = null;

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public void onRenderAfterWorld(RenderWorldLastEvent event) {
    tickCounter++;

    if (TheBetweenlands.proxy instanceof ClientProxy && Minecraft.getMinecraft().player != null) {
      ClientProxy.particleRenderer.renderParticles(Minecraft.getMinecraft().player, event.getPartialTicks());
    }
    //OpenGlHelper.glUseProgram(0);
  }

}
