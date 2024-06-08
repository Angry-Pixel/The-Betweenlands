package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Random;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CarriedBlockLayer;
import net.minecraft.client.renderer.entity.layers.EnderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndermanRenderer extends MobRenderer<EnderMan, EndermanModel<EnderMan>> {
   private static final ResourceLocation ENDERMAN_LOCATION = new ResourceLocation("textures/entity/enderman/enderman.png");
   private final Random random = new Random();

   public EndermanRenderer(EntityRendererProvider.Context p_173992_) {
      super(p_173992_, new EndermanModel<>(p_173992_.bakeLayer(ModelLayers.ENDERMAN)), 0.5F);
      this.addLayer(new EnderEyesLayer<>(this));
      this.addLayer(new CarriedBlockLayer(this));
   }

   public void render(EnderMan p_114339_, float p_114340_, float p_114341_, PoseStack p_114342_, MultiBufferSource p_114343_, int p_114344_) {
      BlockState blockstate = p_114339_.getCarriedBlock();
      EndermanModel<EnderMan> endermanmodel = this.getModel();
      endermanmodel.carrying = blockstate != null;
      endermanmodel.creepy = p_114339_.isCreepy();
      super.render(p_114339_, p_114340_, p_114341_, p_114342_, p_114343_, p_114344_);
   }

   public Vec3 getRenderOffset(EnderMan p_114336_, float p_114337_) {
      if (p_114336_.isCreepy()) {
         double d0 = 0.02D;
         return new Vec3(this.random.nextGaussian() * 0.02D, 0.0D, this.random.nextGaussian() * 0.02D);
      } else {
         return super.getRenderOffset(p_114336_, p_114337_);
      }
   }

   public ResourceLocation getTextureLocation(EnderMan p_114334_) {
      return ENDERMAN_LOCATION;
   }
}