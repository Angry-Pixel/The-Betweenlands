package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerCloudParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   PlayerCloudParticle(ClientLevel p_107483_, double p_107484_, double p_107485_, double p_107486_, double p_107487_, double p_107488_, double p_107489_, SpriteSet p_107490_) {
      super(p_107483_, p_107484_, p_107485_, p_107486_, 0.0D, 0.0D, 0.0D);
      this.friction = 0.96F;
      this.sprites = p_107490_;
      float f = 2.5F;
      this.xd *= (double)0.1F;
      this.yd *= (double)0.1F;
      this.zd *= (double)0.1F;
      this.xd += p_107487_;
      this.yd += p_107488_;
      this.zd += p_107489_;
      float f1 = 1.0F - (float)(Math.random() * (double)0.3F);
      this.rCol = f1;
      this.gCol = f1;
      this.bCol = f1;
      this.quadSize *= 1.875F;
      int i = (int)(8.0D / (Math.random() * 0.8D + 0.3D));
      this.lifetime = (int)Math.max((float)i * 2.5F, 1.0F);
      this.hasPhysics = false;
      this.setSpriteFromAge(p_107490_);
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public float getQuadSize(float p_107504_) {
      return this.quadSize * Mth.clamp(((float)this.age + p_107504_) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      super.tick();
      if (!this.removed) {
         this.setSpriteFromAge(this.sprites);
         Player player = this.level.getNearestPlayer(this.x, this.y, this.z, 2.0D, false);
         if (player != null) {
            double d0 = player.getY();
            if (this.y > d0) {
               this.y += (d0 - this.y) * 0.2D;
               this.yd += (player.getDeltaMovement().y - this.yd) * 0.2D;
               this.setPos(this.x, this.y, this.z);
            }
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet p_107507_) {
         this.sprites = p_107507_;
      }

      public Particle createParticle(SimpleParticleType p_107518_, ClientLevel p_107519_, double p_107520_, double p_107521_, double p_107522_, double p_107523_, double p_107524_, double p_107525_) {
         return new PlayerCloudParticle(p_107519_, p_107520_, p_107521_, p_107522_, p_107523_, p_107524_, p_107525_, this.sprites);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class SneezeProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public SneezeProvider(SpriteSet p_107528_) {
         this.sprites = p_107528_;
      }

      public Particle createParticle(SimpleParticleType p_107539_, ClientLevel p_107540_, double p_107541_, double p_107542_, double p_107543_, double p_107544_, double p_107545_, double p_107546_) {
         Particle particle = new PlayerCloudParticle(p_107540_, p_107541_, p_107542_, p_107543_, p_107544_, p_107545_, p_107546_, this.sprites);
         particle.setColor(200.0F, 50.0F, 120.0F);
         particle.setAlpha(0.4F);
         return particle;
      }
   }
}