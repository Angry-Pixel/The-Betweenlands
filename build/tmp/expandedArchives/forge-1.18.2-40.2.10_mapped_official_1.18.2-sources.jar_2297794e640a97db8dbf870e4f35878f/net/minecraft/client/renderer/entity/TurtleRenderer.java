package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.TurtleModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TurtleRenderer extends MobRenderer<Turtle, TurtleModel<Turtle>> {
   private static final ResourceLocation TURTLE_LOCATION = new ResourceLocation("textures/entity/turtle/big_sea_turtle.png");

   public TurtleRenderer(EntityRendererProvider.Context p_174430_) {
      super(p_174430_, new TurtleModel<>(p_174430_.bakeLayer(ModelLayers.TURTLE)), 0.7F);
   }

   public void render(Turtle p_116261_, float p_116262_, float p_116263_, PoseStack p_116264_, MultiBufferSource p_116265_, int p_116266_) {
      if (p_116261_.isBaby()) {
         this.shadowRadius *= 0.5F;
      }

      super.render(p_116261_, p_116262_, p_116263_, p_116264_, p_116265_, p_116266_);
   }

   public ResourceLocation getTextureLocation(Turtle p_116259_) {
      return TURTLE_LOCATION;
   }
}