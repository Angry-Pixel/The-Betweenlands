package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantmentTableParticle extends TextureSheetParticle {
   private final double xStart;
   private final double yStart;
   private final double zStart;

   EnchantmentTableParticle(ClientLevel p_106464_, double p_106465_, double p_106466_, double p_106467_, double p_106468_, double p_106469_, double p_106470_) {
      super(p_106464_, p_106465_, p_106466_, p_106467_);
      this.xd = p_106468_;
      this.yd = p_106469_;
      this.zd = p_106470_;
      this.xStart = p_106465_;
      this.yStart = p_106466_;
      this.zStart = p_106467_;
      this.xo = p_106465_ + p_106468_;
      this.yo = p_106466_ + p_106469_;
      this.zo = p_106467_ + p_106470_;
      this.x = this.xo;
      this.y = this.yo;
      this.z = this.zo;
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.2F);
      float f = this.random.nextFloat() * 0.6F + 0.4F;
      this.rCol = 0.9F * f;
      this.gCol = 0.9F * f;
      this.bCol = f;
      this.hasPhysics = false;
      this.lifetime = (int)(Math.random() * 10.0D) + 30;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void move(double p_106482_, double p_106483_, double p_106484_) {
      this.setBoundingBox(this.getBoundingBox().move(p_106482_, p_106483_, p_106484_));
      this.setLocationFromBoundingbox();
   }

   public int getLightColor(float p_106486_) {
      int i = super.getLightColor(p_106486_);
      float f = (float)this.age / (float)this.lifetime;
      f *= f;
      f *= f;
      int j = i & 255;
      int k = i >> 16 & 255;
      k += (int)(f * 15.0F * 16.0F);
      if (k > 240) {
         k = 240;
      }

      return j | k << 16;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         float f = (float)this.age / (float)this.lifetime;
         f = 1.0F - f;
         float f1 = 1.0F - f;
         f1 *= f1;
         f1 *= f1;
         this.x = this.xStart + this.xd * (double)f;
         this.y = this.yStart + this.yd * (double)f - (double)(f1 * 1.2F);
         this.z = this.zStart + this.zd * (double)f;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class NautilusProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public NautilusProvider(SpriteSet p_106490_) {
         this.sprite = p_106490_;
      }

      public Particle createParticle(SimpleParticleType p_106501_, ClientLevel p_106502_, double p_106503_, double p_106504_, double p_106505_, double p_106506_, double p_106507_, double p_106508_) {
         EnchantmentTableParticle enchantmenttableparticle = new EnchantmentTableParticle(p_106502_, p_106503_, p_106504_, p_106505_, p_106506_, p_106507_, p_106508_);
         enchantmenttableparticle.pickSprite(this.sprite);
         return enchantmenttableparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_106511_) {
         this.sprite = p_106511_;
      }

      public Particle createParticle(SimpleParticleType p_106522_, ClientLevel p_106523_, double p_106524_, double p_106525_, double p_106526_, double p_106527_, double p_106528_, double p_106529_) {
         EnchantmentTableParticle enchantmenttableparticle = new EnchantmentTableParticle(p_106523_, p_106524_, p_106525_, p_106526_, p_106527_, p_106528_, p_106529_);
         enchantmenttableparticle.pickSprite(this.sprite);
         return enchantmenttableparticle;
      }
   }
}