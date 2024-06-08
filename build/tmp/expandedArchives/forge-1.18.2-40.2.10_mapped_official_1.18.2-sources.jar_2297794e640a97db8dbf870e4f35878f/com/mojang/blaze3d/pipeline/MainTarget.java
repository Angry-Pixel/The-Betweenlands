package com.mojang.blaze3d.pipeline;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Objects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MainTarget extends RenderTarget {
   public static final int DEFAULT_WIDTH = 854;
   public static final int DEFAULT_HEIGHT = 480;
   static final MainTarget.Dimension DEFAULT_DIMENSIONS = new MainTarget.Dimension(854, 480);

   public MainTarget(int p_166137_, int p_166138_) {
      super(true);
      RenderSystem.assertOnRenderThreadOrInit();
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this.createFrameBuffer(p_166137_, p_166138_);
         });
      } else {
         this.createFrameBuffer(p_166137_, p_166138_);
      }

   }

   private void createFrameBuffer(int p_166142_, int p_166143_) {
      RenderSystem.assertOnRenderThreadOrInit();
      MainTarget.Dimension maintarget$dimension = this.allocateAttachments(p_166142_, p_166143_);
      this.frameBufferId = GlStateManager.glGenFramebuffers();
      GlStateManager._glBindFramebuffer(36160, this.frameBufferId);
      GlStateManager._bindTexture(this.colorTextureId);
      GlStateManager._texParameter(3553, 10241, 9728);
      GlStateManager._texParameter(3553, 10240, 9728);
      GlStateManager._texParameter(3553, 10242, 33071);
      GlStateManager._texParameter(3553, 10243, 33071);
      GlStateManager._glFramebufferTexture2D(36160, 36064, 3553, this.colorTextureId, 0);
      GlStateManager._bindTexture(this.depthBufferId);
      GlStateManager._texParameter(3553, 34892, 0);
      GlStateManager._texParameter(3553, 10241, 9728);
      GlStateManager._texParameter(3553, 10240, 9728);
      GlStateManager._texParameter(3553, 10242, 33071);
      GlStateManager._texParameter(3553, 10243, 33071);
      GlStateManager._glFramebufferTexture2D(36160, 36096, 3553, this.depthBufferId, 0);
      GlStateManager._bindTexture(0);
      this.viewWidth = maintarget$dimension.width;
      this.viewHeight = maintarget$dimension.height;
      this.width = maintarget$dimension.width;
      this.height = maintarget$dimension.height;
      this.checkStatus();
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   private MainTarget.Dimension allocateAttachments(int p_166147_, int p_166148_) {
      RenderSystem.assertOnRenderThreadOrInit();
      this.colorTextureId = TextureUtil.generateTextureId();
      this.depthBufferId = TextureUtil.generateTextureId();
      MainTarget.AttachmentState maintarget$attachmentstate = MainTarget.AttachmentState.NONE;

      for(MainTarget.Dimension maintarget$dimension : MainTarget.Dimension.listWithFallback(p_166147_, p_166148_)) {
         maintarget$attachmentstate = MainTarget.AttachmentState.NONE;
         if (this.allocateColorAttachment(maintarget$dimension)) {
            maintarget$attachmentstate = maintarget$attachmentstate.with(MainTarget.AttachmentState.COLOR);
         }

         if (this.allocateDepthAttachment(maintarget$dimension)) {
            maintarget$attachmentstate = maintarget$attachmentstate.with(MainTarget.AttachmentState.DEPTH);
         }

         if (maintarget$attachmentstate == MainTarget.AttachmentState.COLOR_DEPTH) {
            return maintarget$dimension;
         }
      }

      throw new RuntimeException("Unrecoverable GL_OUT_OF_MEMORY (allocated attachments = " + maintarget$attachmentstate.name() + ")");
   }

   private boolean allocateColorAttachment(MainTarget.Dimension p_166140_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GlStateManager._getError();
      GlStateManager._bindTexture(this.colorTextureId);
      GlStateManager._texImage2D(3553, 0, 32856, p_166140_.width, p_166140_.height, 0, 6408, 5121, (IntBuffer)null);
      return GlStateManager._getError() != 1285;
   }

   private boolean allocateDepthAttachment(MainTarget.Dimension p_166145_) {
      RenderSystem.assertOnRenderThreadOrInit();
      GlStateManager._getError();
      GlStateManager._bindTexture(this.depthBufferId);
      GlStateManager._texImage2D(3553, 0, 6402, p_166145_.width, p_166145_.height, 0, 6402, 5126, (IntBuffer)null);
      return GlStateManager._getError() != 1285;
   }

   @OnlyIn(Dist.CLIENT)
   static enum AttachmentState {
      NONE,
      COLOR,
      DEPTH,
      COLOR_DEPTH;

      private static final MainTarget.AttachmentState[] VALUES = values();

      MainTarget.AttachmentState with(MainTarget.AttachmentState p_166164_) {
         return VALUES[this.ordinal() | p_166164_.ordinal()];
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Dimension {
      public final int width;
      public final int height;

      Dimension(int p_166171_, int p_166172_) {
         this.width = p_166171_;
         this.height = p_166172_;
      }

      static List<MainTarget.Dimension> listWithFallback(int p_166174_, int p_166175_) {
         RenderSystem.assertOnRenderThreadOrInit();
         int i = RenderSystem.maxSupportedTextureSize();
         return p_166174_ > 0 && p_166174_ <= i && p_166175_ > 0 && p_166175_ <= i ? ImmutableList.of(new MainTarget.Dimension(p_166174_, p_166175_), MainTarget.DEFAULT_DIMENSIONS) : ImmutableList.of(MainTarget.DEFAULT_DIMENSIONS);
      }

      public boolean equals(Object p_166177_) {
         if (this == p_166177_) {
            return true;
         } else if (p_166177_ != null && this.getClass() == p_166177_.getClass()) {
            MainTarget.Dimension maintarget$dimension = (MainTarget.Dimension)p_166177_;
            return this.width == maintarget$dimension.width && this.height == maintarget$dimension.height;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(this.width, this.height);
      }

      public String toString() {
         return this.width + "x" + this.height;
      }
   }
}