package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SuspendedTownParticle extends TextureSheetParticle {
   SuspendedTownParticle(ClientLevel p_108104_, double p_108105_, double p_108106_, double p_108107_, double p_108108_, double p_108109_, double p_108110_) {
      super(p_108104_, p_108105_, p_108106_, p_108107_, p_108108_, p_108109_, p_108110_);
      float f = this.random.nextFloat() * 0.1F + 0.2F;
      this.rCol = f;
      this.gCol = f;
      this.bCol = f;
      this.setSize(0.02F, 0.02F);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.5F;
      this.xd *= (double)0.02F;
      this.yd *= (double)0.02F;
      this.zd *= (double)0.02F;
      this.lifetime = (int)(20.0D / (Math.random() * 0.8D + 0.2D));
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void move(double p_108122_, double p_108123_, double p_108124_) {
      this.setBoundingBox(this.getBoundingBox().move(p_108122_, p_108123_, p_108124_));
      this.setLocationFromBoundingbox();
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.lifetime-- <= 0) {
         this.remove();
      } else {
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.99D;
         this.yd *= 0.99D;
         this.zd *= 0.99D;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ComposterFillProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public ComposterFillProvider(SpriteSet p_108128_) {
         this.sprite = p_108128_;
      }

      public Particle createParticle(SimpleParticleType p_108139_, ClientLevel p_108140_, double p_108141_, double p_108142_, double p_108143_, double p_108144_, double p_108145_, double p_108146_) {
         SuspendedTownParticle suspendedtownparticle = new SuspendedTownParticle(p_108140_, p_108141_, p_108142_, p_108143_, p_108144_, p_108145_, p_108146_);
         suspendedtownparticle.pickSprite(this.sprite);
         suspendedtownparticle.setColor(1.0F, 1.0F, 1.0F);
         suspendedtownparticle.setLifetime(3 + p_108140_.getRandom().nextInt(5));
         return suspendedtownparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class DolphinSpeedProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public DolphinSpeedProvider(SpriteSet p_108149_) {
         this.sprite = p_108149_;
      }

      public Particle createParticle(SimpleParticleType p_108160_, ClientLevel p_108161_, double p_108162_, double p_108163_, double p_108164_, double p_108165_, double p_108166_, double p_108167_) {
         SuspendedTownParticle suspendedtownparticle = new SuspendedTownParticle(p_108161_, p_108162_, p_108163_, p_108164_, p_108165_, p_108166_, p_108167_);
         suspendedtownparticle.setColor(0.3F, 0.5F, 1.0F);
         suspendedtownparticle.pickSprite(this.sprite);
         suspendedtownparticle.setAlpha(1.0F - p_108161_.random.nextFloat() * 0.7F);
         suspendedtownparticle.setLifetime(suspendedtownparticle.getLifetime() / 2);
         return suspendedtownparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class HappyVillagerProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public HappyVillagerProvider(SpriteSet p_108170_) {
         this.sprite = p_108170_;
      }

      public Particle createParticle(SimpleParticleType p_108181_, ClientLevel p_108182_, double p_108183_, double p_108184_, double p_108185_, double p_108186_, double p_108187_, double p_108188_) {
         SuspendedTownParticle suspendedtownparticle = new SuspendedTownParticle(p_108182_, p_108183_, p_108184_, p_108185_, p_108186_, p_108187_, p_108188_);
         suspendedtownparticle.pickSprite(this.sprite);
         suspendedtownparticle.setColor(1.0F, 1.0F, 1.0F);
         return suspendedtownparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_108191_) {
         this.sprite = p_108191_;
      }

      public Particle createParticle(SimpleParticleType p_108202_, ClientLevel p_108203_, double p_108204_, double p_108205_, double p_108206_, double p_108207_, double p_108208_, double p_108209_) {
         SuspendedTownParticle suspendedtownparticle = new SuspendedTownParticle(p_108203_, p_108204_, p_108205_, p_108206_, p_108207_, p_108208_, p_108209_);
         suspendedtownparticle.pickSprite(this.sprite);
         return suspendedtownparticle;
      }
   }
}