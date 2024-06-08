package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CatCollarLayer extends RenderLayer<Cat, CatModel<Cat>> {
   private static final ResourceLocation CAT_COLLAR_LOCATION = new ResourceLocation("textures/entity/cat/cat_collar.png");
   private final CatModel<Cat> catModel;

   public CatCollarLayer(RenderLayerParent<Cat, CatModel<Cat>> p_174468_, EntityModelSet p_174469_) {
      super(p_174468_);
      this.catModel = new CatModel<>(p_174469_.bakeLayer(ModelLayers.CAT_COLLAR));
   }

   public void render(PoseStack p_116666_, MultiBufferSource p_116667_, int p_116668_, Cat p_116669_, float p_116670_, float p_116671_, float p_116672_, float p_116673_, float p_116674_, float p_116675_) {
      if (p_116669_.isTame()) {
         float[] afloat = p_116669_.getCollarColor().getTextureDiffuseColors();
         coloredCutoutModelCopyLayerRender(this.getParentModel(), this.catModel, CAT_COLLAR_LOCATION, p_116666_, p_116667_, p_116668_, p_116669_, p_116670_, p_116671_, p_116673_, p_116674_, p_116675_, p_116672_, afloat[0], afloat[1], afloat[2]);
      }
   }
}