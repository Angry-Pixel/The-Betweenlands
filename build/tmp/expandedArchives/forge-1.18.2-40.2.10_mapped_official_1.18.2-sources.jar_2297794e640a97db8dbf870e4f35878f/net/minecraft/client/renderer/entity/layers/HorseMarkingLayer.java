package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HorseMarkingLayer extends RenderLayer<Horse, HorseModel<Horse>> {
   private static final Map<Markings, ResourceLocation> LOCATION_BY_MARKINGS = Util.make(Maps.newEnumMap(Markings.class), (p_117069_) -> {
      p_117069_.put(Markings.NONE, (ResourceLocation)null);
      p_117069_.put(Markings.WHITE, new ResourceLocation("textures/entity/horse/horse_markings_white.png"));
      p_117069_.put(Markings.WHITE_FIELD, new ResourceLocation("textures/entity/horse/horse_markings_whitefield.png"));
      p_117069_.put(Markings.WHITE_DOTS, new ResourceLocation("textures/entity/horse/horse_markings_whitedots.png"));
      p_117069_.put(Markings.BLACK_DOTS, new ResourceLocation("textures/entity/horse/horse_markings_blackdots.png"));
   });

   public HorseMarkingLayer(RenderLayerParent<Horse, HorseModel<Horse>> p_117045_) {
      super(p_117045_);
   }

   public void render(PoseStack p_117058_, MultiBufferSource p_117059_, int p_117060_, Horse p_117061_, float p_117062_, float p_117063_, float p_117064_, float p_117065_, float p_117066_, float p_117067_) {
      ResourceLocation resourcelocation = LOCATION_BY_MARKINGS.get(p_117061_.getMarkings());
      if (resourcelocation != null && !p_117061_.isInvisible()) {
         VertexConsumer vertexconsumer = p_117059_.getBuffer(RenderType.entityTranslucent(resourcelocation));
         this.getParentModel().renderToBuffer(p_117058_, vertexconsumer, p_117060_, LivingEntityRenderer.getOverlayCoords(p_117061_, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
      }
   }
}