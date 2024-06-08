package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.CatCollarLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CatRenderer extends MobRenderer<Cat, CatModel<Cat>> {
   public CatRenderer(EntityRendererProvider.Context p_173943_) {
      super(p_173943_, new CatModel<>(p_173943_.bakeLayer(ModelLayers.CAT)), 0.4F);
      this.addLayer(new CatCollarLayer(this, p_173943_.getModelSet()));
   }

   public ResourceLocation getTextureLocation(Cat p_113950_) {
      return p_113950_.getResourceLocation();
   }

   protected void scale(Cat p_113952_, PoseStack p_113953_, float p_113954_) {
      super.scale(p_113952_, p_113953_, p_113954_);
      p_113953_.scale(0.8F, 0.8F, 0.8F);
   }

   protected void setupRotations(Cat p_113956_, PoseStack p_113957_, float p_113958_, float p_113959_, float p_113960_) {
      super.setupRotations(p_113956_, p_113957_, p_113958_, p_113959_, p_113960_);
      float f = p_113956_.getLieDownAmount(p_113960_);
      if (f > 0.0F) {
         p_113957_.translate((double)(0.4F * f), (double)(0.15F * f), (double)(0.1F * f));
         p_113957_.mulPose(Vector3f.ZP.rotationDegrees(Mth.rotLerp(f, 0.0F, 90.0F)));
         BlockPos blockpos = p_113956_.blockPosition();

         for(Player player : p_113956_.level.getEntitiesOfClass(Player.class, (new AABB(blockpos)).inflate(2.0D, 2.0D, 2.0D))) {
            if (player.isSleeping()) {
               p_113957_.translate((double)(0.15F * f), 0.0D, 0.0D);
               break;
            }
         }
      }

   }
}