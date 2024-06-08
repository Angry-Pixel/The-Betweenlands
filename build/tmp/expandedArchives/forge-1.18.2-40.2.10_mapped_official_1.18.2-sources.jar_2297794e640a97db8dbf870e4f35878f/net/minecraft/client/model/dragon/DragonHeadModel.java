package net.minecraft.client.model.dragon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DragonHeadModel extends SkullModelBase {
   private final ModelPart head;
   private final ModelPart jaw;

   public DragonHeadModel(ModelPart p_171097_) {
      this.head = p_171097_.getChild("head");
      this.jaw = this.head.getChild("jaw");
   }

   public static LayerDefinition createHeadLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      float f = -16.0F;
      PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().addBox("upper_lip", -6.0F, -1.0F, -24.0F, 12, 5, 16, 176, 44).addBox("upper_head", -8.0F, -8.0F, -10.0F, 16, 16, 16, 112, 30).mirror(true).addBox("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0).addBox("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0).mirror(false).addBox("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0).addBox("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0), PartPose.ZERO);
      partdefinition1.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(176, 65).addBox("jaw", -6.0F, 0.0F, -16.0F, 12.0F, 4.0F, 16.0F), PartPose.offset(0.0F, 4.0F, -8.0F));
      return LayerDefinition.create(meshdefinition, 256, 256);
   }

   public void setupAnim(float p_104188_, float p_104189_, float p_104190_) {
      this.jaw.xRot = (float)(Math.sin((double)(p_104188_ * (float)Math.PI * 0.2F)) + 1.0D) * 0.2F;
      this.head.yRot = p_104189_ * ((float)Math.PI / 180F);
      this.head.xRot = p_104190_ * ((float)Math.PI / 180F);
   }

   public void renderToBuffer(PoseStack p_104192_, VertexConsumer p_104193_, int p_104194_, int p_104195_, float p_104196_, float p_104197_, float p_104198_, float p_104199_) {
      p_104192_.pushPose();
      p_104192_.translate(0.0D, (double)-0.374375F, 0.0D);
      p_104192_.scale(0.75F, 0.75F, 0.75F);
      this.head.render(p_104192_, p_104193_, p_104194_, p_104195_, p_104196_, p_104197_, p_104198_, p_104199_);
      p_104192_.popPose();
   }
}