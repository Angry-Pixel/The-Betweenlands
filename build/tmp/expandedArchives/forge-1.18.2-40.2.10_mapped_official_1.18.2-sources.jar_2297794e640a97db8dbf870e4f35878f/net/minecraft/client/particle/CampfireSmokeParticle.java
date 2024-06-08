package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CampfireSmokeParticle extends TextureSheetParticle {
   CampfireSmokeParticle(ClientLevel p_105856_, double p_105857_, double p_105858_, double p_105859_, double p_105860_, double p_105861_, double p_105862_, boolean p_105863_) {
      super(p_105856_, p_105857_, p_105858_, p_105859_);
      this.scale(3.0F);
      this.setSize(0.25F, 0.25F);
      if (p_105863_) {
         this.lifetime = this.random.nextInt(50) + 280;
      } else {
         this.lifetime = this.random.nextInt(50) + 80;
      }

      this.gravity = 3.0E-6F;
      this.xd = p_105860_;
      this.yd = p_105861_ + (double)(this.random.nextFloat() / 500.0F);
      this.zd = p_105862_;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ < this.lifetime && !(this.alpha <= 0.0F)) {
         this.xd += (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
         this.zd += (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
            this.alpha -= 0.015F;
         }

      } else {
         this.remove();
      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class CosyProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public CosyProvider(SpriteSet p_105878_) {
         this.sprites = p_105878_;
      }

      public Particle createParticle(SimpleParticleType p_105889_, ClientLevel p_105890_, double p_105891_, double p_105892_, double p_105893_, double p_105894_, double p_105895_, double p_105896_) {
         CampfireSmokeParticle campfiresmokeparticle = new CampfireSmokeParticle(p_105890_, p_105891_, p_105892_, p_105893_, p_105894_, p_105895_, p_105896_, false);
         campfiresmokeparticle.setAlpha(0.9F);
         campfiresmokeparticle.pickSprite(this.sprites);
         return campfiresmokeparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class SignalProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public SignalProvider(SpriteSet p_105899_) {
         this.sprites = p_105899_;
      }

      public Particle createParticle(SimpleParticleType p_105910_, ClientLevel p_105911_, double p_105912_, double p_105913_, double p_105914_, double p_105915_, double p_105916_, double p_105917_) {
         CampfireSmokeParticle campfiresmokeparticle = new CampfireSmokeParticle(p_105911_, p_105912_, p_105913_, p_105914_, p_105915_, p_105916_, p_105917_, true);
         campfiresmokeparticle.setAlpha(0.95F);
         campfiresmokeparticle.pickSprite(this.sprites);
         return campfiresmokeparticle;
      }
   }
}