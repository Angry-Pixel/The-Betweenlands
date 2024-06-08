package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElytraModel<T extends LivingEntity> extends AgeableListModel<T> {
   private final ModelPart rightWing;
   private final ModelPart leftWing;

   public ElytraModel(ModelPart p_170538_) {
      this.leftWing = p_170538_.getChild("left_wing");
      this.rightWing = p_170538_.getChild("right_wing");
   }

   public static LayerDefinition createLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      CubeDeformation cubedeformation = new CubeDeformation(1.0F);
      partdefinition.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(22, 0).addBox(-10.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, cubedeformation), PartPose.offsetAndRotation(5.0F, 0.0F, 0.0F, 0.2617994F, 0.0F, -0.2617994F));
      partdefinition.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(22, 0).mirror().addBox(0.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, cubedeformation), PartPose.offsetAndRotation(-5.0F, 0.0F, 0.0F, 0.2617994F, 0.0F, 0.2617994F));
      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   protected Iterable<ModelPart> headParts() {
      return ImmutableList.of();
   }

   protected Iterable<ModelPart> bodyParts() {
      return ImmutableList.of(this.leftWing, this.rightWing);
   }

   public void setupAnim(T p_102544_, float p_102545_, float p_102546_, float p_102547_, float p_102548_, float p_102549_) {
      float f = 0.2617994F;
      float f1 = -0.2617994F;
      float f2 = 0.0F;
      float f3 = 0.0F;
      if (p_102544_.isFallFlying()) {
         float f4 = 1.0F;
         Vec3 vec3 = p_102544_.getDeltaMovement();
         if (vec3.y < 0.0D) {
            Vec3 vec31 = vec3.normalize();
            f4 = 1.0F - (float)Math.pow(-vec31.y, 1.5D);
         }

         f = f4 * 0.34906584F + (1.0F - f4) * f;
         f1 = f4 * (-(float)Math.PI / 2F) + (1.0F - f4) * f1;
      } else if (p_102544_.isCrouching()) {
         f = 0.6981317F;
         f1 = (-(float)Math.PI / 4F);
         f2 = 3.0F;
         f3 = 0.08726646F;
      }

      this.leftWing.y = f2;
      if (p_102544_ instanceof AbstractClientPlayer) {
         AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer)p_102544_;
         abstractclientplayer.elytraRotX += (f - abstractclientplayer.elytraRotX) * 0.1F;
         abstractclientplayer.elytraRotY += (f3 - abstractclientplayer.elytraRotY) * 0.1F;
         abstractclientplayer.elytraRotZ += (f1 - abstractclientplayer.elytraRotZ) * 0.1F;
         this.leftWing.xRot = abstractclientplayer.elytraRotX;
         this.leftWing.yRot = abstractclientplayer.elytraRotY;
         this.leftWing.zRot = abstractclientplayer.elytraRotZ;
      } else {
         this.leftWing.xRot = f;
         this.leftWing.zRot = f1;
         this.leftWing.yRot = f3;
      }

      this.rightWing.yRot = -this.leftWing.yRot;
      this.rightWing.y = this.leftWing.y;
      this.rightWing.xRot = this.leftWing.xRot;
      this.rightWing.zRot = -this.leftWing.zRot;
   }
}