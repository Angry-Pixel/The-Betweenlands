package net.minecraft.client.model;

import java.util.Arrays;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Slime;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LavaSlimeModel<T extends Slime> extends HierarchicalModel<T> {
   private static final int SEGMENT_COUNT = 8;
   private final ModelPart root;
   private final ModelPart[] bodyCubes = new ModelPart[8];

   public LavaSlimeModel(ModelPart p_170703_) {
      this.root = p_170703_;
      Arrays.setAll(this.bodyCubes, (p_170709_) -> {
         return p_170703_.getChild(getSegmentName(p_170709_));
      });
   }

   private static String getSegmentName(int p_170706_) {
      return "cube" + p_170706_;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();

      for(int i = 0; i < 8; ++i) {
         int j = 0;
         int k = i;
         if (i == 2) {
            j = 24;
            k = 10;
         } else if (i == 3) {
            j = 24;
            k = 19;
         }

         partdefinition.addOrReplaceChild(getSegmentName(i), CubeListBuilder.create().texOffs(j, k).addBox(-4.0F, (float)(16 + i), -4.0F, 8.0F, 1.0F, 8.0F), PartPose.ZERO);
      }

      partdefinition.addOrReplaceChild("inside_cube", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 18.0F, -2.0F, 4.0F, 4.0F, 4.0F), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 64, 32);
   }

   public void setupAnim(T p_102992_, float p_102993_, float p_102994_, float p_102995_, float p_102996_, float p_102997_) {
   }

   public void prepareMobModel(T p_102987_, float p_102988_, float p_102989_, float p_102990_) {
      float f = Mth.lerp(p_102990_, p_102987_.oSquish, p_102987_.squish);
      if (f < 0.0F) {
         f = 0.0F;
      }

      for(int i = 0; i < this.bodyCubes.length; ++i) {
         this.bodyCubes[i].y = (float)(-(4 - i)) * f * 1.7F;
      }

   }

   public ModelPart root() {
      return this.root;
   }
}