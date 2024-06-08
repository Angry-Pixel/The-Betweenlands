package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArmorStandArmorModel extends HumanoidModel<ArmorStand> {
   public ArmorStandArmorModel(ModelPart p_170346_) {
      super(p_170346_);
   }

   public static LayerDefinition createBodyLayer(CubeDeformation p_170348_) {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(p_170348_, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_170348_), PartPose.offset(0.0F, 1.0F, 0.0F));
      partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_170348_.extend(0.5F)), PartPose.offset(0.0F, 1.0F, 0.0F));
      partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170348_), PartPose.offset(-1.9F, 11.0F, 0.0F));
      partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_170348_), PartPose.offset(1.9F, 11.0F, 0.0F));
      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   public void setupAnim(ArmorStand p_102131_, float p_102132_, float p_102133_, float p_102134_, float p_102135_, float p_102136_) {
      this.head.xRot = ((float)Math.PI / 180F) * p_102131_.getHeadPose().getX();
      this.head.yRot = ((float)Math.PI / 180F) * p_102131_.getHeadPose().getY();
      this.head.zRot = ((float)Math.PI / 180F) * p_102131_.getHeadPose().getZ();
      this.body.xRot = ((float)Math.PI / 180F) * p_102131_.getBodyPose().getX();
      this.body.yRot = ((float)Math.PI / 180F) * p_102131_.getBodyPose().getY();
      this.body.zRot = ((float)Math.PI / 180F) * p_102131_.getBodyPose().getZ();
      this.leftArm.xRot = ((float)Math.PI / 180F) * p_102131_.getLeftArmPose().getX();
      this.leftArm.yRot = ((float)Math.PI / 180F) * p_102131_.getLeftArmPose().getY();
      this.leftArm.zRot = ((float)Math.PI / 180F) * p_102131_.getLeftArmPose().getZ();
      this.rightArm.xRot = ((float)Math.PI / 180F) * p_102131_.getRightArmPose().getX();
      this.rightArm.yRot = ((float)Math.PI / 180F) * p_102131_.getRightArmPose().getY();
      this.rightArm.zRot = ((float)Math.PI / 180F) * p_102131_.getRightArmPose().getZ();
      this.leftLeg.xRot = ((float)Math.PI / 180F) * p_102131_.getLeftLegPose().getX();
      this.leftLeg.yRot = ((float)Math.PI / 180F) * p_102131_.getLeftLegPose().getY();
      this.leftLeg.zRot = ((float)Math.PI / 180F) * p_102131_.getLeftLegPose().getZ();
      this.rightLeg.xRot = ((float)Math.PI / 180F) * p_102131_.getRightLegPose().getX();
      this.rightLeg.yRot = ((float)Math.PI / 180F) * p_102131_.getRightLegPose().getY();
      this.rightLeg.zRot = ((float)Math.PI / 180F) * p_102131_.getRightLegPose().getZ();
      this.hat.copyFrom(this.head);
   }
}