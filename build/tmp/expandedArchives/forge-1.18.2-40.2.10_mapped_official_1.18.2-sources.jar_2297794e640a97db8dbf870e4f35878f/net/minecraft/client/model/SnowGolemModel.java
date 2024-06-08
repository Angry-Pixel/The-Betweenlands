package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SnowGolemModel<T extends Entity> extends HierarchicalModel<T> {
   private static final String UPPER_BODY = "upper_body";
   private final ModelPart root;
   private final ModelPart upperBody;
   private final ModelPart head;
   private final ModelPart leftArm;
   private final ModelPart rightArm;

   public SnowGolemModel(ModelPart p_170965_) {
      this.root = p_170965_;
      this.head = p_170965_.getChild("head");
      this.leftArm = p_170965_.getChild("left_arm");
      this.rightArm = p_170965_.getChild("right_arm");
      this.upperBody = p_170965_.getChild("upper_body");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      float f = 4.0F;
      CubeDeformation cubedeformation = new CubeDeformation(-0.5F);
      partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, cubedeformation), PartPose.offset(0.0F, 4.0F, 0.0F));
      CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, 0.0F, -1.0F, 12.0F, 2.0F, 2.0F, cubedeformation);
      partdefinition.addOrReplaceChild("left_arm", cubelistbuilder, PartPose.offsetAndRotation(5.0F, 6.0F, 1.0F, 0.0F, 0.0F, 1.0F));
      partdefinition.addOrReplaceChild("right_arm", cubelistbuilder, PartPose.offsetAndRotation(-5.0F, 6.0F, -1.0F, 0.0F, (float)Math.PI, -1.0F));
      partdefinition.addOrReplaceChild("upper_body", CubeListBuilder.create().texOffs(0, 16).addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, cubedeformation), PartPose.offset(0.0F, 13.0F, 0.0F));
      partdefinition.addOrReplaceChild("lower_body", CubeListBuilder.create().texOffs(0, 36).addBox(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F, cubedeformation), PartPose.offset(0.0F, 24.0F, 0.0F));
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public void setupAnim(T p_103845_, float p_103846_, float p_103847_, float p_103848_, float p_103849_, float p_103850_) {
      this.head.yRot = p_103849_ * ((float)Math.PI / 180F);
      this.head.xRot = p_103850_ * ((float)Math.PI / 180F);
      this.upperBody.yRot = p_103849_ * ((float)Math.PI / 180F) * 0.25F;
      float f = Mth.sin(this.upperBody.yRot);
      float f1 = Mth.cos(this.upperBody.yRot);
      this.leftArm.yRot = this.upperBody.yRot;
      this.rightArm.yRot = this.upperBody.yRot + (float)Math.PI;
      this.leftArm.x = f1 * 5.0F;
      this.leftArm.z = -f * 5.0F;
      this.rightArm.x = -f1 * 5.0F;
      this.rightArm.z = f * 5.0F;
   }

   public ModelPart root() {
      return this.root;
   }

   public ModelPart getHead() {
      return this.head;
   }
}