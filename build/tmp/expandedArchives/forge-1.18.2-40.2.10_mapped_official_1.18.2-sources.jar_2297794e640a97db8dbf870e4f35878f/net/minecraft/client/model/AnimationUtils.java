package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CrossbowItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnimationUtils {
   public static void animateCrossbowHold(ModelPart p_102098_, ModelPart p_102099_, ModelPart p_102100_, boolean p_102101_) {
      ModelPart modelpart = p_102101_ ? p_102098_ : p_102099_;
      ModelPart modelpart1 = p_102101_ ? p_102099_ : p_102098_;
      modelpart.yRot = (p_102101_ ? -0.3F : 0.3F) + p_102100_.yRot;
      modelpart1.yRot = (p_102101_ ? 0.6F : -0.6F) + p_102100_.yRot;
      modelpart.xRot = (-(float)Math.PI / 2F) + p_102100_.xRot + 0.1F;
      modelpart1.xRot = -1.5F + p_102100_.xRot;
   }

   public static void animateCrossbowCharge(ModelPart p_102087_, ModelPart p_102088_, LivingEntity p_102089_, boolean p_102090_) {
      ModelPart modelpart = p_102090_ ? p_102087_ : p_102088_;
      ModelPart modelpart1 = p_102090_ ? p_102088_ : p_102087_;
      modelpart.yRot = p_102090_ ? -0.8F : 0.8F;
      modelpart.xRot = -0.97079635F;
      modelpart1.xRot = modelpart.xRot;
      float f = (float)CrossbowItem.getChargeDuration(p_102089_.getUseItem());
      float f1 = Mth.clamp((float)p_102089_.getTicksUsingItem(), 0.0F, f);
      float f2 = f1 / f;
      modelpart1.yRot = Mth.lerp(f2, 0.4F, 0.85F) * (float)(p_102090_ ? 1 : -1);
      modelpart1.xRot = Mth.lerp(f2, modelpart1.xRot, (-(float)Math.PI / 2F));
   }

   public static <T extends Mob> void swingWeaponDown(ModelPart p_102092_, ModelPart p_102093_, T p_102094_, float p_102095_, float p_102096_) {
      float f = Mth.sin(p_102095_ * (float)Math.PI);
      float f1 = Mth.sin((1.0F - (1.0F - p_102095_) * (1.0F - p_102095_)) * (float)Math.PI);
      p_102092_.zRot = 0.0F;
      p_102093_.zRot = 0.0F;
      p_102092_.yRot = 0.15707964F;
      p_102093_.yRot = -0.15707964F;
      if (p_102094_.getMainArm() == HumanoidArm.RIGHT) {
         p_102092_.xRot = -1.8849558F + Mth.cos(p_102096_ * 0.09F) * 0.15F;
         p_102093_.xRot = -0.0F + Mth.cos(p_102096_ * 0.19F) * 0.5F;
         p_102092_.xRot += f * 2.2F - f1 * 0.4F;
         p_102093_.xRot += f * 1.2F - f1 * 0.4F;
      } else {
         p_102092_.xRot = -0.0F + Mth.cos(p_102096_ * 0.19F) * 0.5F;
         p_102093_.xRot = -1.8849558F + Mth.cos(p_102096_ * 0.09F) * 0.15F;
         p_102092_.xRot += f * 1.2F - f1 * 0.4F;
         p_102093_.xRot += f * 2.2F - f1 * 0.4F;
      }

      bobArms(p_102092_, p_102093_, p_102096_);
   }

   public static void bobModelPart(ModelPart p_170342_, float p_170343_, float p_170344_) {
      p_170342_.zRot += p_170344_ * (Mth.cos(p_170343_ * 0.09F) * 0.05F + 0.05F);
      p_170342_.xRot += p_170344_ * Mth.sin(p_170343_ * 0.067F) * 0.05F;
   }

   public static void bobArms(ModelPart p_102083_, ModelPart p_102084_, float p_102085_) {
      bobModelPart(p_102083_, p_102085_, 1.0F);
      bobModelPart(p_102084_, p_102085_, -1.0F);
   }

   public static void animateZombieArms(ModelPart p_102103_, ModelPart p_102104_, boolean p_102105_, float p_102106_, float p_102107_) {
      float f = Mth.sin(p_102106_ * (float)Math.PI);
      float f1 = Mth.sin((1.0F - (1.0F - p_102106_) * (1.0F - p_102106_)) * (float)Math.PI);
      p_102104_.zRot = 0.0F;
      p_102103_.zRot = 0.0F;
      p_102104_.yRot = -(0.1F - f * 0.6F);
      p_102103_.yRot = 0.1F - f * 0.6F;
      float f2 = -(float)Math.PI / (p_102105_ ? 1.5F : 2.25F);
      p_102104_.xRot = f2;
      p_102103_.xRot = f2;
      p_102104_.xRot += f * 1.2F - f1 * 0.4F;
      p_102103_.xRot += f * 1.2F - f1 * 0.4F;
      bobArms(p_102104_, p_102103_, p_102107_);
   }
}