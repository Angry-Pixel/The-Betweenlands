package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HugeExplosionParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   HugeExplosionParticle(ClientLevel p_106905_, double p_106906_, double p_106907_, double p_106908_, double p_106909_, SpriteSet p_106910_) {
      super(p_106905_, p_106906_, p_106907_, p_106908_, 0.0D, 0.0D, 0.0D);
      this.lifetime = 6 + this.random.nextInt(4);
      float f = this.random.nextFloat() * 0.6F + 0.4F;
      this.rCol = f;
      this.gCol = f;
      this.bCol = f;
      this.quadSize = 2.0F * (1.0F - (float)p_106909_ * 0.5F);
      this.sprites = p_106910_;
      this.setSpriteFromAge(p_106910_);
   }

   public int getLightColor(float p_106921_) {
      return 15728880;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_LIT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet p_106925_) {
         this.sprites = p_106925_;
      }

      public Particle createParticle(SimpleParticleType p_106936_, ClientLevel p_106937_, double p_106938_, double p_106939_, double p_106940_, double p_106941_, double p_106942_, double p_106943_) {
         return new HugeExplosionParticle(p_106937_, p_106938_, p_106939_, p_106940_, p_106941_, this.sprites);
      }
   }
}