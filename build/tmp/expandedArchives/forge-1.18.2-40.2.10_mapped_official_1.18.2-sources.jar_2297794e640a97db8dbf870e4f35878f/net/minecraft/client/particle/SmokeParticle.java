package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SmokeParticle extends BaseAshSmokeParticle {
   protected SmokeParticle(ClientLevel p_107685_, double p_107686_, double p_107687_, double p_107688_, double p_107689_, double p_107690_, double p_107691_, float p_107692_, SpriteSet p_107693_) {
      super(p_107685_, p_107686_, p_107687_, p_107688_, 0.1F, 0.1F, 0.1F, p_107689_, p_107690_, p_107691_, p_107692_, p_107693_, 0.3F, 8, -0.1F, true);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet p_107696_) {
         this.sprites = p_107696_;
      }

      public Particle createParticle(SimpleParticleType p_107707_, ClientLevel p_107708_, double p_107709_, double p_107710_, double p_107711_, double p_107712_, double p_107713_, double p_107714_) {
         return new SmokeParticle(p_107708_, p_107709_, p_107710_, p_107711_, p_107712_, p_107713_, p_107714_, 1.0F, this.sprites);
      }
   }
}