package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.IronGolemCrackinessLayer;
import net.minecraft.client.renderer.entity.layers.IronGolemFlowerLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IronGolemRenderer extends MobRenderer<IronGolem, IronGolemModel<IronGolem>> {
   private static final ResourceLocation GOLEM_LOCATION = new ResourceLocation("textures/entity/iron_golem/iron_golem.png");

   public IronGolemRenderer(EntityRendererProvider.Context p_174188_) {
      super(p_174188_, new IronGolemModel<>(p_174188_.bakeLayer(ModelLayers.IRON_GOLEM)), 0.7F);
      this.addLayer(new IronGolemCrackinessLayer(this));
      this.addLayer(new IronGolemFlowerLayer(this));
   }

   public ResourceLocation getTextureLocation(IronGolem p_115012_) {
      return GOLEM_LOCATION;
   }

   protected void setupRotations(IronGolem p_115014_, PoseStack p_115015_, float p_115016_, float p_115017_, float p_115018_) {
      super.setupRotations(p_115014_, p_115015_, p_115016_, p_115017_, p_115018_);
      if (!((double)p_115014_.animationSpeed < 0.01D)) {
         float f = 13.0F;
         float f1 = p_115014_.animationPosition - p_115014_.animationSpeed * (1.0F - p_115018_) + 6.0F;
         float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
         p_115015_.mulPose(Vector3f.ZP.rotationDegrees(6.5F * f2));
      }
   }
}