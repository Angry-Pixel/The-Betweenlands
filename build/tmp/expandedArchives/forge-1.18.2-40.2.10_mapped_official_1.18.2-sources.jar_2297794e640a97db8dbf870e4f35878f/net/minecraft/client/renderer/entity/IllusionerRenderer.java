package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IllusionerRenderer extends IllagerRenderer<Illusioner> {
   private static final ResourceLocation ILLUSIONER = new ResourceLocation("textures/entity/illager/illusioner.png");

   public IllusionerRenderer(EntityRendererProvider.Context p_174186_) {
      super(p_174186_, new IllagerModel<>(p_174186_.bakeLayer(ModelLayers.ILLUSIONER)), 0.5F);
      this.addLayer(new ItemInHandLayer<Illusioner, IllagerModel<Illusioner>>(this) {
         public void render(PoseStack p_114989_, MultiBufferSource p_114990_, int p_114991_, Illusioner p_114992_, float p_114993_, float p_114994_, float p_114995_, float p_114996_, float p_114997_, float p_114998_) {
            if (p_114992_.isCastingSpell() || p_114992_.isAggressive()) {
               super.render(p_114989_, p_114990_, p_114991_, p_114992_, p_114993_, p_114994_, p_114995_, p_114996_, p_114997_, p_114998_);
            }

         }
      });
      this.model.getHat().visible = true;
   }

   public ResourceLocation getTextureLocation(Illusioner p_114950_) {
      return ILLUSIONER;
   }

   public void render(Illusioner p_114952_, float p_114953_, float p_114954_, PoseStack p_114955_, MultiBufferSource p_114956_, int p_114957_) {
      if (p_114952_.isInvisible()) {
         Vec3[] avec3 = p_114952_.getIllusionOffsets(p_114954_);
         float f = this.getBob(p_114952_, p_114954_);

         for(int i = 0; i < avec3.length; ++i) {
            p_114955_.pushPose();
            p_114955_.translate(avec3[i].x + (double)Mth.cos((float)i + f * 0.5F) * 0.025D, avec3[i].y + (double)Mth.cos((float)i + f * 0.75F) * 0.0125D, avec3[i].z + (double)Mth.cos((float)i + f * 0.7F) * 0.025D);
            super.render(p_114952_, p_114953_, p_114954_, p_114955_, p_114956_, p_114957_);
            p_114955_.popPose();
         }
      } else {
         super.render(p_114952_, p_114953_, p_114954_, p_114955_, p_114956_, p_114957_);
      }

   }

   protected boolean isBodyVisible(Illusioner p_114959_) {
      return true;
   }
}