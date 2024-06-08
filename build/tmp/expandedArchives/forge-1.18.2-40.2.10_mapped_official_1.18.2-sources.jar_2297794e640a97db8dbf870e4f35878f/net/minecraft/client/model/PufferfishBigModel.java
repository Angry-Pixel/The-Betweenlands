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
public class PufferfishBigModel<T extends Entity> extends HierarchicalModel<T> {
   private final ModelPart root;
   private final ModelPart leftBlueFin;
   private final ModelPart rightBlueFin;

   public PufferfishBigModel(ModelPart p_170835_) {
      this.root = p_170835_;
      this.leftBlueFin = p_170835_.getChild("left_blue_fin");
      this.rightBlueFin = p_170835_.getChild("right_blue_fin");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      int i = 22;
      partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.offset(0.0F, 22.0F, 0.0F));
      partdefinition.addOrReplaceChild("right_blue_fin", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F), PartPose.offset(-4.0F, 15.0F, -2.0F));
      partdefinition.addOrReplaceChild("left_blue_fin", CubeListBuilder.create().texOffs(24, 3).addBox(0.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F), PartPose.offset(4.0F, 15.0F, -2.0F));
      partdefinition.addOrReplaceChild("top_front_fin", CubeListBuilder.create().texOffs(15, 17).addBox(-4.0F, -1.0F, 0.0F, 8.0F, 1.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 14.0F, -4.0F, ((float)Math.PI / 4F), 0.0F, 0.0F));
      partdefinition.addOrReplaceChild("top_middle_fin", CubeListBuilder.create().texOffs(14, 16).addBox(-4.0F, -1.0F, 0.0F, 8.0F, 1.0F, 1.0F), PartPose.offset(0.0F, 14.0F, 0.0F));
      partdefinition.addOrReplaceChild("top_back_fin", CubeListBuilder.create().texOffs(23, 18).addBox(-4.0F, -1.0F, 0.0F, 8.0F, 1.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 14.0F, 4.0F, (-(float)Math.PI / 4F), 0.0F, 0.0F));
      partdefinition.addOrReplaceChild("right_front_fin", CubeListBuilder.create().texOffs(5, 17).addBox(-1.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F), PartPose.offsetAndRotation(-4.0F, 22.0F, -4.0F, 0.0F, (-(float)Math.PI / 4F), 0.0F));
      partdefinition.addOrReplaceChild("left_front_fin", CubeListBuilder.create().texOffs(1, 17).addBox(0.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F), PartPose.offsetAndRotation(4.0F, 22.0F, -4.0F, 0.0F, ((float)Math.PI / 4F), 0.0F));
      partdefinition.addOrReplaceChild("bottom_front_fin", CubeListBuilder.create().texOffs(15, 20).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 1.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 22.0F, -4.0F, (-(float)Math.PI / 4F), 0.0F, 0.0F));
      partdefinition.addOrReplaceChild("bottom_middle_fin", CubeListBuilder.create().texOffs(15, 20).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 1.0F, 0.0F), PartPose.offset(0.0F, 22.0F, 0.0F));
      partdefinition.addOrReplaceChild("bottom_back_fin", CubeListBuilder.create().texOffs(15, 20).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 1.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 22.0F, 4.0F, ((float)Math.PI / 4F), 0.0F, 0.0F));
      partdefinition.addOrReplaceChild("right_back_fin", CubeListBuilder.create().texOffs(9, 17).addBox(-1.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F), PartPose.offsetAndRotation(-4.0F, 22.0F, 4.0F, 0.0F, ((float)Math.PI / 4F), 0.0F));
      partdefinition.addOrReplaceChild("left_back_fin", CubeListBuilder.create().texOffs(9, 17).addBox(0.0F, -8.0F, 0.0F, 1.0F, 8.0F, 0.0F), PartPose.offsetAndRotation(4.0F, 22.0F, 4.0F, 0.0F, (-(float)Math.PI / 4F), 0.0F));
      return LayerDefinition.create(meshdefinition, 32, 32);
   }

   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(T p_103451_, float p_103452_, float p_103453_, float p_103454_, float p_103455_, float p_103456_) {
      this.rightBlueFin.zRot = -0.2F + 0.4F * Mth.sin(p_103454_ * 0.2F);
      this.leftBlueFin.zRot = 0.2F - 0.4F * Mth.sin(p_103454_ * 0.2F);
   }
}