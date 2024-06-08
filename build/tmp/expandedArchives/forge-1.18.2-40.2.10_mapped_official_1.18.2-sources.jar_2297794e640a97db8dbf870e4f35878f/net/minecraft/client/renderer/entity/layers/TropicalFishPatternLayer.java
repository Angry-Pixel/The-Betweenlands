package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ColorableHierarchicalModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TropicalFishPatternLayer extends RenderLayer<TropicalFish, ColorableHierarchicalModel<TropicalFish>> {
   private final TropicalFishModelA<TropicalFish> modelA;
   private final TropicalFishModelB<TropicalFish> modelB;

   public TropicalFishPatternLayer(RenderLayerParent<TropicalFish, ColorableHierarchicalModel<TropicalFish>> p_174547_, EntityModelSet p_174548_) {
      super(p_174547_);
      this.modelA = new TropicalFishModelA<>(p_174548_.bakeLayer(ModelLayers.TROPICAL_FISH_SMALL_PATTERN));
      this.modelB = new TropicalFishModelB<>(p_174548_.bakeLayer(ModelLayers.TROPICAL_FISH_LARGE_PATTERN));
   }

   public void render(PoseStack p_117612_, MultiBufferSource p_117613_, int p_117614_, TropicalFish p_117615_, float p_117616_, float p_117617_, float p_117618_, float p_117619_, float p_117620_, float p_117621_) {
      EntityModel<TropicalFish> entitymodel = (EntityModel<TropicalFish>)(p_117615_.getBaseVariant() == 0 ? this.modelA : this.modelB);
      float[] afloat = p_117615_.getPatternColor();
      coloredCutoutModelCopyLayerRender(this.getParentModel(), entitymodel, p_117615_.getPatternTextureLocation(), p_117612_, p_117613_, p_117614_, p_117615_, p_117616_, p_117617_, p_117619_, p_117620_, p_117621_, p_117618_, afloat[0], afloat[1], afloat[2]);
   }
}