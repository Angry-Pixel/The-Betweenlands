package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LlamaModel<T extends AbstractChestedHorse> extends EntityModel<T> {
   private final ModelPart head;
   private final ModelPart body;
   private final ModelPart rightHindLeg;
   private final ModelPart leftHindLeg;
   private final ModelPart rightFrontLeg;
   private final ModelPart leftFrontLeg;
   private final ModelPart rightChest;
   private final ModelPart leftChest;

   public LlamaModel(ModelPart p_170724_) {
      this.head = p_170724_.getChild("head");
      this.body = p_170724_.getChild("body");
      this.rightChest = p_170724_.getChild("right_chest");
      this.leftChest = p_170724_.getChild("left_chest");
      this.rightHindLeg = p_170724_.getChild("right_hind_leg");
      this.leftHindLeg = p_170724_.getChild("left_hind_leg");
      this.rightFrontLeg = p_170724_.getChild("right_front_leg");
      this.leftFrontLeg = p_170724_.getChild("left_front_leg");
   }

   public static LayerDefinition createBodyLayer(CubeDeformation p_170726_) {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -14.0F, -10.0F, 4.0F, 4.0F, 9.0F, p_170726_).texOffs(0, 14).addBox("neck", -4.0F, -16.0F, -6.0F, 8.0F, 18.0F, 6.0F, p_170726_).texOffs(17, 0).addBox("ear", -4.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, p_170726_).texOffs(17, 0).addBox("ear", 1.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, p_170726_), PartPose.offset(0.0F, 7.0F, -6.0F));
      partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(29, 0).addBox(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, p_170726_), PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, ((float)Math.PI / 2F), 0.0F, 0.0F));
      partdefinition.addOrReplaceChild("right_chest", CubeListBuilder.create().texOffs(45, 28).addBox(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, p_170726_), PartPose.offsetAndRotation(-8.5F, 3.0F, 3.0F, 0.0F, ((float)Math.PI / 2F), 0.0F));
      partdefinition.addOrReplaceChild("left_chest", CubeListBuilder.create().texOffs(45, 41).addBox(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, p_170726_), PartPose.offsetAndRotation(5.5F, 3.0F, 3.0F, 0.0F, ((float)Math.PI / 2F), 0.0F));
      int i = 4;
      int j = 14;
      CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(29, 29).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, p_170726_);
      partdefinition.addOrReplaceChild("right_hind_leg", cubelistbuilder, PartPose.offset(-3.5F, 10.0F, 6.0F));
      partdefinition.addOrReplaceChild("left_hind_leg", cubelistbuilder, PartPose.offset(3.5F, 10.0F, 6.0F));
      partdefinition.addOrReplaceChild("right_front_leg", cubelistbuilder, PartPose.offset(-3.5F, 10.0F, -5.0F));
      partdefinition.addOrReplaceChild("left_front_leg", cubelistbuilder, PartPose.offset(3.5F, 10.0F, -5.0F));
      return LayerDefinition.create(meshdefinition, 128, 64);
   }

   public void setupAnim(T p_103049_, float p_103050_, float p_103051_, float p_103052_, float p_103053_, float p_103054_) {
      this.head.xRot = p_103054_ * ((float)Math.PI / 180F);
      this.head.yRot = p_103053_ * ((float)Math.PI / 180F);
      this.rightHindLeg.xRot = Mth.cos(p_103050_ * 0.6662F) * 1.4F * p_103051_;
      this.leftHindLeg.xRot = Mth.cos(p_103050_ * 0.6662F + (float)Math.PI) * 1.4F * p_103051_;
      this.rightFrontLeg.xRot = Mth.cos(p_103050_ * 0.6662F + (float)Math.PI) * 1.4F * p_103051_;
      this.leftFrontLeg.xRot = Mth.cos(p_103050_ * 0.6662F) * 1.4F * p_103051_;
      boolean flag = !p_103049_.isBaby() && p_103049_.hasChest();
      this.rightChest.visible = flag;
      this.leftChest.visible = flag;
   }

   public void renderToBuffer(PoseStack p_103056_, VertexConsumer p_103057_, int p_103058_, int p_103059_, float p_103060_, float p_103061_, float p_103062_, float p_103063_) {
      if (this.young) {
         float f = 2.0F;
         p_103056_.pushPose();
         float f1 = 0.7F;
         p_103056_.scale(0.71428573F, 0.64935064F, 0.7936508F);
         p_103056_.translate(0.0D, 1.3125D, (double)0.22F);
         this.head.render(p_103056_, p_103057_, p_103058_, p_103059_, p_103060_, p_103061_, p_103062_, p_103063_);
         p_103056_.popPose();
         p_103056_.pushPose();
         float f2 = 1.1F;
         p_103056_.scale(0.625F, 0.45454544F, 0.45454544F);
         p_103056_.translate(0.0D, 2.0625D, 0.0D);
         this.body.render(p_103056_, p_103057_, p_103058_, p_103059_, p_103060_, p_103061_, p_103062_, p_103063_);
         p_103056_.popPose();
         p_103056_.pushPose();
         p_103056_.scale(0.45454544F, 0.41322312F, 0.45454544F);
         p_103056_.translate(0.0D, 2.0625D, 0.0D);
         ImmutableList.of(this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.rightChest, this.leftChest).forEach((p_103083_) -> {
            p_103083_.render(p_103056_, p_103057_, p_103058_, p_103059_, p_103060_, p_103061_, p_103062_, p_103063_);
         });
         p_103056_.popPose();
      } else {
         ImmutableList.of(this.head, this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.rightChest, this.leftChest).forEach((p_103073_) -> {
            p_103073_.render(p_103056_, p_103057_, p_103058_, p_103059_, p_103060_, p_103061_, p_103062_, p_103063_);
         });
      }

   }
}