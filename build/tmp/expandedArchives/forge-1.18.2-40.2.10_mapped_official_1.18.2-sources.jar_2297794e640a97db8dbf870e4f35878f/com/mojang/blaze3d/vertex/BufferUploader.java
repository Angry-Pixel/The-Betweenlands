package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BufferUploader {
   private static int lastVertexArrayObject;
   private static int lastVertexBufferObject;
   private static int lastIndexBufferObject;
   @Nullable
   private static VertexFormat lastFormat;

   public static void reset() {
      if (lastFormat != null) {
         lastFormat.clearBufferState();
         lastFormat = null;
      }

      GlStateManager._glBindBuffer(34963, 0);
      lastIndexBufferObject = 0;
      GlStateManager._glBindBuffer(34962, 0);
      lastVertexBufferObject = 0;
      GlStateManager._glBindVertexArray(0);
      lastVertexArrayObject = 0;
   }

   public static void invalidateElementArrayBufferBinding() {
      GlStateManager._glBindBuffer(34963, 0);
      lastIndexBufferObject = 0;
   }

   public static void end(BufferBuilder p_85762_) {
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> {
            Pair<BufferBuilder.DrawState, ByteBuffer> pair1 = p_85762_.popNextBuffer();
            BufferBuilder.DrawState bufferbuilder$drawstate1 = pair1.getFirst();
            _end(pair1.getSecond(), bufferbuilder$drawstate1.mode(), bufferbuilder$drawstate1.format(), bufferbuilder$drawstate1.vertexCount(), bufferbuilder$drawstate1.indexType(), bufferbuilder$drawstate1.indexCount(), bufferbuilder$drawstate1.sequentialIndex());
         });
      } else {
         Pair<BufferBuilder.DrawState, ByteBuffer> pair = p_85762_.popNextBuffer();
         BufferBuilder.DrawState bufferbuilder$drawstate = pair.getFirst();
         _end(pair.getSecond(), bufferbuilder$drawstate.mode(), bufferbuilder$drawstate.format(), bufferbuilder$drawstate.vertexCount(), bufferbuilder$drawstate.indexType(), bufferbuilder$drawstate.indexCount(), bufferbuilder$drawstate.sequentialIndex());
      }

   }

   private static void _end(ByteBuffer p_166839_, VertexFormat.Mode p_166840_, VertexFormat p_166841_, int p_166842_, VertexFormat.IndexType p_166843_, int p_166844_, boolean p_166845_) {
      RenderSystem.assertOnRenderThread();
      p_166839_.clear();
      if (p_166842_ > 0) {
         int i = p_166842_ * p_166841_.getVertexSize();
         updateVertexSetup(p_166841_);
         p_166839_.position(0);
         p_166839_.limit(i);
         GlStateManager._glBufferData(34962, p_166839_, 35048);
         int j;
         if (p_166845_) {
            RenderSystem.AutoStorageIndexBuffer rendersystem$autostorageindexbuffer = RenderSystem.getSequentialBuffer(p_166840_, p_166844_);
            int k = rendersystem$autostorageindexbuffer.name();
            if (k != lastIndexBufferObject) {
               GlStateManager._glBindBuffer(34963, k);
               lastIndexBufferObject = k;
            }

            j = rendersystem$autostorageindexbuffer.type().asGLType;
         } else {
            int i1 = p_166841_.getOrCreateIndexBufferObject();
            if (i1 != lastIndexBufferObject) {
               GlStateManager._glBindBuffer(34963, i1);
               lastIndexBufferObject = i1;
            }

            p_166839_.position(i);
            p_166839_.limit(i + p_166844_ * p_166843_.bytes);
            GlStateManager._glBufferData(34963, p_166839_, 35048);
            j = p_166843_.asGLType;
         }

         ShaderInstance shaderinstance = RenderSystem.getShader();

         for(int j1 = 0; j1 < 8; ++j1) {
            int l = RenderSystem.getShaderTexture(j1);
            shaderinstance.setSampler("Sampler" + j1, l);
         }

         if (shaderinstance.MODEL_VIEW_MATRIX != null) {
            shaderinstance.MODEL_VIEW_MATRIX.set(RenderSystem.getModelViewMatrix());
         }

         if (shaderinstance.PROJECTION_MATRIX != null) {
            shaderinstance.PROJECTION_MATRIX.set(RenderSystem.getProjectionMatrix());
         }

         if (shaderinstance.INVERSE_VIEW_ROTATION_MATRIX != null) {
            shaderinstance.INVERSE_VIEW_ROTATION_MATRIX.set(RenderSystem.getInverseViewRotationMatrix());
         }

         if (shaderinstance.COLOR_MODULATOR != null) {
            shaderinstance.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
         }

         if (shaderinstance.FOG_START != null) {
            shaderinstance.FOG_START.set(RenderSystem.getShaderFogStart());
         }

         if (shaderinstance.FOG_END != null) {
            shaderinstance.FOG_END.set(RenderSystem.getShaderFogEnd());
         }

         if (shaderinstance.FOG_COLOR != null) {
            shaderinstance.FOG_COLOR.set(RenderSystem.getShaderFogColor());
         }

         if (shaderinstance.FOG_SHAPE != null) {
            shaderinstance.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
         }

         if (shaderinstance.TEXTURE_MATRIX != null) {
            shaderinstance.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
         }

         if (shaderinstance.GAME_TIME != null) {
            shaderinstance.GAME_TIME.set(RenderSystem.getShaderGameTime());
         }

         if (shaderinstance.SCREEN_SIZE != null) {
            Window window = Minecraft.getInstance().getWindow();
            shaderinstance.SCREEN_SIZE.set((float)window.getWidth(), (float)window.getHeight());
         }

         if (shaderinstance.LINE_WIDTH != null && (p_166840_ == VertexFormat.Mode.LINES || p_166840_ == VertexFormat.Mode.LINE_STRIP)) {
            shaderinstance.LINE_WIDTH.set(RenderSystem.getShaderLineWidth());
         }

         RenderSystem.setupShaderLights(shaderinstance);
         shaderinstance.apply();
         GlStateManager._drawElements(p_166840_.asGLMode, p_166844_, j, 0L);
         shaderinstance.clear();
         p_166839_.position(0);
      }
   }

   public static void _endInternal(BufferBuilder p_166848_) {
      RenderSystem.assertOnRenderThread();
      Pair<BufferBuilder.DrawState, ByteBuffer> pair = p_166848_.popNextBuffer();
      BufferBuilder.DrawState bufferbuilder$drawstate = pair.getFirst();
      ByteBuffer bytebuffer = pair.getSecond();
      VertexFormat vertexformat = bufferbuilder$drawstate.format();
      int i = bufferbuilder$drawstate.vertexCount();
      bytebuffer.clear();
      if (i > 0) {
         int j = i * vertexformat.getVertexSize();
         updateVertexSetup(vertexformat);
         bytebuffer.position(0);
         bytebuffer.limit(j);
         GlStateManager._glBufferData(34962, bytebuffer, 35048);
         RenderSystem.AutoStorageIndexBuffer rendersystem$autostorageindexbuffer = RenderSystem.getSequentialBuffer(bufferbuilder$drawstate.mode(), bufferbuilder$drawstate.indexCount());
         int k = rendersystem$autostorageindexbuffer.name();
         if (k != lastIndexBufferObject) {
            GlStateManager._glBindBuffer(34963, k);
            lastIndexBufferObject = k;
         }

         int l = rendersystem$autostorageindexbuffer.type().asGLType;
         GlStateManager._drawElements(bufferbuilder$drawstate.mode().asGLMode, bufferbuilder$drawstate.indexCount(), l, 0L);
         bytebuffer.position(0);
      }
   }

   private static void updateVertexSetup(VertexFormat p_166837_) {
      int i = p_166837_.getOrCreateVertexArrayObject();
      int j = p_166837_.getOrCreateVertexBufferObject();
      boolean flag = p_166837_ != lastFormat;
      if (flag) {
         reset();
      }

      if (i != lastVertexArrayObject) {
         GlStateManager._glBindVertexArray(i);
         lastVertexArrayObject = i;
      }

      if (j != lastVertexBufferObject) {
         GlStateManager._glBindBuffer(34962, j);
         lastVertexBufferObject = j;
      }

      if (flag) {
         p_166837_.setupBufferState();
         lastFormat = p_166837_;
      }

   }
}