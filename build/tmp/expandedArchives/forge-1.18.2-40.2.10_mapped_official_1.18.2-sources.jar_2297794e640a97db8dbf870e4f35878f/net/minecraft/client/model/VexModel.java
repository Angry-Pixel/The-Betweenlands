package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.Vex;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VexModel extends HumanoidModel<Vex> {
   private final ModelPart leftWing;
   private final ModelPart rightWing;

   public VexModel(ModelPart p_171045_) {
      super(p_171045_);
      this.leftLeg.visible = false;
      this.hat.visible = false;
      this.rightWing = p_171045_.getChild("right_wing");
      this.leftWing = p_171045_.getChild("left_wing");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(32, 0).addBox(-1.0F, -1.0F, -2.0F, 6.0F, 10.0F, 4.0F), PartPose.offset(-1.9F, 12.0F, 0.0F));
      partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 32).addBox(-20.0F, 0.0F, 0.0F, 20.0F, 12.0F, 1.0F), PartPose.ZERO);
      partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 32).mirror().addBox(0.0F, 0.0F, 0.0F, 20.0F, 12.0F, 1.0F), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   protected Iterable<ModelPart> bodyParts() {
      return Iterables.concat(super.bodyParts(), ImmutableList.of(this.rightWing, this.leftWing));
   }

   public void setupAnim(Vex p_104028_, float p_104029_, float p_104030_, float p_104031_, float p_104032_, float p_104033_) {
      super.setupAnim(p_104028_, p_104029_, p_104030_, p_104031_, p_104032_, p_104033_);
      if (p_104028_.isCharging()) {
         if (p_104028_.getMainHandItem().isEmpty()) {
            this.rightArm.xRot = ((float)Math.PI * 1.5F);
            this.leftArm.xRot = ((float)Math.PI * 1.5F);
         } else if (p_104028_.getMainArm() == HumanoidArm.RIGHT) {
            this.rightArm.xRot = 3.7699115F;
         } else {
            this.leftArm.xRot = 3.7699115F;
         }
      }

      this.rightLeg.xRot += ((float)Math.PI / 5F);
      this.rightWing.z = 2.0F;
      this.leftWing.z = 2.0F;
      this.rightWing.y = 1.0F;
      this.leftWing.y = 1.0F;
      this.rightWing.yRot = 0.47123894F + Mth.cos(p_104031_ * 45.836624F * ((float)Math.PI / 180F)) * (float)Math.PI * 0.05F;
      this.leftWing.yRot = -this.rightWing.yRot;
      this.leftWing.zRot = -0.47123894F;
      this.leftWing.xRot = 0.47123894F;
      this.rightWing.xRot = 0.47123894F;
      this.rightWing.zRot = 0.47123894F;
   }
}