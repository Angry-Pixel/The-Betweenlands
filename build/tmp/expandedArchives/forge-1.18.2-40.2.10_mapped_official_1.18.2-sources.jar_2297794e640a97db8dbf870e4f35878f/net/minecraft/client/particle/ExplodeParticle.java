package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ExplodeParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected ExplodeParticle(ClientLevel p_106576_, double p_106577_, double p_106578_, double p_106579_, double p_106580_, double p_106581_, double p_106582_, SpriteSet p_106583_) {
      super(p_106576_, p_106577_, p_106578_, p_106579_);
      this.gravity = -0.1F;
      this.friction = 0.9F;
      this.sprites = p_106583_;
      this.xd = p_106580_ + (Math.random() * 2.0D - 1.0D) * (double)0.05F;
      this.yd = p_106581_ + (Math.random() * 2.0D - 1.0D) * (double)0.05F;
      this.zd = p_106582_ + (Math.random() * 2.0D - 1.0D) * (double)0.05F;
      float f = this.random.nextFloat() * 0.3F + 0.7F;
      this.rCol = f;
      this.gCol = f;
      this.bCol = f;
      this.quadSize = 0.1F * (this.random.nextFloat() * this.random.nextFloat() * 6.0F + 1.0F);
      this.lifetime = (int)(16.0D / ((double)this.random.nextFloat() * 0.8D + 0.2D)) + 2;
      this.setSpriteFromAge(p_106583_);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet p_106588_) {
         this.sprites = p_106588_;
      }

      public Particle createParticle(SimpleParticleType p_106599_, ClientLevel p_106600_, double p_106601_, double p_106602_, double p_106603_, double p_106604_, double p_106605_, double p_106606_) {
         return new ExplodeParticle(p_106600_, p_106601_, p_106602_, p_106603_, p_106604_, p_106605_, p_106606_, this.sprites);
      }
   }
}