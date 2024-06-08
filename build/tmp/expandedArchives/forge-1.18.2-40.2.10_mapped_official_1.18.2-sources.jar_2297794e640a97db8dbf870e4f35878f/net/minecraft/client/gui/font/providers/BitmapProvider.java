package net.minecraft.client.gui.font.providers;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.RawGlyph;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class BitmapProvider implements GlyphProvider {
   static final Logger LOGGER = LogUtils.getLogger();
   private final NativeImage image;
   private final Int2ObjectMap<BitmapProvider.Glyph> glyphs;

   BitmapProvider(NativeImage p_95333_, Int2ObjectMap<BitmapProvider.Glyph> p_95334_) {
      this.image = p_95333_;
      this.glyphs = p_95334_;
   }

   public void close() {
      this.image.close();
   }

   @Nullable
   public RawGlyph getGlyph(int p_95341_) {
      return this.glyphs.get(p_95341_);
   }

   public IntSet getSupportedGlyphs() {
      return IntSets.unmodifiable(this.glyphs.keySet());
   }

   @OnlyIn(Dist.CLIENT)
   public static class Builder implements GlyphProviderBuilder {
      private final ResourceLocation texture;
      private final List<int[]> chars;
      private final int height;
      private final int ascent;

      public Builder(ResourceLocation p_95349_, int p_95350_, int p_95351_, List<int[]> p_95352_) {
         this.texture = new ResourceLocation(p_95349_.getNamespace(), "textures/" + p_95349_.getPath());
         this.chars = p_95352_;
         this.height = p_95350_;
         this.ascent = p_95351_;
      }

      public static BitmapProvider.Builder fromJson(JsonObject p_95356_) {
         int i = GsonHelper.getAsInt(p_95356_, "height", 8);
         int j = GsonHelper.getAsInt(p_95356_, "ascent");
         if (j > i) {
            throw new JsonParseException("Ascent " + j + " higher than height " + i);
         } else {
            List<int[]> list = Lists.newArrayList();
            JsonArray jsonarray = GsonHelper.getAsJsonArray(p_95356_, "chars");

            for(int k = 0; k < jsonarray.size(); ++k) {
               String s = GsonHelper.convertToString(jsonarray.get(k), "chars[" + k + "]");
               int[] aint = s.codePoints().toArray();
               if (k > 0) {
                  int l = ((int[])list.get(0)).length;
                  if (aint.length != l) {
                     throw new JsonParseException("Elements of chars have to be the same length (found: " + aint.length + ", expected: " + l + "), pad with space or \\u0000");
                  }
               }

               list.add(aint);
            }

            if (!list.isEmpty() && ((int[])list.get(0)).length != 0) {
               return new BitmapProvider.Builder(new ResourceLocation(GsonHelper.getAsString(p_95356_, "file")), i, j, list);
            } else {
               throw new JsonParseException("Expected to find data in chars, found none.");
            }
         }
      }

      @Nullable
      public GlyphProvider create(ResourceManager p_95354_) {
         try {
            Resource resource = p_95354_.getResource(this.texture);

            BitmapProvider bitmapprovider;
            try {
               NativeImage nativeimage = NativeImage.read(NativeImage.Format.RGBA, resource.getInputStream());
               int i = nativeimage.getWidth();
               int j = nativeimage.getHeight();
               int k = i / ((int[])this.chars.get(0)).length;
               int l = j / this.chars.size();
               float f = (float)this.height / (float)l;
               Int2ObjectMap<BitmapProvider.Glyph> int2objectmap = new Int2ObjectOpenHashMap<>();

               for(int i1 = 0; i1 < this.chars.size(); ++i1) {
                  int j1 = 0;

                  for(int k1 : this.chars.get(i1)) {
                     int l1 = j1++;
                     if (k1 != 0 && k1 != 32) {
                        int i2 = this.getActualGlyphWidth(nativeimage, k, l, l1, i1);
                        BitmapProvider.Glyph bitmapprovider$glyph = int2objectmap.put(k1, new BitmapProvider.Glyph(f, nativeimage, l1 * k, i1 * l, k, l, (int)(0.5D + (double)((float)i2 * f)) + 1, this.ascent));
                        if (bitmapprovider$glyph != null) {
                           BitmapProvider.LOGGER.warn("Codepoint '{}' declared multiple times in {}", Integer.toHexString(k1), this.texture);
                        }
                     }
                  }
               }

               bitmapprovider = new BitmapProvider(nativeimage, int2objectmap);
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

            return bitmapprovider;
         } catch (IOException ioexception) {
            throw new RuntimeException(ioexception.getMessage());
         }
      }

      private int getActualGlyphWidth(NativeImage p_95358_, int p_95359_, int p_95360_, int p_95361_, int p_95362_) {
         int i;
         for(i = p_95359_ - 1; i >= 0; --i) {
            int j = p_95361_ * p_95359_ + i;

            for(int k = 0; k < p_95360_; ++k) {
               int l = p_95362_ * p_95360_ + k;
               if (p_95358_.getLuminanceOrAlpha(j, l) != 0) {
                  return i + 1;
               }
            }
         }

         return i + 1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static final class Glyph implements RawGlyph {
      private final float scale;
      private final NativeImage image;
      private final int offsetX;
      private final int offsetY;
      private final int width;
      private final int height;
      private final int advance;
      private final int ascent;

      Glyph(float p_95372_, NativeImage p_95373_, int p_95374_, int p_95375_, int p_95376_, int p_95377_, int p_95378_, int p_95379_) {
         this.scale = p_95372_;
         this.image = p_95373_;
         this.offsetX = p_95374_;
         this.offsetY = p_95375_;
         this.width = p_95376_;
         this.height = p_95377_;
         this.advance = p_95378_;
         this.ascent = p_95379_;
      }

      public float getOversample() {
         return 1.0F / this.scale;
      }

      public int getPixelWidth() {
         return this.width;
      }

      public int getPixelHeight() {
         return this.height;
      }

      public float getAdvance() {
         return (float)this.advance;
      }

      public float getBearingY() {
         return RawGlyph.super.getBearingY() + 7.0F - (float)this.ascent;
      }

      public void upload(int p_95391_, int p_95392_) {
         this.image.upload(0, p_95391_, p_95392_, this.offsetX, this.offsetY, this.width, this.height, false, false);
      }

      public boolean isColored() {
         return this.image.format().components() > 1;
      }
   }
}