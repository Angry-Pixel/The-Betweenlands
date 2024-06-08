package net.minecraft.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PhantomModel<T extends Phantom> extends HierarchicalModel<T> {
   private static final String TAIL_BASE = "tail_base";
   private static final String TAIL_TIP = "tail_tip";
   private final ModelPart root;
   private final ModelPart leftWingBase;
   private final ModelPart leftWingTip;
   private final ModelPart rightWingBase;
   private final ModelPart rightWingTip;
   private final ModelPart tailBase;
   private final ModelPart tailTip;

   public PhantomModel(ModelPart p_170788_) {
      this.root = p_170788_;
      ModelPart modelpart = p_170788_.getChild("body");
      this.tailBase = modelpart.getChild("tail_base");
      this.tailTip = this.tailBase.getChild("tail_tip");
      this.leftWingBase = modelpart.getChild("left_wing_base");
      this.leftWingTip = this.leftWingBase.getChild("left_wing_tip");
      this.rightWingBase = modelpart.getChild("right_wing_base");
      this.rightWingTip = this.rightWingBase.getChild("right_wing_tip");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 8).addBox(-3.0F, -2.0F, -8.0F, 5.0F, 3.0F, 9.0F), PartPose.rotation(-0.1F, 0.0F, 0.0F));
      PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild("tail_base", CubeListBuilder.create().texOffs(3, 20).addBox(-2.0F, 0.0F, 0.0F, 3.0F, 2.0F, 6.0F), PartPose.offset(0.0F, -2.0F, 1.0F));
      partdefinition2.addOrReplaceChild("tail_tip", CubeListBuilder.create().texOffs(4, 29).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 6.0F), PartPose.offset(0.0F, 0.5F, 6.0F));
      PartDefinition partdefinition3 = partdefinition1.addOrReplaceChild("left_wing_base", CubeListBuilder.create().texOffs(23, 12).addBox(0.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F), PartPose.offsetAndRotation(2.0F, -2.0F, -8.0F, 0.0F, 0.0F, 0.1F));
      partdefinition3.addOrReplaceChild("left_wing_tip", CubeListBuilder.create().texOffs(16, 24).addBox(0.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F), PartPose.offsetAndRotation(6.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1F));
      PartDefinition partdefinition4 = partdefinition1.addOrReplaceChild("right_wing_base", CubeListBuilder.create().texOffs(23, 12).mirror().addBox(-6.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F), PartPose.offsetAndRotation(-3.0F, -2.0F, -8.0F, 0.0F, 0.0F, -0.1F));
      partdefinition4.addOrReplaceChild("right_wing_tip", CubeListBuilder.create().texOffs(16, 24).mirror().addBox(-13.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F), PartPose.offsetAndRotation(-6.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1F));
      partdefinition1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -2.0F, -5.0F, 7.0F, 3.0F, 5.0F), PartPose.offsetAndRotation(0.0F, 1.0F, -7.0F, 0.2F, 0.0F, 0.0F));
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(T p_170791_, float p_170792_, float p_170793_, float p_170794_, float p_170795_, float p_170796_) {
      float f = ((float)p_170791_.getUniqueFlapTickOffset() + p_170794_) * 7.448451F * ((float)Math.PI / 180F);
      float f1 = 16.0F;
      this.leftWingBase.zRot = Mth.cos(f) * 16.0F * ((float)Math.PI / 180F);
      this.leftWingTip.zRot = Mth.cos(f) * 16.0F * ((float)Math.PI / 180F);
      this.rightWingBase.zRot = -this.leftWingBase.zRot;
      this.rightWingTip.zRot = -this.leftWingTip.zRot;
      this.tailBase.xRot = -(5.0F + Mth.cos(f * 2.0F) * 5.0F) * ((float)Math.PI / 180F);
      this.tailTip.xRot = -(5.0F + Mth.cos(f * 2.0F) * 5.0F) * ((float)Math.PI / 180F);
   }
}