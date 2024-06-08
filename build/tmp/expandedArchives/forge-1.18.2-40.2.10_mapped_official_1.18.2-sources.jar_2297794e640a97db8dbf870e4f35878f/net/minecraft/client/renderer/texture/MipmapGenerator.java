package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MipmapGenerator {
   private static final int ALPHA_CUTOUT_CUTOFF = 96;
   private static final float[] POW22 = Util.make(new float[256], (p_118058_) -> {
      for(int i = 0; i < p_118058_.length; ++i) {
         p_118058_[i] = (float)Math.pow((double)((float)i / 255.0F), 2.2D);
      }

   });

   private MipmapGenerator() {
   }

   public static NativeImage[] generateMipLevels(NativeImage p_118055_, int p_118056_) {
      NativeImage[] anativeimage = new NativeImage[p_118056_ + 1];
      anativeimage[0] = p_118055_;
      if (p_118056_ > 0) {
         boolean flag = false;

         label51:
         for(int i = 0; i < p_118055_.getWidth(); ++i) {
            for(int j = 0; j < p_118055_.getHeight(); ++j) {
               if (p_118055_.getPixelRGBA(i, j) >> 24 == 0) {
                  flag = true;
                  break label51;
               }
            }
         }

         int maxMipmapLevel = net.minecraftforge.client.ForgeHooksClient.getMaxMipmapLevel(p_118055_.getWidth(), p_118055_.getHeight());
         for(int k1 = 1; k1 <= p_118056_; ++k1) {
            NativeImage nativeimage1 = anativeimage[k1 - 1];
            // Forge: guard against invalid texture size, because we allow generating mipmaps regardless of texture sizes
            NativeImage nativeimage = new NativeImage(Math.max(1, nativeimage1.getWidth() >> 1), Math.max(1, nativeimage1.getHeight() >> 1), false);
            if (k1 <= maxMipmapLevel) {
            int k = nativeimage.getWidth();
            int l = nativeimage.getHeight();

            for(int i1 = 0; i1 < k; ++i1) {
               for(int j1 = 0; j1 < l; ++j1) {
                  nativeimage.setPixelRGBA(i1, j1, alphaBlend(nativeimage1.getPixelRGBA(i1 * 2 + 0, j1 * 2 + 0), nativeimage1.getPixelRGBA(i1 * 2 + 1, j1 * 2 + 0), nativeimage1.getPixelRGBA(i1 * 2 + 0, j1 * 2 + 1), nativeimage1.getPixelRGBA(i1 * 2 + 1, j1 * 2 + 1), flag));
               }
            }
            }

            anativeimage[k1] = nativeimage;
         }
      }

      return anativeimage;
   }

   private static int alphaBlend(int p_118049_, int p_118050_, int p_118051_, int p_118052_, boolean p_118053_) {
      if (p_118053_) {
         float f = 0.0F;
         float f1 = 0.0F;
         float f2 = 0.0F;
         float f3 = 0.0F;
         if (p_118049_ >> 24 != 0) {
            f += getPow22(p_118049_ >> 24);
            f1 += getPow22(p_118049_ >> 16);
            f2 += getPow22(p_118049_ >> 8);
            f3 += getPow22(p_118049_ >> 0);
         }

         if (p_118050_ >> 24 != 0) {
            f += getPow22(p_118050_ >> 24);
            f1 += getPow22(p_118050_ >> 16);
            f2 += getPow22(p_118050_ >> 8);
            f3 += getPow22(p_118050_ >> 0);
         }

         if (p_118051_ >> 24 != 0) {
            f += getPow22(p_118051_ >> 24);
            f1 += getPow22(p_118051_ >> 16);
            f2 += getPow22(p_118051_ >> 8);
            f3 += getPow22(p_118051_ >> 0);
         }

         if (p_118052_ >> 24 != 0) {
            f += getPow22(p_118052_ >> 24);
            f1 += getPow22(p_118052_ >> 16);
            f2 += getPow22(p_118052_ >> 8);
            f3 += getPow22(p_118052_ >> 0);
         }

         f /= 4.0F;
         f1 /= 4.0F;
         f2 /= 4.0F;
         f3 /= 4.0F;
         int i1 = (int)(Math.pow((double)f, 0.45454545454545453D) * 255.0D);
         int j1 = (int)(Math.pow((double)f1, 0.45454545454545453D) * 255.0D);
         int k1 = (int)(Math.pow((double)f2, 0.45454545454545453D) * 255.0D);
         int l1 = (int)(Math.pow((double)f3, 0.45454545454545453D) * 255.0D);
         if (i1 < 96) {
            i1 = 0;
         }

         return i1 << 24 | j1 << 16 | k1 << 8 | l1;
      } else {
         int i = gammaBlend(p_118049_, p_118050_, p_118051_, p_118052_, 24);
         int j = gammaBlend(p_118049_, p_118050_, p_118051_, p_118052_, 16);
         int k = gammaBlend(p_118049_, p_118050_, p_118051_, p_118052_, 8);
         int l = gammaBlend(p_118049_, p_118050_, p_118051_, p_118052_, 0);
         return i << 24 | j << 16 | k << 8 | l;
      }
   }

   private static int gammaBlend(int p_118043_, int p_118044_, int p_118045_, int p_118046_, int p_118047_) {
      float f = getPow22(p_118043_ >> p_118047_);
      float f1 = getPow22(p_118044_ >> p_118047_);
      float f2 = getPow22(p_118045_ >> p_118047_);
      float f3 = getPow22(p_118046_ >> p_118047_);
      float f4 = (float)((double)((float)Math.pow((double)(f + f1 + f2 + f3) * 0.25D, 0.45454545454545453D)));
      return (int)((double)f4 * 255.0D);
   }

   private static float getPow22(int p_118041_) {
      return POW22[p_118041_ & 255];
   }
}
