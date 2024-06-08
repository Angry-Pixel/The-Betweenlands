package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LargeSmokeParticle extends SmokeParticle {
   protected LargeSmokeParticle(ClientLevel p_107044_, double p_107045_, double p_107046_, double p_107047_, double p_107048_, double p_107049_, double p_107050_, SpriteSet p_107051_) {
      super(p_107044_, p_107045_, p_107046_, p_107047_, p_107048_, p_107049_, p_107050_, 2.5F, p_107051_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet p_107054_) {
         this.sprites = p_107054_;
      }

      public Particle createParticle(SimpleParticleType p_107065_, ClientLevel p_107066_, double p_107067_, double p_107068_, double p_107069_, double p_107070_, double p_107071_, double p_107072_) {
         return new LargeSmokeParticle(p_107066_, p_107067_, p_107068_, p_107069_, p_107070_, p_107071_, p_107072_, this.sprites);
      }
   }
}