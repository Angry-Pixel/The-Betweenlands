package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShieldModel extends Model {
   private static final String PLATE = "plate";
   private static final String HANDLE = "handle";
   private static final int SHIELD_WIDTH = 10;
   private static final int SHIELD_HEIGHT = 20;
   private final ModelPart root;
   private final ModelPart plate;
   private final ModelPart handle;

   public ShieldModel(ModelPart p_170911_) {
      super(RenderType::entitySolid);
      this.root = p_170911_;
      this.plate = p_170911_.getChild("plate");
      this.handle = p_170911_.getChild("handle");
   }

   public static LayerDefinition createLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("plate", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -11.0F, -2.0F, 12.0F, 22.0F, 1.0F), PartPose.ZERO);
      partdefinition.addOrReplaceChild("handle", CubeListBuilder.create().texOffs(26, 0).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 6.0F), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 64, 64);
   }

   public ModelPart plate() {
      return this.plate;
   }

   public ModelPart handle() {
      return this.handle;
   }

   public void renderToBuffer(PoseStack p_103703_, VertexConsumer p_103704_, int p_103705_, int p_103706_, float p_103707_, float p_103708_, float p_103709_, float p_103710_) {
      this.root.render(p_103703_, p_103704_, p_103705_, p_103706_, p_103707_, p_103708_, p_103709_, p_103710_);
   }
}