package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndRodParticle extends SimpleAnimatedParticle {
   EndRodParticle(ClientLevel p_106531_, double p_106532_, double p_106533_, double p_106534_, double p_106535_, double p_106536_, double p_106537_, SpriteSet p_106538_) {
      super(p_106531_, p_106532_, p_106533_, p_106534_, p_106538_, 0.0125F);
      this.xd = p_106535_;
      this.yd = p_106536_;
      this.zd = p_106537_;
      this.quadSize *= 0.75F;
      this.lifetime = 60 + this.random.nextInt(12);
      this.setFadeColor(15916745);
      this.setSpriteFromAge(p_106538_);
   }

   public void move(double p_106550_, double p_106551_, double p_106552_) {
      this.setBoundingBox(this.getBoundingBox().move(p_106550_, p_106551_, p_106552_));
      this.setLocationFromBoundingbox();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet p_106555_) {
         this.sprites = p_106555_;
      }

      public Particle createParticle(SimpleParticleType p_106566_, ClientLevel p_106567_, double p_106568_, double p_106569_, double p_106570_, double p_106571_, double p_106572_, double p_106573_) {
         return new EndRodParticle(p_106567_, p_106568_, p_106569_, p_106570_, p_106571_, p_106572_, p_106573_, this.sprites);
      }
   }
}