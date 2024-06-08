package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AshParticle extends BaseAshSmokeParticle {
   protected AshParticle(ClientLevel p_105514_, double p_105515_, double p_105516_, double p_105517_, double p_105518_, double p_105519_, double p_105520_, float p_105521_, SpriteSet p_105522_) {
      super(p_105514_, p_105515_, p_105516_, p_105517_, 0.1F, -0.1F, 0.1F, p_105518_, p_105519_, p_105520_, p_105521_, p_105522_, 0.5F, 20, 0.1F, false);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet p_105525_) {
         this.sprites = p_105525_;
      }

      public Particle createParticle(SimpleParticleType p_105536_, ClientLevel p_105537_, double p_105538_, double p_105539_, double p_105540_, double p_105541_, double p_105542_, double p_105543_) {
         return new AshParticle(p_105537_, p_105538_, p_105539_, p_105540_, 0.0D, 0.0D, 0.0D, 1.0F, this.sprites);
      }
   }
}