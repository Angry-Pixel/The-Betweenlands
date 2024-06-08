package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EvokerRenderer<T extends SpellcasterIllager> extends IllagerRenderer<T> {
   private static final ResourceLocation EVOKER_ILLAGER = new ResourceLocation("textures/entity/illager/evoker.png");

   public EvokerRenderer(EntityRendererProvider.Context p_174108_) {
      super(p_174108_, new IllagerModel<>(p_174108_.bakeLayer(ModelLayers.EVOKER)), 0.5F);
      this.addLayer(new ItemInHandLayer<T, IllagerModel<T>>(this) {
         public void render(PoseStack p_114569_, MultiBufferSource p_114570_, int p_114571_, T p_114572_, float p_114573_, float p_114574_, float p_114575_, float p_114576_, float p_114577_, float p_114578_) {
            if (p_114572_.isCastingSpell()) {
               super.render(p_114569_, p_114570_, p_114571_, p_114572_, p_114573_, p_114574_, p_114575_, p_114576_, p_114577_, p_114578_);
            }

         }
      });
   }

   public ResourceLocation getTextureLocation(T p_114541_) {
      return EVOKER_ILLAGER;
   }
}