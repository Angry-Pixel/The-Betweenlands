package net.minecraft.client.resources.model;

import com.mojang.math.OctahedralGroup;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum BlockModelRotation implements ModelState {
   X0_Y0(0, 0),
   X0_Y90(0, 90),
   X0_Y180(0, 180),
   X0_Y270(0, 270),
   X90_Y0(90, 0),
   X90_Y90(90, 90),
   X90_Y180(90, 180),
   X90_Y270(90, 270),
   X180_Y0(180, 0),
   X180_Y90(180, 90),
   X180_Y180(180, 180),
   X180_Y270(180, 270),
   X270_Y0(270, 0),
   X270_Y90(270, 90),
   X270_Y180(270, 180),
   X270_Y270(270, 270);

   private static final int DEGREES = 360;
   private static final Map<Integer, BlockModelRotation> BY_INDEX = Arrays.stream(values()).collect(Collectors.toMap((p_119163_) -> {
      return p_119163_.index;
   }, (p_119157_) -> {
      return p_119157_;
   }));
   private final Transformation transformation;
   private final OctahedralGroup actualRotation;
   private final int index;

   private static int getIndex(int p_119160_, int p_119161_) {
      return p_119160_ * 360 + p_119161_;
   }

   private BlockModelRotation(int p_119151_, int p_119152_) {
      this.index = getIndex(p_119151_, p_119152_);
      Quaternion quaternion = Vector3f.YP.rotationDegrees((float)(-p_119152_));
      quaternion.mul(Vector3f.XP.rotationDegrees((float)(-p_119151_)));
      OctahedralGroup octahedralgroup = OctahedralGroup.IDENTITY;

      for(int i = 0; i < p_119152_; i += 90) {
         octahedralgroup = octahedralgroup.compose(OctahedralGroup.ROT_90_Y_NEG);
      }

      for(int j = 0; j < p_119151_; j += 90) {
         octahedralgroup = octahedralgroup.compose(OctahedralGroup.ROT_90_X_NEG);
      }

      this.transformation = new Transformation((Vector3f)null, quaternion, (Vector3f)null, (Quaternion)null);
      this.actualRotation = octahedralgroup;
   }

   public Transformation getRotation() {
      return this.transformation;
   }

   public static BlockModelRotation by(int p_119154_, int p_119155_) {
      return BY_INDEX.get(getIndex(Mth.positiveModulo(p_119154_, 360), Mth.positiveModulo(p_119155_, 360)));
   }

   public OctahedralGroup actualRotation() {
      return this.actualRotation;
   }
}