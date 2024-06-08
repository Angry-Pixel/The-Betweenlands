package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RisingParticle extends TextureSheetParticle {
   protected RisingParticle(ClientLevel p_107631_, double p_107632_, double p_107633_, double p_107634_, double p_107635_, double p_107636_, double p_107637_) {
      super(p_107631_, p_107632_, p_107633_, p_107634_, p_107635_, p_107636_, p_107637_);
      this.friction = 0.96F;
      this.xd = this.xd * (double)0.01F + p_107635_;
      this.yd = this.yd * (double)0.01F + p_107636_;
      this.zd = this.zd * (double)0.01F + p_107637_;
      this.x += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.y += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.z += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05F);
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
   }
}