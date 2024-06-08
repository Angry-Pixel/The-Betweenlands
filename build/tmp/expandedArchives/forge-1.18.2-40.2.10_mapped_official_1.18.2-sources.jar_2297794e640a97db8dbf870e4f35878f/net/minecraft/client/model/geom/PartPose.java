package net.minecraft.client.model.geom;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PartPose {
   public static final PartPose ZERO = offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   public final float x;
   public final float y;
   public final float z;
   public final float xRot;
   public final float yRot;
   public final float zRot;

   private PartPose(float p_171413_, float p_171414_, float p_171415_, float p_171416_, float p_171417_, float p_171418_) {
      this.x = p_171413_;
      this.y = p_171414_;
      this.z = p_171415_;
      this.xRot = p_171416_;
      this.yRot = p_171417_;
      this.zRot = p_171418_;
   }

   public static PartPose offset(float p_171420_, float p_171421_, float p_171422_) {
      return offsetAndRotation(p_171420_, p_171421_, p_171422_, 0.0F, 0.0F, 0.0F);
   }

   public static PartPose rotation(float p_171431_, float p_171432_, float p_171433_) {
      return offsetAndRotation(0.0F, 0.0F, 0.0F, p_171431_, p_171432_, p_171433_);
   }

   public static PartPose offsetAndRotation(float p_171424_, float p_171425_, float p_171426_, float p_171427_, float p_171428_, float p_171429_) {
      return new PartPose(p_171424_, p_171425_, p_171426_, p_171427_, p_171428_, p_171429_);
   }
}