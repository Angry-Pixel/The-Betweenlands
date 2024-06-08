package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.WolfModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WolfCollarLayer extends RenderLayer<Wolf, WolfModel<Wolf>> {
   private static final ResourceLocation WOLF_COLLAR_LOCATION = new ResourceLocation("textures/entity/wolf/wolf_collar.png");

   public WolfCollarLayer(RenderLayerParent<Wolf, WolfModel<Wolf>> p_117707_) {
      super(p_117707_);
   }

   public void render(PoseStack p_117720_, MultiBufferSource p_117721_, int p_117722_, Wolf p_117723_, float p_117724_, float p_117725_, float p_117726_, float p_117727_, float p_117728_, float p_117729_) {
      if (p_117723_.isTame() && !p_117723_.isInvisible()) {
         float[] afloat = p_117723_.getCollarColor().getTextureDiffuseColors();
         renderColoredCutoutModel(this.getParentModel(), WOLF_COLLAR_LOCATION, p_117720_, p_117721_, p_117722_, p_117723_, afloat[0], afloat[1], afloat[2]);
      }
   }
}