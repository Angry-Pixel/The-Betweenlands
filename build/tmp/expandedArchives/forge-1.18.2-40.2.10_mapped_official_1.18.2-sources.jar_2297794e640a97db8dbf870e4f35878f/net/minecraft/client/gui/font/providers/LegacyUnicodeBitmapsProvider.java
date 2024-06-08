package net.minecraft.client.gui.font.providers;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.RawGlyph;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class LegacyUnicodeBitmapsProvider implements GlyphProvider {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int UNICODE_SHEETS = 256;
   private static final int CHARS_PER_SHEET = 256;
   private static final int TEXTURE_SIZE = 256;
   private final ResourceManager resourceManager;
   private final byte[] sizes;
   private final String texturePattern;
   private final Map<ResourceLocation, NativeImage> textures = Maps.newHashMap();

   public LegacyUnicodeBitmapsProvider(ResourceManager p_95429_, byte[] p_95430_, String p_95431_) {
      this.resourceManager = p_95429_;
      this.sizes = p_95430_;
      this.texturePattern = p_95431_;

      for(int i = 0; i < 256; ++i) {
         int j = i * 256;
         ResourceLocation resourcelocation = this.getSheetLocation(j);

         try {
            Resource resource = this.resourceManager.getResource(resourcelocation);

            label90: {
               try {
                  NativeImage nativeimage;
                  label104: {
                     nativeimage = NativeImage.read(NativeImage.Format.RGBA, resource.getInputStream());

                     try {
                        if (nativeimage.getWidth() == 256 && nativeimage.getHeight() == 256) {
                           int k = 0;

                           while(true) {
                              if (k >= 256) {
                                 break label104;
                              }

                              byte b0 = p_95430_[j + k];
                              if (b0 != 0 && getLeft(b0) > getRight(b0)) {
                                 p_95430_[j + k] = 0;
                              }

                              ++k;
                           }
                        }
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
                     break label90;
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
               continue;
            }

            if (resource != null) {
               resource.close();
            }
         } catch (IOException ioexception) {
         }

         Arrays.fill(p_95430_, j, j + 256, (byte)0);
      }

   }

   public void close() {
      this.textures.values().forEach(NativeImage::close);
   }

   private ResourceLocation getSheetLocation(int p_95443_) {
      ResourceLocation resourcelocation = new ResourceLocation(String.format(this.texturePattern, String.format("%02x", p_95443_ / 256)));
      return new ResourceLocation(resourcelocation.getNamespace(), "textures/" + resourcelocation.getPath());
   }

   @Nullable
   public RawGlyph getGlyph(int p_95436_) {
      if (p_95436_ >= 0 && p_95436_ <= 65535) {
         byte b0 = this.sizes[p_95436_];
         if (b0 != 0) {
            NativeImage nativeimage = this.textures.computeIfAbsent(this.getSheetLocation(p_95436_), this::loadTexture);
            if (nativeimage != null) {
               int i = getLeft(b0);
               return new LegacyUnicodeBitmapsProvider.Glyph(p_95436_ % 16 * 16 + i, (p_95436_ & 255) / 16 * 16, getRight(b0) - i, 16, nativeimage);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public IntSet getSupportedGlyphs() {
      IntSet intset = new IntOpenHashSet();

      for(int i = 0; i < 65535; ++i) {
         if (this.sizes[i] != 0) {
            intset.add(i);
         }
      }

      return intset;
   }

   @Nullable
   private NativeImage loadTexture(ResourceLocation p_95438_) {
      try {
         Resource resource = this.resourceManager.getResource(p_95438_);

         NativeImage nativeimage;
         try {
            nativeimage = NativeImage.read(NativeImage.Format.RGBA, resource.getInputStream());
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

         return nativeimage;
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't load texture {}", p_95438_, ioexception);
         return null;
      }
   }

   private static int getLeft(byte p_95434_) {
      return p_95434_ >> 4 & 15;
   }

   private static int getRight(byte p_95441_) {
      return (p_95441_ & 15) + 1;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder implements GlyphProviderBuilder {
      private final ResourceLocation metadata;
      private final String texturePattern;

      public Builder(ResourceLocation p_95448_, String p_95449_) {
         this.metadata = p_95448_;
         this.texturePattern = p_95449_;
      }

      public static GlyphProviderBuilder fromJson(JsonObject p_95453_) {
         return new LegacyUnicodeBitmapsProvider.Builder(new ResourceLocation(GsonHelper.getAsString(p_95453_, "sizes")), getTemplate(p_95453_));
      }

      private static String getTemplate(JsonObject p_182570_) {
         String s = GsonHelper.getAsString(p_182570_, "template");

         try {
            String.format(s, "");
            return s;
         } catch (IllegalFormatException illegalformatexception) {
            throw new JsonParseException("Invalid legacy unicode template supplied, expected single '%s': " + s);
         }
      }

      @Nullable
      public GlyphProvider create(ResourceManager p_95451_) {
         try {
            Resource resource = Minecraft.getInstance().getResourceManager().getResource(this.metadata);

            LegacyUnicodeBitmapsProvider legacyunicodebitmapsprovider;
            try {
               byte[] abyte = new byte[65536];
               resource.getInputStream().read(abyte);
               legacyunicodebitmapsprovider = new LegacyUnicodeBitmapsProvider(p_95451_, abyte, this.texturePattern);
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

            return legacyunicodebitmapsprovider;
         } catch (IOException ioexception) {
            LegacyUnicodeBitmapsProvider.LOGGER.error("Cannot load {}, unicode glyphs will not render correctly", (Object)this.metadata);
            return null;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class Glyph implements RawGlyph {
      private final int width;
      private final int height;
      private final int sourceX;
      private final int sourceY;
      private final NativeImage source;

      Glyph(int p_95460_, int p_95461_, int p_95462_, int p_95463_, NativeImage p_95464_) {
         this.width = p_95462_;
         this.height = p_95463_;
         this.sourceX = p_95460_;
         this.sourceY = p_95461_;
         this.source = p_95464_;
      }

      public float getOversample() {
         return 2.0F;
      }

      public int getPixelWidth() {
         return this.width;
      }

      public int getPixelHeight() {
         return this.height;
      }

      public float getAdvance() {
         return (float)(this.width / 2 + 1);
      }

      public void upload(int p_95473_, int p_95474_) {
         this.source.upload(0, p_95473_, p_95474_, this.sourceX, this.sourceY, this.width, this.height, false, false);
      }

      public boolean isColored() {
         return this.source.format().components() > 1;
      }

      public float getShadowOffset() {
         return 0.5F;
      }

      public float getBoldOffset() {
         return 0.5F;
      }
   }
}