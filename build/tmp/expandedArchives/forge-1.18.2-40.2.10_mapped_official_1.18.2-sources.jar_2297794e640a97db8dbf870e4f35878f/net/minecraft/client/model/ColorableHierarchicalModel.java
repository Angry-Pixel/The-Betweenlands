package net.minecraft.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ColorableHierarchicalModel<E extends Entity> extends HierarchicalModel<E> {
   private float r = 1.0F;
   private float g = 1.0F;
   private float b = 1.0F;

   public void setColor(float p_170502_, float p_170503_, float p_170504_) {
      this.r = p_170502_;
      this.g = p_170503_;
      this.b = p_170504_;
   }

   public void renderToBuffer(PoseStack p_170506_, VertexConsumer p_170507_, int p_170508_, int p_170509_, float p_170510_, float p_170511_, float p_170512_, float p_170513_) {
      super.renderToBuffer(p_170506_, p_170507_, p_170508_, p_170509_, this.r * p_170510_, this.g * p_170511_, this.b * p_170512_, p_170513_);
   }
}