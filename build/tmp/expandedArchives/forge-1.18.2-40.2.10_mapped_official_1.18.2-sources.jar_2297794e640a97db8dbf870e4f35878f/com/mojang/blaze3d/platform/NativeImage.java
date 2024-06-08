package com.mojang.blaze3d.platform;

import com.google.common.base.Charsets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public final class NativeImage implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int OFFSET_A = 24;
   private static final int OFFSET_B = 16;
   private static final int OFFSET_G = 8;
   private static final int OFFSET_R = 0;
   private static final Set<StandardOpenOption> OPEN_OPTIONS = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
   private final NativeImage.Format format;
   private final int width;
   private final int height;
   private final boolean useStbFree;
   private long pixels;
   private final long size;

   public NativeImage(int p_84968_, int p_84969_, boolean p_84970_) {
      this(NativeImage.Format.RGBA, p_84968_, p_84969_, p_84970_);
   }

   public NativeImage(NativeImage.Format p_84972_, int p_84973_, int p_84974_, boolean p_84975_) {
      if (p_84973_ > 0 && p_84974_ > 0) {
         this.format = p_84972_;
         this.width = p_84973_;
         this.height = p_84974_;
         this.size = (long)p_84973_ * (long)p_84974_ * (long)p_84972_.components();
         this.useStbFree = false;
         if (p_84975_) {
            this.pixels = MemoryUtil.nmemCalloc(1L, this.size);
         } else {
            this.pixels = MemoryUtil.nmemAlloc(this.size);
         }

      } else {
         throw new IllegalArgumentException("Invalid texture size: " + p_84973_ + "x" + p_84974_);
      }
   }

   private NativeImage(NativeImage.Format p_84977_, int p_84978_, int p_84979_, boolean p_84980_, long p_84981_) {
      if (p_84978_ > 0 && p_84979_ > 0) {
         this.format = p_84977_;
         this.width = p_84978_;
         this.height = p_84979_;
         this.useStbFree = p_84980_;
         this.pixels = p_84981_;
         this.size = (long)p_84978_ * (long)p_84979_ * (long)p_84977_.components();
      } else {
         throw new IllegalArgumentException("Invalid texture size: " + p_84978_ + "x" + p_84979_);
      }
   }

   public String toString() {
      return "NativeImage[" + this.format + " " + this.width + "x" + this.height + "@" + this.pixels + (this.useStbFree ? "S" : "N") + "]";
   }

   private boolean isOutsideBounds(int p_166423_, int p_166424_) {
      return p_166423_ < 0 || p_166423_ >= this.width || p_166424_ < 0 || p_166424_ >= this.height;
   }

   public static NativeImage read(InputStream p_85059_) throws IOException {
      return read(NativeImage.Format.RGBA, p_85059_);
   }

   public static NativeImage read(@Nullable NativeImage.Format p_85049_, InputStream p_85050_) throws IOException {
      ByteBuffer bytebuffer = null;

      NativeImage nativeimage;
      try {
         bytebuffer = TextureUtil.readResource(p_85050_);
         bytebuffer.rewind();
         nativeimage = read(p_85049_, bytebuffer);
      } finally {
         MemoryUtil.memFree(bytebuffer);
         IOUtils.closeQuietly(p_85050_);
      }

      return nativeimage;
   }

   public static NativeImage read(ByteBuffer p_85063_) throws IOException {
      return read(NativeImage.Format.RGBA, p_85063_);
   }

   public static NativeImage read(@Nullable NativeImage.Format p_85052_, ByteBuffer p_85053_) throws IOException {
      if (p_85052_ != null && !p_85052_.supportedByStb()) {
         throw new UnsupportedOperationException("Don't know how to read format " + p_85052_);
      } else if (MemoryUtil.memAddress(p_85053_) == 0L) {
         throw new IllegalArgumentException("Invalid buffer");
      } else {
         MemoryStack memorystack = MemoryStack.stackPush();

         NativeImage nativeimage;
         try {
            IntBuffer intbuffer = memorystack.mallocInt(1);
            IntBuffer intbuffer1 = memorystack.mallocInt(1);
            IntBuffer intbuffer2 = memorystack.mallocInt(1);
            ByteBuffer bytebuffer = STBImage.stbi_load_from_memory(p_85053_, intbuffer, intbuffer1, intbuffer2, p_85052_ == null ? 0 : p_85052_.components);
            if (bytebuffer == null) {
               throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            }

            nativeimage = new NativeImage(p_85052_ == null ? NativeImage.Format.getStbFormat(intbuffer2.get(0)) : p_85052_, intbuffer.get(0), intbuffer1.get(0), true, MemoryUtil.memAddress(bytebuffer));
         } catch (Throwable throwable1) {
            if (memorystack != null) {
               try {
                  memorystack.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }
            }

            throw throwable1;
         }

         if (memorystack != null) {
            memorystack.close();
         }

         return nativeimage;
      }
   }

   private static void setFilter(boolean p_85082_, boolean p_85083_) {
      RenderSystem.assertOnRenderThreadOrInit();
      if (p_85082_) {
         GlStateManager._texParameter(3553, 10241, p_85083_ ? 9987 : 9729);
         GlStateManager._texParameter(3553, 10240, 9729);
      } else {
         GlStateManager._texParameter(3553, 10241, p_85083_ ? 9986 : 9728);
         GlStateManager._texParameter(3553, 10240, 9728);
      }

   }

   private void checkAllocated() {
      if (this.pixels == 0L) {
         throw new IllegalStateException("Image is not allocated.");
      }
   }

   public void close() {
      if (this.pixels != 0L) {
         if (this.useStbFree) {
            STBImage.nstbi_image_free(this.pixels);
         } else {
            MemoryUtil.nmemFree(this.pixels);
         }
      }

      this.pixels = 0L;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public NativeImage.Format format() {
      return this.format;
   }

   public int getPixelRGBA(int p_84986_, int p_84987_) {
      if (this.format != NativeImage.Format.RGBA) {
         throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.format));
      } else if (this.isOutsideBounds(p_84986_, p_84987_)) {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", p_84986_, p_84987_, this.width, this.height));
      } else {
         this.checkAllocated();
         long i = ((long)p_84986_ + (long)p_84987_ * (long)this.width) * 4L;
         return MemoryUtil.memGetInt(this.pixels + i);
      }
   }

   public void setPixelRGBA(int p_84989_, int p_84990_, int p_84991_) {
      if (this.format != NativeImage.Format.RGBA) {
         throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.format));
      } else if (this.isOutsideBounds(p_84989_, p_84990_)) {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", p_84989_, p_84990_, this.width, this.height));
      } else {
         this.checkAllocated();
         long i = ((long)p_84989_ + (long)p_84990_ * (long)this.width) * 4L;
         MemoryUtil.memPutInt(this.pixels + i, p_84991_);
      }
   }

   public void setPixelLuminance(int p_166403_, int p_166404_, byte p_166405_) {
      RenderSystem.assertOnRenderThread();
      if (!this.format.hasLuminance()) {
         throw new IllegalArgumentException(String.format("setPixelLuminance only works on image with luminance; have %s", this.format));
      } else if (this.isOutsideBounds(p_166403_, p_166404_)) {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", p_166403_, p_166404_, this.width, this.height));
      } else {
         this.checkAllocated();
         long i = ((long)p_166403_ + (long)p_166404_ * (long)this.width) * (long)this.format.components() + (long)(this.format.luminanceOffset() / 8);
         MemoryUtil.memPutByte(this.pixels + i, p_166405_);
      }
   }

   public byte getRedOrLuminance(int p_166409_, int p_166410_) {
      RenderSystem.assertOnRenderThread();
      if (!this.format.hasLuminanceOrRed()) {
         throw new IllegalArgumentException(String.format("no red or luminance in %s", this.format));
      } else if (this.isOutsideBounds(p_166409_, p_166410_)) {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", p_166409_, p_166410_, this.width, this.height));
      } else {
         int i = (p_166409_ + p_166410_ * this.width) * this.format.components() + this.format.luminanceOrRedOffset() / 8;
         return MemoryUtil.memGetByte(this.pixels + (long)i);
      }
   }

   public byte getGreenOrLuminance(int p_166416_, int p_166417_) {
      RenderSystem.assertOnRenderThread();
      if (!this.format.hasLuminanceOrGreen()) {
         throw new IllegalArgumentException(String.format("no green or luminance in %s", this.format));
      } else if (this.isOutsideBounds(p_166416_, p_166417_)) {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", p_166416_, p_166417_, this.width, this.height));
      } else {
         int i = (p_166416_ + p_166417_ * this.width) * this.format.components() + this.format.luminanceOrGreenOffset() / 8;
         return MemoryUtil.memGetByte(this.pixels + (long)i);
      }
   }

   public byte getBlueOrLuminance(int p_166419_, int p_166420_) {
      RenderSystem.assertOnRenderThread();
      if (!this.format.hasLuminanceOrBlue()) {
         throw new IllegalArgumentException(String.format("no blue or luminance in %s", this.format));
      } else if (this.isOutsideBounds(p_166419_, p_166420_)) {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", p_166419_, p_166420_, this.width, this.height));
      } else {
         int i = (p_166419_ + p_166420_ * this.width) * this.format.components() + this.format.luminanceOrBlueOffset() / 8;
         return MemoryUtil.memGetByte(this.pixels + (long)i);
      }
   }

   public byte getLuminanceOrAlpha(int p_85088_, int p_85089_) {
      if (!this.format.hasLuminanceOrAlpha()) {
         throw new IllegalArgumentException(String.format("no luminance or alpha in %s", this.format));
      } else if (this.isOutsideBounds(p_85088_, p_85089_)) {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", p_85088_, p_85089_, this.width, this.height));
      } else {
         int i = (p_85088_ + p_85089_ * this.width) * this.format.components() + this.format.luminanceOrAlphaOffset() / 8;
         return MemoryUtil.memGetByte(this.pixels + (long)i);
      }
   }

   public void blendPixel(int p_166412_, int p_166413_, int p_166414_) {
      if (this.format != NativeImage.Format.RGBA) {
         throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
      } else {
         int i = this.getPixelRGBA(p_166412_, p_166413_);
         float f = (float)getA(p_166414_) / 255.0F;
         float f1 = (float)getB(p_166414_) / 255.0F;
         float f2 = (float)getG(p_166414_) / 255.0F;
         float f3 = (float)getR(p_166414_) / 255.0F;
         float f4 = (float)getA(i) / 255.0F;
         float f5 = (float)getB(i) / 255.0F;
         float f6 = (float)getG(i) / 255.0F;
         float f7 = (float)getR(i) / 255.0F;
         float f8 = 1.0F - f;
         float f9 = f * f + f4 * f8;
         float f10 = f1 * f + f5 * f8;
         float f11 = f2 * f + f6 * f8;
         float f12 = f3 * f + f7 * f8;
         if (f9 > 1.0F) {
            f9 = 1.0F;
         }

         if (f10 > 1.0F) {
            f10 = 1.0F;
         }

         if (f11 > 1.0F) {
            f11 = 1.0F;
         }

         if (f12 > 1.0F) {
            f12 = 1.0F;
         }

         int j = (int)(f9 * 255.0F);
         int k = (int)(f10 * 255.0F);
         int l = (int)(f11 * 255.0F);
         int i1 = (int)(f12 * 255.0F);
         this.setPixelRGBA(p_166412_, p_166413_, combine(j, k, l, i1));
      }
   }

   /** @deprecated */
   @Deprecated
   public int[] makePixelArray() {
      if (this.format != NativeImage.Format.RGBA) {
         throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
      } else {
         this.checkAllocated();
         int[] aint = new int[this.getWidth() * this.getHeight()];

         for(int i = 0; i < this.getHeight(); ++i) {
            for(int j = 0; j < this.getWidth(); ++j) {
               int k = this.getPixelRGBA(j, i);
               int l = getA(k);
               int i1 = getB(k);
               int j1 = getG(k);
               int k1 = getR(k);
               int l1 = l << 24 | k1 << 16 | j1 << 8 | i1;
               aint[j + i * this.getWidth()] = l1;
            }
         }

         return aint;
      }
   }

   public void upload(int p_85041_, int p_85042_, int p_85043_, boolean p_85044_) {
      this.upload(p_85041_, p_85042_, p_85043_, 0, 0, this.width, this.height, false, p_85044_);
   }

   public void upload(int p_85004_, int p_85005_, int p_85006_, int p_85007_, int p_85008_, int p_85009_, int p_85010_, boolean p_85011_, boolean p_85012_) {
      this.upload(p_85004_, p_85005_, p_85006_, p_85007_, p_85008_, p_85009_, p_85010_, false, false, p_85011_, p_85012_);
   }

   public void upload(int p_85014_, int p_85015_, int p_85016_, int p_85017_, int p_85018_, int p_85019_, int p_85020_, boolean p_85021_, boolean p_85022_, boolean p_85023_, boolean p_85024_) {
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> {
            this._upload(p_85014_, p_85015_, p_85016_, p_85017_, p_85018_, p_85019_, p_85020_, p_85021_, p_85022_, p_85023_, p_85024_);
         });
      } else {
         this._upload(p_85014_, p_85015_, p_85016_, p_85017_, p_85018_, p_85019_, p_85020_, p_85021_, p_85022_, p_85023_, p_85024_);
      }

   }

   private void _upload(int p_85091_, int p_85092_, int p_85093_, int p_85094_, int p_85095_, int p_85096_, int p_85097_, boolean p_85098_, boolean p_85099_, boolean p_85100_, boolean p_85101_) {
      RenderSystem.assertOnRenderThreadOrInit();
      this.checkAllocated();
      setFilter(p_85098_, p_85100_);
      if (p_85096_ == this.getWidth()) {
         GlStateManager._pixelStore(3314, 0);
      } else {
         GlStateManager._pixelStore(3314, this.getWidth());
      }

      GlStateManager._pixelStore(3316, p_85094_);
      GlStateManager._pixelStore(3315, p_85095_);
      this.format.setUnpackPixelStoreState();
      GlStateManager._texSubImage2D(3553, p_85091_, p_85092_, p_85093_, p_85096_, p_85097_, this.format.glFormat(), 5121, this.pixels);
      if (p_85099_) {
         GlStateManager._texParameter(3553, 10242, 33071);
         GlStateManager._texParameter(3553, 10243, 33071);
      }

      if (p_85101_) {
         this.close();
      }

   }

   public void downloadTexture(int p_85046_, boolean p_85047_) {
      RenderSystem.assertOnRenderThread();
      this.checkAllocated();
      this.format.setPackPixelStoreState();
      GlStateManager._getTexImage(3553, p_85046_, this.format.glFormat(), 5121, this.pixels);
      if (p_85047_ && this.format.hasAlpha()) {
         for(int i = 0; i < this.getHeight(); ++i) {
            for(int j = 0; j < this.getWidth(); ++j) {
               this.setPixelRGBA(j, i, this.getPixelRGBA(j, i) | 255 << this.format.alphaOffset());
            }
         }
      }

   }

   public void downloadDepthBuffer(float p_166401_) {
      RenderSystem.assertOnRenderThread();
      if (this.format.components() != 1) {
         throw new IllegalStateException("Depth buffer must be stored in NativeImage with 1 component.");
      } else {
         this.checkAllocated();
         this.format.setPackPixelStoreState();
         GlStateManager._readPixels(0, 0, this.width, this.height, 6402, 5121, this.pixels);
      }
   }

   public void drawPixels() {
      RenderSystem.assertOnRenderThread();
      this.format.setUnpackPixelStoreState();
      GlStateManager._glDrawPixels(this.width, this.height, this.format.glFormat(), 5121, this.pixels);
   }

   public void writeToFile(String p_166407_) throws IOException {
      this.writeToFile(FileSystems.getDefault().getPath(p_166407_));
   }

   public void writeToFile(File p_85057_) throws IOException {
      this.writeToFile(p_85057_.toPath());
   }

   public void copyFromFont(STBTTFontinfo p_85069_, int p_85070_, int p_85071_, int p_85072_, float p_85073_, float p_85074_, float p_85075_, float p_85076_, int p_85077_, int p_85078_) {
      if (p_85077_ >= 0 && p_85077_ + p_85071_ <= this.getWidth() && p_85078_ >= 0 && p_85078_ + p_85072_ <= this.getHeight()) {
         if (this.format.components() != 1) {
            throw new IllegalArgumentException("Can only write fonts into 1-component images.");
         } else {
            STBTruetype.nstbtt_MakeGlyphBitmapSubpixel(p_85069_.address(), this.pixels + (long)p_85077_ + (long)(p_85078_ * this.getWidth()), p_85071_, p_85072_, this.getWidth(), p_85073_, p_85074_, p_85075_, p_85076_, p_85070_);
         }
      } else {
         throw new IllegalArgumentException(String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", p_85077_, p_85078_, p_85071_, p_85072_, this.getWidth(), this.getHeight()));
      }
   }

   public void writeToFile(Path p_85067_) throws IOException {
      if (!this.format.supportedByStb()) {
         throw new UnsupportedOperationException("Don't know how to write format " + this.format);
      } else {
         this.checkAllocated();
         WritableByteChannel writablebytechannel = Files.newByteChannel(p_85067_, OPEN_OPTIONS);

         try {
            if (!this.writeToChannel(writablebytechannel)) {
               throw new IOException("Could not write image to the PNG file \"" + p_85067_.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
            }
         } catch (Throwable throwable1) {
            if (writablebytechannel != null) {
               try {
                  writablebytechannel.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }
            }

            throw throwable1;
         }

         if (writablebytechannel != null) {
            writablebytechannel.close();
         }

      }
   }

   public byte[] asByteArray() throws IOException {
      ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();

      byte[] abyte;
      try {
         WritableByteChannel writablebytechannel = Channels.newChannel(bytearrayoutputstream);

         try {
            if (!this.writeToChannel(writablebytechannel)) {
               throw new IOException("Could not write image to byte array: " + STBImage.stbi_failure_reason());
            }

            abyte = bytearrayoutputstream.toByteArray();
         } catch (Throwable throwable2) {
            if (writablebytechannel != null) {
               try {
                  writablebytechannel.close();
               } catch (Throwable throwable1) {
                  throwable2.addSuppressed(throwable1);
               }
            }

            throw throwable2;
         }

         if (writablebytechannel != null) {
            writablebytechannel.close();
         }
      } catch (Throwable throwable3) {
         try {
            bytearrayoutputstream.close();
         } catch (Throwable throwable) {
            throwable3.addSuppressed(throwable);
         }

         throw throwable3;
      }

      bytearrayoutputstream.close();
      return abyte;
   }

   private boolean writeToChannel(WritableByteChannel p_85065_) throws IOException {
      NativeImage.WriteCallback nativeimage$writecallback = new NativeImage.WriteCallback(p_85065_);

      boolean flag;
      try {
         int i = Math.min(this.getHeight(), Integer.MAX_VALUE / this.getWidth() / this.format.components());
         if (i < this.getHeight()) {
            LOGGER.warn("Dropping image height from {} to {} to fit the size into 32-bit signed int", this.getHeight(), i);
         }

         if (STBImageWrite.nstbi_write_png_to_func(nativeimage$writecallback.address(), 0L, this.getWidth(), i, this.format.components(), this.pixels, 0) != 0) {
            nativeimage$writecallback.throwIfException();
            return true;
         }

         flag = false;
      } finally {
         nativeimage$writecallback.free();
      }

      return flag;
   }

   public void copyFrom(NativeImage p_85055_) {
      if (p_85055_.format() != this.format) {
         throw new UnsupportedOperationException("Image formats don't match.");
      } else {
         int i = this.format.components();
         this.checkAllocated();
         p_85055_.checkAllocated();
         if (this.width == p_85055_.width) {
            MemoryUtil.memCopy(p_85055_.pixels, this.pixels, Math.min(this.size, p_85055_.size));
         } else {
            int j = Math.min(this.getWidth(), p_85055_.getWidth());
            int k = Math.min(this.getHeight(), p_85055_.getHeight());

            for(int l = 0; l < k; ++l) {
               int i1 = l * p_85055_.getWidth() * i;
               int j1 = l * this.getWidth() * i;
               MemoryUtil.memCopy(p_85055_.pixels + (long)i1, this.pixels + (long)j1, (long)j);
            }
         }

      }
   }

   public void fillRect(int p_84998_, int p_84999_, int p_85000_, int p_85001_, int p_85002_) {
      for(int i = p_84999_; i < p_84999_ + p_85001_; ++i) {
         for(int j = p_84998_; j < p_84998_ + p_85000_; ++j) {
            this.setPixelRGBA(j, i, p_85002_);
         }
      }

   }

   public void copyRect(int p_85026_, int p_85027_, int p_85028_, int p_85029_, int p_85030_, int p_85031_, boolean p_85032_, boolean p_85033_) {
      for(int i = 0; i < p_85031_; ++i) {
         for(int j = 0; j < p_85030_; ++j) {
            int k = p_85032_ ? p_85030_ - 1 - j : j;
            int l = p_85033_ ? p_85031_ - 1 - i : i;
            int i1 = this.getPixelRGBA(p_85026_ + j, p_85027_ + i);
            this.setPixelRGBA(p_85026_ + p_85028_ + k, p_85027_ + p_85029_ + l, i1);
         }
      }

   }

   public void flipY() {
      this.checkAllocated();
      MemoryStack memorystack = MemoryStack.stackPush();

      try {
         int i = this.format.components();
         int j = this.getWidth() * i;
         long k = memorystack.nmalloc(j);

         for(int l = 0; l < this.getHeight() / 2; ++l) {
            int i1 = l * this.getWidth() * i;
            int j1 = (this.getHeight() - 1 - l) * this.getWidth() * i;
            MemoryUtil.memCopy(this.pixels + (long)i1, k, (long)j);
            MemoryUtil.memCopy(this.pixels + (long)j1, this.pixels + (long)i1, (long)j);
            MemoryUtil.memCopy(k, this.pixels + (long)j1, (long)j);
         }
      } catch (Throwable throwable1) {
         if (memorystack != null) {
            try {
               memorystack.close();
            } catch (Throwable throwable) {
               throwable1.addSuppressed(throwable);
            }
         }

         throw throwable1;
      }

      if (memorystack != null) {
         memorystack.close();
      }

   }

   public void resizeSubRectTo(int p_85035_, int p_85036_, int p_85037_, int p_85038_, NativeImage p_85039_) {
      this.checkAllocated();
      if (p_85039_.format() != this.format) {
         throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
      } else {
         int i = this.format.components();
         STBImageResize.nstbir_resize_uint8(this.pixels + (long)((p_85035_ + p_85036_ * this.getWidth()) * i), p_85037_, p_85038_, this.getWidth() * i, p_85039_.pixels, p_85039_.getWidth(), p_85039_.getHeight(), 0, i);
      }
   }

   public void untrack() {
      DebugMemoryUntracker.untrack(this.pixels);
   }

   public static NativeImage fromBase64(String p_85061_) throws IOException {
      byte[] abyte = Base64.getDecoder().decode(p_85061_.replaceAll("\n", "").getBytes(Charsets.UTF_8));
      MemoryStack memorystack = MemoryStack.stackPush();

      NativeImage nativeimage;
      try {
         ByteBuffer bytebuffer = memorystack.malloc(abyte.length);
         bytebuffer.put(abyte);
         bytebuffer.rewind();
         nativeimage = read(bytebuffer);
      } catch (Throwable throwable1) {
         if (memorystack != null) {
            try {
               memorystack.close();
            } catch (Throwable throwable) {
               throwable1.addSuppressed(throwable);
            }
         }

         throw throwable1;
      }

      if (memorystack != null) {
         memorystack.close();
      }

      return nativeimage;
   }

   public static int getA(int p_84984_) {
      return p_84984_ >> 24 & 255;
   }

   public static int getR(int p_85086_) {
      return p_85086_ >> 0 & 255;
   }

   public static int getG(int p_85104_) {
      return p_85104_ >> 8 & 255;
   }

   public static int getB(int p_85120_) {
      return p_85120_ >> 16 & 255;
   }

   public static int combine(int p_84993_, int p_84994_, int p_84995_, int p_84996_) {
      return (p_84993_ & 255) << 24 | (p_84994_ & 255) << 16 | (p_84995_ & 255) << 8 | (p_84996_ & 255) << 0;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Format {
      RGBA(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
      RGB(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
      LUMINANCE_ALPHA(2, 33319, false, false, false, true, true, 255, 255, 255, 0, 8, true),
      LUMINANCE(1, 6403, false, false, false, true, false, 0, 0, 0, 0, 255, true);

      final int components;
      private final int glFormat;
      private final boolean hasRed;
      private final boolean hasGreen;
      private final boolean hasBlue;
      private final boolean hasLuminance;
      private final boolean hasAlpha;
      private final int redOffset;
      private final int greenOffset;
      private final int blueOffset;
      private final int luminanceOffset;
      private final int alphaOffset;
      private final boolean supportedByStb;

      private Format(int p_85148_, int p_85149_, boolean p_85150_, boolean p_85151_, boolean p_85152_, boolean p_85153_, boolean p_85154_, int p_85155_, int p_85156_, int p_85157_, int p_85158_, int p_85159_, boolean p_85160_) {
         this.components = p_85148_;
         this.glFormat = p_85149_;
         this.hasRed = p_85150_;
         this.hasGreen = p_85151_;
         this.hasBlue = p_85152_;
         this.hasLuminance = p_85153_;
         this.hasAlpha = p_85154_;
         this.redOffset = p_85155_;
         this.greenOffset = p_85156_;
         this.blueOffset = p_85157_;
         this.luminanceOffset = p_85158_;
         this.alphaOffset = p_85159_;
         this.supportedByStb = p_85160_;
      }

      public int components() {
         return this.components;
      }

      public void setPackPixelStoreState() {
         RenderSystem.assertOnRenderThread();
         GlStateManager._pixelStore(3333, this.components());
      }

      public void setUnpackPixelStoreState() {
         RenderSystem.assertOnRenderThreadOrInit();
         GlStateManager._pixelStore(3317, this.components());
      }

      public int glFormat() {
         return this.glFormat;
      }

      public boolean hasRed() {
         return this.hasRed;
      }

      public boolean hasGreen() {
         return this.hasGreen;
      }

      public boolean hasBlue() {
         return this.hasBlue;
      }

      public boolean hasLuminance() {
         return this.hasLuminance;
      }

      public boolean hasAlpha() {
         return this.hasAlpha;
      }

      public int redOffset() {
         return this.redOffset;
      }

      public int greenOffset() {
         return this.greenOffset;
      }

      public int blueOffset() {
         return this.blueOffset;
      }

      public int luminanceOffset() {
         return this.luminanceOffset;
      }

      public int alphaOffset() {
         return this.alphaOffset;
      }

      public boolean hasLuminanceOrRed() {
         return this.hasLuminance || this.hasRed;
      }

      public boolean hasLuminanceOrGreen() {
         return this.hasLuminance || this.hasGreen;
      }

      public boolean hasLuminanceOrBlue() {
         return this.hasLuminance || this.hasBlue;
      }

      public boolean hasLuminanceOrAlpha() {
         return this.hasLuminance || this.hasAlpha;
      }

      public int luminanceOrRedOffset() {
         return this.hasLuminance ? this.luminanceOffset : this.redOffset;
      }

      public int luminanceOrGreenOffset() {
         return this.hasLuminance ? this.luminanceOffset : this.greenOffset;
      }

      public int luminanceOrBlueOffset() {
         return this.hasLuminance ? this.luminanceOffset : this.blueOffset;
      }

      public int luminanceOrAlphaOffset() {
         return this.hasLuminance ? this.luminanceOffset : this.alphaOffset;
      }

      public boolean supportedByStb() {
         return this.supportedByStb;
      }

      static NativeImage.Format getStbFormat(int p_85168_) {
         switch(p_85168_) {
         case 1:
            return LUMINANCE;
         case 2:
            return LUMINANCE_ALPHA;
         case 3:
            return RGB;
         case 4:
         default:
            return RGBA;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum InternalGlFormat {
      RGBA(6408),
      RGB(6407),
      RG(33319),
      RED(6403);

      private final int glFormat;

      private InternalGlFormat(int p_85190_) {
         this.glFormat = p_85190_;
      }

      public int glFormat() {
         return this.glFormat;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class WriteCallback extends STBIWriteCallback {
      private final WritableByteChannel output;
      @Nullable
      private IOException exception;

      WriteCallback(WritableByteChannel p_85198_) {
         this.output = p_85198_;
      }

      public void invoke(long p_85204_, long p_85205_, int p_85206_) {
         ByteBuffer bytebuffer = getData(p_85205_, p_85206_);

         try {
            this.output.write(bytebuffer);
         } catch (IOException ioexception) {
            this.exception = ioexception;
         }

      }

      public void throwIfException() throws IOException {
         if (this.exception != null) {
            throw this.exception;
         }
      }
   }
}