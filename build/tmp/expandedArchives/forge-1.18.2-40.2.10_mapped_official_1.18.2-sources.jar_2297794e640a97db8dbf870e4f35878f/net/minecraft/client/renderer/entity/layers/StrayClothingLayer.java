package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StrayClothingLayer<T extends Mob & RangedAttackMob, M extends EntityModel<T>> extends RenderLayer<T, M> {
   private static final ResourceLocation STRAY_CLOTHES_LOCATION = new ResourceLocation("textures/entity/skeleton/stray_overlay.png");
   private final SkeletonModel<T> layerModel;

   public StrayClothingLayer(RenderLayerParent<T, M> p_174544_, EntityModelSet p_174545_) {
      super(p_174544_);
      this.layerModel = new SkeletonModel<>(p_174545_.bakeLayer(ModelLayers.STRAY_OUTER_LAYER));
   }

   public void render(PoseStack p_117553_, MultiBufferSource p_117554_, int p_117555_, T p_117556_, float p_117557_, float p_117558_, float p_117559_, float p_117560_, float p_117561_, float p_117562_) {
      coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, STRAY_CLOTHES_LOCATION, p_117553_, p_117554_, p_117555_, p_117556_, p_117557_, p_117558_, p_117560_, p_117561_, p_117562_, p_117559_, 1.0F, 1.0F, 1.0F);
   }
}