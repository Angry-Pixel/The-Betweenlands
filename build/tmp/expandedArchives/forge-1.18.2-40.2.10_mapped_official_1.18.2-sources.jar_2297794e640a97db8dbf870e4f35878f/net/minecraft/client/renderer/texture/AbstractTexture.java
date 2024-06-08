package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.util.concurrent.Executor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractTexture implements AutoCloseable {
   public static final int NOT_ASSIGNED = -1;
   protected int id = -1;
   protected boolean blur;
   protected boolean mipmap;

   public void setFilter(boolean p_117961_, boolean p_117962_) {
      RenderSystem.assertOnRenderThreadOrInit();
      this.blur = p_117961_;
      this.mipmap = p_117962_;
      int i;
      int j;
      if (p_117961_) {
         i = p_117962_ ? 9987 : 9729;
         j = 9729;
      } else {
         i = p_117962_ ? 9986 : 9728;
         j = 9728;
      }

      this.bind();
      GlStateManager._texParameter(3553, 10241, i);
      GlStateManager._texParameter(3553, 10240, j);
   }

   // FORGE: This seems to have been stripped out, but we need it
   private boolean lastBlur;
   private boolean lastMipmap;

   public void setBlurMipmap(boolean blur, boolean mipmap) {
      this.lastBlur = this.blur;
      this.lastMipmap = this.mipmap;
      setFilter(blur, mipmap);
   }

   public void restoreLastBlurMipmap() {
      setFilter(this.lastBlur, this.lastMipmap);
   }

   public int getId() {
      RenderSystem.assertOnRenderThreadOrInit();
      if (this.id == -1) {
         this.id = TextureUtil.generateTextureId();
      }

      return this.id;
   }

   public void releaseId() {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            if (this.id != -1) {
               TextureUtil.releaseTextureId(this.id);
               this.id = -1;
            }

         });
      } else if (this.id != -1) {
         TextureUtil.releaseTextureId(this.id);
         this.id = -1;
      }

   }

   public abstract void load(ResourceManager p_117955_) throws IOException;

   public void bind() {
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> {
            GlStateManager._bindTexture(this.getId());
         });
      } else {
         GlStateManager._bindTexture(this.getId());
      }

   }

   public void reset(TextureManager p_117956_, ResourceManager p_117957_, ResourceLocation p_117958_, Executor p_117959_) {
      p_117956_.register(p_117958_, this);
   }

   public void close() {
   }
}
