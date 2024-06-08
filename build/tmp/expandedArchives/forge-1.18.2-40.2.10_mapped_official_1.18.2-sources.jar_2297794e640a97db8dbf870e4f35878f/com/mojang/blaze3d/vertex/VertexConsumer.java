package com.mojang.blaze3d.vertex;

import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Vec3i;
import net.minecraft.util.FastColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.MemoryStack;

@OnlyIn(Dist.CLIENT)
public interface VertexConsumer extends net.minecraftforge.client.extensions.IForgeVertexConsumer {
   VertexConsumer vertex(double p_85945_, double p_85946_, double p_85947_);

   VertexConsumer color(int p_85973_, int p_85974_, int p_85975_, int p_85976_);

   VertexConsumer uv(float p_85948_, float p_85949_);

   VertexConsumer overlayCoords(int p_85971_, int p_85972_);

   VertexConsumer uv2(int p_86010_, int p_86011_);

   VertexConsumer normal(float p_86005_, float p_86006_, float p_86007_);

   void endVertex();

   default void vertex(float p_85955_, float p_85956_, float p_85957_, float p_85958_, float p_85959_, float p_85960_, float p_85961_, float p_85962_, float p_85963_, int p_85964_, int p_85965_, float p_85966_, float p_85967_, float p_85968_) {
      this.vertex((double)p_85955_, (double)p_85956_, (double)p_85957_);
      this.color(p_85958_, p_85959_, p_85960_, p_85961_);
      this.uv(p_85962_, p_85963_);
      this.overlayCoords(p_85964_);
      this.uv2(p_85965_);
      this.normal(p_85966_, p_85967_, p_85968_);
      this.endVertex();
   }

   void defaultColor(int p_166901_, int p_166902_, int p_166903_, int p_166904_);

   void unsetDefaultColor();

   default VertexConsumer color(float p_85951_, float p_85952_, float p_85953_, float p_85954_) {
      return this.color((int)(p_85951_ * 255.0F), (int)(p_85952_ * 255.0F), (int)(p_85953_ * 255.0F), (int)(p_85954_ * 255.0F));
   }

   default VertexConsumer color(int p_193480_) {
      return this.color(FastColor.ARGB32.red(p_193480_), FastColor.ARGB32.green(p_193480_), FastColor.ARGB32.blue(p_193480_), FastColor.ARGB32.alpha(p_193480_));
   }

   default VertexConsumer uv2(int p_85970_) {
      return this.uv2(p_85970_ & '\uffff', p_85970_ >> 16 & '\uffff');
   }

   default VertexConsumer overlayCoords(int p_86009_) {
      return this.overlayCoords(p_86009_ & '\uffff', p_86009_ >> 16 & '\uffff');
   }

   default void putBulkData(PoseStack.Pose p_85988_, BakedQuad p_85989_, float p_85990_, float p_85991_, float p_85992_, int p_85993_, int p_85994_) {
      this.putBulkData(p_85988_, p_85989_, new float[]{1.0F, 1.0F, 1.0F, 1.0F}, p_85990_, p_85991_, p_85992_, new int[]{p_85993_, p_85993_, p_85993_, p_85993_}, p_85994_, false);
   }

   default void putBulkData(PoseStack.Pose p_85996_, BakedQuad p_85997_, float[] p_85998_, float p_85999_, float p_86000_, float p_86001_, int[] p_86002_, int p_86003_, boolean p_86004_) {
      float[] afloat = new float[]{p_85998_[0], p_85998_[1], p_85998_[2], p_85998_[3]};
      int[] aint = new int[]{p_86002_[0], p_86002_[1], p_86002_[2], p_86002_[3]};
      int[] aint1 = p_85997_.getVertices();
      Vec3i vec3i = p_85997_.getDirection().getNormal();
      Vector3f vector3f = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
      Matrix4f matrix4f = p_85996_.pose();
      vector3f.transform(p_85996_.normal());
      int i = 8;
      int j = aint1.length / 8;
      MemoryStack memorystack = MemoryStack.stackPush();

      try {
         ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
         IntBuffer intbuffer = bytebuffer.asIntBuffer();

         for(int k = 0; k < j; ++k) {
            intbuffer.clear();
            intbuffer.put(aint1, k * 8, 8);
            float f = bytebuffer.getFloat(0);
            float f1 = bytebuffer.getFloat(4);
            float f2 = bytebuffer.getFloat(8);
            float f3;
            float f4;
            float f5;
            if (p_86004_) {
               float f6 = (float)(bytebuffer.get(12) & 255) / 255.0F;
               float f7 = (float)(bytebuffer.get(13) & 255) / 255.0F;
               float f8 = (float)(bytebuffer.get(14) & 255) / 255.0F;
               f3 = f6 * afloat[k] * p_85999_;
               f4 = f7 * afloat[k] * p_86000_;
               f5 = f8 * afloat[k] * p_86001_;
            } else {
               f3 = afloat[k] * p_85999_;
               f4 = afloat[k] * p_86000_;
               f5 = afloat[k] * p_86001_;
            }

            int l = applyBakedLighting(p_86002_[k], bytebuffer);
            float f9 = bytebuffer.getFloat(16);
            float f10 = bytebuffer.getFloat(20);
            Vector4f vector4f = new Vector4f(f, f1, f2, 1.0F);
            vector4f.transform(matrix4f);
            applyBakedNormals(vector3f, bytebuffer, p_85996_.normal());
            this.vertex(vector4f.x(), vector4f.y(), vector4f.z(), f3, f4, f5, 1.0F, f9, f10, p_86003_, l, vector3f.x(), vector3f.y(), vector3f.z());
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

   default VertexConsumer vertex(Matrix4f p_85983_, float p_85984_, float p_85985_, float p_85986_) {
      Vector4f vector4f = new Vector4f(p_85984_, p_85985_, p_85986_, 1.0F);
      vector4f.transform(p_85983_);
      return this.vertex((double)vector4f.x(), (double)vector4f.y(), (double)vector4f.z());
   }

   default VertexConsumer normal(Matrix3f p_85978_, float p_85979_, float p_85980_, float p_85981_) {
      Vector3f vector3f = new Vector3f(p_85979_, p_85980_, p_85981_);
      vector3f.transform(p_85978_);
      return this.normal(vector3f.x(), vector3f.y(), vector3f.z());
   }
}
