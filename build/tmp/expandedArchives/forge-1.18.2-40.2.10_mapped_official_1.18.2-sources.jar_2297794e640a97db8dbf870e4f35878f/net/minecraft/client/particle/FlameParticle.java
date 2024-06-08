package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlameParticle extends RisingParticle {
   FlameParticle(ClientLevel p_106800_, double p_106801_, double p_106802_, double p_106803_, double p_106804_, double p_106805_, double p_106806_) {
      super(p_106800_, p_106801_, p_106802_, p_106803_, p_106804_, p_106805_, p_106806_);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void move(double p_106817_, double p_106818_, double p_106819_) {
      this.setBoundingBox(this.getBoundingBox().move(p_106817_, p_106818_, p_106819_));
      this.setLocationFromBoundingbox();
   }

   public float getQuadSize(float p_106824_) {
      float f = ((float)this.age + p_106824_) / (float)this.lifetime;
      return this.quadSize * (1.0F - f * f * 0.5F);
   }

   public int getLightColor(float p_106821_) {
      float f = ((float)this.age + p_106821_) / (float)this.lifetime;
      f = Mth.clamp(f, 0.0F, 1.0F);
      int i = super.getLightColor(p_106821_);
      int j = i & 255;
      int k = i >> 16 & 255;
      j += (int)(f * 15.0F * 16.0F);
      if (j > 240) {
         j = 240;
      }

      return j | k << 16;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_106827_) {
         this.sprite = p_106827_;
      }

      public Particle createParticle(SimpleParticleType p_106838_, ClientLevel p_106839_, double p_106840_, double p_106841_, double p_106842_, double p_106843_, double p_106844_, double p_106845_) {
         FlameParticle flameparticle = new FlameParticle(p_106839_, p_106840_, p_106841_, p_106842_, p_106843_, p_106844_, p_106845_);
         flameparticle.pickSprite(this.sprite);
         return flameparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class SmallFlameProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public SmallFlameProvider(SpriteSet p_172113_) {
         this.sprite = p_172113_;
      }

      public Particle createParticle(SimpleParticleType p_172124_, ClientLevel p_172125_, double p_172126_, double p_172127_, double p_172128_, double p_172129_, double p_172130_, double p_172131_) {
         FlameParticle flameparticle = new FlameParticle(p_172125_, p_172126_, p_172127_, p_172128_, p_172129_, p_172130_, p_172131_);
         flameparticle.pickSprite(this.sprite);
         flameparticle.scale(0.5F);
         return flameparticle;
      }
   }
}