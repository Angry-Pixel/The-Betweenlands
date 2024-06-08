package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.DustParticleOptionsBase;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DustParticleBase<T extends DustParticleOptionsBase> extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected DustParticleBase(ClientLevel p_172094_, double p_172095_, double p_172096_, double p_172097_, double p_172098_, double p_172099_, double p_172100_, T p_172101_, SpriteSet p_172102_) {
      super(p_172094_, p_172095_, p_172096_, p_172097_, p_172098_, p_172099_, p_172100_);
      this.friction = 0.96F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.sprites = p_172102_;
      this.xd *= (double)0.1F;
      this.yd *= (double)0.1F;
      this.zd *= (double)0.1F;
      float f = this.random.nextFloat() * 0.4F + 0.6F;
      this.rCol = this.randomizeColor(p_172101_.getColor().x(), f);
      this.gCol = this.randomizeColor(p_172101_.getColor().y(), f);
      this.bCol = this.randomizeColor(p_172101_.getColor().z(), f);
      this.quadSize *= 0.75F * p_172101_.getScale();
      int i = (int)(8.0D / (this.random.nextDouble() * 0.8D + 0.2D));
      this.lifetime = (int)Math.max((float)i * p_172101_.getScale(), 1.0F);
      this.setSpriteFromAge(p_172102_);
   }

   protected float randomizeColor(float p_172105_, float p_172106_) {
      return (this.random.nextFloat() * 0.2F + 0.8F) * p_172105_ * p_172106_;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getQuadSize(float p_172109_) {
      return this.quadSize * Mth.clamp(((float)this.age + p_172109_) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }
}