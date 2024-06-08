package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.VexModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Vex;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VexRenderer extends HumanoidMobRenderer<Vex, VexModel> {
   private static final ResourceLocation VEX_LOCATION = new ResourceLocation("textures/entity/illager/vex.png");
   private static final ResourceLocation VEX_CHARGING_LOCATION = new ResourceLocation("textures/entity/illager/vex_charging.png");

   public VexRenderer(EntityRendererProvider.Context p_174435_) {
      super(p_174435_, new VexModel(p_174435_.bakeLayer(ModelLayers.VEX)), 0.3F);
   }

   protected int getBlockLightLevel(Vex p_116298_, BlockPos p_116299_) {
      return 15;
   }

   public ResourceLocation getTextureLocation(Vex p_116292_) {
      return p_116292_.isCharging() ? VEX_CHARGING_LOCATION : VEX_LOCATION;
   }

   protected void scale(Vex p_116294_, PoseStack p_116295_, float p_116296_) {
      p_116295_.scale(0.4F, 0.4F, 0.4F);
   }
}