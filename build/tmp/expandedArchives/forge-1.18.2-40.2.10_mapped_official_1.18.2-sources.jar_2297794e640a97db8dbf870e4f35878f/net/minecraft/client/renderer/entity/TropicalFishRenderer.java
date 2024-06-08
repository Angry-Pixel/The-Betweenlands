package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.ColorableHierarchicalModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.TropicalFishPatternLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TropicalFishRenderer extends MobRenderer<TropicalFish, ColorableHierarchicalModel<TropicalFish>> {
   private final ColorableHierarchicalModel<TropicalFish> modelA = this.getModel();
   private final ColorableHierarchicalModel<TropicalFish> modelB;

   public TropicalFishRenderer(EntityRendererProvider.Context p_174428_) {
      super(p_174428_, new TropicalFishModelA<>(p_174428_.bakeLayer(ModelLayers.TROPICAL_FISH_SMALL)), 0.15F);
      this.modelB = new TropicalFishModelB<>(p_174428_.bakeLayer(ModelLayers.TROPICAL_FISH_LARGE));
      this.addLayer(new TropicalFishPatternLayer(this, p_174428_.getModelSet()));
   }

   public ResourceLocation getTextureLocation(TropicalFish p_116217_) {
      return p_116217_.getBaseTextureLocation();
   }

   public void render(TropicalFish p_116219_, float p_116220_, float p_116221_, PoseStack p_116222_, MultiBufferSource p_116223_, int p_116224_) {
      ColorableHierarchicalModel<TropicalFish> colorablehierarchicalmodel = p_116219_.getBaseVariant() == 0 ? this.modelA : this.modelB;
      this.model = colorablehierarchicalmodel;
      float[] afloat = p_116219_.getBaseColor();
      colorablehierarchicalmodel.setColor(afloat[0], afloat[1], afloat[2]);
      super.render(p_116219_, p_116220_, p_116221_, p_116222_, p_116223_, p_116224_);
      colorablehierarchicalmodel.setColor(1.0F, 1.0F, 1.0F);
   }

   protected void setupRotations(TropicalFish p_116226_, PoseStack p_116227_, float p_116228_, float p_116229_, float p_116230_) {
      super.setupRotations(p_116226_, p_116227_, p_116228_, p_116229_, p_116230_);
      float f = 4.3F * Mth.sin(0.6F * p_116228_);
      p_116227_.mulPose(Vector3f.YP.rotationDegrees(f));
      if (!p_116226_.isInWater()) {
         p_116227_.translate((double)0.2F, (double)0.1F, 0.0D);
         p_116227_.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
      }

   }
}