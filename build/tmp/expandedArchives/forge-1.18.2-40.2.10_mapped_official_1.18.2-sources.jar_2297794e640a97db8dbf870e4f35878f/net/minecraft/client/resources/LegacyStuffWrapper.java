package net.minecraft.client.resources;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LegacyStuffWrapper {
   /** @deprecated */
   @Deprecated
   public static int[] getPixels(ResourceManager p_118727_, ResourceLocation p_118728_) throws IOException {
      Resource resource = p_118727_.getResource(p_118728_);

      int[] aint;
      try {
         NativeImage nativeimage = NativeImage.read(resource.getInputStream());

         try {
            aint = nativeimage.makePixelArray();
         } catch (Throwable throwable2) {
            if (nativeimage != null) {
               try {
                  nativeimage.close();
               } catch (Throwable throwable1) {
                  throwable2.addSuppressed(throwable1);
               }
            }

            throw throwable2;
         }

         if (nativeimage != null) {
            nativeimage.close();
         }
      } catch (Throwable throwable3) {
         if (resource != null) {
            try {
               resource.close();
            } catch (Throwable throwable) {
               throwable3.addSuppressed(throwable);
            }
         }

         throw throwable3;
      }

      if (resource != null) {
         resource.close();
      }

      return aint;
   }
}