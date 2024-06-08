package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Function;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HumanoidModel<T extends LivingEntity> extends AgeableListModel<T> implements ArmedModel, HeadedModel {
   public static final float OVERLAY_SCALE = 0.25F;
   public static final float HAT_OVERLAY_SCALE = 0.5F;
   private static final float SPYGLASS_ARM_ROT_Y = 0.2617994F;
   private static final float SPYGLASS_ARM_ROT_X = 1.9198622F;
   private static final float SPYGLASS_ARM_CROUCH_ROT_X = 0.2617994F;
   public final ModelPart head;
   public final ModelPart hat;
   public final ModelPart body;
   public final ModelPart rightArm;
   public final ModelPart leftArm;
   public final ModelPart rightLeg;
   public final ModelPart leftLeg;
   public HumanoidModel.ArmPose leftArmPose = HumanoidModel.ArmPose.EMPTY;
   public HumanoidModel.ArmPose rightArmPose = HumanoidModel.ArmPose.EMPTY;
   public boolean crouching;
   public float swimAmount;

   public HumanoidModel(ModelPart p_170677_) {
      this(p_170677_, RenderType::entityCutoutNoCull);
   }

   public HumanoidModel(ModelPart p_170679_, Function<ResourceLocation, RenderType> p_170680_) {
      super(p_170680_, true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F);
      this.head = p_170679_.getChild("head");
      this.hat = p_170679_.getChild("hat");
      this.body = p_170679_.getChild("body");
      this.rightArm = p_170679_.getChild("right_arm");
      this.leftArm = p_170679_.getChild("left_arm");
      this.rightLeg = p_170679_.getChild("right_leg");
      this.leftLeg = p_170679_.getChild("left_leg");
   }

   public static MeshDefinition createMesh(CubeDeformation p_170682_, float p_170683_) {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_170682_), PartPose.offset(0.0F, 0.0F + p_170683_, 0.0F));
      partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_170682_.extend(0.5F)), PartPose.offset(0.0F, 0.0F + p_170683_, 0.0F));
      partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, p_170682_), PartPose.offset(0.0F, 0.0F + p_170683_, 0.0F));
      partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170682_), PartPose.offset(-5.0F, 2.0F + p_170683_, 0.0F));
      partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170682_), PartPose.offset(5.0F, 2.0F + p_170683_, 0.0F));
      partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170682_), PartPose.offset(-1.9F, 12.0F + p_170683_, 0.0F));
      partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170682_), PartPose.offset(1.9F, 12.0F + p_170683_, 0.0F));
      return meshdefinition;
   }

   protected Iterable<ModelPart> headParts() {
      return ImmutableList.of(this.head);
   }

   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.hat);
   }

   public void prepareMobModel(T p_102861_, float p_102862_, float p_102863_, float p_102864_) {
      this.swimAmount = p_102861_.getSwimAmount(p_102864_);
      super.prepareMobModel(p_102861_, p_102862_, p_102863_, p_102864_);
   }

   public void setupAnim(T p_102866_, float p_102867_, float p_102868_, float p_102869_, float p_102870_, float p_102871_) {
      boolean flag = p_102866_.getFallFlyingTicks() > 4;
      boolean flag1 = p_102866_.isVisuallySwimming();
      this.head.yRot = p_102870_ * ((float)Math.PI / 180F);
      if (flag) {
         this.head.xRot = (-(float)Math.PI / 4F);
      } else if (this.swimAmount > 0.0F) {
         if (flag1) {
            this.head.xRot = this.rotlerpRad(this.swimAmount, this.head.xRot, (-(float)Math.PI / 4F));
         } else {
            this.head.xRot = this.rotlerpRad(this.swimAmount, this.head.xRot, p_102871_ * ((float)Math.PI / 180F));
         }
      } else {
         this.head.xRot = p_102871_ * ((float)Math.PI / 180F);
      }

      this.body.yRot = 0.0F;
      this.rightArm.z = 0.0F;
      this.rightArm.x = -5.0F;
      this.leftArm.z = 0.0F;
      this.leftArm.x = 5.0F;
      float f = 1.0F;
      if (flag) {
         f = (float)p_102866_.getDeltaMovement().lengthSqr();
         f /= 0.2F;
         f *= f * f;
      }

      if (f < 1.0F) {
         f = 1.0F;
      }

      this.rightArm.xRot = Mth.cos(p_102867_ * 0.6662F + (float)Math.PI) * 2.0F * p_102868_ * 0.5F / f;
      this.leftArm.xRot = Mth.cos(p_102867_ * 0.6662F) * 2.0F * p_102868_ * 0.5F / f;
      this.rightArm.zRot = 0.0F;
      this.leftArm.zRot = 0.0F;
      this.rightLeg.xRot = Mth.cos(p_102867_ * 0.6662F) * 1.4F * p_102868_ / f;
      this.leftLeg.xRot = Mth.cos(p_102867_ * 0.6662F + (float)Math.PI) * 1.4F * p_102868_ / f;
      this.rightLeg.yRot = 0.0F;
      this.leftLeg.yRot = 0.0F;
      this.rightLeg.zRot = 0.0F;
      this.leftLeg.zRot = 0.0F;
      if (this.riding) {
         this.rightArm.xRot += (-(float)Math.PI / 5F);
         this.leftArm.xRot += (-(float)Math.PI / 5F);
         this.rightLeg.xRot = -1.4137167F;
         this.rightLeg.yRot = ((float)Math.PI / 10F);
         this.rightLeg.zRot = 0.07853982F;
         this.leftLeg.xRot = -1.4137167F;
         this.leftLeg.yRot = (-(float)Math.PI / 10F);
         this.leftLeg.zRot = -0.07853982F;
      }

      this.rightArm.yRot = 0.0F;
      this.leftArm.yRot = 0.0F;
      boolean flag2 = p_102866_.getMainArm() == HumanoidArm.RIGHT;
      if (p_102866_.isUsingItem()) {
         boolean flag3 = p_102866_.getUsedItemHand() == InteractionHand.MAIN_HAND;
         if (flag3 == flag2) {
            this.poseRightArm(p_102866_);
         } else {
            this.poseLeftArm(p_102866_);
         }
      } else {
         boolean flag4 = flag2 ? this.leftArmPose.isTwoHanded() : this.rightArmPose.isTwoHanded();
         if (flag2 != flag4) {
            this.poseLeftArm(p_102866_);
            this.poseRightArm(p_102866_);
         } else {
            this.poseRightArm(p_102866_);
            this.poseLeftArm(p_102866_);
         }
      }

      this.setupAttackAnimation(p_102866_, p_102869_);
      if (this.crouching) {
         this.body.xRot = 0.5F;
         this.rightArm.xRot += 0.4F;
         this.leftArm.xRot += 0.4F;
         this.rightLeg.z = 4.0F;
         this.leftLeg.z = 4.0F;
         this.rightLeg.y = 12.2F;
         this.leftLeg.y = 12.2F;
         this.head.y = 4.2F;
         this.body.y = 3.2F;
         this.leftArm.y = 5.2F;
         this.rightArm.y = 5.2F;
      } else {
         this.body.xRot = 0.0F;
         this.rightLeg.z = 0.1F;
         this.leftLeg.z = 0.1F;
         this.rightLeg.y = 12.0F;
         this.leftLeg.y = 12.0F;
         this.head.y = 0.0F;
         this.body.y = 0.0F;
         this.leftArm.y = 2.0F;
         this.rightArm.y = 2.0F;
      }

      if (this.rightArmPose != HumanoidModel.ArmPose.SPYGLASS) {
         AnimationUtils.bobModelPart(this.rightArm, p_102869_, 1.0F);
      }

      if (this.leftArmPose != HumanoidModel.ArmPose.SPYGLASS) {
         AnimationUtils.bobModelPart(this.leftArm, p_102869_, -1.0F);
      }

      if (this.swimAmount > 0.0F) {
         float f5 = p_102867_ % 26.0F;
         HumanoidArm humanoidarm = this.getAttackArm(p_102866_);
         float f1 = humanoidarm == HumanoidArm.RIGHT && this.attackTime > 0.0F ? 0.0F : this.swimAmount;
         float f2 = humanoidarm == HumanoidArm.LEFT && this.attackTime > 0.0F ? 0.0F : this.swimAmount;
         if (!p_102866_.isUsingItem()) {
            if (f5 < 14.0F) {
               this.leftArm.xRot = this.rotlerpRad(f2, this.leftArm.xRot, 0.0F);
               this.rightArm.xRot = Mth.lerp(f1, this.rightArm.xRot, 0.0F);
               this.leftArm.yRot = this.rotlerpRad(f2, this.leftArm.yRot, (float)Math.PI);
               this.rightArm.yRot = Mth.lerp(f1, this.rightArm.yRot, (float)Math.PI);
               this.leftArm.zRot = this.rotlerpRad(f2, this.leftArm.zRot, (float)Math.PI + 1.8707964F * this.quadraticArmUpdate(f5) / this.quadraticArmUpdate(14.0F));
               this.rightArm.zRot = Mth.lerp(f1, this.rightArm.zRot, (float)Math.PI - 1.8707964F * this.quadraticArmUpdate(f5) / this.quadraticArmUpdate(14.0F));
            } else if (f5 >= 14.0F && f5 < 22.0F) {
               float f6 = (f5 - 14.0F) / 8.0F;
               this.leftArm.xRot = this.rotlerpRad(f2, this.leftArm.xRot, ((float)Math.PI / 2F) * f6);
               this.rightArm.xRot = Mth.lerp(f1, this.rightArm.xRot, ((float)Math.PI / 2F) * f6);
               this.leftArm.yRot = this.rotlerpRad(f2, this.leftArm.yRot, (float)Math.PI);
               this.rightArm.yRot = Mth.lerp(f1, this.rightArm.yRot, (float)Math.PI);
               this.leftArm.zRot = this.rotlerpRad(f2, this.leftArm.zRot, 5.012389F - 1.8707964F * f6);
               this.rightArm.zRot = Mth.lerp(f1, this.rightArm.zRot, 1.2707963F + 1.8707964F * f6);
            } else if (f5 >= 22.0F && f5 < 26.0F) {
               float f3 = (f5 - 22.0F) / 4.0F;
               this.leftArm.xRot = this.rotlerpRad(f2, this.leftArm.xRot, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f3);
               this.rightArm.xRot = Mth.lerp(f1, this.rightArm.xRot, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f3);
               this.leftArm.yRot = this.rotlerpRad(f2, this.leftArm.yRot, (float)Math.PI);
               this.rightArm.yRot = Mth.lerp(f1, this.rightArm.yRot, (float)Math.PI);
               this.leftArm.zRot = this.rotlerpRad(f2, this.leftArm.zRot, (float)Math.PI);
               this.rightArm.zRot = Mth.lerp(f1, this.rightArm.zRot, (float)Math.PI);
            }
         }

         float f7 = 0.3F;
         float f4 = 0.33333334F;
         this.leftLeg.xRot = Mth.lerp(this.swimAmount, this.leftLeg.xRot, 0.3F * Mth.cos(p_102867_ * 0.33333334F + (float)Math.PI));
         this.rightLeg.xRot = Mth.lerp(this.swimAmount, this.rightLeg.xRot, 0.3F * Mth.cos(p_102867_ * 0.33333334F));
      }

      this.hat.copyFrom(this.head);
   }

   private void poseRightArm(T p_102876_) {
      switch(this.rightArmPose) {
      case EMPTY:
         this.rightArm.yRot = 0.0F;
         break;
      case BLOCK:
         this.rightArm.xRot = this.rightArm.xRot * 0.5F - 0.9424779F;
         this.rightArm.yRot = (-(float)Math.PI / 6F);
         break;
      case ITEM:
         this.rightArm.xRot = this.rightArm.xRot * 0.5F - ((float)Math.PI / 10F);
         this.rightArm.yRot = 0.0F;
         break;
      case THROW_SPEAR:
         this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float)Math.PI;
         this.rightArm.yRot = 0.0F;
         break;
      case BOW_AND_ARROW:
         this.rightArm.yRot = -0.1F + this.head.yRot;
         this.leftArm.yRot = 0.1F + this.head.yRot + 0.4F;
         this.rightArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot;
         this.leftArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot;
         break;
      case CROSSBOW_CHARGE:
         AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, p_102876_, true);
         break;
      case CROSSBOW_HOLD:
         AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
         break;
      case SPYGLASS:
         this.rightArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (p_102876_.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
         this.rightArm.yRot = this.head.yRot - 0.2617994F;
      }

   }

   private void poseLeftArm(T p_102879_) {
      switch(this.leftArmPose) {
      case EMPTY:
         this.leftArm.yRot = 0.0F;
         break;
      case BLOCK:
         this.leftArm.xRot = this.leftArm.xRot * 0.5F - 0.9424779F;
         this.leftArm.yRot = ((float)Math.PI / 6F);
         break;
      case ITEM:
         this.leftArm.xRot = this.leftArm.xRot * 0.5F - ((float)Math.PI / 10F);
         this.leftArm.yRot = 0.0F;
         break;
      case THROW_SPEAR:
         this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float)Math.PI;
         this.leftArm.yRot = 0.0F;
         break;
      case BOW_AND_ARROW:
         this.rightArm.yRot = -0.1F + this.head.yRot - 0.4F;
         this.leftArm.yRot = 0.1F + this.head.yRot;
         this.rightArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot;
         this.leftArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot;
         break;
      case CROSSBOW_CHARGE:
         AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, p_102879_, false);
         break;
      case CROSSBOW_HOLD:
         AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, false);
         break;
      case SPYGLASS:
         this.leftArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (p_102879_.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
         this.leftArm.yRot = this.head.yRot + 0.2617994F;
      }

   }

   protected void setupAttackAnimation(T p_102858_, float p_102859_) {
      if (!(this.attackTime <= 0.0F)) {
         HumanoidArm humanoidarm = this.getAttackArm(p_102858_);
         ModelPart modelpart = this.getArm(humanoidarm);
         float f = this.attackTime;
         this.body.yRot = Mth.sin(Mth.sqrt(f) * ((float)Math.PI * 2F)) * 0.2F;
         if (humanoidarm == HumanoidArm.LEFT) {
            this.body.yRot *= -1.0F;
         }

         this.rightArm.z = Mth.sin(this.body.yRot) * 5.0F;
         this.rightArm.x = -Mth.cos(this.body.yRot) * 5.0F;
         this.leftArm.z = -Mth.sin(this.body.yRot) * 5.0F;
         this.leftArm.x = Mth.cos(this.body.yRot) * 5.0F;
         this.rightArm.yRot += this.body.yRot;
         this.leftArm.yRot += this.body.yRot;
         this.leftArm.xRot += this.body.yRot;
         f = 1.0F - this.attackTime;
         f *= f;
         f *= f;
         f = 1.0F - f;
         float f1 = Mth.sin(f * (float)Math.PI);
         float f2 = Mth.sin(this.attackTime * (float)Math.PI) * -(this.head.xRot - 0.7F) * 0.75F;
         modelpart.xRot -= f1 * 1.2F + f2;
         modelpart.yRot += this.body.yRot * 2.0F;
         modelpart.zRot += Mth.sin(this.attackTime * (float)Math.PI) * -0.4F;
      }
   }

   protected float rotlerpRad(float p_102836_, float p_102837_, float p_102838_) {
      float f = (p_102838_ - p_102837_) % ((float)Math.PI * 2F);
      if (f < -(float)Math.PI) {
         f += ((float)Math.PI * 2F);
      }

      if (f >= (float)Math.PI) {
         f -= ((float)Math.PI * 2F);
      }

      return p_102837_ + p_102836_ * f;
   }

   private float quadraticArmUpdate(float p_102834_) {
      return -65.0F * p_102834_ + p_102834_ * p_102834_;
   }

   public void copyPropertiesTo(HumanoidModel<T> p_102873_) {
      super.copyPropertiesTo(p_102873_);
      p_102873_.leftArmPose = this.leftArmPose;
      p_102873_.rightArmPose = this.rightArmPose;
      p_102873_.crouching = this.crouching;
      p_102873_.head.copyFrom(this.head);
      p_102873_.hat.copyFrom(this.hat);
      p_102873_.body.copyFrom(this.body);
      p_102873_.rightArm.copyFrom(this.rightArm);
      p_102873_.leftArm.copyFrom(this.leftArm);
      p_102873_.rightLeg.copyFrom(this.rightLeg);
      p_102873_.leftLeg.copyFrom(this.leftLeg);
   }

   public void setAllVisible(boolean p_102880_) {
      this.head.visible = p_102880_;
      this.hat.visible = p_102880_;
      this.body.visible = p_102880_;
      this.rightArm.visible = p_102880_;
      this.leftArm.visible = p_102880_;
      this.rightLeg.visible = p_102880_;
      this.leftLeg.visible = p_102880_;
   }

   public void translateToHand(HumanoidArm p_102854_, PoseStack p_102855_) {
      this.getArm(p_102854_).translateAndRotate(p_102855_);
   }

   protected ModelPart getArm(HumanoidArm p_102852_) {
      return p_102852_ == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
   }

   public ModelPart getHead() {
      return this.head;
   }

   private HumanoidArm getAttackArm(T p_102857_) {
      HumanoidArm humanoidarm = p_102857_.getMainArm();
      return p_102857_.swingingArm == InteractionHand.MAIN_HAND ? humanoidarm : humanoidarm.getOpposite();
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ArmPose {
      EMPTY(false),
      ITEM(false),
      BLOCK(false),
      BOW_AND_ARROW(true),
      THROW_SPEAR(false),
      CROSSBOW_CHARGE(true),
      CROSSBOW_HOLD(true),
      SPYGLASS(false);

      private final boolean twoHanded;

      private ArmPose(boolean p_102896_) {
         this.twoHanded = p_102896_;
      }

      public boolean isTwoHanded() {
         return this.twoHanded;
      }
   }
}