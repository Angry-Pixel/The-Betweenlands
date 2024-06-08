package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HugeExplosionSeedParticle extends NoRenderParticle {
   private int life;
   private final int lifeTime = 8;

   HugeExplosionSeedParticle(ClientLevel p_106947_, double p_106948_, double p_106949_, double p_106950_) {
      super(p_106947_, p_106948_, p_106949_, p_106950_, 0.0D, 0.0D, 0.0D);
   }

   public void tick() {
      for(int i = 0; i < 6; ++i) {
         double d0 = this.x + (this.random.nextDouble() - this.random.nextDouble()) * 4.0D;
         double d1 = this.y + (this.random.nextDouble() - this.random.nextDouble()) * 4.0D;
         double d2 = this.z + (this.random.nextDouble() - this.random.nextDouble()) * 4.0D;
         this.level.addParticle(ParticleTypes.EXPLOSION, d0, d1, d2, (double)((float)this.life / (float)this.lifeTime), 0.0D, 0.0D);
      }

      ++this.life;
      if (this.life == this.lifeTime) {
         this.remove();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      public Particle createParticle(SimpleParticleType p_106969_, ClientLevel p_106970_, double p_106971_, double p_106972_, double p_106973_, double p_106974_, double p_106975_, double p_106976_) {
         return new HugeExplosionSeedParticle(p_106970_, p_106971_, p_106972_, p_106973_);
      }
   }
}