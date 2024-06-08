package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Matrix4f;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VertexBuffer implements AutoCloseable {
   private int vertextBufferId;
   private int indexBufferId;
   private VertexFormat.IndexType indexType;
   private int arrayObjectId;
   private int indexCount;
   private VertexFormat.Mode mode;
   private boolean sequentialIndices;
   private VertexFormat format;

   public VertexBuffer() {
      RenderSystem.glGenBuffers((p_85928_) -> {
         this.vertextBufferId = p_85928_;
      });
      RenderSystem.glGenVertexArrays((p_166881_) -> {
         this.arrayObjectId = p_166881_;
      });
      RenderSystem.glGenBuffers((p_166872_) -> {
         this.indexBufferId = p_166872_;
      });
   }

   public void bind() {
      RenderSystem.glBindBuffer(34962, () -> {
         return this.vertextBufferId;
      });
      if (this.sequentialIndices) {
         RenderSystem.glBindBuffer(34963, () -> {
            RenderSystem.AutoStorageIndexBuffer rendersystem$autostorageindexbuffer = RenderSystem.getSequentialBuffer(this.mode, this.indexCount);
            this.indexType = rendersystem$autostorageindexbuffer.type();
            return rendersystem$autostorageindexbuffer.name();
         });
      } else {
         RenderSystem.glBindBuffer(34963, () -> {
            return this.indexBufferId;
         });
      }

   }

   public void upload(BufferBuilder p_85926_) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this.upload_(p_85926_);
         });
      } else {
         this.upload_(p_85926_);
      }

   }

   public CompletableFuture<Void> uploadLater(BufferBuilder p_85933_) {
      if (!RenderSystem.isOnRenderThread()) {
         return CompletableFuture.runAsync(() -> {
            this.upload_(p_85933_);
         }, (p_166874_) -> {
            RenderSystem.recordRenderCall(p_166874_::run);
         });
      } else {
         this.upload_(p_85933_);
         return CompletableFuture.completedFuture((Void)null);
      }
   }

   private void upload_(BufferBuilder p_85936_) {
      Pair<BufferBuilder.DrawState, ByteBuffer> pair = p_85936_.popNextBuffer();
      if (this.vertextBufferId != 0) {
         BufferUploader.reset();
         BufferBuilder.DrawState bufferbuilder$drawstate = pair.getFirst();
         ByteBuffer bytebuffer = pair.getSecond();
         int i = bufferbuilder$drawstate.vertexBufferSize();
         this.indexCount = bufferbuilder$drawstate.indexCount();
         this.indexType = bufferbuilder$drawstate.indexType();
         this.format = bufferbuilder$drawstate.format();
         this.mode = bufferbuilder$drawstate.mode();
         this.sequentialIndices = bufferbuilder$drawstate.sequentialIndex();
         this.bindVertexArray();
         this.bind();
         if (!bufferbuilder$drawstate.indexOnly()) {
            bytebuffer.limit(i);
            RenderSystem.glBufferData(34962, bytebuffer, 35044);
            bytebuffer.position(i);
         }

         if (!this.sequentialIndices) {
            bytebuffer.limit(bufferbuilder$drawstate.bufferSize());
            RenderSystem.glBufferData(34963, bytebuffer, 35044);
            bytebuffer.position(0);
         } else {
            bytebuffer.limit(bufferbuilder$drawstate.bufferSize());
            bytebuffer.position(0);
         }

         unbind();
         unbindVertexArray();
      }
   }

   private void bindVertexArray() {
      RenderSystem.glBindVertexArray(() -> {
         return this.arrayObjectId;
      });
   }

   public static void unbindVertexArray() {
      RenderSystem.glBindVertexArray(() -> {
         return 0;
      });
   }

   public void draw() {
      if (this.indexCount != 0) {
         RenderSystem.drawElements(this.mode.asGLMode, this.indexCount, this.indexType.asGLType);
      }
   }

   public void drawWithShader(Matrix4f p_166868_, Matrix4f p_166869_, ShaderInstance p_166870_) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this._drawWithShader(p_166868_.copy(), p_166869_.copy(), p_166870_);
         });
      } else {
         this._drawWithShader(p_166868_, p_166869_, p_166870_);
      }

   }

   public void _drawWithShader(Matrix4f p_166877_, Matrix4f p_166878_, ShaderInstance p_166879_) {
      if (this.indexCount != 0) {
         RenderSystem.assertOnRenderThread();
         BufferUploader.reset();

         for(int i = 0; i < 12; ++i) {
            int j = RenderSystem.getShaderTexture(i);
            p_166879_.setSampler("Sampler" + i, j);
         }

         if (p_166879_.MODEL_VIEW_MATRIX != null) {
            p_166879_.MODEL_VIEW_MATRIX.set(p_166877_);
         }

         if (p_166879_.PROJECTION_MATRIX != null) {
            p_166879_.PROJECTION_MATRIX.set(p_166878_);
         }

         if (p_166879_.INVERSE_VIEW_ROTATION_MATRIX != null) {
            p_166879_.INVERSE_VIEW_ROTATION_MATRIX.set(RenderSystem.getInverseViewRotationMatrix());
         }

         if (p_166879_.COLOR_MODULATOR != null) {
            p_166879_.COLOR_MODULATOR.set(RenderSystem.getShaderColor());
         }

         if (p_166879_.FOG_START != null) {
            p_166879_.FOG_START.set(RenderSystem.getShaderFogStart());
         }

         if (p_166879_.FOG_END != null) {
            p_166879_.FOG_END.set(RenderSystem.getShaderFogEnd());
         }

         if (p_166879_.FOG_COLOR != null) {
            p_166879_.FOG_COLOR.set(RenderSystem.getShaderFogColor());
         }

         if (p_166879_.FOG_SHAPE != null) {
            p_166879_.FOG_SHAPE.set(RenderSystem.getShaderFogShape().getIndex());
         }

         if (p_166879_.TEXTURE_MATRIX != null) {
            p_166879_.TEXTURE_MATRIX.set(RenderSystem.getTextureMatrix());
         }

         if (p_166879_.GAME_TIME != null) {
            p_166879_.GAME_TIME.set(RenderSystem.getShaderGameTime());
         }

         if (p_166879_.SCREEN_SIZE != null) {
            Window window = Minecraft.getInstance().getWindow();
            p_166879_.SCREEN_SIZE.set((float)window.getWidth(), (float)window.getHeight());
         }

         if (p_166879_.LINE_WIDTH != null && (this.mode == VertexFormat.Mode.LINES || this.mode == VertexFormat.Mode.LINE_STRIP)) {
            p_166879_.LINE_WIDTH.set(RenderSystem.getShaderLineWidth());
         }

         RenderSystem.setupShaderLights(p_166879_);
         this.bindVertexArray();
         this.bind();
         this.getFormat().setupBufferState();
         p_166879_.apply();
         RenderSystem.drawElements(this.mode.asGLMode, this.indexCount, this.indexType.asGLType);
         p_166879_.clear();
         this.getFormat().clearBufferState();
         unbind();
         unbindVertexArray();
      }
   }

   public void drawChunkLayer() {
      if (this.indexCount != 0) {
         RenderSystem.assertOnRenderThread();
         this.bindVertexArray();
         this.bind();
         this.format.setupBufferState();
         RenderSystem.drawElements(this.mode.asGLMode, this.indexCount, this.indexType.asGLType);
      }
   }

   public static void unbind() {
      RenderSystem.glBindBuffer(34962, () -> {
         return 0;
      });
      RenderSystem.glBindBuffer(34963, () -> {
         return 0;
      });
   }

   public void close() {
      if (this.indexBufferId >= 0) {
         RenderSystem.glDeleteBuffers(this.indexBufferId);
         this.indexBufferId = -1;
      }

      if (this.vertextBufferId > 0) {
         RenderSystem.glDeleteBuffers(this.vertextBufferId);
         this.vertextBufferId = 0;
      }

      if (this.arrayObjectId > 0) {
         RenderSystem.glDeleteVertexArrays(this.arrayObjectId);
         this.arrayObjectId = 0;
      }

   }

   public VertexFormat getFormat() {
      return this.format;
   }
}