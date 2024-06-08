package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Fox;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoxModel<T extends Fox> extends AgeableListModel<T> {
   public final ModelPart head;
   private final ModelPart body;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;
   private final ModelPart tail;
   private static final int LEG_SIZE = 6;
   private static final float HEAD_HEIGHT = 16.5F;
   private static final float LEG_POS = 17.5F;
   private float legMotionPos;

   public FoxModel(ModelPart p_170566_) {
      super(true, 8.0F, 3.35F);
      this.head = p_170566_.getChild("head");
      this.body = p_170566_.getChild("body");
      this.rightHindLeg = p_170566_.getChild("right_hind_leg");
      this.leftHindLeg = p_170566_.getChild("left_hind_leg");
      this.rightFrontLeg = p_170566_.getChild("right_front_leg");
      this.leftFrontLeg = p_170566_.getChild("left_front_leg");
      this.tail = this.body.getChild("tail");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(1, 5).addBox(-3.0F, -2.0F, -5.0F, 8.0F, 6.0F, 6.0F), PartPose.offset(-1.0F, 16.5F, -3.0F));
      partdefinition1.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(8, 1).addBox(-3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), PartPose.ZERO);
      partdefinition1.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(15, 1).addBox(3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), PartPose.ZERO);
      partdefinition1.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(6, 18).addBox(-1.0F, 2.01F, -8.0F, 4.0F, 2.0F, 3.0F), PartPose.ZERO);
      PartDefinition partdefinition2 = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(24, 15).addBox(-3.0F, 3.999F, -3.5F, 6.0F, 11.0F, 6.0F), PartPose.offsetAndRotation(0.0F, 16.0F, -6.0F, ((float)Math.PI / 2F), 0.0F, 0.0F));
      CubeDeformation cubedeformation = new CubeDeformation(0.001F);
      CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(4, 24).addBox(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, cubedeformation);
      CubeListBuilder cubelistbuilder1 = CubeListBuilder.create().texOffs(13, 24).addBox(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, cubedeformation);
      partdefinition.addOrReplaceChild("right_hind_leg", cubelistbuilder1, PartPose.offset(-5.0F, 17.5F, 7.0F));
      partdefinition.addOrReplaceChild("left_hind_leg", cubelistbuilder, PartPose.offset(-1.0F, 17.5F, 7.0F));
      partdefinition.addOrReplaceChild("right_front_leg", cubelistbuilder1, PartPose.offset(-5.0F, 17.5F, 0.0F));
      partdefinition.addOrReplaceChild("left_front_leg", cubelistbuilder, PartPose.offset(-1.0F, 17.5F, 0.0F));
      partdefinition2.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(30, 0).addBox(2.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F), PartPose.offsetAndRotation(-4.0F, 15.0F, -1.0F, -0.05235988F, 0.0F, 0.0F));
      return LayerDefinition.create(meshdefinition, 48, 32);
   }

   public void prepareMobModel(T p_102664_, float p_102665_, float p_102666_, float p_102667_) {
      this.body.xRot = ((float)Math.PI / 2F);
      this.tail.xRot = -0.05235988F;
      this.rightHindLeg.xRot = Mth.cos(p_102665_ * 0.6662F) * 1.4F * p_102666_;
      this.leftHindLeg.xRot = Mth.cos(p_102665_ * 0.6662F + (float)Math.PI) * 1.4F * p_102666_;
      this.rightFrontLeg.xRot = Mth.cos(p_102665_ * 0.6662F + (float)Math.PI) * 1.4F * p_102666_;
      this.leftFrontLeg.xRot = Mth.cos(p_102665_ * 0.6662F) * 1.4F * p_102666_;
      this.head.setPos(-1.0F, 16.5F, -3.0F);
      this.head.yRot = 0.0F;
      this.head.zRot = p_102664_.getHeadRollAngle(p_102667_);
      this.rightHindLeg.visible = true;
      this.leftHindLeg.visible = true;
      this.rightFrontLeg.visible = true;
      this.leftFrontLeg.visible = true;
      this.body.setPos(0.0F, 16.0F, -6.0F);
      this.body.zRot = 0.0F;
      this.rightHindLeg.setPos(-5.0F, 17.5F, 7.0F);
      this.leftHindLeg.setPos(-1.0F, 17.5F, 7.0F);
      if (p_102664_.isCrouching()) {
         this.body.xRot = 1.6755161F;
         float f = p_102664_.getCrouchAmount(p_102667_);
         this.body.setPos(0.0F, 16.0F + p_102664_.getCrouchAmount(p_102667_), -6.0F);
         this.head.setPos(-1.0F, 16.5F + f, -3.0F);
         this.head.yRot = 0.0F;
      } else if (p_102664_.isSleeping()) {
         this.body.zRot = (-(float)Math.PI / 2F);
         this.body.setPos(0.0F, 21.0F, -6.0F);
         this.tail.xRot = -2.6179938F;
         if (this.young) {
            this.tail.xRot = -2.1816616F;
            this.body.setPos(0.0F, 21.0F, -2.0F);
         }

         this.head.setPos(1.0F, 19.49F, -3.0F);
         this.head.xRot = 0.0F;
         this.head.yRot = -2.0943952F;
         this.head.zRot = 0.0F;
         this.rightHindLeg.visible = false;
         this.leftHindLeg.visible = false;
         this.rightFrontLeg.visible = false;
         this.leftFrontLeg.visible = false;
      } else if (p_102664_.isSitting()) {
         this.body.xRot = ((float)Math.PI / 6F);
         this.body.setPos(0.0F, 9.0F, -3.0F);
         this.tail.xRot = ((float)Math.PI / 4F);
         this.tail.setPos(-4.0F, 15.0F, -2.0F);
         this.head.setPos(-1.0F, 10.0F, -0.25F);
         this.head.xRot = 0.0F;
         this.head.yRot = 0.0F;
         if (this.young) {
            this.head.setPos(-1.0F, 13.0F, -3.75F);
         }

         this.rightHindLeg.xRot = -1.3089969F;
         this.rightHindLeg.setPos(-5.0F, 21.5F, 6.75F);
         this.leftHindLeg.xRot = -1.3089969F;
         this.leftHindLeg.setPos(-1.0F, 21.5F, 6.75F);
         this.rightFrontLeg.xRot = -0.2617994F;
         this.leftFrontLeg.xRot = -0.2617994F;
      }

   }

   protected Iterable<ModelPart> headParts() {
      return ImmutableList.of(this.head);
   }

   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg);
   }

   public void setupAnim(T p_102669_, float p_102670_, float p_102671_, float p_102672_, float p_102673_, float p_102674_) {
      if (!p_102669_.isSleeping() && !p_102669_.isFaceplanted() && !p_102669_.isCrouching()) {
         this.head.xRot = p_102674_ * ((float)Math.PI / 180F);
         this.head.yRot = p_102673_ * ((float)Math.PI / 180F);
      }

      if (p_102669_.isSleeping()) {
         this.head.xRot = 0.0F;
         this.head.yRot = -2.0943952F;
         this.head.zRot = Mth.cos(p_102672_ * 0.027F) / 22.0F;
      }

      if (p_102669_.isCrouching()) {
         float f = Mth.cos(p_102672_) * 0.01F;
         this.body.yRot = f;
         this.rightHindLeg.zRot = f;
         this.leftHindLeg.zRot = f;
         this.rightFrontLeg.zRot = f / 2.0F;
         this.leftFrontLeg.zRot = f / 2.0F;
      }

      if (p_102669_.isFaceplanted()) {
         float f1 = 0.1F;
         this.legMotionPos += 0.67F;
         this.rightHindLeg.xRot = Mth.cos(this.legMotionPos * 0.4662F) * 0.1F;
         this.leftHindLeg.xRot = Mth.cos(this.legMotionPos * 0.4662F + (float)Math.PI) * 0.1F;
         this.rightFrontLeg.xRot = Mth.cos(this.legMotionPos * 0.4662F + (float)Math.PI) * 0.1F;
         this.leftFrontLeg.xRot = Mth.cos(this.legMotionPos * 0.4662F) * 0.1F;
      }

   }
}