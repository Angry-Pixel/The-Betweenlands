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
public class TropicalFishModelB<T extends Entity> extends ColorableHierarchicalModel<T> {
   private final ModelPart root;
   private final ModelPart tail;

   public TropicalFishModelB(ModelPart p_171036_) {
      this.root = p_171036_;
      this.tail = p_171036_.getChild("tail");
   }

   public static LayerDefinition createBodyLayer(CubeDeformation p_171038_) {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      int i = 19;
      partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 20).addBox(-1.0F, -3.0F, -3.0F, 2.0F, 6.0F, 6.0F, p_171038_), PartPose.offset(0.0F, 19.0F, 0.0F));
      partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(21, 16).addBox(0.0F, -3.0F, 0.0F, 0.0F, 6.0F, 5.0F, p_171038_), PartPose.offset(0.0F, 19.0F, 3.0F));
      partdefinition.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(2, 16).addBox(-2.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, p_171038_), PartPose.offsetAndRotation(-1.0F, 20.0F, 0.0F, 0.0F, ((float)Math.PI / 4F), 0.0F));
      partdefinition.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(2, 12).addBox(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 0.0F, p_171038_), PartPose.offsetAndRotation(1.0F, 20.0F, 0.0F, 0.0F, (-(float)Math.PI / 4F), 0.0F));
      partdefinition.addOrReplaceChild("top_fin", CubeListBuilder.create().texOffs(20, 11).addBox(0.0F, -4.0F, 0.0F, 0.0F, 4.0F, 6.0F, p_171038_), PartPose.offset(0.0F, 16.0F, -3.0F));
      partdefinition.addOrReplaceChild("bottom_fin", CubeListBuilder.create().texOffs(20, 21).addBox(0.0F, 0.0F, 0.0F, 0.0F, 4.0F, 6.0F, p_171038_), PartPose.offset(0.0F, 22.0F, -3.0F));
      return LayerDefinition.create(meshdefinition, 32, 32);
   }

   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(T p_103977_, float p_103978_, float p_103979_, float p_103980_, float p_103981_, float p_103982_) {
      float f = 1.0F;
      if (!p_103977_.isInWater()) {
         f = 1.5F;
      }

      this.tail.yRot = -f * 0.45F * Mth.sin(0.6F * p_103980_);
   }
}