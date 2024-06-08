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
public class WitchModel<T extends Entity> extends VillagerModel<T> {
   private boolean holdingItem;

   public WitchModel(ModelPart p_171055_) {
      super(p_171055_);
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = VillagerModel.createBodyModel();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), PartPose.ZERO);
      PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(0, 64).addBox(0.0F, 0.0F, 0.0F, 10.0F, 2.0F, 10.0F), PartPose.offset(-5.0F, -10.03125F, -5.0F));
      PartDefinition partdefinition3 = partdefinition2.addOrReplaceChild("hat2", CubeListBuilder.create().texOffs(0, 76).addBox(0.0F, 0.0F, 0.0F, 7.0F, 4.0F, 7.0F), PartPose.offsetAndRotation(1.75F, -4.0F, 2.0F, -0.05235988F, 0.0F, 0.02617994F));
      PartDefinition partdefinition4 = partdefinition3.addOrReplaceChild("hat3", CubeListBuilder.create().texOffs(0, 87).addBox(0.0F, 0.0F, 0.0F, 4.0F, 4.0F, 4.0F), PartPose.offsetAndRotation(1.75F, -4.0F, 2.0F, -0.10471976F, 0.0F, 0.05235988F));
      partdefinition4.addOrReplaceChild("hat4", CubeListBuilder.create().texOffs(0, 95).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(1.75F, -2.0F, 2.0F, -0.20943952F, 0.0F, 0.10471976F));
      PartDefinition partdefinition5 = partdefinition1.getChild("nose");
      partdefinition5.addOrReplaceChild("mole", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 3.0F, -6.75F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offset(0.0F, -2.0F, 0.0F));
      return LayerDefinition.create(meshdefinition, 64, 128);
   }

   public void setupAnim(T p_104067_, float p_104068_, float p_104069_, float p_104070_, float p_104071_, float p_104072_) {
      super.setupAnim(p_104067_, p_104068_, p_104069_, p_104070_, p_104071_, p_104072_);
      this.nose.setPos(0.0F, -2.0F, 0.0F);
      float f = 0.01F * (float)(p_104067_.getId() % 10);
      this.nose.xRot = Mth.sin((float)p_104067_.tickCount * f) * 4.5F * ((float)Math.PI / 180F);
      this.nose.yRot = 0.0F;
      this.nose.zRot = Mth.cos((float)p_104067_.tickCount * f) * 2.5F * ((float)Math.PI / 180F);
      if (this.holdingItem) {
         this.nose.setPos(0.0F, 1.0F, -1.5F);
         this.nose.xRot = -0.9F;
      }

   }

   public ModelPart getNose() {
      return this.nose;
   }

   public void setHoldingItem(boolean p_104075_) {
      this.holdingItem = p_104075_;
   }
}