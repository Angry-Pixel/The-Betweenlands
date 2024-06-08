package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SheepModel<T extends Sheep> extends QuadrupedModel<T> {
   private float headXRot;

   public SheepModel(ModelPart p_170903_) {
      super(p_170903_, false, 8.0F, 4.0F, 2.0F, 2.0F, 24);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = QuadrupedModel.createBodyMesh(12, CubeDeformation.NONE);
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -6.0F, 6.0F, 6.0F, 8.0F), PartPose.offset(0.0F, 6.0F, -8.0F));
      partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 8).addBox(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F), PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, ((float)Math.PI / 2F), 0.0F, 0.0F));
      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   public void prepareMobModel(T p_103687_, float p_103688_, float p_103689_, float p_103690_) {
      super.prepareMobModel(p_103687_, p_103688_, p_103689_, p_103690_);
      this.head.y = 6.0F + p_103687_.getHeadEatPositionScale(p_103690_) * 9.0F;
      this.headXRot = p_103687_.getHeadEatAngleScale(p_103690_);
   }

   public void setupAnim(T p_103692_, float p_103693_, float p_103694_, float p_103695_, float p_103696_, float p_103697_) {
      super.setupAnim(p_103692_, p_103693_, p_103694_, p_103695_, p_103696_, p_103697_);
      this.head.xRot = this.headXRot;
   }
}