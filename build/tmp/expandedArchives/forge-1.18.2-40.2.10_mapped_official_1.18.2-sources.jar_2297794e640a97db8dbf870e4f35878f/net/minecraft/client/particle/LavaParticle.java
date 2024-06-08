package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LavaParticle extends TextureSheetParticle {
   LavaParticle(ClientLevel p_107074_, double p_107075_, double p_107076_, double p_107077_) {
      super(p_107074_, p_107075_, p_107076_, p_107077_, 0.0D, 0.0D, 0.0D);
      this.gravity = 0.75F;
      this.friction = 0.999F;
      this.xd *= (double)0.8F;
      this.yd *= (double)0.8F;
      this.zd *= (double)0.8F;
      this.yd = (double)(this.random.nextFloat() * 0.4F + 0.05F);
      this.quadSize *= this.random.nextFloat() * 2.0F + 0.2F;
      this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public int getLightColor(float p_107086_) {
      int i = super.getLightColor(p_107086_);
      int j = 240;
      int k = i >> 16 & 255;
      return 240 | k << 16;
   }

   public float getQuadSize(float p_107089_) {
      float f = ((float)this.age + p_107089_) / (float)this.lifetime;
      return this.quadSize * (1.0F - f * f);
   }

   public void tick() {
      super.tick();
      if (!this.removed) {
         float f = (float)this.age / (float)this.lifetime;
         if (this.random.nextFloat() > f) {
            this.level.addParticle(ParticleTypes.SMOKE, this.x, this.y, this.z, this.xd, this.yd, this.zd);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_107092_) {
         this.sprite = p_107092_;
      }

      public Particle createParticle(SimpleParticleType p_107103_, ClientLevel p_107104_, double p_107105_, double p_107106_, double p_107107_, double p_107108_, double p_107109_, double p_107110_) {
         LavaParticle lavaparticle = new LavaParticle(p_107104_, p_107105_, p_107106_, p_107107_);
         lavaparticle.pickSprite(this.sprite);
         return lavaparticle;
      }
   }
}