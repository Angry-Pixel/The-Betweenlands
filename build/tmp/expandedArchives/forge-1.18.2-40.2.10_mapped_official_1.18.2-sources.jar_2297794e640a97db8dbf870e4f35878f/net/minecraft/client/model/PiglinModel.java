package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PiglinModel<T extends Mob> extends PlayerModel<T> {
   public final ModelPart rightEar = this.head.getChild("right_ear");
   private final ModelPart leftEar = this.head.getChild("left_ear");
   private final PartPose bodyDefault = this.body.storePose();
   private final PartPose headDefault = this.head.storePose();
   private final PartPose leftArmDefault = this.leftArm.storePose();
   private final PartPose rightArmDefault = this.rightArm.storePose();

   public PiglinModel(ModelPart p_170810_) {
      super(p_170810_, false);
   }

   public static MeshDefinition createMesh(CubeDeformation p_170812_) {
      MeshDefinition meshdefinition = PlayerModel.createMesh(p_170812_, false);
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, p_170812_), PartPose.ZERO);
      PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, p_170812_).texOffs(31, 1).addBox(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, p_170812_).texOffs(2, 4).addBox(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, p_170812_).texOffs(2, 0).addBox(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, p_170812_), PartPose.ZERO);
      partdefinition1.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(51, 6).addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, p_170812_), PartPose.offsetAndRotation(4.5F, -6.0F, 0.0F, 0.0F, 0.0F, (-(float)Math.PI / 6F)));
      partdefinition1.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(39, 6).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, p_170812_), PartPose.offsetAndRotation(-4.5F, -6.0F, 0.0F, 0.0F, 0.0F, ((float)Math.PI / 6F)));
      partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
      return meshdefinition;
   }

   public void setupAnim(T p_103366_, float p_103367_, float p_103368_, float p_103369_, float p_103370_, float p_103371_) {
      this.body.loadPose(this.bodyDefault);
      this.head.loadPose(this.headDefault);
      this.leftArm.loadPose(this.leftArmDefault);
      this.rightArm.loadPose(this.rightArmDefault);
      super.setupAnim(p_103366_, p_103367_, p_103368_, p_103369_, p_103370_, p_103371_);
      float f = ((float)Math.PI / 6F);
      float f1 = p_103369_ * 0.1F + p_103367_ * 0.5F;
      float f2 = 0.08F + p_103368_ * 0.4F;
      this.leftEar.zRot = (-(float)Math.PI / 6F) - Mth.cos(f1 * 1.2F) * f2;
      this.rightEar.zRot = ((float)Math.PI / 6F) + Mth.cos(f1) * f2;
      if (p_103366_ instanceof AbstractPiglin) {
         AbstractPiglin abstractpiglin = (AbstractPiglin)p_103366_;
         PiglinArmPose piglinarmpose = abstractpiglin.getArmPose();
         if (piglinarmpose == PiglinArmPose.DANCING) {
            float f3 = p_103369_ / 60.0F;
            this.rightEar.zRot = ((float)Math.PI / 6F) + ((float)Math.PI / 180F) * Mth.sin(f3 * 30.0F) * 10.0F;
            this.leftEar.zRot = (-(float)Math.PI / 6F) - ((float)Math.PI / 180F) * Mth.cos(f3 * 30.0F) * 10.0F;
            this.head.x = Mth.sin(f3 * 10.0F);
            this.head.y = Mth.sin(f3 * 40.0F) + 0.4F;
            this.rightArm.zRot = ((float)Math.PI / 180F) * (70.0F + Mth.cos(f3 * 40.0F) * 10.0F);
            this.leftArm.zRot = this.rightArm.zRot * -1.0F;
            this.rightArm.y = Mth.sin(f3 * 40.0F) * 0.5F + 1.5F;
            this.leftArm.y = Mth.sin(f3 * 40.0F) * 0.5F + 1.5F;
            this.body.y = Mth.sin(f3 * 40.0F) * 0.35F;
         } else if (piglinarmpose == PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON && this.attackTime == 0.0F) {
            this.holdWeaponHigh(p_103366_);
         } else if (piglinarmpose == PiglinArmPose.CROSSBOW_HOLD) {
            AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, !p_103366_.isLeftHanded());
         } else if (piglinarmpose == PiglinArmPose.CROSSBOW_CHARGE) {
            AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, p_103366_, !p_103366_.isLeftHanded());
         } else if (piglinarmpose == PiglinArmPose.ADMIRING_ITEM) {
            this.head.xRot = 0.5F;
            this.head.yRot = 0.0F;
            if (p_103366_.isLeftHanded()) {
               this.rightArm.yRot = -0.5F;
               this.rightArm.xRot = -0.9F;
            } else {
               this.leftArm.yRot = 0.5F;
               this.leftArm.xRot = -0.9F;
            }
         }
      } else if (p_103366_.getType() == EntityType.ZOMBIFIED_PIGLIN) {
         AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, p_103366_.isAggressive(), this.attackTime, p_103369_);
      }

      this.leftPants.copyFrom(this.leftLeg);
      this.rightPants.copyFrom(this.rightLeg);
      this.leftSleeve.copyFrom(this.leftArm);
      this.rightSleeve.copyFrom(this.rightArm);
      this.jacket.copyFrom(this.body);
      this.hat.copyFrom(this.head);
   }

   protected void setupAttackAnimation(T p_103363_, float p_103364_) {
      if (this.attackTime > 0.0F && p_103363_ instanceof Piglin && ((Piglin)p_103363_).getArmPose() == PiglinArmPose.ATTACKING_WITH_MELEE_WEAPON) {
         AnimationUtils.swingWeaponDown(this.rightArm, this.leftArm, p_103363_, this.attackTime, p_103364_);
      } else {
         super.setupAttackAnimation(p_103363_, p_103364_);
      }
   }

   private void holdWeaponHigh(T p_103361_) {
      if (p_103361_.isLeftHanded()) {
         this.leftArm.xRot = -1.8F;
      } else {
         this.rightArm.xRot = -1.8F;
      }

   }
}