package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Uniform extends AbstractUniform implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int UT_INT1 = 0;
   public static final int UT_INT2 = 1;
   public static final int UT_INT3 = 2;
   public static final int UT_INT4 = 3;
   public static final int UT_FLOAT1 = 4;
   public static final int UT_FLOAT2 = 5;
   public static final int UT_FLOAT3 = 6;
   public static final int UT_FLOAT4 = 7;
   public static final int UT_MAT2 = 8;
   public static final int UT_MAT3 = 9;
   public static final int UT_MAT4 = 10;
   private static final boolean TRANSPOSE_MATRICIES = false;
   private int location;
   private final int count;
   private final int type;
   private final IntBuffer intValues;
   private final FloatBuffer floatValues;
   private final String name;
   private boolean dirty;
   private final Shader parent;

   public Uniform(String p_166638_, int p_166639_, int p_166640_, Shader p_166641_) {
      this.name = p_166638_;
      this.count = p_166640_;
      this.type = p_166639_;
      this.parent = p_166641_;
      if (p_166639_ <= 3) {
         this.intValues = MemoryUtil.memAllocInt(p_166640_);
         this.floatValues = null;
      } else {
         this.intValues = null;
         this.floatValues = MemoryUtil.memAllocFloat(p_166640_);
      }

      this.location = -1;
      this.markDirty();
   }

   public static int glGetUniformLocation(int p_85625_, CharSequence p_85626_) {
      return GlStateManager._glGetUniformLocation(p_85625_, p_85626_);
   }

   public static void uploadInteger(int p_85617_, int p_85618_) {
      RenderSystem.glUniform1i(p_85617_, p_85618_);
   }

   public static int glGetAttribLocation(int p_85640_, CharSequence p_85641_) {
      return GlStateManager._glGetAttribLocation(p_85640_, p_85641_);
   }

   public static void glBindAttribLocation(int p_166711_, int p_166712_, CharSequence p_166713_) {
      GlStateManager._glBindAttribLocation(p_166711_, p_166712_, p_166713_);
   }

   public void close() {
      if (this.intValues != null) {
         MemoryUtil.memFree(this.intValues);
      }

      if (this.floatValues != null) {
         MemoryUtil.memFree(this.floatValues);
      }

   }

   private void markDirty() {
      this.dirty = true;
      if (this.parent != null) {
         this.parent.markDirty();
      }

   }

   public static int getTypeFromString(String p_85630_) {
      int i = -1;
      if ("int".equals(p_85630_)) {
         i = 0;
      } else if ("float".equals(p_85630_)) {
         i = 4;
      } else if (p_85630_.startsWith("matrix")) {
         if (p_85630_.endsWith("2x2")) {
            i = 8;
         } else if (p_85630_.endsWith("3x3")) {
            i = 9;
         } else if (p_85630_.endsWith("4x4")) {
            i = 10;
         }
      }

      return i;
   }

   public void setLocation(int p_85615_) {
      this.location = p_85615_;
   }

   public String getName() {
      return this.name;
   }

   public final void set(float p_85601_) {
      this.floatValues.position(0);
      this.floatValues.put(0, p_85601_);
      this.markDirty();
   }

   public final void set(float p_85603_, float p_85604_) {
      this.floatValues.position(0);
      this.floatValues.put(0, p_85603_);
      this.floatValues.put(1, p_85604_);
      this.markDirty();
   }

   public final void set(int p_166701_, float p_166702_) {
      this.floatValues.position(0);
      this.floatValues.put(p_166701_, p_166702_);
      this.markDirty();
   }

   public final void set(float p_85606_, float p_85607_, float p_85608_) {
      this.floatValues.position(0);
      this.floatValues.put(0, p_85606_);
      this.floatValues.put(1, p_85607_);
      this.floatValues.put(2, p_85608_);
      this.markDirty();
   }

   public final void set(Vector3f p_166715_) {
      this.floatValues.position(0);
      this.floatValues.put(0, p_166715_.x());
      this.floatValues.put(1, p_166715_.y());
      this.floatValues.put(2, p_166715_.z());
      this.markDirty();
   }

   public final void set(float p_85610_, float p_85611_, float p_85612_, float p_85613_) {
      this.floatValues.position(0);
      this.floatValues.put(p_85610_);
      this.floatValues.put(p_85611_);
      this.floatValues.put(p_85612_);
      this.floatValues.put(p_85613_);
      this.floatValues.flip();
      this.markDirty();
   }

   public final void set(Vector4f p_166717_) {
      this.floatValues.position(0);
      this.floatValues.put(0, p_166717_.x());
      this.floatValues.put(1, p_166717_.y());
      this.floatValues.put(2, p_166717_.z());
      this.floatValues.put(3, p_166717_.w());
      this.markDirty();
   }

   public final void setSafe(float p_85635_, float p_85636_, float p_85637_, float p_85638_) {
      this.floatValues.position(0);
      if (this.type >= 4) {
         this.floatValues.put(0, p_85635_);
      }

      if (this.type >= 5) {
         this.floatValues.put(1, p_85636_);
      }

      if (this.type >= 6) {
         this.floatValues.put(2, p_85637_);
      }

      if (this.type >= 7) {
         this.floatValues.put(3, p_85638_);
      }

      this.markDirty();
   }

   public final void setSafe(int p_85620_, int p_85621_, int p_85622_, int p_85623_) {
      this.intValues.position(0);
      if (this.type >= 0) {
         this.intValues.put(0, p_85620_);
      }

      if (this.type >= 1) {
         this.intValues.put(1, p_85621_);
      }

      if (this.type >= 2) {
         this.intValues.put(2, p_85622_);
      }

      if (this.type >= 3) {
         this.intValues.put(3, p_85623_);
      }

      this.markDirty();
   }

   public final void set(int p_166699_) {
      this.intValues.position(0);
      this.intValues.put(0, p_166699_);
      this.markDirty();
   }

   public final void set(int p_166704_, int p_166705_) {
      this.intValues.position(0);
      this.intValues.put(0, p_166704_);
      this.intValues.put(1, p_166705_);
      this.markDirty();
   }

   public final void set(int p_166707_, int p_166708_, int p_166709_) {
      this.intValues.position(0);
      this.intValues.put(0, p_166707_);
      this.intValues.put(1, p_166708_);
      this.intValues.put(2, p_166709_);
      this.markDirty();
   }

   public final void set(int p_166748_, int p_166749_, int p_166750_, int p_166751_) {
      this.intValues.position(0);
      this.intValues.put(0, p_166748_);
      this.intValues.put(1, p_166749_);
      this.intValues.put(2, p_166750_);
      this.intValues.put(3, p_166751_);
      this.markDirty();
   }

   public final void set(float[] p_85632_) {
      if (p_85632_.length < this.count) {
         LOGGER.warn("Uniform.set called with a too-small value array (expected {}, got {}). Ignoring.", this.count, p_85632_.length);
      } else {
         this.floatValues.position(0);
         this.floatValues.put(p_85632_);
         this.floatValues.position(0);
         this.markDirty();
      }
   }

   public final void setMat2x2(float p_166754_, float p_166755_, float p_166756_, float p_166757_) {
      this.floatValues.position(0);
      this.floatValues.put(0, p_166754_);
      this.floatValues.put(1, p_166755_);
      this.floatValues.put(2, p_166756_);
      this.floatValues.put(3, p_166757_);
      this.markDirty();
   }

   public final void setMat2x3(float p_166643_, float p_166644_, float p_166645_, float p_166646_, float p_166647_, float p_166648_) {
      this.floatValues.position(0);
      this.floatValues.put(0, p_166643_);
      this.floatValues.put(1, p_166644_);
      this.floatValues.put(2, p_166645_);
      this.floatValues.put(3, p_166646_);
      this.floatValues.put(4, p_166647_);
      this.floatValues.put(5, p_166648_);
      this.markDirty();
   }

   public final void setMat2x4(float p_166650_, float p_166651_, float p_166652_, float p_166653_, float p_166654_, float p_166655_, float p_166656_, float p_166657_) {
      this.floatValues.position(0);
      this.floatValues.put(0, p_166650_);
      this.floatValues.put(1, p_166651_);
      this.floatValues.put(2, p_166652_);
      this.floatValues.put(3, p_166653_);
      this.floatValues.put(4, p_166654_);
      this.floatValues.put(5, p_166655_);
      this.floatValues.put(6, p_166656_);
      this.floatValues.put(7, p_166657_);
      this.markDirty();
   }

   public final void setMat3x2(float p_166719_, float p_166720_, float p_166721_, float p_166722_, float p_166723_, float p_166724_) {
      this.floatValues.position(0);
      this.floatValues.put(0, p_166719_);
      this.floatValues.put(1, p_166720_);
      this.floatValues.put(2, p_166721_);
      this.floatValues.put(3, p_166722_);
      this.floatValues.put(4, p_166723_);
      this.floatValues.put(5, p_166724_);
      this.markDirty();
   }

   public final void setMat3x3(float p_166659_, float p_166660_, float p_166661_, float p_166662_, float p_166663_, float p_166664_, float p_166665_, float p_166666_, float p_166667_) {
      this.floatValues.position(0);
      this.floatValues.put(0, p_166659_);
      this.floatValues.put(1, p_166660_);
      this.floatValues.put(2, p_166661_);
      this.floatValues.put(3, p_166662_);
      this.floatValues.put(4, p_166663_);
      this.floatValues.put(5, p_166664_);
      this.floatValues.put(6, p_166665_);
      this.floatValues.put(7, p_166666_);
      this.floatValues.put(8, p_166667_);
      this.markDirty();
   }

   public final void setMat3x4(float p_166669_, float p_166670_, float p_166671_, float p_166672_, float p_166673_, float p_166674_, float p_166675_, float p_166676_, float p_166677_, float p_166678_, float p_166679_, float p_166680_) {
      this.floatValues.position(0);
      this.floatValues.put(0, p_166669_);
      this.floatValues.put(1, p_166670_);
      this.floatValues.put(2, p_166671_);
      this.floatValues.put(3, p_166672_);
      this.floatValues.put(4, p_166673_);
      this.floatValues.put(5, p_166674_);
      this.floatValues.put(6, p_166675_);
      this.floatValues.put(7, p_166676_);
      this.floatValues.put(8, p_166677_);
      this.floatValues.put(9, p_166678_);
      this.floatValues.put(10, p_166679_);
      this.floatValues.put(11, p_166680_);
      this.markDirty();
   }

   public final void setMat4x2(float p_166726_, float p_166727_, float p_166728_, float p_166729_, float p_166730_, float p_166731_, float p_166732_, float p_166733_) {
      this.floatValues.position(0);
      this.floatValues.put(0, p_166726_);
      this.floatValues.put(1, p_166727_);
      this.floatValues.put(2, p_166728_);
      this.floatValues.put(3, p_166729_);
      this.floatValues.put(4, p_166730_);
      this.floatValues.put(5, p_166731_);
      this.floatValues.put(6, p_166732_);
      this.floatValues.put(7, p_166733_);
      this.markDirty();
   }

   public final void setMat4x3(float p_166735_, float p_166736_, float p_166737_, float p_166738_, float p_166739_, float p_166740_, float p_166741_, float p_166742_, float p_166743_, float p_166744_, float p_166745_, float p_166746_) {
      this.floatValues.position(0);
      this.floatValues.put(0, p_166735_);
      this.floatValues.put(1, p_166736_);
      this.floatValues.put(2, p_166737_);
      this.floatValues.put(3, p_166738_);
      this.floatValues.put(4, p_166739_);
      this.floatValues.put(5, p_166740_);
      this.floatValues.put(6, p_166741_);
      this.floatValues.put(7, p_166742_);
      this.floatValues.put(8, p_166743_);
      this.floatValues.put(9, p_166744_);
      this.floatValues.put(10, p_166745_);
      this.floatValues.put(11, p_166746_);
      this.markDirty();
   }

   public final void setMat4x4(float p_166682_, float p_166683_, float p_166684_, float p_166685_, float p_166686_, float p_166687_, float p_166688_, float p_166689_, float p_166690_, float p_166691_, float p_166692_, float p_166693_, float p_166694_, float p_166695_, float p_166696_, float p_166697_) {
      this.floatValues.position(0);
      this.floatValues.put(0, p_166682_);
      this.floatValues.put(1, p_166683_);
      this.floatValues.put(2, p_166684_);
      this.floatValues.put(3, p_166685_);
      this.floatValues.put(4, p_166686_);
      this.floatValues.put(5, p_166687_);
      this.floatValues.put(6, p_166688_);
      this.floatValues.put(7, p_166689_);
      this.floatValues.put(8, p_166690_);
      this.floatValues.put(9, p_166691_);
      this.floatValues.put(10, p_166692_);
      this.floatValues.put(11, p_166693_);
      this.floatValues.put(12, p_166694_);
      this.floatValues.put(13, p_166695_);
      this.floatValues.put(14, p_166696_);
      this.floatValues.put(15, p_166697_);
      this.markDirty();
   }

   public final void set(Matrix4f p_85628_) {
      this.floatValues.position(0);
      p_85628_.store(this.floatValues);
      this.markDirty();
   }

   public final void set(Matrix3f p_200935_) {
      this.floatValues.position(0);
      p_200935_.store(this.floatValues);
      this.markDirty();
   }

   public void upload() {
      if (!this.dirty) {
      }

      this.dirty = false;
      if (this.type <= 3) {
         this.uploadAsInteger();
      } else if (this.type <= 7) {
         this.uploadAsFloat();
      } else {
         if (this.type > 10) {
            LOGGER.warn("Uniform.upload called, but type value ({}) is not a valid type. Ignoring.", (int)this.type);
            return;
         }

         this.uploadAsMatrix();
      }

   }

   private void uploadAsInteger() {
      this.intValues.rewind();
      switch(this.type) {
      case 0:
         RenderSystem.glUniform1(this.location, this.intValues);
         break;
      case 1:
         RenderSystem.glUniform2(this.location, this.intValues);
         break;
      case 2:
         RenderSystem.glUniform3(this.location, this.intValues);
         break;
      case 3:
         RenderSystem.glUniform4(this.location, this.intValues);
         break;
      default:
         LOGGER.warn("Uniform.upload called, but count value ({}) is  not in the range of 1 to 4. Ignoring.", (int)this.count);
      }

   }

   private void uploadAsFloat() {
      this.floatValues.rewind();
      switch(this.type) {
      case 4:
         RenderSystem.glUniform1(this.location, this.floatValues);
         break;
      case 5:
         RenderSystem.glUniform2(this.location, this.floatValues);
         break;
      case 6:
         RenderSystem.glUniform3(this.location, this.floatValues);
         break;
      case 7:
         RenderSystem.glUniform4(this.location, this.floatValues);
         break;
      default:
         LOGGER.warn("Uniform.upload called, but count value ({}) is not in the range of 1 to 4. Ignoring.", (int)this.count);
      }

   }

   private void uploadAsMatrix() {
      this.floatValues.clear();
      switch(this.type) {
      case 8:
         RenderSystem.glUniformMatrix2(this.location, false, this.floatValues);
         break;
      case 9:
         RenderSystem.glUniformMatrix3(this.location, false, this.floatValues);
         break;
      case 10:
         RenderSystem.glUniformMatrix4(this.location, false, this.floatValues);
      }

   }

   public int getLocation() {
      return this.location;
   }

   public int getCount() {
      return this.count;
   }

   public int getType() {
      return this.type;
   }

   public IntBuffer getIntBuffer() {
      return this.intValues;
   }

   public FloatBuffer getFloatBuffer() {
      return this.floatValues;
   }
}