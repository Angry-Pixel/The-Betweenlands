package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.Closeable;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SimpleTexture extends AbstractTexture {
   static final Logger LOGGER = LogUtils.getLogger();
   protected final ResourceLocation location;

   public SimpleTexture(ResourceLocation p_118133_) {
      this.location = p_118133_;
   }

   public void load(ResourceManager p_118135_) throws IOException {
      SimpleTexture.TextureImage simpletexture$textureimage = this.getTextureImage(p_118135_);
      simpletexture$textureimage.throwIfError();
      TextureMetadataSection texturemetadatasection = simpletexture$textureimage.getTextureMetadata();
      boolean flag;
      boolean flag1;
      if (texturemetadatasection != null) {
         flag = texturemetadatasection.isBlur();
         flag1 = texturemetadatasection.isClamp();
      } else {
         flag = false;
         flag1 = false;
      }

      NativeImage nativeimage = simpletexture$textureimage.getImage();
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> {
            this.doLoad(nativeimage, flag, flag1);
         });
      } else {
         this.doLoad(nativeimage, flag, flag1);
      }

   }

   private void doLoad(NativeImage p_118137_, boolean p_118138_, boolean p_118139_) {
      TextureUtil.prepareImage(this.getId(), 0, p_118137_.getWidth(), p_118137_.getHeight());
      p_118137_.upload(0, 0, 0, 0, 0, p_118137_.getWidth(), p_118137_.getHeight(), p_118138_, p_118139_, false, true);
   }

   protected SimpleTexture.TextureImage getTextureImage(ResourceManager p_118140_) {
      return SimpleTexture.TextureImage.load(p_118140_, this.location);
   }

   @OnlyIn(Dist.CLIENT)
   protected static class TextureImage implements Closeable {
      @Nullable
      private final TextureMetadataSection metadata;
      @Nullable
      private final NativeImage image;
      @Nullable
      private final IOException exception;

      public TextureImage(IOException p_118153_) {
         this.exception = p_118153_;
         this.metadata = null;
         this.image = null;
      }

      public TextureImage(@Nullable TextureMetadataSection p_118150_, NativeImage p_118151_) {
         this.exception = null;
         this.metadata = p_118150_;
         this.image = p_118151_;
      }

      public static SimpleTexture.TextureImage load(ResourceManager p_118156_, ResourceLocation p_118157_) {
         try {
            Resource resource = p_118156_.getResource(p_118157_);

            SimpleTexture.TextureImage $$5;
            try {
               NativeImage nativeimage = NativeImage.read(resource.getInputStream());
               TextureMetadataSection texturemetadatasection = null;

               try {
                  texturemetadatasection = resource.getMetadata(TextureMetadataSection.SERIALIZER);
               } catch (RuntimeException runtimeexception) {
                  SimpleTexture.LOGGER.warn("Failed reading metadata of: {}", p_118157_, runtimeexception);
               }

               $$5 = new SimpleTexture.TextureImage(texturemetadatasection, nativeimage);
            } catch (Throwable throwable1) {
               if (resource != null) {
                  try {
                     resource.close();
                  } catch (Throwable throwable) {
                     throwable1.addSuppressed(throwable);
                  }
               }

               throw throwable1;
            }

            if (resource != null) {
               resource.close();
            }

            return $$5;
         } catch (IOException ioexception) {
            return new SimpleTexture.TextureImage(ioexception);
         }
      }

      @Nullable
      public TextureMetadataSection getTextureMetadata() {
         return this.metadata;
      }

      public NativeImage getImage() throws IOException {
         if (this.exception != null) {
            throw this.exception;
         } else {
            return this.image;
         }
      }

      public void close() {
         if (this.image != null) {
            this.image.close();
         }

      }

      public void throwIfError() throws IOException {
         if (this.exception != null) {
            throw this.exception;
         }
      }
   }
}