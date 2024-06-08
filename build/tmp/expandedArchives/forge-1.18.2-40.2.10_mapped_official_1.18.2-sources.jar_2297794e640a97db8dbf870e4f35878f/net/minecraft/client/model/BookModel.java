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
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BookModel extends Model {
   private static final String LEFT_PAGES = "left_pages";
   private static final String RIGHT_PAGES = "right_pages";
   private static final String FLIP_PAGE_1 = "flip_page1";
   private static final String FLIP_PAGE_2 = "flip_page2";
   private final ModelPart root;
   private final ModelPart leftLid;
   private final ModelPart rightLid;
   private final ModelPart leftPages;
   private final ModelPart rightPages;
   private final ModelPart flipPage1;
   private final ModelPart flipPage2;

   public BookModel(ModelPart p_170475_) {
      super(RenderType::entitySolid);
      this.root = p_170475_;
      this.leftLid = p_170475_.getChild("left_lid");
      this.rightLid = p_170475_.getChild("right_lid");
      this.leftPages = p_170475_.getChild("left_pages");
      this.rightPages = p_170475_.getChild("right_pages");
      this.flipPage1 = p_170475_.getChild("flip_page1");
      this.flipPage2 = p_170475_.getChild("flip_page2");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("left_lid", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F), PartPose.offset(0.0F, 0.0F, -1.0F));
      partdefinition.addOrReplaceChild("right_lid", CubeListBuilder.create().texOffs(16, 0).addBox(0.0F, -5.0F, -0.005F, 6.0F, 10.0F, 0.005F), PartPose.offset(0.0F, 0.0F, 1.0F));
      partdefinition.addOrReplaceChild("seam", CubeListBuilder.create().texOffs(12, 0).addBox(-1.0F, -5.0F, 0.0F, 2.0F, 10.0F, 0.005F), PartPose.rotation(0.0F, ((float)Math.PI / 2F), 0.0F));
      partdefinition.addOrReplaceChild("left_pages", CubeListBuilder.create().texOffs(0, 10).addBox(0.0F, -4.0F, -0.99F, 5.0F, 8.0F, 1.0F), PartPose.ZERO);
      partdefinition.addOrReplaceChild("right_pages", CubeListBuilder.create().texOffs(12, 10).addBox(0.0F, -4.0F, -0.01F, 5.0F, 8.0F, 1.0F), PartPose.ZERO);
      CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(24, 10).addBox(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F);
      partdefinition.addOrReplaceChild("flip_page1", cubelistbuilder, PartPose.ZERO);
      partdefinition.addOrReplaceChild("flip_page2", cubelistbuilder, PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   public void renderToBuffer(PoseStack p_102298_, VertexConsumer p_102299_, int p_102300_, int p_102301_, float p_102302_, float p_102303_, float p_102304_, float p_102305_) {
      this.render(p_102298_, p_102299_, p_102300_, p_102301_, p_102302_, p_102303_, p_102304_, p_102305_);
   }

   public void render(PoseStack p_102317_, VertexConsumer p_102318_, int p_102319_, int p_102320_, float p_102321_, float p_102322_, float p_102323_, float p_102324_) {
      this.root.render(p_102317_, p_102318_, p_102319_, p_102320_, p_102321_, p_102322_, p_102323_, p_102324_);
   }

   public void setupAnim(float p_102293_, float p_102294_, float p_102295_, float p_102296_) {
      float f = (Mth.sin(p_102293_ * 0.02F) * 0.1F + 1.25F) * p_102296_;
      this.leftLid.yRot = (float)Math.PI + f;
      this.rightLid.yRot = -f;
      this.leftPages.yRot = f;
      this.rightPages.yRot = -f;
      this.flipPage1.yRot = f - f * 2.0F * p_102294_;
      this.flipPage2.yRot = f - f * 2.0F * p_102295_;
      this.leftPages.x = Mth.sin(f);
      this.rightPages.x = Mth.sin(f);
      this.flipPage1.x = Mth.sin(f);
      this.flipPage2.x = Mth.sin(f);
   }
}