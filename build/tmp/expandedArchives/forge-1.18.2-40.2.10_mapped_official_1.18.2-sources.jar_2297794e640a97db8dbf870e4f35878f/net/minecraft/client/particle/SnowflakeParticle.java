package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SnowflakeParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected SnowflakeParticle(ClientLevel p_172292_, double p_172293_, double p_172294_, double p_172295_, double p_172296_, double p_172297_, double p_172298_, SpriteSet p_172299_) {
      super(p_172292_, p_172293_, p_172294_, p_172295_);
      this.gravity = 0.225F;
      this.friction = 1.0F;
      this.sprites = p_172299_;
      this.xd = p_172296_ + (Math.random() * 2.0D - 1.0D) * (double)0.05F;
      this.yd = p_172297_ + (Math.random() * 2.0D - 1.0D) * (double)0.05F;
      this.zd = p_172298_ + (Math.random() * 2.0D - 1.0D) * (double)0.05F;
      this.quadSize = 0.1F * (this.random.nextFloat() * this.random.nextFloat() * 1.0F + 1.0F);
      this.lifetime = (int)(16.0D / ((double)this.random.nextFloat() * 0.8D + 0.2D)) + 2;
      this.setSpriteFromAge(p_172299_);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
      this.xd *= (double)0.95F;
      this.yd *= (double)0.9F;
      this.zd *= (double)0.95F;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet p_172304_) {
         this.sprites = p_172304_;
      }

      public Particle createParticle(SimpleParticleType p_172315_, ClientLevel p_172316_, double p_172317_, double p_172318_, double p_172319_, double p_172320_, double p_172321_, double p_172322_) {
         SnowflakeParticle snowflakeparticle = new SnowflakeParticle(p_172316_, p_172317_, p_172318_, p_172319_, p_172320_, p_172321_, p_172322_, this.sprites);
         snowflakeparticle.setColor(0.923F, 0.964F, 0.999F);
         return snowflakeparticle;
      }
   }
}