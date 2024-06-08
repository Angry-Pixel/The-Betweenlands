package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WakeParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   WakeParticle(ClientLevel p_108407_, double p_108408_, double p_108409_, double p_108410_, double p_108411_, double p_108412_, double p_108413_, SpriteSet p_108414_) {
      super(p_108407_, p_108408_, p_108409_, p_108410_, 0.0D, 0.0D, 0.0D);
      this.sprites = p_108414_;
      this.xd *= (double)0.3F;
      this.yd = Math.random() * (double)0.2F + (double)0.1F;
      this.zd *= (double)0.3F;
      this.setSize(0.01F, 0.01F);
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.setSpriteFromAge(p_108414_);
      this.gravity = 0.0F;
      this.xd = p_108411_;
      this.yd = p_108412_;
      this.zd = p_108413_;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      int i = 60 - this.lifetime;
      if (this.lifetime-- <= 0) {
         this.remove();
      } else {
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= (double)0.98F;
         this.yd *= (double)0.98F;
         this.zd *= (double)0.98F;
         float f = (float)i * 0.001F;
         this.setSize(f, f);
         this.setSprite(this.sprites.get(i % 4, 4));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet p_108429_) {
         this.sprites = p_108429_;
      }

      public Particle createParticle(SimpleParticleType p_108440_, ClientLevel p_108441_, double p_108442_, double p_108443_, double p_108444_, double p_108445_, double p_108446_, double p_108447_) {
         return new WakeParticle(p_108441_, p_108442_, p_108443_, p_108444_, p_108445_, p_108446_, p_108447_, this.sprites);
      }
   }
}