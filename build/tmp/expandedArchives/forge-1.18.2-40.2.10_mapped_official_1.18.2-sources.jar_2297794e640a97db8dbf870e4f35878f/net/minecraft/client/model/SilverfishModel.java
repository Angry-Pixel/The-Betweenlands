package net.minecraft.client.model;

import java.util.Arrays;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SilverfishModel<T extends Entity> extends HierarchicalModel<T> {
   private static final int BODY_COUNT = 7;
   private final ModelPart root;
   private final ModelPart[] bodyParts = new ModelPart[7];
   private final ModelPart[] bodyLayers = new ModelPart[3];
   private static final int[][] BODY_SIZES = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
   private static final int[][] BODY_TEXS = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

   public SilverfishModel(ModelPart p_170927_) {
      this.root = p_170927_;
      Arrays.setAll(this.bodyParts, (p_170939_) -> {
         return p_170927_.getChild(getSegmentName(p_170939_));
      });
      Arrays.setAll(this.bodyLayers, (p_170933_) -> {
         return p_170927_.getChild(getLayerName(p_170933_));
      });
   }

   private static String getLayerName(int p_170930_) {
      return "layer" + p_170930_;
   }

   private static String getSegmentName(int p_170936_) {
      return "segment" + p_170936_;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      float[] afloat = new float[7];
      float f = -3.5F;

      for(int i = 0; i < 7; ++i) {
         partdefinition.addOrReplaceChild(getSegmentName(i), CubeListBuilder.create().texOffs(BODY_TEXS[i][0], BODY_TEXS[i][1]).addBox((float)BODY_SIZES[i][0] * -0.5F, 0.0F, (float)BODY_SIZES[i][2] * -0.5F, (float)BODY_SIZES[i][0], (float)BODY_SIZES[i][1], (float)BODY_SIZES[i][2]), PartPose.offset(0.0F, (float)(24 - BODY_SIZES[i][1]), f));
         afloat[i] = f;
         if (i < 6) {
            f += (float)(BODY_SIZES[i][2] + BODY_SIZES[i + 1][2]) * 0.5F;
         }
      }

      partdefinition.addOrReplaceChild(getLayerName(0), CubeListBuilder.create().texOffs(20, 0).addBox(-5.0F, 0.0F, (float)BODY_SIZES[2][2] * -0.5F, 10.0F, 8.0F, (float)BODY_SIZES[2][2]), PartPose.offset(0.0F, 16.0F, afloat[2]));
      partdefinition.addOrReplaceChild(getLayerName(1), CubeListBuilder.create().texOffs(20, 11).addBox(-3.0F, 0.0F, (float)BODY_SIZES[4][2] * -0.5F, 6.0F, 4.0F, (float)BODY_SIZES[4][2]), PartPose.offset(0.0F, 20.0F, afloat[4]));
      partdefinition.addOrReplaceChild(getLayerName(2), CubeListBuilder.create().texOffs(20, 18).addBox(-3.0F, 0.0F, (float)BODY_SIZES[4][2] * -0.5F, 6.0F, 5.0F, (float)BODY_SIZES[1][2]), PartPose.offset(0.0F, 19.0F, afloat[1]));
      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   public ModelPart root() {
      return this.root;
   }

   public void setupAnim(T p_103754_, float p_103755_, float p_103756_, float p_103757_, float p_103758_, float p_103759_) {
      for(int i = 0; i < this.bodyParts.length; ++i) {
         this.bodyParts[i].yRot = Mth.cos(p_103757_ * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.05F * (float)(1 + Math.abs(i - 2));
         this.bodyParts[i].x = Mth.sin(p_103757_ * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.2F * (float)Math.abs(i - 2);
      }

      this.bodyLayers[0].yRot = this.bodyParts[2].yRot;
      this.bodyLayers[1].yRot = this.bodyParts[4].yRot;
      this.bodyLayers[1].x = this.bodyParts[4].x;
      this.bodyLayers[2].yRot = this.bodyParts[1].yRot;
      this.bodyLayers[2].x = this.bodyParts[1].x;
   }
}