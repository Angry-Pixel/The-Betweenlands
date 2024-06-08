package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BubblePopParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   BubblePopParticle(ClientLevel p_105814_, double p_105815_, double p_105816_, double p_105817_, double p_105818_, double p_105819_, double p_105820_, SpriteSet p_105821_) {
      super(p_105814_, p_105815_, p_105816_, p_105817_);
      this.sprites = p_105821_;
      this.lifetime = 4;
      this.gravity = 0.008F;
      this.xd = p_105818_;
      this.yd = p_105819_;
      this.zd = p_105820_;
      this.setSpriteFromAge(p_105821_);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.setSpriteFromAge(this.sprites);
      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet p_105836_) {
         this.sprites = p_105836_;
      }

      public Particle createParticle(SimpleParticleType p_105847_, ClientLevel p_105848_, double p_105849_, double p_105850_, double p_105851_, double p_105852_, double p_105853_, double p_105854_) {
         return new BubblePopParticle(p_105848_, p_105849_, p_105850_, p_105851_, p_105852_, p_105853_, p_105854_, this.sprites);
      }
   }
}