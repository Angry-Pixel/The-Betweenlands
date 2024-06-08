package net.minecraft.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ParticleRenderType {
   ParticleRenderType TERRAIN_SHEET = new ParticleRenderType() {
      public void begin(BufferBuilder p_107441_, TextureManager p_107442_) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.depthMask(true);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
         p_107441_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      public void end(Tesselator p_107444_) {
         p_107444_.end();
      }

      public String toString() {
         return "TERRAIN_SHEET";
      }
   };
   ParticleRenderType PARTICLE_SHEET_OPAQUE = new ParticleRenderType() {
      public void begin(BufferBuilder p_107448_, TextureManager p_107449_) {
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
         RenderSystem.setShader(GameRenderer::getParticleShader);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         p_107448_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      public void end(Tesselator p_107451_) {
         p_107451_.end();
      }

      public String toString() {
         return "PARTICLE_SHEET_OPAQUE";
      }
   };
   ParticleRenderType PARTICLE_SHEET_TRANSLUCENT = new ParticleRenderType() {
      public void begin(BufferBuilder p_107455_, TextureManager p_107456_) {
         RenderSystem.depthMask(true);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         p_107455_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      public void end(Tesselator p_107458_) {
         p_107458_.end();
      }

      public String toString() {
         return "PARTICLE_SHEET_TRANSLUCENT";
      }
   };
   ParticleRenderType PARTICLE_SHEET_LIT = new ParticleRenderType() {
      public void begin(BufferBuilder p_107462_, TextureManager p_107463_) {
         RenderSystem.disableBlend();
         RenderSystem.depthMask(true);
         RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
         p_107462_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
      }

      public void end(Tesselator p_107465_) {
         p_107465_.end();
      }

      public String toString() {
         return "PARTICLE_SHEET_LIT";
      }
   };
   ParticleRenderType CUSTOM = new ParticleRenderType() {
      public void begin(BufferBuilder p_107469_, TextureManager p_107470_) {
         RenderSystem.depthMask(true);
         RenderSystem.disableBlend();
      }

      public void end(Tesselator p_107472_) {
      }

      public String toString() {
         return "CUSTOM";
      }
   };
   ParticleRenderType NO_RENDER = new ParticleRenderType() {
      public void begin(BufferBuilder p_107476_, TextureManager p_107477_) {
      }

      public void end(Tesselator p_107479_) {
      }

      public String toString() {
         return "NO_RENDER";
      }
   };

   void begin(BufferBuilder p_107436_, TextureManager p_107437_);

   void end(Tesselator p_107438_);
}