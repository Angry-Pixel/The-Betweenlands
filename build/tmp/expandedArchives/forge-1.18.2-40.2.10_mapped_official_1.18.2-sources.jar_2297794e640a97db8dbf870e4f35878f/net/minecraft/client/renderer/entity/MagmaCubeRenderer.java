package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.LavaSlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MagmaCubeRenderer extends MobRenderer<MagmaCube, LavaSlimeModel<MagmaCube>> {
   private static final ResourceLocation MAGMACUBE_LOCATION = new ResourceLocation("textures/entity/slime/magmacube.png");

   public MagmaCubeRenderer(EntityRendererProvider.Context p_174298_) {
      super(p_174298_, new LavaSlimeModel<>(p_174298_.bakeLayer(ModelLayers.MAGMA_CUBE)), 0.25F);
   }

   protected int getBlockLightLevel(MagmaCube p_115399_, BlockPos p_115400_) {
      return 15;
   }

   public ResourceLocation getTextureLocation(MagmaCube p_115393_) {
      return MAGMACUBE_LOCATION;
   }

   protected void scale(MagmaCube p_115395_, PoseStack p_115396_, float p_115397_) {
      int i = p_115395_.getSize();
      float f = Mth.lerp(p_115397_, p_115395_.oSquish, p_115395_.squish) / ((float)i * 0.5F + 1.0F);
      float f1 = 1.0F / (f + 1.0F);
      p_115396_.scale(f1 * (float)i, 1.0F / f1 * (float)i, f1 * (float)i);
   }
}