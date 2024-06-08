package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkullModel extends SkullModelBase {
   private final ModelPart root;
   protected final ModelPart head;

   public SkullModel(ModelPart p_170945_) {
      this.root = p_170945_;
      this.head = p_170945_.getChild("head");
   }

   public static MeshDefinition createHeadModel() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
      return meshdefinition;
   }

   public static LayerDefinition createHumanoidHeadLayer() {
      MeshDefinition meshdefinition = createHeadModel();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.getChild("head").addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public static LayerDefinition createMobHeadLayer() {
      MeshDefinition meshdefinition = createHeadModel();
      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   public void setupAnim(float p_103811_, float p_103812_, float p_103813_) {
      this.head.yRot = p_103812_ * ((float)Math.PI / 180F);
      this.head.xRot = p_103813_ * ((float)Math.PI / 180F);
   }

   public void renderToBuffer(PoseStack p_103815_, VertexConsumer p_103816_, int p_103817_, int p_103818_, float p_103819_, float p_103820_, float p_103821_, float p_103822_) {
      this.root.render(p_103815_, p_103816_, p_103817_, p_103818_, p_103819_, p_103820_, p_103821_, p_103822_);
   }
}