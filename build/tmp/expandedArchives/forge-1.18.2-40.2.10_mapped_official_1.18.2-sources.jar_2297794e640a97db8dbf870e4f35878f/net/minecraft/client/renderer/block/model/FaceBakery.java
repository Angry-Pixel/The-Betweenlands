package net.minecraft.client.renderer.block.model;

import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockMath;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FaceBakery {
   public static final int VERTEX_INT_SIZE = 8;
   private static final float RESCALE_22_5 = 1.0F / (float)Math.cos((double)((float)Math.PI / 8F)) - 1.0F;
   private static final float RESCALE_45 = 1.0F / (float)Math.cos((double)((float)Math.PI / 4F)) - 1.0F;
   public static final int VERTEX_COUNT = 4;
   private static final int COLOR_INDEX = 3;
   public static final int UV_INDEX = 4;

   public BakedQuad bakeQuad(Vector3f p_111601_, Vector3f p_111602_, BlockElementFace p_111603_, TextureAtlasSprite p_111604_, Direction p_111605_, ModelState p_111606_, @Nullable BlockElementRotation p_111607_, boolean p_111608_, ResourceLocation p_111609_) {
      BlockFaceUV blockfaceuv = p_111603_.uv;
      if (p_111606_.isUvLocked()) {
         blockfaceuv = recomputeUVs(p_111603_.uv, p_111605_, p_111606_.getRotation(), p_111609_);
      }

      float[] afloat = new float[blockfaceuv.uvs.length];
      System.arraycopy(blockfaceuv.uvs, 0, afloat, 0, afloat.length);
      float f = p_111604_.uvShrinkRatio();
      float f1 = (blockfaceuv.uvs[0] + blockfaceuv.uvs[0] + blockfaceuv.uvs[2] + blockfaceuv.uvs[2]) / 4.0F;
      float f2 = (blockfaceuv.uvs[1] + blockfaceuv.uvs[1] + blockfaceuv.uvs[3] + blockfaceuv.uvs[3]) / 4.0F;
      blockfaceuv.uvs[0] = Mth.lerp(f, blockfaceuv.uvs[0], f1);
      blockfaceuv.uvs[2] = Mth.lerp(f, blockfaceuv.uvs[2], f1);
      blockfaceuv.uvs[1] = Mth.lerp(f, blockfaceuv.uvs[1], f2);
      blockfaceuv.uvs[3] = Mth.lerp(f, blockfaceuv.uvs[3], f2);
      int[] aint = this.makeVertices(blockfaceuv, p_111604_, p_111605_, this.setupShape(p_111601_, p_111602_), p_111606_.getRotation(), p_111607_, p_111608_);
      Direction direction = calculateFacing(aint);
      System.arraycopy(afloat, 0, blockfaceuv.uvs, 0, afloat.length);
      if (p_111607_ == null) {
         this.recalculateWinding(aint, direction);
      }

      net.minecraftforge.client.ForgeHooksClient.fillNormal(aint, direction);
      return new BakedQuad(aint, p_111603_.tintIndex, direction, p_111604_, p_111608_);
   }

   public static BlockFaceUV recomputeUVs(BlockFaceUV p_111582_, Direction p_111583_, Transformation p_111584_, ResourceLocation p_111585_) {
      Matrix4f matrix4f = BlockMath.getUVLockTransform(p_111584_, p_111583_, () -> {
         return "Unable to resolve UVLock for model: " + p_111585_;
      }).getMatrix();
      float f = p_111582_.getU(p_111582_.getReverseIndex(0));
      float f1 = p_111582_.getV(p_111582_.getReverseIndex(0));
      Vector4f vector4f = new Vector4f(f / 16.0F, f1 / 16.0F, 0.0F, 1.0F);
      vector4f.transform(matrix4f);
      float f2 = 16.0F * vector4f.x();
      float f3 = 16.0F * vector4f.y();
      float f4 = p_111582_.getU(p_111582_.getReverseIndex(2));
      float f5 = p_111582_.getV(p_111582_.getReverseIndex(2));
      Vector4f vector4f1 = new Vector4f(f4 / 16.0F, f5 / 16.0F, 0.0F, 1.0F);
      vector4f1.transform(matrix4f);
      float f6 = 16.0F * vector4f1.x();
      float f7 = 16.0F * vector4f1.y();
      float f8;
      float f9;
      if (Math.signum(f4 - f) == Math.signum(f6 - f2)) {
         f8 = f2;
         f9 = f6;
      } else {
         f8 = f6;
         f9 = f2;
      }

      float f10;
      float f11;
      if (Math.signum(f5 - f1) == Math.signum(f7 - f3)) {
         f10 = f3;
         f11 = f7;
      } else {
         f10 = f7;
         f11 = f3;
      }

      float f12 = (float)Math.toRadians((double)p_111582_.rotation);
      Vector3f vector3f = new Vector3f(Mth.cos(f12), Mth.sin(f12), 0.0F);
      Matrix3f matrix3f = new Matrix3f(matrix4f);
      vector3f.transform(matrix3f);
      int i = Math.floorMod(-((int)Math.round(Math.toDegrees(Math.atan2((double)vector3f.y(), (double)vector3f.x())) / 90.0D)) * 90, 360);
      return new BlockFaceUV(new float[]{f8, f10, f9, f11}, i);
   }

   private int[] makeVertices(BlockFaceUV p_111574_, TextureAtlasSprite p_111575_, Direction p_111576_, float[] p_111577_, Transformation p_111578_, @Nullable BlockElementRotation p_111579_, boolean p_111580_) {
      int[] aint = new int[32];

      for(int i = 0; i < 4; ++i) {
         this.bakeVertex(aint, i, p_111576_, p_111574_, p_111577_, p_111575_, p_111578_, p_111579_, p_111580_);
      }

      return aint;
   }

   private float[] setupShape(Vector3f p_111593_, Vector3f p_111594_) {
      float[] afloat = new float[Direction.values().length];
      afloat[FaceInfo.Constants.MIN_X] = p_111593_.x() / 16.0F;
      afloat[FaceInfo.Constants.MIN_Y] = p_111593_.y() / 16.0F;
      afloat[FaceInfo.Constants.MIN_Z] = p_111593_.z() / 16.0F;
      afloat[FaceInfo.Constants.MAX_X] = p_111594_.x() / 16.0F;
      afloat[FaceInfo.Constants.MAX_Y] = p_111594_.y() / 16.0F;
      afloat[FaceInfo.Constants.MAX_Z] = p_111594_.z() / 16.0F;
      return afloat;
   }

   private void bakeVertex(int[] p_111621_, int p_111622_, Direction p_111623_, BlockFaceUV p_111624_, float[] p_111625_, TextureAtlasSprite p_111626_, Transformation p_111627_, @Nullable BlockElementRotation p_111628_, boolean p_111629_) {
      FaceInfo.VertexInfo faceinfo$vertexinfo = FaceInfo.fromFacing(p_111623_).getVertexInfo(p_111622_);
      Vector3f vector3f = new Vector3f(p_111625_[faceinfo$vertexinfo.xFace], p_111625_[faceinfo$vertexinfo.yFace], p_111625_[faceinfo$vertexinfo.zFace]);
      this.applyElementRotation(vector3f, p_111628_);
      this.applyModelRotation(vector3f, p_111627_);
      this.fillVertex(p_111621_, p_111622_, vector3f, p_111626_, p_111624_);
   }

   private void fillVertex(int[] p_111615_, int p_111616_, Vector3f p_111617_, TextureAtlasSprite p_111618_, BlockFaceUV p_111619_) {
      int i = p_111616_ * 8;
      p_111615_[i] = Float.floatToRawIntBits(p_111617_.x());
      p_111615_[i + 1] = Float.floatToRawIntBits(p_111617_.y());
      p_111615_[i + 2] = Float.floatToRawIntBits(p_111617_.z());
      p_111615_[i + 3] = -1;
      p_111615_[i + 4] = Float.floatToRawIntBits(p_111618_.getU((double)p_111619_.getU(p_111616_) * .999 + p_111619_.getU((p_111616_ + 2) % 4) * .001));
      p_111615_[i + 4 + 1] = Float.floatToRawIntBits(p_111618_.getV((double)p_111619_.getV(p_111616_) * .999 + p_111619_.getV((p_111616_ + 2) % 4) * .001));
   }

   private void applyElementRotation(Vector3f p_111587_, @Nullable BlockElementRotation p_111588_) {
      if (p_111588_ != null) {
         Vector3f vector3f;
         Vector3f vector3f1;
         switch(p_111588_.axis) {
         case X:
            vector3f = Vector3f.XP;
            vector3f1 = new Vector3f(0.0F, 1.0F, 1.0F);
            break;
         case Y:
            vector3f = Vector3f.YP;
            vector3f1 = new Vector3f(1.0F, 0.0F, 1.0F);
            break;
         case Z:
            vector3f = Vector3f.ZP;
            vector3f1 = new Vector3f(1.0F, 1.0F, 0.0F);
            break;
         default:
            throw new IllegalArgumentException("There are only 3 axes");
         }

         Quaternion quaternion = vector3f.rotationDegrees(p_111588_.angle);
         if (p_111588_.rescale) {
            if (Math.abs(p_111588_.angle) == 22.5F) {
               vector3f1.mul(RESCALE_22_5);
            } else {
               vector3f1.mul(RESCALE_45);
            }

            vector3f1.add(1.0F, 1.0F, 1.0F);
         } else {
            vector3f1.set(1.0F, 1.0F, 1.0F);
         }

         this.rotateVertexBy(p_111587_, p_111588_.origin.copy(), new Matrix4f(quaternion), vector3f1);
      }
   }

   public void applyModelRotation(Vector3f p_111590_, Transformation p_111591_) {
      if (p_111591_ != Transformation.identity()) {
         this.rotateVertexBy(p_111590_, new Vector3f(0.5F, 0.5F, 0.5F), p_111591_.getMatrix(), new Vector3f(1.0F, 1.0F, 1.0F));
      }
   }

   private void rotateVertexBy(Vector3f p_111596_, Vector3f p_111597_, Matrix4f p_111598_, Vector3f p_111599_) {
      Vector4f vector4f = new Vector4f(p_111596_.x() - p_111597_.x(), p_111596_.y() - p_111597_.y(), p_111596_.z() - p_111597_.z(), 1.0F);
      vector4f.transform(p_111598_);
      vector4f.mul(p_111599_);
      p_111596_.set(vector4f.x() + p_111597_.x(), vector4f.y() + p_111597_.y(), vector4f.z() + p_111597_.z());
   }

   public static Direction calculateFacing(int[] p_111613_) {
      Vector3f vector3f = new Vector3f(Float.intBitsToFloat(p_111613_[0]), Float.intBitsToFloat(p_111613_[1]), Float.intBitsToFloat(p_111613_[2]));
      Vector3f vector3f1 = new Vector3f(Float.intBitsToFloat(p_111613_[8]), Float.intBitsToFloat(p_111613_[9]), Float.intBitsToFloat(p_111613_[10]));
      Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(p_111613_[16]), Float.intBitsToFloat(p_111613_[17]), Float.intBitsToFloat(p_111613_[18]));
      Vector3f vector3f3 = vector3f.copy();
      vector3f3.sub(vector3f1);
      Vector3f vector3f4 = vector3f2.copy();
      vector3f4.sub(vector3f1);
      Vector3f vector3f5 = vector3f4.copy();
      vector3f5.cross(vector3f3);
      vector3f5.normalize();
      Direction direction = null;
      float f = 0.0F;

      for(Direction direction1 : Direction.values()) {
         Vec3i vec3i = direction1.getNormal();
         Vector3f vector3f6 = new Vector3f((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
         float f1 = vector3f5.dot(vector3f6);
         if (f1 >= 0.0F && f1 > f) {
            f = f1;
            direction = direction1;
         }
      }

      return direction == null ? Direction.UP : direction;
   }

   private void recalculateWinding(int[] p_111631_, Direction p_111632_) {
      int[] aint = new int[p_111631_.length];
      System.arraycopy(p_111631_, 0, aint, 0, p_111631_.length);
      float[] afloat = new float[Direction.values().length];
      afloat[FaceInfo.Constants.MIN_X] = 999.0F;
      afloat[FaceInfo.Constants.MIN_Y] = 999.0F;
      afloat[FaceInfo.Constants.MIN_Z] = 999.0F;
      afloat[FaceInfo.Constants.MAX_X] = -999.0F;
      afloat[FaceInfo.Constants.MAX_Y] = -999.0F;
      afloat[FaceInfo.Constants.MAX_Z] = -999.0F;

      for(int i = 0; i < 4; ++i) {
         int j = 8 * i;
         float f = Float.intBitsToFloat(aint[j]);
         float f1 = Float.intBitsToFloat(aint[j + 1]);
         float f2 = Float.intBitsToFloat(aint[j + 2]);
         if (f < afloat[FaceInfo.Constants.MIN_X]) {
            afloat[FaceInfo.Constants.MIN_X] = f;
         }

         if (f1 < afloat[FaceInfo.Constants.MIN_Y]) {
            afloat[FaceInfo.Constants.MIN_Y] = f1;
         }

         if (f2 < afloat[FaceInfo.Constants.MIN_Z]) {
            afloat[FaceInfo.Constants.MIN_Z] = f2;
         }

         if (f > afloat[FaceInfo.Constants.MAX_X]) {
            afloat[FaceInfo.Constants.MAX_X] = f;
         }

         if (f1 > afloat[FaceInfo.Constants.MAX_Y]) {
            afloat[FaceInfo.Constants.MAX_Y] = f1;
         }

         if (f2 > afloat[FaceInfo.Constants.MAX_Z]) {
            afloat[FaceInfo.Constants.MAX_Z] = f2;
         }
      }

      FaceInfo faceinfo = FaceInfo.fromFacing(p_111632_);

      for(int i1 = 0; i1 < 4; ++i1) {
         int j1 = 8 * i1;
         FaceInfo.VertexInfo faceinfo$vertexinfo = faceinfo.getVertexInfo(i1);
         float f8 = afloat[faceinfo$vertexinfo.xFace];
         float f3 = afloat[faceinfo$vertexinfo.yFace];
         float f4 = afloat[faceinfo$vertexinfo.zFace];
         p_111631_[j1] = Float.floatToRawIntBits(f8);
         p_111631_[j1 + 1] = Float.floatToRawIntBits(f3);
         p_111631_[j1 + 2] = Float.floatToRawIntBits(f4);

         for(int k = 0; k < 4; ++k) {
            int l = 8 * k;
            float f5 = Float.intBitsToFloat(aint[l]);
            float f6 = Float.intBitsToFloat(aint[l + 1]);
            float f7 = Float.intBitsToFloat(aint[l + 2]);
            if (Mth.equal(f8, f5) && Mth.equal(f3, f6) && Mth.equal(f4, f7)) {
               p_111631_[j1 + 4] = aint[l + 4];
               p_111631_[j1 + 4 + 1] = aint[l + 4 + 1];
            }
         }
      }

   }
}
