package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EvokerFangsModel<T extends Entity> extends HierarchicalModel<T> {
   private static final String BASE = "base";
   private static final String UPPER_JAW = "upper_jaw";
   private static final String LOWER_JAW = "lower_jaw";
   private final ModelPart root;
   private final ModelPart base;
   private final ModelPart upperJaw;
   private final ModelPart lowerJaw;

   public EvokerFangsModel(ModelPart p_170555_) {
      this.root = p_170555_;
      this.base = p_170555_.getChild("base");
      this.upperJaw = p_170555_.getChild("upper_jaw");
      this.lowerJaw = p_170555_.getChild("lower_jaw");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 10.0F, 12.0F, 10.0F), PartPose.offset(-5.0F, 24.0F, -5.0F));
      CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(40, 0).addBox(0.0F, 0.0F, 0.0F, 4.0F, 14.0F, 8.0F);
      partdefinition.addOrReplaceChild("upper_jaw", cubelistbuilder, PartPose.offset(1.5F, 24.0F, -4.0F));
      partdefinition.addOrReplaceChild("lower_jaw", cubelistbuilder, PartPose.offsetAndRotation(-1.5F, 24.0F, 4.0F, 0.0F, (float)Math.PI, 0.0F));
      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   public void setupAnim(T p_102632_, float p_102633_, float p_102634_, float p_102635_, float p_102636_, float p_102637_) {
      float f = p_102633_ * 2.0F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      f = 1.0F - f * f * f;
      this.upperJaw.zRot = (float)Math.PI - f * 0.35F * (float)Math.PI;
      this.lowerJaw.zRot = (float)Math.PI + f * 0.35F * (float)Math.PI;
      float f1 = (p_102633_ + Mth.sin(p_102633_ * 2.7F)) * 0.6F * 12.0F;
      this.upperJaw.y = 24.0F - f1;
      this.lowerJaw.y = this.upperJaw.y;
      this.base.y = this.upperJaw.y;
   }

   public ModelPart root() {
      return this.root;
   }
}