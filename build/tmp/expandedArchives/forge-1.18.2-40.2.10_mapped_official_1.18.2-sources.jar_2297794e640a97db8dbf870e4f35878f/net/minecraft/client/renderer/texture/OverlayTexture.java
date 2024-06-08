package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OverlayTexture implements AutoCloseable {
   private static final int SIZE = 16;
   public static final int NO_WHITE_U = 0;
   public static final int RED_OVERLAY_V = 3;
   public static final int WHITE_OVERLAY_V = 10;
   public static final int NO_OVERLAY = pack(0, 10);
   private final DynamicTexture texture = new DynamicTexture(16, 16, false);

   public OverlayTexture() {
      NativeImage nativeimage = this.texture.getPixels();

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            if (i < 8) {
               nativeimage.setPixelRGBA(j, i, -1308622593);
            } else {
               int k = (int)((1.0F - (float)j / 15.0F * 0.75F) * 255.0F);
               nativeimage.setPixelRGBA(j, i, k << 24 | 16777215);
            }
         }
      }

      RenderSystem.activeTexture(33985);
      this.texture.bind();
      nativeimage.upload(0, 0, 0, 0, 0, nativeimage.getWidth(), nativeimage.getHeight(), false, true, false, false);
      RenderSystem.activeTexture(33984);
   }

   public void close() {
      this.texture.close();
   }

   public void setupOverlayColor() {
      RenderSystem.setupOverlayColor(this.texture::getId, 16);
   }

   public static int u(float p_118089_) {
      return (int)(p_118089_ * 15.0F);
   }

   public static int v(boolean p_118097_) {
      return p_118097_ ? 3 : 10;
   }

   public static int pack(int p_118094_, int p_118095_) {
      return p_118094_ | p_118095_ << 16;
   }

   public static int pack(float p_118091_, boolean p_118092_) {
      return pack(u(p_118091_), v(p_118092_));
   }

   public void teardownOverlayColor() {
      RenderSystem.teardownOverlayColor();
   }
}