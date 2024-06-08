package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.math.Vector3f;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LerpingModel;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AxolotlModel<T extends Axolotl & LerpingModel> extends AgeableListModel<T> {
   public static final float SWIMMING_LEG_XROT = 1.8849558F;
   private final ModelPart tail;
   private final ModelPart leftHindLeg;
   private final ModelPart rightHindLeg;
   private final ModelPart leftFrontLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart body;
   private final ModelPart head;
   private final ModelPart topGills;
   private final ModelPart leftGills;
   private final ModelPart rightGills;

   public AxolotlModel(ModelPart p_170370_) {
      super(true, 8.0F, 3.35F);
      this.body = p_170370_.getChild("body");
      this.head = this.body.getChild("head");
      this.rightHindLeg = this.body.getChild("right_hind_leg");
      this.leftHindLeg = this.body.getChild("left_hind_leg");
      this.rightFrontLeg = this.body.getChild("right_front_leg");
      this.leftFrontLeg = this.body.getChild("left_front_leg");
      this.tail = this.body.getChild("tail");
      this.topGills = this.head.getChild("top_gills");
      this.leftGills = this.head.getChild("left_gills");
      this.rightGills = this.head.getChild("right_gills");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 11).addBox(-4.0F, -2.0F, -9.0F, 8.0F, 4.0F, 10.0F).texOffs(2, 17).addBox(0.0F, -3.0F, -8.0F, 0.0F, 5.0F, 9.0F), PartPose.offset(0.0F, 20.0F, 5.0F));
      CubeDeformation cubedeformation = new CubeDeformation(0.001F);
      PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 1).addBox(-4.0F, -3.0F, -5.0F, 8.0F, 5.0F, 5.0F, cubedeformation), PartPose.offset(0.0F, 0.0F, -9.0F));
      CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(3, 37).addBox(-4.0F, -3.0F, 0.0F, 8.0F, 3.0F, 0.0F, cubedeformation);
      CubeListBuilder cubelistbuilder1 = CubeListBuilder.create().texOffs(0, 40).addBox(-3.0F, -5.0F, 0.0F, 3.0F, 7.0F, 0.0F, cubedeformation);
      CubeListBuilder cubelistbuilder2 = CubeListBuilder.create().texOffs(11, 40).addBox(0.0F, -5.0F, 0.0F, 3.0F, 7.0F, 0.0F, cubedeformation);
      partdefinition2.addOrReplaceChild("top_gills", cubelistbuilder, PartPose.offset(0.0F, -3.0F, -1.0F));
      partdefinition2.addOrReplaceChild("left_gills", cubelistbuilder1, PartPose.offset(-4.0F, 0.0F, -1.0F));
      partdefinition2.addOrReplaceChild("right_gills", cubelistbuilder2, PartPose.offset(4.0F, 0.0F, -1.0F));
      CubeListBuilder cubelistbuilder3 = CubeListBuilder.create().texOffs(2, 13).addBox(-1.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, cubedeformation);
      CubeListBuilder cubelistbuilder4 = CubeListBuilder.create().texOffs(2, 13).addBox(-2.0F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F, cubedeformation);
      partdefinition1.addOrReplaceChild("right_hind_leg", cubelistbuilder4, PartPose.offset(-3.5F, 1.0F, -1.0F));
      partdefinition1.addOrReplaceChild("left_hind_leg", cubelistbuilder3, PartPose.offset(3.5F, 1.0F, -1.0F));
      partdefinition1.addOrReplaceChild("right_front_leg", cubelistbuilder4, PartPose.offset(-3.5F, 1.0F, -8.0F));
      partdefinition1.addOrReplaceChild("left_front_leg", cubelistbuilder3, PartPose.offset(3.5F, 1.0F, -8.0F));
      partdefinition1.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(2, 19).addBox(0.0F, -3.0F, 0.0F, 0.0F, 5.0F, 12.0F), PartPose.offset(0.0F, 0.0F, 1.0F));
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   protected Iterable<ModelPart> headParts() {
      return ImmutableList.of();
   }

   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.body);
   }

   public void setupAnim(T p_170395_, float p_170396_, float p_170397_, float p_170398_, float p_170399_, float p_170400_) {
      this.setupInitialAnimationValues(p_170395_, p_170399_, p_170400_);
      if (p_170395_.isPlayingDead()) {
         this.setupPlayDeadAnimation(p_170399_);
         this.saveAnimationValues(p_170395_);
      } else {
         boolean flag = p_170395_.getDeltaMovement().horizontalDistanceSqr() > 1.0E-7D || p_170395_.getXRot() != p_170395_.xRotO || p_170395_.getYRot() != p_170395_.yRotO || p_170395_.xOld != p_170395_.getX() || p_170395_.zOld != p_170395_.getZ();
         if (p_170395_.isInWaterOrBubble()) {
            if (flag) {
               this.setupSwimmingAnimation(p_170398_, p_170400_);
            } else {
               this.setupWaterHoveringAnimation(p_170398_);
            }

            this.saveAnimationValues(p_170395_);
         } else {
            if (p_170395_.isOnGround()) {
               if (flag) {
                  this.setupGroundCrawlingAnimation(p_170398_, p_170399_);
               } else {
                  this.setupLayStillOnGroundAnimation(p_170398_, p_170399_);
               }
            }

            this.saveAnimationValues(p_170395_);
         }
      }
   }

   private void saveAnimationValues(T p_170389_) {
      Map<String, Vector3f> map = p_170389_.getModelRotationValues();
      map.put("body", this.getRotationVector(this.body));
      map.put("head", this.getRotationVector(this.head));
      map.put("right_hind_leg", this.getRotationVector(this.rightHindLeg));
      map.put("left_hind_leg", this.getRotationVector(this.leftHindLeg));
      map.put("right_front_leg", this.getRotationVector(this.rightFrontLeg));
      map.put("left_front_leg", this.getRotationVector(this.leftFrontLeg));
      map.put("tail", this.getRotationVector(this.tail));
      map.put("top_gills", this.getRotationVector(this.topGills));
      map.put("left_gills", this.getRotationVector(this.leftGills));
      map.put("right_gills", this.getRotationVector(this.rightGills));
   }

   private Vector3f getRotationVector(ModelPart p_170402_) {
      return new Vector3f(p_170402_.xRot, p_170402_.yRot, p_170402_.zRot);
   }

   private void setRotationFromVector(ModelPart p_170409_, Vector3f p_170410_) {
      p_170409_.setRotation(p_170410_.x(), p_170410_.y(), p_170410_.z());
   }

   private void setupInitialAnimationValues(T p_170391_, float p_170392_, float p_170393_) {
      this.body.x = 0.0F;
      this.head.y = 0.0F;
      this.body.y = 20.0F;
      Map<String, Vector3f> map = p_170391_.getModelRotationValues();
      if (map.isEmpty()) {
         this.body.setRotation(p_170393_ * ((float)Math.PI / 180F), p_170392_ * ((float)Math.PI / 180F), 0.0F);
         this.head.setRotation(0.0F, 0.0F, 0.0F);
         this.leftHindLeg.setRotation(0.0F, 0.0F, 0.0F);
         this.rightHindLeg.setRotation(0.0F, 0.0F, 0.0F);
         this.leftFrontLeg.setRotation(0.0F, 0.0F, 0.0F);
         this.rightFrontLeg.setRotation(0.0F, 0.0F, 0.0F);
         this.leftGills.setRotation(0.0F, 0.0F, 0.0F);
         this.rightGills.setRotation(0.0F, 0.0F, 0.0F);
         this.topGills.setRotation(0.0F, 0.0F, 0.0F);
         this.tail.setRotation(0.0F, 0.0F, 0.0F);
      } else {
         this.setRotationFromVector(this.body, map.get("body"));
         this.setRotationFromVector(this.head, map.get("head"));
         this.setRotationFromVector(this.leftHindLeg, map.get("left_hind_leg"));
         this.setRotationFromVector(this.rightHindLeg, map.get("right_hind_leg"));
         this.setRotationFromVector(this.leftFrontLeg, map.get("left_front_leg"));
         this.setRotationFromVector(this.rightFrontLeg, map.get("right_front_leg"));
         this.setRotationFromVector(this.leftGills, map.get("left_gills"));
         this.setRotationFromVector(this.rightGills, map.get("right_gills"));
         this.setRotationFromVector(this.topGills, map.get("top_gills"));
         this.setRotationFromVector(this.tail, map.get("tail"));
      }

   }

   private float lerpTo(float p_170375_, float p_170376_) {
      return this.lerpTo(0.05F, p_170375_, p_170376_);
   }

   private float lerpTo(float p_170378_, float p_170379_, float p_170380_) {
      return Mth.rotLerp(p_170378_, p_170379_, p_170380_);
   }

   private void lerpPart(ModelPart p_170404_, float p_170405_, float p_170406_, float p_170407_) {
      p_170404_.setRotation(this.lerpTo(p_170404_.xRot, p_170405_), this.lerpTo(p_170404_.yRot, p_170406_), this.lerpTo(p_170404_.zRot, p_170407_));
   }

   private void setupLayStillOnGroundAnimation(float p_170415_, float p_170416_) {
      float f = p_170415_ * 0.09F;
      float f1 = Mth.sin(f);
      float f2 = Mth.cos(f);
      float f3 = f1 * f1 - 2.0F * f1;
      float f4 = f2 * f2 - 3.0F * f1;
      this.head.xRot = this.lerpTo(this.head.xRot, -0.09F * f3);
      this.head.yRot = this.lerpTo(this.head.yRot, 0.0F);
      this.head.zRot = this.lerpTo(this.head.zRot, -0.2F);
      this.tail.yRot = this.lerpTo(this.tail.yRot, -0.1F + 0.1F * f3);
      this.topGills.xRot = this.lerpTo(this.topGills.xRot, 0.6F + 0.05F * f4);
      this.leftGills.yRot = this.lerpTo(this.leftGills.yRot, -this.topGills.xRot);
      this.rightGills.yRot = this.lerpTo(this.rightGills.yRot, -this.leftGills.yRot);
      this.lerpPart(this.leftHindLeg, 1.1F, 1.0F, 0.0F);
      this.lerpPart(this.leftFrontLeg, 0.8F, 2.3F, -0.5F);
      this.applyMirrorLegRotations();
      this.body.xRot = this.lerpTo(0.2F, this.body.xRot, 0.0F);
      this.body.yRot = this.lerpTo(this.body.yRot, p_170416_ * ((float)Math.PI / 180F));
      this.body.zRot = this.lerpTo(this.body.zRot, 0.0F);
   }

   private void setupGroundCrawlingAnimation(float p_170419_, float p_170420_) {
      float f = p_170419_ * 0.11F;
      float f1 = Mth.cos(f);
      float f2 = (f1 * f1 - 2.0F * f1) / 5.0F;
      float f3 = 0.7F * f1;
      this.head.xRot = this.lerpTo(this.head.xRot, 0.0F);
      this.head.yRot = this.lerpTo(this.head.yRot, 0.09F * f1);
      this.head.zRot = this.lerpTo(this.head.zRot, 0.0F);
      this.tail.yRot = this.lerpTo(this.tail.yRot, this.head.yRot);
      this.topGills.xRot = this.lerpTo(this.topGills.xRot, 0.6F - 0.08F * (f1 * f1 + 2.0F * Mth.sin(f)));
      this.leftGills.yRot = this.lerpTo(this.leftGills.yRot, -this.topGills.xRot);
      this.rightGills.yRot = this.lerpTo(this.rightGills.yRot, -this.leftGills.yRot);
      this.lerpPart(this.leftHindLeg, 0.9424779F, 1.5F - f2, -0.1F);
      this.lerpPart(this.leftFrontLeg, 1.0995574F, ((float)Math.PI / 2F) - f3, 0.0F);
      this.lerpPart(this.rightHindLeg, this.leftHindLeg.xRot, -1.0F - f2, 0.0F);
      this.lerpPart(this.rightFrontLeg, this.leftFrontLeg.xRot, (-(float)Math.PI / 2F) - f3, 0.0F);
      this.body.xRot = this.lerpTo(0.2F, this.body.xRot, 0.0F);
      this.body.yRot = this.lerpTo(this.body.yRot, p_170420_ * ((float)Math.PI / 180F));
      this.body.zRot = this.lerpTo(this.body.zRot, 0.0F);
   }

   private void setupWaterHoveringAnimation(float p_170373_) {
      float f = p_170373_ * 0.075F;
      float f1 = Mth.cos(f);
      float f2 = Mth.sin(f) * 0.15F;
      this.body.xRot = this.lerpTo(this.body.xRot, -0.15F + 0.075F * f1);
      this.body.y -= f2;
      this.head.xRot = this.lerpTo(this.head.xRot, -this.body.xRot);
      this.topGills.xRot = this.lerpTo(this.topGills.xRot, 0.2F * f1);
      this.leftGills.yRot = this.lerpTo(this.leftGills.yRot, -0.3F * f1 - 0.19F);
      this.rightGills.yRot = this.lerpTo(this.rightGills.yRot, -this.leftGills.yRot);
      this.lerpPart(this.leftHindLeg, 2.3561945F - f1 * 0.11F, 0.47123894F, 1.7278761F);
      this.lerpPart(this.leftFrontLeg, ((float)Math.PI / 4F) - f1 * 0.2F, 2.042035F, 0.0F);
      this.applyMirrorLegRotations();
      this.tail.yRot = this.lerpTo(this.tail.yRot, 0.5F * f1);
      this.head.yRot = this.lerpTo(this.head.yRot, 0.0F);
      this.head.zRot = this.lerpTo(this.head.zRot, 0.0F);
   }

   private void setupSwimmingAnimation(float p_170423_, float p_170424_) {
      float f = p_170423_ * 0.33F;
      float f1 = Mth.sin(f);
      float f2 = Mth.cos(f);
      float f3 = 0.13F * f1;
      this.body.xRot = this.lerpTo(0.1F, this.body.xRot, p_170424_ * ((float)Math.PI / 180F) + f3);
      this.head.xRot = -f3 * 1.8F;
      this.body.y -= 0.45F * f2;
      this.topGills.xRot = this.lerpTo(this.topGills.xRot, -0.5F * f1 - 0.8F);
      this.leftGills.yRot = this.lerpTo(this.leftGills.yRot, 0.3F * f1 + 0.9F);
      this.rightGills.yRot = this.lerpTo(this.rightGills.yRot, -this.leftGills.yRot);
      this.tail.yRot = this.lerpTo(this.tail.yRot, 0.3F * Mth.cos(f * 0.9F));
      this.lerpPart(this.leftHindLeg, 1.8849558F, -0.4F * f1, ((float)Math.PI / 2F));
      this.lerpPart(this.leftFrontLeg, 1.8849558F, -0.2F * f2 - 0.1F, ((float)Math.PI / 2F));
      this.applyMirrorLegRotations();
      this.head.yRot = this.lerpTo(this.head.yRot, 0.0F);
      this.head.zRot = this.lerpTo(this.head.zRot, 0.0F);
   }

   private void setupPlayDeadAnimation(float p_170413_) {
      this.lerpPart(this.leftHindLeg, 1.4137167F, 1.0995574F, ((float)Math.PI / 4F));
      this.lerpPart(this.leftFrontLeg, ((float)Math.PI / 4F), 2.042035F, 0.0F);
      this.body.xRot = this.lerpTo(this.body.xRot, -0.15F);
      this.body.zRot = this.lerpTo(this.body.zRot, 0.35F);
      this.applyMirrorLegRotations();
      this.body.yRot = this.lerpTo(this.body.yRot, p_170413_ * ((float)Math.PI / 180F));
      this.head.xRot = this.lerpTo(this.head.xRot, 0.0F);
      this.head.yRot = this.lerpTo(this.head.yRot, 0.0F);
      this.head.zRot = this.lerpTo(this.head.zRot, 0.0F);
      this.tail.yRot = this.lerpTo(this.tail.yRot, 0.0F);
      this.lerpPart(this.topGills, 0.0F, 0.0F, 0.0F);
      this.lerpPart(this.leftGills, 0.0F, 0.0F, 0.0F);
      this.lerpPart(this.rightGills, 0.0F, 0.0F, 0.0F);
   }

   private void applyMirrorLegRotations() {
      this.lerpPart(this.rightHindLeg, this.leftHindLeg.xRot, -this.leftHindLeg.yRot, -this.leftHindLeg.zRot);
      this.lerpPart(this.rightFrontLeg, this.leftFrontLeg.xRot, -this.leftFrontLeg.yRot, -this.leftFrontLeg.zRot);
   }
}