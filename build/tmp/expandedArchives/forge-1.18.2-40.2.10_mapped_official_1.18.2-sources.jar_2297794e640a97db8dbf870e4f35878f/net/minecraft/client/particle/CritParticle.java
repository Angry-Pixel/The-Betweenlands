package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CritParticle extends TextureSheetParticle {
   CritParticle(ClientLevel p_105919_, double p_105920_, double p_105921_, double p_105922_, double p_105923_, double p_105924_, double p_105925_) {
      super(p_105919_, p_105920_, p_105921_, p_105922_, 0.0D, 0.0D, 0.0D);
      this.friction = 0.7F;
      this.gravity = 0.5F;
      this.xd *= (double)0.1F;
      this.yd *= (double)0.1F;
      this.zd *= (double)0.1F;
      this.xd += p_105923_ * 0.4D;
      this.yd += p_105924_ * 0.4D;
      this.zd += p_105925_ * 0.4D;
      float f = (float)(Math.random() * (double)0.3F + (double)0.6F);
      this.rCol = f;
      this.gCol = f;
      this.bCol = f;
      this.quadSize *= 0.75F;
      this.lifetime = Math.max((int)(6.0D / (Math.random() * 0.8D + 0.6D)), 1);
      this.hasPhysics = false;
      this.tick();
   }

   public float getQuadSize(float p_105938_) {
      return this.quadSize * Mth.clamp(((float)this.age + p_105938_) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      super.tick();
      this.gCol *= 0.96F;
      this.bCol *= 0.9F;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @OnlyIn(Dist.CLIENT)
   public static class DamageIndicatorProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public DamageIndicatorProvider(SpriteSet p_105941_) {
         this.sprite = p_105941_;
      }

      public Particle createParticle(SimpleParticleType p_105952_, ClientLevel p_105953_, double p_105954_, double p_105955_, double p_105956_, double p_105957_, double p_105958_, double p_105959_) {
         CritParticle critparticle = new CritParticle(p_105953_, p_105954_, p_105955_, p_105956_, p_105957_, p_105958_ + 1.0D, p_105959_);
         critparticle.setLifetime(20);
         critparticle.pickSprite(this.sprite);
         return critparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class MagicProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public MagicProvider(SpriteSet p_105962_) {
         this.sprite = p_105962_;
      }

      public Particle createParticle(SimpleParticleType p_105973_, ClientLevel p_105974_, double p_105975_, double p_105976_, double p_105977_, double p_105978_, double p_105979_, double p_105980_) {
         CritParticle critparticle = new CritParticle(p_105974_, p_105975_, p_105976_, p_105977_, p_105978_, p_105979_, p_105980_);
         critparticle.rCol *= 0.3F;
         critparticle.gCol *= 0.8F;
         critparticle.pickSprite(this.sprite);
         return critparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_105983_) {
         this.sprite = p_105983_;
      }

      public Particle createParticle(SimpleParticleType p_105994_, ClientLevel p_105995_, double p_105996_, double p_105997_, double p_105998_, double p_105999_, double p_106000_, double p_106001_) {
         CritParticle critparticle = new CritParticle(p_105995_, p_105996_, p_105997_, p_105998_, p_105999_, p_106000_, p_106001_);
         critparticle.pickSprite(this.sprite);
         return critparticle;
      }
   }
}