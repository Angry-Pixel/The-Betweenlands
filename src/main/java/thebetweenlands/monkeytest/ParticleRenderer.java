package thebetweenlands.monkeytest;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleRenderer {

  ArrayList<Particle> particles = new ArrayList<Particle>();

  public void updateParticles() {
    boolean doRemove = false;
    try {
      for (int i = 0; i < particles.size(); i++) {
        doRemove = true;
        if (particles.get(i) != null) {
          if (particles.get(i) instanceof IParticleTracked) {
            if (((IParticleTracked) particles.get(i)).alive()) {
              particles.get(i).onUpdate();
              doRemove = false;
            }
          }
        }
        if (doRemove) {
          particles.remove(i);
        }
      }
    }
    catch (Exception e) {
      //somehow the "onUpdate" in ParticleMote class gives AbstractMethodError
      //but ITS NOT ABSTRACT!!! ITS RIGHT THER EIN THE CLASS PUBLIC VOID ONUPDATE() 
      //shhheesh
      e.printStackTrace();
    }
  }

  public void renderParticles(EntityPlayer dumbplayer, float partialTicks) {
    float f = ActiveRenderInfo.getRotationX();
    float f1 = ActiveRenderInfo.getRotationZ();
    float f2 = ActiveRenderInfo.getRotationYZ();
    float f3 = ActiveRenderInfo.getRotationXY();
    float f4 = ActiveRenderInfo.getRotationXZ();
    EntityPlayer player = Minecraft.getMinecraft().player;
    if (player != null) {
      Particle.interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
      Particle.interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
      Particle.interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
      Particle.cameraViewDir = player.getLook(partialTicks);
      GlStateManager.enableAlpha();
      GlStateManager.enableBlend();
      GlStateManager.alphaFunc(GL11.GL_ALWAYS, 0);
      GlStateManager.disableCull();
      GlStateManager.disableLighting();
      GlStateManager.depthMask(false);
      Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder buffer = tess.getBuffer();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
      for (int i = 0; i < particles.size(); i++) {
        if (!((IParticleTracked) particles.get(i)).isAdditive()) {
          particles.get(i).renderParticle(buffer, player, partialTicks, f, f4, f1, f2, f3);
        }
      }
      tess.draw();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
      buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
      for (int i = 0; i < particles.size(); i++) {
        if (((IParticleTracked) particles.get(i)).isAdditive()) {
          particles.get(i).renderParticle(buffer, player, partialTicks, f, f4, f1, f2, f3);
        }
      }
      tess.draw();
      GlStateManager.disableDepth();
      buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
      for (int i = 0; i < particles.size(); i++) {
        if (((IParticleTracked) particles.get(i)).ignoreDepth()) {
          particles.get(i).renderParticle(buffer, player, partialTicks, f, f4, f1, f2, f3);
        }
      }
      tess.draw();
      GlStateManager.enableDepth();
      GlStateManager.enableCull();
      GlStateManager.depthMask(true);
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.disableBlend();
      GlStateManager.alphaFunc(516, 0.1F);
    }
  }

  public void addParticle(Particle particle) {
    particles.add(particle);
  }
}
