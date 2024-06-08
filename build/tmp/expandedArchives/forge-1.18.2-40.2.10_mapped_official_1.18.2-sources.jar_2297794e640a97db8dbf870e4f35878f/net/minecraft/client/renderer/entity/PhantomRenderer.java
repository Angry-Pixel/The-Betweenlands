package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.PhantomModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.PhantomEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PhantomRenderer extends MobRenderer<Phantom, PhantomModel<Phantom>> {
   private static final ResourceLocation PHANTOM_LOCATION = new ResourceLocation("textures/entity/phantom.png");

   public PhantomRenderer(EntityRendererProvider.Context p_174338_) {
      super(p_174338_, new PhantomModel<>(p_174338_.bakeLayer(ModelLayers.PHANTOM)), 0.75F);
      this.addLayer(new PhantomEyesLayer<>(this));
   }

   public ResourceLocation getTextureLocation(Phantom p_115679_) {
      return PHANTOM_LOCATION;
   }

   protected void scale(Phantom p_115681_, PoseStack p_115682_, float p_115683_) {
      int i = p_115681_.getPhantomSize();
      float f = 1.0F + 0.15F * (float)i;
      p_115682_.scale(f, f, f);
      p_115682_.translate(0.0D, 1.3125D, 0.1875D);
   }

   protected void setupRotations(Phantom p_115685_, PoseStack p_115686_, float p_115687_, float p_115688_, float p_115689_) {
      super.setupRotations(p_115685_, p_115686_, p_115687_, p_115688_, p_115689_);
      p_115686_.mulPose(Vector3f.XP.rotationDegrees(p_115685_.getXRot()));
   }
}