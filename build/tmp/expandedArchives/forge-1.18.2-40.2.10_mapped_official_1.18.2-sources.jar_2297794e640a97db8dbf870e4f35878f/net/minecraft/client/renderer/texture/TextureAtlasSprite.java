package net.minecraft.client.renderer.texture;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.renderer.SpriteCoordinateExpander;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class TextureAtlasSprite implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final TextureAtlas atlas;
   private final ResourceLocation name;
   final int width;
   final int height;
   protected final NativeImage[] mainImage;
   @Nullable
   private final TextureAtlasSprite.AnimatedTexture animatedTexture;
   private final int x;
   private final int y;
   private final float u0;
   private final float u1;
   private final float v0;
   private final float v1;

   protected TextureAtlasSprite(TextureAtlas p_118358_, TextureAtlasSprite.Info p_118359_, int p_118360_, int p_118361_, int p_118362_, int p_118363_, int p_118364_, NativeImage p_118365_) {
      this.atlas = p_118358_;
      this.width = p_118359_.width;
      this.height = p_118359_.height;
      this.name = p_118359_.name;
      this.x = p_118363_;
      this.y = p_118364_;
      this.u0 = (float)p_118363_ / (float)p_118361_;
      this.u1 = (float)(p_118363_ + this.width) / (float)p_118361_;
      this.v0 = (float)p_118364_ / (float)p_118362_;
      this.v1 = (float)(p_118364_ + this.height) / (float)p_118362_;
      this.animatedTexture = this.createTicker(p_118359_, p_118365_.getWidth(), p_118365_.getHeight(), p_118360_);

      try {
         try {
            this.mainImage = MipmapGenerator.generateMipLevels(p_118365_, p_118360_);
         } catch (Throwable throwable) {
            CrashReport crashreport1 = CrashReport.forThrowable(throwable, "Generating mipmaps for frame");
            CrashReportCategory crashreportcategory1 = crashreport1.addCategory("Frame being iterated");
            crashreportcategory1.setDetail("First frame", () -> {
               StringBuilder stringbuilder = new StringBuilder();
               if (stringbuilder.length() > 0) {
                  stringbuilder.append(", ");
               }

               stringbuilder.append(p_118365_.getWidth()).append("x").append(p_118365_.getHeight());
               return stringbuilder.toString();
            });
            throw new ReportedException(crashreport1);
         }
      } catch (Throwable throwable1) {
         CrashReport crashreport = CrashReport.forThrowable(throwable1, "Applying mipmap");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Sprite being mipmapped");
         crashreportcategory.setDetail("Sprite name", this.name::toString);
         crashreportcategory.setDetail("Sprite size", () -> {
            return this.width + " x " + this.height;
         });
         crashreportcategory.setDetail("Sprite frames", () -> {
            return this.getFrameCount() + " frames";
         });
         crashreportcategory.setDetail("Mipmap levels", p_118360_);
         throw new ReportedException(crashreport);
      }
   }

   public int getFrameCount() {
      return this.animatedTexture != null ? this.animatedTexture.frames.size() : 1;
   }

   @Nullable
   private TextureAtlasSprite.AnimatedTexture createTicker(TextureAtlasSprite.Info p_174730_, int p_174731_, int p_174732_, int p_174733_) {
      AnimationMetadataSection animationmetadatasection = p_174730_.metadata;
      int i = p_174731_ / animationmetadatasection.getFrameWidth(p_174730_.width);
      int j = p_174732_ / animationmetadatasection.getFrameHeight(p_174730_.height);
      int k = i * j;
      List<TextureAtlasSprite.FrameInfo> list = Lists.newArrayList();
      animationmetadatasection.forEachFrame((p_174739_, p_174740_) -> {
         list.add(new TextureAtlasSprite.FrameInfo(p_174739_, p_174740_));
      });
      if (list.isEmpty()) {
         for(int l = 0; l < k; ++l) {
            list.add(new TextureAtlasSprite.FrameInfo(l, animationmetadatasection.getDefaultFrameTime()));
         }
      } else {
         int i1 = 0;
         IntSet intset = new IntOpenHashSet();

         for(Iterator<TextureAtlasSprite.FrameInfo> iterator = list.iterator(); iterator.hasNext(); ++i1) {
            TextureAtlasSprite.FrameInfo textureatlassprite$frameinfo = iterator.next();
            boolean flag = true;
            if (textureatlassprite$frameinfo.time <= 0) {
               LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", this.name, i1, textureatlassprite$frameinfo.time);
               flag = false;
            }

            if (textureatlassprite$frameinfo.index < 0 || textureatlassprite$frameinfo.index >= k) {
               LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", this.name, i1, textureatlassprite$frameinfo.index);
               flag = false;
            }

            if (flag) {
               intset.add(textureatlassprite$frameinfo.index);
            } else {
               iterator.remove();
            }
         }

         int[] aint = IntStream.range(0, k).filter((p_174736_) -> {
            return !intset.contains(p_174736_);
         }).toArray();
         if (aint.length > 0) {
            LOGGER.warn("Unused frames in sprite {}: {}", this.name, Arrays.toString(aint));
         }
      }

      if (list.size() <= 1) {
         return null;
      } else {
         TextureAtlasSprite.InterpolationData textureatlassprite$interpolationdata = animationmetadatasection.isInterpolatedFrames() ? new TextureAtlasSprite.InterpolationData(p_174730_, p_174733_) : null;
         return new TextureAtlasSprite.AnimatedTexture(ImmutableList.copyOf(list), i, textureatlassprite$interpolationdata);
      }
   }

   void upload(int p_118376_, int p_118377_, NativeImage[] p_118378_) {
      for(int i = 0; i < this.mainImage.length; ++i) {
         if ((this.width >> i <= 0) || (this.height >> i <= 0)) break;
         p_118378_[i].upload(i, this.x >> i, this.y >> i, p_118376_ >> i, p_118377_ >> i, this.width >> i, this.height >> i, this.mainImage.length > 1, false);
      }

   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public float getU0() {
      return this.u0;
   }

   public float getU1() {
      return this.u1;
   }

   public float getU(double p_118368_) {
      float f = this.u1 - this.u0;
      return this.u0 + f * (float)p_118368_ / 16.0F;
   }

   public float getUOffset(float p_174728_) {
      float f = this.u1 - this.u0;
      return (p_174728_ - this.u0) / f * 16.0F;
   }

   public float getV0() {
      return this.v0;
   }

   public float getV1() {
      return this.v1;
   }

   public float getV(double p_118394_) {
      float f = this.v1 - this.v0;
      return this.v0 + f * (float)p_118394_ / 16.0F;
   }

   public float getVOffset(float p_174742_) {
      float f = this.v1 - this.v0;
      return (p_174742_ - this.v0) / f * 16.0F;
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public TextureAtlas atlas() {
      return this.atlas;
   }

   public IntStream getUniqueFrames() {
      return this.animatedTexture != null ? this.animatedTexture.getUniqueFrames() : IntStream.of(1);
   }

   public void close() {
      for(NativeImage nativeimage : this.mainImage) {
         if (nativeimage != null) {
            nativeimage.close();
         }
      }

      if (this.animatedTexture != null) {
         this.animatedTexture.close();
      }

   }

   public String toString() {
      return "TextureAtlasSprite{name='" + this.name + "', frameCount=" + this.getFrameCount() + ", x=" + this.x + ", y=" + this.y + ", height=" + this.height + ", width=" + this.width + ", u0=" + this.u0 + ", u1=" + this.u1 + ", v0=" + this.v0 + ", v1=" + this.v1 + "}";
   }

   public boolean isTransparent(int p_118372_, int p_118373_, int p_118374_) {
      int i = p_118373_;
      int j = p_118374_;
      if (this.animatedTexture != null) {
         i = p_118373_ + this.animatedTexture.getFrameX(p_118372_) * this.width;
         j = p_118374_ + this.animatedTexture.getFrameY(p_118372_) * this.height;
      }

      return (this.mainImage[0].getPixelRGBA(i, j) >> 24 & 255) == 0;
   }

   public void uploadFirstFrame() {
      if (this.animatedTexture != null) {
         this.animatedTexture.uploadFirstFrame();
      } else {
         this.upload(0, 0, this.mainImage);
      }

   }

   private float atlasSize() {
      float f = (float)this.width / (this.u1 - this.u0);
      float f1 = (float)this.height / (this.v1 - this.v0);
      return Math.max(f1, f);
   }

   public float uvShrinkRatio() {
      return 4.0F / this.atlasSize();
   }

   @Nullable
   public Tickable getAnimationTicker() {
      return this.animatedTexture;
   }

   public VertexConsumer wrap(VertexConsumer p_118382_) {
      return new SpriteCoordinateExpander(p_118382_, this);
   }

   @OnlyIn(Dist.CLIENT)
   class AnimatedTexture implements Tickable, AutoCloseable {
      int frame;
      int subFrame;
      final List<TextureAtlasSprite.FrameInfo> frames;
      private final int frameRowSize;
      @Nullable
      private final TextureAtlasSprite.InterpolationData interpolationData;

      AnimatedTexture(List<TextureAtlasSprite.FrameInfo> p_174755_, @Nullable int p_174756_, TextureAtlasSprite.InterpolationData p_174757_) {
         this.frames = p_174755_;
         this.frameRowSize = p_174756_;
         this.interpolationData = p_174757_;
      }

      int getFrameX(int p_174760_) {
         return p_174760_ % this.frameRowSize;
      }

      int getFrameY(int p_174765_) {
         return p_174765_ / this.frameRowSize;
      }

      private void uploadFrame(int p_174768_) {
         int i = this.getFrameX(p_174768_) * TextureAtlasSprite.this.width;
         int j = this.getFrameY(p_174768_) * TextureAtlasSprite.this.height;
         TextureAtlasSprite.this.upload(i, j, TextureAtlasSprite.this.mainImage);
      }

      public void close() {
         if (this.interpolationData != null) {
            this.interpolationData.close();
         }

      }

      public void tick() {
         ++this.subFrame;
         TextureAtlasSprite.FrameInfo textureatlassprite$frameinfo = this.frames.get(this.frame);
         if (this.subFrame >= textureatlassprite$frameinfo.time) {
            int i = textureatlassprite$frameinfo.index;
            this.frame = (this.frame + 1) % this.frames.size();
            this.subFrame = 0;
            int j = (this.frames.get(this.frame)).index;
            if (i != j) {
               this.uploadFrame(j);
            }
         } else if (this.interpolationData != null) {
            if (!RenderSystem.isOnRenderThread()) {
               RenderSystem.recordRenderCall(() -> {
                  this.interpolationData.uploadInterpolatedFrame(this);
               });
            } else {
               this.interpolationData.uploadInterpolatedFrame(this);
            }
         }

      }

      public void uploadFirstFrame() {
         this.uploadFrame((this.frames.get(0)).index);
      }

      public IntStream getUniqueFrames() {
         return this.frames.stream().mapToInt((p_174762_) -> {
            return p_174762_.index;
         }).distinct();
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class FrameInfo {
      final int index;
      final int time;

      FrameInfo(int p_174774_, int p_174775_) {
         this.index = p_174774_;
         this.time = p_174775_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Info {
      final ResourceLocation name;
      final int width;
      final int height;
      final AnimationMetadataSection metadata;

      public Info(ResourceLocation p_118427_, int p_118428_, int p_118429_, AnimationMetadataSection p_118430_) {
         this.name = p_118427_;
         this.width = p_118428_;
         this.height = p_118429_;
         this.metadata = p_118430_;
      }

      public ResourceLocation name() {
         return this.name;
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }
   }

   @OnlyIn(Dist.CLIENT)
   final class InterpolationData implements AutoCloseable {
      private final NativeImage[] activeFrame;

      InterpolationData(TextureAtlasSprite.Info p_118446_, int p_118447_) {
         this.activeFrame = new NativeImage[p_118447_ + 1];

         for(int i = 0; i < this.activeFrame.length; ++i) {
            int j = p_118446_.width >> i;
            int k = p_118446_.height >> i;
            if (this.activeFrame[i] == null) {
               // Forge: guard against invalid texture size, because we allow generating mipmaps regardless of texture sizes
               this.activeFrame[i] = new NativeImage(Math.max(1, j), Math.max(1, k), false);
            }
         }

      }

      void uploadInterpolatedFrame(TextureAtlasSprite.AnimatedTexture p_174777_) {
         TextureAtlasSprite.FrameInfo textureatlassprite$frameinfo = p_174777_.frames.get(p_174777_.frame);
         double d0 = 1.0D - (double)p_174777_.subFrame / (double)textureatlassprite$frameinfo.time;
         int i = textureatlassprite$frameinfo.index;
         int j = (p_174777_.frames.get((p_174777_.frame + 1) % p_174777_.frames.size())).index;
         if (i != j) {
            for(int k = 0; k < this.activeFrame.length; ++k) {
               int l = TextureAtlasSprite.this.width >> k;
               int i1 = TextureAtlasSprite.this.height >> k;
               // Forge: guard against invalid texture size, because we allow generating mipmaps regardless of texture sizes
               if (l == 0 || i1 == 0) continue;

               for(int j1 = 0; j1 < i1; ++j1) {
                  for(int k1 = 0; k1 < l; ++k1) {
                     int l1 = this.getPixel(p_174777_, i, k, k1, j1);
                     int i2 = this.getPixel(p_174777_, j, k, k1, j1);
                     int j2 = this.mix(d0, l1 >> 16 & 255, i2 >> 16 & 255);
                     int k2 = this.mix(d0, l1 >> 8 & 255, i2 >> 8 & 255);
                     int l2 = this.mix(d0, l1 & 255, i2 & 255);
                     this.activeFrame[k].setPixelRGBA(k1, j1, l1 & -16777216 | j2 << 16 | k2 << 8 | l2);
                  }
               }
            }

            TextureAtlasSprite.this.upload(0, 0, this.activeFrame);
         }

      }

      private int getPixel(TextureAtlasSprite.AnimatedTexture p_174779_, int p_174780_, int p_174781_, int p_174782_, int p_174783_) {
         return TextureAtlasSprite.this.mainImage[p_174781_].getPixelRGBA(p_174782_ + (p_174779_.getFrameX(p_174780_) * TextureAtlasSprite.this.width >> p_174781_), p_174783_ + (p_174779_.getFrameY(p_174780_) * TextureAtlasSprite.this.height >> p_174781_));
      }

      private int mix(double p_118455_, int p_118456_, int p_118457_) {
         return (int)(p_118455_ * (double)p_118456_ + (1.0D - p_118455_) * (double)p_118457_);
      }

      public void close() {
         for(NativeImage nativeimage : this.activeFrame) {
            if (nativeimage != null) {
               nativeimage.close();
            }
         }

      }
   }

   // Forge Start
   public int getPixelRGBA(int frameIndex, int x, int y) {
       if (this.animatedTexture != null) {
           x += this.animatedTexture.getFrameX(frameIndex) * this.width;
           y += this.animatedTexture.getFrameY(frameIndex) * this.height;
       }

       return this.mainImage[0].getPixelRGBA(x, y);
   }
}
