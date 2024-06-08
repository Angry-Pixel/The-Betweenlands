package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DustParticle extends DustParticleBase<DustParticleOptions> {
   protected DustParticle(ClientLevel p_106415_, double p_106416_, double p_106417_, double p_106418_, double p_106419_, double p_106420_, double p_106421_, DustParticleOptions p_106422_, SpriteSet p_106423_) {
      super(p_106415_, p_106416_, p_106417_, p_106418_, p_106419_, p_106420_, p_106421_, p_106422_, p_106423_);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<DustParticleOptions> {
      private final SpriteSet sprites;

      public Provider(SpriteSet p_106441_) {
         this.sprites = p_106441_;
      }

      public Particle createParticle(DustParticleOptions p_106443_, ClientLevel p_106444_, double p_106445_, double p_106446_, double p_106447_, double p_106448_, double p_106449_, double p_106450_) {
         return new DustParticle(p_106444_, p_106445_, p_106446_, p_106447_, p_106448_, p_106449_, p_106450_, p_106443_, this.sprites);
      }
   }
}