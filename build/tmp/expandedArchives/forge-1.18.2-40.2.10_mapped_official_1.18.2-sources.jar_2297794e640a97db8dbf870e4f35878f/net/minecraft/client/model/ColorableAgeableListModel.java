package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ColorableAgeableListModel<E extends Entity> extends AgeableListModel<E> {
   private float r = 1.0F;
   private float g = 1.0F;
   private float b = 1.0F;

   public void setColor(float p_102420_, float p_102421_, float p_102422_) {
      this.r = p_102420_;
      this.g = p_102421_;
      this.b = p_102422_;
   }

   public void renderToBuffer(PoseStack p_102424_, VertexConsumer p_102425_, int p_102426_, int p_102427_, float p_102428_, float p_102429_, float p_102430_, float p_102431_) {
      super.renderToBuffer(p_102424_, p_102425_, p_102426_, p_102427_, this.r * p_102428_, this.g * p_102429_, this.b * p_102430_, p_102431_);
   }
}